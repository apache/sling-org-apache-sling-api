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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SlingUriBuilderWithAdjustMethodTest {

    @Test
    public void testAdjustAddSelectorFullUrl() {

        testAdjustUri(
                "http://host.com/test/to/path.html",
                slingUriBuilder -> {
                    slingUriBuilder.addSelector("test");
                },
                "http://host.com/test/to/path.test.html",
                slingUri -> {
                    assertEquals("test", slingUri.getSelectorString());
                });
    }

    @Test
    public void testAdjustAddSelectorAndSuffixPath() {

        testAdjustUri(
                "/test/to/path.html",
                slingUriBuilder -> {
                    slingUriBuilder.addSelector("test");
                    slingUriBuilder.setSuffix("/suffix/path/to/file");
                },
                "/test/to/path.test.html/suffix/path/to/file",
                slingUri -> {
                    assertArrayEquals(new String[] { "test" }, slingUri.getSelectors());
                    assertEquals("/suffix/path/to/file", slingUri.getSuffix());
                });
    }

    @Test
    public void testExtendSimplePathToFullUrl() {

        testAdjustUri(
                "/test/to/path.html",
                slingUriBuilder -> {
                    slingUriBuilder.setScheme("https");
                    slingUriBuilder.setHost("example.com");
                    slingUriBuilder.setSuffix("/suffix/path/to/file");
                },
                "https://example.com/test/to/path.html/suffix/path/to/file",
                slingUri -> {
                    assertEquals("https", slingUri.getScheme());
                    assertEquals("example.com", slingUri.getHost());
                    assertEquals("/suffix/path/to/file", slingUri.getSuffix());
                });
    }

    @Test
    public void testSetSelectorAndSuffixToRelativeUrl() {

        testAdjustUri(
                "../to/path.html",
                slingUriBuilder -> {
                    slingUriBuilder.addSelector("sel1");
                    slingUriBuilder.setSuffix("/suffix/path/to/file");
                },
                "../to/path.sel1.html/suffix/path/to/file",
                slingUri -> {
                    assertEquals("../to/path", slingUri.getResourcePath());
                    assertEquals("sel1", slingUri.getSelectorString());
                    assertEquals("/suffix/path/to/file", slingUri.getSuffix());
                });
    }

    @Test
    public void testFullUrltoSimplePath() {

        testAdjustUri(
                "https://user:pw@example.com/test/to/path.html/suffix/path/to/file",
                slingUriBuilder -> {
                    slingUriBuilder.removeSchemeAndAuthority();
                },
                "/test/to/path.html/suffix/path/to/file",
                slingUri -> {
                    assertEquals(null, slingUri.getScheme());
                    assertEquals(null, slingUri.getUserInfo());
                    assertEquals(null, slingUri.getHost());
                });
    }

    @Test
    public void testAdjustPathInOpaqueUriWithoutEffect() {

        testAdjustUri(
                "mailto:jon.doe@example.com",
                slingUriBuilder -> {
                    slingUriBuilder.setUserInfo("user:pw");
                    slingUriBuilder.setHost("example.com");
                    slingUriBuilder.setPort(500);
                    slingUriBuilder.setPath("/path/to/resource");
                    slingUriBuilder.setResourcePath("/path/to/resource");
                    slingUriBuilder.addSelector("test");
                    slingUriBuilder.setExtension("html");
                    slingUriBuilder.setSuffix("/suffix");
                },
                "mailto:jon.doe@example.com",
                slingUri -> {
                    assertNull(slingUri.getHost());
                    assertNull(slingUri.getResourcePath());
                    assertNull(slingUri.getSelectorString());
                    assertNull(slingUri.getExtension());
                    assertNull(slingUri.getSuffix());
                });
    }

    @Test
    public void testAdjustOpaqueToNormalUrl() {

        testAdjustUri(
                "mailto:jon.doe@example.com",
                slingUriBuilder -> {
                    slingUriBuilder.setSchemeSpecificPart(null);
                    slingUriBuilder.setScheme("https");
                    slingUriBuilder.setHost("example.com");
                    slingUriBuilder.setPath("/path/to/resource.html");
                },
                "https://example.com/path/to/resource.html",
                slingUri -> {
                    assertEquals("//example.com/path/to/resource.html", slingUri.getSchemeSpecificPart());
                });
    }

    @Test
    public void testAdjustOpaqueUri() {

        testAdjustUri(
                "mailto:jon.doe@example.com",
                slingUriBuilder -> {
                    slingUriBuilder.setSchemeSpecificPart("mary.doe@example.com");
                },
                "mailto:mary.doe@example.com",
                slingUri -> {
                    assertEquals("mary.doe@example.com", slingUri.getSchemeSpecificPart());
                    assertNull(slingUri.getResourcePath());
                });
    }

    @Test
    public void testAdjustSelectorsInFragmentOnlyUrlWithoutEffect() {

        testAdjustUri(
                "#fragment",
                slingUriBuilder -> {
                    slingUriBuilder.addSelector("test");
                    slingUriBuilder.setSuffix("/suffix");
                },
                "#fragment",
                slingUri -> {
                    assertEquals(null, slingUri.getSelectorString());
                    assertEquals(null, slingUri.getSuffix());
                });
    }

    @Test
    public void testAjustFtpUrl() {

        testAdjustUri(
                "sftp://user:pw@example.com:9090/some/path",
                slingUriBuilder -> {
                    slingUriBuilder.setPath("/some/other/path");
                    slingUriBuilder.setPort(9091);
                },
                "sftp://user:pw@example.com:9091/some/other/path",
                slingUri -> {
                    assertEquals("/some/other/path", slingUri.getResourcePath());
                    assertEquals(null, slingUri.getSelectorString());
                    assertEquals(9091, slingUri.getPort());
                });
    }

    @Test
    public void testAdjustPathParameter() {

        testAdjustUri(
                "/test/to/path.sel1.html/suffix/path/to/file",
                slingUriBuilder -> {
                    slingUriBuilder.setPathParameter("v", "2.0");
                },
                "/test/to/path;v='2.0'.sel1.html/suffix/path/to/file",
                slingUri -> {
                    assertEquals("/test/to/path", slingUri.getResourcePath());
                    assertEquals("sel1", slingUri.getSelectorString());
                    assertEquals("html", slingUri.getExtension());
                    assertEquals("/suffix/path/to/file", slingUri.getSuffix());
                    assertEquals(1, slingUri.getPathParameters().size());
                    assertEquals("2.0", slingUri.getPathParameters().get("v"));
                });
    }

    @Test
    public void testAdjustRemovePath() {

        testAdjustUri(
                "http://example.com/test/to/path;key='val'.sel1.html/suffix/path/to/file?queryPar=val",
                slingUriBuilder -> {
                    slingUriBuilder.setPath(null);
                },
                "http://example.com?queryPar=val",
                slingUri -> {
                    assertEquals(null, slingUri.getPath());
                    assertEquals(null, slingUri.getResourcePath());
                    assertEquals(null, slingUri.getSelectorString());
                    assertEquals(null, slingUri.getExtension());
                    assertEquals(null, slingUri.getSuffix());
                    assertTrue(slingUri.getPathParameters().isEmpty());
                    assertEquals("queryPar=val", slingUri.getQuery());
                });
    }

    @Test
    public void testAdjustReplacePathEffectivelyRemovingSelectors() {

        testAdjustUri(
                "http://example.com/test/to/path;key='val'.sel1.html/suffix/path/to/file?queryPar=val",
                slingUriBuilder -> {
                    slingUriBuilder.setPath("/simple/other/path");
                },
                "http://example.com/simple/other/path?queryPar=val",
                slingUri -> {
                    assertEquals("/simple/other/path", slingUri.getPath());
                    assertEquals("/simple/other/path", slingUri.getResourcePath());
                    assertEquals("setPath() (opposed to setResourcePath()) must also remove selectors if not present in path", null,
                            slingUri.getSelectorString());
                    assertEquals("setPath() (opposed to setResourcePath()) must also remove extension if not present in path", null,
                            slingUri.getExtension());
                    assertEquals("setPath() (opposed to setResourcePath()) must also remove suffix if not present in path", null,
                            slingUri.getSuffix());
                    assertTrue("setPath() (opposed to setResourcePath()) must also remove path parameters if not present in path",
                            slingUri.getPathParameters().isEmpty());
                    assertEquals("queryPar=val", slingUri.getQuery());
                });
    }

    @Test
    public void testAdjustReplacePathWithPathParametersRemovingSelector() {

        testAdjustUri(
                "http://example.com/test/to/path;key='val'.sel1.html/suffix/path/to/file?queryPar=val",
                slingUriBuilder -> {
                    slingUriBuilder.setPath("/simple/other/path;key='val'");
                },
                "http://example.com/simple/other/path;key='val'?queryPar=val",
                slingUri -> {
                    assertEquals("/simple/other/path;key='val'", slingUri.getPath());
                    assertEquals("/simple/other/path", slingUri.getResourcePath());
                    assertEquals("setPath() (opposed to setResourcePath()) must also remove selectors if not present in path", null,
                            slingUri.getSelectorString());
                    assertEquals("setPath() (opposed to setResourcePath()) must also remove extension if not present in path", null,
                            slingUri.getExtension());
                    assertEquals("setPath() (opposed to setResourcePath()) must also remove suffix if not present in path", null,
                            slingUri.getSuffix());
                    assertEquals(1, slingUri.getPathParameters().size());
                    assertEquals("val", slingUri.getPathParameters().get("key"));
                    assertEquals("queryPar=val", slingUri.getQuery());
                });
    }

    // -- helper methods

    public static void testAdjustUri(String testUri, Consumer<SlingUriBuilder> adjuster, String testUriAfterEdit,
            Consumer<SlingUri> additionalAssertions) {
        testAdjustUri(testUri, adjuster, testUriAfterEdit, additionalAssertions, null);
    }

    public static void testAdjustUri(String testUri, Consumer<SlingUriBuilder> adjuster, String testUriAfterEdit,
            Consumer<SlingUri> additionalAssertions, ResourceResolver resourceResolver) {
        SlingUri slingUri = SlingUriBuilder.parse(testUri, resourceResolver).build();

        SlingUri adjustedSlingUri = slingUri.adjust(adjuster);

        assertEquals(testUriAfterEdit, adjustedSlingUri.toString());
        assertEquals(testUriAfterEdit, adjustedSlingUri.toUri().toString());

        additionalAssertions.accept(adjustedSlingUri);
    }

}
