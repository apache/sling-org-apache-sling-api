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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;

/** Represents an immutable URI that points to a resource or alternatively, can contain special URIs like {@code mailto:} or
 * {@code javascript:}. */
public interface ResourceUri extends RequestPathInfo {

    /** @return returns the URI. */
    public URI toUri();

    /** @return returns the URI as String. */
    public String toString();

    /** @return returns the scheme of the link */
    public String getScheme();

    /** @return returns the user info of the link */
    public String getUserInfo();

    /** @return returns the host of the link */
    public String getHost();

    /** @return returns the port of the link */
    public int getPort();

    /** @return returns the resource path or null if link does not contain path. */
    @Override
    public String getResourcePath();

    /** @return returns the selector string */
    @Override
    public String getSelectorString();

    /** @return returns the selector array */
    @Override
    public String[] getSelectors();

    /** @return returns the extension of the link */
    @Override
    public String getExtension();

    /** @return returns the path parameters */
    public Map<String, String> getPathParameters();

    /** @return returns the suffix of the link */
    @Override
    public String getSuffix();

    /** @return returns the query part of the link */
    public String getQuery();

    /** @return returns the url fragment of the link */
    public String getFragment();

    /** @return scheme specific part of the URL */
    public String getSchemeSpecificPart();
    
    /** @return returns the corresponding */
    @Override
    public Resource getSuffixResource();

    /** @return returns true if the link is either a relative or absolute path (this is the case if scheme and host is empty and the URI
     *         path is set) */
    default boolean isPath() {
        return isBlank(getScheme())
                && isBlank(getHost())
                && isNotBlank(getResourcePath());
    }

    /** @return true if the link is a absolute path starting with a slash ('/'). This is the default case for all links to pages and assets
     *         in AEM. */
    default boolean isAbsolutePath() {
        return isPath() && getResourcePath().startsWith(ResourceUriBuilder.CHAR_SLASH);
    }

    /** @return true if link is relative (not an URL and not starting with '/') */
    default boolean isRelativePath() {
        return isPath() && !getResourcePath().startsWith(ResourceUriBuilder.CHAR_SLASH);
    }

    /** @return true if the link is an absolute URI containing a scheme. */
    default boolean isFullUri() {
        return isNotBlank(getScheme())
                && isNotBlank(getHost());
    }

    /** @param builderConsumer
     * @return the adjusted ResourceUri (new instance) */
    default ResourceUri adjust(Consumer<ResourceUriBuilder> builderConsumer) {
        ResourceUriBuilder builder = ResourceUriBuilder.createFrom(this);
        builderConsumer.accept(builder);
        return builder.build();
    }

}
