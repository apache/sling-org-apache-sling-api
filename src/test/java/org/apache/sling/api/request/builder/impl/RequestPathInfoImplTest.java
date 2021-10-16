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
package org.apache.sling.api.request.builder.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.mockito.Mockito;

public class RequestPathInfoImplTest {

    @Test
    public void testExtension() {
        final ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        final Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getPath()).thenReturn("/content/page");
        Mockito.when(resource.getResourceResolver()).thenReturn(resolver);

        RequestPathInfo requestPathInfo = new RequestPathInfoImpl(resource, null, null, null);
        assertNull(requestPathInfo.getExtension());
        requestPathInfo = new RequestPathInfoImpl(resource, null, "ext", null);
        assertEquals("ext", requestPathInfo.getExtension());
    }

    @Test
    public void testResourcePath() {
        final ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        final Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getPath()).thenReturn("/content/page");
        Mockito.when(resource.getResourceResolver()).thenReturn(resolver);

        RequestPathInfo requestPathInfo = new RequestPathInfoImpl(resource, null, null, null);
        assertEquals("/content/page", requestPathInfo.getResourcePath());
    }

    @Test
    public void testSelector() {
        final ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        final Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getPath()).thenReturn("/content/page");
        Mockito.when(resource.getResourceResolver()).thenReturn(resolver);

        RequestPathInfo requestPathInfo = new RequestPathInfoImpl(resource, null, null, null);
        assertNull(requestPathInfo.getSelectorString());
        assertEquals(0, requestPathInfo.getSelectors().length);
        
        requestPathInfo = new RequestPathInfoImpl(resource, new String[] {"aa", "bb"}, null, null);
        assertEquals("aa.bb", requestPathInfo.getSelectorString());
        assertArrayEquals(new String[] { "aa", "bb" }, requestPathInfo.getSelectors());
    }

    @Test
    public void testSuffix() {
        final ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        final Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getPath()).thenReturn("/content/page");
        Mockito.when(resource.getResourceResolver()).thenReturn(resolver);

        RequestPathInfo requestPathInfo = new RequestPathInfoImpl(resource, null, null, null);
        assertNull(requestPathInfo.getSuffix());
        
        requestPathInfo = new RequestPathInfoImpl(resource, null, null, "/suffix");
        assertEquals("/suffix", requestPathInfo.getSuffix());
    }

    @Test
    public void testGetSuffixResource() {
        final ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        final Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getPath()).thenReturn("/content/page");
        Mockito.when(resource.getResourceResolver()).thenReturn(resolver);
        final Resource suffixResource = Mockito.mock(Resource.class);
        Mockito.when(resolver.getResource("/suffix")).thenReturn(suffixResource);

        RequestPathInfo requestPathInfo = new RequestPathInfoImpl(resource, null, null, null);
        assertNull(requestPathInfo.getSuffixResource());
        
        requestPathInfo = new RequestPathInfoImpl(resource, null, null, "/suffix");        
        assertSame(suffixResource, requestPathInfo.getSuffixResource());
    }
}
