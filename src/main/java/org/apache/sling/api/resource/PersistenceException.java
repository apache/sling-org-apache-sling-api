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

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * This exception will be thrown during the commit to persist
 * changes to a {@link PersistableValueMap}, a
 * {@link ModifiableValueMap} or the {@link ResourceResolver}.
 */
public class PersistenceException extends IOException {

    private static final long serialVersionUID = 2454225989618227698L;

    private static final Set<Character> PUNCTUATION = Set.of('.', ':', '!');

    /** Optional resource path. */
    private final String resourcePath;

    /** Optional property name. */
    private final String propertyName;

    /**
     * Create a new persistence exception.
     */
    public PersistenceException() {
        this(null, null, null, null);
    }

    /**
     * Create a new persistence exception.
     * @param msg Exception message.
     */
    public PersistenceException(final String msg) {
        this(msg, null, null, null);
    }

    /**
     * Create a new persistence exception.
     * @param msg Exception message.
     * @param cause Exception cause.
     */
    public PersistenceException(final String msg, final Throwable cause) {
        this(msg, cause, null, null);
    }

    /**
     * Create a new persistence exception.
     * @param msg Exception message.
     * @param cause Exception cause.
     * @param resourcePath The optional resource path
     * @param propertyName The optional property name
     */
    public PersistenceException(
            final String msg, final Throwable cause, final String resourcePath, final String propertyName) {
        super(msg);
        initCause(cause);
        this.resourcePath = resourcePath;
        this.propertyName = propertyName;
    }

    /**
     * Get the resource path related to this exception.
     * @return The resource path or <code>null</code>
     * @since 2.2  (Sling API Bundle 2.2.0)
     */
    public String getResourcePath() {
        return this.resourcePath;
    }

    /**
     * Get the property name related to this exception.
     * @return The property name or <code>null</code>
     * @since 2.2  (Sling API Bundle 2.2.0)
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * Returns a message for this exception.
     * If the cause is not null, it will be appended to the message.
     * @return The message for this exception
     */
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if (this.getCause() != null && !Objects.equals(this.getCause().getMessage(), super.getMessage())) {
            sb.append(stripTailingPunctuation(super.getMessage()))
                    .append(" caused by ")
                    .append(this.getCause().getMessage());
        } else {
            sb.append(super.getMessage());
        }

        return sb.toString();
    }

    private static String stripTailingPunctuation(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int len = str.length();
        if (len > 0 && PUNCTUATION.contains(str.charAt(len - 1))) {
            return str.substring(0, len - 1);
        }
        return str;
    }
}
