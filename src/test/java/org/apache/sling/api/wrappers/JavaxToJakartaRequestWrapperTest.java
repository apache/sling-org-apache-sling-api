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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;

import java.util.Locale;

import org.apache.felix.http.jakartawrappers.CookieWrapper;
import org.apache.felix.http.jakartawrappers.HttpServletRequestWrapper;
import org.apache.felix.http.jakartawrappers.ServletRequestWrapper;
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
public class JavaxToJakartaRequestWrapperTest {

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#toJakartaRequest(javax.servlet.ServletRequest)}.
     */
    @Test
    public void testToJakartaRequestServletRequestForNull() {
        ServletRequest originalRequest = null;
        jakarta.servlet.ServletRequest jakartaRequest =
                JavaxToJakartaRequestWrapper.toJakartaRequest((ServletRequest) originalRequest);
        assertNull(jakartaRequest);
    }

    @Test
    public void testToJakartaRequestServletRequestForJakartaToJavaxRequestWrapper() {
        jakarta.servlet.ServletRequest originalRequest = Mockito.mock(jakarta.servlet.ServletRequest.class);
        JakartaToJavaxRequestWrapper originalWrapper = Mockito.mock(JakartaToJavaxRequestWrapper.class);
        Mockito.when(originalWrapper.getRequest()).thenReturn(originalRequest);
        assertSame(originalRequest, JavaxToJakartaRequestWrapper.toJakartaRequest((ServletRequest) originalWrapper));
    }

    @Test
    public void testToJakartaRequestServletRequestForSlingHttpServletRequest() {
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        jakarta.servlet.ServletRequest jakartaRequest =
                JavaxToJakartaRequestWrapper.toJakartaRequest((ServletRequest) originalRequest);
        assertTrue(jakartaRequest instanceof JavaxToJakartaRequestWrapper);
        assertSame(originalRequest, ((JavaxToJakartaRequestWrapper) jakartaRequest).getRequest());
    }

    @Test
    public void testToJakartaRequestServletRequestForHttpServletRequest() {
        javax.servlet.http.HttpServletRequest originalRequest =
                Mockito.mock(javax.servlet.http.HttpServletRequest.class);
        jakarta.servlet.ServletRequest jakartaRequest =
                JavaxToJakartaRequestWrapper.toJakartaRequest((ServletRequest) originalRequest);
        assertTrue(jakartaRequest instanceof HttpServletRequestWrapper);
        assertSame(originalRequest, ((HttpServletRequestWrapper) jakartaRequest).getRequest());
    }

