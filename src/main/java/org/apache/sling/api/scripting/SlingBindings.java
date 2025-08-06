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
package org.apache.sling.api.scripting;

import java.io.PrintWriter;
import java.io.Reader;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.SlingJakartaHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.wrappers.JakartaToJavaxRequestWrapper;
import org.apache.sling.api.wrappers.JakartaToJavaxResponseWrapper;
import org.apache.sling.api.wrappers.JavaxToJakartaRequestWrapper;
import org.apache.sling.api.wrappers.JavaxToJakartaResponseWrapper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * The <code>SlingBindings</code> class is used to prepare global variables
 * for script execution. The constants in this class define names of variables
 * which <em>MUST</em> or <em>MAY</em> be provided for the script execution.
 * Other variables may be define as callers see fit.
 */
public class SlingBindings extends LazyBindings {

    private static final long serialVersionUID = 209505693646323450L;

    /**
     * The name of the global scripting variable providing the
     * {@link org.apache.sling.api.SlingJakartaHttpServletRequest} object (value is
     * "request"). The value of the scripting variable is the same as that
     * returned by the
     * {@link org.apache.sling.api.scripting.SlingScriptHelper#getJakartaRequest()}
     * method.
     * <p>
     * This bound variable is required in the bindings given the script.
     * @since 2.6.0
     */
    public static final String JAKARTA_REQUEST = "jakartaRequest";

    /**
     * The name of the global scripting variable providing the
     * {@link org.apache.sling.api.SlingJakartaHttpServletResponse} object (value is
     * "response"). The value of the scripting variable is the same as that
     * returned by the
     * {@link org.apache.sling.api.scripting.SlingScriptHelper#getJakartaResponse()}
     * method.
     * <p>
     * This bound variable is required in the bindings given the script.
     * @since 2.6.0
     */
    public static final String JAKARTA_RESPONSE = "jakartaResponse";

    /**
     * The name of the global scripting variable providing the
     * {@link org.apache.sling.api.SlingHttpServletRequest} object (value is
     * "request"). The value of the scripting variable is the same as that
     * returned by the
     * {@link org.apache.sling.api.scripting.SlingScriptHelper#getRequest()}
     * method.
     * <p>
     * This bound variable is required in the bindings given the script.
     * @deprecated Use {@link #JAKARTA_REQUEST} instead.
     */
    @Deprecated
    public static final String REQUEST = "request";

    /**
     * The name of the global scripting variable providing the
     * {@link org.apache.sling.api.SlingHttpServletResponse} object (value is
     * "response"). The value of the scripting variable is the same as that
     * returned by the
     * {@link org.apache.sling.api.scripting.SlingScriptHelper#getResponse()}
     * method.
     * <p>
     * This bound variable is required in the bindings given the script.
     * @deprecated Use {@link #JAKARTA_RESPONSE} instead.
     */
    @Deprecated
    public static final String RESPONSE = "response";

    /**
     * The name of the global scripting variable providing the
     * {@link java.io.Reader} object (value is "reader").
     * <p>
     * This bound variable is required in the bindings given the script.
     */
    public static final String READER = "reader";

    /**
     * The name of the global scripting variable providing the
     * {@link org.apache.sling.api.scripting.SlingScriptHelper} for the request
     * (value is "sling").
     * <p>
     * This bound variable is optional. If existing, the script helper instance
     * must be bound to the same request and response objects as bound with the
     * {@link #REQUEST} and {@link #RESPONSE} variables. If this variable is not
     * bound, the script implementation will create it before actually
     * evaluating the script.
     */
    public static final String SLING = "sling";

    /**
     * The name of the global scripting variable providing the
     * {@link org.apache.sling.api.resource.Resource} object (value is
     * "resource"). The value of the scripting variable is the same as that
     * returned by the <code>SlingScriptHelper.getRequest().getResource()</code>
     * method.
     * <p>
     * This bound variable is optional. If existing, the resource must be bound
     * to the same resource as returned by the
     * <code>SlingHttpServletRequest.getResource()</code> method. If this
     * variable is not bound, the script implementation will bind it before
     * actually evaluating the script.
     */
    public static final String RESOURCE = "resource";

