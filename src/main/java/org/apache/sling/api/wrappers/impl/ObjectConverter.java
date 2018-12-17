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

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.osgi.util.converter.ConversionException;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeRule;

/**
 * Converts objects to specific types.
 */
public final class ObjectConverter {

    private ObjectConverter() {
    }

    private static Converter converter;

    private static Converter getConverter() {
        // TODO at some point it may be practical to have TypeRules as OSGI services to
        // add more complex conversions much in the way that adaptTo has evolved
        if (converter == null) {
            synchronized (ObjectConverter.class) {
                if (converter == null) {
                    converter = Converters.newConverterBuilder()
                            .rule(new TypeRule<String, GregorianCalendar>(String.class, GregorianCalendar.class,
                                    ObjectConverter::toCalendar))
                            .rule(new TypeRule<Date, GregorianCalendar>(Date.class, GregorianCalendar.class,
                                    ObjectConverter::toCalendar))
                            .rule(new TypeRule<String, Date>(String.class, Date.class, ObjectConverter::toDate))
                            .rule(new TypeRule<Calendar, String>(Calendar.class, String.class,
                                    ObjectConverter::toString))
                            .rule(new TypeRule<Date, String>(Date.class, String.class, ObjectConverter::toString))
                            .rule(new TypeRule<GregorianCalendar, Date>(GregorianCalendar.class, Date.class,
                                    ObjectConverter::toDate))
                            .build();
                }
            }
        }
        return converter;
    }

    private static String toString(Calendar cal) {
        return cal.toInstant().toString();
    }

    private static String toString(Date cal) {
        return cal.toInstant().toString();
    }

    private static GregorianCalendar toCalendar(String date) {
        Calendar response = Calendar.getInstance();
        response.setTime(Date.from(Instant.parse(date)));
        return (GregorianCalendar) response;
    }

    private static GregorianCalendar toCalendar(Date date) {
        Calendar response = Calendar.getInstance();
        response.setTime(date);
        return (GregorianCalendar) response;
    }

    private static Date toDate(String date) {
        return Date.from(Instant.parse(date));
    }

    private static Date toDate(Calendar cal) {
        return cal.getTime();
    }

    /**
     * Converts the object to the given type.
     * 
     * @param obj
     *            object
     * @param type
     *            type
     * @param <T>
     *            Target type
     * @return the converted object
     */
    public static <T> T convert(Object obj, Class<T> type) {
        if (obj == null) {
            return null;
        }
        try {
            return getConverter().convert(obj).to(type);
        } catch (ConversionException ce) {
            return null;
        }
    }

}
