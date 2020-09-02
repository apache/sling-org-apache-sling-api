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
package org.apache.sling.api.wrappers;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.impl.ObjectConverter;
import org.jetbrains.annotations.NotNull;

/**
 * <code>ValueMapDecorator</code> decorates another {@link Map}
 * to provide a basic implementation for the additional methods
 * of a {@link ValueMap}.
 */
public class ValueMapDecorator implements ValueMap {

    /**
     * underlying map
     */
    private final Map<String, Object> base;

    /**
     * Creates a new wrapper around a given map.
     * @param base wrapped object
     */
    public ValueMapDecorator(Map<String, Object> base) {
        this.base = base;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull String name, @NotNull Class<T> type) {
        if (base instanceof ValueMap) {
            // shortcut if decorated map is ValueMap
            return ((ValueMap)base).get(name, type);
        }
        Object value = get(name);
        if (value == null) {
            return null;
        }
        if (type.isAssignableFrom(value.getClass())) {
            return (T)value;
        }
        return ObjectConverter.convert(value, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull String name, @NotNull T defaultValue) {
        if (base instanceof ValueMap) {
            // shortcut if decorated map is ValueMap
            return ((ValueMap)base).get(name, defaultValue);
        }
        if (defaultValue == null) {
            return (T) get(name);
        }
        T value = (T) get(name, defaultValue.getClass());
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return base.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return base.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object value) {
        return base.containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        return base.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object put(String key, Object value) {
        return base.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object remove(Object key) {
        return base.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<? extends String, ?> t) {
        base.putAll(t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        base.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> keySet() {
        return base.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Object> values() {
        return base.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return base.entrySet();
    }

    @Override
    public String toString() {
        return super.toString() + " : " + this.base.toString();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return base.hashCode();
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        return base.equals(obj);
    }


}
