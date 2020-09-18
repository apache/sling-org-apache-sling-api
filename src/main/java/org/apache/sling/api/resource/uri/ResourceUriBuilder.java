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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builder for ResourceUris.
 */
public class ResourceUriBuilder {

    private static final String HTTPS_SCHEME = "https";
    private static final int HTTPS_DEFAULT_PORT = 443;
    private static final String HTTP_SCHEME = "http";
    private static final int HTTP_DEFAULT_PORT = 80;

    static final char CHAR_HASH = '#';
    static final char CHAR_QM = '?';
    static final char CHAR_AT = '@';
    static final char CHAR_COLON = ':';
    static final char CHAR_SEMICOLON = ';';
    static final char CHAR_EQUALS = '=';
    static final char CHAR_SINGLEQUOTE = '\'';
    static final String CHAR_DOT = ".";
    static final String CHAR_SLASH = "/";
    static final String SELECTOR_DOT_REGEX = "\\.(?!\\.?/)"; // (?!\\.?/) to avoid matching ./ and ../
    static final String PATH_PARAMETERS_REGEX = ";([a-zA-z0-9]+)=(?:\\'([^']*)\\'|([^/]+))";

    public static ResourceUriBuilder create() {
        return new ResourceUriBuilder();
    }

    /**
     * Creates a builder from another ResourceUri.
     * 
     * @param resourceUri
     * @return a ResourceUriBuilder
     */
    public static ResourceUriBuilder createFrom(ResourceUri resourceUri) {
        return create()
                .setScheme(resourceUri.getScheme())
                .setUserInfo(resourceUri.getUserInfo())
                .setHost(resourceUri.getHost())
                .setPort(resourceUri.getPort())
                .setResourcePath(resourceUri.getResourcePath())
                .setPathParameters(resourceUri.getPathParameters())
                .setSelectors(resourceUri.getSelectors())
                .setExtension(resourceUri.getExtension())
                .setSuffix(resourceUri.getSuffix())
                .setQuery(resourceUri.getQuery())
                .setFragment(resourceUri.getFragment())
                .setSchemeSpecificPart(resourceUri.isOpaque() ? resourceUri.getSchemeSpecificPart() : null)
                .setResourceResolver(resourceUri instanceof ImmutableResourceUri
                        ? ((ImmutableResourceUri) resourceUri).getBuilder().resourceResolver
                        : null);
    }

    /**
     * Creates a builder from a Resource (only taking the resource path into account).
     * 
     * @param resource
     * @return a ResourceUriBuilder
     */
    public static ResourceUriBuilder createFrom(Resource resource) {
        return create()
                .setResourcePath(resource.getPath())
                .setResourceResolver(resource.getResourceResolver());
    }

    /**
     * Creates a builder from a RequestPathInfo instance .
     * 
     * @param requestPathInfo
     * @return a ResourceUriBuilder
     */
    public static ResourceUriBuilder createFrom(RequestPathInfo requestPathInfo) {
        Resource suffixResource = requestPathInfo.getSuffixResource();
        return create()
                .setResourceResolver(suffixResource != null ? suffixResource.getResourceResolver() : null)
                .setResourcePath(requestPathInfo.getResourcePath())
                .setSelectors(requestPathInfo.getSelectors())
                .setExtension(requestPathInfo.getExtension())
                .setSuffix(requestPathInfo.getSuffix());
    }

    /**
     * Creates a builder from a request.
     * 
     * @param request
     * @return a ResourceUriBuilder
     */
    public static ResourceUriBuilder createFrom(SlingHttpServletRequest request) {
        return createFrom(request.getRequestPathInfo())
                .setResourceResolver(request.getResourceResolver())
                .setScheme(request.getScheme())
                .setHost(request.getServerName())
                .setPort(request.getServerPort())
                .setQuery(request.getQueryString());
    }

