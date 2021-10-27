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
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

/**
 * <p>This is an extension of a {@link SlingHttpServletResponse} to get the result from a processing operation.</p>
 *
 * <p><strong>Note:</strong> instances of this interface are not thread-safe.</p>
 * @since 1.0 (Sling API Bundle 2.24.0)
 */
@ProviderType
public interface SlingHttpServletResponseResult extends SlingHttpServletResponse {

    /**
     * Gets the content length
     *
     * @return the content length or {@code -1} if not set
     */
    long getContentLength();

    /**
     * Gets the status message
     *
     * @return the status message or {@code null}.
     */
    @Nullable String getStatusMessage();

    /**
     * Gets the named cookie.
     *
     * @param name the name of the cookie
     * @return the cookie or {@code null} if no cookie with that name exists
     */
    @Nullable Cookie getCookie(String name);

    /**
     * Gets all cookies.
     *
     * @return the array of cookies or {@code null} if no cookies were set
     */
    @Nullable Cookie[] getCookies();

    /**
     * Gets the output as a byte array.
     *
     * @return the output as a byte array
     */
    byte[] getOutput();

    /**
     * Gets the output as a string.
     *
     * @return the output as a string
     */
    @NotNull String getOutputAsString();
}
