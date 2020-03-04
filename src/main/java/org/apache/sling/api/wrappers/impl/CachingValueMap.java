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
package org.apache.sling.api.wrappers.impl;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ValueMap decorator that caches key-value pairs that were accessed before.
 *
 * @see ValueMapUtil#cache(org.apache.sling.api.resource.ValueMap)
 */
public class CachingValueMap implements ValueMap {

    private static final String IMMUTABLE_ERROR_MESSAGE = "CachingValueMap is immutable";

    private final ValueMap delegate;

    private final Map<String, Object> cache = new HashMap<>();

    private boolean fullyCached = false;

    public CachingValueMap(ValueMap delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return fullyCached ? cache.size() : delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key) || delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value) || delegate.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return key instanceof String ? cache.computeIfAbsent((String)key, delegate::get) : null;
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        ensureFullyCached();
        return cache.keySet();
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        ensureFullyCached();
        return cache.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        ensureFullyCached();
        return cache.entrySet();
    }

    private void ensureFullyCached() {
        if (!fullyCached) {
            cache.putAll(delegate);
            fullyCached = true;
        }
    }

    @Nullable
    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException(IMMUTABLE_ERROR_MESSAGE);
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException(IMMUTABLE_ERROR_MESSAGE);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
        throw new UnsupportedOperationException(IMMUTABLE_ERROR_MESSAGE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(IMMUTABLE_ERROR_MESSAGE);
    }
}
