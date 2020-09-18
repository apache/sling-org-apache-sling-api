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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Consumer;

import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResourceUriTest {

    @Mock
    ResourceResolver resolver;

    @Test
    public void testFullResourceUri() {

        String testUriStr = "http://host.com/test/to/path.html";
        testUri(testUriStr, false, false, false, true, false, resourceUri -> {
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
        }, asList(resolver, null));

    }

    @Test
    public void testFullResourceUriComplex() {

        String testUriStr = "https://test:pw@host.com:888/test/to/path.sel1.json/suffix/path?p1=2&p2=3#frag3939";
        testUri(testUriStr, false, false, false, true, false, resourceUri -> {
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
        }, asList(resolver, null));

    }

    @Test
    public void testAbsolutePathResourceUri() {
        String testUriStr = "/test/to/path.sel1.json/suffix/path?p1=2&p2=3#frag3939";

        testUri(testUriStr, true, true, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testResourceUriSuffixWithDots() {

        String testUriStr = "/test/to/path.min.js/suffix/app.nodesbrowser.js";
        testUri(testUriStr, true, true, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testResourceUriMultipleDots() {

        String testUriStr = "/test/to/path.sel1.sel2..sel4.js";
        testUri(testUriStr, true, true, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));

        String testUriStr2 = "/test/to/path.sel1.sel2../sel4.js";
        testUri(testUriStr2, true, true, false, false, false, resourceUri -> {
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
        }, null, true);
    }

    @Test
    public void testRelativePathResourceUri() {
        String testUriStr = "../path.html#frag1";

        testUri(testUriStr, true, false, true, false, false, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testRelativePathResourceUriComplex() {
        String testUriStr = "../path/./deep/path/../path.sel1.sel2.html?test=1#frag1";

        testUri(testUriStr, true, false, true, false, false, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testAbsolutePathWithPathParameter() {
        String testUriStr = "/test/to/path;v='1.0'.sel1.html/suffix/path?p1=2&p2=3#frag3939";

        testUri(testUriStr, true, true, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));

        String testUriStr2 = "/test/to/file;foo='bar'.sel1.sel2.json/suffix/path";
        testUri(testUriStr2, true, true, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testAbsolutePathWithPathParameterMultiple() {
        String testUriStr = "/test/to/path;v='1.0';antotherParam='test/nested';antotherParam2='7'.sel1.html/suffix/path?p1=2&p2=3#frag3939";

        testUri(testUriStr, true, true, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testAbsolutePathWithPathParameterAfterExtension() {
        String testUriStr = "/test/to/path.sel1.html;v='1.0'/suffix/path?p1=2&p2=3#frag3939";

        ResourceUri testUri = testUri(testUriStr, true, true, false, false, false, resourceUri -> {
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
        }, null, true /* URL is restructured (parameter moved to end), assertion below */);

        assertEquals("/test/to/path;v='1.0'.sel1.html/suffix/path?p1=2&p2=3#frag3939", testUri.toString());

    }

    @Test
    public void testJavascriptUri() {
        String testUriStr = "javascript:void(0)";

        testUri(testUriStr, false, false, false, false, true, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testMailtotUri() {
        String testUriStr = "mailto:jon.doe@example.com";

        testUri(testUriStr, false, false, false, false, true, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testHashOnlyUri() {

        testUri("#", false, false, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));

        testUri("#fragment", false, false, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testQueryOnlyUri() {

        testUri("?", false, false, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));

        testUri("?test=test", false, false, false, false, false, resourceUri -> {
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
        }, asList(resolver, null));
    }

    @Test
    public void testUnusualQueryFragmentCombinations() {
        testUri("?#", false, false, false, false, false, resourceUri -> {
            assertEquals("", resourceUri.getQuery());
            assertEquals("", resourceUri.getFragment());
        }, asList(resolver, null));
        testUri("?t=2#", false, false, false, false, false, resourceUri -> {
            assertEquals("t=2", resourceUri.getQuery());
            assertEquals("", resourceUri.getFragment());
        }, asList(resolver, null));
        testUri("?#t=3", false, false, false, false, false, resourceUri -> {
            assertEquals("", resourceUri.getQuery());
            assertEquals("t=3", resourceUri.getFragment());
        }, asList(resolver, null));
        testUri("", false, false, false, false, false, resourceUri -> {
            assertEquals(null, resourceUri.getQuery());
            assertEquals(null, resourceUri.getFragment());
        }, asList(resolver, null));
    }

    // -- helper methods
    public static void testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isFullUri,
            boolean isOpaque, Consumer<ResourceUri> additionalAssertions, List<ResourceResolver> resourceResolvers) {
        for (ResourceResolver rr : resourceResolvers) {
            testUri(testUri, isPath, isAbsolutePath, isRelativePath, isFullUri, isOpaque, additionalAssertions, rr);
        }
    }

    public static ResourceUri testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isFullUri,
            boolean isOpaque, Consumer<ResourceUri> additionalAssertions) {
        return testUri(testUri, isPath, isAbsolutePath, isRelativePath, isFullUri, isOpaque, additionalAssertions, (ResourceResolver) null);
    }

    public static ResourceUri testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isFullUri,
            boolean isOpaque, Consumer<ResourceUri> additionalAssertions, ResourceResolver resourceResolver) {
        return testUri(testUri, isPath, isAbsolutePath, isRelativePath, isFullUri, isOpaque, additionalAssertions, resourceResolver, false);
    }

    public static ResourceUri testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isFullUri,
            boolean isOpaque, Consumer<ResourceUri> additionalAssertions, ResourceResolver resourceResolver, boolean urlIsRestructured) {
        ResourceUri resourceUri = ResourceUriBuilder.parse(testUri, resourceResolver).build();

        if (!urlIsRestructured) {
            assertEquals("Uri toString() same as input", testUri, resourceUri.toString());
            assertEquals("Uri toUri().toString() same as input", testUri, resourceUri.toUri().toString());
        }

        assertEquals("isPath()", isPath, resourceUri.isPath());
        assertEquals("isAbsolutePath()", isAbsolutePath, resourceUri.isAbsolutePath());
        assertEquals("isRelativePath()", isRelativePath, resourceUri.isRelativePath());
        assertEquals("isFullUri()", isFullUri, resourceUri.isFullUri());
        assertEquals("isOpaque()", isOpaque, resourceUri.isOpaque());
        assertEquals("isOpaque() matches to java URI impl", resourceUri.toUri().isOpaque(), resourceUri.isOpaque());

        additionalAssertions.accept(resourceUri);

        ResourceUri resourceUriParsedFromSameInput = ResourceUriBuilder.parse(testUri, resourceResolver).build();
        assertEquals("uris parsed from same input are expected to be equal", resourceUriParsedFromSameInput, resourceUri);
        assertEquals("uris parsed from same input are expected to have the same hash code", resourceUriParsedFromSameInput.hashCode(),
                resourceUri.hashCode());

        return resourceUri;
    }

}
