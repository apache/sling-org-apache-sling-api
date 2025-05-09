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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.SlingJakartaHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingJakartaBindings;
import org.apache.sling.api.scripting.SlingJakartaScript;
import org.apache.sling.api.scripting.SlingJakartaScriptHelper;
import org.apache.sling.api.scripting.SlingScript;
import org.apache.sling.api.scripting.SlingScriptHelper;

/**
 * Support for wrapping scripting objects
 * @since 2.9.0
 */
public class ScriptingWrapperSupport {

    /**
     * Create a new bindings object from the Sling bindings
     * @param props The Sling bindings
     * @return The Jakarta bindings
     */
    @SuppressWarnings("deprecation")
    public static SlingJakartaBindings toJakartaBindings(final SlingBindings props) {
        if (props == null) {
            return null;
        }
        final SlingJakartaBindings slingBindings = new SlingJakartaBindings();
        props.entrySet().forEach(entry -> slingBindings.put(entry.getKey(), entry.getValue()));
        if (props.getRequest() != null) {
            slingBindings.setRequest(JavaxToJakartaRequestWrapper.toJakartaRequest(props.getRequest()));
        }
        if (props.getResponse() != null) {
            slingBindings.setResponse(JavaxToJakartaResponseWrapper.toJakartaResponse(props.getResponse()));
        }
        if (props.getSling() != null) {
            slingBindings.setSling(new JakartaHelper(props.getSling()));
        }
        return slingBindings;
    }

    /**
     * Create a new bindings object from the Sling Jakarta bindings
     * @param props The Sling Jakarta bindings
     * @return The Sling bindings
     */
    @SuppressWarnings("deprecation")
    public static SlingBindings toJavaxBindings(final SlingJakartaBindings props) {
        if (props == null) {
            return null;
        }
        final SlingBindings slingBindings = new SlingBindings();
        props.entrySet().forEach(entry -> slingBindings.put(entry.getKey(), entry.getValue()));
        if (props.getRequest() != null) {
            slingBindings.setRequest(JakartaToJavaxRequestWrapper.toJavaxRequest(props.getRequest()));
        }
        if (props.getResponse() != null) {
            slingBindings.setResponse(JakartaToJavaxResponseWrapper.toJavaxResponse(props.getResponse()));
        }
        if (props.getSling() != null) {
            slingBindings.setSling(new JavaxHelper(props.getSling()));
        }
        return slingBindings;
    }

    @SuppressWarnings("deprecation")
    private static final class JakartaHelper implements SlingJakartaScriptHelper {
        private final SlingScriptHelper slingScriptHelper;

        public JakartaHelper(final SlingScriptHelper slingScriptHelper) {
            this.slingScriptHelper = slingScriptHelper;
        }

        @Override
        public void dispose() {
            this.slingScriptHelper.dispose();
        }

        @Override
        public void forward(final String path) {
            this.slingScriptHelper.forward(path);
        }

        @Override
        public void forward(final String path, final String requestDispatcherOptions) {
            this.slingScriptHelper.forward(path, requestDispatcherOptions);
        }

        @Override
        public void forward(final String path, final RequestDispatcherOptions options) {
            this.slingScriptHelper.forward(path, options);
        }

        @Override
        public void forward(final Resource resource) {
            this.slingScriptHelper.forward(resource);
        }

        @Override
        public void forward(final Resource resource, final String requestDispatcherOptions) {
            this.slingScriptHelper.forward(resource, requestDispatcherOptions);
        }

        @Override
        public void forward(final Resource resource, final RequestDispatcherOptions options) {
            this.slingScriptHelper.forward(resource, options);
        }

        @Override
        public SlingJakartaHttpServletRequest getRequest() {
            return JavaxToJakartaRequestWrapper.toJakartaRequest(slingScriptHelper.getRequest());
        }

        @Override
        public SlingJakartaHttpServletResponse getResponse() {
            return JavaxToJakartaResponseWrapper.toJakartaResponse(slingScriptHelper.getResponse());
        }

        @Override
        public SlingJakartaScript getScript() {
            final SlingScript s = this.slingScriptHelper.getScript();
            return new SlingJakartaScript() {

                @Override
                public Object call(SlingJakartaBindings props, String method, Object... args) {
                    return s.call(toJavaxBindings(props), method, args);
                }

                @Override
                public Object eval(SlingJakartaBindings props) {
                    return s.eval(toJavaxBindings(props));
                }

                @Override
                public Resource getScriptResource() {
                    return s.getScriptResource();
                }
            };
        }

