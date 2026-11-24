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

import org.apache.sling.api.SlingException;

/**
 * The <code>QuerySyntaxException</code> is thrown by the
 * {@link ResourceResolver#findResources(String, String)} and
 * {@link ResourceResolver#queryResources(String, String)} methods if the query
 * syntax is wrong or the requested query language is not available.
 */
public class QuerySyntaxException extends SlingException {

    private static final long serialVersionUID = -6529624886228517646L;

    /** The query that could not be executed. */
    private final String query;

    /** The requested query language. */
    private final String language;

    /**
     * Creates an exception describing an invalid query or unsupported language.
     * @param message The detail message
     * @param query The query
     * @param language The query language
     */
    public QuerySyntaxException(String message, String query, String language) {
        super(message);

        this.query = query;
        this.language = language;
    }

    /**
     * Creates an exception describing an invalid query or unsupported language and its cause.
     * @param message The detail message
     * @param query The query
     * @param language The query language
     * @param cause The underlying cause
     */
    public QuerySyntaxException(String message, String query, String language, Throwable cause) {
        super(message, cause);

        this.query = query;
        this.language = language;
    }

    /**
     * Returns the query that could not be executed.
     * @return The query supplied to the constructor
     */
    public String getQuery() {
        return query;
    }

    /**
     * Returns the requested query language.
     * @return The language supplied to the constructor
     */
    public String getLanguage() {
        return language;
    }
}
