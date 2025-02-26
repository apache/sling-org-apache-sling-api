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
import org.apache.felix.http.javaxwrappers.HttpServletResponseWrapper;
import org.apache.felix.http.javaxwrappers.ServletResponseWrapper;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.SlingJakartaHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link SlingJakartaHttpServletResponse} to adapt it to the Jacax Servlet API.
 * @since 2.9.0
 */
@SuppressWarnings("deprecation")
public class JakartaToJavaxResponseWrapper extends HttpServletResponseWrapper implements SlingHttpServletResponse {

    /**
     * Create a new wrapper
     * @param Response Jakarta Servlet API based response object
     * @return The wrapped response
     */
    public static javax.servlet.ServletResponse toJavaxResponse(final ServletResponse Response) {
        if (Response instanceof JavaxToJakartaResponseWrapper) {
            return ((JavaxToJakartaResponseWrapper) Response).getResponse();
        }
        if (Response instanceof SlingJakartaHttpServletResponse) {
            return new JakartaToJavaxResponseWrapper((SlingJakartaHttpServletResponse) Response);
        }
        if (Response instanceof HttpServletResponse) {
            return new HttpServletResponseWrapper((HttpServletResponse) Response);
        }
        return new ServletResponseWrapper(Response);
    }

    public static javax.servlet.http.HttpServletResponse toJavaxResponse(final HttpServletResponse Response) {
        return (javax.servlet.http.HttpServletResponse) toJavaxResponse((ServletResponse) Response);
    }

    public static SlingHttpServletResponse toJavaxResponse(final SlingJakartaHttpServletResponse Response) {
        return (SlingHttpServletResponse) toJavaxResponse((ServletResponse) Response);
    }

    private final SlingJakartaHttpServletResponse wrappedResponse;

    public JakartaToJavaxResponseWrapper(final SlingJakartaHttpServletResponse wrappedResponse) {
        super(wrappedResponse);
        this.wrappedResponse = wrappedResponse;
    }

    @Override
    public <AdapterType> @Nullable AdapterType adaptTo(@NotNull Class<AdapterType> type) {
        return this.wrappedResponse.adaptTo(type);
    }
}
