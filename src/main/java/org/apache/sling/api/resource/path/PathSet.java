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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.sling.api.resource.ResourceUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Simple helper class for path matching against a set of paths.
 *
 * @since 1.0.0 (Sling API Bundle 2.11.0)
 */
public class PathSet implements Iterable<Path> {

    /** Empty path set. */
    public static final PathSet EMPTY_SET = new PathSet(Collections.emptySet());

    /**
     * When a PathSet is created it gets optimized. For small Sets optimizing
     * by iterating with O(n^2) is more efficient then building and traversing
     * a tree with O(n+m), with m being the maximum depth of the tree. For
     * setting the boundary a max depth of 6 was assumed, which theoretically
     * results in the same time complexity for both algorithms.
     */
    private static final int SMALL_SET_SIZE = 3;

    /**
     * Create a path set from a collection of path objects
     * @param paths The collection of path objects
     * @return The path set
     */
    public static PathSet fromPathCollection(final Collection<Path> paths) {
        return create(paths, paths.size());
    }

    /**
     * Create a path set from a collection of path objects
     * @param paths The collection of path objects
     * @return The path set
     */
    public static PathSet fromPaths(final Path...paths) {
        return fromPathCollection(Arrays.asList(paths));
    }

    /**
     * Create a path set from a collection of strings
     * @param paths The collection of strings
     * @return The path set
     */
    public static PathSet fromStringCollection(final Collection<String> paths) {
        return create(paths.stream().map(Path::new)::iterator, paths.size());
    }

    /**
     * Create a path set from a collection of strings
     * @param strings The array of strings
     * @return The path set
     */
    public static PathSet fromStrings(final String...strings) {
        return fromStringCollection(Arrays.asList(strings));
    }

    /**
     * Create a new PathSet either as a small Set, optimized in O(n^2) or using a tree-like builder for larger Sets.
     *
     * @param paths an iterable of paths to add to the PathSet
     * @param size  the number of paths in the iterable
     * @return
     */
    private static PathSet create(final Iterable<Path> paths, int size) {
        if (size <= SMALL_SET_SIZE) {
            Set<Path> simpleSet = new HashSet<>();
            paths.forEach(simpleSet::add);
            optimize(simpleSet);
            return new PathSet(simpleSet);
        } else {
            Builder builder = new Builder();
            builder.addAll(paths);
            return builder.build();
        }
    }

    /**
     * Optimize the set by filtering out paths which are a sub path
     * of another path in the set.
     * @param set The path set
     */
    private static void optimize(final Set<Path> set) {
        final Iterator<Path> i = set.iterator();
        while ( i.hasNext() ) {
            final Path next = i.next();
            boolean found = false;
            for(final Path p : set) {
                if ( p != next && p.matches(next.getPath()) ) {
                    found = true;
                    break;
                }
            }
            if ( found ) {
                i.remove();
            }
        }
    }

    private final Collection<Path> paths;

    /**
     * Create a path set from a set of paths
     * @param paths A set of paths
     */
    private PathSet(final Collection<Path> paths) {
        this.paths = paths;
    }

    /**
     * Check whether the provided path is in the sub tree of any
     * of the paths in this set.
     * @param otherPath The path to match
     * @return The path which matches the provided path, {@code null} otherwise.
     * @see Path#matches(String)
     */
    public Path matches(final String otherPath) {
         for(final Path p : this.paths) {
             if ( p.matches(otherPath) ) {
                 return p;
             }
         }
         return null;
    }

    /**
     * Generate a path set of paths from this set which
     * are in the sub tree of the provided path
     * @param path The base path
     * @return Path set, might be empty
     */
    public PathSet getSubset(final String path) {
        return getSubset(new Path(path));
    }

    /**
     * Generate a path set of paths from this set which
     * are in the sub tree of the provided path
     * @param path The base path
     * @return Path set, might be empty
     * @since 1.2.0 (Sling API Bundle 2.15.0)
     */
    public PathSet getSubset(final Path path) {
        final Set<Path> result = new HashSet<Path>();
        for(final Path p : this.paths) {
            if ( path.matches(p.getPath()) ) {
                result.add(p);
            }
        }
        return new PathSet(result);
    }

    /**
     * Generate a path set of paths from this set which
     * are in at least one of the sub tree of the provided path set.
     * @param set The base path set
     * @return Path set
     */
    public PathSet getSubset(final PathSet set) {
        final Set<Path> result = new HashSet<Path>();
        for(final Path p : this.paths) {
            if ( set.matches(p.getPath()) != null ) {
                result.add(p);
            }
        }
        return new PathSet(result);
    }

    /**
     * Create a unmodifiable set of strings
     * @return A set of strings
     */
    public Set<String> toStringSet() {
        final Set<String> set = new HashSet<String>();
        for(final Path p : this) {
            set.add(p.getPath());
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     * Return an unmodifiable iterator for the paths.
     * @return An iterator for the paths
     */
    @Override
    public Iterator<Path> iterator() {
        return Collections.unmodifiableCollection(this.paths).iterator();
    }

    @Override
    public int hashCode() {
        return paths.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof PathSet)) {
            return false;
        }
        return this.paths.equals(((PathSet)obj).paths);
    }

    @Override
    public String toString() {
        return "PathSet [paths=" + paths + "]";
    }

