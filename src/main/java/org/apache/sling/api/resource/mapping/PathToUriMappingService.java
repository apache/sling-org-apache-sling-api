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
package org.apache.sling.api.resource.mapping;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.uri.ResourceUri;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

/** Provides a way to resolve URIs to resource paths and map resource paths to URIs. */
@ProviderType
public interface PathToUriMappingService {

    /** A context hint for a consumer of the map/resolve result */
    @ProviderType
    public interface ContextHint {
        String getName();
    }

    /** The result of a map or resolve operation */
    @ProviderType
    public interface Result {
        /** @return the ResourceUri */
        @NotNull
        ResourceUri getResourceUri();

        /** @return context hints (e.g. 'invalid link' for map(), or 'requires authentication' for resolve()) */
        @NotNull
        Set<ContextHint> getContextHints();

        /** @return all intermediate mappings as produced by {@link org.apache.sling.spi.resource.mapping.ResourceUriMapper} services. */
        @NotNull
        Map<String, ResourceUri> getIntermediateMappings();
    }

    /** Maps a path to a URI
     * 
     * @param request
     * @param resourcePath
     * @return a @{link PathToUriMappingService.Result} */
    Result map(@Nullable HttpServletRequest exampleRequest, @NotNull String resourcePath);

    /** Resolves a path relative to the given request.
     * 
     * @param request
     * @param path
     * @return a @{link PathToUriMappingService.Result} */
    Result resolve(@Nullable HttpServletRequest request, @Nullable String path);
}