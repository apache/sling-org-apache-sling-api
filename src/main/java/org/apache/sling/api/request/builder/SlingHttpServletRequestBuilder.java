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
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/** 
 * Fluent helper for building a request.
 * 
 * Instances of this interface are not thread-safe.
 */
@ProviderType
public interface SlingHttpServletRequestBuilder {

    /** 
     * Set the HTTP request method to use - defaults to GET
     * @param method The HTTP method
     * @return this object
     * @throws IllegalArgumentException If method is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder withRequestMethod(@NotNull String method);

    /** 
     * Set the HTTP request's Content-Type
     * @param content type
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withContentType(String contentType);

    /** 
     * Use the supplied content as the request's body content
     * @param content the content
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withBody(String content);

    /**
     * Sets the optional selectors of the internal request, which influence
     * the Servlet/Script resolution.
     * @param selectors The selectors
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withSelectors(String ... selectors);

    /** 
     * Sets the optional extension of the internal request, which influences
     * the Servlet/Script resolution.
     * @param extension The extension
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withExtension(String extension);

    /**
     * Sets the optional suffix of the internal request.
     * @param suffix The suffix
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withSuffix(String suffix);

    /** 
     * Set a request parameter
     * @param key The name of the parameter
     * @param value The value of the parameter
     * @return this object
     * @throws IllegalArgumentException If key or value is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder withParameter(@NotNull String key, @NotNull String value);

    /** 
     * Set a request parameter
     * @param key The name of the parameter
     * @param values The values of the parameter
     * @return this object
     * @throws IllegalArgumentException If key or values is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder withParameter(@NotNull String key, @NotNull String[] values);

    /** 
     * Add the supplied request parameters to the current ones.
     * @param parameters Additional parameters
     * @return this object
     */
    @NotNull SlingHttpServletRequestBuilder withParameters(Map<String, String[]> parameters);

    /** 
     * Use the request dispatcher from the provided request.
     * @param request The request
     * @return this object
     * @throws IllegalArgumentException If request is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder useRequestDispatcherFrom(@NotNull SlingHttpServletRequest request);

    /** 
     * If a session is used, use the session from the provided request
     * @param request The request
     * @return this object
     * @throws IllegalArgumentException If request is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder useSessionFrom(@NotNull HttpServletRequest request);

    /** 
     * Use the attributes backed by the provided request
     * @param request The request
     * @return this object
     * @throws IllegalArgumentException If request is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder useAttributesFrom(@NotNull HttpServletRequest request);

    /** 
     * Use the servlet context from the provided request
     * @param request The request
     * @return this object
     * @throws IllegalArgumentException If request is {@code null}
     */
    @NotNull SlingHttpServletRequestBuilder useServletContextFrom(@NotNull HttpServletRequest request);

    /**
     * Build the request.
     * Once this method has been called, the builder must not be used anymore. A new builder
     * needs to be created, to create a new request.
     * @return A request object
     */
    @NotNull SlingHttpServletRequest build();
}
