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
package org.apache.sling.spi.resource.mapping;

import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.mapping.PathToUriMappingService.ContextHint;
import org.apache.sling.api.resource.uri.ResourceUri;
import org.osgi.annotation.versioning.ProviderType;

/** Provides ResourceToUriMapper instances with additional context. */
@ProviderType
public interface MappingChainContext {

    /** May be called by any ResourceUriMapper in the chain to indicate that the rest of the chain should be skipped. */
    void skipRemainingChain();

    /** Add @{link ContextHint} (e.g. 'invalid link' for map(), or 'requires authentication' for resolve()) */
    void addContextHint(ContextHint contextHint);

    /** The resource resolver that was used to call map() or resolve(). */
    ResourceResolver getResourceResolver();

    /** Allows to share state between ResourceToUriMapper instances in the chain.
     * 
     * @return a mutable map to share state (never null). */
    Map<String, Object> getAttributes();

    /** Provides access to intermediate mappings.
     * 
     * @return the resource mappings */
    Map<String, ResourceUri> getIntermediateMappings();

}
