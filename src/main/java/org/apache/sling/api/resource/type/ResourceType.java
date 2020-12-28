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
package org.apache.sling.api.resource.type;

import java.util.Objects;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Version;

/**
 * The {@code ResourceType} encapsulates details about a Sling resource type and provides methods for parsing resource type strings.
 *
 * <p>The following patterns are supported for parsing:</p>
 * <ol>
 * <li><tt>a/b/c</tt> - path-based</li>
 * <li><tt>a/b/c/1.0.0</tt> - path-based, versioned</li>
 * <li><tt>a.b.c</tt> - Java package name</li>
 * <li><tt>a.b.c/1.0.0</tt> - Java package name, versioned</li>
 * <li><tt>a</tt> - flat (sub-set of path-based)</li>
 * </ol>
 */
public final class ResourceType {

    private static final Pattern versionPattern = Pattern.compile("[\\d\\.]+(-.*)*$");

    private final String type;
    private final String version;
    private final String resourceLabel;
    private final String toString;

    private ResourceType(@NotNull String type, @Nullable String version) {
        this.type = type;
        this.version = version;
        if (type.lastIndexOf('/') != -1) {
            resourceLabel = type.substring(type.lastIndexOf('/') + 1);
        } else if (type.lastIndexOf('.') != -1) {
            resourceLabel = type.substring(type.lastIndexOf('.') + 1);
        } else {
            resourceLabel = type;
        }
        toString = type + (version == null ? "" : "/" + version);
    }

    /**
     * Returns a resource type's label. The label is important for script selection, since it will provide the name of the main script
     * for this resource type. For more details check the Apache Sling
     * <a href="https://sling.apache.org/documentation/the-sling-engine/url-to-script-resolution.html#scripts-for-get-requests">URL to
     * Script Resolution</a> page
     *
     * @return the resource type label
     */
    @NotNull
    public String getResourceLabel() {
        return resourceLabel;
    }

    /**
     * Returns the resource type string, without any version information.
     *
     * @return the resource type string
     */
    @NotNull
    public String getType() {
        return type;
    }

    /**
     * Returns the version, if available.
     *
     * @return the version, if available; {@code null} otherwise
     */
    @Nullable
    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return toString;
    }

    /**
     * Given a {@code resourceTypeString}, this method will extract a {@link ResourceType} object.
     * <p>The accepted patterns are:</p>
     * <ol>
     * <li><tt>a/b/c</tt> - path-based</li>
     * <li><tt>a/b/c/1.0.0</tt> - path-based, versioned</li>
     * <li><tt>a.b.c</tt> - Java package name</li>
     * <li><tt>a.b.c/1.0.0</tt> - Java package name, versioned</li>
     * <li><tt>a</tt> - flat (sub-set of path-based)</li>
     * </ol>
     *
     * @param resourceTypeString the resource type string to parse
     * @return a {@link ResourceType} object
     * @throws IllegalArgumentException if the {@code resourceTypeString} cannot be parsed
     */
    @NotNull
    public static ResourceType parseResourceType(@NotNull String resourceTypeString) {
        String type = "";
        String version = null;
        if (Objects.nonNull(resourceTypeString) && !resourceTypeString.isEmpty()) {
            int lastSlash = resourceTypeString.lastIndexOf('/');
            if (lastSlash != -1 && !resourceTypeString.endsWith("/")) {
                String versionString = resourceTypeString.substring(lastSlash + 1);
                if (versionPattern.matcher(versionString).matches()) {
                    try {
                        version = Version.parseVersion(versionString).toString();
                        type = resourceTypeString.substring(0, lastSlash);
                    } catch (IllegalArgumentException e) {
                        type = resourceTypeString;
                    }
                } else {
                    type = resourceTypeString;
                }
            } else {
                type = resourceTypeString;
            }
        }
        if (type.isEmpty()) {
            throw new IllegalArgumentException(String.format("Cannot extract a type for the resourceTypeString %s.", resourceTypeString));
        }
        return new ResourceType(type, version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, version, resourceLabel);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ResourceType) {
            ResourceType other = (ResourceType) obj;
            return Objects.equals(type, other.type) && Objects.equals(version, other.version) && Objects.equals(resourceLabel,
                    other.resourceLabel);
        }
        return false;
    }
}
