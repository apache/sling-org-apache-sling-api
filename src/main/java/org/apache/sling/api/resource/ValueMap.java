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

import java.util.Collections;
import java.util.Map;

import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.api.wrappers.impl.ObjectConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * The <code>ValueMap</code> is an easy way to access properties of a resource.
 * With resources you can use {@link Resource#getValueMap()} to obtain the
 * value map of a resource. The various getter methods can be used to get the
 * properties of the resource.
 * <p>
 * In addition a value map returned by a resource supports getting of deep
 * values, like get("content/title") which is equivalent to call
 * getChild("content") on the resource, get the value map of that resource
 * and call get("title") - but without requiring to do all the checks.
 * Only the following methods support deep reads: {@link #get(Object)},
 * {@link #get(String, Class)}, {@link #get(String, Object)} and
 * {@link #containsKey(Object)}.
 * <p>
 * A <code>ValueMap</code> should be immutable.
 * <p>
 * All methods may throw any {@link RuntimeException} if the underlying property
 * cannot be read for some reason (e.g. if the underlying storage is corrupt).
 */
@ConsumerType
public interface ValueMap extends Map<String, Object> {

    /**
     * Empty immutable value map.
     */
    ValueMap EMPTY = new ValueMapDecorator(Collections.emptyMap());

    /**
     * Get a named property and convert it into the given type.
     * This method does not support conversion into a primitive type or an
     * array of a primitive type. It should return <code>null</code> in this
     * case.
     *
     * @param name The name of the property
     * @param type The class of the type
     * @param <T> The class of the type
     * @return Return named value converted to type T or <code>null</code> if
     *         non existing or can't be converted.
     * @throws RuntimeException if the underlying property cannot be accessed
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default <T> T get(@NotNull String name, @NotNull Class<T> type) {
        Object value = get(name);
        if (value == null) {
            return null;
        }
        if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return ObjectConverter.convert(value, type);
    }

    /**
     * Get a named property and convert it into the given type.
     * This method does not support conversion into a primitive type or an
     * array of a primitive type. It should return the default value in this
     * case.
     * <br><br>
     * <b>Implementation hint</b>: In the past it was allowed to call this with a 2nd parameter being {@code null}.
     * Therefore all implementations should internally call {@link #get(Object)} when the 2nd parameter
     * has value {@code null}.
     *
     * @param name The name of the property
     * @param <T> The expected type
     * @param defaultValue The default value to use if the named property does
     *            not exist or cannot be converted to the requested type. The
     *            default value is also used to define the type to convert the
     *            value to. Must not be {@code null}. If you want to return {@code null} by default
     *            rather rely on {@link #get(String, Class)}.
     * @return Return named value converted to type T or the default value if
     *         non existing or can't be converted.
     * @throws RuntimeException if the underlying property cannot be accessed
     */
    @SuppressWarnings("unchecked")
    @NotNull
    default <T> T get(@NotNull String name, @NotNull T defaultValue) {
        if (defaultValue == null) {
            return (T) get(name);
        }
        T value = (T) get(name, defaultValue.getClass());
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
