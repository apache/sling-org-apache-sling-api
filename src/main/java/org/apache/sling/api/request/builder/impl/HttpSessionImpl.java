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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

/**
 * Internal {@link HttpSession} implementation.
 */
public class HttpSessionImpl implements HttpSession {

    private final ServletContext servletContext;
    private final Map<String, Object> attributeMap = new HashMap<String, Object>();
    private final String sessionID = UUID.randomUUID().toString();
    private final long creationTime = System.currentTimeMillis();
    private boolean invalidated = false;
    private int maxActiveInterval = 1800;

    public HttpSessionImpl(final ServletContext context) {
        this.servletContext = context;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public Object getAttribute(final String name) {
        checkInvalidatedState();
        return this.attributeMap.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkInvalidatedState();
        return Collections.enumeration(this.attributeMap.keySet());
    }

    @Override
    public String getId() {
        return this.sessionID;
    }

    @Override
    public long getCreationTime() {
        checkInvalidatedState();
        return this.creationTime;
    }

    @Override
    public void removeAttribute(final String name) {
        checkInvalidatedState();
        this.attributeMap.remove(name);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        checkInvalidatedState();
        this.attributeMap.put(name, value);
    }

    @Override
    public void invalidate() {
        checkInvalidatedState();
        this.invalidated = true;
    }

    private void checkInvalidatedState() {
        if (invalidated) {
            throw new IllegalStateException("Session is already invalidated.");
        }
    }

    public boolean isInvalidated() {
        return invalidated;
    }

    @Override
    public boolean isNew() {
        checkInvalidatedState();
        return true;
    }

    @Override
    public long getLastAccessedTime() {
        checkInvalidatedState();
        return creationTime;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxActiveInterval;
    }

    @Override
    public void setMaxInactiveInterval(final int interval) {
        this.maxActiveInterval = interval;
    }
}
