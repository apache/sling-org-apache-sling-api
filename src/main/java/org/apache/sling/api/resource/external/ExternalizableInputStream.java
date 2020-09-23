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

import org.jetbrains.annotations.NotNull;

/**
 * This interface is normally used to extend an InputStream to indicate that it has a URI form that could
 * be used in place of the InputStream if desired. It is used in situations where the internal of a ResourceProvider
 * wants to offload IO to channels that do not pass through the JVM. The URI that is returned may have restrictions
 * imposed on it requiring it to be used immediately. Do not store the URI for later usage as it will, in most cases,
 * have expired.
 *
 */
public interface ExternalizableInputStream {

    /**
     * Get a URI that is specific to the current session, and may be used in any context internal or external. The URI must not
     * be stored and must not be shared between clients. For a wider range of URIs the caller should use the URIProvider class
     * directly and not this interface.
     * @return a URI intended for any network context.
     */
    @NotNull URI getURI();

}
