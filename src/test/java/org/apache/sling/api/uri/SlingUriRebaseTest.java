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

import static org.apache.sling.api.uri.SlingUriTest.testUri;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SlingUriRebaseTest {

    private static final String FULL_URI = "/test/to/file.ext.sel1.json/suffix/path.js";

    @Mock
    ResourceResolver resolver;

    @Mock
    Resource resource;

    @Test
    public void testRebaseResourcePathSimplePath() {
        String testUriStrSimple = "/test/to/file";
        when(resolver.getResource("/test/to/file")).thenReturn(resource);
        testUri(testUriStrSimple, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
        }, resolver);
    }

    @Test
    public void testRebaseResourcePathSimpleResourcePathIncludesExtension() {

        String testUriStrSimpleFile = "/test/to/file.css";
        when(resolver.getResource("/test/to/file.css")).thenReturn(resource);
        when(resolver.getResource("/test/to/file")).thenReturn(null);
        testUri(testUriStrSimpleFile, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file.css", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
        }, resolver);
    }

    @Test
    public void testRebaseResourcePathSimpleResourcePathExcludesExtension() {
        String testUriStrSimplePage = "/path/to/page.html";
        when(resolver.getResource("/path/to/page.html")).thenReturn(null);
        when(resolver.getResource("/path/to/page")).thenReturn(resource);
        testUri(testUriStrSimplePage, true, true, false, false, false, slingUri -> {
            assertEquals("/path/to/page", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("html", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
        }, resolver);
    }

    @Test
    public void testRebaseResourcePathFullPathIsResource() {

        // pull path with suffix is resource path
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(resource);
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resolver.getResource("/test/to/file")).thenReturn(null);
        testUri(FULL_URI, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file.ext.sel1.json/suffix/path.js", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals(null, slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
        }, resolver);
    }

    @Test
    public void testRebaseResourcePathPartOfSuffixIsResource() {
        //
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path")).thenReturn(resource);
        when(resolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resolver.getResource("/test/to/file")).thenReturn(null);
        testUri(FULL_URI, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file.ext.sel1.json/suffix/path", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("js", slingUri.getExtension());
            assertEquals(null, slingUri.getSuffix());
        }, resolver);
    }

    @Test
    public void testRebaseResourcePathWithSelectorsAndExtension() {
        // if the resource for before the suffix part exits, it is ignored
        // compare org.apache.sling.resourceresolver.impl.helper.ResourcePathIteratorTest.testMixed()
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(resource);
        when(resolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resolver.getResource("/test/to/file")).thenReturn(null);
        testUri(FULL_URI, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file", slingUri.getResourcePath());
            assertEquals("ext.sel1", slingUri.getSelectorString());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path.js", slingUri.getSuffix());
        }, resolver);

    }

    @Test
    public void testRebaseResourcePathContainsTwoDots() {
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1")).thenReturn(resource);
        when(resolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resolver.getResource("/test/to/file")).thenReturn(null);
        testUri(FULL_URI, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file.ext.sel1", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path.js", slingUri.getSuffix());
        }, resolver);

    }

    @Test
    public void testRebaseResourcePathContainsExtension() {
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext")).thenReturn(resource);
        when(resolver.getResource("/test/to/file")).thenReturn(null);
        testUri(FULL_URI, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file.ext", slingUri.getResourcePath());
            assertEquals("sel1", slingUri.getSelectorString());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path.js", slingUri.getSuffix());
        }, resolver);

    }

    @Test
    public void testRebaseResourcePathWithoutDot() {
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resolver.getResource("/test/to/file")).thenReturn(resource);
        testUri(FULL_URI, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file", slingUri.getResourcePath());
            assertEquals("ext.sel1", slingUri.getSelectorString());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path.js", slingUri.getSuffix());
        }, resolver);
    }

    @Test
    public void testRebaseResourcePathLongestMatchingPathWins() {
        // side by side resources in same folder: the longest path wins
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1")).thenReturn(resource);
        when(resolver.getResource("/test/to/file.ext")).thenReturn(resource);
        when(resolver.getResource("/test/to/file")).thenReturn(resource);
        testUri(FULL_URI, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file.ext.sel1", slingUri.getResourcePath());
            assertEquals(null, slingUri.getSelectorString());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path.js", slingUri.getSuffix());
        }, resolver);
    }

    @Test
    public void testRebaseResourcePathNoResourceExistsAtAll() {
        // if no resource exists at all the first dot has to be taken as split
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path.js")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json/suffix/path")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1.json")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext.sel1")).thenReturn(null);
        when(resolver.getResource("/test/to/file.ext")).thenReturn(null);
        when(resolver.getResource("/test/to/file")).thenReturn(null);
        testUri(FULL_URI, true, true, false, false, false, slingUri -> {
            assertEquals("/test/to/file", slingUri.getResourcePath());
            assertEquals("ext.sel1", slingUri.getSelectorString());
            assertEquals("json", slingUri.getExtension());
            assertEquals("/suffix/path.js", slingUri.getSuffix());
        }, resolver);
    }

    @Test(expected = IllegalStateException.class)
    public void testRebaseNotAllowedWithoutResolver() throws URISyntaxException {

        String testPath = "/path/to/page.html";
        SlingUriBuilder.parse(testPath, null)
                .rebaseResourcePath()
                .build();
    }

}
