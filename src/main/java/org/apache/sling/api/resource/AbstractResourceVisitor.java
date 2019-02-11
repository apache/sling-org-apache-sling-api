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
 * and call the {@link AbstractResourceVisitor#visitAndContinueWithChildren(Resource)} method for each visited resource.
 * It decouples the actual traversal code from application code. 
 * 
 * Concrete subclasses must implement the {@link AbstractResourceVisitor#visit(Resource)} method.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Depth-first_search">Depth-First-Search</a>
 * @since 2.2 (Sling API Bundle 2.2.0)
 */
public abstract class AbstractResourceVisitor {

    /**
     * Visit the given resource and all its descendants in a depth-first approach.
     * @param res The resource
     */
    public void accept(final Resource res) {
        if (res != null && visitAndContinueWithChildren(res)) {
            this.traverseChildren(res.listChildren());
        }
    }

    /**
     * Visit the given resources.
     * @param children The list of resources
     */
    protected void traverseChildren(final @NotNull Iterator<Resource> children) {
        while (children.hasNext()) {
            final Resource child = children.next();

            accept(child);
        }
    }

    /**
     * Implement this method to do actual work on the resources.
     * In most of the cases one should rather override {@link #visitAndContinueWithChildren(Resource)} which allows to limit the traversal.
     * @param res The resource
     */
    protected abstract void visit(final @NotNull Resource res);

    /**
     * Implement this method to do actual work on the resources and to indicate whether to traverse also the given resource's children.
     * The default implementation just calls {@link AbstractResourceVisitor#visit(Resource)} and returns {@code true}
     * @param res The resource
     * @return {@code true} in case the traversal should also cover the children of {@link #res}.
     * @since 2.12.0
     */
    protected boolean visitAndContinueWithChildren(final @NotNull Resource res) {
        visit(res);
        return true;
    }
}
