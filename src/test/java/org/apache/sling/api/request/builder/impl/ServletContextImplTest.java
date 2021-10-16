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
package org.apache.sling.api.request.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.EventListener;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.junit.Test;

public class ServletContextImplTest {

    @Test public void testContext() throws ServletException {
        final ServletContextImpl context = new ServletContextImpl();
        // the context is very useless, it throws an exception for most methods
        assertEquals("application/octet-stream", context.getMimeType("file"));
        try {
            context.getAttribute("name");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getAttributeNames();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getContext("path");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getContextPath();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getInitParameter("name");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getInitParameterNames();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getMajorVersion();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getMinorVersion();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getNamedDispatcher("name");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getRealPath("path");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getRequestDispatcher("path");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getResource("path");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getResourceAsStream("path");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getResourcePaths("path");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getServerInfo();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getServlet("path");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getServletContextName();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getServletNames();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getServlets();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.log("msg");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.log("msg", new Exception());
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.log(new Exception(), "msg");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.removeAttribute("name");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.setAttribute("name", "value");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getEffectiveMajorVersion();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getEffectiveMinorVersion();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.setInitParameter("name", "value");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.addServlet("name", "classname");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.addServlet("name", new HttpServlet(){
                
            });
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.addServlet("name", HttpServlet.class);
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.addFilter("name", "classname");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.addFilter("name", new Filter(){

                @Override
                public void init(FilterConfig filterConfig) throws ServletException {
                }

                @Override
                public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                        throws IOException, ServletException {
                }

                @Override
                public void destroy() {
                }
                
            });
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.addFilter("name", Filter.class);
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.createServlet(HttpServlet.class);
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getServletRegistration("name");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getServletRegistrations();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.createFilter(Filter.class);
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getFilterRegistration("name");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getFilterRegistrations();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getSessionCookieConfig();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.setSessionTrackingModes(null);
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getDefaultSessionTrackingModes();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getEffectiveSessionTrackingModes();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.addListener(new EventListener(){
                
            });
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.addListener(EventListener.class);
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.createListener(EventListener.class);
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getJspConfigDescriptor();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getClassLoader();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.declareRoles("a");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            context.getVirtualServerName();
            fail();
        } catch ( final UnsupportedOperationException expected) {}
    }
}
