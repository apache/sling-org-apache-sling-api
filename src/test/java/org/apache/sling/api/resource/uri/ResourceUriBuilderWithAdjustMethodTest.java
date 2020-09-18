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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.function.Consumer;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResourceUriBuilderWithAdjustMethodTest {

    @Test
    public void testAdjustAddSelectorFullUrl() {

        testAdjustUri(
                "http://host.com/test/to/path.html",
                resourceUriBuilder -> {
                    resourceUriBuilder.addSelector("test");
                },
                "http://host.com/test/to/path.test.html",
                resourceUri -> {
                    assertEquals("test", resourceUri.getSelectorString());
                });
    }

    @Test
    public void testAdjustAddSelectorAndSuffixPath() {

        testAdjustUri(
                "/test/to/path.html",
                resourceUriBuilder -> {
                    resourceUriBuilder.addSelector("test");
                    resourceUriBuilder.setSuffix("/suffix/path/to/file");
                },
                "/test/to/path.test.html/suffix/path/to/file",
                resourceUri -> {
                    assertArrayEquals(new String[] { "test" }, resourceUri.getSelectors());
                    assertEquals("/suffix/path/to/file", resourceUri.getSuffix());
                });
    }

    @Test
    public void testExtendSimplePathToFullUrl() {

        testAdjustUri(
                "/test/to/path.html",
                resourceUriBuilder -> {
                    resourceUriBuilder.setScheme("https");
                    resourceUriBuilder.setHost("example.com");
                    resourceUriBuilder.setSuffix("/suffix/path/to/file");
                },
                "https://example.com/test/to/path.html/suffix/path/to/file",
                resourceUri -> {
                    assertEquals("https", resourceUri.getScheme());
                    assertEquals("example.com", resourceUri.getHost());
                    assertEquals("/suffix/path/to/file", resourceUri.getSuffix());
                });
    }

    @Test
    public void testSetSelectorAndSuffixToRelativeUrl() {

        testAdjustUri(
                "../to/path.html",
                resourceUriBuilder -> {
                    resourceUriBuilder.addSelector("sel1");
                    resourceUriBuilder.setSuffix("/suffix/path/to/file");
                },
                "../to/path.sel1.html/suffix/path/to/file",
                resourceUri -> {
                    assertEquals("../to/path", resourceUri.getResourcePath());
                    assertEquals("sel1", resourceUri.getSelectorString());
                    assertEquals("/suffix/path/to/file", resourceUri.getSuffix());
                });
    }

    @Test
    public void testFullUrltoSimplePath() {

        testAdjustUri(
                "https://user:pw@example.com/test/to/path.html/suffix/path/to/file",
                resourceUriBuilder -> {
                    resourceUriBuilder.removeSchemeAndAuthority();
                },
                "/test/to/path.html/suffix/path/to/file",
                resourceUri -> {
                    assertEquals(null, resourceUri.getScheme());
                    assertEquals(null, resourceUri.getUserInfo());
                    assertEquals(null, resourceUri.getHost());
                });
    }

    @Test
    public void testAdjustPathInOpaqueUriWithoutEffect() {

        testAdjustUri(
                "mailto:jon.doe@example.com",
                resourceUriBuilder -> {
                    resourceUriBuilder.setUserInfo("user:pw");
                    resourceUriBuilder.setHost("example.com");
                    resourceUriBuilder.setPort(500);
                    resourceUriBuilder.setPath("/path/to/resource");
                    resourceUriBuilder.setResourcePath("/path/to/resource");
                    resourceUriBuilder.addSelector("test");
                    resourceUriBuilder.setExtension("html");
                    resourceUriBuilder.setSuffix("/suffix");
                },
                "mailto:jon.doe@example.com",
                resourceUri -> {
                    assertNull(resourceUri.getHost());
                    assertNull(resourceUri.getResourcePath());
                    assertNull(resourceUri.getSelectorString());
                    assertNull(resourceUri.getExtension());
                    assertNull(resourceUri.getSuffix());
                });
    }

    @Test
    public void testAdjustOpaqueToNormalUrl() {

        testAdjustUri(
                "mailto:jon.doe@example.com",
                resourceUriBuilder -> {
                    resourceUriBuilder.setSchemeSpecificPart(null);
                    resourceUriBuilder.setScheme("https");
                    resourceUriBuilder.setHost("example.com");
                    resourceUriBuilder.setPath("/path/to/resource.html");
                },
                "https://example.com/path/to/resource.html",
                resourceUri -> {
                    assertNull(resourceUri.getSchemeSpecificPart());
                });
    }

    @Test
    public void testAdjustOpaqueUri() {

        testAdjustUri(
                "mailto:jon.doe@example.com",
                resourceUriBuilder -> {
                    resourceUriBuilder.setSchemeSpecificPart("mary.doe@example.com");
                },
                "mailto:mary.doe@example.com",
                resourceUri -> {
                    assertEquals("mary.doe@example.com", resourceUri.getSchemeSpecificPart());
                    assertNull(resourceUri.getResourcePath());
                });
    }

    @Test
    public void testAdjustSelectorsInFragmentOnlyUrlWithoutEffect() {

        testAdjustUri(
                "#fragment",
                resourceUriBuilder -> {
                    resourceUriBuilder.addSelector("test");
                    resourceUriBuilder.setSuffix("/suffix");
                },
                "#fragment",
                resourceUri -> {
                    assertEquals(null, resourceUri.getSelectorString());
                    assertEquals(null, resourceUri.getSuffix());
                });
    }

    @Test
    public void testAjustFtpUrl() {

        testAdjustUri(
                "sftp://user:pw@example.com:9090/some/path",
                resourceUriBuilder -> {
                    resourceUriBuilder.setPath("/some/other/path");
                    resourceUriBuilder.setPort(9091);
                },
                "sftp://user:pw@example.com:9091/some/other/path",
                resourceUri -> {
                    assertEquals("/some/other/path", resourceUri.getResourcePath());
                    assertEquals(null, resourceUri.getSelectorString());
                    assertEquals(9091, resourceUri.getPort());
                });
    }

    @Test
    public void testAdjustPathParameter() {

        testAdjustUri(
                "/test/to/path.sel1.html/suffix/path/to/file",
                resourceUriBuilder -> {
                    resourceUriBuilder.setPathParameter("v", "2.0");
                },
                "/test/to/path;v='2.0'.sel1.html/suffix/path/to/file",
                resourceUri -> {
                    assertEquals("/test/to/path", resourceUri.getResourcePath());
                    assertEquals("sel1", resourceUri.getSelectorString());
                    assertEquals("html", resourceUri.getExtension());
                    assertEquals("/suffix/path/to/file", resourceUri.getSuffix());
                    assertEquals(1, resourceUri.getPathParameters().size());
                    assertEquals("2.0", resourceUri.getPathParameters().get("v"));
                });
    }

    // -- helper methods

    public static void testAdjustUri(String testUri, Consumer<ResourceUriBuilder> adjuster, String testUriAfterEdit,
            Consumer<ResourceUri> additionalAssertions) {
        testAdjustUri(testUri, adjuster, testUriAfterEdit, additionalAssertions, null);
    }

    public static void testAdjustUri(String testUri, Consumer<ResourceUriBuilder> adjuster, String testUriAfterEdit,
            Consumer<ResourceUri> additionalAssertions, ResourceResolver resourceResolver) {
        ResourceUri resourceUri = ResourceUriBuilder.parse(testUri, resourceResolver).build();

        ResourceUri adjustedResourceUri = resourceUri.adjust(adjuster);

        assertEquals(testUriAfterEdit, adjustedResourceUri.toString());
        assertEquals(testUriAfterEdit, adjustedResourceUri.toUri().toString());

        additionalAssertions.accept(adjustedResourceUri);
    }

}
