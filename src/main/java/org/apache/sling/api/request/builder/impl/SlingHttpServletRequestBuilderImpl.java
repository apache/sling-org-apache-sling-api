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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.felix.http.jakartawrappers.HttpServletRequestWrapper;
import org.apache.felix.http.jakartawrappers.RequestDispatcherWrapper;
import org.apache.felix.http.jakartawrappers.ServletContextWrapper;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.request.builder.Builders;
import org.apache.sling.api.request.builder.SlingHttpServletRequestBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.uri.SlingUriBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Internal {@link SlingHttpServletRequestBuilder} implementation
 */
public class SlingHttpServletRequestBuilderImpl implements SlingHttpServletRequestBuilder {

    /** Default http method */
    private static final String DEFAULT_METHOD = "GET";

    /** Protocol */
    static final String SECURE_PROTOCOL = "https";

    static final String HTTP_PROTOCOL = "http";

    static final String CHARSET_SEPARATOR = ";charset=";

    static final ResourceBundle EMPTY_RESOURCE_BUNDLE = new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[0][0];
        }
    };
    private static final String REQUEST = "request";

    /** Required resource */
    final Resource resource;

    /** Optional selectors */
    private String[] selectors;

    /** Optional extension */
    private String extension;

    /** Optional suffix */
    private String suffix;

    /** HTTP method */
    String requestMethod = DEFAULT_METHOD;

    /** Optional content type / character encoding */
    String contentType;

    String characterEncoding;

    /** Optional body */
    String body;

    /** Is the builder locked? */
    private boolean locked = false;

    /** Parameters map */
    final Map<String, String[]> parameters = new LinkedHashMap<>();

    /** Request path info */
    RequestPathInfo requestPathInfo;

    /** Optional query string */
    String queryString;

    /** The path info, calculated based on the provided resource */
    String pathInfo;

    /** Attributes */
    final Map<String, Object> attributeMap = new HashMap<>();

    /** On demand session */
    HttpSession session;

    /** On demand request parameter map */
    RequestParameterMap requestParameterMap;

    /** Headers */
    final HeaderSupport headerSupport = new HeaderSupport();

    /** Cookies */
    final Map<String, Cookie> cookies = new LinkedHashMap<>();

    /** Request progress tracker */
    RequestProgressTracker progressTracker;

    HttpServletRequest sessionProvider;

    HttpServletRequest attributesProvider;

    ServletContext servletContext;

    SlingJakartaHttpServletRequest requestDispatcherProvider;

    boolean getInputStreamCalled;
    boolean getReaderCalled;

    // the following fields are not settable atm
    final Locale locale = Locale.US;
    final String contextPath = "";
    final String scheme = HTTP_PROTOCOL;
    final String serverName = "localhost";
    final int serverPort = 80;
    String authType;
    String remoteUser;
    String remoteAddr;
    String remoteHost;
    int remotePort;
    String servletPath = "";
    String responseContentType;

    /**
     * Create a new request builder with the minimal information
     * @param resource The resource
     */
    public SlingHttpServletRequestBuilderImpl(final @NotNull Resource resource) {
        checkNotNull("resource", resource);
        this.resource = resource;
    }

    private void checkLocked() {
        if (locked) {
            throw new IllegalStateException("The builder can't be reused. Create a new builder instead.");
        }
    }

    private void checkNotNull(final String info, final Object candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException(info.concat(" is null"));
        }
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withRequestMethod(@NotNull String method) {
        this.checkLocked();
        this.checkNotNull("method", method);
        this.requestMethod = method.toUpperCase();
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withContentType(final @Nullable String type) {
        this.checkLocked();
        final int pos = type == null ? -1 : type.indexOf(SlingHttpServletRequestBuilderImpl.CHARSET_SEPARATOR);
        if (pos != -1) {
            this.contentType = type.substring(0, pos);
            this.characterEncoding =
                    type.substring(pos + SlingHttpServletRequestBuilderImpl.CHARSET_SEPARATOR.length());
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
    public @NotNull SlingHttpServletRequestBuilder withSelectors(final String... selectors) {
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
    public @NotNull SlingHttpServletRequestBuilder withParameter(
            final @NotNull String key, final @NotNull String value) {
        this.checkLocked();
        this.checkNotNull("key", key);
        this.checkNotNull("value", value);
        this.parameters.put(key, new String[] {value});
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder withParameter(
            final @NotNull String key, final @NotNull String[] values) {
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
    public @NotNull SlingHttpServletRequestBuilder useAttributesFrom(
            @NotNull javax.servlet.http.HttpServletRequest request) {
        this.checkLocked();
        this.checkNotNull(REQUEST, request);
        this.attributesProvider = new HttpServletRequestWrapper(request);
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder useServletContextFrom(
            @NotNull javax.servlet.http.HttpServletRequest request) {
        this.checkLocked();
        this.checkNotNull(REQUEST, request);
        this.servletContext = new ServletContextWrapper(request.getServletContext());
        return this;
    }

    @Override
    public @NotNull SlingHttpServletRequestBuilder useSessionFrom(
            @NotNull javax.servlet.http.HttpServletRequest request) {
        this.checkLocked();
        this.checkNotNull(REQUEST, request);
        this.sessionProvider = new HttpServletRequestWrapper(request);
        return this;
    }

    @Override
    @Deprecated
    public @NotNull SlingHttpServletRequestBuilder useRequestDispatcherFrom(
            @NotNull org.apache.sling.api.SlingHttpServletRequest request) {
        this.checkLocked();
        this.checkNotNull(REQUEST, request);
        this.requestDispatcherProvider = new SlingJakartaHttpServletRequestImpl(this) {

            @Override
            public RequestDispatcher getRequestDispatcher(final String path) {
                final javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher(path);
                if (dispatcher != null) {
                    return new RequestDispatcherWrapper(dispatcher);
                }
                return null;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(final String path, final RequestDispatcherOptions options) {
                final javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher(path, options);
                if (dispatcher != null) {
                    return new RequestDispatcherWrapper(dispatcher);
                }
                return null;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(final Resource resource) {
                final javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher(resource);
                if (dispatcher != null) {
                    return new RequestDispatcherWrapper(dispatcher);
                }
                return null;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(
                    final Resource resource, final RequestDispatcherOptions options) {
                final javax.servlet.RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
                if (dispatcher != null) {
                    return new RequestDispatcherWrapper(dispatcher);
                }
                return null;
            }
        };
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
    public @NotNull SlingHttpServletRequestBuilder useRequestDispatcherFrom(
            @NotNull SlingJakartaHttpServletRequest request) {
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
    @Deprecated
    public @NotNull org.apache.sling.api.SlingHttpServletRequest build() {
        this.checkLocked();
        this.locked = true;

        this.requestPathInfo = SlingUriBuilder.createFrom(this.resource)
                .setExtension(this.extension)
                .setSuffix(this.suffix)
                .setSelectors(this.selectors)
                .build();

        this.queryString = this.formatQueryString();
        this.pathInfo = this.buildPathInfo();

        if (this.servletContext == null) {
            this.servletContext = new ServletContextImpl();
        }
        if (this.body == null) {
            this.body = "";
        }
        final org.apache.sling.api.SlingHttpServletRequest req = new SlingHttpServletRequestImpl(this);
        if (this.progressTracker == null) {
            // if attributes are shared with a Sling request, then the progress tracker is available from there
            final Object attrTracker = req.getAttribute(RequestProgressTracker.class.getName());
            if (attrTracker instanceof RequestProgressTracker) {
                this.progressTracker = (RequestProgressTracker) attrTracker;
            } else {
                this.progressTracker = Builders.newRequestProgressTracker();
            }
        }
        return req;
    }

    @Override
    public @NotNull SlingJakartaHttpServletRequest buildJakartaRequest() {
        this.checkLocked();
        this.locked = true;

        this.requestPathInfo = SlingUriBuilder.createFrom(this.resource)
                .setExtension(this.extension)
                .setSuffix(this.suffix)
                .setSelectors(this.selectors)
                .build();

        this.queryString = this.formatQueryString();
        this.pathInfo = this.buildPathInfo();

        if (this.servletContext == null) {
            this.servletContext = new ServletContextImpl();
        }
        if (this.body == null) {
            this.body = "";
        }
        final SlingJakartaHttpServletRequest req = new SlingJakartaHttpServletRequestImpl(this);
        if (this.progressTracker == null) {
            // if attributes are shared with a Sling request, then the progress tracker is available from there
            final Object attrTracker = req.getAttribute(RequestProgressTracker.class.getName());
            if (attrTracker instanceof RequestProgressTracker) {
                this.progressTracker = (RequestProgressTracker) attrTracker;
            } else {
                this.progressTracker = Builders.newRequestProgressTracker();
            }
        }
        return req;
    }

    private String buildPathInfo() {
        final StringBuilder builder = new StringBuilder();

        builder.append(this.requestPathInfo.getResourcePath());
        if (this.requestPathInfo.getSelectorString() != null) {
            builder.append('.');
            builder.append(this.requestPathInfo.getSelectorString());
        }

        if (this.requestPathInfo.getExtension() != null) {
            builder.append('.');
            builder.append(this.requestPathInfo.getExtension());
        }

        if (this.requestPathInfo.getSuffix() != null) {
            builder.append(this.requestPathInfo.getSuffix());
        }

        return builder.toString();
    }

    private String formatQueryString() {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String[]> entry : this.parameters.entrySet()) {
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

    private static void formatQueryStringParameter(
            final StringBuilder builder, final Map.Entry<String, String[]> entry) {
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
}
