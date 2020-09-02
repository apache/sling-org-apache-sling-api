/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Licensed to the Apache Software Foundation (ASF) under one
 ~ or more contributor license agreements.  See the NOTICE file
 ~ distributed with this work for additional information
 ~ regarding copyright ownership.  The ASF licenses this file
 ~ to you under the Apache License, Version 2.0 (the
 ~ "License"); you may not use this file except in compliance
 ~ with the License.  You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package org.apache.sling.api.wrappers.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.Binary;
import javax.jcr.Value;

import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.ConverterFunction;
import org.osgi.util.converter.TypeRule;

public final class JcrRules {

    private JcrRules(){}

    static void addJcrRules(ConverterBuilder converterBuilder) {
        converterBuilder.rule(new TypeRule<Value, String>(Value.class, String.class, value -> {
            try {
                return value.getString();
            } catch (Exception e) {
                return (String) ConverterFunction.CANNOT_HANDLE;
            }
        }));
        converterBuilder.rule(new TypeRule<Value, InputStream>(Value.class, InputStream.class, value -> {
            try {
                return value.getBinary().getStream();
            } catch (Exception e) {
                return (InputStream) ConverterFunction.CANNOT_HANDLE;
            }
        }));
        converterBuilder.rule(new TypeRule<Value, Binary>(Value.class, Binary.class, value -> {
            try {
                return value.getBinary();
            } catch (Exception e) {
                return (Binary) ConverterFunction.CANNOT_HANDLE;
            }
        }));
        converterBuilder.rule(new TypeRule<Value, Long>(Value.class, Long.class, value -> {
            try {
                return value.getLong();
            } catch (Exception e) {
                return (Long) ConverterFunction.CANNOT_HANDLE;
            }
        }));
        converterBuilder.rule(new TypeRule<Value, Double>(Value.class, Double.class, value -> {
            try {
                return value.getDouble();
            } catch (Exception e) {
                return (Double) ConverterFunction.CANNOT_HANDLE;
            }
        }));
        converterBuilder.rule(new TypeRule<Value, BigDecimal>(Value.class, BigDecimal.class, value -> {
            try {
                return value.getDecimal();
            } catch (Exception e) {
                return (BigDecimal) ConverterFunction.CANNOT_HANDLE;
            }
        }));
        converterBuilder.rule(new TypeRule<Value, Calendar>(Value.class, Calendar.class, value -> {
            try {
                return value.getDate();
            } catch (Exception e) {
                return (Calendar) ConverterFunction.CANNOT_HANDLE;
            }
        }));
        converterBuilder.rule(new TypeRule<Value, Boolean>(Value.class, Boolean.class, value -> {
            try {
                return value.getBoolean();
            } catch (Exception e) {
                return (Boolean) ConverterFunction.CANNOT_HANDLE;
            }
        }));
    }

}
