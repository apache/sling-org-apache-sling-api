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
package org.apache.sling.api.request.builder;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestProgressTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

/** 
 * <p>Fluent helper for building a request.</p>
 * <p><strong>Note:</strong> instances of this interface are not thread-safe.</p>
 * @since 1.0 (Sling API Bundle 2.24.0)
 */
@ProviderType
public interface SlingHttpServletRequestBuilder {

    /** 
     * Sets the HTTP request method to use - defaults to {@code GET}.
     * @param method the HTTP method
     * @return this object
     * @throws IllegalArgumentException If method is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder withRequestMethod(@NotNull String method);

    /** 
     * Sets the HTTP request's {@code Content-Type} header.
     * @param contentType the {@code Content-Type} value
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withContentType(@Nullable String contentType);

    /** 
     * Uses the supplied content as the request's body content.
     * @param content the request body content
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withBody(@Nullable String content);

    /**
     * Sets the optional selectors of the internal request, which influence the servlet/script resolution.
     * @param selectors the selectors
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withSelectors(@Nullable String ... selectors);

    /** 
     * Sets the optional extension of the internal request, which influences the servlet/script resolution.
     * @param extension the extension
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withExtension(@Nullable String extension);

    /**
     * Sets the optional suffix of the internal request.
     * @param suffix the suffix
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withSuffix(@Nullable String suffix);

    /** 
     * Sets a request parameter.
     * @param key the name of the parameter
     * @param value the value of the parameter
     * @return this object
     * @throws IllegalArgumentException if either {@code key} or {@code value} is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder withParameter(@NotNull String key, @NotNull String value);

    /** 
     * Sets a request parameter.
     * @param key the name of the parameter
     * @param values the values of the parameter
     * @return this object
     * @throws IllegalArgumentException if either {@code key} or {@code values} is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder withParameter(@NotNull String key, @NotNull String[] values);

    /** 
     * Adds the supplied request parameters to the current ones.
     * @param parameters additional parameters
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withParameters(@Nullable Map<String, String[]> parameters);

    /** 
     * Uses the request dispatcher from the provided request.
     * @param request the request from which to use the dispatcher
     * @return this object
     * @throws IllegalArgumentException if {@code request} is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder useRequestDispatcherFrom(@NotNull SlingHttpServletRequest request);

    /** 
     * Uses the session from the provided request.
     * @param request the request from which to use the session
     * @return this object
     * @throws IllegalArgumentException if {@code request} is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder useSessionFrom(@NotNull HttpServletRequest request);

    /** 
     * Uses the attributes backed by the provided request.
     * @param request the request from which to use the attributes
     * @return this object
     * @throws IllegalArgumentException if {@code request} is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder useAttributesFrom(@NotNull HttpServletRequest request);

    /** 
     * Uses the servlet context from the provided request.
     * @param request the request from which to use the servlet context
     * @return this object
     * @throws IllegalArgumentException if {@code request} is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder useServletContextFrom(@NotNull HttpServletRequest request);

    /**
     * Uses the provided request progress tracker
     * @param tracker The tracker
     * @return this object
     * @since 1.1 (Sling API Bundle 2.25.0)
     */
    @NotNull SlingHttpServletRequestBuilder withRequestProgressTracker(@NotNull RequestProgressTracker tracker);

    /**
     * Builds the request. Once this method has been called, the builder must not be used anymore. In order to create a new request a new
     * builder has to be used.
     * @return a request object
     */
    @NotNull SlingHttpServletRequest build();
}
