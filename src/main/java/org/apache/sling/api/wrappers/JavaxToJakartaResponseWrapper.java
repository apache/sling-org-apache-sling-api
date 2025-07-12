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

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.felix.http.jakartawrappers.HttpServletResponseWrapper;
import org.apache.felix.http.jakartawrappers.ServletResponseWrapper;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.SlingJakartaHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link SlingHttpServletResponse} to adapt it to the Jacax Servlet API.
 * @since 2.9.0
 */
@SuppressWarnings("deprecation")
public class JavaxToJakartaResponseWrapper extends HttpServletResponseWrapper
        implements SlingJakartaHttpServletResponse {

    /**
     * Create a new wrapper
     * @param response The response object (may be null)
     * @return null if the response parameter is null or the wrapped response object otherwise
     */
    public static @Nullable ServletResponse toJakartaResponse(@Nullable final javax.servlet.ServletResponse response) {
        if (response == null) {
            return null;
        }
        if (response instanceof JakartaToJavaxResponseWrapper r) {
            return r.getResponse();
        }
        if (response instanceof SlingHttpServletResponse r) {
            return new JavaxToJakartaResponseWrapper(r);
        }
        if (response instanceof javax.servlet.http.HttpServletResponse r) {
            return new HttpServletResponseWrapper(r);
        }
        return new ServletResponseWrapper(response);
    }

    /**
     * Create a new wrapper
     * @param response The http response object (may be null)
     * @return null if the response parameter is null or the wrapped response object otherwise
     */
    public static @Nullable HttpServletResponse toJakartaResponse(
            @Nullable final javax.servlet.http.HttpServletResponse response) {
        return (HttpServletResponse) toJakartaResponse((javax.servlet.ServletResponse) response);
    }

    /**
     * Create a new wrapper
     * @param response The sling http response object (may be null)
     * @return null if the response parameter is null or the wrapped response object otherwise
     */
    public static @Nullable SlingJakartaHttpServletResponse toJakartaResponse(
            @Nullable final SlingHttpServletResponse response) {
        return (SlingJakartaHttpServletResponse) toJakartaResponse((javax.servlet.ServletResponse) response);
    }

    private final SlingHttpServletResponse wrappedResponse;

    public JavaxToJakartaResponseWrapper(final SlingHttpServletResponse wrappedResponse) {
        super(wrappedResponse);
        this.wrappedResponse = wrappedResponse;
    }

    @Override
    public <AdapterType> @Nullable AdapterType adaptTo(@NotNull Class<AdapterType> type) {
        return this.wrappedResponse.adaptTo(type);
    }
}
