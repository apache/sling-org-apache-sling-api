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

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.uri.SlingUri;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * <p>
 * SPI interface that contributes to the resolving and mapping of Sling URIs. All registered services build a conceptual chain sorted by
 * service ranking. The Sling URI is passed through the chain while any SlingUriMapper chain member may or may not make adjustments to the
 * Sling URI.
 * </p>
 * <p>
 * The {@link org.apache.sling.api.resource.mapping.PathToUriMappingService} allows to call the resolve() (however normally called by
 * request only) and map() methods. resolve() passes through the chain starting at the SlingUriMapper with the <strong>highest</strong>
 * service ranking and map() passes through the chain starting at the SlingUriMapper with the <strong>lowest</strong> service ranking.
 * </p>
 * <p>
 * The resource resolver's map() and resolve() methods also use PathToUriMappingService as implementation.
 * </p>
 */
@ConsumerType
public interface SlingUriMapper {

    /**
     * Contributes to the resolve process (forward mapping), may or may not make adjustments to the Sling URI
     * 
     * @param resourceUri the URI to be mapped for resolution
     * @param request the request context that may or may not influence the resolution process (request may be null)
     * @param context can be used to skip further processing of the chain or for sharing state between instances of SlingUriMapper services
     * @return the adjusted SlingUri or if no adjustments are necessary, just return resourceUri as passed in by first parameter
     */
    SlingUri resolve(@NotNull SlingUri resourceUri, @Nullable HttpServletRequest request, @NotNull MappingChainContext context);

    /**
     * Contributes to the reverse mapping process, may or may not make adjustments to the Sling URI.
     * 
     * @param resourceUri the URI to be mapped
     * @param request the request to be taken as reference
     * @param context can be used to skip further processing of the chain or for sharing state between instances of SlingUriMapper services
     * @return the adjusted SlingUri or if no adjustments are necessary, just return resourceUri as passed in by first parameter
     */
    SlingUri map(@NotNull SlingUri resourceUri, @Nullable HttpServletRequest request, @NotNull MappingChainContext context);

}
