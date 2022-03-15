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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.request.builder.Builders;
import org.apache.sling.api.request.builder.SlingHttpServletRequestBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.uri.SlingUriBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Internal {@link SlingHttpServletRequest} implementation.
 */
public class SlingHttpServletRequestImpl extends SlingAdaptable 
    implements SlingHttpServletRequest, SlingHttpServletRequestBuilder {

    /** Default http method */
    private static final String DEFAULT_METHOD = "GET";

    /** Protocol */
    private static final String SECURE_PROTOCOL = "https";
    private static final String HTTP_PROTOCOL = "http";

    static final String CHARSET_SEPARATOR = ";charset=";

    private static final ResourceBundle EMPTY_RESOURCE_BUNDLE = new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[0][0];
        }
    };
    private static final String REQUEST = "request";

    /** Required resource */
    private final Resource resource;

    /** Optional selectors */
    private String[] selectors;

    /** Optional extension */
    private String extension;

    /** Optional suffix */
    private String suffix;

    /** HTTP method */
    private String requestMethod = DEFAULT_METHOD;
    
    /** Optional content type / character encoding */
    private String contentType;
    private String characterEncoding;

    /** Optional body */
    private String body;

    /** Is the builder locked? */
    private boolean locked = false;

    /** Parameters map */
    private final Map<String, String[]> parameters = new LinkedHashMap<>();

    /** Request path info */
    private RequestPathInfo requestPathInfo;
    
    /** Optional query string */
    private String queryString;

    /** The path info, calculated based on the provided resource */
    private String pathInfo;

    /** Attributes */
    private final Map<String, Object> attributeMap = new HashMap<>();

    /** On demand session */
    private HttpSession session;

    /** On demand request parameter map */
    private RequestParameterMap requestParameterMap;

    /** Headers */
    private final HeaderSupport headerSupport = new HeaderSupport();
    
    /** Cookies */
    private final Map<String, Cookie> cookies = new LinkedHashMap<>();

    /** Request progress tracker */
    private RequestProgressTracker progressTracker;

    private HttpServletRequest sessionProvider;

    private HttpServletRequest attributesProvider;

    private ServletContext servletContext;

    private SlingHttpServletRequest requestDispatcherProvider;

    private boolean getInputStreamCalled;
    private boolean getReaderCalled;

    // the following fields are not settable atm
    private final Locale locale = Locale.US;
    private final String contextPath = "";
    private final String scheme = HTTP_PROTOCOL;
    private final String serverName = "localhost";
    private final int serverPort = 80;
    private String authType;
    private String remoteUser;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private String servletPath = "";
    private String responseContentType;
    
    /** 
     * Create a new request builder with the minimal information
     * @param resource The resource
     */
    public SlingHttpServletRequestImpl(final @NotNull Resource resource) {
        checkNotNull("resource", resource);
        this.resource = resource;
    }

    private void checkLocked() {
        if ( locked ) {
            throw new IllegalStateException("The builder can't be reused. Create a new builder instead.");
        }
    }

    private void checkNotNull(final String info, final Object candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException(info.concat(" is null"));
        }
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withRequestMethod(@NotNull  String method) {
        this.checkLocked();
        this.checkNotNull("method", method);
        this.requestMethod = method.toUpperCase();
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withContentType(final @Nullable String type) {
        this.checkLocked();
        final int pos = type == null ? -1 : type.indexOf(SlingHttpServletRequestImpl.CHARSET_SEPARATOR);
        if (pos != -1) {
            this.contentType = type.substring(0, pos);
            this.characterEncoding = type.substring(pos + SlingHttpServletRequestImpl.CHARSET_SEPARATOR.length());
        } else {
            this.contentType = type;
        }
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withBody(final @Nullable String content) {
        this.checkLocked();
        this.body = content;
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withSelectors(final String ... selectors) {
        this.checkLocked();
        this.selectors = selectors;
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withExtension(final String extension) {
        this.checkLocked();
        this.extension = extension;
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withSuffix(String suffix) {
        this.checkLocked();
        this.suffix = suffix;
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withParameter(final @NotNull String key, final @NotNull String value) {
        this.checkLocked();
        this.checkNotNull("key", key);
        this.checkNotNull("value", value);
        this.parameters.put(key, new String[] {value});
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withParameter(final @NotNull String key, final @NotNull String[] values) {
        this.checkLocked();
        this.checkNotNull("key", key);
        this.checkNotNull("values", values);
        this.parameters.put(key, values);
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withParameters(final @Nullable Map<String, String[]> parameters) {
        this.checkLocked();
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder useAttributesFrom(@NotNull HttpServletRequest request) {
        this.checkLocked();
        this.checkNotNull(REQUEST, request);
        this.attributesProvider = request;
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder useServletContextFrom(@NotNull HttpServletRequest request) {
        this.checkLocked();
        this.checkNotNull(REQUEST, request);
        this.servletContext = request.getServletContext();
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder useSessionFrom(@NotNull HttpServletRequest request) {
        this.checkLocked();
        this.checkNotNull(REQUEST, request);
        this.sessionProvider = request;
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder useRequestDispatcherFrom(@NotNull SlingHttpServletRequest request) {
        this.checkLocked();
        this.checkNotNull(REQUEST, request);
        this.requestDispatcherProvider = request;
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withRequestProgressTracker(@NotNull RequestProgressTracker tracker) {
        this.progressTracker = tracker;
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequest build() {
        this.checkLocked();
        this.locked = true;

        this.requestPathInfo = SlingUriBuilder.createFrom(this.resource)
            .setExtension(this.extension)
            .setSuffix(this.suffix)
            .setSelectors(this.selectors)
            .build();
        
        this.queryString = this.formatQueryString();
        this.pathInfo = this.buildPathInfo();

        if ( this.servletContext == null ) {
            this.servletContext = new ServletContextImpl();
        }
        if (this.body == null ) {
            this.body = "";
        }
        if (this.progressTracker == null) {
            // if attributes are shared with a Sling request, then the progress tracker is available from there
            final Object attrTracker = this.getAttribute(RequestProgressTracker.class.getName());
            if ( attrTracker instanceof RequestProgressTracker) {
                this.progressTracker = (RequestProgressTracker)attrTracker;
            } else {
                this.progressTracker = Builders.newRequestProgressTracker();
            }
        }
        return this;
    }

    private String buildPathInfo() {
        final StringBuilder builder = new StringBuilder();

        builder.append(this.requestPathInfo.getResourcePath());
        if ( this.requestPathInfo.getSelectorString() != null ) {
            builder.append('.');
            builder.append(this.requestPathInfo.getSelectorString());
        }

        if ( this.requestPathInfo.getExtension() != null ) {
            builder.append('.');
            builder.append(this.requestPathInfo.getExtension());
        }

        if ( this.requestPathInfo.getSuffix() != null ) {
            builder.append(this.requestPathInfo.getSuffix());
        }

        return builder.toString();
    }

    private String formatQueryString() {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String[]> entry : this.getParameterMap().entrySet()) {
            if (entry.getValue() != null) {
                formatQueryStringParameter(builder, entry);
            }
        }
        return builder.length() > 0 ? builder.toString() : null;
    }

    private static String encode(final String v) {
        try {
            return URLEncoder.encode(v, StandardCharsets.UTF_8.name());
        } catch (final UnsupportedEncodingException uee) {
            // UTF-8 is always supported, we return the string as-is to make the compiler happy
            return v;
        }
    }

    private static void formatQueryStringParameter(final StringBuilder builder, final Map.Entry<String, String[]> entry) {
        for (String value : entry.getValue()) {
            if (builder.length() != 0) {
                builder.append('&');
            }
            builder.append(encode(entry.getKey()));
            builder.append('=');
            if (value != null) {
                builder.append(encode(value));
            }
        }
    }

    @Override
    public Resource getResource() {
        return this.resource;
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
        if (this.session == null && create) {
            if ( this.sessionProvider != null ) {
                this.session = this.sessionProvider.getSession(create);
            } else {
                this.session = new HttpSessionImpl(this.servletContext);
           }
        }
        return this.session;
    }

    @Override
    public RequestPathInfo getRequestPathInfo() {
        return this.requestPathInfo;
    }

    @Override
    public Object getAttribute(final String name) {
        if ( this.attributesProvider != null ) {
            return this.attributesProvider.getAttribute(name);
        }
        return this.attributeMap.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        if ( this.attributesProvider != null ) {
            return this.attributesProvider.getAttributeNames();
        }
        return Collections.enumeration(this.attributeMap.keySet());
    }

    @Override
    public void removeAttribute(final String name) {
        if ( this.attributesProvider != null ) {
            this.attributesProvider.removeAttribute(name);
        } else {
            this.attributeMap.remove(name);
        }
    }

    @Override
    public void setAttribute(final String name, final Object object) {
        if ( this.attributesProvider != null ) {
            this.attributesProvider.setAttribute(name, object);
        } else {
            this.attributeMap.put(name, object);
        }
    }

    @Override
    public String getParameter(final String name) {
        final String[] values = this.parameters.get(name);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(this.parameters);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.parameters.keySet());
    }

    @Override
    public String[] getParameterValues(final String name) { 
        return this.parameters.get(name);
    }

    @Override
    public RequestParameter getRequestParameter(final String name) {
        return this.getRequestParameterMap().getValue(name);
    }

    @Override
    public RequestParameterMap getRequestParameterMap() {
        if ( this.requestParameterMap == null ) {
            this.requestParameterMap = new RequestParameterMapImpl(this.parameters);
        }
        return this.requestParameterMap;
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
        return this.locale;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(Collections.singleton(getLocale()));
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    public String getScheme() {
        return this.scheme;
    }

    @Override
    public String getServerName() {
        return this.serverName;
    }

    @Override
    public int getServerPort() {
        return this.serverPort;
    }

    @Override
    public boolean isSecure() {
        return SECURE_PROTOCOL.equals(this.scheme);
    }

    @Override
    public String getMethod() {
        return this.requestMethod;
    }

    @Override
    public long getDateHeader(final String name) {
        return headerSupport.getDateHeader(name);
    }

    @Override
    public String getHeader(final String name) {
        return headerSupport.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headerSupport.getHeaderNames());
    }

    @Override
    public Enumeration<String> getHeaders(final String name) {
        return Collections.enumeration(headerSupport.getHeaders(name));
    }
 
    @Override
    public int getIntHeader(final String name) {
        return headerSupport.getIntHeader(name);
    }

    @Override
    public Cookie getCookie(final String name) {
        return this.cookies.get(name);
    }

    @Override
    public Cookie[] getCookies() {
        if ( this.cookies.isEmpty() ) {
            return null;
        }
        return cookies.values().toArray(new Cookie[cookies.size()]);
    }

    @Override
    public ResourceBundle getResourceBundle(final Locale locale) {
        return getResourceBundle(null, locale);
    }

    @Override
    public ResourceBundle getResourceBundle(final String baseName, final Locale locale) {
        return EMPTY_RESOURCE_BUNDLE;
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public void setCharacterEncoding(final String encoding) throws UnsupportedEncodingException {
        this.characterEncoding = encoding;
    }

    @Override
    public String getContentType() {
        if (this.contentType == null) {
            return null;
        } else if ( this.characterEncoding == null ) {
            return this.contentType;
        }
        return this.contentType.concat(CHARSET_SEPARATOR).concat(this.characterEncoding);
    }

    @Override
    public ServletInputStream getInputStream() {
        if (getReaderCalled) {
            throw new IllegalStateException();
        }
        getInputStreamCalled = true;
        return new ServletInputStream() {
            private final InputStream is = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));

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
        if (getInputStreamCalled) {
            throw new IllegalStateException();
        }
        getReaderCalled = true;
        return new BufferedReader(new StringReader(this.body));
    }

    @Override
    public int getContentLength() {
        return this.body.length();
    }
    
    @Override
    public long getContentLengthLong() {
        return this.getContentLength();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path) {
        if ( this.requestDispatcherProvider != null ) {
            return this.requestDispatcherProvider.getRequestDispatcher(path);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path, final RequestDispatcherOptions options) {
        if ( this.requestDispatcherProvider != null ) {
            return this.requestDispatcherProvider.getRequestDispatcher(path, options);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final Resource resource) {
        if ( this.requestDispatcherProvider != null ) {
            return this.requestDispatcherProvider.getRequestDispatcher(resource);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final Resource resource, final RequestDispatcherOptions options) {
        if ( this.requestDispatcherProvider != null ) {
            return this.requestDispatcherProvider.getRequestDispatcher(resource, options);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemoteUser() {
        return remoteUser;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    @Override
    public String getServletPath() {
        return this.servletPath;
    }

    @Override
    public String getPathInfo() {        
        return this.pathInfo;
    }

    @Override
    public String getRequestURI() {
        final StringBuilder requestUri = new StringBuilder();
        requestUri.append(this.contextPath);
        requestUri.append(this.servletPath);
        requestUri.append(this.pathInfo);
        return requestUri.toString();
    }

    @Override
    public StringBuffer getRequestURL() {
        final StringBuffer requestUrl = new StringBuffer();

        requestUrl.append(this.scheme);
        requestUrl.append("://");
        requestUrl.append(this.serverName);
        boolean includePort = true;
        if ( (HTTP_PROTOCOL.equals(this.scheme) && this.serverPort == 80 )
             || (SECURE_PROTOCOL.equals(this.scheme) && this.serverPort == 443) ) {
            includePort = false;
        }
        if ( includePort ) {
            requestUrl.append(':');
            requestUrl.append(this.serverPort);
        }
        requestUrl.append(getRequestURI());

        return requestUrl;
    }

    @Override
    public String getAuthType() {
        return this.authType;
    }

    @Override
    public String getResponseContentType() {
        return responseContentType;
    }

    @Override
    public Enumeration<String> getResponseContentTypes() {
        return Collections.enumeration(Collections.singleton(responseContentType));
    }

    @Override
    public RequestProgressTracker getRequestProgressTracker() {
        return this.progressTracker;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
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
    public boolean isRequestedSessionIdFromUrl() {
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
    public String getRealPath(String path) {
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
}
