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
package org.apache.sling.api.request.builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BuildersTest {

    @Test(expected = IllegalArgumentException.class)
    public void createRequestBuilderNullResource() {
        Builders.newRequestBuilder(null);
    }

    @Test
    @Deprecated
    public void createRequestBuilder() {
        final ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        final Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getPath()).thenReturn("/content/page");
        Mockito.when(resource.getResourceResolver()).thenReturn(resolver);

        final SlingHttpServletRequestBuilder builder = Builders.newRequestBuilder(resource);
        builder.withExtension("html").withSelectors("tidy", "json");

        final SlingHttpServletRequest req = builder.build();
        assertEquals(resource, req.getResource());
        assertEquals(resolver, req.getResourceResolver());
        assertEquals("html", req.getRequestPathInfo().getExtension());
        assertEquals("/content/page", req.getRequestPathInfo().getResourcePath());
        assertEquals("tidy.json", req.getRequestPathInfo().getSelectorString());
        assertArrayEquals(
                new String[] {"tidy", "json"}, req.getRequestPathInfo().getSelectors());
        assertNull(req.getRequestPathInfo().getSuffix());
        assertNull(req.getRequestPathInfo().getSuffixResource());

        assertNotNull(req.getRequestProgressTracker());
    }

    @Test
    @Deprecated
    public void createResponseBuilder() {
        final SlingHttpServletResponseBuilder builder = Builders.newResponseBuilder();
        final SlingHttpServletResponseResult result = builder.build();
        assertNotNull(result);
    }

    @Test
    public void createRequestProgressTracker() {
        assertNotNull(Builders.newRequestProgressTracker());
    }

    @Test
    public void createRequestParameter() throws UnsupportedEncodingException {
        @NotNull RequestParameter rp = Builders.newRequestParameter("key", "value");
        assertNotNull(rp);
        assertEquals("key", rp.getName());
        assertEquals("value", rp.getString());
        assertEquals("value", rp.getString(StandardCharsets.UTF_8.name()));
    }

    @Test
    public void createRequestParameterWithCharset() throws UnsupportedEncodingException {
        @NotNull RequestParameter rp = Builders.newRequestParameter("key", "value", StandardCharsets.UTF_16);
        assertNotNull(rp);
        assertEquals("key", rp.getName());
        assertEquals("value", rp.getString());
        assertEquals("value", rp.getString(StandardCharsets.UTF_16.name()));
    }

    @Test
    public void createBinaryRequestParameter() throws IOException {
        byte[] value = new byte[] {1, 2, 3};
        RequestParameter rp = Builders.newRequestParameter("key", value, "filename", "application/octet-stream");
        assertNotNull(rp);
        assertEquals("key", rp.getName());
        try (InputStream is = rp.getInputStream()) {
            byte[] actualValue = new byte[30];
            int length = is.read(actualValue);
            assertEquals(value.length, length);
            actualValue = Arrays.copyOfRange(actualValue, 0, length);
            assertArrayEquals(value, actualValue);
        }
        assertEquals(value.length, rp.getSize());
        assertEquals("filename", rp.getFileName());
        assertEquals("application/octet-stream", rp.getContentType());
    }
}
