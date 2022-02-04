/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Licensed to the Apache Software Foundation (ASF) under one
 ~ or more contributor license agreements.  See the NOTICE file
 ~ distributed with this work for additional information
 ~ regarding copyright ownership.  The ASF licenses this file
 ~ to you under the Apache License, Version 2.0 (the
 ~ "License"); you may not use this file except in compliance
 ~ with the License.  You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package org.apache.sling.api.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LazyBindingsTest {
    private static final String THE_QUESTION = "What is The Answer to the Ultimate Question of Life, The Universe, and Everything?";
    private static final int THE_ANSWER = 42;

    private Set<String> usedSuppliers;
    private Map<String, Integer> hashCodeCalls;
    private LazyBindings lazyBindings;

    private TestSupplier supplier;

    @Before
    public void setUp() {
        hashCodeCalls = new HashMap<>();
        usedSuppliers = new HashSet<>();
        supplier = new TestSupplier(THE_QUESTION, () -> THE_ANSWER);
        final Map<String, LazyBindings.Supplier> supplierMap = new HashMap<>();
        supplierMap.put(THE_QUESTION, supplier);
        lazyBindings = new LazyBindings(supplierMap);
    }

    @After
    public void tearDown() {
        usedSuppliers = null;
        lazyBindings = null;
    }

    @Test
    public void testGet() {
        assertFalse(usedSuppliers.contains(THE_QUESTION));
        assertEquals(THE_ANSWER, lazyBindings.get(THE_QUESTION));
        assertTrue(usedSuppliers.contains(THE_QUESTION));
        assertNull(lazyBindings.get("none"));
    }

    @Test
    public void testRemove() {
        lazyBindings.put("a", 0);
        assertNull(lazyBindings.remove("null"));
        assertEquals(0, lazyBindings.remove("a"));
        assertFalse(usedSuppliers.contains(THE_QUESTION));
        assertEquals(THE_ANSWER, lazyBindings.remove(THE_QUESTION));
        assertTrue(usedSuppliers.contains(THE_QUESTION));
    }

    @Test
    public void testPut() {
        assertFalse(usedSuppliers.contains(THE_QUESTION));
        Object supplierProvidedValueReplacement = lazyBindings.put(THE_QUESTION, 43);
        assertEquals(42, supplierProvidedValueReplacement);
        assertEquals(43, lazyBindings.get(THE_QUESTION));

        lazyBindings.put("putSupplier", (LazyBindings.Supplier) () -> {
            usedSuppliers.add("putSupplier");
            return "putSupplierValue";
        });
        assertFalse(usedSuppliers.contains("putSupplier"));
        assertTrue(lazyBindings.containsKey("putSupplier"));
        assertEquals("putSupplierValue", lazyBindings.get("putSupplier"));
        assertTrue(usedSuppliers.contains("putSupplier"));
    }

    @Test
    public void testPutAll() {
        Map<String, Object> toMerge = new HashMap<>();
        toMerge.put(THE_QUESTION, (LazyBindings.Supplier) () -> {
            usedSuppliers.add(THE_QUESTION);
            return THE_ANSWER;
        });
        toMerge.put("b", 1);
        toMerge.put("c", 2);
        lazyBindings.put("a", 0);
        lazyBindings.put("putSupplier", (LazyBindings.Supplier) () -> {
            usedSuppliers.add("putSupplier");
            return "putSupplierValue";
        });
        lazyBindings.putAll(toMerge);
        Set<String> keys = new HashSet<>(Arrays.asList(THE_QUESTION, "a", "b", "c", "putSupplier"));
        assertEquals(keys, lazyBindings.keySet());
        assertEquals(THE_ANSWER, lazyBindings.get(THE_QUESTION));
        assertTrue(usedSuppliers.contains(THE_QUESTION));
        assertEquals("putSupplierValue", lazyBindings.get("putSupplier"));
        assertTrue(usedSuppliers.contains("putSupplier"));
        assertEquals(2, lazyBindings.get("c"));
    }

    @Test
    public void testClearSizeEmpty() {
        lazyBindings.put("a", 0);
        assertEquals(2, lazyBindings.size());
        assertFalse(lazyBindings.isEmpty());
        lazyBindings.clear();
        assertEquals(0, lazyBindings.size());
        assertTrue(lazyBindings.isEmpty());
    }

    @Test
    public void testLazyContainsKey() {
        lazyBindings.put("a", 0);
        assertTrue(lazyBindings.containsKey(THE_QUESTION));
        assertTrue(lazyBindings.containsKey("a"));
        assertFalse(usedSuppliers.contains(THE_QUESTION));
    }

    @Test
    public void testContainsValue() {
        assertFalse(usedSuppliers.contains(THE_QUESTION));
        assertFalse(lazyBindings.containsValue(THE_ANSWER));
        assertFalse(usedSuppliers.contains(THE_QUESTION));
        lazyBindings.put("a", 0);
        assertTrue(lazyBindings.containsValue(0));
    }

    @Test
    public void testEntrySet() {
        lazyBindings.put("a", 0);
        Set<Map.Entry<String, Object>> expectedEntrySet = new HashSet<>();
        expectedEntrySet.add(new AbstractMap.SimpleEntry<>("a", 0));
        expectedEntrySet.add(new AbstractMap.SimpleEntry<>(THE_QUESTION, supplier));
        assertFalse(usedSuppliers.contains(THE_QUESTION));
        supplier.trackHashCodeCalls = true;
        assertEquals(2, lazyBindings.entrySet().size());
        assertEquals("Unexpected hashCode call for the test supplier.", 0, (int) hashCodeCalls.get(THE_QUESTION));
        assertEquals(expectedEntrySet, lazyBindings.entrySet());
        // the equals implementation will have to call hashCode on the supplier, but the entrySet method should not, so we expect only
        // one hashCode call
        assertEquals(1, (int) hashCodeCalls.get(THE_QUESTION));
        assertFalse(usedSuppliers.contains(THE_QUESTION));
    }

    @Test
    public void testKeySet() {
        lazyBindings.put("a", 0);
        assertEquals(new HashSet<>(Arrays.asList(THE_QUESTION, "a")), lazyBindings.keySet());
        assertFalse(usedSuppliers.contains(THE_QUESTION));
    }

    @Test
    public void testValues() {
        assertFalse(usedSuppliers.contains(THE_QUESTION));
        Collection<Object> values = lazyBindings.values();
        assertFalse(usedSuppliers.contains(THE_QUESTION));
        assertEquals(0, values.size());
        lazyBindings.put("a", 0);
        Set<Object> expectedValues = new HashSet<>();
        expectedValues.add(0);
        assertEquals(expectedValues, new HashSet<>(lazyBindings.values()));
    }

    @Test
    public void testGetOrDefault() {
        lazyBindings.put("a", 0);
        assertEquals(0, lazyBindings.getOrDefault("a", 1));
        assertFalse(usedSuppliers.contains(THE_QUESTION));
        assertEquals(THE_ANSWER, lazyBindings.getOrDefault(THE_QUESTION, THE_ANSWER + 1));
        assertTrue(usedSuppliers.contains(THE_QUESTION));
        assertEquals(1, lazyBindings.getOrDefault("b", 1));
    }

    @Test
    public void testThatNormalSuppliersAreNotUnwrapped() {
        final String supplierName = "regularSupplier";
        Supplier<Object> regularSupplier = () -> {
            usedSuppliers.add(supplierName);
            return 0;
        };
        lazyBindings.put(supplierName, regularSupplier);
        assertEquals(regularSupplier, lazyBindings.get(supplierName));
        assertFalse(usedSuppliers.contains(supplierName));
    }

    @Test
    public void testMultiplePuts() {
        final LazyBindings bindings = new LazyBindings();
        bindings.put("foo", "bar");
        bindings.put("foo", new LazyBindings.Supplier() {

            @Override
            public Object get() {
                return "lazybar";
            }
        });
        assertEquals("lazybar", bindings.get("foo"));
    }

    private class TestSupplier implements LazyBindings.Supplier {

        private final String name;
        private final LazyBindings.Supplier wrapped;
        private boolean trackHashCodeCalls;

        TestSupplier(String name, LazyBindings.Supplier wrapped) {
            this.name = name;
            this.wrapped = wrapped;
            hashCodeCalls.putIfAbsent(name, 0);
        }

        @Override
        public Object get() {
            usedSuppliers.add(name);
            return wrapped.get();
        }

        @Override
        public int hashCode() {
            if (trackHashCodeCalls) {
                hashCodeCalls.compute(name, (s, integer) -> integer == null ? 0 : integer + 1);
            }
            return super.hashCode();
        }
    }

}
