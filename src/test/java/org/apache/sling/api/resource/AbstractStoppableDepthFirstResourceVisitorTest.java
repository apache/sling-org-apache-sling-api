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
package org.apache.sling.api.resource;

import java.util.Arrays;
import java.util.Iterator;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AbstractStoppableDepthFirstResourceVisitorTest {

    @Mock
    private Resource a, aa, ab, aaa, aab, aba, abb;

    private static Iterator<Resource> resourceIterator(Resource... args) {
        return Arrays.stream(args).iterator();
    }

    @Before
    public void setUp() {
        Mockito.when(a.listChildren()).thenReturn(resourceIterator(aa, ab));
        Mockito.when(aa.listChildren()).thenReturn(resourceIterator(aaa,aab));
        Mockito.when(ab.listChildren()).thenReturn(resourceIterator(aba,abb));
        Mockito.when(aaa.listChildren()).thenReturn(resourceIterator());
        Mockito.when(aab.listChildren()).thenReturn(resourceIterator());
        Mockito.when(aba.listChildren()).thenReturn(resourceIterator());
        Mockito.when(abb.listChildren()).thenReturn(resourceIterator());
    }

    @Test
    public void testFullTraversalWithCorrectOrder() {
        AbstractStoppableDepthFirstResourceVisitor visitor = new AbstractStoppableDepthFirstResourceVisitor() {
            @Override
            protected @NotNull TraversalContinuation visit(@NotNull Resource resource) {
                // call method for verification
                resource.getName();
                return TraversalContinuation.NORMAL;
            }
        };
        Assert.assertTrue(visitor.accept(a));
        
        InOrder orderVerifier = Mockito.inOrder(a, aa, aaa, aab, ab, aba, abb);
        orderVerifier.verify(a).getName();
        orderVerifier.verify(aa).getName();
        orderVerifier.verify(aaa).getName();
        orderVerifier.verify(aab).getName();
        orderVerifier.verify(ab).getName();
        orderVerifier.verify(aba).getName();
        orderVerifier.verify(abb).getName();
    }

    @Test
    public void testSkipSubtree() {
        AbstractStoppableDepthFirstResourceVisitor visitor = new AbstractStoppableDepthFirstResourceVisitor() {
            int elementNo = 0;
            
            @Override
            protected @NotNull TraversalContinuation visit(@NotNull Resource resource) {
                // call method for verification
                resource.getName();
                if (elementNo++ == 1) { // element aa
                    return TraversalContinuation.SKIP_SUBTREE;
                }
                return TraversalContinuation.NORMAL;
            }
        };
        Assert.assertTrue(visitor.accept(a));
        
        InOrder orderVerifier = Mockito.inOrder(a, aa, ab, aba, abb);
        orderVerifier.verify(a).getName();
        orderVerifier.verify(aa).getName();
        orderVerifier.verify(ab).getName();
        orderVerifier.verify(aba).getName();
        orderVerifier.verify(abb).getName();
        Mockito.verifyZeroInteractions(aaa, aab);
    }

    @Test
    public void testStop() {
        AbstractStoppableDepthFirstResourceVisitor visitor = new AbstractStoppableDepthFirstResourceVisitor() {
            int elementNo = 0;
            
            @Override
            protected @NotNull TraversalContinuation visit(@NotNull Resource resource) {
                // call method for verification
                resource.getName();
                if (elementNo++ == 1) { // element aa
                    return TraversalContinuation.STOP;
                }
                return TraversalContinuation.NORMAL;
            }
        };
        Assert.assertFalse(visitor.accept(a));
        
        InOrder orderVerifier = Mockito.inOrder(a, aa);
        orderVerifier.verify(a).getName();
        orderVerifier.verify(aa).getName();
        Mockito.verifyZeroInteractions(aaa, aab, ab, aba, abb);
    }
}
