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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * Internal {@link SlingJakartaHttpServletRequest} implementation.
 */
public class SlingJakartaHttpServletRequestImpl extends SlingAdaptable implements SlingJakartaHttpServletRequest {

    private final SlingHttpServletRequestBuilderImpl builder;

    public SlingJakartaHttpServletRequestImpl(final SlingHttpServletRequestBuilderImpl builder) {
        this.builder = builder;
    }

    @Override
    public Resource getResource() {
        return this.builder.resource;
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return this.getResource().getResourceResolver();
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(final boolean create) {
        if (this.builder.session == null && create) {
            if ( this.builder.sessionProvider != null ) {
                this.builder.session = this.builder.sessionProvider.getSession(create);
            } else {
                this.builder.session = new HttpSessionImpl(this.builder.servletContext);
           }
        }
        return this.builder.session;
    }

    @Override
    public RequestPathInfo getRequestPathInfo() {
        return this.builder.requestPathInfo;
    }

    @Override
    public Object getAttribute(final String name) {
        if ( this.builder.attributesProvider != null ) {
            return this.builder.attributesProvider.getAttribute(name);
        }
        return this.builder.attributeMap.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        if ( this.builder.attributesProvider != null ) {
            return this.builder.attributesProvider.getAttributeNames();
        }
        return Collections.enumeration(this.builder.attributeMap.keySet());
    }

    @Override
    public void removeAttribute(final String name) {
        if ( this.builder.attributesProvider != null ) {
            this.builder.attributesProvider.removeAttribute(name);
        } else {
            this.builder.attributeMap.remove(name);
        }
    }

    @Override
    public void setAttribute(final String name, final Object object) {
        if ( this.builder.attributesProvider != null ) {
            this.builder.attributesProvider.setAttribute(name, object);
        } else {
            this.builder.attributeMap.put(name, object);
        }
    }

    @Override
    public String getParameter(final String name) {
        final String[] values = this.builder.parameters.get(name);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(this.builder.parameters);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.builder.parameters.keySet());
    }

    @Override
    public String[] getParameterValues(final String name) {
        return this.builder.parameters.get(name);
    }

    @Override
    public RequestParameter getRequestParameter(final String name) {
        return this.getRequestParameterMap().getValue(name);
    }

    @Override
    public RequestParameterMap getRequestParameterMap() {
        if ( this.builder.requestParameterMap == null ) {
            this.builder.requestParameterMap = new RequestParameterMapImpl(this.builder.parameters);
        }
        return this.builder.requestParameterMap;
    }

    @Override
    public RequestParameter[] getRequestParameters(final String name) {
        return this.getRequestParameterMap().get(name);
    }

    @Override
    public List<RequestParameter> getRequestParameterList() {
        final List<RequestParameter> params = new ArrayList<>();
        for (final RequestParameter[] requestParameters : getRequestParameterMap().values()) {
            params.addAll(Arrays.asList(requestParameters));
        }
        return params;
    }

    @Override
    public Collection<Part> getParts() {
        return Collections.emptyList();
    }

    @Override
    public Part getPart(final String name) {
        return null;
    }

    @Override
    public Locale getLocale() {
        return this.builder.locale;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(Collections.singleton(getLocale()));
    }

    @Override
    public String getContextPath() {
        return this.builder.contextPath;
    }

    @Override
    public String getQueryString() {
        return this.builder.queryString;
    }

    @Override
    public String getScheme() {
        return this.builder.scheme;
    }

    @Override
    public String getServerName() {
        return this.builder.serverName;
    }

    @Override
    public int getServerPort() {
        return this.builder.serverPort;
    }

    @Override
    public boolean isSecure() {
        return SlingHttpServletRequestBuilderImpl.SECURE_PROTOCOL.equals(this.builder.scheme);
    }

    @Override
    public String getMethod() {
        return this.builder.requestMethod;
    }

    @Override
    public long getDateHeader(final String name) {
        return builder.headerSupport.getDateHeader(name);
    }

    @Override
    public String getHeader(final String name) {
        return builder.headerSupport.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(builder.headerSupport.getHeaderNames());
    }

    @Override
    public Enumeration<String> getHeaders(final String name) {
        return Collections.enumeration(builder.headerSupport.getHeaders(name));
    }

    @Override
    public int getIntHeader(final String name) {
        return builder.headerSupport.getIntHeader(name);
    }

    @Override
    public Cookie getCookie(final String name) {
        return this.builder.cookies.get(name);
    }

    @Override
    public Cookie[] getCookies() {
        if ( this.builder.cookies.isEmpty() ) {
            return null;
        }
        return this.builder.cookies.values().toArray(new Cookie[this.builder.cookies.size()]);
    }

