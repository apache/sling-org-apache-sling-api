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

import java.util.Locale;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.felix.http.javaxwrappers.CookieWrapper;
import org.apache.felix.http.javaxwrappers.HttpServletRequestWrapper;
import org.apache.felix.http.javaxwrappers.ServletRequestWrapper;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
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
public class JakartaToJavaxRequestWrapperTest {

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#toJavaxRequest(jakarta.servlet.ServletRequest)}.
     */
    @Test
    public void testToJavaxRequestServletRequestForNull() {
        ServletRequest original = null;
        assertNull(JakartaToJavaxRequestWrapper.toJavaxRequest(original));
    }

    @Test
    public void testToJavaxRequestServletRequestForJavaxToJakartaRequestWrapper() {
        javax.servlet.ServletRequest originalRequest = Mockito.mock(javax.servlet.ServletRequest.class);
        JavaxToJakartaRequestWrapper originalWrapper = Mockito.mock(JavaxToJakartaRequestWrapper.class);
        Mockito.when(originalWrapper.getRequest()).thenReturn(originalRequest);
        assertSame(originalRequest, JakartaToJavaxRequestWrapper.toJavaxRequest((ServletRequest) originalWrapper));
    }

    @Test
    public void testToJavaxRequestServletRequestForSlingJakartaHttpServletRequest() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        javax.servlet.ServletRequest javaxRequest =
                JakartaToJavaxRequestWrapper.toJavaxRequest((ServletRequest) originalRequest);
        assertTrue(javaxRequest instanceof JakartaToJavaxRequestWrapper);
        assertSame(originalRequest, ((JakartaToJavaxRequestWrapper) javaxRequest).getRequest());
    }

    @Test
    public void testToJavaxRequestServletRequestForHttpServletRequest() {
        HttpServletRequest originalRequest = Mockito.mock(HttpServletRequest.class);
        javax.servlet.ServletRequest javaxRequest =
                JakartaToJavaxRequestWrapper.toJavaxRequest((ServletRequest) originalRequest);
        assertTrue(javaxRequest instanceof HttpServletRequestWrapper);
        assertSame(originalRequest, ((HttpServletRequestWrapper) javaxRequest).getRequest());
    }

    @Test
    public void testToJavaxRequestServletRequestForOther() {
        ServletRequest originalRequest = Mockito.mock(ServletRequest.class);
        javax.servlet.ServletRequest javaxRequest =
                JakartaToJavaxRequestWrapper.toJavaxRequest((ServletRequest) originalRequest);
        assertTrue(javaxRequest instanceof ServletRequestWrapper);
        assertSame(originalRequest, ((ServletRequestWrapper) javaxRequest).getRequest());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#toJavaxRequest(jakarta.servlet.http.HttpServletRequest)}.
     */
    @Test
    public void testToJavaxRequestHttpServletRequestForNull() {
        HttpServletRequest originalRequest = null;
        javax.servlet.http.HttpServletRequest javaxRequest =
                JakartaToJavaxRequestWrapper.toJavaxRequest(originalRequest);
        assertNull(javaxRequest);
    }

    @Test
    public void testToJavaxRequestHttpServletRequest() {
        HttpServletRequest originalRequest = Mockito.mock(HttpServletRequest.class);
        javax.servlet.http.HttpServletRequest javaxRequest =
                JakartaToJavaxRequestWrapper.toJavaxRequest(originalRequest);
        assertTrue(javaxRequest instanceof HttpServletRequestWrapper);
        assertSame(originalRequest, ((HttpServletRequestWrapper) javaxRequest).getRequest());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#toJavaxRequest(org.apache.sling.api.SlingJakartaHttpServletRequest)}.
     */
    @Test
    public void testToJavaxRequestSlingJakartaHttpServletRequestForNull() {
        SlingJakartaHttpServletRequest originalRequest = null;
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletRequest javaxRequest =
                JakartaToJavaxRequestWrapper.toJavaxRequest(originalRequest);
        assertNull(javaxRequest);
    }

    @Test
    public void testToJavaxRequestSlingJakartaHttpServletRequest() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletRequest javaxRequest =
                JakartaToJavaxRequestWrapper.toJavaxRequest(originalRequest);
        assertTrue(javaxRequest instanceof JakartaToJavaxRequestWrapper);
        assertSame(originalRequest, ((JakartaToJavaxRequestWrapper) javaxRequest).getRequest());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getCookie(java.lang.String)}.
     */
    @Test
    public void testGetCookieThatDoesNotExist() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        assertNull(requestWrapper.getCookie("test"));
        Mockito.verify(originalRequest, times(1)).getCookie("test");
    }

    @Test
    public void testGetCookie() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Cookie testCookie = new Cookie("test", "value");
        Mockito.when(originalRequest.getCookie("test")).thenReturn(testCookie);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        javax.servlet.http.Cookie foundCookie = requestWrapper.getCookie("test");
        assertTrue(foundCookie instanceof CookieWrapper);
        assertEquals("test", foundCookie.getName());
        Mockito.verify(originalRequest, times(1)).getCookie("test");
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getRequestDispatcher(java.lang.String, org.apache.sling.api.request.RequestDispatcherOptions)}.
     */
    @Test
    public void testGetRequestDispatcherStringRequestDispatcherOptionsWithNullDispatcher() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Resource mockResource = Mockito.mock(Resource.class);
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        Mockito.when(originalRequest.getRequestDispatcher(mockResource, options))
                .thenReturn(null);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        String path = "/test";
        requestWrapper.getRequestDispatcher(path, options);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(path, options);
    }

    @Test
    public void testGetRequestDispatcherStringRequestDispatcherOptions() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        String path = "/test";
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(originalRequest.getRequestDispatcher(path, options)).thenReturn(mockDispatcher);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(path, options);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(path, options);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getRequestDispatcher(org.apache.sling.api.resource.Resource, org.apache.sling.api.request.RequestDispatcherOptions)}.
     */
    @Test
    public void testGetRequestDispatcherResourceRequestDispatcherOptionsWithNullDispatcher() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Resource mockResource = Mockito.mock(Resource.class);
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        Mockito.when(originalRequest.getRequestDispatcher(mockResource, options))
                .thenReturn(null);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(mockResource, options);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(mockResource, options);
    }

    @Test
    public void testGetRequestDispatcherResourceRequestDispatcherOptions() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Resource mockResource = Mockito.mock(Resource.class);
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(originalRequest.getRequestDispatcher(mockResource, options))
                .thenReturn(mockDispatcher);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(mockResource, options);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(mockResource, options);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getRequestDispatcher(org.apache.sling.api.resource.Resource)}.
     */
    @Test
    public void testGetRequestDispatcherResourceWithNullDispatcher() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Resource mockResource = Mockito.mock(Resource.class);
        Mockito.when(originalRequest.getRequestDispatcher(mockResource)).thenReturn(null);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(mockResource);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(mockResource);
    }

    @Test
    public void testGetRequestDispatcherResource() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Resource mockResource = Mockito.mock(Resource.class);
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(originalRequest.getRequestDispatcher(mockResource)).thenReturn(mockDispatcher);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(mockResource);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(mockResource);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getRequestParameter(java.lang.String)}.
     */
    @Test
    public void testGetRequestParameter() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        String name = "name";
        requestWrapper.getRequestParameter(name);
        Mockito.verify(originalRequest, times(1)).getRequestParameter(name);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getRequestParameterList()}.
     */
    @Test
    public void testGetRequestParameterList() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getRequestParameterList();
        Mockito.verify(originalRequest, times(1)).getRequestParameterList();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getRequestParameterMap()}.
     */
    @Test
    public void testGetRequestParameterMap() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getRequestParameterMap();
        Mockito.verify(originalRequest, times(1)).getRequestParameterMap();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getRequestParameters(java.lang.String)}.
     */
    @Test
    public void testGetRequestParameters() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        String name = "name";
        requestWrapper.getRequestParameters(name);
        Mockito.verify(originalRequest, times(1)).getRequestParameters(name);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getRequestPathInfo()}.
     */
    @Test
    public void testGetRequestPathInfo() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getRequestPathInfo();
        Mockito.verify(originalRequest, times(1)).getRequestPathInfo();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getRequestProgressTracker()}.
     */
    @Test
    public void testGetRequestProgressTracker() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getRequestProgressTracker();
        Mockito.verify(originalRequest, times(1)).getRequestProgressTracker();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getResource()}.
     */
    @Test
    public void testGetResource() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getResource();
        Mockito.verify(originalRequest, times(1)).getResource();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getResourceBundle(java.util.Locale)}.
     */
    @Test
    public void testGetResourceBundleLocale() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        Locale locale = Locale.getDefault();
        requestWrapper.getResourceBundle(locale);
        Mockito.verify(originalRequest, times(1)).getResourceBundle(locale);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getResourceBundle(java.lang.String, java.util.Locale)}.
     */
    @Test
    public void testGetResourceBundleStringLocale() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        String baseName = "basename";
        Locale locale = Locale.getDefault();
        requestWrapper.getResourceBundle(baseName, locale);
        Mockito.verify(originalRequest, times(1)).getResourceBundle(baseName, locale);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getResourceResolver()}.
     */
    @Test
    public void testGetResourceResolver() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getResourceResolver();
        Mockito.verify(originalRequest, times(1)).getResourceResolver();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getResponseContentType()}.
     */
    @Test
    public void testGetResponseContentType() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getResponseContentType();
        Mockito.verify(originalRequest, times(1)).getResponseContentType();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#getResponseContentTypes()}.
     */
    @Test
    public void testGetResponseContentTypes() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        requestWrapper.getResponseContentTypes();
        Mockito.verify(originalRequest, times(1)).getResponseContentTypes();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper#adaptTo(java.lang.Class)}.
     */
    @Test
    public void testAdaptTo() {
        SlingJakartaHttpServletRequest originalRequest = Mockito.mock(SlingJakartaHttpServletRequest.class);
        JakartaToJavaxRequestWrapper requestWrapper = new JakartaToJavaxRequestWrapper(originalRequest);
        Class<? extends JakartaToJavaxRequestWrapperTest> toClass = getClass();
        requestWrapper.adaptTo(toClass);
        Mockito.verify(originalRequest, times(1)).adaptTo(toClass);
    }
}
