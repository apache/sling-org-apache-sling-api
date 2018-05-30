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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

/**
 * Create a Stream<Resource> of Resource objects from a managed traversal of a
 * Resource tree
 *
 */
public class ResourceStream {

    private Resource resource;

    private long limit = 0;

    private long startOfRange;

    // determine if a given child of a resource should be traversed
    private Predicate<Resource> branchSelector = resource -> true;

    // determine if a given resource should be added to the stream
    private Predicate<Resource> resourceSelector = resource -> true;

    /**
     * Base resource for traversal
     * 
     * @param resource
     *            traversal starting point
     * @return new instance of ResourceQuery;
     */
    public static ResourceStream from(@Nonnull Resource resource) {
        return new ResourceStream(resource);
    }

    /*
     * Constructor to establish the starting resource
     * 
     * @param resource
     */
    protected ResourceStream(Resource resource) {
        this.resource = Objects.requireNonNull(resource);
    }

    /**
     * Sets the maximum number of items to be returned. Starting from the first
     * matching resource. This method is mutually exclusive to the range method
     * 
     * @param number
     *            maximum number of items to be returned
     * @return ResourceStream
     */
    public ResourceStream limit(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("value may not be negative");
        }
        this.startOfRange = 0;
        this.limit = number;
        return this;
    }

    /**
     * Sets the maximum number of items to be returned. Starting from the nth
     * identified resource as defined by the startOfRange. This method is mutually
     * exclusive to the limit method
     * 
     * @param startOfRange
     *            index of the first resource to be returned
     * @param limit
     *            maximum number of resources to be returned
     * @return ResourceStream
     */
    public ResourceStream range(long startOfRange, long limit) {
        if (startOfRange < 0 || limit < 0) {
            throw new IllegalArgumentException("value may not be negative");
        }
        this.startOfRange = startOfRange;
        this.limit = limit;
        return this;
    }

    /**
     * Resets the starting path for the tree traversal.
     * 
     * @param path
     *            set the internal resource to path
     * @return ResourceStream
     */
    public ResourceStream startingFrom(String path) {
        this.resource = Objects.requireNonNull(resource.getResourceResolver().getResource(path));
        return this;
    }

    /**
     * Predicate used to select child resources for traversal
     * 
     * @param branchSelector
     *            predicate for traversal control
     * @return ResourceStream
     */
    public ResourceStream setBranchSelector(Predicate<Resource> branchSelector) {
        this.branchSelector = Objects.requireNonNull(branchSelector);
        return this;
    }

    /**
     * Sets a resource selector which defines whether a given resource should be
     * part of the stream
     * 
     * @param resourceSelector
     *            identifies resource for Stream<Resource>
     * @return ResourceStream
     */
    public ResourceStream setResourceSelector(Predicate<Resource> resourceSelector) {
        this.resourceSelector = Objects.requireNonNull(resourceSelector);
        return this;
    }

    /**
     * Provides a stream of resources starting from the initiator resource and
     * traversing through it's descendants
     * 
     * @return self closing {@code Stream<Resource>} of unknown size.
     */
    public Stream<Resource> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<Resource>() {

            private final LinkedList<Resource> resourcesToCheck = new LinkedList<>();

            {
                resourcesToCheck.addFirst(resource);
            }

            Resource current;

            @Override
            public boolean hasNext() {
                do {
                    do {
                        if (resourcesToCheck.isEmpty()) {
                            return false;
                        }

                        current = resourcesToCheck.removeFirst();
                        int index = 0;
                        for (Resource child : current.getChildren()) {
                            if (branchSelector.test(child)) {
                                resourcesToCheck.add(index++, child);
                            }
                        }
                    } while (!resourceSelector.test(current));
                    if (startOfRange > 0) {
                        --startOfRange;
                    }
                    if (limit > 0 && startOfRange == 0) {
                        if (--limit == 0) {
                            resourcesToCheck.clear();
                        }
                    }
                } while (startOfRange > 0);
                return true;
            }

            @Override
            public Resource next() {
                return current;
            }
        }, Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
    }

    /**
     * Perform the consumer on each Resource in the defined Stream
     * 
     * @param consumer
     */
    public void forEach(Consumer<Resource> consumer) {
        stream().forEach(consumer);
    }
}
