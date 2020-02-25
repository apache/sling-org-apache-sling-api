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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FallbackValueMap implements ValueMap {
    List<Resource> resources;

    Set<Entry<String, Object>> entrySet;
    Set<String> keySet;
    Set<String> accessed = new HashSet<>();
    boolean cacheFilled = false;
    Collection<Object> values;
    ValueMap cache = new ValueMapDecorator(new HashMap<>());

    private static final String EMPTY = "";

    /**
     * Main entry point for building the value map's list.
     *
     * @param resources var args resources from where value map will be computed, order matters
     *                  here : will be the order of the lookup.
     */
    public void setResources(Resource... resources) {
        this.resources = resources != null ? Arrays.asList(resources) : Collections.EMPTY_LIST;
    }

    /**
     * Main entry point for building the value map's list.
     *
     * @param resources list of resources from where value map will be computed, order matters
     *                  here : will be the order of the lookup.
     */
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    /**
     * Constructor.
     */
    public FallbackValueMap() {}

    public FallbackValueMap(Resource... resources) {
        setResources(resources);
    }

    @Nullable
    @Override
    public <T> T get(@NotNull String s, @NotNull Class<T> requiredClass) {
        return getFromCache(s, requiredClass);
    }

    @NotNull
    @Override
    public <T> T get(@NotNull String s, @NotNull T defaultValue) {
        T value;
        if (defaultValue == null) {
            value = (T)get(s, String.class);
        } else {
            value = get(s, (Class<T>) defaultValue.getClass());
        }
        return value == null ? defaultValue : value;
    }

    @Override
    public Object get(Object key) {
        String k = key != null ? key.toString() : EMPTY;
        return get(k, null);
    }

    /**
     * Get value from cache (or fill the cache with it).
     *
     * @param s key
     * @param <T> value's class
     * @param type type requested
     * @return value corresponding to the key. Note that if the key is "deep", that is
     *          contains '/', an additional lookup will be done through the resources layers,
     *          cache will be synced accordingly.
     */
    <T> T getFromCache(@NotNull String s, Class<T> type) {
        Class clazz = type == null ? String.class : type;
        T v = (T)cache.get(s, clazz);
        if (v == null && !accessed.contains(s)) {
            for (Resource resource : resources) {
                v = resource.getValueMap().get(s, type);
                if (v != null) {
                    cache.put(s, v);
                    break;
                }
            }
        }
        accessed.add(s);
        return v;
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return keySet().contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (Map.Entry<String, Object> entry : entrySet()) {
            if (entry.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> keySet() {
        if (keySet == null) {
            keySet = entrySet().stream().map(e -> e.getKey()).collect(Collectors.toSet());
        }
        return keySet;
    }

    @Override
    public Collection<Object> values() {
        if (values == null) {
            values = entrySet().stream().map(e -> e.getValue()).collect(Collectors.toCollection(ArrayList::new));
        }
        return values;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        if (entrySet == null) {
            if (!cacheFilled) {
                fillCache();
            }
            entrySet = cache.entrySet();
        }
        return entrySet;
    }

    /**
     * Simple and 'superficial' cache fill.
     */
    void fillCache() {
        List<Resource> reverse = new ArrayList<>(resources);
        Collections.reverse(reverse);
        for (Resource resource : reverse) {
            ValueMap map = resource.getValueMap();
            for (String key : map.keySet()) {
                cache.put(key, map.get(key));
            }
        }
        cacheFilled = true;
    }

    //mutation operations: we don't support
    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException("Fallback value maps are immutable");
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Fallback value maps are immutable");
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException("Fallback value maps are immutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Fallback value maps are immutable");
    }
}
