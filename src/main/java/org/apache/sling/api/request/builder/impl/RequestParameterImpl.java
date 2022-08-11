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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.sling.api.request.RequestParameter;

/**
 * Implementation of {@link RequestParameter}.
 */
public class RequestParameterImpl implements RequestParameter {

    private static final String CONTENT_TYPE = "text/plain";

    private final String name;
    private final String value;
    private final Charset encoding;

    public RequestParameterImpl(final String name, final String value) {
        this(name, value, StandardCharsets.UTF_8);
    }

    public RequestParameterImpl(final String name, final String value, final Charset encoding) {
        this.name = name;
        this.value = value;
        this.encoding = encoding;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public byte[] get() {
        return this.value.getBytes(encoding);
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.get());
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public long getSize() {
        return this.get().length;
    }

    @Override
    public String getString() {
        return this.value;
    }

    @Override
    public String getString(final String encoding) throws UnsupportedEncodingException {
        return new String(this.get(), encoding);
    }

    @Override
    public boolean isFormField() {
        return true;
    }

    @Override
    public String toString() {
        return this.getString();
    }
}
