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
package org.apache.sling.api.request.builder;

import javax.servlet.http.Cookie;

import org.apache.sling.api.SlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/** 
 * This is an extension of a {@link SlingHttpServletResponse} to get
 * the result from a processing.
 * 
 * Instances of this interface are not thread-safe.
 */
@ProviderType
public interface SlingHttpServletResponseResult extends SlingHttpServletResponse {

    /**
     * Build the response.
     * Once this method has been called, the builder must not be used anymore. A new builder
     * needs to be created, to create a new response.
     * @return A response object
     */
    @NotNull SlingHttpServletResponse build();

    /**
     * Get the content length
     * @return The content length or {@code -1} if not set
     */
    long getContentLength();

    /**
     * Get the status message
     * @return The status message or {@code null}.
     */
    String getStatusMessage();

    /**
     * Get the named cookie
     * @param name The name of the cookie
     * @return The cookie or {@code null} if no cookie with that name exists.
     */
    public Cookie getCookie(String name);

    /**
     * Get all cookies
     * @return The array of cookies or {@code null} if no cookies are set.
     */
    Cookie[] getCookies();

    /**
     * Get the output as a byte array
     */
    byte [] getOutput();

    /**
     * Get the output as a string
     */
    String getOutputAsString();
}