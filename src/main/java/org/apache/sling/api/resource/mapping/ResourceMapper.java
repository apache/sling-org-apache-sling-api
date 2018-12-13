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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Allows access to resource mappings
 * 
 * <p>This interface superceeds the resource mapping functionality present
 * in the {@link ResourceResolver}. Although the methods present in that
 * interface will continue to work, the resource mapper will provide better
 * APIs to access the resource mappings.</p>
 * 
 * <p>Implementations of this class are obtained by adapting a {@link ResourceResolver}
 * instance. As such, the mappings returned by its methods reflect the repository
 * permissions of the underyling resolver instance.</p> 
 */
@ProviderType
public interface ResourceMapper {
    
    /**
     * Returns a path mapped from the (resource) path applying the reverse
     * mapping used by the {@link ResourceResolver#resolve(String)} such that when the path is
     * given to the {@link ResourceResolver#resolve(String)} method the same resource is
     * returned.
     * <p>
     * Note, that technically the <code>resourcePath</code> need not refer to an
     * existing resource. This method just applies the mappings and returns the
     * resulting string. If the <code>resourcePath</code> does not address an
     * existing resource roundtripping may of course not work and calling
     * {@link ResourceResolver#resolve(String)} with the path returned may return
     * <code>null</code>.
     * <p>
     * This method is intended as the reverse operation of the
     * {@link ResourceResolver#resolve(String)} method.
     *
     * @param resourcePath The path for which to return a mapped path.
     * @return The mapped path.
     * @throws IllegalStateException if the underlying resource resolver has already been
     *             {@link ResourceResolver#close() closed}.
     *
     * @since 1.0.0 (Sling API Bundle 2.19.0)
     */
    @NotNull String getMapping(@NotNull String resourcePath);

    /**
     * Returns an URL mapped from the (resource) path applying the reverse
     * mapping used by the {@link ResourceResolver#resolve(HttpServletRequest, String)} such
     * that when the path is given to the
     * {@link ResourceResolver#resolve(HttpServletRequest, String)} method the same resource is
     * returned.
     * <p>
     * Note, that technically the <code>resourcePath</code> need not refer to an
     * existing resource. This method just applies the mappings and returns the
     * resulting string. If the <code>resourcePath</code> does not address an
     * existing resource roundtripping may of course not work and calling
     * {@link ResourceResolver#resolve(HttpServletRequest, String)} with the path returned may
     * return <code>null</code>.
     * <p>
     * This method is intended as the reverse operation of the
     * {@link ResourceResolver#resolve(HttpServletRequest, String)} method. As such the URL
     * returned is expected to be an absolute URL including scheme, host, any
     * servlet context path and the actual path used to resolve the resource.

     * @param resourcePath The path for which to return a mapped path.
     * @param request The http servlet request object which may be used to apply
     *            more mapping functionality.
     * @return The mapped URL.
     * @throws IllegalStateException if the underlying resource resolver has already been
     *             {@link ResourceResolver#close() closed}.
     * @since 1.0.0 (Sling API Bundle 2.19.0)
     */
    @NotNull String getMapping(@NotNull String resourcePath, @NotNull HttpServletRequest request);
    
    /**
     * Returns all possible mappings for a given {@code resourcePath} as paths.
     * 
     * <p>
     * This method differs from the {@link #getMapping(String)} variant
     * by guaranteeing that all possible mappings are returned for a specified path.
     * 
     * <p>
     * The mappings are not returned in any particular order.
     * 
     * @param resourcePath The path for which to return a mapped path.
     * @return a collection of mapped URLs, in no particular order. May not be null or empty.
     * @throws IllegalStateException if the underlying resource resolver has already been
     *             {@link ResourceResolver#close() closed}.
     * @since 1.0.0 (Sling API Bundle 2.19.0)
     */
    Collection<String> getAllMappings(@NotNull String resourcePath);

    /**
     * Returns all possible mappings for a given {@code resourcePath} as URLs.
     * 
     * <p>
     * This method differs from the {@link #getMapping(String, HttpServletRequest)} variant
     * by guaranteeing that all possible mappings are returned for a specified path.
     * 
     * <p>
     * The mappings are not returned in any particular order.
     * 
     * @param resourcePath The path for which to return a mapped path.
     * @param request The http servlet request object which may be used to apply
     *            more mapping functionality.

     * @return a collection of mapped URLs, in no particular order. May not be null or empty.
     * @throws IllegalStateException if the underlying resource resolver has already been
     *             {@link ResourceResolver#close() closed}.
     * @since 1.0.0 (Sling API Bundle 2.19.0)
     */
    Collection<String> getAllMappings(@NotNull String resourcePath, @NotNull HttpServletRequest request);
}
