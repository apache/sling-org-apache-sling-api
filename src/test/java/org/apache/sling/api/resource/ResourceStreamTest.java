/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.api.resource;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ResourceStreamTest {

    @Rule
    public final SlingContext context = new SlingContext();

    private static final String PATH = "/content/sample/en";

    @Before
    public void setUp() throws ParseException {
        context.load().json("/data.json", PATH);
    }

    @Test
    public void testObtainResourceFromContext() {
        Resource resource = context.resourceResolver().getResource(PATH);
        assertEquals("en", resource.getName());
    }

    @Test
    public void testResourceStream() {
        Resource resource = context.resourceResolver().getResource(PATH);
        Object[] found = resource.stream().toArray();
        assertEquals(10, found.length);
    }

    @Test
    public void testResourceStreamBranchSelector() {
        Resource resource = context.resourceResolver().getResource(PATH);
        Object[] found = resource.stream(r -> r.getValueMap().get("jcr:primaryType", "").equals("app:Page"))
                .toArray();
        assertEquals(3, found.length);
    }

    @Test
    public void testResourceStreamResourceSelector() {
        Resource resource = context.resourceResolver().getResource(PATH);
        Object[] found = resource.stream().filter(r -> r.getValueMap().get("jcr:primaryType").equals("app:PageContent"))
                .toArray();
        assertEquals(4, found.length);
    }

    @Test
    public void testResourceStreamLowLimit() {
        Resource resource = context.resourceResolver().getResource(PATH);
        Object[] found = resource.stream().filter(r -> r.getValueMap().get("jcr:primaryType").equals("app:PageContent"))
                .limit(3).toArray();
        assertEquals(3, found.length);
    }

    @Test
    public void testResourceStreamHighLimit() {
        Resource resource = context.resourceResolver().getResource(PATH);
        Object[] found = resource.stream().filter(r -> r.getValueMap().get("jcr:primaryType").equals("app:PageContent"))
                .limit(7).toArray();
        assertEquals(4, found.length);
    }

    @Test
    public void testResourceStreamRange() {
        Resource resource = context.resourceResolver().getResource(PATH);
        Object[] found = resource.stream().filter(r -> r.getValueMap().get("jcr:primaryType").equals("app:PageContent"))
                .skip(1).limit(3).toArray();
        assertEquals(3, found.length);
    }

}
