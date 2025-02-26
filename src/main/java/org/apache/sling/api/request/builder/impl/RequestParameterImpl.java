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
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of {@link RequestParameter}.
 */
public class RequestParameterImpl implements RequestParameter {

    private final @NotNull String name;
    private final byte @NotNull [] value;
    private final @NotNull Charset encoding;
    private final String contentType;
    private final String fileName;
    private final boolean isFormField;

    public RequestParameterImpl(@NotNull final String name, @NotNull final String value) {
        this(name, value, StandardCharsets.UTF_8);
    }

    public RequestParameterImpl(
            @NotNull final String name, @NotNull final String value, @NotNull final Charset encoding) {
        this(
                name,
                value.getBytes(encoding),
                encoding,
                null,
                null,
                true); // by default application/x-www-form-urlencoded is returning null for content type in Sling
        // Engine
    }

    public RequestParameterImpl(
            @NotNull final String name, byte @NotNull [] value, String fileName, String contentType) {
        this(name, value, StandardCharsets.UTF_8, fileName, contentType, false);
    }

    private RequestParameterImpl(
            @NotNull final String name,
            byte @NotNull [] value,
            @NotNull final Charset encoding,
            String fileName,
            final String contentType,
            boolean isFormField) {
        this.name = name;
        this.value = value;
        this.contentType = contentType;
        this.encoding = encoding;
        this.fileName = fileName;
        this.isFormField = isFormField;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public byte[] get() {
        return this.value;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.get());
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public long getSize() {
        return this.get().length;
    }

    @Override
    public @NotNull String getString() {
        return new String(this.value, encoding);
    }

    @Override
    public @NotNull String getString(final @NotNull String encoding) throws UnsupportedEncodingException {
        return new String(this.value, encoding);
    }

    @Override
    public boolean isFormField() {
        return isFormField;
    }

    @Override
    public String toString() {
        return this.getString();
    }
}
