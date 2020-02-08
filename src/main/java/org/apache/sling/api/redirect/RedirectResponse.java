/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.sling.api.redirect;

/**
 * <p>
 * A Redirect redirect defines the base class used to hold information to build a redirect.  It is extended
 * by the caller to RedirectResolver.resolve, and called by the implementation of the RedirectResolver interface.
 * On return from that call the caller will use the information in the RedirectResponse to perform whatever operation
 * it needs to perform. Typically this will be returning a http redirect, but there may be other uses.
 * </p><p>
 * A class is being used rather than an interface to allow empty setter methods to be added over time.
 * Callers to RedirectResolver.resolve should override those methods that they need information for. New methods to this
 * class should be added with empty implementations to ensure that pre-exiting classes extending this method to not
 * have to be updated.
 * </p>
 */
public class RedirectResponse {

    /**
     * Value to be used in setStatus to indicate no redirect available for context of the resolution.
     */
    public static final int NO_REDIRECT = -1;
    /**
     * Set a header on the redirect. This will replace existing headers of the same name.
     * If called twice, the header will be reset with the latest value.
     * Multiple headers of the same name are not supported.
     * @param name the name of the header.
     * @param value the value of the header.
     */
    public void setHeader(String name, String value) {
    }

    /**
     * Set the redirect to be used. Will not be sent until the RedirectResponse is returned from the
     * RedirectResolver.resolver and processed.
     * @param url the url to set, should be set in the context of a request.
     */
    public void setRedirect(String url) {

    }

    /**
     * Set the status code used in the redirect redirect. Typically 301, but other status codes may be used.
     * If the status code is not set, or is set to NO_REDIRECT then the redirect will not be acted on.
     * @param i status code number.
     */
    public void setStatus(int i) {

    }
}
