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
package org.apache.sling.api.request;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

/**
 * The <code>RequestParameterMap</code> encapsulates all request parameters of
 * a request.
 */
@ProviderType
public interface RequestParameterMap extends Map<String, RequestParameter[]> {

    /**
     * Returns all values for the named parameter or null if none
     * @param name The parameter name
     * @return The request parameter array or {@code null}.
     */
    @Nullable
    RequestParameter[] getValues(@NotNull String name);

    /**
     * Returns the first value for the named parameter or null if none
     * @param name The parameter name
     * @return The request parameter or {@code null}.
     */
    @Nullable
    RequestParameter getValue(String name);
}
