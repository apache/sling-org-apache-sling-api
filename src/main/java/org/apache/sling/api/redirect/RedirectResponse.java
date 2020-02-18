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
import org.osgi.annotation.versioning.ProviderType;

/**
 * <p>
 * A Redirect redirect defines the interface used to hold information to build a redirect.  It is implemented
 * by the caller to RedirectResolver.resolve, and called by the implementation of the RedirectResolver interface.
 * On return from that call the caller will use the information in the RedirectResponse to perform whatever operation
 * it needs to perform. Typically this will be returning a http redirect, but there may be other uses.
 * </p><p>
 * If the implementation finds that it needs to pass values that the interface does not support, it should set
 * attributes in the request object passed into resolve operation. If the implementation of the RedirectResolver needs to
 * pass values that the interface does not support, it should also use the request object attributes.
 * </p>
 */
@ProviderType
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
     * @return true if the instance has resolved after calling RedirectResolver
     */
    boolean hasResolved();


}
