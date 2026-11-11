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
package org.apache.sling.api.request.header;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.TestCase;
import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.mockito.Mockito;

public class JakartaMediaRangeListTest extends TestCase {
    protected JakartaMediaRangeList rangeList;

    public void setUp() throws Exception {
        super.setUp();
        rangeList = new JakartaMediaRangeList("text/*;q=0.3, text/html;q=0.7, text/html;level=1,\n"
                + "               text/html;level=2;q=0.4, */*;q=0.5");
    }

    public void testContains() throws Exception {
        assertTrue(rangeList.contains("text/html"));
        assertTrue(rangeList.contains("application/json")); // Since rangeList contains */*
        assertTrue(rangeList.contains("text/plain"));
    }

    public void testPrefer() throws Exception {
        assertEquals("text/html;level=1", rangeList.prefer("text/html;level=1", "*/*"));
    }

    public void testPreferJson() {
        JakartaMediaRangeList rangeList = new JakartaMediaRangeList("text/html;q=0.8, application/json");
        assertEquals("application/json", rangeList.prefer("text/html", "application/json"));
    }

    /**
     * Matches wildcard ranges and treats missing or empty Accept headers as accepting all types.
     */
    public void testPreferWildcardsAndMissingHeader() {
        for (String accept : new String[] {null, "", "*/*", "text/*"}) {
            assertEquals(
                    accept, "text/html", new JakartaMediaRangeList(accept).prefer("text/html", "application/json"));
        }
        assertEquals(
                "application/json", new JakartaMediaRangeList("application/*").prefer("text/html", "application/json"));
    }

    /**
     * Rejects unsupported types and q=0 ranges, including exclusions that override broader wildcards.
     */
    public void testPreferExcludesUnacceptableTypes() {
        for (String accept : new String[] {
            "text/plain",
            "*/*;q=0",
            "text/html;q=0",
            "text/html;q=0,application/json;q=0,*/*",
            "text/*;q=0,application/*;q=0,*/*"
        }) {
            assertNull(accept, new JakartaMediaRangeList(accept).prefer("text/html", "application/json"));
        }
        assertEquals(
                "application/json",
                new JakartaMediaRangeList("text/html;q=0,*/*").prefer("text/html", "application/json"));
    }

    /**
     * Uses the most specific matching range's quality rather than the quality of a broader wildcard.
     */
    public void testPreferUsesMostSpecificQuality() {
        assertEquals(
                "application/json",
                new JakartaMediaRangeList("text/html;q=0.1,*/*;q=0.9").prefer("text/html", "application/json"));
        assertEquals(
                "text/html",
                new JakartaMediaRangeList("text/html;q=0.2,text/*;q=0.1,application/json;q=0.15")
                        .prefer("text/html", "application/json"));
    }

    /**
     * Requires media parameters to match and gives parameter-specific ranges precedence.
     */
    public void testPreferMatchesMediaParameters() {
        JakartaMediaRangeList ranges = new JakartaMediaRangeList("text/html;level=1;q=0,text/html;q=0.5,*/*");
        assertNull(ranges.prefer("text/html;level=1"));
        assertEquals("text/html;level=2", ranges.prefer("text/html;level=2"));
        assertEquals("text/html", ranges.prefer("text/html"));
        assertNull(new JakartaMediaRangeList("text/html;level=1").prefer("text/html;level=2"));
    }

    public void testParameterLookupIsCaseInsensitive() {
        JakartaMediaRangeList.MediaRange range = rangeList.new MediaRange("text/html;Charset=UTF-8");
        for (String name : new String[] {"Charset", "charset", "CHARSET"}) {
            assertEquals(name, "UTF-8", range.getParameter(name));
        }
        assertNull(range.getParameter(null));
    }

    /**
     * Ignores accept extensions after q when matching ranges and determining specificity.
     */
    public void testPreferIgnoresAcceptExtensions() {
        assertEquals("text/html", new JakartaMediaRangeList("text/html;q=1;extension=value").prefer("text/html"));
        assertNull(new JakartaMediaRangeList("text/html;q=0.5;extension=value,text/html;level=1;q=0")
                .prefer("text/html;level=1", "application/json"));
    }

    /**
     * Accepts whitespace around quality parameters and rejects invalid or non-finite quality values.
     */
    public void testPreferHandlesQualityWhitespaceAndInvalidValues() {
        for (String accept :
                new String[] {"text/html; q=0", "text/html;q=NaN", "text/html;q=Infinity", "text/html;q=invalid"}) {
            assertNull(accept, new JakartaMediaRangeList(accept).prefer("text/html"));
        }
        assertEquals(
                "application/json",
                new JakartaMediaRangeList("Text/HTML; q=0.2, application/json; q=0.8")
                        .prefer("text/html", "application/json"));
    }

    /**
     * Preserves explicit-type tie-breaking and returns the first offered instance for a shared wildcard match.
     */
    public void testPreferPreservesExplicitTieAndOfferedInstance() {
        assertEquals(
                "application/json",
                new JakartaMediaRangeList("text/html,application/json").prefer("text/html", "application/json"));
        JakartaMediaRangeList ranges = new JakartaMediaRangeList("text/*");
        JakartaMediaRangeList.MediaRange html = ranges.new MediaRange("text/html");
        Set<JakartaMediaRangeList.MediaRange> offered = new LinkedHashSet<>();
        offered.add(html);
        offered.add(ranges.new MediaRange("text/plain"));
        assertSame(html, ranges.prefer(offered));
    }

    public void testHttpEquivParam() {
        SlingJakartaHttpServletRequest req = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Mockito.when(req.getHeader(JakartaMediaRangeList.HEADER_ACCEPT)).thenReturn("text/plain");
        Mockito.when(req.getParameter(JakartaMediaRangeList.PARAM_ACCEPT)).thenReturn("text/html");
        JakartaMediaRangeList rangeList = new JakartaMediaRangeList(req);
        assertTrue("Did not contain media type from query param", rangeList.contains("text/html"));
        assertFalse("Contained media type from overridden Accept header", rangeList.contains("text/plain"));
    }

    public void testInvalidJdkAcceptHeader() {
        // This header is sent by Java client which make use of URLConnection on Oracle JDK
        // See acceptHeader at
        // http://hg.openjdk.java.net/jdk6/jdk6-gate/jdk/file/tip/src/share/classes/sun/net/www/protocol/http/HttpURLConnection.java
        // To support such case the MediaRange parser has to be made bit linient
        final String invalidHeader = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        SlingJakartaHttpServletRequest req = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Mockito.when(req.getHeader(JakartaMediaRangeList.HEADER_ACCEPT)).thenReturn(invalidHeader);
        JakartaMediaRangeList rangeList = new JakartaMediaRangeList(req);
        assertTrue("Did not contain media type from query param", rangeList.contains("text/html"));
    }
}