    /**
     * Utility method returns all parents recursively of the given
     * <code>path</code>, which is normalized by
     * {@link ResourceUtil#normalize(String)} before resolving the parent. The
     * ancestors are returned ordered parent-first, so that the first element
     * in the resulting list is always the root path (<code>/</code>) and test
     * last is the given <code>path</code> itself.
     *
     * @param path The path whose ancestors are to be returned
     * @return a list of all ancestors of the given <code>path</code> starting
     *         with the root path (<code>/</code>) and ending with the given
     *         <code>path</code> itself.
     * @throws IllegalArgumentException If the path cannot be normalized by the
     *             {@link ResourceUtil#normalize(String)} method.
     * @throws NullPointerException If <code>path</code> is <code>null</code>.
     */
    private static @NotNull List<String> getAncestors(@NotNull String path) {
        List<String> parts = new LinkedList<>();
        path = ResourceUtil.normalize(path);
        for (String parent = path; parent != null && !"/".equals(parent); parent = ResourceUtil.getParent(parent)) {
            parts.add(parent);
        }
        Collections.reverse(parts);
        return parts;
    }

    /**
     * Traverses the given subtree by creating a {@link Stream} that emits the
     * parent before child axis.
     *
     * @param node the subtree given as it's root {@link Node}.
     * @return a {@link Stream} consisting of all the Nodes in the sub tree
     */
    private static Stream<Node> traverse(Node node) {
        return traverse(node, null);
    }

    /**
     * Traverses the given subtree by creating a {@link Stream} that emits the
     * parent before child axis. If not null the {@link Predicate<Node>} can
     * be used to stop any deeper traversal at a particular {@link Node}.
     *
     * @param node the subtree given as it's root {@link Node}.
     * @return a {@link Stream} consisting of all the Nodes in the sub tree
     */
    private static Stream<Node> traverse(Node node, Predicate<Node> stopAfter) {
        if (node == null) {
            return Stream.empty();
        }

        Stream<Node> self = Stream.of(node);
        if (node.children != null && !node.children.isEmpty() && (stopAfter == null || !stopAfter.test(node))) {
            return Stream.concat(self, node.children.values().stream().flatMap(child -> traverse(child, stopAfter)));
        } else {
            return self;
        }
    }

    /**
     * A tree-like builder that leverages the known hierarchy information of
     * {@link Path}s in order to optimize the resulting PathSet on the fly in
     * O(n+m). This comes on a cost and so this class should only used for
     * {@link Set}s of {@link Path} that are larger then a small set.
     *
     * @see PathSet#SMALL_SET_SIZE
     */
    private static class Builder {

        /**
         * The data structures of the Builder's state are lazily initialised.
         */
        private Node root;
        private Set<Path> patterns;

        /**
         * Adds all given {@link Path}s to the {@link Builder}
         *
         * @param paths an collection of paths
         * @throws NullPointerException when the collection or any of the
         *              contained {@link Path}s is null.
         */
        private void addAll(@NotNull Iterable<Path> paths) {
            paths.forEach(this::add);
        }

        /**
         * Adds a single {@link Path} to the {@link Builder}.
         *
         * @param path the <code>path</code> to be added
         * @throws NullPointerException when path is <code>null</code>
         */
        private void add(Path path) {
            if (path.isPattern()) {
                if (patterns == null) {
                    patterns = new HashSet<>();
                }
                if (patterns.add(path) && root != null) {
                    // remove all that match the added pattern from the current tree
                    // create a temporary copy to prevent concurrent modification of the base collection(s)
                    Predicate<Node> condition = node -> path.matches(node.path);
                    traverse(root, condition).filter(condition).collect(Collectors.toList()).forEach(Node::remove);
                }
            } else {
                if (patterns != null && patterns.stream().anyMatch(pattern -> pattern.matches(path.getPath()))) {
                    return;
                }
                if (root == null) {
                    root = new Node("/");
                }

                Node current = root;
                for (String part : getAncestors(path.getPath())) {
                    // an ancestor of currently added Path is already included, skipping
                    if (current.isIncluded()) {
                        return;
                    }
                    current = current.addChild(part);
                }
                current.setPayload(path);
            }
        }

        /**
         * Returns a {@link PathSet} from the {@link Builder}'s current state,
         * resetting the Builder afterwards.
         *
         * @return an optimized {@link PathSet}
         */
        private PathSet build() {
            try {
                if (patterns == null && root == null) {
                    return PathSet.EMPTY_SET;
                } else if (patterns != null && root == null) {
                    // if we only have patterns the set is already optimized.
                    // According to Path#matches(String) two patterns only
                    // match when they are equal. For at set of patterns this
                    // condition is always met for all elements in the Set.
                    return new PathSet(patterns);
                } else {
                    Stream<Path> paths = traverse(root).filter(Node::isIncluded).map(Node::getPayload);
                    if (patterns != null) {
                        paths = Stream.concat(patterns.stream(), paths);
                    }
                    return new PathSet(paths.collect(Collectors.toList()));
                }
            } finally {
                patterns = null;
                root = null;
            }
        }
    }

    /**
     * A simple implementation of a node in a tree. This class is used to
     * build the optimized {@link PathSet} in {@link Builder}.
     */
    private static class Node {
        private final String path;
        private Node parent;
        private Path payload;
        private Map<String, Node> children;

        private Node(String path) {
            this.path = path;
        }

        private boolean isIncluded() {
            return payload != null;
        }

        private Node addChild(String path) {
            if (children == null) {
                children = new HashMap<>();
            }
            Node child = children.computeIfAbsent(path, Node::new);
            child.parent = this;
            return child;
        }

        private void remove() {
            if (parent != null) {
                parent.children.remove(path);
            }
        }

        private Path getPayload() {
            return payload;
        }

        public void setPayload(Path payload) {
            this.payload = payload;
            this.children = null;
        }
    }
}