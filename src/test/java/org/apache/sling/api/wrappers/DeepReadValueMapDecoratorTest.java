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

import org.apache.sling.api.resource.AbstractResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.impl.DeepReadValueMapConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests deep reads relative to {@code /content/site/page}, using this conceptual
 * resource structure:
 * <pre>
 * /content/site
 *   page                 starting resource; base map has title = "base"
 *     child              inside the permitted subtree
 *       grandchild       inside the permitted subtree
 *   sibling              outside the permitted subtree
 *   page2                outside, despite sharing the "/content/site/page" prefix
 * </pre>
 */
public class DeepReadValueMapDecoratorTest {

    private final DeepReadValueMapConfiguration component = new DeepReadValueMapConfiguration();
    private ResourceResolver resolver;
    private DeepReadValueMapDecorator decorator;
    private ByteArrayOutputStream logOutput;
    private PrintStream originalError;
    private PrintStream capturedError;

    @Before
    public void setUp() {
        component.deactivate();
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
        component.deactivate();
        System.setErr(originalError);
        capturedError.close();
    }

    /**
     * Creates a decorator whose starting resource has {@code title = "base"}.
     * All decorators use the same resolver mock, but this does not create
     * resources in a repository or stub any deep-read targets.
     * @param path The starting resource's path, used as the containment boundary.
     */
    private DeepReadValueMapDecorator createDecorator(final String path) {
        final Resource resource = mock(Resource.class);
        when(resource.getPath()).thenReturn(path);
        when(resource.getResourceResolver()).thenReturn(resolver);
        return new DeepReadValueMapDecorator(resource, new ValueMapDecorator(Map.of("title", "base")));
    }

    /**
     * Calls the configuration component directly, without an OSGi runtime.
     * The policy is shared by all decorators, including ones already created.
     * @param enforce {@code true} to throw on violations; {@code false} to warn
     *                and continue the lookup.
     */
    private void configure(final boolean enforce) {
        final DeepReadValueMapConfiguration.Configuration configuration =
                mock(DeepReadValueMapConfiguration.Configuration.class);
        when(configuration.enforce()).thenReturn(enforce);
        component.configure(configuration);
    }

    /**
     * Makes one exact resolver lookup return a resource with {@code title = "target"}.
     * For example, to read {@code ../sibling/title} from the standard starting
     * resource, stub {@code /content/site/page/../sibling}. The resolver mock
     * does not normalize dots or slashes; unstubbed lookups return {@code null}.
     * @param path The complete resource lookup string, excluding the property name.
     */
    private void stubTarget(final String path) {
        final Resource target = mock(Resource.class);
        when(resolver.getResource(path)).thenReturn(target);
        when(target.adaptTo(ValueMap.class)).thenReturn(new ValueMapDecorator(Map.of("title", "target")));
    }

    /**
     * Checks all four deep-read methods against a target configured by
     * {@link #stubTarget(String)}. Each getter must return {@code "target"},
     * and {@code containsKey} must report that the property exists.
     * @param map The decorator being tested.
     * @param key The deep-read key, including the final {@code title} property name.
     */
    private void assertTargetReads(final DeepReadValueMapDecorator map, final String key) {
        assertEquals("target", map.get(key));
        assertEquals("target", map.get(key, String.class));
        assertEquals("target", map.get(key, "default"));
        assertTrue(map.containsKey(key));
    }

