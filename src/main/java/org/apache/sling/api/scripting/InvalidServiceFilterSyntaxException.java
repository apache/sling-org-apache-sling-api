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
package org.apache.sling.api.scripting;

import org.apache.sling.api.SlingException;

/** Thrown when an invalid service filter is used */
public class InvalidServiceFilterSyntaxException extends SlingException {

    private static final long serialVersionUID = 6557699360505403255L;

    /** The invalid service filter. */
    private final String filter;

    /**
     * Creates an exception describing an invalid service filter.
     * @param filter The invalid filter
     * @param reason The reason the filter is invalid
     */
    public InvalidServiceFilterSyntaxException(String filter, String reason) {
        super(reason);

        this.filter = filter;
    }

    /**
     * Creates an exception describing an invalid service filter and its cause.
     * @param filter The invalid filter
     * @param reason The reason the filter is invalid
     * @param cause The underlying cause
     */
    public InvalidServiceFilterSyntaxException(String filter, String reason, Throwable cause) {
        super(reason, cause);

        this.filter = filter;
    }

    /**
     * Returns the invalid service filter.
     * @return The filter supplied to the constructor
     */
    public String getFilter() {
        return filter;
    }
}
