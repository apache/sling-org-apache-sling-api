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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.SlingJakartaHttpServletResponse;
import org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper;
import org.apache.sling.api.wrappers.JakartaToJavaxResponseWrapper;
import org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper;
import org.apache.sling.api.wrappers.JavaxToJakartaResponseWrapper;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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

        // call the set again with the same param to make sure we are not creating
        //  a new wrapper object
        SlingHttpServletRequest original = bindings.getRequest();
        bindings.setJakartaRequest(r);
        assertSame(original, bindings.getRequest());

        bindings.setRequest(r2);
        assertSame(r2, bindings.getRequest());
        assertTrue(bindings.getJakartaRequest() instanceof JavaxToJakartaRequestWrapper);
        assertSame(r2, ((JavaxToJakartaRequestWrapper) bindings.getJakartaRequest()).getRequest());

        // call the set again with the same param to make sure we are not creating
        //  a new wrapper object
        SlingJakartaHttpServletRequest original2 = bindings.getJakartaRequest();
        bindings.setRequest(r2);
        assertSame(original2, bindings.getJakartaRequest());

        bindings.remove(SlingBindings.JAKARTA_REQUEST);
        assertNull(bindings.getRequest());
        assertNull(bindings.getJakartaRequest());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPutWithoutWrapping() {
        final SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.REQUEST, new Object());
        assertFalse(bindings.containsKey(SlingBindings.JAKARTA_REQUEST));

        bindings.clear();
        bindings.put(SlingBindings.JAKARTA_REQUEST, new Object());
        assertFalse(bindings.containsKey(SlingBindings.REQUEST));

        bindings.clear();
        bindings.put(SlingBindings.RESPONSE, new Object());
        assertFalse(bindings.containsKey(SlingBindings.JAKARTA_RESPONSE));

        bindings.clear();
        bindings.put(SlingBindings.JAKARTA_RESPONSE, new Object());
        assertFalse(bindings.containsKey(SlingBindings.RESPONSE));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPutWithReWrapping() {
        final SlingBindings bindings = new SlingBindings();
        // for code coverage to not match any of the cases
        bindings.put("other", new Object());

        final SlingJakartaHttpServletRequest jakartaRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        bindings.put(SlingBindings.JAKARTA_REQUEST, jakartaRequest);
        final SlingJakartaHttpServletResponse jakartaResponse = Mockito.mock(SlingJakartaHttpServletResponse.class);
        bindings.put(SlingBindings.JAKARTA_RESPONSE, jakartaResponse);

        SlingHttpServletRequest wrappedJavaxRequest1 = bindings.getRequest();
        final SlingJakartaHttpServletRequest jakartaRequest2 = Mockito.mock(SlingJakartaHttpServletRequest.class);
        bindings.put(SlingBindings.JAKARTA_REQUEST, jakartaRequest2);
        SlingHttpServletRequest wrappedJavaxRequest2 = bindings.getRequest();
        assertNotSame(wrappedJavaxRequest2, wrappedJavaxRequest1);

        SlingHttpServletResponse wrappedJavaxResponse1 = bindings.getResponse();
        final SlingJakartaHttpServletResponse jakartaResponse2 = Mockito.mock(SlingJakartaHttpServletResponse.class);
        bindings.put(SlingBindings.JAKARTA_RESPONSE, jakartaResponse2);
        SlingHttpServletResponse wrappedJavaxResponse2 = bindings.getResponse();
        assertNotSame(wrappedJavaxResponse2, wrappedJavaxResponse1);

        bindings.clear();
        final SlingHttpServletRequest javaxRequest = Mockito.mock(SlingHttpServletRequest.class);
        bindings.put(SlingBindings.REQUEST, javaxRequest);
        final SlingHttpServletResponse javaxResponse = Mockito.mock(SlingHttpServletResponse.class);
        bindings.put(SlingBindings.RESPONSE, javaxResponse);

        SlingJakartaHttpServletRequest wrappedJakartaRequest1 = bindings.getJakartaRequest();
        final SlingHttpServletRequest javaxRequest2 = Mockito.mock(SlingHttpServletRequest.class);
        bindings.put(SlingBindings.REQUEST, javaxRequest2);
        SlingJakartaHttpServletRequest wrappedJakartaRequest2 = bindings.getJakartaRequest();
        assertNotSame(wrappedJakartaRequest2, wrappedJakartaRequest1);

        SlingJakartaHttpServletResponse wrappedJakartaResponse1 = bindings.getJakartaResponse();
        final SlingHttpServletResponse javaxResponse2 = Mockito.mock(SlingHttpServletResponse.class);
        bindings.put(SlingBindings.RESPONSE, javaxResponse2);
        SlingJakartaHttpServletResponse wrappedJakartaResponse2 = bindings.getJakartaResponse();
        assertNotSame(wrappedJakartaResponse2, wrappedJakartaResponse1);
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

        // call the set again with the same param to make sure we are not creating
        //  a new wrapper object
        SlingHttpServletResponse original = bindings.getResponse();
        bindings.setJakartaResponse(r);
        assertSame(original, bindings.getResponse());

        bindings.setResponse(r2);
        assertSame(r2, bindings.getResponse());
        assertTrue(bindings.getJakartaResponse() instanceof JavaxToJakartaResponseWrapper);
        assertSame(r2, ((JavaxToJakartaResponseWrapper) bindings.getJakartaResponse()).getResponse());

        // call the set again with the same param to make sure we are not creating
        //  a new wrapper object
        SlingJakartaHttpServletResponse original2 = bindings.getJakartaResponse();
        bindings.setResponse(r2);
        assertSame(original2, bindings.getJakartaResponse());

        bindings.remove(SlingBindings.JAKARTA_RESPONSE);
        assertNull(bindings.getResponse());
        assertNull(bindings.getJakartaResponse());
    }
}
