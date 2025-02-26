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

import junit.framework.TestCase;
import org.apache.sling.api.SlingHttpServletRequest;
import org.mockito.Mockito;

@SuppressWarnings("deprecation")
public class MediaRangeListTest extends TestCase {
    protected MediaRangeList rangeList;

    public void setUp() throws Exception {
        super.setUp();
        rangeList = new MediaRangeList("text/*;q=0.3, text/html;q=0.7, text/html;level=1,\n"
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
        MediaRangeList rangeList = new MediaRangeList("text/html;q=0.8, application/json");
        assertEquals("application/json", rangeList.prefer("text/html", "application/json"));
    }

    public void testHttpEquivParam() {
        SlingHttpServletRequest req = Mockito.mock(SlingHttpServletRequest.class);
        Mockito.when(req.getHeader(MediaRangeList.HEADER_ACCEPT)).thenReturn("text/plain");
        Mockito.when(req.getParameter(MediaRangeList.PARAM_ACCEPT)).thenReturn("text/html");
        MediaRangeList rangeList = new MediaRangeList(req);
        assertTrue("Did not contain media type from query param", rangeList.contains("text/html"));
        assertFalse("Contained media type from overridden Accept header", rangeList.contains("text/plain"));
    }

    public void testInvalidJdkAcceptHeader() {
        // This header is sent by Java client which make use of URLConnection on Oracle JDK
        // See acceptHeader at
        // http://hg.openjdk.java.net/jdk6/jdk6-gate/jdk/file/tip/src/share/classes/sun/net/www/protocol/http/HttpURLConnection.java
        // To support such case the MediaRange parser has to be made bit linient
        final String invalidHeader = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        SlingHttpServletRequest req = Mockito.mock(SlingHttpServletRequest.class);
        Mockito.when(req.getHeader(MediaRangeList.HEADER_ACCEPT)).thenReturn(invalidHeader);
        MediaRangeList rangeList = new MediaRangeList(req);
        assertTrue("Did not contain media type from query param", rangeList.contains("text/html"));
    }
}
