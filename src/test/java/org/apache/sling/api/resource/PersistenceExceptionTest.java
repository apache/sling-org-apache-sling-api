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
package org.apache.sling.api.resource;

import javax.jcr.RepositoryException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersistenceExceptionTest {

    @Test
    public void testGetMessage() {
        Throwable cause = new RepositoryException("JCR error");
        PersistenceException e = new PersistenceException("Test message");
        assertEquals("Test message", e.getMessage());
        e = new PersistenceException("Test message", cause);
        assertEquals("Test message caused by JCR error", e.getMessage());
        e = new PersistenceException("Test message!", cause);
        assertEquals("Test message caused by JCR error", e.getMessage());
        e = new PersistenceException("Test message.", cause);
        assertEquals("Test message caused by JCR error", e.getMessage());
        e = new PersistenceException(cause.getMessage(), cause);
        assertEquals("JCR error", e.getMessage());
    }
}
