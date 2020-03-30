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
package org.apache.sling.api.resource.mapping.spi;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.uri.ResourceUri;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

/** SPI interface that contributes to resource mapping and resolving of the resource resolver's map() and resolve() methods.
 * 
 * All registered services build a conceptual chain sorted by service ranking. The resource link is passed through the chain while any
 * ResourceToUriMapper chain member may or may not make adjustments to the resource link.
 * 
 * rr.resolve() passes through the chain starting at the ResourceToUriMapper with the <strong>highest</strong> service ranking and rr.map()
 * passes through the chain starting at the ResourceToUriMapper with the <strong>lowest</strong> service ranking */
@ConsumerType
public interface ResourceToUriMapper {
    
    /** Contributes to the resolve process, may or may not make adjustments to the resource link
     * 
     * @param resourceURI the URI to be resolved
     * @param request the request
     * @param pipelineContext can be used to skip further processing of the chain or for sharing state between instances of ResourceMapping
     * @return the adjusted ResourceUri */
    ResourceUri resolve(@NotNull ResourceUri resourceUri, HttpServletRequest request, MappingChainContext context);

    /** Contributes to the map process, may or may not make adjustments to the resource link.
     * 
     * @param resourceURI the URI to be mapped
     * @param request the request to be taken as example
     * @param pipelineContext can be used to skip further processing of the chain or for sharing state between instances of ResourceMapping
     * @return the adjusted ResourceUri */
    ResourceUri map(@NotNull ResourceUri resourceUri, HttpServletRequest request, MappingChainContext context);

}
