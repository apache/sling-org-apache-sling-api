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
package org.apache.sling.api.request.builder.impl;

import org.apache.sling.api.request.builder.SlingHttpServletResponseBuilder;
import org.apache.sling.api.request.builder.SlingJakartaHttpServletResponseResult;
import org.jetbrains.annotations.NotNull;

/**
 * Internal {@link SlingHttpServletResponseBuilder} implementation.
 */
public class SlingHttpServletResponseBuilderImpl implements SlingHttpServletResponseBuilder {

    /** Is the builder locked? */
    private boolean locked = false;

    private void checkLocked() {
        if (locked) {
            throw new IllegalStateException("The builder can't be reused. Create a new builder instead.");
        }
    }

    @Override
    @Deprecated
    public @NotNull org.apache.sling.api.request.builder.SlingHttpServletResponseResult build() {
        this.checkLocked();
        this.locked = true;
        return new SlingHttpServletResponseResultImpl();
    }

    @Override
    public @NotNull SlingJakartaHttpServletResponseResult buildJakartaResponseResult() {
        this.checkLocked();
        this.locked = true;
        return new SlingJakartaHttpServletResponseResultImpl();
    }
}
