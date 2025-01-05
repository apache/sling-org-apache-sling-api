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

import org.apache.felix.http.javaxwrappers.FilterConfigWrapper;
import org.apache.felix.http.jakartawrappers.ServletExceptionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class JavaxToJakartaFilterWrapper implements Filter {

    public static @Nullable Filter toJakartaFilter(final @Nullable javax.servlet.Filter filter) {
        if (filter != null) {
            return new JavaxToJakartaFilterWrapper(filter);
        }
        return null;
    }

    private final javax.servlet.Filter filter;

    public JavaxToJakartaFilterWrapper(final javax.servlet.Filter filter) {
         this.filter = filter;
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {
        try {
            this.filter.init(new FilterConfigWrapper(config));
        } catch (final javax.servlet.ServletException e) {
            throw ServletExceptionUtil.getServletException(e);
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        try {
            this.filter.doFilter(JakartaToJavaxRequestWrapper.toJavaxRequest(request),
                JakartaToJavaxResponseWrapper.toJavaxResponse(response),
                new FilterChainWrapper(chain));
        } catch (final javax.servlet.ServletException e) {
            throw ServletExceptionUtil.getServletException(e);
        }
    }

    @Override
    public void destroy() {
        this.filter.destroy();
    }

    public static class FilterChainWrapper implements javax.servlet.FilterChain{

        private final FilterChain filterChain;

        public FilterChainWrapper(@NotNull final FilterChain chain) {
            this.filterChain = chain;
        }

        @Override
        public void doFilter(final javax.servlet.ServletRequest request, final javax.servlet.ServletResponse response)
                throws IOException, javax.servlet.ServletException {
            try {
                this.filterChain.doFilter(JavaxToJakartaRequestWrapper.toJakartaRequest(request), JavaxToJakartaResponseWrapper.toJakartaResponse(response));
            } catch (final jakarta.servlet.ServletException e) {
                throw org.apache.felix.http.javaxwrappers.ServletExceptionUtil.getServletException(e);
            }
        }
    }
}

