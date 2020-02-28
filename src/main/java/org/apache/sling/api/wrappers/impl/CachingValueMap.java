package org.apache.sling.api.wrappers.impl;

import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CachingValueMap implements ValueMap {

    private static final String IMMUTABLE_ERROR_MESSAGE = "CompositeValueMap is immutable";

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