    /**
     * Creates a builder from an arbitrary URI.
     * 
     * @param uri
     *            the uri to transform to a ResourceUri
     * @param resourceResolver
     *            a resource resolver is needed to decide up to what part the path is the resource path (that decision is only possible by
     *            checking against the underlying repository). If null is passed in, the shortest viable resource path is used.
     * @return a ResourceUriBuilder
     */
    public static ResourceUriBuilder createFrom(@NotNull URI uri, @Nullable ResourceResolver resourceResolver) {
        String path = uri.getPath();
        boolean pathExists = isNotBlank(path);
        boolean schemeSpecificRelevant = !pathExists && uri.getQuery() == null;
        return create()
                .setResourceResolver(resourceResolver)
                .setScheme(uri.getScheme())
                .setUserInfo(uri.getUserInfo())
                .setHost(uri.getHost())
                .setPort(uri.getPort())
                .setPath(pathExists ? path : null)
                .setQuery(uri.getQuery())
                .setFragment(uri.getFragment())
                .setSchemeSpecificPart(schemeSpecificRelevant ? uri.getSchemeSpecificPart() : null);
    }

    /**
     * Creates a builder from an arbitrary URI string.
     * 
     * @param uriStr
     *            to uri string to parse
     * @param resourceResolver
     *            a resource resolver is needed to decide up to what part the path is the resource path (that decision is only possible by
     *            checking against the underlying repository). If null is passed in, the shortest viable resource path is used.
     * @return a ResourceUriBuilder
     */
    public static ResourceUriBuilder parse(@NotNull String uriStr, @Nullable ResourceResolver resourceResolver) {
        URI uri;
        try {
            uri = new URI(uriStr);
            return createFrom(uri, resourceResolver);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI " + uriStr + ": " + e.getMessage(), e);
        }
    }

    // simple package scope helper to avoid stringutil dependency
    static boolean isBlank(final CharSequence cs) {
        return cs == null || cs.chars().allMatch(Character::isWhitespace);
    }

    static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    private String scheme = null;

    private String userInfo = null;
    private String host = null;
    private int port = -1;

    private String resourcePath = null;
    private final List<String> selectors = new LinkedList<>();
    private String extension = null;
    private final Map<String, String> pathParameters = new LinkedHashMap<>();
    private String suffix = null;
    private String schemeSpecificPart = null;
    private String query = null;
    private String fragment = null;

    // only needed for getSuffixResource() from interface RequestPathInfo
    private ResourceResolver resourceResolver = null;

    // to ensure a builder is used only once (as the ImmutableResourceUri being created in build() is sharing its state)
    private boolean isBuilt = false;

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

        // adds path parameters to this.pathParameters and returns path without those
        path = extractPathParameters(path);

        // split in resource path, selectors, extension and suffix
        Matcher dotMatcher;
        if (path != null && path.startsWith(ResourceUriBuilder.CHAR_SLASH) && resourceResolver != null) {
            setResourcePath(path);
            rebaseResourcePath();
        } else if (path != null && (dotMatcher = Pattern.compile(SELECTOR_DOT_REGEX).matcher(path)).find()) {
            int firstDotPosition = dotMatcher.start();
            setPathWithDefinedResourcePosition(path, firstDotPosition);
        } else {
            setResourcePath(path);
        }