    @Test
    public void testToJakartaRequestServletRequestForOther() {
        ServletRequest originalRequest = Mockito.mock(ServletRequest.class);
        jakarta.servlet.ServletRequest jakartaRequest =
                JavaxToJakartaRequestWrapper.toJakartaRequest((ServletRequest) originalRequest);
        assertTrue(jakartaRequest instanceof ServletRequestWrapper);
        assertSame(originalRequest, ((ServletRequestWrapper) jakartaRequest).getRequest());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#toJakartaRequest(javax.servlet.http.HttpServletRequest)}.
     */
    @Test
    public void testToJakartaRequestHttpServletRequestForNull() {
        javax.servlet.http.HttpServletRequest originalRequest = null;
        jakarta.servlet.ServletRequest jakartaRequest = JavaxToJakartaRequestWrapper.toJakartaRequest(originalRequest);
        assertNull(jakartaRequest);
    }

    @Test
    public void testToJakartaRequestHttpServletRequest() {
        javax.servlet.http.HttpServletRequest originalRequest =
                Mockito.mock(javax.servlet.http.HttpServletRequest.class);
        jakarta.servlet.ServletRequest jakartaRequest = JavaxToJakartaRequestWrapper.toJakartaRequest(originalRequest);
        assertTrue(jakartaRequest instanceof HttpServletRequestWrapper);
        assertSame(originalRequest, ((HttpServletRequestWrapper) jakartaRequest).getRequest());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#toJakartaRequest(org.apache.sling.api.SlingHttpServletRequest)}.
     */
    @Test
    public void testToJakartaRequestSlingHttpServletRequestForNull() {
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletRequest originalRequest = null;
        org.apache.sling.api.SlingJakartaHttpServletRequest jakartaRequest =
                JavaxToJakartaRequestWrapper.toJakartaRequest(originalRequest);
        assertNull(jakartaRequest);
    }

    @Test
    public void testToJakartaRequestSlingHttpServletRequest() {
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        org.apache.sling.api.SlingJakartaHttpServletRequest jakartaRequest =
                JavaxToJakartaRequestWrapper.toJakartaRequest(originalRequest);
        assertTrue(jakartaRequest instanceof JavaxToJakartaRequestWrapper);
        assertSame(originalRequest, ((JavaxToJakartaRequestWrapper) jakartaRequest).getRequest());
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getCookie(java.lang.String)}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetCookieThatDoesNotExist() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        assertNull(requestWrapper.getCookie("test"));
        Mockito.verify(originalRequest, times(1)).getCookie("test");
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetCookie() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        Cookie testCookie = new Cookie("test", "value");
        Mockito.when(originalRequest.getCookie("test")).thenReturn(testCookie);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        jakarta.servlet.http.Cookie foundCookie = requestWrapper.getCookie("test");
        assertTrue(foundCookie instanceof CookieWrapper);
        assertEquals("test", foundCookie.getName());
        Mockito.verify(originalRequest, times(1)).getCookie("test");
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getRequestDispatcher(java.lang.String, org.apache.sling.api.request.RequestDispatcherOptions)}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestDispatcherStringRequestDispatcherOptionsWithNullDispatcher() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        String path = "/path1";
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        Mockito.when(originalRequest.getRequestDispatcher(path, options)).thenReturn(null);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(path, options);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(path, options);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestDispatcherStringRequestDispatcherOptions() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        String path = "/path1";
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(originalRequest.getRequestDispatcher(path, options)).thenReturn(mockDispatcher);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(path, options);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(path, options);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getRequestDispatcher(org.apache.sling.api.resource.Resource, org.apache.sling.api.request.RequestDispatcherOptions)}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestDispatcherResourceRequestDispatcherOptionsWithNullDispatcher() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        Resource mockResource = Mockito.mock(Resource.class);
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        Mockito.when(originalRequest.getRequestDispatcher(mockResource, options))
                .thenReturn(null);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(mockResource, options);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(mockResource, options);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestDispatcherResourceRequestDispatcherOptions() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        Resource mockResource = Mockito.mock(Resource.class);
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(originalRequest.getRequestDispatcher(mockResource, options))
                .thenReturn(mockDispatcher);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(mockResource, options);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(mockResource, options);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getRequestDispatcher(org.apache.sling.api.resource.Resource)}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestDispatcherResourceWithNullDispatcher() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        Resource mockResource = Mockito.mock(Resource.class);
        Mockito.when(originalRequest.getRequestDispatcher(mockResource)).thenReturn(null);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(mockResource);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(mockResource);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestDispatcherResource() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        Resource mockResource = Mockito.mock(Resource.class);
        RequestDispatcher mockDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(originalRequest.getRequestDispatcher(mockResource)).thenReturn(mockDispatcher);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestDispatcher(mockResource);
        Mockito.verify(originalRequest, times(1)).getRequestDispatcher(mockResource);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getRequestParameter(java.lang.String)}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestParameter() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        String name = "name";
        requestWrapper.getRequestParameter(name);
        Mockito.verify(originalRequest, times(1)).getRequestParameter(name);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getRequestParameterList()}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestParameterList() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestParameterList();
        Mockito.verify(originalRequest, times(1)).getRequestParameterList();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getRequestParameterMap()}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestParameterMap() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestParameterMap();
        Mockito.verify(originalRequest, times(1)).getRequestParameterMap();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getRequestParameters(java.lang.String)}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestParameters() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        String name = "name";
        requestWrapper.getRequestParameters(name);
        Mockito.verify(originalRequest, times(1)).getRequestParameters(name);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getRequestPathInfo()}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestPathInfo() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestPathInfo();
        Mockito.verify(originalRequest, times(1)).getRequestPathInfo();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getRequestProgressTracker()}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetRequestProgressTracker() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getRequestProgressTracker();
        Mockito.verify(originalRequest, times(1)).getRequestProgressTracker();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getResource()}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetResource() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getResource();
        Mockito.verify(originalRequest, times(1)).getResource();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getResourceBundle(java.util.Locale)}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetResourceBundleLocale() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        Locale locale = Locale.getDefault();
        requestWrapper.getResourceBundle(locale);
        Mockito.verify(originalRequest, times(1)).getResourceBundle(locale);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getResourceBundle(java.lang.String, java.util.Locale)}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetResourceBundleStringLocale() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        String basename = "basename";
        Locale locale = Locale.getDefault();
        requestWrapper.getResourceBundle(basename, locale);
        Mockito.verify(originalRequest, times(1)).getResourceBundle(basename, locale);
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getResourceResolver()}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetResourceResolver() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getResourceResolver();
        Mockito.verify(originalRequest, times(1)).getResourceResolver();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getResponseContentType()}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetResponseContentType() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getResponseContentType();
        Mockito.verify(originalRequest, times(1)).getResponseContentType();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#getResponseContentTypes()}.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetResponseContentTypes() {
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        requestWrapper.getResponseContentTypes();
        Mockito.verify(originalRequest, times(1)).getResponseContentTypes();
    }

    /**
     * Test method for {@link org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper#adaptTo(java.lang.Class)}.
     */
    @Test
    public void testAdaptTo() {
        @SuppressWarnings("deprecation")
        org.apache.sling.api.SlingHttpServletRequest originalRequest =
                Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        JavaxToJakartaRequestWrapper requestWrapper = new JavaxToJakartaRequestWrapper(originalRequest);
        Class<? extends JavaxToJakartaRequestWrapperTest> toClass = getClass();
        requestWrapper.adaptTo(toClass);
        Mockito.verify(originalRequest, times(1)).adaptTo(toClass);
    }
}
