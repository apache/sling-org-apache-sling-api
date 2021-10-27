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
package org.apache.sling.api.request.builder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

public class SlingHttpServletResponseImplTest {
    
    private SlingHttpServletResponseImpl res;

    @Before
    public void setup() {
        this.res = new SlingHttpServletResponseImpl();
    }

    @Test(expected = IllegalStateException.class) public void testCheckLocked() {
        try {
            res.build();
        } catch ( final IllegalStateException error) {
            fail();
        }
        res.build();
    }


    @Test public void testGetCharacterEncoding() throws UnsupportedEncodingException {
        res.build();
        assertNull(res.getCharacterEncoding());
        res.setCharacterEncoding("UTF-8");
        assertEquals("UTF-8", res.getCharacterEncoding());
    }

    @Test public void testDefaultContentType() throws UnsupportedEncodingException {
        res.build();
        assertNull(res.getContentType());
    }

    @Test public void testContentType() throws UnsupportedEncodingException {
        res.build();
        res.setContentType("text/text");
        assertEquals("text/text", res.getContentType());
        assertNull(res.getCharacterEncoding());
        res.setCharacterEncoding("UTF-8");
        assertEquals("UTF-8", res.getCharacterEncoding());
        assertEquals("text/text;charset=UTF-8", res.getContentType());

        res.setContentType(null);
        assertNull("null", res.getContentType());
        res.getWriter();
        // this should not be possible anymore, since a writer was created
        res.setContentType("text/text");
        assertNull("null", res.getContentType());
    }

    @Test public void testContentTypeAndCharset() throws UnsupportedEncodingException {
        res.build();
        res.setContentType("text/text;charset=UTF-16");
        assertEquals("text/text;charset=UTF-16", res.getContentType());
        assertEquals("UTF-16", res.getCharacterEncoding());
        res.setCharacterEncoding("UTF-8");
        assertEquals("UTF-8", res.getCharacterEncoding());
        assertEquals("text/text;charset=UTF-8", res.getContentType());
    }

    @Test public void testContentLength() {
        res.build();
        assertEquals(-1L, res.getContentLength());
        res.setContentLength(500);
        assertEquals(500L, res.getContentLength());
        res.setContentLengthLong(5000L);
        assertEquals(5000L, res.getContentLength());
    }

    @Test public void testSetStatus() {
        res.build();
        assertEquals(200, res.getStatus());
        assertNull(res.getStatusMessage());

        res.setStatus(201);
        assertEquals(201, res.getStatus());
        assertNull(res.getStatusMessage());

        res.setStatus(202, "msg");
        assertEquals(202, res.getStatus());
        assertEquals("msg", res.getStatusMessage());

        assertFalse(res.isCommitted());
    }

    @Test public void testSendError() {
        res.build();
        res.sendError(500);
        assertEquals(500, res.getStatus());
        assertNull(res.getStatusMessage());
        assertTrue(res.isCommitted());
    }

    @Test public void testSendErrorWithMessage() {
        res.build();
        res.sendError(500, "msg");
        assertEquals(500, res.getStatus());
        assertEquals("msg", res.getStatusMessage());
        assertTrue(res.isCommitted());
    }

    @Test public void testSendRedirect() {
        res.build();
        res.sendRedirect("/redirect");
        assertEquals(302, res.getStatus());
        assertNull(res.getStatusMessage());
        assertTrue(res.isCommitted());
        assertEquals("/redirect", res.getHeader("Location"));
    }

    @Test public void testHeaders() {
        res.build();
        assertTrue(res.getHeaderNames().isEmpty());
        res.addDateHeader("date", 50000);
        res.addIntHeader("number", 5);
        res.addHeader("name", "value");
        res.setDateHeader("adate", 100000);
        res.setIntHeader("anumber", 10);
        res.setHeader("aname", "something");

        assertEquals(6, res.getHeaderNames().size());
        assertEquals("Thu, 1 Jan 1970 00:00:50 GMT", res.getHeader("date"));
        assertEquals("5", res.getHeader("number"));
        assertEquals("value", res.getHeader("name"));
        assertEquals("Thu, 1 Jan 1970 00:01:40 GMT", res.getHeader("adate"));
        assertEquals("10", res.getHeader("anumber"));
        assertEquals("something", res.getHeader("aname"));

        assertTrue(res.containsHeader("name"));
        assertFalse(res.containsHeader("foo"));

        assertEquals(1, res.getHeaders("name").size());
    }

    @Test public void testGetWriter() {
        res.build();
        final PrintWriter writer = res.getWriter();
        writer.write("body");
        assertEquals("body", res.getOutputAsString());
        assertEquals("body", new String(res.getOutput(), StandardCharsets.UTF_8));
    }

    @Test public void testGetOutputStream() throws IOException {
        res.build();
        final OutputStream out = res.getOutputStream();
        out.write("body".getBytes(StandardCharsets.UTF_8));
        assertEquals("body", res.getOutputAsString());
        assertEquals("body", new String(res.getOutput(), StandardCharsets.UTF_8));
    }

    @Test public void testReset() {
        res.build();
        res.setStatus(201);
        res.setContentLength(500);
        res.reset();
        assertEquals(200, res.getStatus());
        assertEquals(-1L, res.getContentLength());
    }

    @Test public void testResetBuffer() {
        res.build();
        res.setStatus(201);
        res.setContentLength(500);
        res.resetBuffer();
        assertEquals(201, res.getStatus());
        assertEquals(500L, res.getContentLength());
    }

    @Test public void testBufferSize() {
        res.build();
        assertEquals(8192, res.getBufferSize());
        res.setBufferSize(16384);
        assertEquals(16384, res.getBufferSize());
    }

    @Test public void testFlushBuffer() {
        res.build();
        assertFalse(res.isCommitted());
        res.flushBuffer();
        assertTrue(res.isCommitted());
    }

    @Test public void testCookies() {
        res.build();
        assertNull(res.getCookies());
        res.addCookie(new Cookie("name", "value"));
        assertEquals(1, res.getCookies().length);
        assertEquals("name", res.getCookies()[0].getName());
        assertNotNull(res.getCookie("name"));
    }

    @Test public void testLocale() {
        res.build();
        assertEquals(Locale.US, res.getLocale());
        res.setLocale(Locale.CANADA);
        assertEquals(Locale.CANADA, res.getLocale());
    }

    @Test public void testUnsupportedMethods() {
        res.build();
        try {
            res.encodeRedirectURL("/url");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            res.encodeRedirectUrl("/url");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            res.encodeURL("/url");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
        try {
            res.encodeUrl("/url");
            fail();
        } catch ( final UnsupportedOperationException expected) {}
    }
}
