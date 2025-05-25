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
package org.apache.sling.api.scripting;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.SlingJakartaHttpServletResponse;
import org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper;
import org.apache.sling.api.wrappers.JakartaToJavaxResponseWrapper;
import org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper;
import org.apache.sling.api.wrappers.JavaxToJakartaResponseWrapper;

public class SlingBindingsTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequest() {
        final SlingBindings bindings = new SlingBindings();
        assertNull(bindings.getRequest());
        assertNull(bindings.getJakartaRequest());

        final SlingJakartaHttpServletRequest r = Mockito.mock(SlingJakartaHttpServletRequest.class);
        bindings.setJakartaRequest(r);
        assertSame(r, bindings.getJakartaRequest());
        assertTrue(bindings.getRequest() instanceof JakartaToJavaxRequestWrapper);
        assertSame(r, ((JakartaToJavaxRequestWrapper) bindings.getRequest()).getRequest());

        bindings.remove(SlingBindings.REQUEST);
        assertNull(bindings.getRequest());
        assertNull(bindings.getJakartaRequest());

        final SlingHttpServletRequest r2 = Mockito.mock(SlingHttpServletRequest.class);
        bindings.setRequest(r2);
        assertSame(r2, bindings.getRequest());
        assertTrue(bindings.getJakartaRequest() instanceof JavaxToJakartaRequestWrapper);
        assertSame(r2, ((JavaxToJakartaRequestWrapper) bindings.getJakartaRequest()).getRequest());

        bindings.setJakartaRequest(r);
        assertSame(r, bindings.getJakartaRequest());
        assertTrue(bindings.getRequest() instanceof JakartaToJavaxRequestWrapper);
        assertSame(r, ((JakartaToJavaxRequestWrapper) bindings.getRequest()).getRequest());

        bindings.setRequest(r2);
        assertSame(r2, bindings.getRequest());
        assertTrue(bindings.getJakartaRequest() instanceof JavaxToJakartaRequestWrapper);
        assertSame(r2, ((JavaxToJakartaRequestWrapper) bindings.getJakartaRequest()).getRequest());

        bindings.remove(SlingBindings.JAKARTA_REQUEST);
        assertNull(bindings.getRequest());
        assertNull(bindings.getJakartaRequest());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetResponse() {
        final SlingBindings bindings = new SlingBindings();
        assertNull(bindings.getResponse());
        assertNull(bindings.getJakartaResponse());

        final SlingJakartaHttpServletResponse r = Mockito.mock(SlingJakartaHttpServletResponse.class);
        bindings.setJakartaResponse(r);
        assertSame(r, bindings.getJakartaResponse());
        assertTrue(bindings.getResponse() instanceof JakartaToJavaxResponseWrapper);
        assertSame(r, ((JakartaToJavaxResponseWrapper) bindings.getResponse()).getResponse());

        bindings.remove(SlingBindings.RESPONSE);
        assertNull(bindings.getResponse());
        assertNull(bindings.getJakartaResponse());

        final SlingHttpServletResponse r2 = Mockito.mock(SlingHttpServletResponse.class);
        bindings.setResponse(r2);
        assertSame(r2, bindings.getResponse());
        assertTrue(bindings.getJakartaResponse() instanceof JavaxToJakartaResponseWrapper);
        assertSame(r2, ((JavaxToJakartaResponseWrapper) bindings.getJakartaResponse()).getResponse());

        bindings.setJakartaResponse(r);
        assertSame(r, bindings.getJakartaResponse());
        assertTrue(bindings.getResponse() instanceof JakartaToJavaxResponseWrapper);
        assertSame(r, ((JakartaToJavaxResponseWrapper) bindings.getResponse()).getResponse());

        bindings.setResponse(r2);
        assertSame(r2, bindings.getResponse());
        assertTrue(bindings.getJakartaResponse() instanceof JavaxToJakartaResponseWrapper);
        assertSame(r2, ((JavaxToJakartaResponseWrapper) bindings.getJakartaResponse()).getResponse());

        bindings.remove(SlingBindings.JAKARTA_RESPONSE);
        assertNull(bindings.getResponse());
        assertNull(bindings.getJakartaResponse());
    }
}
