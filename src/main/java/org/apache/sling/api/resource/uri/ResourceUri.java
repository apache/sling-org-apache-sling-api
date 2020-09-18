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
package org.apache.sling.api.resource.uri;

import static org.apache.sling.api.resource.uri.ResourceUriBuilder.isBlank;
import static org.apache.sling.api.resource.uri.ResourceUriBuilder.isNotBlank;

import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;

/**
 * Represents an immutable URI that points to a resource or alternatively, can contain opaque URIs like {@code mailto:} or
 * {@code javascript:}. Use {@link ResourceUri#adjust(Consumer)} or {@link ResourceUriBuilder} to create new or modified instances.
 */
public interface ResourceUri extends RequestPathInfo {

    /**
     * @return returns the URI.
     */
    public URI toUri();

    /**
     * @return returns the URI as String.
     */
    public String toString();

    /**
     * @return returns the scheme of the ResourceUri or null if not set
     */
    public String getScheme();

    /**
     * @return returns the user info of the ResourceUri or null if not set
     */
    public String getUserInfo();

    /**
     * @return returns the host of the ResourceUri or null if not set
     */
    public String getHost();

    /**
     * @return returns the port of the ResourceUri or null if not set
     */
    public int getPort();

    /**
     * @return returns the resource path or null if the URI does not contain a path.
     */
    @Override
    public String getResourcePath();

    /**
     * @return returns the selector string or null if the URI does not contain selector(s)
     */
    @Override
    public String getSelectorString();

    /**
     * @return returns the selector array (empty if the URI does not contain selector(s))
     */
    @Override
    public String[] getSelectors();

    /**
     * @return returns the extension or null if the URI does not contain an extension
     */
    @Override
    public String getExtension();

    /**
     * @return returns the path parameters or an empty Map if the URI does not contain any
     */
    public Map<String, String> getPathParameters();

    /**
     * @return returns the suffix or null if the URI does not contain a suffix
     */
    @Override
    public String getSuffix();

    /**
     * @return returns the joint path of resource path, selectors, extension and suffix or null if resource path is not set
     */
    public String getPath();

    /**
     * @return returns the query part of the URI or null if the URI does not contain a query
     */
    public String getQuery();

    /**
     * @return returns the fragment or null if the URI does not contain a fragment
     */
    public String getFragment();

    /**
     * The scheme-specific part of the URI, compare with Javadoc of class
     * <h href="https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URI.html">URI</a>.
     * 
     * @return scheme specific part of the URI
     */
    public String getSchemeSpecificPart();

    /**
     * @return returns the corresponding suffix resource or null if
     *         <ul>
     *         <li>no resource resolver is available (depends on the create method used in ResourceUriBuilder)</li>
     *         <li>the URI does not contain a suffix</li>
     *         <li>if the suffix resource could not be found</li>
     *         </ul>
     */
    @Override
    public Resource getSuffixResource();

    /**
     * @return returns true if the URI is either a relative or absolute path (this is the case if scheme and host is empty and the URI path
     *         is set)
     */
    default boolean isPath() {
        return isBlank(getScheme())
                && isBlank(getHost())
                && isNotBlank(getResourcePath());
    }

    /**
     * @return true if the URI is a absolute path starting with a slash ('/').
     */
    default boolean isAbsolutePath() {
        return isPath() && getResourcePath().startsWith(ResourceUriBuilder.CHAR_SLASH);
    }

    /**
     * @return true if URI is relative (not an URL and not starting with '/')
     */
    default boolean isRelativePath() {
        return isPath() && !getResourcePath().startsWith(ResourceUriBuilder.CHAR_SLASH);
    }

    /**
     * @return true if the URI is an absolute URI containing a scheme.
     */
    default boolean isFullUri() {
        return isNotBlank(getScheme())
                && isNotBlank(getHost());
    }

    /**
     * @return true if the URI is an opaque URI like e.g. mailto:jon@example.com
     */
    default boolean isOpaque() {
        return toUri().isOpaque();
    }

    /**
     * Shortcut to adjust resource URIs, e.g. {@code resourceUri = resourceUri.adjust(b -> b.setExtension("html")); }.
     * 
     * @param builderConsumer
     * @return the adjusted ResourceUri (new instance)
     */
    default ResourceUri adjust(Consumer<ResourceUriBuilder> builderConsumer) {
        ResourceUriBuilder builder = ResourceUriBuilder.createFrom(this);
        builderConsumer.accept(builder);
        return builder.build();
    }

}
