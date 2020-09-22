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
package org.apache.sling.api.uri;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SlingUriInvalidUrisTest {

    @Parameters(name = "Invalid URI: {0}")
    public static Collection<String> data() {
        return Arrays.asList(":foo", "https://", "https:", "@:", "://", "::::");
    }

    private final String invalidUri;

    public SlingUriInvalidUrisTest(String invalidUri) {
        this.invalidUri = invalidUri;
    }

    @Test
    public void testInvalidUriToStringIsUnchanged() {
        try {
            new URI(invalidUri);
            fail("URI " + invalidUri + " is not invalid");
        } catch (URISyntaxException e) {
            assertEquals("Invalid URI " + invalidUri + "(e=" + e + ") is unchanged for SlingUriBuilder parse/toString",
                    invalidUri,
                    SlingUriBuilder.parse(invalidUri, null).build().toString());
        }
    }

    @Test
    public void testAdjustInvalidUriNoEffect() {

        SlingUri slingUri = SlingUriBuilder.parse(invalidUri, null).build();
        SlingUri slingUriAdjusted = slingUri.adjust(b -> b.setResourcePath("/test"));
        assertNull("setResourcePath() should have been ignored for uri " + invalidUri, slingUriAdjusted.getResourcePath());
    }

    @Test
    public void testAdjustInvalidUriToValidUri() {

        SlingUri slingUri = SlingUriBuilder.parse(invalidUri, null).build();
        SlingUri slingUriAdjusted = slingUri.adjust(b -> b.setSchemeSpecificPart(null).setResourcePath("/test"));
        assertEquals("Using setSchemeSpecificPart(null) should reset the invalid URI to be adjustable", "/test",
                slingUriAdjusted.getResourcePath());
    }

}
