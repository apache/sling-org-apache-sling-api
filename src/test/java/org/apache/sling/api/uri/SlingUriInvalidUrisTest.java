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
    public static Collection<String[]> data() {
        return Arrays.asList(
                // test fix URIs with spaces
                new String[] { "/path/with/spaces/A name with spaces.pdf", "/test.pdf" },
                new String[] { "http://example.com/path/with/spaces/A name with spaces.pdf", "http://example.com/test.pdf" },
                new String[] { "http://user:pw@example.com/path/with/spaces/A name with spaces.sel1.pdf/suffix?par1=val1&par2=val2#frag",
                        "http://user:pw@example.com/test.sel1.pdf/suffix?par1=val1&par2=val2#frag" },
                new String[] { "http://user:pw with spaces@example.com", "http://user:pw with spaces@example.com/test" },

                // duplicate fragment
                new String[] { "#fragment1#fragment2", "/test" },

                // short invalid URIs
                new String[] { "\\path\\on\\windows", "/test" },
                new String[] { "https://", "https://" },
                new String[] { "special:", "special:/test" },
                new String[] { "@:", "/test" },
                new String[] { ":foo", "/test" },
                new String[] { "://", "/test" },
                new String[] { "::::", "/test" });
    }

    private final String invalidUri;
    private final String invalidUriAdjustedAfterSetPath;

    public SlingUriInvalidUrisTest(String invalidUri, String invalidUriAdjustedAfterSetPath) {
        this.invalidUri = invalidUri;
        this.invalidUriAdjustedAfterSetPath = invalidUriAdjustedAfterSetPath;
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
    public void testAdjustInvalidUriToValidUri() {

        SlingUri slingUri = SlingUriBuilder.parse(invalidUri, null).build();
        SlingUri slingUriAdjusted = slingUri.adjust(b -> b.setSchemeSpecificPart(null).setResourcePath("/test"));
        assertEquals("Using setSchemeSpecificPart(null) should reset the invalid URI to be adjustable", "/test",
                slingUriAdjusted.getResourcePath());
    }

    @Test
    public void testAdjustInvalidUri() {

        SlingUri slingUri = SlingUriBuilder.parse(invalidUri, null).build();
        SlingUri slingUriAdjusted = slingUri.adjust(b -> b.setResourcePath("/test"));
        assertEquals("setResourcePath('/test') to invalid URI '" + invalidUri + "' should result in: "
                + invalidUriAdjustedAfterSetPath, invalidUriAdjustedAfterSetPath, slingUriAdjusted.toString());
    }


}
