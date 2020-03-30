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
package org.apache.sling.api.resource.uri;

import java.net.URI;

import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.mapping.spi.ResourceMapping;

/** Represents a link (or anything that can be used in markup href/src/etc. attributes). A link can be one of the following:
 * 
 * <ul>
 * <li>URI</li>
 * <li>absolute path</li>
 * <li>relative path</li>
 * <li>special link like 'javascript:...' or 'mailto:...'</li>
 * <li>anchor #</li>
 * </ul>
 * 
 * ResourceLink supports changing any part of the link by a clean interface <strong>without using error-prone string operations</strong> on
 * strings.
 * 
 * ResourceLinks are meant to be used in regular application code but are also used in {@link ResourceMapping} pipelines. */
public interface ResourceURI extends RequestPathInfo {

    /** @return returns true if the link is either a relative or absolute path (this is the case if scheme and host is empty and the URI
     *         path is set) */
    public boolean isPath();

    /** @return true if the link is a absolute path starting with a slash ('/'). This is the default case for all links to pages and assets
     *         in AEM. */
    public boolean isAbsolutePath();

    /** @return true if link is relative (not an URL and not starting with '/') */
    public boolean isRelativePath();

    /** @return true if the link is an absolute URI containing a scheme. */
    public boolean isAbsoluteUri();

    /** @return returns the URI. */
    public URI getUri();

    /** @return returns the resource path or null if link does not contain path. */
    @Override
    public String getResourcePath();

    /** @return returns the selector string */
    @Override
    public String getSelectorString();

    /** @return returns the selector array */
    @Override
    public String[] getSelectors();

    /** @return returns the extension of the link */
    @Override
    public String getExtension();

    /** @return returns the suffix of the link */
    @Override
    public String getSuffix();

    /** @return returns the query part of the link */
    public String getQuery();

    /** @return returns the url fragment of the link */
    public String getFragment();

    /** @return returns the scheme of the link */
    public String getScheme();

    /** @return returns the host of the link */
    public String getHost();

    /** @return returns the port of the link */
    public int getPort();

    /** @return returns the corresponding */
    @Override
    public Resource getSuffixResource();

    // -- Currently the implementation is designed gto be mutable (easier to use, smaller memory footprint)
    // mutable vs. immutable to be discussed

    /** Resets the entire link to the given string.
     *
     * @param linkStrParam link to be used for setting the request */
    public void setLinkString(String linkStrParam);

    /** Sets the resource path of the link (leaving selectors, extension, suffix and query unchanged)
     * 
     * @param resourcePath the resource path */
    public void setResourcePath(final String resourcePath);

    /** Sets selectors of the link (leaving path, extension, suffix and query unchanged)
     * 
     * @param selectors the new selectors for the link */
    public void setSelectors(final String[] selectors);

    /** Adds a selector to the existing selectors of the link (leaving path, extension, suffix and query unchanged)
     * 
     * @param selector adds a selector to the existing selectors of the list */
    public void addSelector(final String selector);

    /** Sets the extension of the link (leaving path, selectors, suffix and query unchanged)
     * 
     * @param extension the extension */
    public void setExtension(final String extension);

    /** Sets the suffix of the link (leaving path, selectors, extension and query unchanged)
     * 
     * @param suffix suffix to be set */
    public void setSuffix(final String suffix);

    /** Sets the query of the link (leaving path, selectors, extension and suffix unchanged).
     * 
     * @param query the query */
    public void setQuery(final String query);

    /** Sets the url fragment of the link (leaving path, selectors, extension, suffix and query unchanged).
     * 
     * @param urlFragment url fragment */
    public void setFragment(final String urlFragment);

    /** Sets the scheme of the link
     * 
     * @param scheme scheme */
    public void setScheme(final String scheme);

    /** Sets the host of the link
     * 
     * @param host host */
    public void setHost(final String host);

    /** Sets the port of the link
     * 
     * @param port port */
    public void setPort(final int port);
}
