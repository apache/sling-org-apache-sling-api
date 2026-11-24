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
package org.apache.sling.api.servlets;

/**
 * HTTP-related constants
 * <p>
 * This class is not intended to be extended or instantiated because it just
 * provides constants not intended to be overwritten.
 */
public class HttpConstants {

    /**
     * Creates an instance. Use the static constants directly instead.
     */
    public HttpConstants() {}

    /** The HTTP OPTIONS method. */
    public static final String METHOD_OPTIONS = "OPTIONS";
    /** The HTTP GET method. */
    public static final String METHOD_GET = "GET";
    /** The HTTP HEAD method. */
    public static final String METHOD_HEAD = "HEAD";
    /** The HTTP POST method. */
    public static final String METHOD_POST = "POST";
    /** The HTTP PUT method. */
    public static final String METHOD_PUT = "PUT";
    /** The HTTP DELETE method. */
    public static final String METHOD_DELETE = "DELETE";
    /** The HTTP TRACE method. */
    public static final String METHOD_TRACE = "TRACE";
    /** The HTTP CONNECT method. */
    public static final String METHOD_CONNECT = "CONNECT";

    /** The Accept header name. */
    public static final String HEADER_ACCEPT = "Accept";
    /** The ETag header name. */
    public static final String HEADER_ETAG = "ETag";
    /** The If-Match header name. */
    public static final String HEADER_IF_MATCH = "If-Match";
    /** The If-Modified-Since header name. */
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    /** The Last-Modified header name. */
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
}
