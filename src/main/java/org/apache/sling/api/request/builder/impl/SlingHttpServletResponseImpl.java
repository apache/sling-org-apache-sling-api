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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.apache.sling.api.request.builder.SlingHttpServletResponseBuilder;
import org.apache.sling.api.request.builder.SlingHttpServletResponseResult;
import org.jetbrains.annotations.NotNull;

/**
 * Internal {@link SlingHttpServletResponse} implementation.
 */
public class SlingHttpServletResponseImpl 
    extends SlingAdaptable 
    implements SlingHttpServletResponseResult, SlingHttpServletResponseBuilder {

    /** Headers */
    private final HeaderSupport headerSupport = new HeaderSupport();
    
    /** Cookies */
    private final Map<String, Cookie> cookies = new LinkedHashMap<>();

    private String contentType;

    private String characterEncoding;

    private Locale locale = Locale.US;

    private long contentLength;

    private int status;

    private boolean isCommitted;

    private String statusMessage;

    private int bufferSize = 8192;

    private ByteArrayOutputStream outputStream;
    
    private ServletOutputStream servletOutputStream;
    
    private PrintWriter printWriter;
    
    /** Is the builder locked? */
    private boolean locked = false;

    private void checkLocked() {
        if ( locked ) {
            throw new IllegalStateException("The builder can't be reused. Create a new builder instead.");
        }
    }

    @Override
    public @NotNull SlingHttpServletResponseResult build() {
        this.checkLocked();
        this.locked = true;
        this.reset();
        return this;
    }

    private void checkCommitted() {
        if (isCommitted()) {
            throw new IllegalStateException("Response already committed.");
        }
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public void setCharacterEncoding(final String encoding) {
        this.characterEncoding = encoding;
    }

    @Override
    public String getContentType() {
        if (this.contentType == null) {
            return null;
        } else if ( this.characterEncoding == null ) {
            return this.contentType;
        }
        return this.contentType.concat(SlingHttpServletRequestImpl.CHARSET_SEPARATOR).concat(this.characterEncoding);
    }

    @Override
    public void setContentType(final String type) {
        if (this.printWriter == null) {
            final int pos = type == null ? -1 : type.indexOf(SlingHttpServletRequestImpl.CHARSET_SEPARATOR);
            if (pos != -1) {
                this.contentType = type.substring(0, pos);
                this.characterEncoding = type.substring(pos + SlingHttpServletRequestImpl.CHARSET_SEPARATOR.length());
            } else {
                this.contentType = type;
            }
        }
    }

    @Override
    public void setContentLength(final int len) {
        this.contentLength = len;
    }

    @Override
    public void setContentLengthLong(final long len) {
        this.contentLength = len;
    }

    @Override
    public void setStatus(final int sc, final String message) {
        setStatus(sc);
        this.statusMessage = message;
    }

    @Override
    public void setStatus(final int sc) {
        this.status = sc;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public void sendError(final int sc, final String msg) {
        this.setStatus(sc);
        this.statusMessage = msg;
        this.isCommitted = true;
    }

    @Override
    public void sendError(final int sc) {
        this.setStatus(sc);
        this.statusMessage = null;
        this.isCommitted = true;
    }

    @Override
    public void sendRedirect(final String location) {
        this.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        this.statusMessage = null;
        this.setHeader("Location", location);
        this.isCommitted = true;
    }

    @Override
    public void addHeader(final String name, final String value) {
        this.headerSupport.addHeader(name, value);
    }

    @Override
    public void addIntHeader(final String name, final int value) {
        this.headerSupport.addIntHeader(name, value);
    }

    @Override
    public void addDateHeader(final String name, final long date) {
        this.headerSupport.addDateHeader(name, date);
    }

    @Override
    public void setHeader(final String name, final String value) {
        this.headerSupport.setHeader(name, value);
    }

    @Override
    public void setIntHeader(final String name, final int value) {
        this.headerSupport.setIntHeader(name, value);
    }

    @Override
    public void setDateHeader(final String name, final long date) {
        this.headerSupport.setDateHeader(name, date);
    }

    @Override
    public boolean containsHeader(final String name) {
        return this.headerSupport.containsHeader(name);
    }

    @Override
    public String getHeader(final String name) {
        return this.headerSupport.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(final String name) {
        return this.headerSupport.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return this.headerSupport.getHeaderNames();
    }

    private Charset getCharset() {
        if ( this.characterEncoding == null ) {
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(this.characterEncoding);
    }

    @Override
    public PrintWriter getWriter() {
        if (this.printWriter == null) {
            this.printWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharset()));
        }
        return this.printWriter;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (this.servletOutputStream == null) {
            this.servletOutputStream = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                }

                @Override
                public boolean isReady() {
                    return true;
                }
                
                @Override
                public void setWriteListener(final WriteListener writeListener) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return this.servletOutputStream;
    }

    @Override
    public void reset() {
        this.checkCommitted();
        this.cookies.clear();
        this.headerSupport.reset();
        this.status = HttpServletResponse.SC_OK;
        this.contentLength = -1L;
        this.statusMessage = null;
        this.resetBuffer();
    }

    @Override
    public void resetBuffer() {
        this.checkCommitted();
        this.outputStream = new ByteArrayOutputStream();
        this.servletOutputStream = null;
        this.printWriter = null;
    }

    @Override
    public int getBufferSize() {
        return this.bufferSize;
    }

    @Override
    public void setBufferSize(final int size) {
        this.bufferSize = size;
    }

    @Override
    public void flushBuffer() {
        this.isCommitted = true;
    }

    @Override
    public boolean isCommitted() {
        return isCommitted;
    }

    @Override
    public void addCookie(final Cookie cookie) {
        this.cookies.put(cookie.getName(), cookie);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(final Locale loc) {
        this.locale = loc;
    }

    @Override
    public long getContentLength() {
        return this.contentLength;
    }    

    @Override
    public String getStatusMessage() {
        return this.statusMessage;
    }

    @Override
    public Cookie getCookie(final String name) {
        return this.cookies.get(name);
    }

    @Override
    public Cookie[] getCookies() {
        if ( this.cookies.isEmpty() ) {
            return null;
        }
        return cookies.values().toArray(new Cookie[cookies.size()]);
    }

    @Override
    public byte[] getOutput() {
        this.isCommitted = true;
        if (printWriter != null) {
            printWriter.flush();
        }
        if (servletOutputStream != null) {
            try {
                servletOutputStream.flush();
            } catch (IOException ex) {
                // ignore
            }
        }
        return outputStream.toByteArray();
    }

    @Override
    public @NotNull String getOutputAsString() {
        this.isCommitted = true;
        return new String(getOutput(), this.getCharset());
    }

    // --- unsupported operations ---
    @Override
    public String encodeRedirectUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeUrl(String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeURL(String url) {
        throw new UnsupportedOperationException();
    }
}
