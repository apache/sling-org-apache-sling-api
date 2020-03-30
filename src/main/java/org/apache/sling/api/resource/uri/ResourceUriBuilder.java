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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class ResourceUriBuilder {

    static final String CHAR_HASH = "#";
    static final String CHAR_QM = "?";
    static final String CHAR_DOT = ".";
    static final String CHAR_SLASH = "/";
    static final String CHAR_AT = "@";
    static final String DOT_LITERAL_REGEX = "\\.(?!\\.?/)";
    static final String CHAR_COLON = ":";

    public static ResourceUriBuilder create() {
        return new ResourceUriBuilder();
    }

    /** Creates a builder from another ResourceUri.
     * 
     * @param resourceUri
     * @return a ResourceUriBuilder */
    public static ResourceUriBuilder createFrom(ResourceUri resourceUri) {
        return create()
                .setScheme(resourceUri.getScheme())
                .setUserInfo(resourceUri.getUserInfo())
                .setHost(resourceUri.getHost())
                .setPort(resourceUri.getPort())
                .setResourcePath(resourceUri.getResourcePath())
                .setSelectors(resourceUri.getSelectors())
                .setExtension(resourceUri.getExtension())
                .setSuffix(resourceUri.getSuffix())
                .setQuery(resourceUri.getQuery())
                .setFragment(resourceUri.getFragment())
                .setSchemeSpecificPart(resourceUri.getSchemeSpecificPart())
                .setResourceResolver(resourceUri instanceof ImmutableResourceUri
                        ? ((ImmutableResourceUri) resourceUri).getBuilder().resourceResolver
                        : null);
    }

    /** Creates a builder from a Resource (only taking the resource path into account).
     * 
     * @param resource
     * @return a ResourceUriBuilder */
    public static ResourceUriBuilder createFrom(Resource resource) {
        return create()
                .setResourcePath(resource.getPath())
                .setResourceResolver(resource.getResourceResolver());
    }

    /** Creates a builder from a RequestPathInfo instance .
     * 
     * @param requestPathInfo
     * @return a ResourceUriBuilder */
    public static ResourceUriBuilder createFrom(RequestPathInfo requestPathInfo) {
        return create()
                .setResourcePath(requestPathInfo.getResourcePath())
                .setSelectors(requestPathInfo.getSelectors())
                .setExtension(requestPathInfo.getExtension())
                .setSuffix(requestPathInfo.getSuffix());
    }

    /** Creates a builder from a request.
     * 
     * @param request
     * @return a ResourceUriBuilder */
    public static ResourceUriBuilder createFrom(SlingHttpServletRequest request) {
        return createFrom(request.getRequestPathInfo())
                .setScheme(request.getScheme())
                .setHost(request.getServerName())
                .setPort(request.getServerPort())
                .setResourceResolver(request.getResourceResolver());
    }

    /** Creates a builder from a URI.
     * 
     * @param uri
     * @return a ResourceUriBuilder */
    public static ResourceUriBuilder createFrom(URI uri) {
        String path = uri.getPath();
        boolean pathExists = !StringUtils.isBlank(path);
        boolean schemeSpecificRelevant = !pathExists && uri.getQuery() == null;
        return create()
                .setScheme(uri.getScheme())
                .setUserInfo(uri.getUserInfo())
                .setHost(uri.getHost())
                .setPort(uri.getPort())
                .setPath(pathExists ? path : null)
                .setQuery(uri.getQuery())
                .setFragment(uri.getFragment())
                .setSchemeSpecificPart(schemeSpecificRelevant ? uri.getSchemeSpecificPart() : null);
    }

    /** Creates a builder from an arbitrary URI string.
     * 
     * @param resourceUriStr
     * @return a ResourceUriBuilder */
    public static ResourceUriBuilder parse(String resourceUriStr) {
        URI uri;
        try {
            uri = new URI(resourceUriStr);
            return createFrom(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI " + resourceUriStr + ": " + e.getMessage(), e);
        }
    }

    /** Creates a builder from a resource path.
     * 
     * @param resourcePathStr
     * @return a ResourceUriBuilder */
    public static ResourceUriBuilder forPath(String resourcePathStr) {
        return new ResourceUriBuilder().setPath(resourcePathStr);
    }

    private String scheme = null;

    private String userInfo = null;
    private String host = null;
    private int port = -1;

    private String resourcePath = null;
    private final List<String> selectors = new LinkedList<String>();
    private String extension = null;
    private String suffix = null;
    private String schemeSpecificPart = null;
    private String query = null;
    private String fragment = null;

    // only needed for getSuffixResource() from interface RequestPathInfo
    private ResourceResolver resourceResolver = null;

    private ResourceUriBuilder() {
    }

    /** @param userInfo
     * @return the builder for method chaining */
    public ResourceUriBuilder setUserInfo(String userInfo) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.userInfo = userInfo;
        return this;
    }

    /** @param host
     * @return the builder for method chaining */
    public ResourceUriBuilder setHost(String host) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.host = host;
        return this;
    }

    /** @param port
     * @return the builder for method chaining */
    public ResourceUriBuilder setPort(int port) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.port = port;
        return this;
    }

    /** @param path
     * @return the builder for method chaining */
    public ResourceUriBuilder setPath(String path) {
        if (schemeSpecificPart != null) {
            return this;
        }
        if (path != null && path.contains(CHAR_DOT)) {
            String[] pathBits = path.split(DOT_LITERAL_REGEX);
            setResourcePath(pathBits[0]);
            if (pathBits.length > 2) {
                setSelectors(Arrays.copyOfRange(pathBits, 1, pathBits.length - 1));
            }
            String extensionAndSuffix = pathBits[pathBits.length - 1];
            String[] extensionAndSuffixBits = extensionAndSuffix.split(CHAR_SLASH, 2);
            setExtension(extensionAndSuffixBits[0]);
            if (extensionAndSuffixBits.length == 2) {
                setSuffix(CHAR_SLASH + extensionAndSuffixBits[1]);
            }
        } else {
            setResourcePath(path);
        }
        return this;
    }

    /** @param resourcePath
     * @return the builder for method chaining */
    public ResourceUriBuilder setResourcePath(String resourcePath) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.resourcePath = resourcePath;
        return this;
    }

    /** @param selectors
     * @return the builder for method chaining */
    public ResourceUriBuilder setSelectors(String[] selectors) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.selectors.clear();
        Arrays.stream(selectors).forEach(this.selectors::add);
        return this;
    }

    /** @param selector
     * @return the builder for method chaining */
    public ResourceUriBuilder addSelector(String selector) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.selectors.add(selector);
        return this;
    }

    /** @param extension
     * @return the builder for method chaining */
    public ResourceUriBuilder setExtension(String extension) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.extension = extension;
        return this;
    }

    /** @param suffix
     * @return the builder for method chaining */
    public ResourceUriBuilder setSuffix(String suffix) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        if (suffix != null && !StringUtils.startsWith(suffix, "/")) {
            throw new IllegalArgumentException("Suffix needs to start with slash");
        }
        this.suffix = suffix;
        return this;
    }

    /** @param query
     * @return the builder for method chaining */
    public ResourceUriBuilder setQuery(String query) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.query = query;
        return this;
    }

    /** @param urlFragment
     * @return the builder for method chaining */
    public ResourceUriBuilder setFragment(String urlFragment) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.fragment = urlFragment;
        return this;
    }

    /** @param scheme
     * @return the builder for method chaining */
    public ResourceUriBuilder setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    /** @param schemeSpecificPart
     * @return the builder for method chaining */
    public ResourceUriBuilder setSchemeSpecificPart(String schemeSpecificPart) {
        if (schemeSpecificPart != null && schemeSpecificPart.isEmpty()) {
            return this;
        }
        this.schemeSpecificPart = schemeSpecificPart;
        return this;
    }

    /** Will remove scheme and authority (that is user info, host and port).
     * 
     * @return the builder for method chaining */
    public ResourceUriBuilder removeSchemeAndAuthority() {
        setScheme(null);
        setUserInfo(null);
        setHost(null);
        setPort(-1);
        return this;
    }

    /** Will take over scheme and authority (user info, host and port) from provided resourceUri.
     * 
     * @param resourceUri
     * @return the builder for method chaining */
    public ResourceUriBuilder useSchemeAndAuthority(ResourceUri resourceUri) {
        setScheme(resourceUri.getScheme());
        setUserInfo(resourceUri.getUserInfo());
        setHost(resourceUri.getHost());
        setPort(resourceUri.getPort());
        return this;
    }

    // only to support getSuffixResource() from interface RequestPathInfo
    private ResourceUriBuilder setResourceResolver(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        return this;
    }

    /** Will take over scheme and authority (user info, host and port) from provided uri.
     * 
     * @param uri
     * @return the builder for method chaining */
    public ResourceUriBuilder useSchemeAndAuthority(URI uri) {
        useSchemeAndAuthority(createFrom(uri).build());
        return this;
    }

    /** Builds the immutable ResourceUri from this builder.
     * 
     * @return the builder for method chaining */
    public ResourceUri build() {
        return new ImmutableResourceUri();
    }

    /** @return string representation of builder */
    public String toString() {
        return build().toString();
    }

    // read-only view on the builder data (to avoid another copy of the data into a new object)
    private class ImmutableResourceUri implements ResourceUri {

        private static final String HTTPS_SCHEME = "https";
        private static final int HTTPS_DEFAULT_PORT = 443;
        private static final String HTTP_SCHEME = "http";
        private static final int HTTP_DEFAULT_PORT = 80;

        @Override
        public String getResourcePath() {
            return resourcePath;
        }

        // returns null in line with
        // https://sling.apache.org/apidocs/sling11/org/apache/sling/api/request/RequestPathInfo.html#getSelectorString--
        @Override
        public String getSelectorString() {
            return !selectors.isEmpty() ? String.join(CHAR_DOT, selectors) : null;
        }

        @Override
        public String[] getSelectors() {
            return selectors.toArray(new String[selectors.size()]);
        }

        @Override
        public String getExtension() {
            return extension;
        }

        @Override
        public String getSuffix() {
            return suffix;
        }

        @Override
        public String getSchemeSpecificPart() {
            return schemeSpecificPart;
        }

        @Override
        public String getQuery() {
            return query;
        }

        @Override
        public String getFragment() {
            return fragment;
        }

        @Override
        public String getScheme() {
            return scheme;
        }

        @Override
        public String getHost() {
            return host;
        }

        @Override
        public int getPort() {
            return port;
        }

        @Override
        public Resource getSuffixResource() {
            if (StringUtils.isNotBlank(suffix) && resourceResolver != null) {
                return resourceResolver.resolve(suffix);
            } else {
                return null;
            }
        }

        @Override
        public String getUserInfo() {
            return userInfo;
        }

        @Override
        public String toString() {
            StringBuilder requestUri = new StringBuilder();

            if (StringUtils.isNotBlank(scheme)) {
                requestUri.append(scheme + CHAR_COLON);
            }
            if (isFullUri()) {
                requestUri.append(CHAR_SLASH + CHAR_SLASH);
                if (StringUtils.isNotBlank(userInfo)) {
                    requestUri.append(userInfo + CHAR_AT);
                }
                requestUri.append(host);
                if (port > 0
                        && !(scheme.equals(HTTP_SCHEME) && port == HTTP_DEFAULT_PORT)
                        && !(scheme.equals(HTTPS_SCHEME) && port == HTTPS_DEFAULT_PORT)) {
                    requestUri.append(CHAR_COLON + port);
                }
            }
            if (resourcePath != null) {
                requestUri.append(resourcePath);
            }
            if (!selectors.isEmpty()) {
                requestUri.append(CHAR_DOT + String.join(CHAR_DOT, selectors));
            }
            if (!StringUtils.isBlank(extension)) {
                requestUri.append(CHAR_DOT + extension);
            }
            if (!StringUtils.isBlank(suffix)) {
                requestUri.append(suffix);
            }
            if (schemeSpecificPart != null) {
                requestUri.append(schemeSpecificPart);
            }
            if (query != null) {
                requestUri.append(CHAR_QM + query);
            }
            if (fragment != null) {
                requestUri.append(CHAR_HASH + fragment);
            }
            return requestUri.toString();
        }

        @Override
        public URI toUri() {
            String uriString = toString();
            try {
                return new URI(uriString);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid Sling URI: " + uriString, e);
            }
        }

        private ResourceUriBuilder getBuilder() {
            return ResourceUriBuilder.this;
        }

    }

}
