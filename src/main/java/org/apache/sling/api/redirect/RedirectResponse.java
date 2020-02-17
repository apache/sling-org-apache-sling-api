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

package org.apache.sling.api.redirect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * A Redirect redirect defines the base class used to hold information to build a redirect.  It is extended
 * by the caller to RedirectResolver.resolve, and called by the implementation of the RedirectResolver interface.
 * On return from that call the caller will use the information in the RedirectResponse to perform whatever operation
 * it needs to perform. Typically this will be returning a http redirect, but there may be other uses.
 * </p><p>
 * A class is being used rather than an interface to allow empty setter methods to be added over time.
 * Callers to RedirectResolver.resolve should override those methods that they need information for. New methods to this
 * class should be added with empty implementations to ensure that pre-exiting classes extending this method to not
 * have to be updated.
 * </p>
 */
public interface RedirectResponse {

    /**
     * Set a header on the redirect. This will replace existing headers of the same name.
     * If called twice, the header will be reset with the latest value.
     * Multiple headers of the same name are not supported.
     * @param name the name of the header.
     * @param value the value of the header.
     */
    void setHeader(@NotNull  String name, @NotNull  String value);

    /**
     * Set the redirect to be used. Will not be sent until the RedirectResponse is returned from the
     * RedirectResolver.resolver and processed.
     * @param url the url to set, should be set in the context of a request.
     */
    void setRedirect(@NotNull  String url);

    /**
     * Set the status code used in the redirect redirect. Typically 301, but other status codes may be used.
     * @param i status code number.
     */
    void setStatus(int i);

    /**
     * @return true if the instance has resolved after calling RedirectREesolver
     */
    boolean hasResolved();


    /**
     * Sets an annotation to a value.
     * Typically an implementation will use annotations to communicate extra values specific to the implementation
     * without requiring changes to the API. Updates to an annotation will overwrite previous updates to the same key.
     * @param key the annotation key. This should be namespaced to avoid clashes eg kubernetes.io/rewrite.
     * @param attribute the value of the attribute
     */
    void setAnnotation(@NotNull  String key, @NotNull  Object attribute);


    /**
     * Gets the annotation value associated with a key.
     * @param key the key of the annotation to get, as per the key in setAnnotation.
     * @return the value of the annotation or null if no annotation value is available for that key.
     */
    @Nullable Object getAnnotation(@NotNull String key);
}
