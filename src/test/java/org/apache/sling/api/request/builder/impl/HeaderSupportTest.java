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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class HeaderSupportTest {
    
    @Test public void testDateHeaders() {
        final HeaderSupport support = new HeaderSupport();
        // no header set, return -1
        assertEquals(-1L, support.getDateHeader("date"));
        // set/get date header as long
        final long now = System.currentTimeMillis();
        support.addDateHeader("date", now);
        // precision is second (not millisecond)
        assertEquals(now - (now % 1000), support.getDateHeader("date"));
        assertNotNull(support.getHeader("date"));
        // no need to test exact output of JDK formatter
        assertTrue(support.getHeader("date").endsWith(" GMT"));
        // wrong format
        support.addHeader("nodate", "foo");
        try {
            support.getDateHeader("nodate");
            fail();
        } catch ( final IllegalArgumentException iae) {
            // expected
        }
    }

    @Test public void testIntDateHeaders() {
        final HeaderSupport support = new HeaderSupport();
        // no header set, return -1
        assertEquals(-1L, support.getIntHeader("number"));
        // set/get int header
        support.addIntHeader("number", 5);
        assertEquals(5, support.getIntHeader("number"));
        assertEquals("5", support.getHeader("number"));
        // wrong format
        support.addHeader("nonumber", "foo");
        try {
            support.getIntHeader("nonumber");
            fail();
        } catch ( final NumberFormatException nfe) {
            // expected
        }
    }

    @Test public void testAddSetHeaders() {
        final HeaderSupport support = new HeaderSupport();
        support.addHeader("string", "a");
        support.addHeader("string", "b");

        support.addIntHeader("number", 3);
        support.addIntHeader("number", 1);

        support.addDateHeader("date", 300000000);
        support.addDateHeader("date", 100000000);

        assertEquals("a", support.getHeader("string"));
        assertEquals(Arrays.asList("a", "b"), support.getHeaders("string"));

        assertEquals("3", support.getHeader("number"));
        assertEquals(Arrays.asList("3", "1"), support.getHeaders("number"));

        assertEquals(300000000, support.getDateHeader("date"));
        assertEquals(2, support.getHeaders("date").size());

        support.setHeader("string", "c");
        assertEquals("c", support.getHeader("string"));
        assertEquals(Arrays.asList("c"), support.getHeaders("string"));

        support.setIntHeader("number", 9);
        assertEquals("9", support.getHeader("number"));
        assertEquals(Arrays.asList("9"), support.getHeaders("number"));

        support.setDateHeader("date", 900000000);
        assertEquals(900000000, support.getDateHeader("date"));
        assertEquals(1, support.getHeaders("date").size());

        assertTrue(support.containsHeader("date"));
        assertFalse(support.containsHeader("foo"));
        
        assertTrue(support.getHeaders("foo").isEmpty());

        assertEquals(Arrays.asList("string", "number", "date"), new ArrayList<>(support.getHeaderNames()));

        support.reset();
        assertTrue(support.getHeaderNames().isEmpty());
    }
}
