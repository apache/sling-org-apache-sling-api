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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class RequestParameterImplTest {
    
    @Test public void testParameter() throws UnsupportedEncodingException {
        final RequestParameterImpl param = new RequestParameterImpl("foo", "bar");
        assertEquals("foo", param.getName());
        assertEquals("bar", param.getString());
        assertArrayEquals("bar".getBytes(StandardCharsets.UTF_8), param.get());
        assertEquals("text/plain", param.getContentType());
        assertNotNull(param.getInputStream());
        assertNull(param.getFileName());
        assertEquals(3L, param.getSize());
        assertEquals("bar", param.getString("UTF-8"));
        assertTrue(param.isFormField());
        assertEquals(param.getString(), param.toString());
    }
}
