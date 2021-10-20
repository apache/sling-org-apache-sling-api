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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage HTTP headers for request and response.
 */
public class HeaderSupport {

    private static final DateTimeFormatter RFC_1123_DATE_TIME = DateTimeFormatter.RFC_1123_DATE_TIME;

    private final Map<String, List<String>> headers = new LinkedHashMap<>();

    public void addHeader(final String name, final String value) {
        this.headers.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
    }

    public void addIntHeader(final String name, final int value) {
        this.addHeader(name, Integer.toString(value));
    }

    public void addDateHeader(final String name, final long date) {
        final Date d = new Date(date);
        this.addHeader(name, RFC_1123_DATE_TIME.format(d.toInstant().atOffset(ZoneOffset.UTC)));
    }

    public void setHeader(final String name, final String value) {
        removeHeaders(name);
        addHeader(name, value);
    }

    public void setIntHeader(final String name, final int value) {
        removeHeaders(name);
        addIntHeader(name, value);
    }

    public void setDateHeader(final String name, final long date) {
        removeHeaders(name);
        addDateHeader(name, date);
    }

    private void removeHeaders(final String name) {
        this.headers.remove(name);
    }

    public boolean containsHeader(final String name) {
        return this.headers.get(name) != null;
    }

    public String getHeader(final String name) {
        final List<String> values = this.headers.get(name);
        if ( values == null ) {
            return null;
        }
        return values.get(0);
    }

    public int getIntHeader(String name) {
        final String value = getHeader(name);
        if ( value != null ) {
            return Integer.valueOf(value);
        }
        return -1;
    }

    public long getDateHeader(final String name) {
        final String value = getHeader(name);
        if ( value != null ) {
            try {                
                final Date date = Date.from(ZonedDateTime.parse(value, RFC_1123_DATE_TIME).toInstant());
                return date.getTime();
            } catch (final DateTimeParseException ex) {
                throw new IllegalArgumentException("Invalid date value: " + value, ex);                
            }
        }
        return -1L;
    }

    public Collection<String> getHeaders(final String name) {
        final List<String> values = new ArrayList<String>();
        final List<String> headers = this.headers.get(name);
        if ( headers != null ) {
            values.addAll(headers);
        }
        return values;
    }

    public Collection<String> getHeaderNames() {
        return this.headers.keySet();
    }

    public void reset() {
        this.headers.clear();
    }
}