    /**
     * <p>
     * The name of the global scripting variable providing the
     * {@link org.apache.sling.api.resource.ResourceResolver} object (value is
     * "resolver"). The value of the scripting variable is the same as that
     * returned by the {@code SlingScriptHelper.getRequest().getResourceResolver()}
     * method.
     * </p>
     * <p>
     * This bound variable is optional. If existing, the resource resolver must be
     * bound to the same resolver as returned by the {@code
     * SlingHttpServletRequest.getResource().getResourceResolver} method. If this
     * variable is not bound, the script implementation will bind it before actually
     * evaluating the script.
     * </p>
     */
    public static final String RESOLVER = "resolver";

    /**
     * The name of the global scripting variable providing the
     * <code>java.io.PrintWriter</code> object to return the response content
     * (value is "out"). The value of the scripting variable is the same as that
     * returned by the <code>SlingScriptHelper.getResponse().getWriter()</code>
     * method.
     * <p>
     * Note, that it may be advisable to implement a lazy acquiring writer for
     * the <em>out</em> variable to enable the script to write binary data to
     * the response output stream instead of the writer.
     * <p>
     * This bound variable is optional. If existing, the resource must be bound
     * to the same writer as returned by the
     * <code>SlingHttpServletResponse.getWriter()</code> method of the
     * response object bound to the {@link #RESPONSE} variable. If this variable
     * is not bound, the script implementation will bind it before actually
     * evaluating the script.
     */
    public static final String OUT = "out";

    /**
     * The name of the global scripting variable indicating whether the output
     * used by the script should be flushed after the script evaluation ended
     * normally (value is "flush").
     * <p>
     * The type of this variable is <code>java.lang.Boolean</code> indicating
     * whether to flush the output (value is <code>TRUE</code>) or not (value
     * is <code>FALSE</code>). If the variable has a non-<code>null</code>
     * value of another type, the output is not flush as if the value would be
     * <code>FALSE</code>.
     */
    public static final String FLUSH = "flush";

    /**
     * The name of the global scripting variable providing a logger which may be
     * used for logging purposes (value is "log"). The logger provides the API
     * defined by the SLF4J <code>org.slf4j.Logger</code> interface.
     * <p>
     * This bound variable is optional. If this variable is not bound, the
     * script implementation will bind it before actually evaluating the script.
     */
    public static final String LOG = "log";

