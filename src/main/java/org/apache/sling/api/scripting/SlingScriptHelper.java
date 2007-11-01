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

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;

/**
 * The <code>SlingScriptHelper</code> interface defines the API of a helper
 * class which is provided to the scripts called from sling through the global
 * <code>{@link SlingScriptEngine#SLING sling}</code> variable.
 */
public interface SlingScriptHelper {

    /**
     * Returns the {@link SlingHttpServletRequest} representing the input of the
     * request.
     */
    SlingHttpServletRequest getRequest();

    /**
     * Returns the {@link SlingHttpServletResponse} representing the output of
     * the request.
     */
    SlingHttpServletResponse getResponse();

    /**
     * Returns the {@link SlingScript} being called to handle the request. This
     * is the same instance as given to the {@link SlingScriptEngine} for
     * evaluation.
     */
    SlingScript getScript();

    /**
     * Helper method to include the result of processing the request for the
     * given <code>path</code>. This method is intended to be implemented as
     * follows:
     *
     * <pre>
     * RequestDispatcher dispatcher = getRequest().getRequestDispatcher(path);
     * if (dispatcher != null) {
     *     dispatcher.include(getRequest(), getResponse());
     * }
     * </pre>
     *
     * @param path The path to the resource to include.
     * @param options influence the rendering of the included Resource            
     * @throws ServletException Forwarded from including the resource
     * @throws IOException Forwarded from including the resource
     */
    void include(String path,RequestDispatcherOptions options) throws ServletException, IOException;

    /** Same as {@link #include(String,RequestDispatcherOptions)}, but using 
     *  empty options.
     */
    void include(String path) throws ServletException, IOException;
}