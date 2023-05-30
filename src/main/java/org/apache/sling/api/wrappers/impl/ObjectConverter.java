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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.osgi.util.converter.ConversionException;
import org.osgi.util.converter.Converter;
import org.osgi.util.converter.ConverterBuilder;
import org.osgi.util.converter.Converters;
import org.osgi.util.converter.TypeRule;

/**
 * Converts objects to specific types.
 */
public final class ObjectConverter {

    private static class ConverterHolder {
        private static final Converter CONVERTER;

        static {
            ConverterBuilder converterBuilder = Converters.newConverterBuilder()
                    .rule(new TypeRule<String, Calendar>(String.class, Calendar.class,
                            ObjectConverter::toCalendar))
                    .rule(new TypeRule<Date, Calendar>(Date.class, Calendar.class,
                            ObjectConverter::toCalendar))
                    .rule(new TypeRule<String, Date>(String.class, Date.class, ObjectConverter::toDate))
                    .rule(new TypeRule<Calendar, String>(Calendar.class, String.class,
                            ObjectConverter::toString))
                    .rule(new TypeRule<Date, String>(Date.class, String.class, ObjectConverter::toString))
                    .rule(new TypeRule<Calendar, Date>(Calendar.class, Date.class,
                            ObjectConverter::toDate))
                    .rule(new TypeRule<>(Calendar.class, ZonedDateTime.class, ObjectConverter::toZonedDateTime))
                    .rule(new TypeRule<ZonedDateTime, Calendar>(ZonedDateTime.class, Calendar.class, ObjectConverter::toCalendar))
                    .rule(new TypeRule<ZonedDateTime, String>(ZonedDateTime.class, String.class, ObjectConverter::toString));
            try {
                JcrRules.addJcrRules(converterBuilder);
            } catch (NoClassDefFoundError e) {
                // do nothing if the JCR API is not present
            }
            CONVERTER = converterBuilder.build();
        }
    }

    private ObjectConverter() {}

    private static String toString(ZonedDateTime zonedDateTime) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(zonedDateTime);
    }

    private static Calendar toCalendar(ZonedDateTime zonedDateTime) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zonedDateTime.getOffset()));
        calendar.setTimeInMillis(zonedDateTime.toInstant().toEpochMilli());
        return calendar;
    }

    private static ZonedDateTime toZonedDateTime(Calendar calendar) {
        return ZonedDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId().normalized());
    }

    private static String toString(Calendar cal) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(cal.getTimeInMillis()), cal.getTimeZone().toZoneId()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private static String toString(Date cal) {
        return cal.toInstant().toString();
    }

    private static Calendar toCalendar(String date) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return toCalendar(zonedDateTime);
    }

    private static Calendar toCalendar(Date date) {
        Calendar response = Calendar.getInstance();
        response.setTime(date);
        return response;
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
            return ConverterHolder.CONVERTER.convert(obj).to(type);
        } catch (ConversionException ce) {
            return null;
        }
    }

}
