/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.api.adapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SlingAdaptableTest {

    private SlingAdaptable sut;
    private AdapterManager adapterMgr;

    @Before
    public void setup() {
        sut = new SlingAdaptable() {};
        adapterMgr = mock(AdapterManager.class);
    }

    @Test
    public void testAdaptTo() {
        assertNull(sut.adaptTo(TestAdapterType.class));
        SlingAdaptable.setAdapterManager(adapterMgr);
        assertNull(sut.adaptTo(TestAdapterType.class));
    }

    @Test
    public void testAdaptToWithCache() {
        SlingAdaptable.setAdapterManager(adapterMgr);
        when (adapterMgr.getAdapter(any(), eq(TestAdapterType.class))).thenReturn(new TestAdapterType());
        assertNotNull(sut.adaptTo(TestAdapterType.class));
        verify(adapterMgr,times(1)).getAdapter(any(), eq(TestAdapterType.class));
        
        // the 2nd time it has to come out of the cache
        assertNotNull(sut.adaptTo(TestAdapterType.class));
        verify(adapterMgr,times(1)).getAdapter(any(), eq(TestAdapterType.class));

        assertNull(sut.adaptTo(TestAdapterType2.class));
        when (adapterMgr.getAdapter(any(), eq(TestAdapterType2.class))).thenReturn(new TestAdapterType2());
        assertNotNull(sut.adaptTo(TestAdapterType2.class));
        assertNotNull(sut.adaptTo(TestAdapterType.class));
    }
    
    // SLING-10371
    @Test
    public void testNestedAdaptTo() throws ExecutionException, InterruptedException {
        SlingAdaptable.setAdapterManager(adapterMgr);
        SuperTypeCallingAdaptable adaptable = new SuperTypeCallingAdaptable();
        when(adapterMgr.getAdapter(eq(adaptable),eq(TestAdapterType.class)))
            .thenAnswer(invocation -> {
                SlingAdaptable a = (SlingAdaptable) invocation.getArgument(0);
                a.adaptTo(String.class); // trigger nested invocation
                return new TestAdapterType();
            });
        when(adapterMgr.getAdapter(eq(adaptable),eq(String.class)))
        .thenAnswer(invocation -> {
            return "someValue";
        });
        final List<TestAdapterType> results = runConcurrently(64, 1, () -> adaptable.adaptTo(TestAdapterType.class));
        assertAllTheSame(adaptable.adaptTo(TestAdapterType.class), results);
    }

    @Test
    public void testConcurrentInitializationOfAdaptersCache() throws InterruptedException, ExecutionException {
        SlingAdaptable.setAdapterManager(adapterMgr);
        when (adapterMgr.getAdapter(any(), eq(TestAdapterType.class))).then((Answer<TestAdapterType>) invocationOnMock -> new TestAdapterType());

        List<TestAdapterType> results = runConcurrently(64, 1, () -> sut.adaptTo(TestAdapterType.class));
        verify(adapterMgr, times(1)).getAdapter(any(), eq(TestAdapterType.class));
        assertAllTheSame(sut.adaptTo(TestAdapterType.class), results);
    }

    public static class SuperTypeCallingAdaptable extends SlingAdaptable {
        @Override
        public <AdapterType> @Nullable AdapterType adaptTo(@NotNull Class<AdapterType> type) {
            // always fallback to the supertype implementation
            if (type == TestAdapterType.class) {
                return (@Nullable AdapterType) super.adaptTo(TestAdapterType.class);
            }
            if (type == String.class) {
                return (@Nullable AdapterType) super.adaptTo(String.class);
            }
            return null;
        }
    }

    // test adaptables
    public static class TestAdapterType {}

    // pseudo adaptable
    public static class TestAdapterType2 {}

    private static <T> void assertAllTheSame(T referenceObject, Iterable<T> results) {
        assertNotNull("reference object should not be null", referenceObject);
        results.forEach(result -> assertSame("cached adaptable objects should be the same instance", referenceObject, result));
    }

    private static <T> List<T> runConcurrently(int threads, int timeoutSeconds, Callable<T> callable) throws InterruptedException, ExecutionException {
        final CountDownLatch countDownLatch = new CountDownLatch(threads);
        final ExecutorService executorService = Executors.newFixedThreadPool(threads);
        final List<Future<T>> results = new ArrayList<>(threads);

        for (int i = 0; i < threads; i++) {
            results.add(executorService.submit(() -> {
                countDownLatch.countDown();
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Should never be interrupted", e);
                }
                return callable.call();
            }));
        }
        executorService.shutdown();
        if (executorService.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
            List<T> resultList = new ArrayList<>();
            for (Future<T> result : results) {
                resultList.add(result.get());
            }
            return resultList;
        }
        fail("Concurrent run did not finish within " + timeoutSeconds + " seconds");
        throw new IllegalStateException("unreachable due to \"fail\" - just to satisfy the compiler");
    }
}