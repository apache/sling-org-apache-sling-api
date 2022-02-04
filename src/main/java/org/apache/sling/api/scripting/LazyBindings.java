/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Licensed to the Apache Software Foundation (ASF) under one
 ~ or more contributor license agreements.  See the NOTICE file
 ~ distributed with this work for additional information
 ~ regarding copyright ownership.  The ASF licenses this file
 ~ to you under the Apache License, Version 2.0 (the
 ~ "License"); you may not use this file except in compliance
 ~ with the License.  You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package org.apache.sling.api.scripting;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.script.Bindings;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * <p>
 * The {@code LazyBindings} wraps another map and dynamically provides entries for the wrapped map through a map of {@link
 * LazyBindings.Supplier}s.
 * </p>
 * <p>
 * When {@link #get(Object)} is called with a {@code key} that's not present in the wrapped map, then the {@link LazyBindings.Supplier}s map
 * will be queried and, if an entry exists for that key, the {@link LazyBindings.Supplier}-generated value will be used to populate the
 * wrapped map.
 * </p>
 * <p>
 * While the {@link #keySet()} and {@link #containsKey(Object)} will also check the keys present in the {@link LazyBindings.Supplier}s map,
 * all other methods (e.g. {@link #values()}, {@link #containsValue(Object)}) will only deal with the contents of the wrapped map.
 * <p>
 * {@link #entrySet()} will however return a merged view of both the {@link LazyBindings.Supplier}s and the wrapped map, so that copies to
 * other {@code LazyBindings} maps preserve the functionality of having lazily-evaluated bindings.</p>
 * <p>
 * This class <b>does not provide any thread-safety guarantees</b>. If {@code this} {@code Bindings} map needs to be used in a concurrent
 * setup it's the responsibility of the caller to synchronize access. The simplest way would be to wrap it through {@link
 * Collections#synchronizedMap(Map)}.
 * </p>
 */
@ConsumerType
public class LazyBindings extends HashMap<String, Object> implements Bindings {

    private final Map<String, LazyBindings.Supplier> suppliers;

    public LazyBindings() {
        this(new HashMap<>(), Collections.emptyMap());
    }

    public LazyBindings(Map<String, LazyBindings.Supplier> suppliers) {
        this(suppliers, Collections.emptyMap());
    }

    public LazyBindings(Map<String, LazyBindings.Supplier> suppliers, Map<String, Object> wrapped) {
        super(wrapped);
        this.suppliers = suppliers;
    }


    @Override
    public Object put(String key, Object value) {
        Object previous = this.get(key);
        if (value instanceof LazyBindings.Supplier) {
            suppliers.put(key, (LazyBindings.Supplier) value);
            super.remove(key);
        } else {
            super.put(key, value);
            suppliers.remove(key);
        }
        return previous;
    }

    @Override
    public void putAll(Map<? extends String, ?> toMerge) {
        for (Entry<? extends String, ?> entry : toMerge.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        super.clear();
        suppliers.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<>(super.keySet());
        if (!suppliers.isEmpty()) {
            keySet.addAll(suppliers.keySet());
        }
        return Collections.unmodifiableSet(keySet);
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return super.values();
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        Map<String, Object> holder = new HashMap<>();
        Stream.concat(super.entrySet().stream(), suppliers.entrySet().stream())
                .forEach(entry -> holder.put(entry.getKey(), entry.getValue()));
        return holder.entrySet();
    }

    @Override
    public int size() {
        Set<String> keys = new HashSet<>(super.keySet());
        keys.addAll(suppliers.keySet());
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key) || suppliers.containsKey(key);
    }

    @Override
    public Object get(Object key) {
        String k = key.toString();
        if (!super.containsKey(k) && suppliers.containsKey(k)) {
            Object value = suppliers.get(k).get();
            super.put(k, value);
            suppliers.remove(k);
        }
        return super.get(key);
    }

    @Override
    public Object remove(Object key) {
        Object previous = super.remove(key);
        if (previous == null) {
            LazyBindings.Supplier supplier = suppliers.remove(key);
            if (supplier != null) {
                return supplier.get();
            }
        }
        return previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof LazyBindings && super.equals(o)) {
            LazyBindings other = (LazyBindings) o;
            return suppliers.equals(other.suppliers);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + suppliers.hashCode();
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        Object result = get(key);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }
    /**
     * This marker interface should be used for suppliers which should be unwrapped when used as values stored in a {@link LazyBindings} map.
     */
    @ConsumerType
    @FunctionalInterface
    public interface Supplier extends java.util.function.Supplier {}
}
