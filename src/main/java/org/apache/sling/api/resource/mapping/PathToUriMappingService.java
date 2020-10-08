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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.uri.SlingUri;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides a way to resolve and map paths to Sling URIs. Both operations are extensible by
 * {@link org.apache.sling.spi.urimapping.SlingUriMapper} OSGi services.
 * 
 * @since 1.1.0 (Sling API Bundle 2.23.0)
 */
@ProviderType
public interface PathToUriMappingService {

    /**
     * Maps a path to a Sling URI.
     * 
     * @param referenceRequest the reference request with the same properties as the actual request that will have to resolve the produced
     *        URI.
     * @param unmappedPath the path that is not mapped yet (may or may not contain selector, extension and suffix)
     * @return a @{link PathToUriMappingService.Result}
     */
    Result map(@Nullable HttpServletRequest referenceRequest, @NotNull String unmappedPath);

    /**
     * Resolves a path relative to the given request.
     * 
     * @param request the request
     * @param path the path to be resolved or null for which case the information from request is used
     * @return a @{link PathToUriMappingService.Result}
     */
    Result resolve(@Nullable HttpServletRequest request, @Nullable String path);

    /** The result of a map or resolve operation */
    @ProviderType
    public interface Result {
        /**
         * The Sling URI as result of the resolve or map operation.
         * 
         * @return the Sling URI
         */
        @NotNull
        SlingUri getUri();

        /**
         * Returns all intermediate mappings as produced by {@link org.apache.sling.spi.urimapping.SlingUriMapper} services.
         * 
         * @return the intermediate mappings
         */
        @NotNull
        List<IntermediateMapping> getIntermediateMappings();
    }

    /** Tuple of mapper name and its result as SlingUri. */
    @ProviderType
    public interface IntermediateMapping {

        /**
         * @return The name of the {@link org.apache.sling.spi.urimapping.SlingUriMapper} that produced the intermediate result
         */
        @NotNull
        String getName();

        /**
         * @return The SlingUri as produced by the {@link org.apache.sling.spi.urimapping.SlingUriMapper}
         */
        @NotNull
        SlingUri getUri();
    }

}