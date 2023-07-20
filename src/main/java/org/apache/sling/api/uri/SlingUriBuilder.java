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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import org.osgi.annotation.versioning.ProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder for immutable {@link SlingUri}s.
 * <p>
 * Example:
 * 
 * <pre>
 * SlingUri testUri = SlingUriBuilder.create()
 *         .setResourcePath("/test/to/path")
 *         .setSelectors(new String[] { "sel1", "sel2" })
 *         .setExtension("html")
 *         .setSuffix("/suffix/path")
 *         .setQuery("par1=val1&amp;par2=val2")
 *         .build();
 * </pre>
 * <p>
 * 
 * @since 1.0.0 (Sling API Bundle 2.23.0)
 */
@ProviderType
public class SlingUriBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(SlingUriBuilder.class);

    private static final String HTTPS_SCHEME = "https";
    private static final int HTTPS_DEFAULT_PORT = 443;
    private static final String HTTP_SCHEME = "http";
    private static final int HTTP_DEFAULT_PORT = 80;
    private static final String FILE_SCHEME = "file";

    static final String CHAR_HASH = "#";
    static final String CHAR_QM = "?";
    static final char CHAR_AMP = '&';
    static final char CHAR_AT = '@';
    static final char CHAR_SEMICOLON = ';';
    static final char CHAR_EQUALS = '=';
    static final char CHAR_SINGLEQUOTE = '\'';
    static final String CHAR_COLON = ":";
    static final String CHAR_DOT = ".";
    static final String CHAR_SLASH = "/";
    static final String SELECTOR_DOT_REGEX = "\\.(?!\\.?/)"; // (?!\\.?/) to avoid matching ./ and ../
    static final String PATH_PARAMETERS_REGEX = ";([a-zA-z0-9]+)=(?:\\'([^']*)\\'|([^/]+))";
    static final String BEST_EFFORT_INVALID_URI_MATCHER = "^(?:([^:#@]+):)?(?://(?:([^@#]+)@)?([^/#:]+)(?::([0-9]+))?)?(?:([^?#]+))?(?:\\?([^#]*))?(?:#(.*))?$";

    /**
     * Creates a builder without any URI parameters set.
     * 
     * @return a SlingUriBuilder
     */
    @NotNull
    public static SlingUriBuilder create() {
        return new SlingUriBuilder();
    }

    /**
     * Creates a builder from another SlingUri (clone and modify use case).
     * 
     * @param slingUri the Sling URI to clone
     * @return a SlingUriBuilder
     */
    @NotNull
    public static SlingUriBuilder createFrom(@NotNull SlingUri slingUri) {
        return create()
                .setScheme(slingUri.getScheme())
                .setUserInfo(slingUri.getUserInfo())
                .setHost(slingUri.getHost())
                .setPort(slingUri.getPort())
                .setResourcePath(slingUri.getResourcePath())
                .setPathParameters(slingUri.getPathParameters())
                .setSelectors(slingUri.getSelectors())
                .setExtension(slingUri.getExtension())
                .setSuffix(slingUri.getSuffix())
                .setQuery(slingUri.getQuery())
                .setFragment(slingUri.getFragment())
                .setSchemeSpecificPart(slingUri.isOpaque() ? slingUri.getSchemeSpecificPart() : null)
                .setResourceResolver(slingUri instanceof ImmutableSlingUri
                        ? ((ImmutableSlingUri) slingUri).getData().resourceResolver
                        : null);
    }

    /**
     * Creates a builder from a resource (only taking the resource path into account).
     * 
     * @param resource the resource to take the resource path from
     * @return a SlingUriBuilder
     */
    @NotNull
    public static SlingUriBuilder createFrom(@NotNull Resource resource) {
        return create()
                .setResourcePath(resource.getPath())
                .setResourceResolver(resource.getResourceResolver());
    }

    /**
     * Creates a builder from a RequestPathInfo instance .
     * 
     * @param requestPathInfo the request path info to take resource path, selectors, extension and suffix from.
     * @return a SlingUriBuilder
     */
    @NotNull
    public static SlingUriBuilder createFrom(@NotNull RequestPathInfo requestPathInfo) {
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
     * @param request request to take the URI information from
     * @return a SlingUriBuilder
     */
    @NotNull
    public static SlingUriBuilder createFrom(@NotNull SlingHttpServletRequest request) {
        @NotNull
        ResourceResolver resourceResolver = request.getResourceResolver();
        @NotNull
        SlingUriBuilder uriBuilder = createFrom(request.getRequestPathInfo())
                .setResourceResolver(resourceResolver)
                .setScheme(request.getScheme())
                .setHost(request.getServerName())
                .setPort(request.getServerPort())
                .setQuery(request.getQueryString());

        // SLING-11347 - check if the original request was using a mapped path
        @Nullable
        String resourcePath = uriBuilder.getResourcePath();
        if (resourcePath != null) {
            @NotNull
            String mappedResourcePath = resourceResolver.map(request, resourcePath);
            if (!resourcePath.equals(mappedResourcePath) &&
                    request.getPathInfo().startsWith(mappedResourcePath)) {
                // mapped path is different from the resource path and
                // the request path was the mapped path, so switch to it
                uriBuilder.setResourcePath(mappedResourcePath);
            }
        }
        return uriBuilder;
    }

    /**
     * Creates a builder from an arbitrary URI.
     * 
     * @param uri the uri to transform to a SlingUri
     * @param resourceResolver a resource resolver is needed to decide up to what part the path is the resource path (that decision is only
     *        possible by checking against the underlying repository). If null is passed in, the shortest viable resource path is used.
     * @return a SlingUriBuilder
     */
    @NotNull
    public static SlingUriBuilder createFrom(@NotNull URI uri, @Nullable ResourceResolver resourceResolver) {
        String path = uri.getRawPath();
        boolean pathExists = isNotBlank(path);
        String uriQuery = uri.getRawQuery();
        boolean schemeSpecificRelevant = !pathExists && uriQuery == null;
        String uriHost = uri.getHost();
        if (FILE_SCHEME.equals(uri.getScheme()) && uriHost == null) {
            uriHost = ""; // ensure three slashes in file URIs without host
        }
        return create()
                .setResourceResolver(resourceResolver)
                .setScheme(uri.getScheme())
                .setUserInfo(uri.getRawUserInfo())
                .setHost(uriHost)
                .setPort(uri.getPort())
                .setPath(pathExists ? path : null)
                .setQuery(uriQuery)
                .setFragment(uri.getRawFragment())
                .setSchemeSpecificPart(schemeSpecificRelevant ? uri.getRawSchemeSpecificPart() : null);
    }

    /**
     * Creates a builder from an arbitrary URI string.
     * 
     * @param uriStr to uri string to parse
     * @param resourceResolver a resource resolver is needed to decide up to what part the path is the resource path (that decision is only
     *        possible by checking against the underlying repository). If null is passed in, the shortest viable resource path is used.
     * @return a SlingUriBuilder
     */
    @NotNull
    public static SlingUriBuilder parse(@NotNull String uriStr, @Nullable ResourceResolver resourceResolver) {
        URI uri;
        try {
            uri = new URI(uriStr);
            return createFrom(uri, resourceResolver);
        } catch (URISyntaxException e) {
            LOG.debug("Invalid URI {}: {}", uriStr, e.getMessage(), e);
            // best effort to match input, see SlingUriInvalidUrisTest
            return parseBestEffort(uriStr, resourceResolver);
        }
    }

    private static SlingUriBuilder parseBestEffort(String uriStr, ResourceResolver resourceResolver) {
        Matcher matcher = Pattern.compile(BEST_EFFORT_INVALID_URI_MATCHER).matcher(uriStr);
        matcher.find();

        String scheme = matcher.group(1);
        String userInfo = matcher.group(2);
        String host = matcher.group(3);
        String port = matcher.groupCount() >= 4 ? matcher.group(4) : null;
        String path = matcher.groupCount() >= 5 ? matcher.group(5) : null;
        String query = matcher.groupCount() >= 6 ? matcher.group(6) : null;
        String fragment = matcher.groupCount() >= 7 ? matcher.group(7) : null;
        if (!isBlank(scheme) && isBlank(host)) {
            // opaque case
            return create()
                    .setResourceResolver(resourceResolver)
                    .setScheme(scheme)
                    .setSchemeSpecificPart(path)
                    .setFragment(fragment);
        } else if (!isBlank(host) || !isBlank(path)) {
            return create()
                    .setResourceResolver(resourceResolver)
                    .setScheme(scheme)
                    .setUserInfo(userInfo)
                    .setHost(host)
                    .setPort(port != null ? Integer.parseInt(port) : -1)
                    .setPath(path)
                    .setQuery(query)
                    .setFragment(fragment);
        } else {
            return create()
                    .setResourceResolver(resourceResolver)
                    .setSchemeSpecificPart(uriStr);
        }
    }

    // simple helper to avoid StringUtils dependency
    private static boolean isBlank(final CharSequence cs) {
        return cs == null || cs.chars().allMatch(Character::isWhitespace);
    }

    private static boolean isNotBlank(final CharSequence cs) {
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

    // needed for getSuffixResource() from interface RequestPathInfo and rebaseResourcePath()
    private ResourceResolver resourceResolver = null;

    // to ensure a builder is used only once (as the ImmutableSlingUri being created in build() is sharing its state)
    private boolean isBuilt = false;

    private SlingUriBuilder() {
    }

    /**
     * Set the user info of the URI.
     * 
     * @param userInfo the user info
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setUserInfo(@Nullable String userInfo) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.userInfo = userInfo;
        return this;
    }

    /**
     * Set the host of the URI.
     * 
     * @param host the host
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setHost(@Nullable String host) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.host = host;
        return this;
    }

    /**
     * Set the port of the URI.
     * 
     * @param port the port
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setPort(int port) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.port = port;
        return this;
    }

    /**
     * Set the path of the URI that contains a resource path and optionally path parameters, selectors, an extension and a suffix. To remove
     * an existing path set path to {@code null}.
     * 
     * @param path the path
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setPath(@Nullable String path) {
        if (schemeSpecificPart != null) {
            return this;
        }

        // adds path parameters to this.pathParameters and returns path without those
        path = extractPathParameters(path);

        // split in resource path, selectors, extension and suffix
        Matcher dotMatcher;
        if (path != null && path.startsWith(SlingUriBuilder.CHAR_SLASH) && resourceResolver != null) {
            setResourcePath(path);
            rebaseResourcePath();
        } else if (path != null && (dotMatcher = Pattern.compile(SELECTOR_DOT_REGEX).matcher(path)).find()) {
            int firstDotPosition = dotMatcher.start();
            setPathWithDefinedResourcePosition(path, firstDotPosition);
        } else {
            setSelectors(new String[] {});
            setSuffix(null);
            setExtension(null);
            setResourcePath(path);
        }

        return this;
    }

    /**
     * Will rebase the URI based on the underlying resource structure. Rebasing will potentially adjust the
     * {@link #resourcePath}, {@link #selectors}, {@link extension} and {@code suffix} in a way that the path resolves to an existing resource.
     * <p>
     * A resource resolver is necessary for this operation, hence
     * {@link #setResourceResolver(ResourceResolver)} needs to be called before {@link #rebaseResourcePath()} or a create method that implicitly sets this has to be used.
     * 
     * @return the builder for method chaining
     * @throws IllegalStateException
     *             if no resource resolver is available
     */
    @NotNull
    public SlingUriBuilder rebaseResourcePath() {
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
     * Set the resource path of the URI.
     * 
     * @param resourcePath the resource path
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setResourcePath(@Nullable String resourcePath) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.resourcePath = resourcePath;
        return this;
    }

    /**
     * Set the selectors of the URI.
     * Passing in {@code null} has the same effect as passing in an empty array.
     * @param selectors the selectors
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setSelectors(@Nullable String[] selectors) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.selectors.clear();
        if ( selectors != null ) {
            Arrays.stream(selectors).forEach(this.selectors::add);
        }
        return this;
    }

    /**
     * Add a selector to the URI.
     * 
     * @param selector the selector to add
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder addSelector(@NotNull String selector) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.selectors.add(selector);
        return this;
    }

    /**
     * Remove a selector from the URI.
     *
     * @param selector the selector to remove
     * @return the builder for method chaining
     * @since 1.3 (Sling API Bundle 2.25.0)
     */
    @NotNull
    public SlingUriBuilder removeSelector(@NotNull String selector) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.selectors.remove(selector);
        return this;
    }

    /**
     * Set the extension of the URI.
     * 
     * @param extension the extension
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setExtension(@Nullable String extension) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.extension = extension;
        return this;
    }

    /**
     * Set a path parameter to the URI.
     * 
     * @param key the path parameter key
     * @param value the path parameter value
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setPathParameter(@NotNull String key, @NotNull String value) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        this.pathParameters.put(key, value);
        return this;
    }

    /**
     * Replaces all path parameters in the URI.
     * 
     * @param pathParameters the path parameters
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setPathParameters(@NotNull Map<String, String> pathParameters) {
        this.pathParameters.clear();
        this.pathParameters.putAll(pathParameters);
        return this;
    }

    /**
     * Set the suffix of the URI.
     * 
     * @param suffix the suffix
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setSuffix(@Nullable String suffix) {
        if (schemeSpecificPart != null || resourcePath == null) {
            return this;
        }
        if (suffix != null && !suffix.startsWith("/")) {
            throw new IllegalArgumentException("Suffix needs to start with slash");
        }
        this.suffix = suffix;
        return this;
    }

    /**
     * Set the query of the URI.
     * 
     * @param query the query
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setQuery(@Nullable String query) {
        if (schemeSpecificPart != null) {
            return this;
        }
        this.query = query;
        return this;
    }

    /**
     * Add a query parameter to the query of the URI. Key and value are URL-encoded before adding the parameter to the query string.
     * 
     * @param parameterName the parameter name
     * @param value the parameter value
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder addQueryParameter(@NotNull String parameterName, @NotNull String value) {
        if (schemeSpecificPart != null) {
            return this;
        }
        try {
            this.query = (this.query == null ? "" : this.query + CHAR_AMP)
                    + URLEncoder.encode(parameterName, StandardCharsets.UTF_8.name())
                    + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Encoding not supported: " + StandardCharsets.UTF_8, e);
        }
        return this;
    }

    /**
     * <p>
     * Replace all query parameters of the URL. Both keys and values are URL-encoded before adding them to the query string.
     * </p>
     * <p>
     * For adding multiple query parameters with the same name prefer to use {@link #addQueryParameter(String, String)}.
     * </p>
     * 
     * @param queryParameters the map with the query parameters
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setQueryParameters(@NotNull Map<String, String> queryParameters) {
        if (schemeSpecificPart != null) {
            return this;
        }
        setQuery(null); // reset first
        for (Map.Entry<String, String> parameter : queryParameters.entrySet()) {
            addQueryParameter(parameter.getKey(), parameter.getValue());
        }
        return this;
    }

    /**
     * Set the fragment of the URI.
     * 
     * @param fragment the fragment
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setFragment(@Nullable String fragment) {
        this.fragment = fragment;
        return this;
    }

    /**
     * Set the scheme of the URI.
     * 
     * @param scheme the scheme
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setScheme(@Nullable String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * Set the scheme specific part of the URI. Use this for e.g. mail:jon@example.com URIs.
     * 
     * @param schemeSpecificPart the scheme specific part
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setSchemeSpecificPart(@Nullable String schemeSpecificPart) {
        this.schemeSpecificPart = schemeSpecificPart;
        return this;
    }

    /**
     * Will remove scheme and authority (that is user info, host and port).
     * 
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder removeSchemeAndAuthority() {
        setScheme(null);
        setUserInfo(null);
        setHost(null);
        setPort(-1);
        return this;
    }

    /**
     * Will take over scheme and authority (user info, host and port) from provided slingUri.
     * 
     * @param slingUri the Sling URI
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder useSchemeAndAuthority(@NotNull SlingUri slingUri) {
        setScheme(slingUri.getScheme());
        setUserInfo(slingUri.getUserInfo());
        setHost(slingUri.getHost());
        setPort(slingUri.getPort());
        return this;
    }

    /**
     * Returns the resource path.
     * 
     * @return returns the resource path or null if the URI does not contain a path.
     */
    @Nullable
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * Returns the selector string
     * 
     * @return returns the selector string or null if the URI does not contain selector(s) (in line with {@link RequestPathInfo})
     */
    @Nullable
    public String getSelectorString() {
        return !selectors.isEmpty() ? String.join(CHAR_DOT, selectors) : null;
    }

    /**
     * Returns the selectors array.
     * 
     * @return the selectors array (empty if the URI does not contain selector(s), never null)
     */
    @NotNull
    public String[] getSelectors() {
        return selectors.toArray(new String[selectors.size()]);
    }

    /**
     * Returns the extension.
     * 
     * @return the extension or null if the URI does not contain an extension
     */
    @Nullable
    public String getExtension() {
        return extension;
    }

    /**
     * Returns the path parameters.
     * 
     * @return the path parameters or an empty Map if the URI does not contain any
     */
    @Nullable
    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    /**
     * Returns the suffix part of the URI
     * 
     * @return the suffix string or null if the URI does not contain a suffix
     */
    @Nullable
    public String getSuffix() {
        return suffix;
    }

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
    @Nullable
    public Resource getSuffixResource() {
        if (isNotBlank(suffix) && resourceResolver != null) {
            return resourceResolver.getResource(suffix);
        } else {
            return null;
        }
    }

    /**
     * Returns the joint path of resource path, selectors, extension and suffix.
     * 
     * @return the path or null if no path is set
     */
    @Nullable
    public String getPath() {
        return assemblePath(true);
    }

    /**
     * Returns the scheme-specific part of the URI, compare with Javadoc of {@link URI}.
     * 
     * @return scheme specific part of the URI
     */
    @Nullable
    public String getSchemeSpecificPart() {
        if (isOpaque()) {
            return schemeSpecificPart;
        } else {
            return toStringInternal(false, false);
        }
    }

    /**
     * Returns the query.
     * 
     * @return the query part of the URI or null if the URI does not contain a query
     */
    @Nullable
    public String getQuery() {
        return query;
    }

    /**
     * Returns the fragment.
     * 
     * @return the fragment or null if the URI does not contain a fragment
     */
    @Nullable
    public String getFragment() {
        return fragment;
    }

    /**
     * Returns the scheme.
     * 
     * @return the scheme or null if not set
     */
    @Nullable
    public String getScheme() {
        return scheme;
    }

    /**
     * Returns the host.
     * 
     * @return returns the host of the SlingUri or null if not set
     */
    @Nullable
    public String getHost() {
        return host;
    }

    /**
     * Returns the port.
     * 
     * @return returns the port of the SlingUri or -1 if not set
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the user info.
     * 
     * @return the user info of the SlingUri or null if not set
     */
    @Nullable
    public String getUserInfo() {
        return userInfo;
    }

    /**
     * Will take over scheme and authority (user info, host and port) from provided URI.
     * 
     * @param uri the URI
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder useSchemeAndAuthority(@NotNull URI uri) {
        useSchemeAndAuthority(createFrom(uri, resourceResolver).build());
        return this;
    }

    /**
     * Sets the resource resolver (required for {@link RequestPathInfo#getSuffixResource()}).
     * 
     * @param resourceResolver the resource resolver
     * @return the builder for method chaining
     */
    @NotNull
    public SlingUriBuilder setResourceResolver(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        return this;
    }

    /** Builds the immutable SlingUri from this builder.
     * 
     * @return the builder for method chaining */
    @NotNull
    public SlingUri build() {
        if (isBuilt) {
            throw new IllegalStateException("SlingUriBuilder.build() may only be called once per builder instance");
        }
        isBuilt = true;
        return new ImmutableSlingUri();
    }

    /**
     * Builds the corresponding string URI for this builder.
     * 
     * @return string representation of builder
     */
    public String toString() {
        return toStringInternal(true, true);
    }

    /**
     * Returns true the URI is either a relative or absolute path (this is the case if scheme and host is empty and the URI path is set)
     * 
     * @return returns true for path URIs
     */
    public boolean isPath() {
        return isBlank(scheme)
                && isBlank(host)
                && isNotBlank(resourcePath);
    }

    /**
     * Returns true if the URI has an absolute path starting with a slash ('/').
     * 
     * @return true if the URI is an absolute path
     */
    public boolean isAbsolutePath() {
        return isPath() && resourcePath.startsWith(SlingUriBuilder.CHAR_SLASH);
    }

    /**
     * Returns true if the URI is a relative path (no scheme and path does not start with '/').
     * 
     * @return true if URI is a relative path
     */
    public boolean isRelativePath() {
        return isPath() && !resourcePath.startsWith(SlingUriBuilder.CHAR_SLASH);
    }

    /**
     * Returns true the URI is an absolute URI.
     * 
     * @return true if the URI is an absolute URI containing a scheme.
     */
    public boolean isAbsolute() {
        return scheme != null;
    }

    /**
     * Returns true for opaque URIs like e.g. mailto:jon@example.com.
     * 
     * @return true if the URI is an opaque URI
     */
    public boolean isOpaque() {
        return scheme != null && schemeSpecificPart != null;
    }

    private String toStringInternal(boolean includeScheme, boolean includeFragment) {
        StringBuilder requestUri = new StringBuilder();
        
        if (includeScheme && isAbsolute()) {
            requestUri.append(scheme + CHAR_COLON);
        }
        if (host != null) {
            requestUri.append(CHAR_SLASH + CHAR_SLASH);
            if (isNotBlank(userInfo)) {
                requestUri.append(userInfo + CHAR_AT);
            }
            requestUri.append(host);
            if (port > 0
                    && !(HTTP_SCHEME.equals(scheme) && port == HTTP_DEFAULT_PORT)
                    && !(HTTPS_SCHEME.equals(scheme) && port == HTTPS_DEFAULT_PORT)) {
                requestUri.append(CHAR_COLON);
                requestUri.append(port);
            }
        }
        if (schemeSpecificPart != null) {
            requestUri.append(schemeSpecificPart);
        }
        if (resourcePath != null) {
            requestUri.append(assemblePath(true));
        }
        if (query != null) {
            requestUri.append(CHAR_QM + query);
        }
        if (includeFragment && fragment != null) {
            requestUri.append(CHAR_HASH + fragment);
        }
        return requestUri.toString();
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
        // we rebuild the parameters from scratch as given in path (if path is set to null we also reset)
        pathParameters.clear();
        if (path != null) {
            Pattern pathParameterRegex = Pattern.compile(PATH_PARAMETERS_REGEX);

            StringBuffer resultString = null;
            Matcher regexMatcher = pathParameterRegex.matcher(path);
            while (regexMatcher.find()) {
                if (resultString == null) {
                    resultString = new StringBuffer();
                }
                regexMatcher.appendReplacement(resultString, "");
                String key = regexMatcher.group(1);
                String value = isNotBlank(regexMatcher.group(2)) ? regexMatcher.group(2) : regexMatcher.group(3);
                pathParameters.put(key, value);
            }
            if (resultString != null) {
                regexMatcher.appendTail(resultString);
                path = resultString.toString();
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
    private class ImmutableSlingUri implements SlingUri {

        @Override
        public String getResourcePath() {
            return getData().getResourcePath();
        }

        @Override
        public String getSelectorString() {
            return getData().getSelectorString();
        }

        @Override
        public String[] getSelectors() {
            return getData().getSelectors();
        }

        @Override
        public String getExtension() {
            return getData().getExtension();
        }

        @Override
        public Map<String, String> getPathParameters() {
            return Collections.unmodifiableMap(getData().getPathParameters());
        }

        @Override
        public String getSuffix() {
            return getData().getSuffix();
        }

        @Override
        public String getPath() {
            return getData().getPath();
        }

        @Override
        public String getSchemeSpecificPart() {
            return getData().getSchemeSpecificPart();
        }

        @Override
        public String getQuery() {
            return getData().getQuery();
        }

        @Override
        public String getFragment() {
            return getData().getFragment();
        }

        @Override
        public String getScheme() {
            return getData().getScheme();
        }

        @Override
        public String getHost() {
            return getData().getHost();
        }

        @Override
        public int getPort() {
            return getData().getPort();
        }

        @Override
        public Resource getSuffixResource() {
            return getData().getSuffixResource();
        }

        @Override
        public String getUserInfo() {
            return getData().getUserInfo();
        }

        @Override
        public boolean isOpaque() {
            return getData().isOpaque();
        }

        @Override
        public boolean isPath() {
            return getData().isPath();
        }

        @Override
        public boolean isAbsolutePath() {
            return getData().isAbsolutePath();
        }

        @Override
        public boolean isRelativePath() {
            return getData().isRelativePath();
        }

        @Override
        public boolean isAbsolute() {
            return getData().isAbsolute();
        }

        @Override
        public String toString() {
            return getData().toString();
        }

        @Override
        public URI toUri() {
            String uriString = toString();
            try {
                return new URI(uriString);
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Invalid Sling URI: " + uriString, e);
            }
        }

        private SlingUriBuilder getData() {
            return SlingUriBuilder.this;
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
            ImmutableSlingUri other = (ImmutableSlingUri) obj;
            if (extension == null) {
                if (other.getData().extension != null)
                    return false;
            } else if (!extension.equals(other.getData().extension))
                return false;
            if (fragment == null) {
                if (other.getData().fragment != null)
                    return false;
            } else if (!fragment.equals(other.getData().fragment))
                return false;
            if (host == null) {
                if (other.getData().host != null)
                    return false;
            } else if (!host.equals(other.getData().host))
                return false;
            if (pathParameters == null) {
                if (other.getData().pathParameters != null)
                    return false;
            } else if (!pathParameters.equals(other.getData().pathParameters))
                return false;
            if (port != other.getData().port)
                return false;
            if (query == null) {
                if (other.getData().query != null)
                    return false;
            } else if (!query.equals(other.getData().query))
                return false;
            if (resourcePath == null) {
                if (other.getData().resourcePath != null)
                    return false;
            } else if (!resourcePath.equals(other.getData().resourcePath))
                return false;
            if (scheme == null) {
                if (other.getData().scheme != null)
                    return false;
            } else if (!scheme.equals(other.getData().scheme))
                return false;
            if (schemeSpecificPart == null) {
                if (other.getData().schemeSpecificPart != null)
                    return false;
            } else if (!schemeSpecificPart.equals(other.getData().schemeSpecificPart))
                return false;
            if (selectors == null) {
                if (other.getData().selectors != null)
                    return false;
            } else if (!selectors.equals(other.getData().selectors))
                return false;
            if (suffix == null) {
                if (other.getData().suffix != null)
                    return false;
            } else if (!suffix.equals(other.getData().suffix))
                return false;
            if (userInfo == null) {
                if (other.getData().userInfo != null)
                    return false;
            } else if (!userInfo.equals(other.getData().userInfo))
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

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

}
