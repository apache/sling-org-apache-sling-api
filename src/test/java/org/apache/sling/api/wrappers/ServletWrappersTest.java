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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test related to some of the servlet wrappers in the Sling API.
 */
public class ServletWrappersTest {

    @Test
    public void testJakartaRequestWrappingWithNull() {
        assertEquals(null, JakartaToJavaxRequestWrapper.toJavaxRequest(null));
    }

    @Test
    public void testJakartaResponseWrappingWithNull() {
        assertEquals(null, JakartaToJavaxResponseWrapper.toJavaxResponse(null));
    }

    @Test
    public void testJavaxRequestWrappingWithNull() {
        assertEquals(null, JavaxToJakartaRequestWrapper.toJakartaRequest(null));
    }

    @Test
    public void testJavaxResponseWrappingWithNull() {
        assertEquals(null, JavaxToJakartaResponseWrapper.toJakartaResponse(null));
    }
}
