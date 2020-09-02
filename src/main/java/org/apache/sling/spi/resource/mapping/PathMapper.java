/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.apache.sling.spi.resource.mapping;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * SPI interface that contributes to path rewriting.
 *
 * All registered services build a conceptual chain sorted by service ranking. The resource link is passed through the chain while any
 * chain member may or may not make adjustments to the path.
 */
@ConsumerType
public interface PathMapper {

    /**
     * Contributes to the rewrite process, may or may not make adjustments to the path
     *
     * @param path the path to be rewritten
     * @param context mapping context
     * @return the adjusted ResourceUri
     */
    @NotNull String resolve(@NotNull String path, @NotNull MappingChainContext context);

    /** Contributes to the map process, may or may not make adjustments to the resource link.
      *
     * @param path the path to be mapped
     * @param context mapping context
     * @return the adjusted path
     */
    @NotNull String map(@NotNull String path, @NotNull MappingChainContext context);

}
