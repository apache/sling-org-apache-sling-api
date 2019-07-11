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

import org.jetbrains.annotations.NotNull;

/**
 * This visitor will traverse the given resource and all its children in a depth-first approach
 * and call the {@link AbstractStoppableDepthFirstResourceVisitor#visit(Resource)} method for each visited resource.
 * It decouples the actual traversal code from application code in its visit method. 
 * 
 * Concrete subclasses must implement only the {@link AbstractStoppableDepthFirstResourceVisitor#visit(Resource)} method.
 * 
 * The difference between this class and {@link AbstractResourceVisitor} is that this class allows to stop
 * the traversal early or skip some parts of the resource tree.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Depth-first_search">Depth-First-Search</a>
 * @since 2.12.0 (Sling API Bundle 2.22.0)
 */
public abstract class AbstractStoppableDepthFirstResourceVisitor {

    /**
     * Defines how the traversal should be continued.
     * <ul>
     * <li>{@link #NORMAL} for regular continuation</li>
     * <li>{@link #SKIP_SUBTREE} to skip the children of the current resource and continue with its siblings</li>
     * <li>{@link #STOP} to stop the whole traversal (i.e. to no visit any other resource afterwards)</li>
     */
    enum TraversalContinuation {
        NORMAL,
        SKIP_SUBTREE,
        STOP
    }

    /**
     * Visit the given resource and all its descendants.
     * @param resource The resource
     * @return {@code false} in case traversal was stopped because one {@link #visit(Resource)} call returned 
     *         {@link TraversalContinuation.STOP} otherwise {@code true}.
     */
    public boolean accept(final @NotNull Resource resource) {
        boolean continueTraversal = true;
        switch(this.visit(resource)) {
            case STOP:
                continueTraversal = false;
                break;
            case NORMAL:
                continueTraversal = this.traverseChildren(resource.listChildren());
                break;
            default:
                break;
        }
        return continueTraversal;
    }

    /**
     * Visit the given resources.
     * @param children The list of resources to traverse.
     * @return {@code false} in case traversal should be stopped because one {@link #visit(Resource)} call returned 
     *         {@link TraversalContinuation.STOP} otherwise {@code true}.
     */
    protected boolean traverseChildren(final @NotNull Iterator<Resource> children) {
        while (children.hasNext()) {
            final Resource child = children.next();
            if (!accept(child)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Implement this method to do actual work on the resources.
     * @param resource The resource
     * @return one of the values of {@link TraversalContinuation} to optionally stop the traversal or 
     * not descend further (i.e. skip this subtree), usually {@link TraversalContinuation#NORMAL} to 
     * continue the depth-first traversal.
     */
    protected abstract @NotNull TraversalContinuation visit(final @NotNull Resource resource);

}
