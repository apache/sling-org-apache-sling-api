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

import java.util.function.Consumer;

import org.junit.Test;

public class ResourceUriTest {

    @Test
    public void testFullResourceUri() {

        String testUriStr = "http://host.com/test/to/path.html";
        testUri(testUriStr, false, false, false, true, resourceUri -> {
            assertEquals("http", resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals("host.com", resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("/test/to/path", resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals("html", resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        });

    }

    @Test
    public void testFullResourceUriComplex() {

        String testUriStr = "https://test:pw@host.com:888/test/to/path.sel1.json/suffix/path?p1=2&p2=3#frag3939";
        testUri(testUriStr, false, false, false, true, resourceUri -> {
            assertEquals("https", resourceUri.getScheme());
            assertEquals("test:pw", resourceUri.getUserInfo());
            assertEquals("host.com", resourceUri.getHost());
            assertEquals(888, resourceUri.getPort());
            assertEquals("/test/to/path", resourceUri.getResourcePath());
            assertEquals("sel1", resourceUri.getSelectorString());
            assertEquals("json", resourceUri.getExtension());
            assertEquals("/suffix/path", resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals("p1=2&p2=3", resourceUri.getQuery());
            assertEquals("frag3939", resourceUri.getFragment());
        });

    }

    @Test
    public void testAbsolutePathResourceUri() {
        String testUriStr = "/test/to/path.sel1.json/suffix/path?p1=2&p2=3#frag3939";

        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("/test/to/path", resourceUri.getResourcePath());
            assertEquals("sel1", resourceUri.getSelectorString());
            assertEquals("json", resourceUri.getExtension());
            assertEquals("/suffix/path", resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals("p1=2&p2=3", resourceUri.getQuery());
            assertEquals("frag3939", resourceUri.getFragment());
        });
    }

    @Test
    public void testRelativePathResourceUri() {
        String testUriStr = "../path.html#frag1";

        testUri(testUriStr, true, false, true, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("../path", resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals("html", resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals("frag1", resourceUri.getFragment());
        });
    }

    @Test
    public void testJavascriptUri() {
        String testUriStr = "javascript:void(0)";

        testUri(testUriStr, false, false, false, false, resourceUri -> {
            assertEquals("javascript", resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals("void(0)", resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        });
    }

    @Test
    public void testMailtotUri() {
        String testUriStr = "mailto:jon.doe@example.com";

        testUri(testUriStr, false, false, false, false, resourceUri -> {
            assertEquals("mailto", resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals("jon.doe@example.com", resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        });
    }

    @Test
    public void testHashOnlyUri() {

        testUri("#", false, false, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals(null, resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals("", resourceUri.getFragment());
        });

        testUri("#fragment", false, false, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals(null, resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals("fragment", resourceUri.getFragment());
        });
    }

    @Test
    public void testQueryOnlyUri() {

        testUri("?", false, false, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals(null, resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals("", resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        });

        testUri("?test=test", false, false, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals(null, resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals("test=test", resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        });
    }

    @Test
    public void testUnusualQueryFragmentCombinations() {
        testUri("?#", false, false, false, false, resourceUri -> {
            assertEquals("", resourceUri.getQuery());
            assertEquals("", resourceUri.getFragment());
        });
        testUri("?t=2#", false, false, false, false, resourceUri -> {
            assertEquals("t=2", resourceUri.getQuery());
            assertEquals("", resourceUri.getFragment());
        });
        testUri("?#t=3", false, false, false, false, resourceUri -> {
            assertEquals("", resourceUri.getQuery());
            assertEquals("t=3", resourceUri.getFragment());
        });
        testUri("", false, false, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        });
    }

    // -- adjustment test cases

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
    public void testAdjustPathInSpecialUriWithoutEffect() {

        testAdjustUri(
                "mailto:jon.doe@example.com",
                resourceUriBuilder -> {
                    resourceUriBuilder.setPath("/path/to/resource");
                    resourceUriBuilder.setResourcePath("/path/to/resource");
                    resourceUriBuilder.addSelector("test");
                    resourceUriBuilder.setExtension("html");
                    resourceUriBuilder.setSuffix("/suffix");
                },
                "mailto:jon.doe@example.com",
                resourceUri -> {
                    assertEquals(null, resourceUri.getResourcePath());
                    assertEquals(null, resourceUri.getSelectorString());
                    assertEquals(null, resourceUri.getExtension());
                    assertEquals(null, resourceUri.getSuffix());
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

    // -- helper methods

    public void testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isFullUri,
            Consumer<ResourceUri> additionalAssertions) {
        ResourceUri resourceUri = ResourceUriBuilder.parse(testUri).build();

        assertEquals(testUri, resourceUri.toString());
        assertEquals(testUri, resourceUri.toUri().toString());

        assertEquals("isPath()", isPath, resourceUri.isPath());
        assertEquals("isAbsolutePath()", isAbsolutePath, resourceUri.isAbsolutePath());
        assertEquals("isRelativePath()", isRelativePath, resourceUri.isRelativePath());
        assertEquals("isFullUri()", isFullUri, resourceUri.isFullUri());

        additionalAssertions.accept(resourceUri);
    }

    public void testAdjustUri(String testUri, Consumer<ResourceUriBuilder> adjuster, String testUriAfterEdit,
            Consumer<ResourceUri> additionalAssertions) {
        ResourceUri resourceUri = ResourceUriBuilder.parse(testUri).build();

        ResourceUri adjustedResourceUri = resourceUri.adjust(adjuster);

        assertEquals(testUriAfterEdit, adjustedResourceUri.toString());
        assertEquals(testUriAfterEdit, adjustedResourceUri.toUri().toString());

        additionalAssertions.accept(adjustedResourceUri);
    }

}
