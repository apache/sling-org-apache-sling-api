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

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SlingUriEncodingTest {

    @Test
    public void testUriWithEuroSign() throws URISyntaxException, UnsupportedEncodingException {

        testUriUnchangedForEncodedAndDecodedStr("/test-with-euro-sign-%E2%82%AC-suffix.pdf", true, true, false, false, false);
    }

    @Test
    public void testUriWithSpaces() throws URISyntaxException, UnsupportedEncodingException {

        testUriUnchangedForEncodedAndDecodedStr("/test+with+spaces%20in+different%20encodings", true, true, false, false, false);
    }

    @Test
    public void testUriWithSpecialCharacters() throws URISyntaxException, UnsupportedEncodingException {

        testUriUnchangedForEncodedAndDecodedStr(
                "/path/with/many/special/chars/%2B%26%2B-%25%3D%2A%5E%3F%23%5E%21%24%3D%3D%5E_-_%24%2B%24%3D%5E%3F%3F%2B%26%40%3D%25%40-%24",
                true, true, false, false, false);
    }

    @Test
    public void testUriWithSpecialCharactersInUserInfo() throws URISyntaxException, UnsupportedEncodingException {

        testUriUnchangedForEncodedAndDecodedStr(
                "http://user:%2B%26%2B-%25%3D%2A%5E%3F%23@example.com/path.txt",
                false, false, false, true, false);
    }

    @Test
    public void testUriWithSpecialCharactersInQuery() throws URISyntaxException, UnsupportedEncodingException {

        testUriUnchangedForEncodedAndDecodedStr(
                "http://example.com/path.txt?testParam=%2B%26%2B-%25%3D%2A%5E%3F%23",
                false, false, false, true, false);
    }

    @Test
    public void testUriWithSpecialCharactersInFragment() throws URISyntaxException, UnsupportedEncodingException {

        testUriUnchangedForEncodedAndDecodedStr(
                "http://example.com/path.txt?testParam=testVal#%2B%26%2B-%25%3D%2A%5E%3F%23",
                false, false, false, true, false);
    }

    private void testUriUnchangedForEncodedAndDecodedStr(String testUriStrEncoded, boolean isPath, boolean isAbsolutePath,
            boolean isRelativePath, boolean isAbsolute, boolean isOpaque) throws UnsupportedEncodingException {

        testUriUnchanged(testUriStrEncoded, isPath, isAbsolutePath, isRelativePath, isAbsolute, isOpaque);

        // decoded variant should also stay unchanged
        String testUriStrDecoded = URLDecoder.decode(testUriStrEncoded, StandardCharsets.UTF_8.name());
        testUriUnchanged(testUriStrDecoded, isPath, isAbsolutePath, isRelativePath, isAbsolute, isOpaque);
    }

    public static SlingUri testUriUnchanged(String testUri, boolean isPath, boolean isAbsolutePath, boolean isRelativePath,
            boolean isAbsolute, boolean isOpaque) {
        SlingUri slingUri = SlingUriBuilder.parse(testUri, null).build();

        assertEquals("Uri toString() same as input", testUri, slingUri.toString());

        assertEquals("isPath()", isPath, slingUri.isPath());
        assertEquals("isAbsolutePath()", isAbsolutePath, slingUri.isAbsolutePath());
        assertEquals("isRelativePath()", isRelativePath, slingUri.isRelativePath());
        assertEquals("isAbsolute()", isAbsolute, slingUri.isAbsolute());
        assertEquals("isOpaque()", isOpaque, slingUri.isOpaque());

        SlingUri slingUriParsedFromSameInput = SlingUriBuilder.parse(testUri, null).build();
        assertEquals("uris parsed from same input are expected to be equal", slingUriParsedFromSameInput, slingUri);
        assertEquals("uris parsed from same input are expected to have the same hash code", slingUriParsedFromSameInput.hashCode(),
                slingUri.hashCode());

        return slingUri;
    }

}
