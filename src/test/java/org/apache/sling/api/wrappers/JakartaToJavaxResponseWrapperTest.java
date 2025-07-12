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
package org.apache.sling.api.wrappers;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.felix.http.javaxwrappers.HttpServletResponseWrapper;
import org.apache.felix.http.javaxwrappers.ServletResponseWrapper;
import org.apache.sling.api.SlingJakartaHttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

/**
 *
 */
public class JakartaToJavaxResponseWrapperTest {

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxResponseWrapper#toJavaxResponse(jakarta.servlet.ServletResponse)}.
     */
    @Test
    public void testToJavaxResponseServletResponseForNull() {
        ServletResponse original = null;
        assertNull(JakartaToJavaxResponseWrapper.toJavaxResponse(original));
    }

    @Test
    public void testToJavaxResponseServletResponseForJavaxToJakartaResponseWrapper() {
        javax.servlet.ServletResponse originalResponse = Mockito.mock(javax.servlet.ServletResponse.class);
        JavaxToJakartaResponseWrapper originalWrapper = Mockito.mock(JavaxToJakartaResponseWrapper.class);
        Mockito.when(originalWrapper.getResponse()).thenReturn(originalResponse);
        assertSame(originalResponse, JakartaToJavaxResponseWrapper.toJavaxResponse((ServletResponse) originalWrapper));
    }

    @Test
    public void testToJavaxResponseServletResponseForSlingJakartaHttpServletResponse() {
        SlingJakartaHttpServletResponse originalResponse = Mockito.mock(SlingJakartaHttpServletResponse.class);
        javax.servlet.ServletResponse javaxResponse =
                JakartaToJavaxResponseWrapper.toJavaxResponse((ServletResponse) originalResponse);
        assertTrue(javaxResponse instanceof JakartaToJavaxResponseWrapper);
        assertSame(originalResponse, ((JakartaToJavaxResponseWrapper) javaxResponse).getResponse());
    }

    @Test
    public void testToJavaxResponseServletResponseForHttpServletResponse() {
        HttpServletResponse originalResponse = Mockito.mock(HttpServletResponse.class);
        javax.servlet.ServletResponse javaxResponse =
                JakartaToJavaxResponseWrapper.toJavaxResponse((ServletResponse) originalResponse);
        assertTrue(javaxResponse instanceof HttpServletResponseWrapper);
        assertSame(originalResponse, ((HttpServletResponseWrapper) javaxResponse).getResponse());
    }

    @Test
    public void testToJavaxResponseServletResponseForServletResponse() {
        ServletResponse originalResponse = Mockito.mock(ServletResponse.class);
        javax.servlet.ServletResponse javaxResponse =
                JakartaToJavaxResponseWrapper.toJavaxResponse((ServletResponse) originalResponse);
        assertTrue(javaxResponse instanceof ServletResponseWrapper);
        assertSame(originalResponse, ((ServletResponseWrapper) javaxResponse).getResponse());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxResponseWrapper#toJavaxResponse(jakarta.servlet.http.HttpServletResponse)}.
     */
    @Test
    public void testToJavaxResponseHttpServletResponseForNull() {
        HttpServletResponse originalResponse = null;
        javax.servlet.http.HttpServletResponse javaxResponse =
                JakartaToJavaxResponseWrapper.toJavaxResponse(originalResponse);
        assertNull(javaxResponse);
    }

    @Test
    public void testToJavaxResponseHttpServletResponse() {
        HttpServletResponse originalResponse = Mockito.mock(HttpServletResponse.class);
        javax.servlet.http.HttpServletResponse javaxResponse =
                JakartaToJavaxResponseWrapper.toJavaxResponse(originalResponse);
        assertTrue(javaxResponse instanceof HttpServletResponseWrapper);
        assertSame(originalResponse, ((HttpServletResponseWrapper) javaxResponse).getResponse());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxResponseWrapper#toJavaxResponse(org.apache.sling.api.SlingJakartaHttpServletResponse)}.
     */
    @Test
    public void testToJavaxResponseSlingJakartaHttpServletResponse() {
        SlingJakartaHttpServletResponse originalResponse = Mockito.mock(SlingJakartaHttpServletResponse.class);
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletResponse javaxResponse =
                JakartaToJavaxResponseWrapper.toJavaxResponse(originalResponse);
        assertTrue(javaxResponse instanceof JakartaToJavaxResponseWrapper);
        assertSame(originalResponse, ((JakartaToJavaxResponseWrapper) javaxResponse).getResponse());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxResponseWrapper#adaptTo(java.lang.Class)}.
     */
    @Test
    public void testAdaptTo() {
        SlingJakartaHttpServletResponse originalResponse = Mockito.mock(SlingJakartaHttpServletResponse.class);
        JakartaToJavaxResponseWrapper responseWrapper = new JakartaToJavaxResponseWrapper(originalResponse);
        Class<? extends JakartaToJavaxResponseWrapperTest> toClass = getClass();
        responseWrapper.adaptTo(toClass);
        Mockito.verify(originalResponse, times(1)).adaptTo(toClass);
    }
}
