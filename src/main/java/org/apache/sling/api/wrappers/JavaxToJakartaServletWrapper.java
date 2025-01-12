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

import org.apache.felix.http.javaxwrappers.ServletConfigWrapper;
import org.apache.felix.http.jakartawrappers.ServletExceptionUtil;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.servlets.JakartaOptingServlet;
import org.apache.sling.api.servlets.OptingServlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * This class wraps a servlet based on the Servlet API 3
 * to implement Jakarta Servlet API.
 * @since 2.9.0
 */
@SuppressWarnings("deprecation")
public class JavaxToJakartaServletWrapper implements Servlet {

    /**
     * Create a new wrapper
     * @param servlet The servlet to wrap
     * @return The wrapped servlet
     */
    public static @Nullable Servlet toJakartaServlet(final @Nullable javax.servlet.Servlet servlet) {
        if (servlet != null) {
            if (servlet instanceof OptingServlet) {
                return new JavaxToJakartaOptingServletWrapper((OptingServlet) servlet);
            }
            return new JavaxToJakartaServletWrapper(servlet);
        }
        return null;
    }

    private final javax.servlet.Servlet servlet;

    public JavaxToJakartaServletWrapper(final javax.servlet.Servlet servlet) {
         this.servlet = servlet;
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        try {
            this.servlet.init(new ServletConfigWrapper(config));
        } catch (final javax.servlet.ServletException e) {
            throw ServletExceptionUtil.getServletException(e);
        }
    }

    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        try {
            this.servlet.service(JakartaToJavaxRequestWrapper.toJavaxRequest(req), JakartaToJavaxResponseWrapper.toJavaxResponse(res));
        } catch (final javax.servlet.ServletException e) {
            throw ServletExceptionUtil.getServletException(e);
        }
    }

    @Override
    public void destroy() {
        this.servlet.destroy();
    }

    @Override
    public ServletConfig getServletConfig() {
        return new org.apache.felix.http.jakartawrappers.ServletConfigWrapper(this.servlet.getServletConfig());
    }

    @Override
    public String getServletInfo() {
        return servlet.getServletInfo();
    }

    public static class JavaxToJakartaOptingServletWrapper extends JavaxToJakartaServletWrapper implements JakartaOptingServlet {

        private final OptingServlet servlet;

        public JavaxToJakartaOptingServletWrapper(final OptingServlet servlet) {
            super(servlet);
            this.servlet = servlet;
        }

        @Override
        public boolean accepts(@NotNull SlingJakartaHttpServletRequest request) {
            return this.servlet.accepts(JakartaToJavaxRequestWrapper.toJavaxRequest(request));
        }
    }
}