    @Override
    public ResourceBundle getResourceBundle(final Locale locale) {
        return getResourceBundle(null, locale);
    }

    @Override
    public ResourceBundle getResourceBundle(final String baseName, final Locale locale) {
        return SlingHttpServletRequestBuilderImpl.EMPTY_RESOURCE_BUNDLE;
    }

    @Override
    public String getCharacterEncoding() {
        return this.builder.characterEncoding;
    }

    @Override
    public void setCharacterEncoding(final String encoding) throws UnsupportedEncodingException {
        this.builder.characterEncoding = encoding;
    }

    @Override
    public String getContentType() {
        if (this.builder.contentType == null) {
            return null;
        } else if ( this.builder.characterEncoding == null ) {
            return this.builder.contentType;
        }
        return this.builder.contentType.concat(SlingHttpServletRequestBuilderImpl.CHARSET_SEPARATOR).concat(this.builder.characterEncoding);
    }

    @Override
    public ServletInputStream getInputStream() {
        if (this.builder.getReaderCalled) {
            throw new IllegalStateException();
        }
        this.builder.getInputStreamCalled = true;
        return new ServletInputStream() {
            private final InputStream is = new ByteArrayInputStream(builder.body.getBytes(StandardCharsets.UTF_8));

            @Override
            public int read() throws IOException {
                return is.read();
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public boolean isFinished() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        if (this.builder.getInputStreamCalled) {
            throw new IllegalStateException();
        }
        this.builder.getReaderCalled = true;
        return new BufferedReader(new StringReader(this.builder.body));
    }

    @Override
    public int getContentLength() {
        return this.builder.body.length();
    }

    @Override
    public long getContentLengthLong() {
        return this.getContentLength();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path) {
        if ( this.builder.requestDispatcherProvider != null ) {
            return this.builder.requestDispatcherProvider.getRequestDispatcher(path);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path, final RequestDispatcherOptions options) {
        if ( this.builder.requestDispatcherProvider != null ) {
            return this.builder.requestDispatcherProvider.getRequestDispatcher(path, options);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final Resource resource) {
        if ( this.builder.requestDispatcherProvider != null ) {
            return this.builder.requestDispatcherProvider.getRequestDispatcher(resource);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final Resource resource, final RequestDispatcherOptions options) {
        if ( this.builder.requestDispatcherProvider != null ) {
            return this.builder.requestDispatcherProvider.getRequestDispatcher(resource, options);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemoteUser() {
        return this.builder.remoteUser;
    }

    @Override
    public String getRemoteAddr() {
        return this.builder.remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        return this.builder.remoteHost;
    }

    @Override
    public int getRemotePort() {
        return this.builder.remotePort;
    }

    @Override
    public String getServletPath() {
        return this.builder.servletPath;
    }

    @Override
    public String getPathInfo() {
        return this.builder.pathInfo;
    }

    @Override
    public String getRequestURI() {
        final StringBuilder requestUri = new StringBuilder();
        requestUri.append(this.builder.contextPath);
        requestUri.append(this.builder.servletPath);
        requestUri.append(this.builder.pathInfo);
        return requestUri.toString();
    }

    @Override
    public StringBuffer getRequestURL() {
        final StringBuffer requestUrl = new StringBuffer();

        requestUrl.append(this.builder.scheme);
        requestUrl.append("://");
        requestUrl.append(this.builder.serverName);
        boolean includePort = true;
        if ( (SlingHttpServletRequestBuilderImpl.HTTP_PROTOCOL.equals(this.builder.scheme) && this.builder.serverPort == 80 )
             || (SlingHttpServletRequestBuilderImpl.SECURE_PROTOCOL.equals(this.builder.scheme) && this.builder.serverPort == 443) ) {
            includePort = false;
        }
        if ( includePort ) {
            requestUrl.append(':');
            requestUrl.append(this.builder.serverPort);
        }
        requestUrl.append(getRequestURI());

        return requestUrl;
    }

    @Override
    public String getAuthType() {
        return this.builder.authType;
    }

    @Override
    public String getResponseContentType() {
        return this.builder.responseContentType;
    }

    @Override
    public Enumeration<String> getResponseContentTypes() {
        return Collections.enumeration(Collections.singleton(this.builder.responseContentType));
    }

    @Override
    public RequestProgressTracker getRequestProgressTracker() {
        return this.builder.progressTracker;
    }

    @Override
    public ServletContext getServletContext() {
        return this.builder.servletContext;
    }

    // --- unsupported operations ---

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUserInRole(String role) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void login(String pUsername, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocolRequestId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletConnection getServletConnection() {
        throw new UnsupportedOperationException();
    }
}
