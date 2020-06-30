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
package org.apache.sling.api.resource.observation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ResourceChangeListenerTest {

    private final static String CHANGE_CONSTANT_PREFIX = "CHANGE_";

    @Test
    public void testChangeConstants() throws IllegalArgumentException, IllegalAccessException {
        Collection<Field> changeConstants = getChangeConstants();
        for (Field changeConstant : changeConstants) {
            ResourceChange.ChangeType.valueOf((String)changeConstant.get(null));
        }
        Assert.assertEquals(ResourceChange.ChangeType.values().length, changeConstants.size());
    }

    static Collection<Field> getChangeConstants() {
        Field[] declaredFields = ResourceChangeListener.class.getDeclaredFields();
        List<Field> constantChangeFields = new ArrayList<Field>();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                if (field.getName().startsWith(CHANGE_CONSTANT_PREFIX)) {
                    constantChangeFields.add(field);
                }
            }
        }
        return constantChangeFields;
    }
}
