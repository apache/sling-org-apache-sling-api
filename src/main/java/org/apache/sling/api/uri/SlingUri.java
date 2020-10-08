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
package org.apache.sling.api.uri;

import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

/**
 * <p>
 * Represents an immutable URI in the same way as java.net.URI but is extended with Sling-specific URI parts (e.g. selectors). A SlingUri
 * usually points to a resource but alternatively, can also contain an opaque URI like {@code mailto:} or {@code javascript:}. Use
 * {@link SlingUri#adjust(Consumer)} or {@link SlingUriBuilder} to create new or modified Sling URIs.
 * </p>
 * <p>
 * Opposed to {@link URI}, the regular getters of {@code SlingUri} will not return decoded values, rather the values for user info, path,
 * query and fragment are returned exactly as set before.
 * 
 * @since 1.0.0 (Sling API Bundle 2.23.0)
 */
@ProviderType
public interface SlingUri extends RequestPathInfo {

    /**
     * Returns the {@link URI}.
     * 
     * @return the URI
     */
    @NotNull
    URI toUri();

    /**
     * Returns the URI as String.
     * 
     * @return the URI string
     */
    @NotNull
    String toString();

    /**
     * Returns the scheme.
     * 
     * @return the scheme or null if not set
     */
    @Nullable
    String getScheme();

    /**
     * Returns the user info.
     * 
     * @return the user info of the SlingUri or null if not set
     */
    @Nullable
    String getUserInfo();

    /**
     * Returns the host.
     * 
     * @return returns the host of the SlingUri or null if not set
     */
    @Nullable
    String getHost();

    /**
     * Returns the port.
     * 
     * @return returns the port of the SlingUri or -1 if not set
     */
    int getPort();

    /**
     * Returns the resource path.
     * 
     * @return returns the resource path or null if the URI does not contain a path.
     */
    @Override
    @Nullable
    String getResourcePath();

    /**
     * Returns the selector string.
     * 
     * @return returns the selector string or null if the URI does not contain selector(s) (in line with {@link RequestPathInfo})
     */
    @Override
    @Nullable
    String getSelectorString();

    /**
     * Returns the selectors array.
     * 
     * @return the selectors array (empty if the URI does not contain selector(s), never null)
     */
    @Override
    @NotNull
    String[] getSelectors();

    /**
     * Returns the extension.
     * 
     * @return the extension or null if the URI does not contain an extension
     */
    @Override
    @Nullable
    String getExtension();

    /**
     * Returns the path parameters.
     * 
     * @return the path parameters or an empty Map if the URI does not contain any
     */
    @NotNull
    Map<String, String> getPathParameters();

    /**
     * Returns the suffix part of the URI
     * 
     * @return the suffix string or null if the URI does not contain a suffix
     */
    @Override
    @Nullable
    String getSuffix();

    /**
     * Returns the joint path of resource path, selectors, extension and suffix.
     * 
     * @return the path or null if no path is set
     */
    @Nullable
    String getPath();

    /**
     * Returns the query.
     * 
     * @return the query part of the URI or null if the URI does not contain a query
     */
    @Nullable
    String getQuery();

    /**
     * Returns the fragment.
     * 
     * @return the fragment or null if the URI does not contain a fragment
     */
    @Nullable
    String getFragment();

    /**
     * Returns the scheme-specific part of the URI, compare with Javadoc of {@link URI}.
     * 
     * @return scheme specific part of the URI
     */
    @Nullable
    String getSchemeSpecificPart();

    /**
     * Returns the corresponding suffix resource or null if
     * <ul>
     * <li>no resource resolver is available (depends on the create method used in SlingUriBuilder)</li>
     * <li>the URI does not contain a suffix</li>
     * <li>if the suffix resource could not be found</li>
     * </ul>
     * 
     * @return the suffix resource if available or null
     */
    @Override
    @Nullable
    Resource getSuffixResource();

    /**
     * Returns true the URI is either a relative or absolute path (this is the case if scheme and host is empty and the URI path is set)
     * 
     * @return returns true for path URIs
     */
    boolean isPath();

    /**
     * Returns true if the URI has an absolute path starting with a slash ('/').
     * 
     * @return true if the URI is an absolute path
     */
    boolean isAbsolutePath();

    /**
     * Returns true if the URI is a relative path (no scheme and path does not start with '/').
     * 
     * @return true if URI is a relative path
     */
    boolean isRelativePath();

    /**
     * Returns true the URI is an absolute URI.
     * 
     * @return true if the URI is an absolute URI containing a scheme.
     */
    boolean isAbsolute();

    /**
     * Returns true for opaque URIs like e.g. mailto:jon@example.com.
     * 
     * @return true if the URI is an opaque URI
     */
    boolean isOpaque();

    /**
     * Shortcut to adjust Sling URIs, e.g. {@code slingUri = slingUri.adjust(b -> b.setExtension("html")); }.
     * 
     * @param builderConsumer the consumer (e.g. {@code b -> b.setExtension("html")})
     * @return the adjusted SlingUri (new instance)
     */
    @NotNull
    default SlingUri adjust(@NotNull Consumer<SlingUriBuilder> builderConsumer) {
        SlingUriBuilder builder = SlingUriBuilder.createFrom(this);
        builderConsumer.accept(builder);
        return builder.build();
    }

}
