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
package org.apache.sling.api.adapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SlingAdaptableTest {


    SlingAdaptable sut;
    AdapterManager adapterMgr;

    @Before
    public void setup() {
        sut = new SlingAdaptable() {};
        adapterMgr = mock(AdapterManager.class);
    }

    @Test
    public void testAdaptTo() {
        assertNull(sut.adaptTo(AdapterType.class));
        SlingAdaptable.setAdapterManager(adapterMgr);
        assertNull(sut.adaptTo(AdapterType.class));

    }

    @Test
    public void testAdaptToWithCache() {
        SlingAdaptable.setAdapterManager(adapterMgr);
        when (adapterMgr.getAdapter(any(), eq(AdapterType.class))).thenReturn(new AdapterType());
        assertNotNull(sut.adaptTo(AdapterType.class));
        verify(adapterMgr,times(1)).getAdapter(any(), eq(AdapterType.class));
        
        // the 2nd time it has to come out of the cache
        assertNotNull(sut.adaptTo(AdapterType.class));
        verify(adapterMgr,times(1)).getAdapter(any(), eq(AdapterType.class));

        assertNull(sut.adaptTo(AdapterType2.class));
        when (adapterMgr.getAdapter(any(), eq(AdapterType2.class))).thenReturn(new AdapterType2());
        assertNotNull(sut.adaptTo(AdapterType2.class));
        assertNotNull(sut.adaptTo(AdapterType.class));
    }


    // pseudo adaptable
    public class AdapterType {}

    // pseudo adaptable
    public class AdapterType2 {}
}