    /**
     * Helper method to get an object with a given type from this map.
     * @param key The key for the object
     * @param <ObjectType> The object type
     * @param type The object type
     * @return The searched object if it has the specified type, otherwise <code>null</code> is returned.
     */
    @SuppressWarnings("unchecked")
    protected <ObjectType> ObjectType get(final String key, final Class<ObjectType> type) {
        final Object o = this.get(key);
        if (type.isInstance(o)) {
            return (ObjectType) o;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object put(final String key, final Object value) {
        final Object result = super.put(key, value);
        // also put the alternate wrapper if it is not already wrapping the same value
        if (REQUEST.equals(key)) {
            if (shouldWrapJavaxRequest(value)) {
                super.put(JAKARTA_REQUEST, JavaxToJakartaRequestWrapper.toJakartaRequest(getRequest()));
            }
        } else if (JAKARTA_REQUEST.equals(key)) {
            if (shouldWrapJakartaRequest(value)) {
                super.put(REQUEST, JakartaToJavaxRequestWrapper.toJavaxRequest(getJakartaRequest()));
            }
        } else if (RESPONSE.equals(key)) {
            if (shouldWrapJavaxResponse(value)) {
                super.put(JAKARTA_RESPONSE, JavaxToJakartaResponseWrapper.toJakartaResponse(getResponse()));
            }
        } else if (JAKARTA_RESPONSE.equals(key) && shouldWrapJakartaResponse(value)) {
            super.put(RESPONSE, JakartaToJavaxResponseWrapper.toJavaxResponse(getJakartaResponse()));
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    private boolean shouldWrapJavaxRequest(final Object value) {
        return value instanceof SlingHttpServletRequest
                && !(getJakartaRequest() instanceof JavaxToJakartaRequestWrapper rw && rw.getRequest() == value);
    }

    @SuppressWarnings("deprecation")
    private boolean shouldWrapJakartaRequest(final Object value) {
        return value instanceof SlingJakartaHttpServletRequest
                && !(getRequest() instanceof JakartaToJavaxRequestWrapper rw && rw.getRequest() == value);
    }

    @SuppressWarnings("deprecation")
    private boolean shouldWrapJavaxResponse(final Object value) {
        return value instanceof SlingHttpServletResponse
                && !(getJakartaResponse() instanceof JavaxToJakartaResponseWrapper rw && rw.getResponse() == value);
    }

    @SuppressWarnings("deprecation")
    private boolean shouldWrapJakartaResponse(final Object value) {
        return value instanceof SlingJakartaHttpServletResponse
                && !(getResponse() instanceof JakartaToJavaxResponseWrapper rw && rw.getResponse() == value);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object remove(final Object key) {
        if (REQUEST.equals(key)) {
            super.remove(JAKARTA_REQUEST);
        } else if (JAKARTA_REQUEST.equals(key)) {
            super.remove(REQUEST);
        } else if (RESPONSE.equals(key)) {
            super.remove(JAKARTA_RESPONSE);
        } else if (JAKARTA_RESPONSE.equals(key)) {
            super.remove(RESPONSE);
        }
        return super.remove(key);
    }

    /**
     * Helper method which invokes {@link #put(Object, Object)} only if the value is not null.
     * @param key The key of the object
     * @param value The value
     */
    protected void safePut(final String key, final Object value) {
        if (value != null) {
            this.put(key, value);
        }
    }

    /**
     * Sets the {@link #FLUSH} property to <code>flush</code>.
     * @param flush Whether to flush or not
     */
    public void setFlush(boolean flush) {
        put(FLUSH, flush);
    }

    /**
     * Returns the {@link #FLUSH} property if not <code>null</code> and a
     * <code>boolean</code>. Otherwise <code>false</code> is returned.
     * @return {@code true} if flush
     */
    public boolean getFlush() {
        Boolean value = this.get(FLUSH, Boolean.class);
        if (value != null) {
            return value;
        }

        return false;
    }

    /**
     * Sets the {@link #LOG} property to <code>log</code> if not
     * <code>null</code>.
     * @param log The logger
     */
    public void setLog(Logger log) {
        this.safePut(LOG, log);
    }

    /**
     * Returns the {@link #LOG} property if not <code>null</code> and a
     * <code>org.slf4j.Logger</code> instance. Otherwise <code>null</code>
     * is returned.
     * @return The logger or {@code null}
     */
    public @Nullable Logger getLog() {
        return this.get(LOG, Logger.class);
    }

    /**
     * Sets the {@link #OUT} property to <code>out</code> if not
     * <code>null</code>.
     * @param out The print writer
     */
    public void setOut(PrintWriter out) {
        this.safePut(OUT, out);
    }

    /**
     * Returns the {@link #OUT} property if not <code>null</code> and a
     * <code>PrintWriter</code> instance. Otherwise <code>null</code> is
     * returned.
     * @return The print writer or {@code null}
     */
    public @Nullable PrintWriter getOut() {
        return this.get(OUT, PrintWriter.class);
    }

    /**
     * Sets the {@link #JAKARTA_REQUEST} property to <code>request</code> if not
     * <code>null</code>.
     * @param request The request object.
     * @since 2.6.0
     */
    public void setJakartaRequest(SlingJakartaHttpServletRequest request) {
        this.safePut(JAKARTA_REQUEST, request);
    }

    /**
     * Returns the {@link #JAKARTA_REQUEST} property if not <code>null</code> and a
     * <code>SlingJakartaHttpServletRequest</code> instance. Otherwise
     * <code>null</code> is returned.
     * @return The request object or {@code null}
     * @since 2.6.0
     */
    public @Nullable SlingJakartaHttpServletRequest getJakartaRequest() {
        return this.get(JAKARTA_REQUEST, SlingJakartaHttpServletRequest.class);
    }

    /**
     * Sets the {@link #REQUEST} property to <code>request</code> if not
     * <code>null</code>.
     * @param request The request object.
     * @deprecated Use {@link #setJakartaRequest(SlingJakartaHttpServletRequest)} instead.
     */
    @Deprecated
    public void setRequest(SlingHttpServletRequest request) {
        this.safePut(REQUEST, request);
    }

    /**
     * Returns the {@link #REQUEST} property if not <code>null</code> and a
     * <code>SlingHttpServletRequest</code> instance. Otherwise
     * <code>null</code> is returned.
     * @return The request object or {@code null}
     * @deprecated Use {@link #getJakartaRequest()} instead.
     */
    @Deprecated
    public @Nullable SlingHttpServletRequest getRequest() {
        return this.get(REQUEST, SlingHttpServletRequest.class);
    }

    /**
     * Sets the {@link #READER} property to <code>reader</code> if not
     * <code>null</code>.
     * @param reader The reader
     */
    public void setReader(Reader reader) {
        this.safePut(READER, reader);
    }

    /**
     * Returns the {@link #READER} property if not <code>null</code> and a
     * <code>Reader</code> instance. Otherwise <code>null</code> is
     * returned.
     * @return The reader or {@code null}.
     */
    public @Nullable Reader getReader() {
        return this.get(READER, Reader.class);
    }

    /**
     * Sets the {@link #RESOURCE} property to <code>resource</code> if not
     * <code>null</code>.
     * @param resource The resource
     */
    public void setResource(Resource resource) {
        this.safePut(RESOURCE, resource);
    }

    /**
     * Returns the {@link #RESOURCE} property if not <code>null</code> and a
     * <code>Resource</code> instance. Otherwise <code>null</code> is
     * returned.
     * @return The resource or {@code null}.
     */
    public @Nullable Resource getResource() {
        return this.get(RESOURCE, Resource.class);
    }

    /**
     * Sets the {@link #RESOLVER} property to the provided {@code resourceResolver} if not {@code null}.
     * @param resourceResolver the Resource Resolver
     */
    public void setResourceResolver(ResourceResolver resourceResolver) {
        this.safePut(RESOLVER, resourceResolver);
    }

    /**
     * Returns the {@link #RESOLVER} property if not <code>null</code> and a
     * <code>ResourceResolver</code> instance. Otherwise <code>null</code> is
     * returned.
     * @return the bound {@link ResourceResolver} if one exists, <code>null</code> otherwise
     */
    public @Nullable ResourceResolver getResourceResolver() {
        return this.get(RESOLVER, ResourceResolver.class);
    }

    /**
     * Sets the {@link #JAKARTA_RESPONSE} property to <code>response</code> if not
     * <code>null</code>.
     * @param response The response
     * @since 2.6.0
     */
    public void setJakartaResponse(SlingJakartaHttpServletResponse response) {
        this.safePut(JAKARTA_RESPONSE, response);
    }

    /**
     * Returns the {@link #JAKARTA_RESPONSE} property if not <code>null</code> and a
     * <code>SlingJakartaHttpServletResponse</code> instance. Otherwise
     * <code>null</code> is returned.
     * @return The response or {@code null}.
     * @since 2.6.0
     */
    public @Nullable SlingJakartaHttpServletResponse getJakartaResponse() {
        return this.get(JAKARTA_RESPONSE, SlingJakartaHttpServletResponse.class);
    }

    /**
     * Sets the {@link #RESPONSE} property to <code>response</code> if not
     * <code>null</code>.
     * @param response The response
     * @deprecated Use {@link #setJakartaResponse(SlingJakartaHttpServletResponse)} instead.
     */
    @Deprecated
    public void setResponse(SlingHttpServletResponse response) {
        this.safePut(RESPONSE, response);
    }

    /**
     * Returns the {@link #RESPONSE} property if not <code>null</code> and a
     * <code>SlingHttpServletResponse</code> instance. Otherwise
     * <code>null</code> is returned.
     * @return The response or {@code null}.
     * @deprecated Use {@link #getJakartaResponse()} instead.
     */
    @Deprecated
    public @Nullable SlingHttpServletResponse getResponse() {
        return this.get(RESPONSE, SlingHttpServletResponse.class);
    }

    /**
     * Sets the {@link #SLING} property to <code>sling</code> if not
     * <code>null</code>.
     * @param sling The script helper
     */
    public void setSling(SlingScriptHelper sling) {
        this.safePut(SLING, sling);
    }

    /**
     * Returns the {@link #SLING} property if not <code>null</code> and a
     * <code>SlingScriptHelper</code> instance. Otherwise <code>null</code>
     * is returned.
     * @return The script helper or {@code null}.
     */
    public @Nullable SlingScriptHelper getSling() {
        return this.get(SLING, SlingScriptHelper.class);
    }
}
