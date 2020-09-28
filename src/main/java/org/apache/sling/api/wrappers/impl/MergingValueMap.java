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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Merge provided {@code ValueMaps} into a single view {@code ValueMap} that aggregates
 * all key-value pairs of the given maps. The value for a key-value pair is taken from
 * the first {@code ValueMap} (in iteration order) that has a mapping for the given key.
 * <br>
 * In case you would like to avoid duplicating properties on multiple resources,
 * you can use a <code>{@link MergingValueMap}</code> to get a concatenated map of
 * properties.
 *
 * @see ValueMapUtil#merge(List)
 * @see ValueMapUtil#merge(ValueMap...)
 * @see ValueMapUtil#mergeAndCache(List)
 */
public class MergingValueMap implements ValueMap {

    private static final String IMMUTABLE_ERROR_MESSAGE = "MergingValueMap is immutable";

    private final List<ValueMap> valueMaps;

    /**
     * Constructor that allows merging any number of {@code ValueMap}
     * instances into a single {@code ValueMap} view. The keys of the
     * view are the union of the keys of all value maps. The values of
     * the view is the mapping of all keys to their respective value.
     * The entries are the key-value pairs. Values are retrieved by
     * getting the value for a key for each {@code ValueMap} until a
     * non-null value is found.
     *
     * @param valueMaps The ValueMaps to be merged.
     *
     * @see ValueMapUtil#merge(List)
     * @see ValueMapUtil#merge(ValueMap...)
     */
    public MergingValueMap(@NotNull List<ValueMap> valueMaps) {
        this.valueMaps = Collections.unmodifiableList(new ArrayList<>(valueMaps));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return keySet().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(final Object key) {
        return keyStream().anyMatch(k -> Objects.equals(k, key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(final Object value) {
        return valueStream().anyMatch(v -> Objects.equals(v, value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        return valueMaps.stream()
                .filter(vm -> vm.containsKey(key)) // SLING-9774
                .findFirst()
                .map(vm -> vm.get(key))
                .orElse(null);
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return keyStream().collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return valueStream().collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return keyStream()
                .map(key -> new AbstractMap.SimpleEntry<>(key, get(key)))
                .collect(Collectors.toSet());
    }

    @NotNull
    private Stream<String> keyStream() {
        return valueMaps.stream()
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .distinct();
    }

    @NotNull
    private Stream<Object> valueStream() {
        return keyStream().map(this::get);
    }

    /**
     * {@inheritDoc}
     */
    public Object put(final String aKey, final Object value) {
        throw new UnsupportedOperationException(IMMUTABLE_ERROR_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    public Object remove(final Object key) {
        throw new UnsupportedOperationException(IMMUTABLE_ERROR_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(final Map<? extends String, ?> properties) {
        throw new UnsupportedOperationException(IMMUTABLE_ERROR_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        throw new UnsupportedOperationException(IMMUTABLE_ERROR_MESSAGE);
    }
}