    /**
     * Returns the captured standard-error output since setup, including any
     * WARN messages emitted by the test logging backend. Reading does not
     * clear the buffer, so later assertions also see earlier messages.
     */
    private String logs() {
        return logOutput.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void testDefaultModeWarnsAndAllowsAllReadMethods() {
        final String requestedPath = "/content/site/page/../sibling";
        stubTarget(requestedPath);

        assertTargetReads(decorator, "../sibling/title");

        // all 4 relevant methods have been invoked
        assertEquals(4, logs().lines().filter(line -> line.contains("WARN")).count());
        assertTrue(logs().contains("resource '/content/site/page'"));
        assertTrue(logs().contains("requested path '" + requestedPath + "'"));
    }

    @Test
    public void testEnforcementRejectsAllReadMethodsBeforeResolution() {
        configure(true);
        final String key = "../sibling/title";
        final String expected = "Deep read outside resource subtree: resource '/content/site/page', "
                + "requested path '/content/site/page/../sibling'";

        assertEquals(
                expected,
                assertThrows(IllegalArgumentException.class, () -> decorator.get(key))
                        .getMessage());
        assertEquals(
                expected,
                assertThrows(IllegalArgumentException.class, () -> decorator.get(key, String.class))
                        .getMessage());
        assertEquals(
                expected,
                assertThrows(IllegalArgumentException.class, () -> decorator.get(key, "default"))
                        .getMessage());
        assertEquals(
                expected,
                assertThrows(IllegalArgumentException.class, () -> decorator.containsKey(key))
                        .getMessage());
        verifyNoInteractions(resolver);
        assertTrue(logs().isEmpty());
    }

    @Test
    public void testContainedPathsAreAllowedInBothModes() {
        final String[] keys = {"child/title", "child/../title", "./title", "child//grandchild/title"};
        for (final boolean enforce : new boolean[] {false, true}) {
            configure(enforce);
            for (final String key : keys) {
                stubTarget("/content/site/page/" + key.substring(0, key.lastIndexOf('/')));
                assertTargetReads(decorator, key);
            }
        }
        assertTrue(logs().isEmpty());
    }

    @Test
    public void testSimilarPrefixDoesNotEstablishContainment() {
        configure(true);

        assertThrows(IllegalArgumentException.class, () -> decorator.get("../page2/title"));
        verifyNoInteractions(resolver);
    }

    @Test
    public void testInvalidPathsAreRejectedInEnforcementMode() {
        configure(true);

        assertThrows(IllegalArgumentException.class, () -> decorator.get("../../../../secret/title"));
        assertThrows(IllegalArgumentException.class, () -> decorator.get(".../title"));
        verifyNoInteractions(resolver);
    }

    @Test
    public void testInvalidPathsWarnAndContinueInLoggingMode() {
        assertNull(decorator.get("../../../../secret/title"));
        assertNull(decorator.get(".../title"));

        verify(resolver).getResource("/content/site/page/../../../../secret");
        verify(resolver).getResource("/content/site/page/...");
        assertEquals(2, logs().lines().filter(line -> line.contains("WARN")).count());
    }

    @Test
    public void testRootAllowsAnyValidAbsoluteTargetButNotAboveRoot() {
        configure(true);
        final DeepReadValueMapDecorator root = createDecorator("/");
        stubTarget("//content");
        stubTarget("//.");

        assertTargetReads(root, "content/title");
        assertTargetReads(root, "./title");
        assertThrows(IllegalArgumentException.class, () -> root.get("../title"));
        assertFalse(logs().contains("WARN"));
    }

    @Test
    public void testConfigurationChangesAffectExistingDecorators() {
        configure(true);
        assertThrows(IllegalArgumentException.class, () -> decorator.get("../sibling/title"));

        configure(false);
        assertNull(decorator.get("../sibling/title"));
        assertTrue(logs().contains("WARN"));

        configure(true);
        assertThrows(IllegalArgumentException.class, () -> decorator.get("../sibling/title"));

        component.deactivate();
        assertNull(decorator.get("../sibling/title"));
        assertEquals(2, logs().lines().filter(line -> line.contains("WARN")).count());
    }

    @Test
    public void testBaseAndMissingPropertiesRetainTheirBehavior() {
        configure(true);

        assertEquals("base", decorator.get("title"));
        assertEquals("base", decorator.get("title", String.class));
        assertEquals("base", decorator.get("title", "default"));
        assertTrue(decorator.containsKey("title"));
        assertNull(decorator.get((Object) null));
        assertFalse(decorator.containsKey(null));
        assertNull(decorator.get("missing"));
        assertEquals("default", decorator.get("missing", "default"));
        verifyNoInteractions(resolver);

        assertNull(decorator.get("child/missing"));
        assertFalse(decorator.containsKey("child/missing"));
        assertEquals("default", decorator.get("child/missing", "default"));
        when(resolver.getResource(anyString())).thenReturn(mock(Resource.class));
        assertNull(decorator.get("child/missing"));
        assertTrue(logs().isEmpty());
    }

    @Test
    public void testDiagnosticsEscapeControlCharactersInBothModes() {
        final DeepReadValueMapDecorator map = createDecorator("/content/site\npage");
        final String key = "../sibling\r\u001b\u2028/title";
        assertNull(map.get(key));
        final String message = "Deep read outside resource subtree: resource '/content/site\\u000apage', "
                + "requested path '/content/site\\u000apage/../sibling\\u000d\\u001b\\u2028'";
        assertTrue(logs().contains(message));
        assertEquals(1, logs().lines().filter(line -> line.contains("WARN")).count());

        configure(true);
        assertEquals(
                message,
                assertThrows(IllegalArgumentException.class, () -> map.get(key)).getMessage());
    }

    @Test
    public void testStartingResourcePathIsNormalizedForContainment() {
        configure(true);
        final DeepReadValueMapDecorator map = createDecorator("/content//site/page/");
        stubTarget("/content//site/page//child");

        assertTargetReads(map, "child/title");
        assertThrows(IllegalArgumentException.class, () -> map.get("../sibling/title"));
        assertTrue(logs().isEmpty());
    }

    @Test
    public void testInvalidStartingResourceIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> createDecorator("/../content"));
        assertThrows(IllegalArgumentException.class, () -> createDecorator("relative"));
    }

    @Test
    public void testAbstractResourceFallbackUsesConfiguredPolicy() {
        final AbstractResource resource = mock(AbstractResource.class, CALLS_REAL_METHODS);
        when(resource.getPath()).thenReturn("/content/site/page");
        when(resource.getResourceResolver()).thenReturn(resolver);
        doReturn(null).when(resource).adaptTo(ValueMap.class);
        doReturn(Map.of("title", "base")).when(resource).adaptTo(Map.class);
        final ValueMap map = resource.getValueMap();

        assertEquals("base", map.get("title"));
        assertNull(map.get("../sibling/title"));
        assertTrue(logs().contains("WARN"));
        configure(true);
        assertThrows(IllegalArgumentException.class, () -> map.get("../sibling/title"));
    }
}
