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
package org.apache.sling.api.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.Reader;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

public class SlingBindingsTest {
    
    SlingBindings bindings = new SlingBindings();
    
    @Test
    public void test_Flush() {
        assertFalse(bindings.getFlush());
        bindings.setFlush(true);
        assertTrue(bindings.getFlush());
    }
    
    @Test
    public void test_Log() {
        Logger logger = Mockito.mock(Logger.class);
        assertNull(bindings.getLog());
        bindings.setLog(null);
        assertNull(bindings.getLog());
        bindings.setLog(logger);
        assertEquals(logger,bindings.getLog());
    }
    
    @Test
    public void test_out() {
        PrintWriter printWriter = Mockito.mock(PrintWriter.class);
        assertNull(bindings.getOut());
        bindings.setOut(null);
        assertNull(bindings.getOut());
        bindings.setOut(printWriter);
        assertEquals(printWriter,bindings.getOut());
    }
    
    @Test
    public void test_request() {
        SlingHttpServletRequest request = Mockito.mock(SlingHttpServletRequest.class);
        assertNull(bindings.getRequest());
        bindings.setRequest(null);
        assertNull(bindings.getRequest());
        bindings.setRequest(request);
        assertEquals(request,bindings.getRequest());
    }
    
    @Test
    public void test_reader() {
        Reader reader = Mockito.mock(Reader.class);
        assertNull(bindings.getReader());
        bindings.setReader(null);
        assertNull(bindings.getReader());
        bindings.setReader(reader);
        assertEquals(reader,bindings.getReader());
    }
    
    @Test
    public void test_resource() {
        Resource resource = Mockito.mock(Resource.class);
        assertNull(bindings.getResource());
        bindings.setResource(null);
        assertNull(bindings.getResource());
        bindings.setResource(resource);
        assertEquals(resource,bindings.getResource());
    }
    
    @Test
    public void test_resourceresolver() {
        ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        assertNull(bindings.getResourceResolver());
        bindings.setResourceResolver(null);
        assertNull(bindings.getResourceResolver());
        bindings.setResourceResolver(resolver);
        assertEquals(resolver,bindings.getResourceResolver());
    }
    
    @Test
    public void test_response() {
        SlingHttpServletResponse response = Mockito.mock(SlingHttpServletResponse.class);
        assertNull(bindings.getResponse());
        bindings.setResponse(null);
        assertNull(bindings.getResponse());
        bindings.setResponse(response);
        assertEquals(response, bindings.getResponse());
    }
    
    @Test
    public void test_sling() {
        SlingScriptHelper sling = Mockito.mock(SlingScriptHelper.class);
        assertNull(bindings.getSling());
        bindings.setSling(null);
        assertNull(bindings.getSling());
        bindings.setSling(sling);
        assertEquals(sling,bindings.getSling());
    }
    
    @Test
    public void test_nonMatchingClass() {
        SlingHttpServletResponse response = Mockito.mock(SlingHttpServletResponse.class);
        bindings.setResponse(response);
        assertEquals(response,bindings.getResponse());
        
        // manually put a wrong type 
        SlingHttpServletRequest request = Mockito.mock(SlingHttpServletRequest.class);
        bindings.put(SlingBindings.RESPONSE, request);
        // incorrect type, so a null is expected
        assertNull(bindings.getResponse());
    }
}
