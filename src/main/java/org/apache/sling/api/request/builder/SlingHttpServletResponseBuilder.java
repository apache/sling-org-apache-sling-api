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

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/** 
 * Fluent helper for building a response.
 * 
 * Instances of this interface are not thread-safe.
 */
@ProviderType
public interface SlingHttpServletResponseBuilder {

    /**
     * Build the response.
     * Once this method has been called, the builder must not be used anymore. A new builder
     * needs to be created, to create a new response.
     * @return A response object
     */
    @NotNull SlingHttpServletResponseResult build();
}