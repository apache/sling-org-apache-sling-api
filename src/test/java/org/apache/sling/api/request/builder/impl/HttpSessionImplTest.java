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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HttpSessionImplTest {

    @Test
    public void testServletContext() {
        HttpSessionImpl httpSession = new HttpSessionImpl(null);
        assertNull(httpSession.getServletContext());
        httpSession = new HttpSessionImpl(new ServletContextImpl());
        assertNotNull(httpSession.getServletContext());
    }

    @Test
    public void testId() {
        HttpSessionImpl httpSession = new HttpSessionImpl(new ServletContextImpl());
        assertNotNull(httpSession.getId());
    }

    @Test
    public void testCreationTime() {
        HttpSessionImpl httpSession = new HttpSessionImpl(new ServletContextImpl());
        assertTrue(httpSession.getCreationTime() > 0);
    }

    @Test
    public void testAttributes() {
        HttpSessionImpl httpSession = new HttpSessionImpl(new ServletContextImpl());
        httpSession.setAttribute("attr1", "value1");
        assertTrue(httpSession.getAttributeNames().hasMoreElements());
        assertEquals("value1", httpSession.getAttribute("attr1"));
        httpSession.removeAttribute("attr1");
        assertFalse(httpSession.getAttributeNames().hasMoreElements());
    }

    @Test
    public void testValues() {
        HttpSessionImpl httpSession = new HttpSessionImpl(new ServletContextImpl());
        httpSession.putValue("attr1", "value1");
        assertEquals(1, httpSession.getValueNames().length);
        assertEquals("value1", httpSession.getValue("attr1"));
        httpSession.removeValue("attr1");
        assertEquals(0, httpSession.getValueNames().length);
    }

    @Test
    public void testInvalidate() {
        HttpSessionImpl httpSession = new HttpSessionImpl(new ServletContextImpl());
        httpSession.invalidate();
        assertTrue(httpSession.isInvalidated());
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidateStateCheck() {
        HttpSessionImpl httpSession = new HttpSessionImpl(new ServletContextImpl());
        httpSession.invalidate();
        httpSession.getAttribute("attr1");
    }

    @Test
    public void testIsNew() {
        HttpSessionImpl httpSession = new HttpSessionImpl(new ServletContextImpl());
        assertTrue(httpSession.isNew());
   }

    @Test
    public void testGetLastAccessedTime() {
        HttpSessionImpl httpSession = new HttpSessionImpl(new ServletContextImpl());
        assertTrue(httpSession.getLastAccessedTime() > 0);
    }

    @Test
    public void testGetMaxInactiveInterval() {
        HttpSessionImpl httpSession = new HttpSessionImpl(new ServletContextImpl());
        assertTrue(httpSession.getMaxInactiveInterval() > 0);
        httpSession.setMaxInactiveInterval(123);
        assertEquals(123, httpSession.getMaxInactiveInterval());
    }
}
