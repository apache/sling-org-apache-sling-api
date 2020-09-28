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

package org.apache.sling.api.resource.impl;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapUtil;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ValueMapUtilMergeTest {

    private final Function<Collection<ValueMap>, ValueMap> mergeFn;

    @Parameterized.Parameters(name = "using ValueMapUtil#{0}")
    public static Iterable<Object[]> testedMergeMethod() {
        return asList(
                new Object[] { "mergeAndCache", (Function<List<ValueMap>, ValueMap>)ValueMapUtil::mergeAndCache},
                new Object[] { "merge", (Function<List<ValueMap>, ValueMap>)ValueMapUtil::merge}
        );
    }

    public ValueMapUtilMergeTest(String name, Function<Collection<ValueMap>, ValueMap> mergeFn) {
        this.mergeFn = mergeFn;
    }

    private ValueMap merge(ValueMap... valueMaps) {
        return mergeFn.apply(asList(valueMaps));
    }

    @Test
    public void isEmptyTest() {
        ValueMap vm = merge(createValueMap(), createValueMap());
        assertTrue("Value map should be empty", vm.isEmpty());
        assertFalse("Typical map should not be empty", typicalVM().isEmpty());
    }

    @Test
    public void keySetTest() {
        assertThat("key set should be k1-k5", typicalVM().keySet(), containsInAnyOrder("k1", "k2", "k3", "k4", "k5"));
    }

    @Test
    public void valuesTest() {
        assertThat("values should be the one expected", typicalVM().values(), containsInAnyOrder("11", "22", "13", "24", "35"));
    }

    @Test
    public void testGet() {
        ValueMap vm = typicalVM();
        assertEquals("k1-11", "11", vm.get("k1"));
        assertEquals("k2-22", "22", vm.get("k2"));
        assertEquals("k3-13", "13", vm.get("k3"));
        assertEquals("k4-24", "24", vm.get("k4"));
        assertEquals("k5-5", "35", vm.get("k5"));
    }

    @Test
    public void testNullGet() {
        assertNull("null get should return null", typicalVM().get(null));
    }

    @Test
    public void testDefaultValue() {
        ValueMap vm = typicalVM();
        String randomString = "not-that-random-string";
        assertEquals("default value should work", randomString, vm.get("random-key", randomString));
    }

    @Test
    public void testContainsKey() {
        ValueMap vm = typicalVM();
        assertTrue("should contain 11", vm.containsValue("11"));
        assertFalse("should not contain 21", vm.containsValue("21"));
    }

    @Test
    public void testContainsValue() {
        ValueMap vm = typicalVM();
        assertTrue("should contain k1", vm.containsKey("k1"));
        assertTrue("should contain k2", vm.containsKey("k2"));
        assertTrue("should contain k3", vm.containsKey("k3"));
        assertTrue("should contain k4", vm.containsKey("k4"));
        assertTrue("should contain k5", vm.containsKey("k5"));
    }

    @Test
    public void testDeepGet() {
        ValueMap vm = merge(createValueMap("k/1", "11"), createValueMap("1", "21"));
        assertEquals("value should be 11", "11", vm.get("k/1", String.class));
    }

    @Test
    public void testLong() {
        ValueMap vm = merge(createValueMap("k", 11L));
        assertEquals("result should be the same than a simple property fetching", "11", vm.get("k", String.class));
        assertEquals("result should be the same than a simple property fetching", 11L, Objects.requireNonNull(vm.get("k", Long.class)).longValue());
    }

    @Test
    public void testBoolean() {
        ValueMap vm = merge(createValueMap("k", true));
        assertEquals("result should be the same than a simple property fetching", "true", vm.get("k", String.class));
        assertEquals("result should be the same than a simple property fetching", true, vm.get("k", boolean.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutOnVM() {
        typicalVM().put("foo", "bar");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveOnVM() {
        typicalVM().remove("foo");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutAllOnVM() {
        typicalVM().putAll(typicalVM());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClearOnVM() {
        typicalVM().clear();
    }

    /**
     * SLING-9774 - Test that a key=null value in the first map is used
     * instead of the key=value entry in the second map
     */
    @Test
    public void testNullValueForExistingKey() {
        // value is null in the first map
        ValueMap v1 = createValueMap("k1", null);

        // value is not null in the second map
        ValueMap v2 = createValueMap("k1", "11");

        // expected to get null value since the first
        //   map had a real key
        ValueMap merge = merge(v1, v2);
        assertTrue(merge.containsKey("k1"));
        assertNull(merge.get("k1"));
    }

    private static ValueMap createValueMap(Object... pairs) {
        ValueMap vm = new ValueMapDecorator(new HashMap<>());
        for (int i = 0; i < pairs.length; i += 2) {
            vm.put(pairs[i].toString(), pairs[i + 1]);
        }
        return vm;
    }

    /**
     * @return typical vm, with 3 inner resources, sharing properties from k1 to k5, noted kX each level Y's value
     * being YX
     */
    private ValueMap typicalVM() {
        ValueMap v1 = createValueMap("k1", "11", "k3", "13");
        ValueMap v2 = createValueMap("k1", "21", "k2", "22", "k4", "24");
        ValueMap v3 = createValueMap("k1", "31", "k3", "33", "k4", "34", "k5", "35");
        return merge(v1, v2, v3);
    }
}
