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

import java.nio.charset.Charset;

import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.request.builder.impl.RequestParameterImpl;
import org.apache.sling.api.request.builder.impl.RequestProgressTrackerImpl;
import org.apache.sling.api.request.builder.impl.SlingHttpServletRequestBuilderImpl;
import org.apache.sling.api.request.builder.impl.SlingHttpServletResponseBuilderImpl;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

/**
 * Creates builders.
 * @since 1.0 (Sling API Bundle 2.24.0)
 *
 */
public final class Builders {

    private Builders() {}

    /**
     * Creates a new request builder.
     *
     * @param resource the resource on which the request is based
     * @return a request builder
     * @throws IllegalArgumentException If resource is {@code null}
     */
    public static @NotNull SlingHttpServletRequestBuilder newRequestBuilder(@NotNull final Resource resource) {
        return new SlingHttpServletRequestBuilderImpl(resource);
    }

    /**
     * Creates a new response builder.
     *
     * @return a response builder
     */
    public static @NotNull SlingHttpServletResponseBuilder newResponseBuilder() {
        return new SlingHttpServletResponseBuilderImpl();
    }

    /**
     * Creates a new request progress tracker
     *
     * @return a request progress tracker
     * @since 1.1 (Sling API Bundle 2.25.0)
     */
    public static @NotNull RequestProgressTracker newRequestProgressTracker() {
        return new RequestProgressTrackerImpl();
    }

    /**
     * Creates a new {@code application/x-www-form-urlencoded} request parameter with UTF-8 encoding
     *
     * @param name the parameter name
     * @param value the parameter value
     * @return a request parameter
     * @since 1.2 (Sling API Bundle 2.26.2)
     */
    public static @NotNull RequestParameter newRequestParameter(@NotNull String name, @NotNull String value) {
        return new RequestParameterImpl(name, value);
    }

    /**
     * Creates a new {@code application/x-www-form-urlencoded} request parameter with the given encoding
     *
     * @param name the parameter name
     * @param value the parameter value
     * @param encoding the charset of the value
     * @return a request parameter
     * @since 1.2 (Sling API Bundle 2.26.2)
     */
    public static @NotNull RequestParameter newRequestParameter(@NotNull String name, @NotNull String value, @NotNull Charset encoding) {
        return new RequestParameterImpl(name, value, encoding);
    }

    /**
     * Creates a new binary request parameter
     *
     * @param name the parameter name
     * @param value the parameter value
     * @param fileName the file name (may be {@code null})
     * @param contentType the content type (may be {@code null})
     * @return a request parameter
     * @since 1.3 (Sling API Bundle 2.28.0)
     */
    public static @NotNull RequestParameter newRequestParameter(@NotNull String name, byte @NotNull[] value, String fileName, String contentType) {
        return new RequestParameterImpl(name, value, fileName, contentType);
    }
}
