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
package org.apache.sling.api.resource.path;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PathBuilderTest {

    @Test
    public void noChangeNeeded_root() {

        assertEquals("/path", new PathBuilder("/").append("path").toString());
    }

    @Test
    public void noChangeNeeded_intermediate() {

        assertEquals(
                "/parent/child", new PathBuilder("/parent").append("/child").toString());
    }

    @Test
    public void removeSlash_root() {

        assertEquals("/path", new PathBuilder("/").append("/path").toString());
    }

    @Test
    public void removeSlash_intermediate() {

        assertEquals(
                "/parent/child", new PathBuilder("/parent/").append("/child").toString());
    }

    @Test
    public void addSlash() {

        assertEquals("/parent/child", new PathBuilder("/parent").append("child").toString());
    }

    @Test
    public void normalizeJoinedPath() {
        assertEquals(
                "/parent/other",
                new PathBuilder("/parent//").append("./child/../other/").build());
        assertEquals("/", new PathBuilder("/parent").append("..").build());
    }

    @Test
    public void normalizeInitialPath() {
        assertEquals("/parent", new PathBuilder("/parent//child/../").build());
        assertEquals("/", new PathBuilder("///").build());
    }

    @Test
    public void allowTraversalOutsideInitialSubtree() {
        assertEquals(
                "/etc/keys",
                new PathBuilder("/var/inbox").append("../../etc/keys").build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectTraversalAboveRoot() {
        final PathBuilder builder = new PathBuilder("/parent").append("../../secret");
        assertEquals("/parent/../../secret", builder.toString()); // passes
        builder.build(); // throws
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectUnnormalizableDotSegment() {
        final PathBuilder builder = new PathBuilder("/parent").append(".../child");
        assertEquals("/parent/.../child", builder.toString()); // passes
        builder.build(); // throws
    }

    @Test
    public void toStringPreservesUnnormalizedPath() {
        final PathBuilder builder = new PathBuilder("/parent//").append("./child/../other/");
        assertEquals("/parent//./child/../other/", builder.toString());
        assertEquals("/parent/other", builder.build());
        assertEquals("/parent//./child/../other/", builder.toString());
    }

    @Test
    public void toStringAllowsUnnormalizablePaths() {
        assertEquals(
                "/parent/../../secret",
                new PathBuilder("/parent").append("../../secret").toString());
        assertEquals(
                "/parent/.../child",
                new PathBuilder("/parent").append(".../child").toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void relativeInitialPaths() {
        new PathBuilder("relative");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInitialPath() {
        new PathBuilder(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyInitialPath() {
        new PathBuilder("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyAppendedPath() {
        new PathBuilder("/parent").append("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullAppendedPath() {
        new PathBuilder("/parent").append(null);
    }
}
