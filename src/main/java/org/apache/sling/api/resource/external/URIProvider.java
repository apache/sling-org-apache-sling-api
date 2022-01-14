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

package org.apache.sling.api.resource.external;

import java.net.URI;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides a URI in exchange for a Resource.
 * Typically the Resource will represent something where is a URI is valiable and usefull.
 * Implementations of this interface must ensure that the any underlying security model is delegated
 * securely and not circumvented. Typically resource provider bundles should implement this provider as in most cases
 * internal implementation details of the resource will be required to achieve the implementation. Ideally
 * implementations should be carefully reviewed by peers.
 *
 */
@ProviderType
public interface URIProvider {

    /**
     * Return a URI applicable to the defined scope.
     * @param resource the resource to convert from.
     * @param scope the required scope.
     * @param operation the required operation.
     * @return a URI if the resource has a URI suitable for the requested scope and operation, otherwise the implementation should throw an IlleagalArgumentException.
     * @throws IllegalArgumentException if a URI for the requested scope and operation cannot be provided to the caller.
     */
    @NotNull URI toURI(@NotNull Resource resource, @NotNull URIProvider.Scope scope, @NotNull URIProvider.Operation operation);

    /**
     * Defines which operation the URI may be used to perform.
     */
    enum Operation {
        /**
         * The URI may be used to create resources at the resource identified by the Resource.
         */
        CREATE,
        /**
         * The URI may be used to read the resource.
         */
        READ,
        /**
         * The URI may be used to update the resource.
         */
        UPDATE,
        /**
         * The URI may be used to delete the resource.
         */
        DELETE
    }

    /**
     * Defines the scope in which the URI may be used.
     * Implementations should pay close attention to the scope requested and not emit URIs inappropriate for the scope requested.
     */
    enum Scope {
        /**
         * A External URI safe to be used by the requesting client in a external context.
         * This does not imply it can be stored, shared between clients or published, only that the client may be on the public
         * internet as opposed to an internal network.
         */
        EXTERNAL,
        /**
         * Internal URI only to be used by a client on an internal network and never leaked onto a public network.
         */
        INTERNAL,
        /**
         * A URI that may be published to many client in public context. Implementations should only issue URIs with this scope
         * if the URI can safely be shared between multiple clients, and therefore by definition public to anonymous clients anywhere
         * on the internet.
         */
        PUBLIC
    }
}