        return this;
    }

    /**
     * Will rebase the uri based on the underlying resource structure. A resource resolver is necessary for this operation, hence
     * setResourceResolver() needs to be called before balanceResourcePath() or a create method that implicitly sets this has to be used.
     * 
     * @return the builder for method chaining
     * @throws IllegalStateException
     *             if no resource resolver is available
     */
    public ResourceUriBuilder rebaseResourcePath() {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        if (resourceResolver == null) {
            throw new IllegalStateException("setResourceResolver() needs to be called before balanceResourcePath()");
        }

        String path = assemblePath(false);
        if (path == null) {
            return this; // nothing to rebase
        }
        ResourcePathIterator it = new ResourcePathIterator(path);
        String availableResourcePath = null;
        while (it.hasNext()) {
            availableResourcePath = it.next();
            if (resourceResolver.getResource(availableResourcePath) != null) {
                break;
            }
        }
        if (availableResourcePath == null) {
            return this; // nothing to rebase
        }

        selectors.clear();
        extension = null;
        suffix = null;
        if (availableResourcePath.length() == path.length()) {
            resourcePath = availableResourcePath;
        } else {
            setPathWithDefinedResourcePosition(path, availableResourcePath.length());
        }
        return this;
    }

    /**
     * @param resourcePath
     * @return the builder for method chaining
     */
    public ResourceUriBuilder setResourcePath(String resourcePath) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.resourcePath = resourcePath;
        return this;
    }

    /**
     * @param selectors
     * @return the builder for method chaining
     */
    public ResourceUriBuilder setSelectors(String[] selectors) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.selectors.clear();
        Arrays.stream(selectors).forEach(this.selectors::add);
        return this;
    }

    /**
     * @param selector
     * @return the builder for method chaining
     */
    public ResourceUriBuilder addSelector(String selector) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.selectors.add(selector);
        return this;
    }

    /**
     * @param extension
     * @return the builder for method chaining
     */
    public ResourceUriBuilder setExtension(String extension) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.extension = extension;
        return this;
    }

    /**
     * @return returns the path parameters
     */
    public ResourceUriBuilder setPathParameter(String key, String value) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.pathParameters.put(key, value);
        return this;
    }

    public ResourceUriBuilder setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters.clear();
        this.pathParameters.putAll(pathParameters);
        return this;
    }

    /**
     * @param suffix
     * @return the builder for method chaining
     */
    public ResourceUriBuilder setSuffix(String suffix) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        if (suffix != null && !suffix.startsWith("/")) {
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

    /**
     * Sets the resource resolver (required for {@link RequestPathInfo#getSuffixResource()}).
     * 
     * @param resourceResolver
     *            the resource resolver
     * @return the builder for method chaining
     */
    public ResourceUriBuilder setResourceResolver(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        return this;
    }

    /** Will take over scheme and authority (user info, host and port) from provided uri.
     * 
     * @param uri
     * @return the builder for method chaining */
    public ResourceUriBuilder useSchemeAndAuthority(URI uri) {
        useSchemeAndAuthority(createFrom(uri, resourceResolver).build());
        return this;
    }

    /** Builds the immutable ResourceUri from this builder.
     * 
     * @return the builder for method chaining */
    public ResourceUri build() {
        if (isBuilt) {
            throw new IllegalStateException("ResourceUriBuilder.build() may only be called once per builder instance");
        }
        isBuilt = true;
        return new ImmutableResourceUri();
    }

    private String toStringInternal(boolean includeScheme, boolean includeFragment) {
        StringBuilder requestUri = new StringBuilder();

        if (includeScheme && isNotBlank(scheme)) {
            requestUri.append(scheme + CHAR_COLON);
        }
        if (isNotBlank(scheme) && isNotBlank(host)) {
            requestUri.append(CHAR_SLASH + CHAR_SLASH);
            if (isNotBlank(userInfo)) {
                requestUri.append(userInfo + CHAR_AT);
            }
            requestUri.append(host);
            if (port > 0
                    && !(scheme.equals(HTTP_SCHEME) && port == HTTP_DEFAULT_PORT)
                    && !(scheme.equals(HTTPS_SCHEME) && port == HTTPS_DEFAULT_PORT)) {
                requestUri.append(CHAR_COLON);
                requestUri.append(port);
            }
        }
        if (resourcePath != null) {
            requestUri.append(assemblePath(true));
        }
        if (schemeSpecificPart != null) {
            requestUri.append(schemeSpecificPart);
        }
        if (query != null) {
            requestUri.append(CHAR_QM + query);
        }
        if (includeFragment && fragment != null) {
            requestUri.append(CHAR_HASH + fragment);
        }
        return requestUri.toString();
    }

    /** @return string representation of builder */
    public String toString() {
        return toStringInternal(true, true);
    }

    private void setPathWithDefinedResourcePosition(String path, int firstDotPositionAfterResourcePath) {
        setResourcePath(path.substring(0, firstDotPositionAfterResourcePath));
        int firstSlashAfterFirstDotPosition = path.indexOf(CHAR_SLASH, firstDotPositionAfterResourcePath);
        String pathWithoutSuffix = firstSlashAfterFirstDotPosition > -1
                ? path.substring(firstDotPositionAfterResourcePath + 1, firstSlashAfterFirstDotPosition)
                : path.substring(firstDotPositionAfterResourcePath + 1);
        String[] pathBits = pathWithoutSuffix.split(SELECTOR_DOT_REGEX);
        if (pathBits.length > 1) {
            setSelectors(Arrays.copyOfRange(pathBits, 0, pathBits.length - 1));
        }
        setExtension(pathBits.length > 0 && pathBits[pathBits.length - 1].length() > 0 ? pathBits[pathBits.length - 1] : null);
        setSuffix(firstSlashAfterFirstDotPosition > -1 ? path.substring(firstSlashAfterFirstDotPosition) : null);
    }

    private String extractPathParameters(String path) {
        Map<String, String> currentPathParameters = null;
        if (path != null) {
            Pattern pathParameterRegex = Pattern.compile(PATH_PARAMETERS_REGEX);

            StringBuffer resultString = null;
            Matcher regexMatcher = pathParameterRegex.matcher(path);
            while (regexMatcher.find()) {
                if (resultString == null) {
                    resultString = new StringBuffer();
                }
                if (currentPathParameters == null) {
                    currentPathParameters = new LinkedHashMap<>();
                }
                regexMatcher.appendReplacement(resultString, "");
                String key = regexMatcher.group(1);
                String value = isNotBlank(regexMatcher.group(2)) ? regexMatcher.group(2) : regexMatcher.group(3);
                currentPathParameters.put(key, value);
            }
            if (resultString != null) {
                regexMatcher.appendTail(resultString);
                path = resultString.toString();
                pathParameters.putAll(currentPathParameters);
            }
        }
        return path;
    }

    private String assemblePath(boolean includePathParamters) {
        if (resourcePath == null) {
            return null;
        }

        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(resourcePath);
        if (includePathParamters && !pathParameters.isEmpty()) {
            for (Map.Entry<String, String> pathParameter : pathParameters.entrySet()) {
                pathBuilder.append(CHAR_SEMICOLON + pathParameter.getKey() + CHAR_EQUALS +
                        CHAR_SINGLEQUOTE + pathParameter.getValue() + CHAR_SINGLEQUOTE);
            }
        }

        boolean dotAdded = false;
        if (!selectors.isEmpty()) {
            pathBuilder.append(CHAR_DOT + String.join(CHAR_DOT, selectors));
            dotAdded = true;
        }
        if (isNotBlank(extension)) {
            pathBuilder.append(CHAR_DOT + extension);
            dotAdded = true;
        }

        if (isNotBlank(suffix)) {
            if (!dotAdded) {
                pathBuilder.append(CHAR_DOT);
            }
            pathBuilder.append(suffix);
        }
        return pathBuilder.toString();
    }


    // read-only view on the builder data (to avoid another copy of the data into a new object)
    private class ImmutableResourceUri implements ResourceUri {

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
        public Map<String, String> getPathParameters() {
            return Collections.unmodifiableMap(pathParameters);
        }

        @Override
        public String getSuffix() {
            return suffix;
        }

        @Override
        public String getPath() {
            return assemblePath(true);
        }

        @Override
        public String getSchemeSpecificPart() {
            if (isOpaque()) {
                return schemeSpecificPart;
            } else {
                return toStringInternal(false, false);
            }
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
            if (isNotBlank(suffix) && resourceResolver != null) {
                return resourceResolver.resolve(suffix);
            } else {
                return null;
            }
        }

        @Override
        public String getUserInfo() {
            return userInfo;
        }

        // overwriting default, this implementation keeps the schemeSpecificPart empty for all non-opaque URLs
        public boolean isOpaque() {
            return isNotBlank(getScheme())
                    && isNotBlank(schemeSpecificPart);
        }

        @Override
        public String toString() {
            return toStringInternal(true, true);
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

        // generated hashCode() and equals()
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((extension == null) ? 0 : extension.hashCode());
            result = prime * result + ((fragment == null) ? 0 : fragment.hashCode());
            result = prime * result + ((host == null) ? 0 : host.hashCode());
            result = prime * result + pathParameters.hashCode();
            result = prime * result + port;
            result = prime * result + ((query == null) ? 0 : query.hashCode());
            result = prime * result + ((resourcePath == null) ? 0 : resourcePath.hashCode());
            result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
            result = prime * result + ((schemeSpecificPart == null) ? 0 : schemeSpecificPart.hashCode());
            result = prime * result + selectors.hashCode();
            result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
            result = prime * result + ((userInfo == null) ? 0 : userInfo.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ImmutableResourceUri other = (ImmutableResourceUri) obj;
            if (extension == null) {
                if (other.getBuilder().extension != null)
                    return false;
            } else if (!extension.equals(other.getBuilder().extension))
                return false;
            if (fragment == null) {
                if (other.getBuilder().fragment != null)
                    return false;
            } else if (!fragment.equals(other.getBuilder().fragment))
                return false;
            if (host == null) {
                if (other.getBuilder().host != null)
                    return false;
            } else if (!host.equals(other.getBuilder().host))
                return false;
            if (pathParameters == null) {
                if (other.getBuilder().pathParameters != null)
                    return false;
            } else if (!pathParameters.equals(other.getBuilder().pathParameters))
                return false;
            if (port != other.getBuilder().port)
                return false;
            if (query == null) {
                if (other.getBuilder().query != null)
                    return false;
            } else if (!query.equals(other.getBuilder().query))
                return false;
            if (resourcePath == null) {
                if (other.getBuilder().resourcePath != null)
                    return false;
            } else if (!resourcePath.equals(other.getBuilder().resourcePath))
                return false;
            if (scheme == null) {
                if (other.getBuilder().scheme != null)
                    return false;
            } else if (!scheme.equals(other.getBuilder().scheme))
                return false;
            if (schemeSpecificPart == null) {
                if (other.getBuilder().schemeSpecificPart != null)
                    return false;
            } else if (!schemeSpecificPart.equals(other.getBuilder().schemeSpecificPart))
                return false;
            if (selectors == null) {
                if (other.getBuilder().selectors != null)
                    return false;
            } else if (!selectors.equals(other.getBuilder().selectors))
                return false;
            if (suffix == null) {
                if (other.getBuilder().suffix != null)
                    return false;
            } else if (!suffix.equals(other.getBuilder().suffix))
                return false;
            if (userInfo == null) {
                if (other.getBuilder().userInfo != null)
                    return false;
            } else if (!userInfo.equals(other.getBuilder().userInfo))
                return false;
            return true;
        }

    }

    /** Iterate over a path by creating shorter segments of that path using "." as a separator.
     * <p>
     * For example, if path = /some/path.a4.html/xyz.ext the sequence is:
     * <ol>
     * <li>/some/path.a4.html/xyz.ext</li>
     * <li>/some/path.a4.html/xyz</li>
     * <li>/some/path.a4</li>
     * <li>/some/path</li>
     * </ol>
     * <p>
     * The root path (/) is never returned. */
    private class ResourcePathIterator implements Iterator<String> {

        // the next path to return, null if nothing more to return
        private String nextPath;

        /** Creates a new instance iterating over the given path
         *
         * @param path The path to iterate over. If this is empty or <code>null</code> this iterator will not return anything. */
        private ResourcePathIterator(String path) {
            if (path == null || path.length() == 0) {
                // null or empty path, there is nothing to return
                nextPath = null;
            } else {
                // find last non-slash character
                int i = path.length() - 1;
                while (i >= 0 && path.charAt(i) == '/') {
                    i--;
                }
                if (i < 0) {
                    // only slashes, assume root node
                    nextPath = "/";
                } else if (i < path.length() - 1) {
                    // cut off slash
                    nextPath = path.substring(0, i + 1);
                } else {
                    // no trailing slash
                    nextPath = path;
                }
            }
        }

        public boolean hasNext() {
            return nextPath != null;
        }

        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            final String result = nextPath;
            // find next path
            int lastDot = nextPath.lastIndexOf('.');
            nextPath = (lastDot > 0) ? nextPath.substring(0, lastDot) : null;

            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

}
