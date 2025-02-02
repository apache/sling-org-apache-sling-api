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

import org.apache.felix.http.jakartawrappers.ServletConfigWrapper;
import org.apache.felix.http.javaxwrappers.ServletExceptionUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.JakartaOptingServlet;
import org.apache.sling.api.servlets.OptingServlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This class wraps a servlet based on the Jakarta Servlet API
 * to implement Servlet API 3.
 * @since 2.9.0
 */
@SuppressWarnings("deprecation")
public class JakartaToJavaxServletWrapper implements Servlet {

    /**
     * Create a new wrapper
     * @param servlet The servlet to wrap
     * @return The wrapped servlet
     */
    public static @Nullable Servlet toJavaxServlet(final @Nullable jakarta.servlet.Servlet servlet) {
        if (servlet != null) {
            if (servlet instanceof JakartaOptingServlet) {
                return new JakartaToJavaxOptingServletWrapper((JakartaOptingServlet) servlet);
            }
            return new JakartaToJavaxServletWrapper(servlet);
        }
        return null;
    }

    private final jakarta.servlet.Servlet servlet;

    public JakartaToJavaxServletWrapper(final jakarta.servlet.Servlet servlet) {
         this.servlet = servlet;
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        try {
            this.servlet.init(new ServletConfigWrapper(config));
        } catch (final jakarta.servlet.ServletException e) {
            throw ServletExceptionUtil.getServletException(e);
        }
    }

    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        try {
            this.servlet.service(JavaxToJakartaRequestWrapper.toJakartaRequest(req), JavaxToJakartaResponseWrapper.toJakartaResponse(res));
        } catch (final jakarta.servlet.ServletException e) {
            throw ServletExceptionUtil.getServletException(e);
        }
    }

    @Override
    public void destroy() {
        this.servlet.destroy();
    }

    @Override
    public ServletConfig getServletConfig() {
        if (this.servlet.getServletConfig() == null) {
            return null;
        }
        return new org.apache.felix.http.javaxwrappers.ServletConfigWrapper(this.servlet.getServletConfig());
    }

    @Override
    public String getServletInfo() {
        return servlet.getServletInfo();
    }

    public static class JakartaToJavaxOptingServletWrapper extends JakartaToJavaxServletWrapper implements OptingServlet {

        private final JakartaOptingServlet servlet;

        public JakartaToJavaxOptingServletWrapper(final JakartaOptingServlet servlet) {
            super(servlet);
            this.servlet = servlet;
        }

        @Override
        public boolean accepts(@NotNull SlingHttpServletRequest request) {
            return this.servlet.accepts(JavaxToJakartaRequestWrapper.toJakartaRequest(request));
        }
    }
}

