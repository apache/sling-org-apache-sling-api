/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.api.request.header;

import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.mockito.Mockito;

import junit.framework.TestCase;

public class JakartaMediaRangeListTest extends TestCase {
    protected JakartaMediaRangeList rangeList;

    public void setUp() throws Exception {
        super.setUp();
        rangeList = new JakartaMediaRangeList("text/*;q=0.3, text/html;q=0.7, text/html;level=1,\n" +
                "               text/html;level=2;q=0.4, */*;q=0.5");
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

    public void testHttpEquivParam() {
        SlingJakartaHttpServletRequest req = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Mockito.when(req.getHeader(JakartaMediaRangeList.HEADER_ACCEPT)).thenReturn("text/plain");
        Mockito.when(req.getParameter(JakartaMediaRangeList.PARAM_ACCEPT)).thenReturn("text/html");
        JakartaMediaRangeList rangeList = new JakartaMediaRangeList(req);
        assertTrue("Did not contain media type from query param", rangeList.contains("text/html"));
        assertFalse("Contained media type from overridden Accept header", rangeList.contains("text/plain"));
    }

    public void testInvalidJdkAcceptHeader() {
        //This header is sent by Java client which make use of URLConnection on Oracle JDK
        //See acceptHeader at http://hg.openjdk.java.net/jdk6/jdk6-gate/jdk/file/tip/src/share/classes/sun/net/www/protocol/http/HttpURLConnection.java
        //To support such case the MediaRange parser has to be made bit linient
        final String invalidHeader = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
        SlingJakartaHttpServletRequest req = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Mockito.when(req.getHeader(JakartaMediaRangeList.HEADER_ACCEPT)).thenReturn(invalidHeader);
        JakartaMediaRangeList rangeList = new JakartaMediaRangeList(req);
        assertTrue("Did not contain media type from query param", rangeList.contains("text/html"));
    }
}
