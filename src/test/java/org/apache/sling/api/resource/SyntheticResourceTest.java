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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class SyntheticResourceTest {

    @Test
    public void testSyntheticResourceCreation() {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        String path = "/content/synthetic";
        String resourceType = "/apps/my/resourcetype";

        SyntheticResource syntheticResource = new SyntheticResource(resourceResolver, path, resourceType);

        assertEquals(path, syntheticResource.getPath());
        assertEquals(resourceType, syntheticResource.getResourceType());
        assertNotNull(syntheticResource.getResourceMetadata());
        assertEquals(path, syntheticResource.getResourceMetadata().getResolutionPath());
        ValueMap vm = syntheticResource.getValueMap();
        assertNotNull(vm);
        assertEquals(1, vm.size());
        assertEquals(resourceType, vm.get(ResourceResolver.PROPERTY_RESOURCE_TYPE, String.class));
    }
}
