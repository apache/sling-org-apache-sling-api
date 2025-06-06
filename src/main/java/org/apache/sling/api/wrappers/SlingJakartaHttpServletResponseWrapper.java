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

import jakarta.servlet.http.HttpServletResponseWrapper;
import org.apache.sling.api.SlingJakartaHttpServletResponse;

/**
 * The <code>SlingJakartaHttpServletResponseWrapper</code> class is a default wrapper
 * class around a {@link SlingJakartaHttpServletResponse} which may be extended to
 * amend the functionality of the original response object.
 *
 * There's nothing interesting to wrap currently, as the SlingJakartaHttpServletResponse
 * interface is empty.
 * So this exists only for symmetry with {@link SlingJakartaHttpServletRequestWrapper}
 * @since 2.9.0
 */
public class SlingJakartaHttpServletResponseWrapper extends HttpServletResponseWrapper
        implements SlingJakartaHttpServletResponse {

    /**
     * Create a wrapper for the supplied wrappedRequest
     * @param wrappedResponse The response
     */
    public SlingJakartaHttpServletResponseWrapper(SlingJakartaHttpServletResponse wrappedResponse) {
        super(wrappedResponse);
    }

    /**
     * Return the original {@link SlingJakartaHttpServletResponse} object wrapped by
     * this.
     * @return The wrapped response.
     */
    public SlingJakartaHttpServletResponse getSlingResponse() {
        return (SlingJakartaHttpServletResponse) getResponse();
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        return getSlingResponse().adaptTo(type);
    }
}
