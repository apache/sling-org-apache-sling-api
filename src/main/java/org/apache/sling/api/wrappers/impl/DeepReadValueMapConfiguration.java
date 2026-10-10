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
package org.apache.sling.api.wrappers.impl;

import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(
        service = {},
        immediate = true,
        configurationPid = "org.apache.sling.api.wrappers.DeepReadValueMapDecorator")
@Designate(ocd = DeepReadValueMapConfiguration.Configuration.class)
public class DeepReadValueMapConfiguration {

    @ObjectClassDefinition(
            id = "org.apache.sling.api.wrappers.DeepReadValueMapDecorator",
            name = "Apache Sling Deep Read Value Map",
            description = "Controls resource subtree containment for DeepReadValueMapDecorator.")
    public @interface Configuration {
        @AttributeDefinition(
                name = "Enforce subtree containment",
                description = "Throw IllegalArgumentException for out-of-subtree or invalid deep reads. "
                        + "When disabled, log a WARN and allow the lookup.")
        boolean enforce() default false;
    }

    private static volatile boolean enforcementEnabled;

    public DeepReadValueMapConfiguration() {}

    @Activate
    @Modified
    public void configure(@NotNull final Configuration configuration) {
        enforcementEnabled = configuration.enforce();
    }

    @Deactivate
    public void deactivate() {
        enforcementEnabled = false;
    }

    public static boolean isEnforcementEnabled() {
        return enforcementEnabled;
    }
}
