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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class FallbackValueMapTest extends Assert {

    @Rule
    public final SlingContext context = new SlingContext();

    @Test
    public void isEmptyTest(){
        Resource empty1 = context.build().resource("/content/test/1").getCurrentParent();
        Resource empty2 = context.build().resource("/content/test/2").getCurrentParent();
        FallbackValueMap vm = new FallbackValueMap(empty1, empty2);
        assertTrue("Value map should be empty", vm.isEmpty());
        assertFalse("Typical map should not be empty", typicalVM().isEmpty());
    }

    @Test
    public void keySetTest(){
        Set expected = new HashSet(Arrays.asList("k1","k2","k3","k4","k5"));
        assertEquals("key set should be k1-k5", expected, typicalVM().keySet());
    }

    @Test
    public void valuesTest(){
        Collection<Object> expected = Arrays.asList("11","22","13","24","35");
        assertEquals("values should be the one expected", expected, typicalVM().values());
    }

    @Test
    public void testGet(){
        FallbackValueMap vm = typicalVM();
        assertEquals("k1-11","11",vm.get("k1"));
        assertEquals("k2-22","22",vm.get("k2"));
        assertEquals("k3-13","13",vm.get("k3"));
        assertEquals("k4-24","24",vm.get("k4"));
        assertEquals("k5-35","35",vm.get("k5"));
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
    public void testContainsValue() {
        ValueMap vm = typicalVM();
        assertTrue("should contain 11", vm.containsValue("11"));
        assertFalse("should not contain 21", vm.containsValue("21"));
    }

    @Test
    public void testDeepGet(){
        Resource l1 = context.build().resource("/content/test/1/k",
            "1", "11").getCurrentParent().getParent();
        Resource l2 = context.build().resource("/content/test/2/k",
            "1", "21").getCurrentParent().getParent();
        ValueMap vm = new FallbackValueMap(l1,l2);
        assertEquals("value should be 11", "11", vm.get("k/1", String.class));
    }

    @Test
    public void testLong() throws PersistenceException {
        Resource lLong =  context.build().resource("/content/test/1").getCurrentParent().getParent();
        ModifiableValueMap vm = lLong.adaptTo(ModifiableValueMap.class);
        vm.put( "k", 11L);
        context.resourceResolver().commit();
        assertEquals("result should be the same than a simple property fetching","11", vm.get("k", String.class));
        assertEquals("result should be the same than a simple property fetching",11L, vm.get("k", Long.class).longValue());
    }

    @Test
    public void testBoolean() throws PersistenceException {
        Resource lBoolean =  context.build().resource("/content/test/1").getCurrentParent().getParent();
        ModifiableValueMap vm = lBoolean.adaptTo(ModifiableValueMap.class);
        vm.put( "k", true);
        context.resourceResolver().commit();
        assertEquals("result should be the same than a simple property fetching","true", vm.get("k", String.class));
        assertEquals("result should be the same than a simple property fetching",true, vm.get("k", Boolean.class).booleanValue());
    }

    /**
     * @return typical vm, with 3 inner resources, sharing properties from k1 to k5, noted kX each level Y's value
     * being YX
     */
    protected FallbackValueMap typicalVM() {
        Resource l1 = context.build().resource("/content/test/1",
            "k1", "11",
            "k3", "13").getCurrentParent();
        Resource l2 = context.build().resource("/content/test/2",
            "k1","21",
            "k2","22",
            "k4","24").getCurrentParent();
        Resource l3 = context.build().resource("/content/test/3",
            "k1","31",
            "k3","33",
            "k4","34",
            "k5","35").getCurrentParent();
        return new FallbackValueMap(l1,l2,l3);
    }
}
