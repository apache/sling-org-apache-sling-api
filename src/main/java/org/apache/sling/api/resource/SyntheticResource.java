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
package org.apache.sling.api.resource;

import java.util.Map;

import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.api.wrappers.ValueMapUtil;
import org.jetbrains.annotations.NotNull;

/**
 * The <code>SyntheticResource</code> class is a simple implementation of the
 * <code>Resource</code> interface which may be used to provide a resource
 * object which has no actual resource data (except for the mandatory property
 * {@value ResourceResolver#PROPERTY_RESOURCE_TYPE}).
 */
public class SyntheticResource extends AbstractResource {

    /** The resource resolver to which this resource is related */
    private final ResourceResolver resourceResolver;

    /** The path of the synthetic resource */
    private final String path;

    /** The type this synthetic resource assumes */
    private final String resourceType;

    /** The metadata of this resource just containing the resource path */
    private final ResourceMetadata resourceMetadata;

    /**
     * Creates a synthetic resource with the given <code>path</code> and
     * <code>resourceType</code>.
     * @param resourceResolver The resource resolver
     * @param path The absolute resource path including the name. Make sure that each segment of the path only contains valid characters in Sling API resource names.
     * @param resourceType The type of the resource
     * @see ResourceUtil#escapeName(String)
     */
    public SyntheticResource(
            @NotNull ResourceResolver resourceResolver, @NotNull String path, @NotNull String resourceType) {
        this.resourceResolver = resourceResolver;
        this.path = path;
        this.resourceType = resourceType;
        this.resourceMetadata = new ResourceMetadata();
        this.resourceMetadata.setResolutionPath(path);
    }

    /**
     * Creates a synthetic resource with the given <code>ResourceMetadata</code>
     * and <code>resourceType</code>.
     * @param resourceResolver The resource resolver
     * @param rm The resource meta data
     * @param resourceType The type of the resource
     */
    public SyntheticResource(
            @NotNull ResourceResolver resourceResolver, @NotNull ResourceMetadata rm, @NotNull String resourceType) {
        this.resourceResolver = resourceResolver;
        this.path = rm.getResolutionPath();
        this.resourceType = resourceType;
        this.resourceMetadata = rm;
    }

    /**
     * @see org.apache.sling.api.resource.Resource#getPath()
     */
    @Override
    public @NotNull String getPath() {
        return path;
    }

    /**
     * @see org.apache.sling.api.resource.Resource#getResourceType()
     */
    @Override
    public @NotNull String getResourceType() {
        return resourceType;
    }

    /**
     * Synthetic resources by default do not have a resource super type.
     */
    @Override
    public String getResourceSuperType() {
        return null;
    }

    /**
     * Returns a resource metadata object containing just the path of this
     * resource as the {@link ResourceMetadata#RESOLUTION_PATH} property.
     */
    @Override
    public @NotNull ResourceMetadata getResourceMetadata() {
        return resourceMetadata;
    }

    /**
     * Returns the {@link ResourceResolver} with which this synthetic resource
     * is related or <code>null</code> if none.
     */
    @Override
    public @NotNull ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    /**
     * Merges the original value map with one containing the single property {@value ResourceResolver#PROPERTY_RESOURCE_TYPE}.
     */
    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if (type == ValueMap.class) {
            ValueMap newValueMap = new ValueMapDecorator(Map.of(ResourceResolver.PROPERTY_RESOURCE_TYPE, resourceType));
            ValueMap originalValueMap = super.adaptTo(ValueMap.class);
            if (originalValueMap != null) {
                // the one with the resource type takes precedence, i.e. a property with the same name in the original
                // value map is hidden
                return (AdapterType) ValueMapUtil.merge(newValueMap, originalValueMap);
            }
            return (AdapterType) newValueMap;
        }
        return super.adaptTo(type);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", type=" + getResourceType() + ", path=" + getPath();
    }
}
