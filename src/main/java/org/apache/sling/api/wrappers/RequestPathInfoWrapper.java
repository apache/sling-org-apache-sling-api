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
package org.apache.sling.api.wrappers;

import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The <code>RequestPathInfoWrapper</code> class is a default wrapper
 * class around a {@link RequestPathInfo} which may be extended to amend
 * the functionality of the original request path info object.
 */
public class RequestPathInfoWrapper implements RequestPathInfo {

    private final @NotNull RequestPathInfo delegate;
    
    public RequestPathInfoWrapper(@NotNull RequestPathInfo delegate) {
        this.delegate = delegate;
    }

    /**
     * Returns the original {@link RequestPathInfo} object wrapped by
     * this object.
     * @return The wrapped request path info.
     */
    public @NotNull RequestPathInfo getRequestPathInfo() {
        return delegate;
    }

    public @NotNull String getResourcePath() {
        return delegate.getResourcePath();
    }

    public @Nullable String getExtension() {
        return delegate.getExtension();
    }

    public @Nullable String getSelectorString() {
        return delegate.getSelectorString();
    }

    public @NotNull String[] getSelectors() {
        return delegate.getSelectors();
    }

    public @Nullable String getSuffix() {
        return delegate.getSuffix();
    }

    public @Nullable Resource getSuffixResource() {
        return delegate.getSuffixResource();
    }
}
