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

import java.io.IOException;

import org.apache.felix.http.jakartawrappers.FilterConfigWrapper;
import org.apache.felix.http.javaxwrappers.ServletExceptionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This class wraps a filter based on the Jakarta Servlet API
 * to implement Servlet API 3.
 * @since 2.9.0
 */
public class JakartaToJavaxFilterWrapper implements Filter {

    /**
     * Create a wrapper
     * @param filter The filter to wrap
     * @return The wrapped filter
     */
    public static @Nullable Filter toJavaxFilter(final @Nullable jakarta.servlet.Filter filter) {
        if (filter != null) {
            return new JakartaToJavaxFilterWrapper(filter);
        }
        return null;
    }

    private final jakarta.servlet.Filter filter;

    public JakartaToJavaxFilterWrapper(final jakarta.servlet.Filter filter) {
         this.filter = filter;
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {
        try {
            this.filter.init(new FilterConfigWrapper(config));
        } catch (final jakarta.servlet.ServletException e) {
            throw ServletExceptionUtil.getServletException(e);
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        try {
            this.filter.doFilter(JavaxToJakartaRequestWrapper.toJakartaRequest(request),
                JavaxToJakartaResponseWrapper.toJakartaResponse(response),
                new FilterChainWrapper(chain));
        } catch (final jakarta.servlet.ServletException e) {
            throw ServletExceptionUtil.getServletException(e);
        }
    }

    @Override
    public void destroy() {
        this.filter.destroy();
    }

    public static class FilterChainWrapper implements jakarta.servlet.FilterChain{

        private final FilterChain filterChain;

        public FilterChainWrapper(@NotNull final FilterChain chain) {
            this.filterChain = chain;
        }

        @Override
        public void doFilter(final jakarta.servlet.ServletRequest request, final jakarta.servlet.ServletResponse response)
                throws IOException, jakarta.servlet.ServletException {
            try {
                this.filterChain.doFilter(JakartaToJavaxRequestWrapper.toJavaxRequest(request), JakartaToJavaxResponseWrapper.toJavaxResponse(response));
            } catch (final javax.servlet.ServletException e) {
                throw org.apache.felix.http.jakartawrappers.ServletExceptionUtil.getServletException(e);
            }
        }
    }
}

