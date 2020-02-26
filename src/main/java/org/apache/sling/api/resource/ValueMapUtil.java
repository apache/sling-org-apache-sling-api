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
package org.apache.sling.api.resource;

import org.apache.sling.api.resource.internal.FIFOValueMap;
import org.jetbrains.annotations.NotNull;

public class ValueMapUtil {

    /**
     * Merge provided Value Map into a ValueMap in a FIFO way:
     * typically <code>asFIFOValueMap(v1, v2, v3)</code> considering all of those maps have
     * a value to return to the key <code>k1</code>, will return v1's value.
     *
     * @param valueMaps list of value maps to merge
     * @return
     */
    public static ValueMap asFIFOValueMap(@NotNull ValueMap... valueMaps) {
        return new FIFOValueMap(valueMaps);
    }
}
