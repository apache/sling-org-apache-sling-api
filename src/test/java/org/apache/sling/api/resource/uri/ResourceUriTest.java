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
import static org.mockito.Mockito.when;

import java.util.function.Consumer;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ResourceUriTest {

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Resource resource;

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
    public void testResourceUriSuffixWithDots() {

        String testUriStr = "/test/to/path.min.js/suffix/app.nodesbrowser.js";
        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("/test/to/path", resourceUri.getResourcePath());
            assertEquals("min", resourceUri.getSelectorString());
            assertEquals("js", resourceUri.getExtension());
            assertEquals("/suffix/app.nodesbrowser.js", resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        });
    }

    @Test
    public void testResourceUriMultipleDots() {

        String testUriStr = "/test/to/path.sel1.sel2..sel4.js";
        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("/test/to/path", resourceUri.getResourcePath());
            assertEquals(4, resourceUri.getSelectors().length);
            assertEquals("sel1.sel2..sel4", resourceUri.getSelectorString());
            assertEquals("js", resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        });
        
        String testUriStr2 = "/test/to/path.sel1.sel2../sel4.js";
        testUri(testUriStr2, true, true, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("/test/to/path", resourceUri.getResourcePath());
            assertEquals(1, resourceUri.getSelectors().length);
            assertEquals("sel1", resourceUri.getSelectorString());
            assertEquals("sel2", resourceUri.getExtension());
            assertEquals("/sel4.js", resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        }, true);
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
    public void testRelativePathResourceUriComplex() {
        String testUriStr = "../path/./deep/path/../path.sel1.sel2.html?test=1#frag1";

        testUri(testUriStr, true, false, true, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("../path/./deep/path/../path", resourceUri.getResourcePath());
            assertEquals("sel1.sel2", resourceUri.getSelectorString());
            assertEquals("html", resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals("test=1", resourceUri.getQuery());
            assertEquals("frag1", resourceUri.getFragment());
        });
    }

    @Test
    public void testAbsolutePathWithPathParameter() {
        String testUriStr = "/test/to/path;v='1.0'.sel1.html/suffix/path?p1=2&p2=3#frag3939";

        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("/test/to/path", resourceUri.getResourcePath());
            assertEquals("sel1", resourceUri.getSelectorString());
            assertEquals("html", resourceUri.getExtension());
            assertEquals(1, resourceUri.getPathParameters().size());
            assertEquals("1.0", resourceUri.getPathParameters().get("v"));
            assertEquals("/suffix/path", resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals("p1=2&p2=3", resourceUri.getQuery());
            assertEquals("frag3939", resourceUri.getFragment());
        });

        String testUriStr2 = "/test/to/file;foo='bar'.sel1.sel2.json/suffix/path";
        testUri(testUriStr2, true, true, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("/test/to/file", resourceUri.getResourcePath());
            assertEquals("sel1.sel2", resourceUri.getSelectorString());
            assertEquals("json", resourceUri.getExtension());
            assertEquals(1, resourceUri.getPathParameters().size());
            assertEquals("bar", resourceUri.getPathParameters().get("foo"));
            assertEquals("/suffix/path", resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals(null, resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        });
    }

    @Test
    public void testAbsolutePathWithPathParameterMultiple() {
        String testUriStr = "/test/to/path;v='1.0';antotherParam='test/nested';antotherParam2='7'.sel1.html/suffix/path?p1=2&p2=3#frag3939";

        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("/test/to/path", resourceUri.getResourcePath());
            assertEquals("sel1", resourceUri.getSelectorString());
            assertEquals("html", resourceUri.getExtension());

            assertEquals(3, resourceUri.getPathParameters().size());
            assertEquals("1.0", resourceUri.getPathParameters().get("v"));
            assertEquals("test/nested", resourceUri.getPathParameters().get("antotherParam"));
            assertEquals("7", resourceUri.getPathParameters().get("antotherParam2"));

            assertEquals("/suffix/path", resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals("p1=2&p2=3", resourceUri.getQuery());
            assertEquals("frag3939", resourceUri.getFragment());
        });
    }

    @Test
    public void testAbsolutePathWithPathParameterAfterExtension() {
        String testUriStr = "/test/to/path.sel1.html;v='1.0'/suffix/path?p1=2&p2=3#frag3939";

        ResourceUri testUri = testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getScheme());
            assertEquals(null, resourceUri.getUserInfo());
            assertEquals(null, resourceUri.getHost());
            assertEquals(-1, resourceUri.getPort());
            assertEquals("/test/to/path", resourceUri.getResourcePath());
            assertEquals("sel1", resourceUri.getSelectorString());
            assertEquals("html", resourceUri.getExtension());
            assertEquals(1, resourceUri.getPathParameters().size());
            assertEquals("1.0", resourceUri.getPathParameters().get("v"));
            assertEquals("/suffix/path", resourceUri.getSuffix());
            assertEquals(null, resourceUri.getSchemeSpecificPart());
            assertEquals("p1=2&p2=3", resourceUri.getQuery());
            assertEquals("frag3939", resourceUri.getFragment());
        }, true /* URL is restructured (parameter moved to end), assertion below */);

        assertEquals("/test/to/path;v='1.0'.sel1.html/suffix/path?p1=2&p2=3#frag3939", testUri.toString());

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
    public void testBalanceResourcePathSimpleCases() {
        // simple case
        String testUriStrSimple = "/test/to/file";
        when(resourceResolver.getResource("/test/to/file")).thenReturn(resource);
        testUri(testUriStrSimple, true, true, false, false, resourceUri -> {
            assertEquals("/test/to/file", resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
        });

        // simple file case
        String testUriStrSimpleFile = "/test/to/file.css";
        when(resourceResolver.getResource("/test/to/file.css")).thenReturn(resource);
        when(resourceResolver.getResource("/test/to/file")).thenReturn(null);
        testUri(testUriStrSimpleFile, true, true, false, false, resourceUri -> {
            assertEquals("/test/to/file.css", resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
        });

        // simple html rendering case
        String testUriStrSimplePage = "/path/to/page.html";
        when(resourceResolver.getResource("/path/to/page.html")).thenReturn(null);
        when(resourceResolver.getResource("/path/to/page")).thenReturn(resource);
        testUri(testUriStrSimplePage, true, true, false, false, resourceUri -> {
            assertEquals("/path/to/page", resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals("html", resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
        });
    }

    @Test
    public void testBalanceResourcePathWithSelectorsAndExtension() {

        String testUriStr = "/test/to/file.ext.sel1.json/suffix/path.js";

        // pull path with suffix is resource path
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(resource);
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file")).thenReturn(null);
        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals("/test/to/file.ext.sel1.json/suffix/path.js", resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals(null, resourceUri.getSuffix());
        });

        // full path without suffix is resource path
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(resource);
        when(resourceResolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file")).thenReturn(null);
        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals("/test/to/file.ext.sel1.json", resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals(null, resourceUri.getExtension());
            assertEquals("/suffix/path.js", resourceUri.getSuffix());
        });

        // mix of extension and resource path with dots
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1")).thenReturn(resource);
        when(resourceResolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file")).thenReturn(null);
        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals("/test/to/file.ext.sel1", resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals("json", resourceUri.getExtension());
            assertEquals("/suffix/path.js", resourceUri.getSuffix());
        });

        when(resourceResolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext")).thenReturn(resource);
        when(resourceResolver.getResource("/test/to/file")).thenReturn(null);
        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals("/test/to/file.ext", resourceUri.getResourcePath());
            assertEquals("sel1", resourceUri.getSelectorString());
            assertEquals("json", resourceUri.getExtension());
            assertEquals("/suffix/path.js", resourceUri.getSuffix());
        });

        // usual case: resource path does not contain dot
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file")).thenReturn(resource);
        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals("/test/to/file", resourceUri.getResourcePath());
            assertEquals("ext.sel1", resourceUri.getSelectorString());
            assertEquals("json", resourceUri.getExtension());
            assertEquals("/suffix/path.js", resourceUri.getSuffix());
        });

        // side by side resources in same folder: the longest path wins
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resourceResolver.getResource("/test/to/file.ext.sel1")).thenReturn(resource);
        when(resourceResolver.getResource("/test/to/file.ext")).thenReturn(resource);
        when(resourceResolver.getResource("/test/to/file")).thenReturn(resource);
        testUri(testUriStr, true, true, false, false, resourceUri -> {
            assertEquals("/test/to/file.ext.sel1", resourceUri.getResourcePath());
            assertEquals(null, resourceUri.getSelectorString());
            assertEquals("json", resourceUri.getExtension());
            assertEquals("/suffix/path.js", resourceUri.getSuffix());
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


    public ResourceUri testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isFullUri,
            Consumer<ResourceUri> additionalAssertions) {
        return testUri(testUri, isPath, isAbsolutePath, isRelativePath, isFullUri, additionalAssertions, false);
    }

    public ResourceUri testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isFullUri,
            Consumer<ResourceUri> additionalAssertions, boolean urlIsRestructured) {
        ResourceUri resourceUri = ResourceUriBuilder.parse(testUri, resourceResolver).build();

        if (!urlIsRestructured) {
            assertEquals(testUri, resourceUri.toString());
            assertEquals(testUri, resourceUri.toUri().toString());
        }

        assertEquals("isPath()", isPath, resourceUri.isPath());
        assertEquals("isAbsolutePath()", isAbsolutePath, resourceUri.isAbsolutePath());
        assertEquals("isRelativePath()", isRelativePath, resourceUri.isRelativePath());
        assertEquals("isFullUri()", isFullUri, resourceUri.isFullUri());

        additionalAssertions.accept(resourceUri);

        return resourceUri;
    }

    public void testAdjustUri(String testUri, Consumer<ResourceUriBuilder> adjuster, String testUriAfterEdit,
            Consumer<ResourceUri> additionalAssertions) {
        ResourceUri resourceUri = ResourceUriBuilder.parse(testUri, resourceResolver).build();

        ResourceUri adjustedResourceUri = resourceUri.adjust(adjuster);

        assertEquals(testUriAfterEdit, adjustedResourceUri.toString());
        assertEquals(testUriAfterEdit, adjustedResourceUri.toUri().toString());

        additionalAssertions.accept(adjustedResourceUri);
    }

}
