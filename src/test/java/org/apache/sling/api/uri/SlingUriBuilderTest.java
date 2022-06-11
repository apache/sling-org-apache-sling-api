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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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

    @Mock
    ResourceResolver resourceResolver;

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

        // to satisfy that the return of this call is @NotNull
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.map(request, "/test/to/path")).thenReturn("/test/to/path");

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

    /**
     * SLING-11347 verify that mapped path remains mapped in the new SlingUri
     */
    @Test
    public void testCreateFromRequestWithMappedPath() {

        when(request.getResourceResolver()).thenReturn(resourceResolver);
        // simulate the resourcePath not already under /content so ResourceResolver#resolve would
        //   change the result
        when(request.getPathInfo()).thenReturn("/test/to/path");
        when(resourceResolver.map(request, "/content/test/to/path")).thenReturn("/test/to/path");

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443);
        when(request.getQueryString()).thenReturn("par1=val1");
        // simulate the ResourceResolver#resolve switching to a resource under /content
        when(requestPathInfo.getResourcePath()).thenReturn("/content/test/to/path");
        when(requestPathInfo.getSelectors()).thenReturn(new String[] { "sel1", "sel2" });
        when(requestPathInfo.getExtension()).thenReturn("html");
        when(requestPathInfo.getSuffix()).thenReturn("/suffix/path");

        SlingUri testUri = SlingUriBuilder.createFrom(request).build();

        assertEquals("https://example.com/test/to/path.sel1.sel2.html/suffix/path?par1=val1", testUri.toString());
    }

    /**
     * SLING-11347 verify that not-mapped path remains not-mapped in the new SlingUri
     */
    @Test
    public void testCreateFromRequestWithNotMappedPath() {

        when(request.getResourceResolver()).thenReturn(resourceResolver);
        // simulate the resourcePath already under /content so ResourceResolver#resolve would
        //   not change the result
        when(request.getPathInfo()).thenReturn("/content/test/to/path");
        when(resourceResolver.map(request, "/content/test/to/path")).thenReturn("/test/to/path");

        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("example.com");
        when(request.getServerPort()).thenReturn(443);
        when(request.getQueryString()).thenReturn("par1=val1");
        when(requestPathInfo.getResourcePath()).thenReturn("/content/test/to/path");
        when(requestPathInfo.getSelectors()).thenReturn(new String[] { "sel1", "sel2" });
        when(requestPathInfo.getExtension()).thenReturn("html");
        when(requestPathInfo.getSuffix()).thenReturn("/suffix/path");

        SlingUri testUri = SlingUriBuilder.createFrom(request).build();

        assertEquals("https://example.com/content/test/to/path.sel1.sel2.html/suffix/path?par1=val1", testUri.toString());
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

        // to satisfy that the return of this call is @NotNull
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.map(request, "/test/to/path")).thenReturn("/test/to/path");

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

    @Test
    public void testAddQueryParameter() throws URISyntaxException {
        String testPath = "/path/to/page.html";
        SlingUri testUri = SlingUriBuilder.parse(testPath, null)
                .addQueryParameter("param1", "val1")
                .build();
        assertEquals("/path/to/page.html?param1=val1", testUri.toString());
    }

    @Test
    public void testAddQueryParameterValueEncoded() throws URISyntaxException {
        String testPath = "/path/to/page.html";
        SlingUri testUri = SlingUriBuilder.parse(testPath, null)
                .addQueryParameter("redirect", "http://www.example.com/path/to/file.txt?q=3&test=3#test")
                .addQueryParameter("param2", "true")
                .build();
        assertEquals(
                "/path/to/page.html?redirect=http%3A%2F%2Fwww.example.com%2Fpath%2Fto%2Ffile.txt%3Fq%3D3%26test%3D3%23test&param2=true",
                testUri.toString());
    }

    @Test
    public void testSetQueryParameterValueEncoded() throws URISyntaxException {
        String testPath = "/path/to/page.html?param=value"; // existing query parameters are meant to be replaced

        Map<String, String> queryParams = new LinkedHashMap<>();
        queryParams.put("param1", "value1");
        queryParams.put("param2[*]", "value2");
        queryParams.put("param3", "value3%@");

        SlingUri testUri = SlingUriBuilder.parse(testPath, null)
                .setQueryParameters(queryParams)
                .build();
        assertEquals("/path/to/page.html?param1=value1&param2%5B*%5D=value2&param3=value3%25%40", testUri.toString());
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

    @Test
    public void testSetSelectors() {
        SlingUri u1 = SlingUriBuilder.parse("/content", null).build();
        assertEquals(0, u1.getSelectors().length);
        assertNull(u1.getSelectorString());

        u1 = SlingUriBuilder.parse("/content", null).setSelectors(null).build();
        assertEquals(0, u1.getSelectors().length);
        assertNull(u1.getSelectorString());

        u1 = SlingUriBuilder.parse("/content", null).setSelectors(new String[] {"a","b"}).build();
        assertArrayEquals(new String[] {"a", "b"}, u1.getSelectors());
        assertEquals("a.b", u1.getSelectorString());

        u1 = SlingUriBuilder.parse("/content", null).setSelectors(new String[] {"a","b"}).setSelectors(null).build();
        assertEquals(0, u1.getSelectors().length);
        assertNull(u1.getSelectorString());
    }

    @Test
    public void testRemoveSelector() {
        SlingUri u1 = SlingUriBuilder.parse("/content", null).setSelectors(new String[] {"a","b"}).removeSelector("b").removeSelector("c").build();
        assertArrayEquals(new String[] {"a"}, u1.getSelectors());
        assertEquals("a", u1.getSelectorString());
    }

    @Test
    public void testAddSelector() {
        SlingUri u1 = SlingUriBuilder.parse("/content", null).setSelectors(new String[] {"a","b"}).addSelector("c").build();
        assertArrayEquals(new String[] {"a", "b", "c"}, u1.getSelectors());
        assertEquals("a.b.c", u1.getSelectorString());
    }
}
