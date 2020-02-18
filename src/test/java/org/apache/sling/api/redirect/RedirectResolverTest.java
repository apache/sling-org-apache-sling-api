/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.sling.api.redirect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RedirectResolverTest {

    @Mock
    private HttpServletRequest request;



    /**
     * Typical usage pattern extend the redirect response in order to use it.
     */
    public class RedirectResponseForTests implements RedirectResponse {
        /**
         * Status code for the redirect
         */
        private int status;
        /**
         * The redirect url
         */
        private String redirect = null;
        /**
         * List of headers
         */
        private List<String[]> headers = new ArrayList<String[]>();



        /**
         * Headers to be added to the redirect
         * @return iterable of headers in the form of String[2] array.
         */
        public Iterable<String[]> getHeaders() {
            return headers;
        }

        /**
         *
         * @return the redirect url
         */
        public String getRedirect() {
            return redirect;
        }

        /**
         *
         * @return the status code
         */
        public int getStatus() {
            return this.status;
        }

        /**
         *
         * @return true if after being passed to RedirectResolver.resolve, the resource was resolved to a redirect.
         */
        @Override
        public boolean hasResolved() {
            return redirect != null;
        }


        @Override
        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public void setHeader(String name, String value) {
            headers.add(new String[]{ name, value});
        }

        @Override
        public void setRedirect(String redirect) {
            this.redirect = redirect;
        }
    }

    /**
     * Only testing the interface here, not testing adaption or the provider.
     */
    public class RedirectResolverForTesting implements  RedirectResolver {

        private final String redirect;
        private final List<String[]> headers;
        private final int status;

        RedirectResolverForTesting(String redirect, List<String[]> headers, int status) {
            this.redirect = redirect;
            this.headers = headers;
            this.status = status;
        }

        @Override
        public void resolve(@NotNull HttpServletRequest request, @NotNull RedirectResponse redirectResponse) {
            for (String[] header: headers) {
                redirectResponse.setHeader(header[0], header[1]);
            }
            redirectResponse.setStatus(status);
            redirectResponse.setRedirect(redirect);
        }
    }


    public RedirectResolverTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRedirect() {
        // this would have been adpated, typically from a resource, not create new.
        List<String[]> headers = new ArrayList<>();
        headers.add(new String[] { "x-test", "header"});
        RedirectResolver redirectResolver = new RedirectResolverForTesting("https://xxx.blobs.com/container/id", headers, 301);

        // test behaviour. This is rather pointless as the behaviour is defined in the test, but
        // it does ensure that the base class and API are as expected.
        RedirectResponseForTests redirectResponse = new RedirectResponseForTests();
        redirectResolver.resolve(request, redirectResponse);

        Assert.assertTrue(redirectResponse.hasResolved());
        Assert.assertEquals("https://xxx.blobs.com/container/id", redirectResponse.getRedirect());
        Assert.assertEquals(301, redirectResponse.getStatus());
    }

    @Test
    public void testNoRedirect() {
        // this would have been adpated, typically from a resource, not create new.
        List<String[]> headers = new ArrayList<>();
        headers.add(new String[] { "x-test", "header"});
        RedirectResolver redirectResolver = new RedirectResolverForTesting(null, headers, -1);

        // test behaviour. This is rather pointless as the behaviour is defined in the test, but
        // it does ensure that the base class and API are as expected.
        RedirectResponseForTests redirectResponse = new RedirectResponseForTests();
        redirectResolver.resolve(request, redirectResponse);

        Assert.assertFalse(redirectResponse.hasResolved());
    }


}
