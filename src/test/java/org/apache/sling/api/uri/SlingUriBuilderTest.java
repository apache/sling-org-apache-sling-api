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
package org.apache.sling.api.uri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SlingUriBuilderTest {

    @Mock
    SlingHttpServletRequest request;

    @Mock
    RequestPathInfo requestPathInfo;

    @Mock
    Resource resource;

    @Before
    public void before() {
        when(request.getRequestPathInfo()).thenReturn(requestPathInfo);
    }

    @Test
    public void testBasicUsage() {

        SlingUri testUri = SlingUriBuilder.create()
                .setResourcePath("/test/to/path")
                .setSelectors(new String[] { "sel1", "sel2" })
                .setExtension("html")
                .setSuffix("/suffix/path")
                .setQuery("par1=val1&par2=val2")
                .build();

        assertEquals("/test/to/path.sel1.sel2.html/suffix/path?par1=val1&par2=val2", testUri.toString());
    }

    // the tests in SlingUriTest extensively test the builder's parse method by using it for constructing
    // all types of SlingUris
    @Test
    public void testParse() {

        String testUriStr = "https://example.com/test/to/path.sel1.sel2.html";
        SlingUri testUri = SlingUriBuilder.parse(testUriStr, null).build();
        assertEquals(testUriStr, testUri.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidSuffix() {
        SlingUriBuilder.parse("/test/to/path.sel1.html", null).setSuffix("suffixWithoutSlash");
    }

    @Test
    public void testCreateFromRequest() {

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443);
        when(request.getQueryString()).thenReturn("par1=val1");
        when(requestPathInfo.getResourcePath()).thenReturn("/test/to/path");
        when(requestPathInfo.getSelectors()).thenReturn(new String[] { "sel1", "sel2" });
        when(requestPathInfo.getExtension()).thenReturn("html");
        when(requestPathInfo.getSuffix()).thenReturn("/suffix/path");

        SlingUri testUri = SlingUriBuilder.createFrom(request).build();

        assertEquals("https://example.com/test/to/path.sel1.sel2.html/suffix/path?par1=val1", testUri.toString());
    }

    @Test
    public void testCreateFromResource() {

        when(resource.getPath()).thenReturn("/test/to/path");
        SlingUri testUri = SlingUriBuilder.createFrom(resource).build();

        assertEquals("/test/to/path", testUri.getResourcePath());
        assertNull(testUri.getSelectorString());
        assertNull(testUri.getExtension());
        assertNull(testUri.getSuffix());
    }

    @Test
    public void testCreateFromResourceWithDotInPath() {

        when(resource.getPath()).thenReturn("/test/to/image.jpg");
        SlingUri testUri = SlingUriBuilder.createFrom(resource).build();

        assertEquals("/test/to/image.jpg", testUri.getResourcePath());
        assertNull(testUri.getSelectorString());
        assertNull(testUri.getExtension());
        assertNull(testUri.getSuffix());
    }

    @Test
    public void testCreateFromPath() {

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443);
        when(request.getQueryString()).thenReturn("par1=val1");
        when(requestPathInfo.getResourcePath()).thenReturn("/test/to/path");
        when(requestPathInfo.getSelectors()).thenReturn(new String[] { "sel1", "sel2" });
        when(requestPathInfo.getExtension()).thenReturn("html");
        when(requestPathInfo.getSuffix()).thenReturn("/suffix/path");

        SlingUri testUri = SlingUriBuilder.createFrom(request).build();

        assertEquals("https://example.com/test/to/path.sel1.sel2.html/suffix/path?par1=val1", testUri.toString());
    }

    @Test
    public void testUseSchemeAndAuthority() throws URISyntaxException {

        URI testUriToUseSchemeAndAuthorityFrom = new URI("https://example.com:8080/test/to/path.sel1.sel2.html");
        String testPath = "/path/to/page.html";
        SlingUri testUri = SlingUriBuilder.parse(testPath, null)
                .useSchemeAndAuthority(testUriToUseSchemeAndAuthorityFrom)
                .build();
        assertEquals("https://example.com:8080/path/to/page.html", testUri.toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testBuilderMayOnlyBeUsedToBuildAnUri() {
        SlingUriBuilder builder = SlingUriBuilder.parse("/path/to/page.html", null);
        SlingUri slingUri = builder.build();
        assertNotNull(slingUri);
        // calling build twice is not allowed
        builder.build();
    }

    @Test
    public void testEmpty() {
        SlingUri testUriEmpty = SlingUriBuilder.create().build();
        assertEquals("", testUriEmpty.toString());
    }
}
