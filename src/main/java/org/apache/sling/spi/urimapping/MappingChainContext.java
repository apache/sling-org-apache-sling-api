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
package org.apache.sling.spi.urimapping;

import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.uri.SlingUri;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides SlingUriMapper instances with additional context.
 * 
 * @since 1.0.0 (Sling API Bundle 2.23.0)
 */
@ProviderType
public interface MappingChainContext {

    /**
     * May be called by any SlingUriMapper in the chain to indicate that the rest of the chain should be skipped.
     */
    void skipRemainingChain();

    /**
     * A service resource resolver with read permissions.
     * 
     * @return a resource resolver
     */
    @NotNull
    ResourceResolver getResourceResolver();

    /**
     * Allows to share state between SlingUriMapper instances in the chain.
     * 
     * @return a mutable map to share state (never null).
     */
    @NotNull
    Map<String, Object> getAttributes();

    /**
     * Provides access to intermediate mappings as already created by SlingUriMapper instances earlier in the chain.
     * 
     * @return the URI mappings
     */
    @NotNull
    Map<String, SlingUri> getIntermediateMappings();

}
