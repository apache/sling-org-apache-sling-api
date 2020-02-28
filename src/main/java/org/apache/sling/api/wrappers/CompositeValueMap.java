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

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.impl.ObjectConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * An implementation of the {@link ValueMap} based on two {@link ValueMap}s:
 * - One containing the properties
 * - Another one containing the defaults to use in case the properties map
 *   does not contain the values.
 * In case you would like to avoid duplicating properties on multiple resources,
 * you can use a <code>CompositeValueMap</code> to get a concatenated map of
 * properties.
 * @since 2.3 (Sling API Bundle 2.5.0)
 */
@ProviderType
public class CompositeValueMap implements ValueMap {

    private static final String IMMUTABLE_ERROR_MESSAGE = "CompositeValueMap is immutable";

    private final Collection<ValueMap> valueMaps;

    /**
     * Merge mode (only applicable when valueMaps.size() == 2)
     */
    private final boolean merge;

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
     * @see org.apache.sling.api.resource.ValueMapUtil#merge(Collection)
     * @see org.apache.sling.api.resource.ValueMapUtil#merge(ValueMap...)
     */
    public CompositeValueMap(Collection<ValueMap> valueMaps) {
        this.valueMaps = valueMaps;
        this.merge = true;
    }

    /**
     * Constructor
     * @param properties The {@link ValueMap} to read from
     * @param defaults The default {@link ValueMap} to use as fallback
     */
    public CompositeValueMap(final ValueMap properties, final ValueMap defaults) {
        this(properties, defaults, true);
    }

    /**
     * Constructor
     * @param properties The {@link ValueMap} to read from
     * @param defaults The default {@link ValueMap} to use as fallback
     * @param merge Merge flag
     *              - If <code>true</code>, getting a key would return the
     *              current property map's value (if available), even if the
     *              corresponding default does not exist.
     *              - If <code>false</code>, getting a key would return
     *              <code>null</code> if the corresponding default does not
     *              exist
     */
    public CompositeValueMap(final ValueMap properties, final ValueMap defaults, boolean merge) {
        this.valueMaps = asList(checkNotNull(properties, "Properties need to be provided"), defaults != null ? defaults : ValueMap.EMPTY);
        this.merge = merge;
    }

    private static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    public <T> T get(@NotNull String name, @NotNull Class<T> type) {
        // removing the method in order to use the default method causes
        // baselining to suggest a major version bump. calling the default
        // method explicitly works around this issue
        return ValueMap.super.get(name, type);
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    public <T> T get(@NotNull String name, @NotNull T defaultValue) {
        // removing the method in order to use the default method causes
        // baselining to suggest a major version bump. calling the default
        // method explicitly works around this issue
        return ValueMap.super.get(name, defaultValue);
    }

    // ---- Map

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
        // containsKey check is necessary to support merge == false (indirectly via keyStream())
        if (containsKey(key)) {
            return valueMaps.stream()
                    .map(vm -> vm.get(key))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return null;
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
        if (!merge) {
            assert valueMaps.size() == 2;
            return valueMaps.stream()
                    .skip(1).findFirst() // get "default" ValueMap
                    .map(Map::keySet)
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .distinct();
        }
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