        @Override
        public <ServiceType> ServiceType getService(final Class<ServiceType> serviceType) {
            return slingScriptHelper.getService(serviceType);
        }

        @Override
        public <ServiceType> ServiceType[] getServices(final Class<ServiceType> serviceType, final String filter) {
            return slingScriptHelper.getServices(serviceType, filter);
        }

        @Override
        public void include(final String path) {
            slingScriptHelper.include(path);
        }

        @Override
        public void include(final String path, final String requestDispatcherOptions) {
            slingScriptHelper.include(path, requestDispatcherOptions);
        }

        @Override
        public void include(final String path, final RequestDispatcherOptions options) {
            slingScriptHelper.include(path, options);
        }

        @Override
        public void include(final Resource resource) {
            slingScriptHelper.include(resource);
        }

        @Override
        public void include(final Resource resource, final String requestDispatcherOptions) {
            slingScriptHelper.include(resource, requestDispatcherOptions);
        }

        @Override
        public void include(final Resource resource, final RequestDispatcherOptions options) {
            slingScriptHelper.include(resource, options);
        }
    }

    @Deprecated
    private static final class JavaxHelper implements SlingScriptHelper {
        private final SlingJakartaScriptHelper slingScriptHelper;

        public JavaxHelper(final SlingJakartaScriptHelper slingScriptHelper) {
            this.slingScriptHelper = slingScriptHelper;
        }

        @Override
        public void dispose() {
            this.slingScriptHelper.dispose();
        }

        @Override
        public void forward(final String path) {
            this.slingScriptHelper.forward(path);
        }

        @Override
        public void forward(final String path, final String requestDispatcherOptions) {
            this.slingScriptHelper.forward(path, requestDispatcherOptions);
        }

        @Override
        public void forward(final String path, final RequestDispatcherOptions options) {
            this.slingScriptHelper.forward(path, options);
        }

        @Override
        public void forward(final Resource resource) {
            this.slingScriptHelper.forward(resource);
        }

        @Override
        public void forward(final Resource resource, final String requestDispatcherOptions) {
            this.slingScriptHelper.forward(resource, requestDispatcherOptions);
        }

        @Override
        public void forward(final Resource resource, final RequestDispatcherOptions options) {
            this.slingScriptHelper.forward(resource, options);
        }

        @Override
        public SlingHttpServletRequest getRequest() {
            return JakartaToJavaxRequestWrapper.toJavaxRequest(slingScriptHelper.getRequest());
        }

        @Override
        public SlingHttpServletResponse getResponse() {
            return JakartaToJavaxResponseWrapper.toJavaxResponse(slingScriptHelper.getResponse());
        }

        @Override
        public SlingScript getScript() {
            final SlingJakartaScript s = this.slingScriptHelper.getScript();
            return new SlingScript() {

                @Override
                public Object call(SlingBindings props, String method, Object... args) {
                    return s.call(toJakartaBindings(props), method, args);
                }

                @Override
                public Object eval(SlingBindings props) {
                    return s.eval(toJakartaBindings(props));
                }

                @Override
                public Resource getScriptResource() {
                    return s.getScriptResource();
                }
            };
        }

        @Override
        public <ServiceType> ServiceType getService(final Class<ServiceType> serviceType) {
            return slingScriptHelper.getService(serviceType);
        }

        @Override
        public <ServiceType> ServiceType[] getServices(final Class<ServiceType> serviceType, final String filter) {
            return slingScriptHelper.getServices(serviceType, filter);
        }

        @Override
        public void include(final String path) {
            slingScriptHelper.include(path);
        }

        @Override
        public void include(final String path, final String requestDispatcherOptions) {
            slingScriptHelper.include(path, requestDispatcherOptions);
        }

        @Override
        public void include(final String path, final RequestDispatcherOptions options) {
            slingScriptHelper.include(path, options);
        }

        @Override
        public void include(final Resource resource) {
            slingScriptHelper.include(resource);
        }

        @Override
        public void include(final Resource resource, final String requestDispatcherOptions) {
            slingScriptHelper.include(resource, requestDispatcherOptions);
        }

        @Override
        public void include(final Resource resource, final RequestDispatcherOptions options) {
            slingScriptHelper.include(resource, options);
        }
    }
}
