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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SlingUriTest {

    @Mock
    ResourceResolver resolver;

    @Test
    public void testFullSlingUri() {

        String testUriStr = "http://host.com/test/to/path.html";
        testUri(testUriStr, false, false, false, true, false, slingUri -> {
            assertEquals("http", slingUri.getScheme());
            assertEquals("//host.com/test/to/path.html", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("host.com", slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertArrayEquals(new String[] {}, slingUri.getSelectors());
            assertEquals("html", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));

    }

    @Test
    public void testFullSlingUriComplex() {

        String testUriStr = "https://test:pw@host.com:888/test/to/path.sel1.json/suffix/path?p1=2&p2=3#frag3939";
        testUri(testUriStr, false, false, false, true, false, slingUri -> {
            assertEquals("https", slingUri.getScheme());
            assertEquals("//test:pw@host.com:888/test/to/path.sel1.json/suffix/path?p1=2&p2=3", slingUri.getSchemeSpecificPart());
            assertEquals("test:pw", slingUri.getUserInfo());
            assertEquals("host.com", slingUri.getHost());
            assertEquals(888, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals("sel1", slingUri.getSelectorString());
            assertArrayEquals(new String[] { "sel1" }, slingUri.getSelectors());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path", slingUri.getSuffix());
            assertEquals("p1=2&p2=3", slingUri.getQuery());
            assertEquals("frag3939", slingUri.getFragment());
        }, asList(resolver, null));

    }

    @Test
    public void testSlingUriDomainIPv4() {

        String testUriStr = "https://88.39.107.39:888/test/to/path.sel1.json/suffix/path?p1=2&p2=3#frag3939";
        testUri(testUriStr, false, false, false, true, false, slingUri -> {
            assertEquals("https", slingUri.getScheme());
            assertEquals("//88.39.107.39:888/test/to/path.sel1.json/suffix/path?p1=2&p2=3", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("88.39.107.39", slingUri.getHost());
            assertEquals(888, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals("sel1", slingUri.getSelectorString());
            assertArrayEquals(new String[] { "sel1" }, slingUri.getSelectors());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path", slingUri.getSuffix());
            assertEquals("p1=2&p2=3", slingUri.getQuery());
            assertEquals("frag3939", slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testSlingUriDomainIPv6() {

        String testUriStr = "https://[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]:8080/index.sel1.json/suffix/path?p1=2&p2=3#frag3939";
        testUri(testUriStr, false, false, false, true, false, slingUri -> {
            assertEquals("https", slingUri.getScheme());
            assertEquals("//[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]:8080/index.sel1.json/suffix/path?p1=2&p2=3",
                    slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("[FEDC:BA98:7654:3210:FEDC:BA98:7654:3210]", slingUri.getHost());
            assertEquals(8080, slingUri.getPort());
            assertEquals("/index", slingUri.getResourcePath());
            assertEquals("sel1", slingUri.getSelectorString());
            assertArrayEquals(new String[] { "sel1" }, slingUri.getSelectors());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path", slingUri.getSuffix());
            assertEquals("p1=2&p2=3", slingUri.getQuery());
            assertEquals("frag3939", slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testFullUriWithoutSchemeSimple() {

        String testUriStr = "//cdn.example.com/test/js_file.js";

        testUri(testUriStr, false, false, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals("//cdn.example.com/test/js_file.js", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("cdn.example.com", slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/js_file", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("js", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
        }, asList(resolver, null));

    }

    @Test
    public void testFullUriWithoutSchemeComplex() {

        String testUriStr = "//user:pw@cdn.example.com:3000/test/js_file.txt.js?par1=val1&par2=val2";

        testUri(testUriStr, false, false, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals("//user:pw@cdn.example.com:3000/test/js_file.txt.js?par1=val1&par2=val2", slingUri.getSchemeSpecificPart());
            assertEquals("user:pw", slingUri.getUserInfo());
            assertEquals("cdn.example.com", slingUri.getHost());
            assertEquals(3000, slingUri.getPort());
            assertEquals("/test/js_file", slingUri.getResourcePath());
            assertEquals("txt", slingUri.getSelectorString());
            assertEquals("js", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("par1=val1&par2=val2", slingUri.getQuery());
        }, asList(resolver, null));

    }
    
    @Test
    public void testAbsolutePathSlingUri() {
        String testUriStr = "/test/to/path.sel1.json/suffix/path?p1=2&p2=3#frag3939";

        testUri(testUriStr, true, true, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals("sel1", slingUri.getSelectorString());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path", slingUri.getSuffix());
            assertEquals("p1=2&p2=3", slingUri.getQuery());
            assertEquals("frag3939", slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testSlingUriSuffixWithDots() {

        String testUriStr = "/test/to/path.min.js/suffix/app.nodesbrowser.js";
        testUri(testUriStr, true, true, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals("min", slingUri.getSelectorString());
            assertEquals("js", slingUri.getExtension());
            assertEquals("/suffix/app.nodesbrowser.js", slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testSlingUriMultipleDots() {

        String testUriStr = "/test/to/path.sel1.sel2..sel4.js";
        testUri(testUriStr, true, true, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals(4, slingUri.getSelectors().length);
            assertEquals("sel1.sel2..sel4", slingUri.getSelectorString());
            assertArrayEquals(new String[] { "sel1", "sel2", "", "sel4" }, slingUri.getSelectors());
            assertEquals("js", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));

        String testUriStr2 = "/test/to/path.sel1.sel2../sel4.js";
        testUri(testUriStr2, true, true, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals(1, slingUri.getSelectors().length);
            assertEquals("sel1", slingUri.getSelectorString());
            assertEquals("sel2", slingUri.getExtension());
            assertEquals("/sel4.js", slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, null, true);
    }

    @Test
    public void testRelativePathSlingUri() {
        String testUriStr = "../path.html#frag1";

        testUri(testUriStr, true, false, true, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("../path", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("html", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
            assertEquals("frag1", slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testRelativePathSlingUriComplex() {
        String testUriStr = "../path/./deep/path/../path.sel1.sel2.html?test=1#frag1";

        testUri(testUriStr, true, false, true, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("../path/./deep/path/../path", slingUri.getResourcePath());
            assertEquals("sel1.sel2", slingUri.getSelectorString());
            assertArrayEquals(new String[] { "sel1", "sel2" }, slingUri.getSelectors());
            assertEquals("html", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("test=1", slingUri.getQuery());
            assertEquals("frag1", slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testAbsolutePathWithPathParameter() {
        String testUriStr = "/test/to/path;v='1.0'.sel1.html/suffix/path?p1=2&p2=3#frag3939";

        testUri(testUriStr, true, true, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals("sel1", slingUri.getSelectorString());
            assertEquals("html", slingUri.getExtension());
            assertEquals(1, slingUri.getPathParameters().size());
            assertEquals("1.0", slingUri.getPathParameters().get("v"));
            assertEquals("/suffix/path", slingUri.getSuffix());
            assertEquals("p1=2&p2=3", slingUri.getQuery());
            assertEquals("frag3939", slingUri.getFragment());
        }, asList(resolver, null));

        String testUriStr2 = "/test/to/file;foo='bar'.sel1.sel2.json/suffix/path";
        testUri(testUriStr2, true, true, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/to/file", slingUri.getResourcePath());
            assertEquals("sel1.sel2", slingUri.getSelectorString());
            assertEquals("json", slingUri.getExtension());
            assertEquals(1, slingUri.getPathParameters().size());
            assertEquals("bar", slingUri.getPathParameters().get("foo"));
            assertEquals("/suffix/path", slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testAbsolutePathWithPathParameterMultiple() {
        String testUriStr = "/test/to/path;v='1.0';antotherParam='test/nested';antotherParam2='7'.sel1.html/suffix/path?p1=2&p2=3#frag3939";

        testUri(testUriStr, true, true, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals("sel1", slingUri.getSelectorString());
            assertEquals("html", slingUri.getExtension());

            assertEquals(3, slingUri.getPathParameters().size());
            assertEquals("1.0", slingUri.getPathParameters().get("v"));
            assertEquals("test/nested", slingUri.getPathParameters().get("antotherParam"));
            assertEquals("7", slingUri.getPathParameters().get("antotherParam2"));

            assertEquals("/suffix/path", slingUri.getSuffix());
            assertEquals("p1=2&p2=3", slingUri.getQuery());
            assertEquals("frag3939", slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testAbsolutePathWithPathParameterAfterExtension() {
        String testUriStr = "/test/to/path.sel1.html;v='1.0'/suffix/path?p1=2&p2=3#frag3939";

        SlingUri testUri = testUri(testUriStr, true, true, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/test/to/path", slingUri.getResourcePath());
            assertEquals("sel1", slingUri.getSelectorString());
            assertEquals("html", slingUri.getExtension());
            assertEquals(1, slingUri.getPathParameters().size());
            assertEquals("1.0", slingUri.getPathParameters().get("v"));
            assertEquals("/suffix/path", slingUri.getSuffix());
            assertEquals("p1=2&p2=3", slingUri.getQuery());
            assertEquals("frag3939", slingUri.getFragment());
        }, null, true /* URL is restructured (parameter moved to end), assertion below */);

        assertEquals("/test/to/path;v='1.0'.sel1.html/suffix/path?p1=2&p2=3#frag3939", testUri.toString());

    }

    @Test
    public void testJavascriptUri() {
        testUri("javascript:void(0)", false, false, false, true, true, slingUri -> {
            assertEquals("javascript", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("void(0)", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testJavascriptUriLong() {

        testUri("javascript:console.log('long js'); return true;", false, false, false, true, true, slingUri -> {
            assertEquals("javascript", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("console.log('long js'); return true;", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, null, true);

    }

    @Test
    public void testMailtotUri() {
        String testUriStr = "mailto:jon.doe@example.com";

        testUri(testUriStr, false, false, false, true, true, slingUri -> {
            assertEquals("mailto", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("jon.doe@example.com", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testFileUriLocal() {
        testUri("file:///path/to/test/file.txt", false, false, false, true, false, slingUri -> {
            assertEquals("file", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("", slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/path/to/test/file", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("txt", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("///path/to/test/file.txt", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testFileUriWithHost() {
        testUri("file://example.com:80/path/to/test/file.txt", false, false, false, true, false, slingUri -> {
            assertEquals("file", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("example.com", slingUri.getHost());
            assertEquals(80, slingUri.getPort());
            assertEquals("/path/to/test/file", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("txt", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("//example.com:80/path/to/test/file.txt", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testSpecialFileUris() {
        testUri("file:/path/to/test/file.txt", false, false, false, true, false, slingUri -> {
            assertEquals("file", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("", slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/path/to/test/file", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("txt", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("///path/to/test/file.txt", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
            assertEquals("file:///path/to/test/file.txt", slingUri.toString());
        }, null, true /* single slash is expanded to correct three slashes */);

        testUri("file:///c:/WINDOWS/clock.avi", false, false, false, true, false, slingUri -> {
            assertEquals("file", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("", slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/c:/WINDOWS/clock", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("avi", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("///c:/WINDOWS/clock.avi", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testCalltoUri() {
        testUri("callto:screenname", false, false, false, true, true, slingUri -> {
            assertEquals("callto", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("screenname", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));

    }

    @Test
    public void testSmsUri() {

        testUri("sms:+15105550101,+15105550102?body=hello%20there", false, false, false, true, true, slingUri -> {
            assertEquals("sms", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("+15105550101,+15105550102?body=hello%20there", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testRsyncUri() {

        testUri("rsync://testhost:333/path", false, false, false, true, false, slingUri -> {
            assertEquals("rsync", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("testhost", slingUri.getHost());
            assertEquals(333, slingUri.getPort());
            assertEquals("/path", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("//testhost:333/path", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testViewSourceUri() {
        testUri("view-source:http://en.wikipedia.org/wiki/URI_scheme", false, false, false, true, true, slingUri -> {
            assertEquals("view-source", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("http://en.wikipedia.org/wiki/URI_scheme", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testGitUrl() {
        testUri("git://github.com/user/project-name.git", false, false, false, true, false, slingUri -> {
            assertEquals("git", slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals("github.com", slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals("/user/project-name", slingUri.getResourcePath());

            assertEquals(null, slingUri.getSelectorString());
            assertEquals("git", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("//github.com/user/project-name.git", slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testHashOnlyUri() {

        testUri("#", false, false, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
            assertEquals("", slingUri.getFragment());
        }, asList(resolver, null));

        testUri("#fragment", false, false, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
            assertEquals("fragment", slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testQueryOnlyUri() {

        testUri("?", false, false, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("", slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));

        testUri("?test=test", false, false, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getScheme());
            assertEquals(null, slingUri.getUserInfo());
            assertEquals(null, slingUri.getHost());
            assertEquals(-1, slingUri.getPort());
            assertEquals(null, slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
            assertEquals("test=test", slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testUnusualQueryFragmentCombinations() {
        testUri("?#", false, false, false, false, false, slingUri -> {
            assertEquals("", slingUri.getQuery());
            assertEquals("", slingUri.getFragment());
        }, asList(resolver, null));
        testUri("?t=2#", false, false, false, false, false, slingUri -> {
            assertEquals("t=2", slingUri.getQuery());
            assertEquals("", slingUri.getFragment());
        }, asList(resolver, null));
        testUri("?#t=3", false, false, false, false, false, slingUri -> {
            assertEquals("", slingUri.getQuery());
            assertEquals("t=3", slingUri.getFragment());
        }, asList(resolver, null));
        testUri("", false, false, false, false, false, slingUri -> {
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testDataUrl() {

        // data url according to https://tools.ietf.org/html/rfc2397
        String dataUrl = "data:image/gif;base64,R0lGODdhMAAwAPAAAAAAAP///ywAAAAAMAAw" +
                "AAAC8IyPqcvt3wCcDkiLc7C0qwyGHhSWpjQu5yqmCYsapyuvUUlvONmOZtfzgFz" +
                "ByTB10QgxOR0TqBQejhRNzOfkVJ+5YiUqrXF5Y5lKh/DeuNcP5yLWGsEbtLiOSp" +
                "a/TPg7JpJHxyendzWTBfX0cxOnKPjgBzi4diinWGdkF8kjdfnycQZXZeYGejmJl" +
                "ZeGl9i2icVqaNVailT6F5iJ90m6mvuTS4OK05M0vDk0Q4XUtwvKOzrcd3iq9uis" +
                "F81M1OIcR7lEewwcLp7tuNNkM3uNna3F2JQFo97Vriy/Xl4/f1cf5VWzXyym7PH" +
                "hhx4dbgYKAAA7";

        testUri(dataUrl, false, false, false, true, true, slingUri -> {
            assertEquals("data", slingUri.getScheme());
            assertEquals(StringUtils.substringAfter(dataUrl, ":"), slingUri.getSchemeSpecificPart());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testWellKnownUri() {

        // well-known URIs as specified in https://tools.ietf.org/html/rfc8615
        String dataUrl = "/.well-known/change-password";

        testUri(dataUrl, true, true, false, false, false, slingUri -> {
            assertEquals("/", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("well-known", slingUri.getExtension());
            assertEquals("/change-password", slingUri.getSuffix());
            assertEquals(null, slingUri.getQuery());
            assertEquals(null, slingUri.getFragment());
        }, asList(resolver, null));
    }

    @Test
    public void testGetSuffixResource() {
        Resource expectedSuffixResource = mock(Resource.class);
        when(resolver.getResource("/suffix/path")).thenReturn(expectedSuffixResource);
        SlingUri slingUri = SlingUriBuilder.parse("/test/to/path.sel1.json/suffix/path?p1=2&p2=3#frag3939", resolver).build();
        Resource suffixResource = slingUri.getSuffixResource();
        assertTrue("Suffix resource equal to mock", expectedSuffixResource == suffixResource);

        SlingUri slingUriDifferentSuffix = slingUri.adjust(b -> b.setSuffix("/suffix/path/non-existing"));
        assertNull("Suffix resource is null if resource does not exist", slingUriDifferentSuffix.getSuffixResource());

        SlingUri slingUriNoSuffix = slingUri.adjust(b -> b.setSuffix(null));
        assertNull("Suffix resource is null if suffix is null", slingUriNoSuffix.getSuffixResource());

    }

    // -- helper methods
    public static void testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isAbsolute,
            boolean isOpaque, Consumer<SlingUri> additionalAssertions, List<ResourceResolver> resourceResolvers) {
        for (ResourceResolver rr : resourceResolvers) {
            testUri(testUri, isPath, isAbsolutePath, isRelativePath, isAbsolute, isOpaque, additionalAssertions, rr);
        }
    }

    public static SlingUri testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isAbsolute,
            boolean isOpaque, Consumer<SlingUri> additionalAssertions, ResourceResolver resourceResolver) {
        return testUri(testUri, isPath, isAbsolutePath, isRelativePath, isAbsolute, isOpaque, additionalAssertions, resourceResolver,
                false);
    }

    public static SlingUri testUri(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath, boolean isAbsolute,
            boolean isOpaque, Consumer<SlingUri> additionalAssertions, ResourceResolver resourceResolver, boolean urlIsRestructured) {
        SlingUri slingUri = SlingUriBuilder.parse(testUri, resourceResolver).build();

        if (!urlIsRestructured) {
            URI javaUri = slingUri.toUri();
            assertEquals("Uri toString() same as input", testUri, slingUri.toString());
            assertEquals("Uri toUri().toString() same as input", testUri, javaUri.toString());
            assertEquals("isOpaque() matches to java URI impl", javaUri.isOpaque(), slingUri.isOpaque());
            assertEquals("getSchemeSpecificPart() matches to java URI impl", javaUri.getRawSchemeSpecificPart(),
                    slingUri.getSchemeSpecificPart());
            assertEquals("getFragment() matches to java URI impl", javaUri.getRawFragment(), slingUri.getFragment());
            assertEquals("getQuery() matches to java URI impl", javaUri.getRawQuery(), slingUri.getQuery());
            assertEquals("isAbsolute() matches to java URI impl", javaUri.isAbsolute(), slingUri.isAbsolute());
        }

        assertEquals("isPath()", isPath, slingUri.isPath());
        assertEquals("isAbsolutePath()", isAbsolutePath, slingUri.isAbsolutePath());
        assertEquals("isRelativePath()", isRelativePath, slingUri.isRelativePath());
        assertEquals("isAbsolute()", isAbsolute, slingUri.isAbsolute());
        assertEquals("isOpaque()", isOpaque, slingUri.isOpaque());

        additionalAssertions.accept(slingUri);

        SlingUri slingUriParsedFromSameInput = SlingUriBuilder.parse(testUri, resourceResolver).build();
        assertEquals("uris parsed from same input are expected to be equal", slingUriParsedFromSameInput, slingUri);
        assertNotEquals("not equal to different URI", SlingUriBuilder.parse("special://host:393/path.txt/suffix", resourceResolver).build(),
                slingUri);
        assertNotEquals("not equal to null", slingUri, null);
        assertEquals("uris parsed from same input are expected to have the same hash code", slingUriParsedFromSameInput.hashCode(),
                slingUri.hashCode());

        return slingUri;
    }

}
