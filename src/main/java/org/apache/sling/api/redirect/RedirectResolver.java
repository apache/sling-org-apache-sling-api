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


import org.osgi.annotation.versioning.ProviderType;


import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * A RedirectResolver resolves a redirect in the context of a request. It resolves that redirect having first been bound
 * to a Resource. This is typically achieved through the Adapt to mechanism eg resource.adaptTo(RedirectResolver.class).
 * </p><p>
 * Implementations should implement an AdapterFactory to perform the adaption to an implementation of this class. That class
 * should implement the resolve method such that the methods of the Redirect Response are called to define the RedirectResponse.
 * </p><p>
 * The implementation of the resolve method indicates that it has successfully resolved a redirect by setting a setting a status code
 * see the documentation of the RedirectResponse class for more information.
 * </p>
 */
@ProviderType
public interface RedirectResolver {

    /**
     * Resolve the redirect for the resource bound to this redirect resolver, in the context of the request.
     * May decline to perform this operation, but not setting the status code on the redirectResponse.
     * @param request the context of the resolution given by a request. This is required and must not be null. It will indicate
     *                where the request came from, including the exposed edge Host and other http request headers. Typically
     *                and implementation will use these to determine aspects of the redirect redirect. For instance, requests through
     *                a CDN may redirect differently from request made direct from a service running behind the CDN.
     * @param redirectResponse the redirect redirect class. This is implemented by the caller and will contain the methods
     *                         defined in the base class at the time the implementation of the RedirectResolver was written.
     *                         If methods are added to the RedirectResponse class, they will be added in a way to ensure existing
     *                         implementations do not need to be updated to continue to work.
     */
    void resolve(HttpServletRequest request, RedirectResponse redirectResponse);
}
