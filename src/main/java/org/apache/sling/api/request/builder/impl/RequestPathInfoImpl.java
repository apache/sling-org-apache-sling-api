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
package org.apache.sling.api.request.builder.impl;

import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * {@link RequestPathInfo} implementation.
 */
public class RequestPathInfoImpl implements RequestPathInfo {

    private final String extension;
    private final String resourcePath;
    private final String[] selectors;
    private final String suffix;
    
    private final ResourceResolver resourceResolver;
    
    public RequestPathInfoImpl(final Resource resource,
        final String[] selectors,
        final String extension,
        final String suffix) {
        this.resourceResolver = resource.getResourceResolver();
        this.resourcePath = resource.getPath();
        this.selectors = selectors == null ? new String[0] : selectors;
        this.extension = extension;
        this.suffix = suffix;
    }

    @Override
    public String getExtension() {
        return this.extension;
    }

    @Override
    public String getResourcePath() {
        return this.resourcePath;
    }

    @Override
    public String[] getSelectors() {
        return this.selectors;
    }

    @Override
    public String getSelectorString() {
        return this.selectors.length == 0 ? null : String.join(".", this.selectors);
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }

    @Override
    public Resource getSuffixResource() {
        if (suffix == null) {
            return null;
        }
        return this.resourceResolver.getResource(suffix);
    }
}
