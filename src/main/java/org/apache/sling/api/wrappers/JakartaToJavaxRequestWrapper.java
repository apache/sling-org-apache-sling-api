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
import javax.servlet.http.Cookie;

import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.felix.http.javaxwrappers.CookieWrapper;
import org.apache.felix.http.javaxwrappers.HttpServletRequestWrapper;
import org.apache.felix.http.javaxwrappers.RequestDispatcherWrapper;
import org.apache.felix.http.javaxwrappers.ServletRequestWrapper;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link SlingJakartaHttpServletRequest} to adapt it to the Javax Servlet API.
 * @since 2.9.0
 */
@SuppressWarnings("deprecation")
public class JakartaToJavaxRequestWrapper extends HttpServletRequestWrapper implements SlingHttpServletRequest {

    /**
     * Create a new wrapper
     * @param request Jakarta Servlet API based request object
     * @return The wrapped request
     */
    public static @Nullable javax.servlet.ServletRequest toJavaxRequest(@Nullable final ServletRequest request) {
        if (request == null) {
            return null;
        }
        if (request instanceof JavaxToJakartaRequestWrapper) {
            return ((JavaxToJakartaRequestWrapper) request).getRequest();
        }
        if (request instanceof SlingJakartaHttpServletRequest) {
            return new JakartaToJavaxRequestWrapper((SlingJakartaHttpServletRequest) request);
        }
        if (request instanceof HttpServletRequest) {
            return new HttpServletRequestWrapper((HttpServletRequest) request);
        }
        return new ServletRequestWrapper(request);
    }

    public static @Nullable javax.servlet.http.HttpServletRequest toJavaxRequest(
            @Nullable final HttpServletRequest request) {
        return (javax.servlet.http.HttpServletRequest) toJavaxRequest((ServletRequest) request);
    }

    public static @Nullable SlingHttpServletRequest toJavaxRequest(
            @Nullable final SlingJakartaHttpServletRequest request) {
        return (SlingHttpServletRequest) toJavaxRequest((ServletRequest) request);
    }

    private final SlingJakartaHttpServletRequest wrappedRequest;

    public JakartaToJavaxRequestWrapper(final SlingJakartaHttpServletRequest wrappedRequest) {
        super(wrappedRequest);
        this.wrappedRequest = wrappedRequest;
    }

    @Override
    public @Nullable Cookie getCookie(final String name) {
        final jakarta.servlet.http.Cookie cookie = this.wrappedRequest.getCookie(name);
        if (cookie != null) {
            return new CookieWrapper(cookie);
        }
        return null;
    }

    @Override
    public @Nullable RequestDispatcher getRequestDispatcher(
            @NotNull final String path, final RequestDispatcherOptions options) {
        final jakarta.servlet.RequestDispatcher dispatcher = this.wrappedRequest.getRequestDispatcher(path, options);
        if (dispatcher != null) {
            return new RequestDispatcherWrapper(dispatcher);
        }
        return null;
    }

    @Override
    public @Nullable RequestDispatcher getRequestDispatcher(
            @NotNull final Resource resource, RequestDispatcherOptions options) {
        final jakarta.servlet.RequestDispatcher dispatcher =
                this.wrappedRequest.getRequestDispatcher(resource, options);
        if (dispatcher != null) {
            return new RequestDispatcherWrapper(dispatcher);
        }
        return null;
    }

    @Override
    public @Nullable RequestDispatcher getRequestDispatcher(@NotNull final Resource resource) {
        final jakarta.servlet.RequestDispatcher dispatcher = this.wrappedRequest.getRequestDispatcher(resource);
        if (dispatcher != null) {
            return new RequestDispatcherWrapper(dispatcher);
        }
        return null;
    }

    @Override
    public @Nullable RequestParameter getRequestParameter(@NotNull final String name) {
        return this.wrappedRequest.getRequestParameter(name);
    }

    @Override
    public @NotNull List<RequestParameter> getRequestParameterList() {
        return this.wrappedRequest.getRequestParameterList();
    }

    @Override
    public @NotNull RequestParameterMap getRequestParameterMap() {
        return this.wrappedRequest.getRequestParameterMap();
    }

    @Override
    public @Nullable RequestParameter[] getRequestParameters(@NotNull final String name) {
        return this.wrappedRequest.getRequestParameters(name);
    }

    @Override
    public @NotNull RequestPathInfo getRequestPathInfo() {
        return this.wrappedRequest.getRequestPathInfo();
    }

    @Override
    public @NotNull RequestProgressTracker getRequestProgressTracker() {
        return this.wrappedRequest.getRequestProgressTracker();
    }

    @Override
    public @NotNull Resource getResource() {
        return this.wrappedRequest.getResource();
    }

    @Override
    public @Nullable ResourceBundle getResourceBundle(final Locale locale) {
        return this.wrappedRequest.getResourceBundle(locale);
    }

    @Override
    public @Nullable ResourceBundle getResourceBundle(final String baseName, final Locale locale) {
        return this.wrappedRequest.getResourceBundle(baseName, locale);
    }

    @Override
    public @NotNull ResourceResolver getResourceResolver() {
        return this.wrappedRequest.getResourceResolver();
    }

    @Override
    public @Nullable String getResponseContentType() {
        return this.wrappedRequest.getResponseContentType();
    }

    @Override
    public @NotNull Enumeration<String> getResponseContentTypes() {
        return this.wrappedRequest.getResponseContentTypes();
    }

    @Override
    public <AdapterType> @Nullable AdapterType adaptTo(@NotNull final Class<AdapterType> type) {
        return this.wrappedRequest.adaptTo(type);
    }
}
