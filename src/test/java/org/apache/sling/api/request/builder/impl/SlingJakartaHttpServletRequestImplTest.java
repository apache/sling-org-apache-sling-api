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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.request.builder.Builders;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SlingJakartaHttpServletRequestImplTest {

    private SlingHttpServletRequestBuilderImpl builder;
    private SlingJakartaHttpServletRequest req;

    private Resource resource;

    @Before
    public void setup() {
        final ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        this.resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getPath()).thenReturn("/content/page");
        Mockito.when(resource.getResourceResolver()).thenReturn(resolver);

        this.builder = new SlingHttpServletRequestBuilderImpl(resource);
    }

    @Test(expected = IllegalStateException.class)
    public void testCheckLocked() {
        builder.buildJakartaRequest();
        builder.withExtension("foo");
    }

    @Test
    public void testGetResource() {
        req = builder.buildJakartaRequest();
        assertEquals(resource, req.getResource());
        assertEquals(resource.getResourceResolver(), req.getResourceResolver());
    }

    @Test
    public void testGetRequestPathInfo() {
        builder.withExtension("html").withSelectors("tidy", "json");
        req = builder.buildJakartaRequest();
        assertEquals("html", req.getRequestPathInfo().getExtension());
        assertEquals("/content/page", req.getRequestPathInfo().getResourcePath());
        assertEquals("tidy.json", req.getRequestPathInfo().getSelectorString());
        assertArrayEquals(
                new String[] {"tidy", "json"}, req.getRequestPathInfo().getSelectors());
        assertNull(req.getRequestPathInfo().getSuffix());
        assertNull(req.getRequestPathInfo().getSuffixResource());
    }

    @Test
    public void testInternalSession() {
        req = builder.buildJakartaRequest();
        assertNull(req.getSession(false));
        final HttpSession s = req.getSession();
        assertNotNull(s);
        assertSame(s, req.getSession(true));
        assertTrue(s instanceof HttpSessionImpl);
    }

    @Test
    public void testProvidedSession() {
        final SlingJakartaHttpServletRequest outer = Mockito.mock(SlingJakartaHttpServletRequest.class);
        final HttpSession outerSession = Mockito.mock(HttpSession.class);
        Mockito.when(outer.getSession()).thenReturn(outerSession);
        Mockito.when(outer.getSession(true)).thenReturn(outerSession);
        Mockito.when(outerSession.getId()).thenReturn("provided");

        req = builder.useSessionFrom(outer).buildJakartaRequest();
        assertNull(req.getSession(false));
        final HttpSession s = req.getSession();
        assertNotNull(s);
        assertSame(s, req.getSession(true));
        assertEquals("provided", s.getId());
    }

    @Test
    public void testInternalAttributes() {
        req = builder.buildJakartaRequest();
        assertFalse(req.getAttributeNames().hasMoreElements());
        req.setAttribute("a", "b");
        assertEquals("b", req.getAttribute("a"));
        assertEquals("a", req.getAttributeNames().nextElement());
        req.removeAttribute("a");
        assertFalse(req.getAttributeNames().hasMoreElements());
        assertNull(req.getAttribute("a"));
    }

    @Test
    public void testProvidedAttributes() {
        final SlingJakartaHttpServletRequest outer = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Mockito.when(outer.getAttributeNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        req = builder.useAttributesFrom(outer).buildJakartaRequest();
        req.getAttributeNames();
        Mockito.verify(outer, Mockito.atLeastOnce()).getAttributeNames();
        req.getAttribute("foo");
        Mockito.verify(outer, Mockito.atLeastOnce()).getAttribute("foo");
        req.removeAttribute("foo");
        Mockito.verify(outer, Mockito.atLeastOnce()).removeAttribute("foo");
        req.setAttribute("foo", "bar");
        Mockito.verify(outer, Mockito.atLeastOnce()).setAttribute("foo", "bar");
    }

    @Test
    public void testParameters() throws IOException, ServletException {
        req = builder.withParameter("a", "b")
                .withParameter("c", new String[] {"d", "e"})
                .withParameters(Collections.singletonMap("f", new String[] {"g"}))
                .withParameters(null)
                .buildJakartaRequest();

        assertEquals("b", req.getParameter("a"));
        assertEquals("d", req.getParameter("c"));
        assertEquals("g", req.getParameter("f"));
        assertNull(req.getParameter("g"));

        final Map<String, String[]> params = req.getParameterMap();
        assertEquals(3, params.size());
        assertArrayEquals(new String[] {"b"}, params.get("a"));
        assertArrayEquals(new String[] {"d", "e"}, params.get("c"));
        assertArrayEquals(new String[] {"g"}, params.get("f"));

        assertNotNull(req.getRequestParameter("a"));
        assertNotNull(req.getRequestParameter("c"));
        assertNotNull(req.getRequestParameter("f"));
        assertNull(req.getRequestParameter("g"));

        assertEquals(1, req.getRequestParameters("a").length);
        assertEquals(2, req.getRequestParameters("c").length);
        assertEquals(1, req.getRequestParameters("f").length);
        assertNull(req.getRequestParameters("g"));

        final List<RequestParameter> list = req.getRequestParameterList();
        assertEquals(4, list.size());

        assertTrue(req.getParts().isEmpty());
        assertNull(req.getPart("a"));
    }

    @Test
    public void testNoQueryString() {
        req = builder.buildJakartaRequest();
        assertNull(req.getQueryString());
    }

    @Test
    public void testQueryString() {
        req = builder.withParameter("a", "b")
                .withParameter("c", new String[] {"d", "e"})
                .withParameters(Collections.singletonMap("f", new String[] {"g"}))
                .buildJakartaRequest();

        assertEquals("a=b&c=d&c=e&f=g", req.getQueryString());
    }

    @Test
    public void testLocale() {
        req = builder.buildJakartaRequest();
        assertEquals(Locale.US, req.getLocale());
        assertEquals(Collections.singletonList(Locale.US), Collections.list(req.getLocales()));
    }

    @Test
    public void testGetContextPath() {
        req = builder.buildJakartaRequest();
        assertEquals("", req.getContextPath());
    }

    @Test
    public void testGetScheme() {
        req = builder.buildJakartaRequest();
        assertEquals("http", req.getScheme());
    }

    @Test
    public void testGetServerName() {
        req = builder.buildJakartaRequest();
        assertEquals("localhost", req.getServerName());
    }

    @Test
    public void testGetServerPort() {
        req = builder.buildJakartaRequest();
        assertEquals(80, req.getServerPort());
    }

    @Test
    public void testIsSecure() {
        req = builder.buildJakartaRequest();
        assertFalse(req.isSecure());
    }

    @Test
    public void testDefaultGetMethod() {
        req = builder.buildJakartaRequest();
        assertEquals("GET", req.getMethod());
    }

    @Test
    public void testGetMethod() {
        req = builder.withRequestMethod("POST").buildJakartaRequest();
        assertEquals("POST", req.getMethod());
    }

    @Test
    public void testHeaders() {
        req = builder.buildJakartaRequest();
        assertFalse(req.getHeaderNames().hasMoreElements());
        assertEquals(-1, req.getDateHeader("foo"));
        assertEquals(-1, req.getIntHeader("foo"));
        assertNull(req.getHeader("foo"));
        assertFalse(req.getHeaders("foo").hasMoreElements());
    }

    @Test
    public void testCookies() {
        req = builder.buildJakartaRequest();
        assertNull(req.getCookies());
        assertNull(req.getCookie("name"));
    }

    @Test
    public void testGetResourceBundle() {
        req = builder.buildJakartaRequest();
        assertNotNull(req.getResourceBundle(req.getLocale()));
        assertNotNull(req.getResourceBundle("base", req.getLocale()));
    }

    @Test
    public void testGetCharacterEncoding() throws UnsupportedEncodingException {
        req = builder.buildJakartaRequest();
        assertNull(req.getCharacterEncoding());
        req.setCharacterEncoding("UTF-8");
        assertEquals("UTF-8", req.getCharacterEncoding());
    }

    @Test
    public void testDefaultContentType() throws UnsupportedEncodingException {
        req = builder.buildJakartaRequest();
        assertNull(req.getContentType());
    }

    @Test
    public void testContentType() throws UnsupportedEncodingException {
        req = builder.withContentType("text/text").buildJakartaRequest();
        assertEquals("text/text", req.getContentType());
        assertNull(req.getCharacterEncoding());
        req.setCharacterEncoding("UTF-8");
        assertEquals("UTF-8", req.getCharacterEncoding());
        assertEquals("text/text;charset=UTF-8", req.getContentType());
    }

    @Test
    public void testNullContentType() {
        req = builder.withContentType(null).buildJakartaRequest();
        assertNull("null", req.getContentType());
    }

    @Test
    public void testContentTypeAndCharset() throws UnsupportedEncodingException {
        req = builder.withContentType("text/text;charset=UTF-16").buildJakartaRequest();
        assertEquals("text/text;charset=UTF-16", req.getContentType());
        assertEquals("UTF-16", req.getCharacterEncoding());
        req.setCharacterEncoding("UTF-8");
        assertEquals("UTF-8", req.getCharacterEncoding());
        assertEquals("text/text;charset=UTF-8", req.getContentType());
    }

    @Test
    public void testNoBody() {
        req = builder.buildJakartaRequest();
        assertEquals(0, req.getContentLength());
        assertEquals(0L, req.getContentLengthLong());
    }

    @Test
    public void testBodyReader() throws IOException {
        req = builder.withBody("body").buildJakartaRequest();
        assertEquals(4, req.getContentLength());
        assertEquals(4L, req.getContentLengthLong());
        final Reader r = req.getReader();
        try {
            req.getInputStream();
            fail();
        } catch (final IllegalStateException iae) {
        }
        final char[] cbuf = new char[96];
        final int l = r.read(cbuf);
        assertEquals("body", new String(cbuf, 0, l));
    }

    @Test
    public void testBodyInputStream() throws IOException {
        req = builder.withBody("body").buildJakartaRequest();
        assertEquals(4, req.getContentLength());
        assertEquals(4L, req.getContentLengthLong());
        final InputStream in = req.getInputStream();
        try {
            req.getReader();
            fail();
        } catch (final IllegalStateException iae) {
        }
        final byte[] buf = new byte[96];
        final int l = in.read(buf);
        assertEquals("body", new String(buf, 0, l));
    }

    @Test
    public void testDefaultRequestDispatcher() {
        final Resource rsrc = Mockito.mock(Resource.class);
        req = builder.buildJakartaRequest();
        try {
            req.getRequestDispatcher("/path");
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getRequestDispatcher("/path", new RequestDispatcherOptions());
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getRequestDispatcher(rsrc);
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getRequestDispatcher(rsrc, new RequestDispatcherOptions());
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
    }

    @Test
    public void testProvidedRequestDispatcher() {
        final Resource rsrc = Mockito.mock(Resource.class);
        final RequestDispatcherOptions opts = new RequestDispatcherOptions();
        final SlingJakartaHttpServletRequest outer = Mockito.mock(SlingJakartaHttpServletRequest.class);
        req = builder.useRequestDispatcherFrom(outer).buildJakartaRequest();

        req.getRequestDispatcher("/path");
        Mockito.verify(outer, Mockito.times(1)).getRequestDispatcher("/path");
        req.getRequestDispatcher("/path", opts);
        Mockito.verify(outer, Mockito.times(1)).getRequestDispatcher("/path", opts);
        req.getRequestDispatcher(rsrc);
        Mockito.verify(outer, Mockito.times(1)).getRequestDispatcher(rsrc);
        req.getRequestDispatcher(rsrc, opts);
        Mockito.verify(outer, Mockito.times(1)).getRequestDispatcher(rsrc, opts);
    }

    @Test
    public void testGetRemoteUser() {
        req = builder.buildJakartaRequest();
        assertNull(req.getRemoteUser());
    }

    @Test
    public void testGetRemoteAddr() {
        req = builder.buildJakartaRequest();
        assertNull(req.getRemoteAddr());
    }

    @Test
    public void testGetRemoteHost() {
        req = builder.buildJakartaRequest();
        assertNull(req.getRemoteHost());
    }

    @Test
    public void testGetRemotePort() {
        req = builder.buildJakartaRequest();
        assertEquals(0, req.getRemotePort());
    }

    @Test
    public void testGetServletPath() {
        req = builder.buildJakartaRequest();
        assertEquals("", req.getServletPath());
    }

    @Test
    public void testGetPathInfo() {
        req = builder.buildJakartaRequest();
        assertEquals("/content/page", req.getPathInfo());
    }

    @Test
    public void testGetRequestURI() {
        req = builder.buildJakartaRequest();
        assertEquals("/content/page", req.getRequestURI());
    }

    @Test
    public void testGetRequestURL() {
        req = builder.buildJakartaRequest();
        assertEquals("http://localhost/content/page", req.getRequestURL().toString());
    }

    @Test
    public void testGetAuthType() {
        req = builder.buildJakartaRequest();
        assertNull(req.getAuthType());
    }

    @Test
    public void getResponseContentType() {
        req = builder.buildJakartaRequest();
        assertNull(req.getResponseContentType());
        assertEquals(Collections.singletonList(null), Collections.list(req.getResponseContentTypes()));
    }

    @Test
    public void testNewGetRequestProgressTracker() {
        req = builder.buildJakartaRequest();
        assertNotNull(req.getRequestProgressTracker());
    }

    @Test
    public void testProvidedGetRequestProgressTracker() {
        final RequestProgressTracker t = Builders.newRequestProgressTracker();
        req = builder.withRequestProgressTracker(t).buildJakartaRequest();
        assertSame(t, req.getRequestProgressTracker());
    }

    @Test
    public void testProvidedByAttributesGetRequestProgressTracker() {
        // build a request with a tracker set in an attribute first
        final SlingJakartaHttpServletRequest orig =
                new SlingHttpServletRequestBuilderImpl(this.resource).buildJakartaRequest();
        final RequestProgressTracker t = Builders.newRequestProgressTracker();
        orig.setAttribute(RequestProgressTracker.class.getName(), t);

        req = builder.useAttributesFrom(orig).buildJakartaRequest();
        assertSame(t, req.getRequestProgressTracker());
    }

    @Test
    public void testDefaultServletContext() {
        req = builder.buildJakartaRequest();
        final ServletContext ctx = req.getServletContext();
        assertNotNull(ctx);
        assertTrue(ctx instanceof ServletContextImpl);
    }

    @Test
    public void testProvidedServletContext() {
        final SlingJakartaHttpServletRequest outer = Mockito.mock(SlingJakartaHttpServletRequest.class);
        final ServletContext outerCtx = Mockito.mock(ServletContext.class);
        Mockito.when(outer.getServletContext()).thenReturn(outerCtx);

        req = builder.useServletContextFrom(outer).buildJakartaRequest();
        final ServletContext ctx = req.getServletContext();
        assertNotNull(ctx);
        Mockito.verify(outer, Mockito.times(1)).getServletContext();
        assertFalse(ctx instanceof ServletContextImpl);
    }

    @Test
    public void testUnsupportedMethods() throws ServletException, IOException {
        req = builder.buildJakartaRequest();
        try {
            req.getPathTranslated();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getRequestedSessionId();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getUserPrincipal();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.isRequestedSessionIdFromCookie();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.isRequestedSessionIdFromURL();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.isRequestedSessionIdValid();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.isUserInRole("foo");
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getLocalAddr();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getLocalName();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getLocalPort();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.authenticate(null);
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.login("u", "p");
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.logout();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.startAsync();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.startAsync(null, null);
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.isAsyncStarted();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.isAsyncSupported();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getAsyncContext();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.getDispatcherType();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.changeSessionId();
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
        try {
            req.upgrade(null);
            fail();
        } catch (final UnsupportedOperationException expected) {
        }
    }
}
