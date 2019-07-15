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
 * and call the {@link AbstractResourceVisitor#visit(Resource)} method for each visited resource.
 * It decouples the actual traversal code from application code. 
 * 
 * Concrete subclasses must implement the {@link AbstractResourceVisitor#visit(Resource)} method.
 * There is no possibility to stop traversal in this visitor. If you want to skip certain
 * parts of the subtree or stop traversal at a certain point rather use 
 * {@link org.apache.sling.resource.filter.ResourceStream} or 
 * {@link org.apache.sling.resource.filter.ResourceFilterStream}.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Depth-first_search">Depth-First-Search</a>
 * @since 2.2 (Sling API Bundle 2.2.0)
 */
public abstract class AbstractResourceVisitor {

    /**
     * Visit the given resource and all its descendants.
     * @param res The resource
     */
    public void accept(final Resource res) {
        if (res != null) {
            this.visit(res);
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
     * @param res The resource
     */
    protected abstract void visit(final @NotNull Resource res);
}
