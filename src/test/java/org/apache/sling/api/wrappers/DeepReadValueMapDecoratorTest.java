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
package org.apache.sling.api.wrappers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class DeepReadValueMapDecoratorTest {

    private ResourceResolver resolver;
    private DeepReadValueMapDecorator decorator;
    private ByteArrayOutputStream logOutput;
    private PrintStream originalError;
    private PrintStream capturedError;

    @Before
    public void setUp() {
        originalError = System.err;
        logOutput = new ByteArrayOutputStream();
        capturedError = new PrintStream(logOutput, true, StandardCharsets.UTF_8);
        System.setErr(capturedError);
        resolver = mock(ResourceResolver.class);
        decorator = createDecorator("/content/site/page");
        logOutput.reset();
    }

    @After
    public void tearDown() {
        System.setErr(originalError);
        capturedError.close();
    }

    private DeepReadValueMapDecorator createDecorator(final String path) {
        final Resource resource = mock(Resource.class);
        when(resource.getPath()).thenReturn(path);
        when(resource.getResourceResolver()).thenReturn(resolver);
        return new DeepReadValueMapDecorator(resource, new ValueMapDecorator(Map.of("title", "base")));
    }

    // Stub the original lookup string: normalization must only affect diagnostics.
    private void stubTarget(final String path) {
        final Resource target = mock(Resource.class);
        when(resolver.getResource(path)).thenReturn(target);
        when(target.adaptTo(ValueMap.class)).thenReturn(new ValueMapDecorator(Map.of("title", "target")));
    }

    private String logs() {
        return logOutput.toString(StandardCharsets.UTF_8);
    }

    private void assertWarning(final String resourcePath, final String requestedPath, final long count) {
        assertEquals(count, logs().lines().filter(line -> line.contains("WARN")).count());
        assertTrue(logs().contains("resource '" + resourcePath + "', requested path '" + requestedPath + "'"));
    }

    @Test
    public void testParentTraversalWarnsAndContinuesForAllReadMethods() {
        final String requestedPath = "/content/site/page/../sibling";
        stubTarget(requestedPath);

        assertEquals("target", decorator.get("../sibling/title"));
        assertEquals("target", decorator.get("../sibling/title", String.class));
        assertEquals("target", decorator.get("../sibling/title", "default"));
        assertTrue(decorator.containsKey("../sibling/title"));
        assertWarning("/content/site/page", requestedPath, 4);
    }

    @Test
    public void testContainedPathsDoNotWarn() {
        for (final String basePath : new String[] {"/content/site/page", "/content//site/page/"}) {
            final DeepReadValueMapDecorator map = createDecorator(basePath);
            for (final String key :
                    new String[] {"child/title", "child/../title", "./title", "child//grandchild/title"}) {
                stubTarget(basePath + "/" + key.substring(0, key.lastIndexOf('/')));
                assertEquals(key, "target", map.get(key));
            }
        }
        assertTrue(logs().isEmpty());
    }

    @Test
    public void testSimilarPrefixIsOutsideSubtree() {
        final String requestedPath = "/content/site/page/../page2";
        stubTarget(requestedPath);

        assertEquals("target", decorator.get("../page2/title"));
        assertWarning("/content/site/page", requestedPath, 1);
    }

    @Test
    public void testUnnormalizablePathsWarnAndContinue() {
        for (final String key : new String[] {"../../../../secret/title", ".../title"}) {
            final String requestedPath = "/content/site/page/" + key.substring(0, key.lastIndexOf('/'));
            stubTarget(requestedPath);

            assertEquals(key, "target", decorator.get(key));
            assertWarning("/content/site/page", requestedPath, 1);
            logOutput.reset();
        }
    }

    @Test
    public void testRootWarnsOnlyForTraversalAboveRoot() {
        final DeepReadValueMapDecorator root = createDecorator("/");
        stubTarget("//content");
        stubTarget("//.");
        stubTarget("//..");

        assertEquals("target", root.get("content/title"));
        assertEquals("target", root.get("./title"));
        assertTrue(logs().isEmpty());

        assertEquals("target", root.get("../title"));
        assertWarning("/", "//..", 1);
    }

    @Test
    public void testDirectPropertyReadDoesNotResolveOrWarn() {
        assertEquals("base", decorator.get("title"));
        verifyNoInteractions(resolver);
        assertTrue(logs().isEmpty());
    }

    @Test
    public void testDiagnosticsEscapeControlCharacters() {
        final DeepReadValueMapDecorator map = createDecorator("/content/site\npage");
        stubTarget("/content/site\npage/../sibling\r\u001b\u2028");

        assertEquals("target", map.get("../sibling\r\u001b\u2028/title"));
        assertWarning("/content/site\\u000apage", "/content/site\\u000apage/../sibling\\u000d\\u001b\\u2028", 1);
    }
}
