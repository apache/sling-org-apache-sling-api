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
package org.apache.sling.api.resource.path;

import org.apache.sling.api.resource.ResourceUtil;

/**
 * The {@code PathBuilder} offers a convenient way of creating a normalized absolute path from multiple fragments.
 * Normalization does not confine the result to the initial path's subtree.
 *
 * @since 1.0.0 (Sling API Bundle 2.11.0)
 */
public final class PathBuilder {

    private final StringBuilder sb = new StringBuilder();

    /**
     * Creates a new {@code PathBuilder} instance with an absolute initial path.
     * Normalization is deferred until {@link #toString()} is called.
     *
     * @param path the nonempty initial path, starting with {@code /}
     * @throws IllegalArgumentException if the path is null, empty, or relative
     */
    public PathBuilder(final String path) {

        if (path == null || path.isEmpty() || path.charAt(0) != '/') {
            throw new IllegalArgumentException("Path '" + path + "' is not absolute");
        }

        sb.append(path);
    }

    /**
     * Appends a path fragment to the accumulated path.
     * Fragments may be relative and contain {@code .} or {@code ..} segments,
     * which are resolved when {@link #toString()} is called.
     * A leading {@code /} does not replace the accumulated path; the fragment
     * is joined to it with a slash at the boundary.
     *
     * @param path the nonempty path fragment to append
     * @return this instance
     * @throws IllegalArgumentException if the fragment is null or empty
     */
    public PathBuilder append(final String path) {

        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path '" + path + "' is null or empty");
        }

        boolean trailingSlash = sb.charAt(sb.length() - 1) == '/';
        boolean leadingSlash = path.charAt(0) == '/';

        if (trailingSlash && leadingSlash) {
            sb.append(path.substring(1));
        } else if (!trailingSlash && !leadingSlash) {
            sb.append('/').append(path);
        } else {
            sb.append(path);
        }

        return this;
    }

    /**
     * Returns the normalized path without changing the accumulated fragments.
     * Dot segments are resolved, duplicate slashes are collapsed, and trailing
     * slashes are removed except for the root path.
     *
     * @return the normalized absolute path
     * @throws IllegalArgumentException if the path cannot be normalized, for example
     *         because it traverses above root
     */
    @Override
    public String toString() {
        final String path = sb.toString();
        final String normalizedPath = ResourceUtil.normalize(path);
        if (normalizedPath == null) {
            throw new IllegalArgumentException("Path cannot be normalized");
        }
        return normalizedPath;
    }
}
