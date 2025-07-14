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

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.http.jakartawrappers.HttpServletResponseWrapper;
import org.apache.felix.http.jakartawrappers.ServletResponseWrapper;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

/**
 *
 */
public class JavaxToJakartaResponseWrapperTest {

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaResponseWrapper#toJakartaResponse(javax.servlet.ServletResponse)}.
     */
    @Test
    public void testToJakartaResponseServletResponseForNull() {
        ServletResponse originalResponse = null;
        jakarta.servlet.ServletResponse jakartaResponse =
                JavaxToJakartaResponseWrapper.toJakartaResponse((ServletResponse) originalResponse);
        assertNull(jakartaResponse);
    }

    @Test
    public void testToJakartaResponseServletResponseForJakartaToJavaxResponseWrapper() {
        jakarta.servlet.ServletResponse originalResponse = Mockito.mock(jakarta.servlet.ServletResponse.class);
        JakartaToJavaxResponseWrapper originalWrapper = Mockito.mock(JakartaToJavaxResponseWrapper.class);
        Mockito.when(originalWrapper.getResponse()).thenReturn(originalResponse);
        assertSame(
                originalResponse, JavaxToJakartaResponseWrapper.toJakartaResponse((ServletResponse) originalWrapper));
    }

    @Test
    public void testToJakartaResponseServletResponseForSlingHttpServletResponse() {
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletResponse originalResponse =
                Mockito.mock(org.apache.sling.api.SlingHttpServletResponse.class);
        jakarta.servlet.ServletResponse jakartaResponse =
                JavaxToJakartaResponseWrapper.toJakartaResponse((ServletResponse) originalResponse);
        assertTrue(jakartaResponse instanceof JavaxToJakartaResponseWrapper);
        assertSame(originalResponse, ((JavaxToJakartaResponseWrapper) jakartaResponse).getResponse());
    }

    @Test
    public void testToJakartaResponseServletResponseForHttpServletResponse() {
        HttpServletResponse originalResponse = Mockito.mock(HttpServletResponse.class);
        jakarta.servlet.ServletResponse jakartaResponse =
                JavaxToJakartaResponseWrapper.toJakartaResponse((ServletResponse) originalResponse);
        assertTrue(jakartaResponse instanceof HttpServletResponseWrapper);
        assertSame(originalResponse, ((HttpServletResponseWrapper) jakartaResponse).getResponse());
    }

    @Test
    public void testToJakartaResponseServletResponseForOther() {
        ServletResponse originalResponse = Mockito.mock(ServletResponse.class);
        jakarta.servlet.ServletResponse jakartaResponse =
                JavaxToJakartaResponseWrapper.toJakartaResponse((ServletResponse) originalResponse);
        assertTrue(jakartaResponse instanceof ServletResponseWrapper);
        assertEquals(originalResponse, ((ServletResponseWrapper) jakartaResponse).getResponse());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaResponseWrapper#toJakartaResponse(javax.servlet.http.HttpServletResponse)}.
     */
    @Test
    public void testToJakartaResponseHttpServletResponseForNull() {
        HttpServletResponse originalResponse = null;
        jakarta.servlet.ServletResponse jakartaResponse =
                JavaxToJakartaResponseWrapper.toJakartaResponse(originalResponse);
        assertNull(jakartaResponse);
    }

    @Test
    public void testToJakartaResponseHttpServletResponse() {
        HttpServletResponse originalResponse = Mockito.mock(HttpServletResponse.class);
        jakarta.servlet.ServletResponse jakartaResponse =
                JavaxToJakartaResponseWrapper.toJakartaResponse(originalResponse);
        assertTrue(jakartaResponse instanceof HttpServletResponseWrapper);
        assertSame(originalResponse, ((HttpServletResponseWrapper) jakartaResponse).getResponse());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaResponseWrapper#toJakartaResponse(org.apache.sling.api.SlingHttpServletResponse)}.
     */
    @Test
    public void testToJakartaResponseSlingHttpServletResponseForNull() {
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletResponse originalResponse = null;
        jakarta.servlet.ServletResponse jakartaResponse =
                JavaxToJakartaResponseWrapper.toJakartaResponse(originalResponse);
        assertNull(jakartaResponse);
    }

    @Test
    public void testToJakartaResponseSlingHttpServletResponse() {
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletResponse originalResponse =
                Mockito.mock(org.apache.sling.api.SlingHttpServletResponse.class);
        jakarta.servlet.ServletResponse jakartaResponse =
                JavaxToJakartaResponseWrapper.toJakartaResponse(originalResponse);
        assertTrue(jakartaResponse instanceof JavaxToJakartaResponseWrapper);
        assertSame(originalResponse, ((JavaxToJakartaResponseWrapper) jakartaResponse).getResponse());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaResponseWrapper#adaptTo(java.lang.Class)}.
     */
    @Test
    public void testAdaptTo() {
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletResponse originalResponse =
                Mockito.mock(org.apache.sling.api.SlingHttpServletResponse.class);
        JavaxToJakartaResponseWrapper responseWrapper = new JavaxToJakartaResponseWrapper(originalResponse);
        Class<? extends JavaxToJakartaResponseWrapperTest> toClass = getClass();
        responseWrapper.adaptTo(toClass);
        Mockito.verify(originalResponse, times(1)).adaptTo(toClass);
    }
}
