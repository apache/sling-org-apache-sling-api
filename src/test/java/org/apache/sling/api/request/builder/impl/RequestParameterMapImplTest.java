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
package org.apache.sling.api.request.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RequestParameterMapImplTest {

    @Test public void testMap() {
        final Map<String, String[]> initial = new HashMap<>();
        initial.put("foo", new String[] {"bar"});
        initial.put("a", new String[] {"b", "c"});

        final RequestParameterMapImpl map = new RequestParameterMapImpl(initial);
        assertEquals(2, map.size());
        assertTrue(map.containsKey("foo"));
        assertTrue(map.containsKey("a"));

        assertEquals("bar", map.getValue("foo").getString());
        assertEquals("b", map.getValue("a").getString());
        assertNull(map.getValue("unknown"));
        assertEquals(2, map.getValues("a").length);
        assertEquals("b", map.getValues("a")[0].getString());
        assertEquals("c", map.getValues("a")[1].getString());
    }
}
