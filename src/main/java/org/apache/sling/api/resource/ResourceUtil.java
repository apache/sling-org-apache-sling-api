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
package org.apache.sling.api.resource;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The <code>ResourceUtil</code> class provides helper methods dealing with
 * resources.
 * <p>
 * This class is not intended to be extended or instantiated because it just
 * provides static utility methods not intended to be overwritten.
 */
public class ResourceUtil {

    private static final Pattern UNICODE_ESCAPE_SEQUENCE_PATTERN = Pattern.compile("\\\\u[0-9a-fA-F]{4}");
    /**
     * Resolves relative path segments '.' and '..' in the path.
     * The path can either be relative or absolute. Relative paths are treated
     * the same as an absolute path.
     * Returns {@code null} if not possible (for example .. points above root).
     *
     * @param path The path to normalize
     * @return The normalized path or {@code null}.
     */
    public static @Nullable String normalize(@NotNull String path) {
        // remove trailing slashes
        path = removeTrailingSlashes(path);
        // don't care for empty paths or just slash
        if (path.isEmpty() || "/".equals(path)) {
            return path;
        }

        // ignore leading slashes
        int startPos = 0;
        while (startPos < path.length() && path.charAt(startPos) == '/') {
            startPos++;
        }

        // split into segments
        final String[] parts = path.substring(startPos).split("/");
        String[] newParts = new String[parts.length];
        int newPartsPos = 0;
        for (final String part : parts) {
            // check each segment for empty and dots
            final int dotCount = countDotsSegment(part);
            if (part.isEmpty() || dotCount == 1) {
                // ignore
            } else if (dotCount == 2) {
                if (newPartsPos == 0) {
                    // can't go above root
                    return null;
                }
                newPartsPos--;
            } else if (dotCount > 2) {
                // invalid
                return null;
            } else {
                newParts[newPartsPos++] = part;
            }
        }
        // nothing changed ?
        if (newPartsPos == newParts.length && startPos < 2) {
            return path;
        }
        // only slash
        if (newPartsPos == 0 && startPos > 0) {
            return "/";
        }
        // reconstruct (we don't use String.join as array elements might be null)
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < newPartsPos; i++) {
            if (i > 0 || startPos > 0) {
                sb.append('/');
            }
            sb.append(newParts[i]);
        }
        return sb.toString();
    }

    /**
     * Remove all trailing slashes except for the root
     */
    private static String removeTrailingSlashes(@NotNull final String path) {
        int endPos = path.length() - 1;
        while (endPos >= 0 && path.charAt(endPos) == '/') {
            endPos--;
        }
        if (endPos == path.length() - 1) {
            return path;
        }
        // only slashes, just return one
        if (endPos == -1) {
            return "/";
        }
        return path.substring(0, endPos + 1);
    }

    /**
     * Return the number of dots in the segment, if the segment only contains dots
     * @param segment The segment
     * @return The number of dots or 0 if the segment contains no dot or other characters.
     */
    private static int countDotsSegment(final String segment) {
        for (int i = 0; i < segment.length(); i++) {
            if (segment.charAt(i) != '.') {
                return 0;
            }
        }
        return segment.length();
    }

    /**
     * Utility method returns the parent path of the given <code>path</code>,
     * which is normalized by {@link #normalize(String)} before resolving the
     * parent.
     *
     * @param path The path whose parent is to be returned.
     * @return <code>null</code> if <code>path</code> is the root path (
     *         <code>/</code>) or if <code>path</code> is a single name
     *         containing no slash (<code>/</code>) characters.
     * @throws IllegalArgumentException If the path cannot be normalized by the
     *             {@link #normalize(String)} method.
     * @throws NullPointerException If <code>path</code> is <code>null</code>.
     */
    public static @Nullable String getParent(@NotNull String path) {
        if ("/".equals(path)) {
            return null;
        }

        // normalize path (remove . and ..)
        path = normalize(path);

        // if normalized to root, there is no parent
        if (path == null || "/".equals(path)) {
            return null;
        }

        String workspaceName = null;

        final int wsSepPos = path.indexOf(":/");
        if (wsSepPos != -1) {
            workspaceName = path.substring(0, wsSepPos);
            path = path.substring(wsSepPos + 1);
        }

        // find the last slash, after which to cut off
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash < 0) {
            // no slash in the path
            return null;
        } else if (lastSlash == 0) {
            // parent is root
            if (workspaceName != null) {
                return workspaceName + ":/";
            }
            return "/";
        }

        String parentPath = path.substring(0, lastSlash);
        if (workspaceName != null) {
            return workspaceName + ":" + parentPath;
        }
        return parentPath;
    }

    /**
     * Utility method returns the ancestor's path at the given <code>level</code>
     * relative to <code>path</code>, which is normalized by {@link #normalize(String)}
     * before resolving the ancestor.
     *
     * <ul>
     * <li><code>level</code> = 0 returns the <code>path</code>.</li>
     * <li><code>level</code> = 1 returns the parent of <code>path</code>, if it exists, <code>null</code> otherwise.</li>
     * <li><code>level</code> = 2 returns the grandparent of <code>path</code>, if it exists, <code>null</code> otherwise.</li>
     * </ul>
     *
     * @param path The path whose ancestor is to be returned.
     * @param level The relative level of the ancestor, relative to <code>path</code>.
     * @return <code>null</code> if <code>path</code> doesn't have an ancestor at the
     *            specified <code>level</code>.
     * @throws IllegalArgumentException If the path cannot be normalized by the
     *             {@link #normalize(String)} method or if <code>level</code> &lt; 0.
     * @throws NullPointerException If <code>path</code> is <code>null</code>.
     * @since 2.2 (Sling API Bundle 2.2.0)
     */
    public static String getParent(final String path, final int level) {
        if (level < 0) {
            throw new IllegalArgumentException("level must be non-negative");
        }
        String result = path;
        for (int i = 0; i < level; i++) {
            result = getParent(result);
            if (result == null) {
                break;
            }
        }
        return result;
    }

    /**
     * Utility method returns the parent resource of the resource.
     *
     * @param rsrc The resource to get the parent of.
     * @return The parent resource or null if the rsrc is the root.
     * @throws NullPointerException If <code>rsrc</code> is <code>null</code>.
     * @throws org.apache.sling.api.SlingException If an error occurs trying to
     *             get the resource object from the path.
     * @throws IllegalStateException if the resource resolver has already been
     *             closed}.
     * @deprecated since 2.1.0, use {@link Resource#getParent()} instead
     */
    @Deprecated
    public static @Nullable Resource getParent(@NotNull Resource rsrc) {
        return rsrc.getParent();
    }

    /**
     * Utility method returns the name of the resource.
     *
     * @param rsrc The resource to get the name from.
     * @return The name of the resource
     * @throws NullPointerException If <code>rsrc</code> is <code>null</code>.
     * @deprecated since 2.1.0, use {@link Resource#getName()} instead
     */
    @Deprecated
    public static @NotNull String getName(@NotNull Resource rsrc) {
        /*
         * Same as AbstractResource.getName() implementation to prevent problems
         * if there are implementations of the pre-2.1.0 Resource interface in
         * the framework.
         */
        return getName(rsrc.getPath());
    }

    /**
     * Utility method returns the name of the given <code>path</code>, which is
     * normalized by {@link #normalize(String)} before resolving the name.
     *
     * @param path The path whose name (the last path element) is to be
     *            returned.
     * @return The empty string if <code>path</code> is the root path (
     *         <code>/</code>) or if <code>path</code> is a single name
     *         containing no slash (<code>/</code>) characters.
     * @throws IllegalArgumentException If the path cannot be normalized by the
     *             {@link #normalize(String)} method.
     * @throws NullPointerException If <code>path</code> is <code>null</code>.
     */
    public static @NotNull String getName(@NotNull String path) {
        Objects.requireNonNull(path, "provided path is null");
        if ("/".equals(path)) {
            return "";
        }

        // normalize path (remove . and ..)
        final String normalizedPath = normalize(path);
        if (normalizedPath == null) {
            throw new IllegalArgumentException(
                    String.format("normalizing path '%s' resolves to a path higher than root", path));
        }
        if ("/".equals(normalizedPath)) {
            return "";
        }

        // find the last slash
        return normalizedPath.substring(normalizedPath.lastIndexOf('/') + 1);
    }

    /**
     * Escapes the given <code>name</code> for use in a resource name. It escapes all invalid characters according to Sling API, i.e.
     * it escapes the slash and names only consisting of dots. It uses Java UTF-16 unicode escape sequences for those characters.
     * @param name
     * @return the escaped name
     * @see ResourceResolver#create(Resource, String, Map)
     * @see SyntheticResource#SyntheticResource(ResourceResolver, String, String)
     * @see <a href="https://www.rfc-editor.org/rfc/rfc5137#section-6.3">RFC 5137, section 6.3</a>
     * @since 2.14.0 (Sling API Bundle 3.0.0)
     */
    public static @NotNull String escapeName(@NotNull String name) {
        if (name.chars().allMatch(c -> c == '.')) {
            return escapeWithUnicode(name, '.');
        }
        return escapeWithUnicode(name, '/');
    }

    /**
     * Unescapes the given <code>escapedName</code> previously escaped using {@link #escapeName(String)}.
     * It replaces the unicode escape sequences with the original characters.
     *
     * @param escapedName The escaped name to unescape.
     * @return The unescaped name.
     * @see Resource#getName()
     * @see <a href="https://www.rfc-editor.org/rfc/rfc5137#section-6.3">RFC 5137, section 6.3</a>
     * @since 2.14.0 (Sling API Bundle 3.0.0)
     */
    public static @NotNull String unescapeName(@NotNull String escapedName) {
        return unescapeWithUnicode(escapedName);
    }

    private static String escapeWithUnicode(String text, Character... additionalCharactersToEscape) {
        List<Character> charactersToEscape = new LinkedList<>();
        charactersToEscape.add('\\'); // always escape the backslash as it used for unicode escaping itself
        charactersToEscape.addAll(Arrays.asList(additionalCharactersToEscape));
        for (Character characterToEscape : charactersToEscape) {
            String escapedChar = getUnicodeEscapeSequence(characterToEscape);
            text = text.replace(characterToEscape.toString(), escapedChar);
        }
        return text;
    }

    private static String getUnicodeEscapeSequence(char c) {
        return String.format("\\u%04X", (int) c);
    }

    private static String unescapeWithUnicode(String escapedText) {
        Matcher matcher = UNICODE_ESCAPE_SEQUENCE_PATTERN.matcher(escapedText);

        StringBuilder decodedString = new StringBuilder();

        while (matcher.find()) {
            String unicodeSequence = matcher.group();
            char unicodeChar = (char) Integer.parseInt(unicodeSequence.substring(2), 16);
            matcher.appendReplacement(decodedString, Character.toString(unicodeChar));
        }
        matcher.appendTail(decodedString);
        return decodedString.toString();
    }

    /**
     * Returns <code>true</code> if the resource <code>res</code> is a synthetic
     * resource.
     * <p>
     * This method checks whether the resource is an instance of the
     * <code>org.apache.sling.resource.SyntheticResource</code> class.
     *
     * @param res The <code>Resource</code> to check whether it is a synthetic
     *            resource.
     * @return <code>true</code> if <code>res</code> is a synthetic resource.
     *         <code>false</code> is returned if <code>res</code> is
     *         <code>null</code> or not an instance of the
     *         <code>org.apache.sling.resource.SyntheticResource</code> class.
     */
    public static boolean isSyntheticResource(@NotNull Resource res) {
        if (res instanceof SyntheticResource) {
            return true;
        }

        if (!(res instanceof ResourceWrapper)) {
            return false;
        }

        do {
            res = ((ResourceWrapper) res).getResource();
        } while (res instanceof ResourceWrapper);

        return res instanceof SyntheticResource;
    }

    /**
     * Returns <code>true</code> if the resource <code>res</code> is a "star
     * resource". A <i>star resource</i> is a resource returned from the
     * <code>ResourceResolver.resolve(HttpServletRequest)</code> whose path
     * terminates in a <code>/*</code>. Generally such resource result from
     * requests to something like <code>/some/path/*</code> or
     * <code>/some/path/*.html</code> which may be used web applications to
     * uniformly handle resources to be created.
     * <p>
     * This method checks whether the resource path ends with a <code>/*</code>
     * indicating such a star resource.
     *
     * @param res The <code>Resource</code> to check whether it is a star
     *            resource.
     * @return <code>true</code> if <code>res</code> is to be considered a star
     *         resource.
     * @throws NullPointerException if <code>res</code> is <code>null</code>.
     */
    public static boolean isStarResource(@NotNull Resource res) {
        return res.getPath().endsWith("/*");
    }

    /**
     * Returns <code>true</code> if the resource <code>res</code> is a
     * non-existing resource.
     * <p>
     * This method checks the resource type of the resource to match the
     * well-known resource type <code>sling:nonexisting</code> of the
     * <code>NonExistingResource</code> class defined in the Sling API.
     *
     * @param res The <code>Resource</code> to check whether it is a
     *            non-existing resource.
     * @return <code>true</code> if <code>res</code> is to be considered a
     *         non-existing resource.
     * @throws NullPointerException if <code>res</code> is <code>null</code>.
     */
    public static boolean isNonExistingResource(@NotNull Resource res) {
        return Resource.RESOURCE_TYPE_NON_EXISTING.equals(res.getResourceType());
    }

    /**
     * Returns an <code>Iterator</code> of {@link Resource} objects loaded from
     * the children of the given <code>Resource</code>.
     * <p>
     * This is a convenience method for
     * {@link ResourceResolver#listChildren(Resource)}.
     *
     * @param parent The {@link Resource Resource} whose children are requested.
     * @return An <code>Iterator</code> of {@link Resource} objects.
     * @throws NullPointerException If <code>parent</code> is <code>null</code>.
     * @throws IllegalStateException if the resource resolver has already been
     *             closed}.
     * @throws org.apache.sling.api.SlingException If any error occurs acquiring
     *             the child resource iterator.
     * @see ResourceResolver#listChildren(Resource)
     * @deprecated since 2.1.0, use {@link Resource#listChildren()} instead
     */
    @Deprecated
    public static @NotNull Iterator<Resource> listChildren(@NotNull Resource parent) {
        // directly call the resource resolver to ensure the correct result
        return parent.getResourceResolver().listChildren(parent);
    }

    /**
     * Returns an <code>ValueMap</code> object for the given
     * <code>Resource</code>. This method calls {@link Resource#getValueMap()}.
     * If <code>null</code> is provided as the resource an empty map is returned as
     * well.
     * <p>For backward compatibility reasons the map returned is not immutable,
     * but it is not recommend to rely on this behavior.</p>
     *
     * @param res The <code>Resource</code> to adapt to the value map.
     * @return A value map.
     */
    public static @NotNull ValueMap getValueMap(final Resource res) {
        if (res == null) {
            // use empty map
            return new ValueMapDecorator(new HashMap<String, Object>());
        }
        return res.getValueMap();
    }

    /**
     * Helper method, which returns the given resource type as returned from the
     * {@link org.apache.sling.api.resource.Resource#getResourceType()} as a
     * relative path.
     *
     * @param type The resource type to be converted into a path
     * @return The resource type as a path.
     * @since 2.0.6 (Sling API Bundle 2.0.6)
     */
    public static @NotNull String resourceTypeToPath(@NotNull final String type) {
        return type.replace(':', '/');
    }

    /**
     * Returns the super type of the given resource type. This method converts
     * the resource type to a resource path by calling
     * {@link #resourceTypeToPath(String)} and uses the
     * <code>resourceResolver</code> to get the corresponding resource. If the
     * resource exists, the {@link Resource#getResourceSuperType()} method is
     * called.
     *
     * @param resourceResolver The <code>ResourceResolver</code> used to access
     *            the resource whose path (relative or absolute) is given by the
     *            <code>resourceType</code> parameter.
     * @param resourceType The resource type whose super type is to be returned.
     *            This type is turned into a path by calling the
     *            {@link #resourceTypeToPath(String)} method before trying to
     *            get the resource through the <code>resourceResolver</code>.
     * @return the super type of the <code>resourceType</code> or
     *         <code>null</code> if the resource type does not exists or returns
     *         <code>null</code> for its super type.
     * @throws IllegalStateException if the resource resolver has already been
     *             closed}.
     * @since 2.0.6 (Sling API Bundle 2.0.6)
     * @deprecated Use {@link ResourceResolver#getParentResourceType(String)}
     */
    @Deprecated
    public static @Nullable String getResourceSuperType(
            final @NotNull ResourceResolver resourceResolver, final String resourceType) {
        return resourceResolver.getParentResourceType(resourceType);
    }

    /**
     * Returns the super type of the given resource. This method checks first if
     * the resource itself knows its super type by calling
     * {@link Resource#getResourceSuperType()}. If that returns
     * <code>null</code> {@link #getResourceSuperType(ResourceResolver, String)}
     * is invoked with the resource type of the resource.
     *
     * @param resource The resource to return the resource super type for.
     * @return the super type of the <code>resource</code> or <code>null</code>
     *         if no super type could be computed.
     * @throws IllegalStateException if the resource resolver has already been
     *             closed}.
     * @since 2.0.6 (Sling API Bundle 2.0.6)
     * @deprecated Use {@link ResourceResolver#getParentResourceType(Resource)}
     */
    @Deprecated
    public static @Nullable String findResourceSuperType(@NotNull final Resource resource) {
        if (resource == null) {
            return null;
        }
        return resource.getResourceResolver().getParentResourceType(resource);
    }

    /**
     * Check if the resource is of the given type. This method first checks the
     * resource type of the resource, then its super resource type and continues
     * to go up the resource super type hierarchy.
     *
     * In case the type of the given resource or the given resource type starts with one of the resource resolver's search paths
     * it is converted to a relative resource type by stripping off the resource resolver's search path
     * before doing the comparison.
     *
     * @param resource the resource to check
     * @param resourceType the resource type to check the resource against
     * @return <code>false</code> if <code>resource</code> is <code>null</code>.
     *         Otherwise returns the result of calling
     *         {@link Resource#isResourceType(String)} with the given
     *         <code>resourceType</code>.
     * @throws IllegalStateException if the resource resolver has already been
     *             closed}.
     * @since 2.0.6 (Sling API Bundle 2.0.6)
     * @deprecated Use {@link ResourceResolver#isResourceType(Resource, String)}
     */
    @Deprecated
    public static boolean isA(@NotNull final Resource resource, final String resourceType) {
        if (resource == null) {
            return false;
        }
        return resource.getResourceResolver().isResourceType(resource, resourceType);
    }

    /**
     * Return an iterator for objects of the specified type. A new iterator is
     * returned which tries to adapt the provided resources to the given type
     * (using {@link Resource#adaptTo(Class)}. If a resource in the original
     * iterator is not adaptable to the given class, this object is skipped.
     * This implies that the number of objects returned by the new iterator
     * might be less than the number of resource objects.
     *
     * @param iterator A resource iterator.
     * @param <T> The adapted type
     * @param type The adapted type
     * @return An iterator of the adapted objects
     * @since 2.0.6 (Sling API Bundle 2.0.6)
     */
    public static @NotNull <T> Iterator<T> adaptTo(final @NotNull Iterator<Resource> iterator, final Class<T> type) {
        return new Iterator<T>() {

            private T nextObject = seek();

            @Override
            public boolean hasNext() {
                return nextObject != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                final T object = nextObject;
                nextObject = seek();
                return object;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            private T seek() {
                T result = null;
                while (result == null && iterator.hasNext()) {
                    final Resource r = iterator.next();
                    result = r.adaptTo(type);
                }
                return result;
            }
        };
    }

    /**
     * Creates or gets the resource at the given path.
     *
     * @param resolver The resource resolver to use for creation
     * @param path     The full path to be created
     * @param resourceType The optional resource type of the final resource to create
     * @param intermediateResourceType THe optional resource type of all intermediate resources
     * @param autoCommit If set to true, a commit is performed after each resource creation.
     * @return The resource for the path.
     * @throws org.apache.sling.api.SlingException If an error occurs trying to
     *             get/create the resource object from the path.
     * @throws IllegalStateException if the resource resolver has already been
     *             closed}.
     * @throws PersistenceException If a persistence error occurs.
     * @since 2.3.0  (Sling API Bundle 2.4.0)
     */
    public static @NotNull Resource getOrCreateResource(
            final @NotNull ResourceResolver resolver,
            final @NotNull String path,
            final String resourceType,
            final String intermediateResourceType,
            final boolean autoCommit)
            throws PersistenceException {
        final Map<String, Object> props;
        if (resourceType == null) {
            props = null;
        } else {
            props = Collections.singletonMap(ResourceResolver.PROPERTY_RESOURCE_TYPE, (Object) resourceType);
        }
        return getOrCreateResource(resolver, path, props, intermediateResourceType, autoCommit);
    }

    /**
     * Creates or gets the resource at the given path.
     * If an exception occurs, it retries the operation up to five times if autoCommit is enabled.
     * In this case, {@link ResourceResolver#revert()} is called on the resolver before the
     * creation is retried.
     *
     * @param resolver The resource resolver to use for creation
     * @param path     The full path to be created
     * @param resourceProperties The optional resource properties of the final resource to create
     * @param intermediateResourceType THe optional resource type of all intermediate resources
     * @param autoCommit If set to true, a commit is performed after each resource creation.
     * @return The resource for the path.
     * @throws org.apache.sling.api.SlingException If an error occurs trying to
     *             get/create the resource object from the path.
     * @throws IllegalStateException if the resource resolver has already been
     *             closed}.
     * @throws PersistenceException If a persistence error occurs.
     * @since 2.3.0  (Sling API Bundle 2.4.0)
     */
    public static @NotNull Resource getOrCreateResource(
            final @NotNull ResourceResolver resolver,
            final @NotNull String path,
            final Map<String, Object> resourceProperties,
            final String intermediateResourceType,
            final boolean autoCommit)
            throws PersistenceException {
        PersistenceException mostRecentPE = null;
        for (int i = 0; i < 5; i++) {
            try {
                return ResourceUtil.getOrCreateResourceInternal(
                        resolver, path, resourceProperties, intermediateResourceType, autoCommit);
            } catch (final PersistenceException pe) {
                if (autoCommit) {
                    // in case of exception, revert to last clean state and retry
                    resolver.revert();
                    resolver.refresh();
                    mostRecentPE = pe;
                } else {
                    throw pe;
                }
            }
        }
        throw mostRecentPE;
    }

    /**
     * Creates or gets the resource at the given path.
     *
     * @param resolver The resource resolver to use for creation
     * @param path     The full path to be created
     * @param resourceProperties The optional resource properties of the final resource to create
     * @param intermediateResourceType THe optional resource type of all intermediate resources
     * @param autoCommit If set to true, a commit is performed after each resource creation.
     * @return The resource for the path.
     * @throws org.apache.sling.api.SlingException If an error occurs trying to
     *             get/create the resource object from the path.
     * @throws IllegalStateException if the resource resolver has already been
     *             closed}.
     * @throws PersistenceException If a persistence error occurs.
     */
    private static Resource getOrCreateResourceInternal(
            final ResourceResolver resolver,
            final String path,
            final Map<String, Object> resourceProperties,
            final String intermediateResourceType,
            final boolean autoCommit)
            throws PersistenceException {
        Resource rsrc = resolver.getResource(path);
        if (rsrc == null) {
            final int lastPos = path.lastIndexOf('/');
            final String name = path.substring(lastPos + 1);

            final Resource parentResource;
            if (lastPos == 0) {
                parentResource = resolver.getResource("/");
            } else {
                final String parentPath = path.substring(0, lastPos);
                parentResource = getOrCreateResource(
                        resolver, parentPath, intermediateResourceType, intermediateResourceType, autoCommit);
            }
            if (autoCommit) {
                resolver.refresh();
            }
            try {
                int retry = 5;
                while (retry > 0 && rsrc == null) {
                    rsrc = resolver.create(parentResource, name, resourceProperties);
                    // check for SNS
                    if (!name.equals(rsrc.getName())) {
                        resolver.refresh();
                        resolver.delete(rsrc);
                        rsrc = resolver.getResource(parentResource, name);
                    }
                    retry--;
                }
                if (rsrc == null) {
                    throw new PersistenceException("Unable to create resource.");
                }
            } catch (final PersistenceException pe) {
                // this could be thrown because someone else tried to create this
                // node concurrently
                resolver.refresh();
                rsrc = resolver.getResource(parentResource, name);
                if (rsrc == null) {
                    throw pe;
                }
            }
            if (autoCommit) {
                try {
                    resolver.commit();
                    resolver.refresh();
                    rsrc = resolver.getResource(parentResource, name);
                } catch (final PersistenceException pe) {
                    // try again - maybe someone else did create the resource in the meantime
                    // or we ran into Jackrabbit's stale item exception in a clustered environment
                    resolver.revert();
                    resolver.refresh();
                    rsrc = resolver.getResource(parentResource, name);
                    if (rsrc == null) {
                        rsrc = resolver.create(parentResource, name, resourceProperties);
                        resolver.commit();
                    }
                }
            }
        }
        return rsrc;
    }

    /**
     * Create a unique name for a child of the <code>parent</code>.
     * Creates a unique name and test if child already exists.
     * If child resource with the same name exists, iterate until a unique one is found.
     *
     * @param parent The parent resource
     * @param name   The name of the child resource
     * @return a unique non-existing name for child resource for a given <code>parent</code>
     *
     * @throws PersistenceException if it can not find unique name for child resource.
     * @throws NullPointerException if <code>parent</code> is null
     * @throws IllegalStateException if the resource resolver has already been
     *             closed}.
     * @since 2.5 (Sling API Bundle 2.7.0)
     */
    public static String createUniqueChildName(final Resource parent, final String name) throws PersistenceException {
        if (parent.getChild(name) != null) {
            // leaf node already exists, create new unique name
            String childNodeName = null;
            int i = 0;
            do {
                childNodeName = name + String.valueOf(i);
                // just so that it does not run into an infinite loop
                // this should not happen though :)
                if (i == Integer.MAX_VALUE) {
                    String message =
                            MessageFormat.format("can not find a unique name {0} for {1}", name, parent.getPath());
                    throw new PersistenceException(message);
                }
                i++;
            } while (parent.getChild(childNodeName) != null);

            return childNodeName;
        }
        return name;
    }

    /**
     * Unwrap the resource and return the wrapped implementation.
     * @param rsrc The resource to unwrap
     * @return The unwrapped resource
     * @since 2.5  (Sling API Bundle 2.7.0)
     */
    public static @NotNull Resource unwrap(final @NotNull Resource rsrc) {
        Resource result = rsrc;
        while (result instanceof ResourceWrapper) {
            result = ((ResourceWrapper) result).getResource();
        }
        return result;
    }

    /**
     * A batch resource remover deletes resources in batches. Once the batch
     * size (threshold) is reached, an intermediate commit is performed. Resource
     * trees are deleted resource by resource starting with the deepest children first.
     * Once all resources have been passed to the batch resource remover, a final
     * commit needs to be called on the resource resolver.
     * @since 2.6  (Sling API Bundle 2.8.0)
     */
    public static class BatchResourceRemover {

        private final int max;

        private int count;

        public BatchResourceRemover(final int batchSize) {
            this.max = (batchSize < 1 ? 50 : batchSize);
        }

        public void delete(@NotNull final Resource rsrc) throws PersistenceException {
            final ResourceResolver resolver = rsrc.getResourceResolver();
            for (final Resource child : rsrc.getChildren()) {
                delete(child);
            }
            resolver.delete(rsrc);
            count++;
            if (count >= max) {
                resolver.commit();
                count = 0;
            }
        }
    }

    /**
     * Create a batch resource remover.
     * A batch resource remove can be used to delete resources in batches.
     * Once the passed in threshold of deleted resources is reached, an intermediate
     * commit is called on the resource resolver. In addition the batch remover
     * deletes a resource recursively.
     * Once all resources to delete are passed to the remover, a final commit needs
     * to be call on the resource resolver.
     * @param threshold The threshold for the intermediate saves.
     * @return A new batch resource remover.
     * Since 2.6
     */
    public static @NotNull BatchResourceRemover getBatchResourceRemover(final int threshold) {
        return new BatchResourceRemover(threshold);
    }
}
