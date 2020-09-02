/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.api.wrappers;

import static java.util.Arrays.asList;

import java.util.List;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.impl.CachingValueMap;
import org.apache.sling.api.wrappers.impl.MergingValueMap;
import org.jetbrains.annotations.NotNull;

/**
 * Factory methods to create {@code ValueMap}s.
 * @since 2.7
 */
public final class ValueMapUtil {

    /**
     * A convenience method that turns the var-args into a {@code Collection}
     * and delegates to {@link #merge(List)}.
     *
     * @param valueMaps the {@code ValueMap} instances to merge
     * @return the merged {@code ValueMap} view
     *
     * @see #merge(List)
     */
    @NotNull
    public static ValueMap merge(@NotNull ValueMap... valueMaps) {
        return merge(asList(valueMaps));
    }

    /**
     * Merge provided {@code ValueMaps} into a single view {@code ValueMap} that aggregates
     * all key-value pairs of the given maps. The value for a key-value pair is taken from
     * the first {@code ValueMap} (in iteration order) that has a mapping for the given key.
     * <br>
     * E.g. assuming {@code merge(vm1, vm2, vm3} where all maps {@code vm1, vm2, vm3} have
     * a value mapped to the key {@code k1}, then the value from {@code vm1} is returned.
     *
     * @param valueMaps the {@code ValueMap} instances to merge
     * @return the merged {@code ValueMap} view
     */
    @NotNull
    public static ValueMap merge(@NotNull List<ValueMap> valueMaps) {
        return new MergingValueMap(valueMaps);
    }

    /**
     * Convenience method that allows creating a merged {@code ValueMap} where
     * accessed mappings are cached to optimize repeated lookups.
     * <br>
     * This is equivalent to calling {@code cache(merge(valueMaps))}.
     *
     * @param valueMaps the {@code ValueMap} instances to merge
     * @return the merged and cached {@code ValueMap} view
     */
    @NotNull
    public static ValueMap mergeAndCache(@NotNull List<ValueMap> valueMaps) {
        return cache(merge(valueMaps));
    }

    /**
     * Decorates the given {@code ValueMap} with a caching layer.
     * Every key-value pair that is accessed is cached for
     * subsequent accesses. Calls to {@code ValueMap#keySet()},
     * {@code ValueMap#values()} and {@code ValueMap#entrySet()}
     * will cause all entries to be cached.
     * <br>
     * Note: if the underlying {@code ValueMap} is modified, the
     * modification may not be reflected via the caching wrapper.
     *
     * @param valueMap the {@code ValueMap} instance to cache
     * @return the cached {@code ValueMap} view
     */
    @NotNull
    public static ValueMap cache(@NotNull ValueMap valueMap) {
        return new CachingValueMap(valueMap);
    }

    /**
     * private constructor to hide implicit public one
     */
    private ValueMapUtil() {
    }
}
