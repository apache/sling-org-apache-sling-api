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

import java.util.Locale;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.impl.DeepReadValueMapConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A value map wrapper which implements deep reading of properties
 * based on the resource tree.
 * <p>Deep reads outside the starting resource's subtree, or whose resource paths
 * cannot be normalized, log a WARN and continue the lookup by default.
 * The OSGi configuration PID {@code org.apache.sling.api.wrappers.DeepReadValueMapDecorator}
 * supports the boolean property {@code enforce} (default {@code false}).
 * When enabled, these reads throw {@link IllegalArgumentException} before resource
 * resolution instead. Configuration changes also apply to existing decorators.
 * Diagnostics include the starting resource and requested resource path.
 * This setting only controls this decorator, not other {@link ValueMap} implementations.
 * @since 2.5 (Sling API Bundle 2.7.0)
 */
public class DeepReadValueMapDecorator extends ValueMapDecorator {

    private static final Logger LOG = LoggerFactory.getLogger(DeepReadValueMapDecorator.class);

    private final String resourcePath;

    private final String normalizedResourcePath;

    private final String pathPrefix;

    private final ResourceResolver resolver;

    private final ValueMap base;

    /**
     * Creates a deep-reading view of the resource's properties.
     * @param resource The starting resource.
     * @param base The starting resource's properties.
     * @throws IllegalArgumentException If the resource path cannot be normalized
     *         to an absolute path.
     */
    public DeepReadValueMapDecorator(@NotNull final Resource resource, @NotNull final ValueMap base) {
        super(base);
        this.resourcePath = resource.getPath();
        this.normalizedResourcePath = ResourceUtil.normalize(resourcePath);
        if (normalizedResourcePath == null || !normalizedResourcePath.startsWith("/")) {
            throw new IllegalArgumentException("Invalid resource path: " + sanitizeForMessage(resourcePath));
        }
        this.pathPrefix = resourcePath + "/";
        this.resolver = resource.getResourceResolver();
        this.base = base;
    }

    private ValueMap getValueMap(final String name) {
        final int pos = name.lastIndexOf("/");
        if (pos == -1) {
            return this.base;
        }
        final String requestedPath = pathPrefix + name.substring(0, pos);
        final String normalizedPath = ResourceUtil.normalize(requestedPath);
        if (normalizedPath == null
                || !(normalizedResourcePath.equals("/")
                        || normalizedPath.equals(normalizedResourcePath)
                        || normalizedPath.startsWith(normalizedResourcePath + "/"))) {
            final String message = "Deep read outside resource subtree: resource '" + sanitizeForMessage(resourcePath)
                    + "', requested path '" + sanitizeForMessage(requestedPath) + "'";
            if (DeepReadValueMapConfiguration.isEnforcementEnabled()) {
                throw new IllegalArgumentException(message);
            }
            LOG.warn(message);
        }
        final Resource rsrc = this.resolver.getResource(requestedPath);
        if (rsrc != null) {
            final ValueMap vm = rsrc.adaptTo(ValueMap.class);
            if (vm != null) {
                return vm;
            }
        }
        return ValueMap.EMPTY; // fall back
    }

    private static String sanitizeForMessage(final String value) {
        final StringBuilder result = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            final char character = value.charAt(i);
            if (Character.isISOControl(character) || character == '\u2028' || character == '\u2029') {
                result.append(String.format(Locale.ROOT, "\\u%04x", (int) character));
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    private String getPropertyName(final String name) {
        final int pos = name.lastIndexOf("/");
        if (pos == -1) {
            return name;
        }
        return name.substring(pos + 1);
    }

    /**
     * @see org.apache.sling.api.resource.ValueMap#get(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T get(@NotNull final String name, @NotNull final Class<T> type) {
        return this.getValueMap(name).get(this.getPropertyName(name), type);
    }

    /**
     * @see org.apache.sling.api.resource.ValueMap#get(java.lang.String, java.lang.Object)
     */
    @NotNull
    @Override
    public <T> T get(@NotNull final String name, @NotNull T defaultValue) {
        return this.getValueMap(name).get(this.getPropertyName(name), defaultValue);
    }

    /**
     * @see org.apache.sling.api.wrappers.ValueMapDecorator#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(final Object key) {
        if (key == null) {
            return false;
        }
        final String name = key.toString();
        return this.getValueMap(name).containsKey(this.getPropertyName(name));
    }

    /**
     * @see org.apache.sling.api.wrappers.ValueMapDecorator#get(java.lang.Object)
     */
    @Override
    public Object get(final Object key) {
        if (key == null) {
            return null;
        }
        final String name = key.toString();
        return this.getValueMap(name).get(this.getPropertyName(name));
    }
}
