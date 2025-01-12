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
package org.apache.sling.api.request;

import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingJakartaHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.mockito.Mockito;

import junit.framework.TestCase;

public class RequestUtilTest extends TestCase {

    @SuppressWarnings("deprecation")
    public void testHandleIfModifiedSince(){
        assertTrue(RequestUtil.handleIfModifiedSince(getMockRequest(1309268989938L,1309269042730L),getMockResponse()));

        assertFalse(RequestUtil.handleIfModifiedSince(getMockRequest(1309269042730L,1309268989938L),getMockResponse()));
        assertFalse(RequestUtil.handleIfModifiedSince(getMockRequest(-1,1309268989938L),getMockResponse()));
    }

    public void testHandleIfModifiedSinceJakarta(){
        assertTrue(RequestUtil.handleIfModifiedSince(getMockRequestJakarta(1309268989938L,1309269042730L), getMockResponseJakarta()));

        assertFalse(RequestUtil.handleIfModifiedSince(getMockRequestJakarta(1309269042730L,1309268989938L),getMockResponseJakarta()));
        assertFalse(RequestUtil.handleIfModifiedSince(getMockRequestJakarta(-1,1309268989938L),getMockResponseJakarta()));
    }

    protected SlingJakartaHttpServletRequest getMockRequestJakarta(final long modificationTime, final long ifModifiedSince) {
        SlingJakartaHttpServletRequest r = Mockito.mock(SlingJakartaHttpServletRequest.class);
        Mockito.when(r.getDateHeader(Mockito.anyString())).thenReturn(ifModifiedSince);
        final String path = "/foo/node";
        final Resource mr = Mockito.mock(Resource.class);
        Mockito.when(mr.getPath()).thenReturn(path);
        final ResourceMetadata metadata = new ResourceMetadata();
        metadata.setModificationTime(modificationTime);
        Mockito.when(mr.getResourceMetadata()).thenReturn(metadata);
        Mockito.when(r.getResource()).thenReturn(mr);
        return r;
    }

    @SuppressWarnings("deprecation")
    protected org.apache.sling.api.SlingHttpServletRequest getMockRequest(final long modificationTime, final long ifModifiedSince) {
        org.apache.sling.api.SlingHttpServletRequest r = Mockito.mock(org.apache.sling.api.SlingHttpServletRequest.class);
        Mockito.when(r.getDateHeader(Mockito.anyString())).thenReturn(ifModifiedSince);
        final String path = "/foo/node";
        final Resource mr = Mockito.mock(Resource.class);
        Mockito.when(mr.getPath()).thenReturn(path);
        final ResourceMetadata metadata = new ResourceMetadata();
        metadata.setModificationTime(modificationTime);
        Mockito.when(mr.getResourceMetadata()).thenReturn(metadata);
        Mockito.when(r.getResource()).thenReturn(mr);
        return r;
    }

    public void testParserAcceptHeader(){
        assertEquals(RequestUtil.parserAcceptHeader("compress;q=0.5, gzip;q=1.0").get("compress"), 0.5);
        assertEquals(RequestUtil.parserAcceptHeader("compress,gzip").get("compress"),1.0);
        assertEquals(RequestUtil.parserAcceptHeader("compress").get("compress"),1.0);
        assertEquals(RequestUtil.parserAcceptHeader("compress;q=string,gzip;q=1.0").get("compress"), 1.0);

        assertNull(RequestUtil.parserAcceptHeader("compress;q=0.5, gzip;q=1.0").get("compres"));
    }

    protected HttpServletResponse getMockResponse() {
        return Mockito.mock(HttpServletResponse.class);
    }

    protected jakarta.servlet.http.HttpServletResponse getMockResponseJakarta() {
        final jakarta.servlet.http.HttpServletResponse res = Mockito.mock(jakarta.servlet.http.HttpServletResponse.class);
        return res;
    }
}
