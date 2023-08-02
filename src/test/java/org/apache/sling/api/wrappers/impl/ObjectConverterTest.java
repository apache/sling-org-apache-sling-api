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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.jcr.Binary;
import javax.jcr.Value;

import org.junit.Test;

public class ObjectConverterTest {

    private static final String STRING_1 = "item1";
    private static final String STRING_2 = "item2";
    private static final boolean BOOLEAN_1 = true;
    private static final boolean BOOLEAN_2 = false;
    private static final byte BYTE_1 = (byte)0x01;
    private static final byte BYTE_2 = (byte)0x02;
    private static final short SHORT_1 = (short)12;
    private static final short SHORT_2 = (short)34;
    private static final int INT_1 = 55;
    private static final int INT_2 = -123;
    private static final long LONG_1 = 1234L;
    private static final long LONG_2 = -4567L;
    private static final float FLOAT_1 = 1.23f;
    private static final float FLOAT_2 = -4.56f;
    private static final double DOUBLE_1 = 12.34d;
    private static final double DOUBLE_2 = -45.67d;
    private static final BigDecimal BIGDECIMAL_1 = new BigDecimal("12345.67");
    private static final BigDecimal BIGDECIMAL_2 = new BigDecimal("-23456.78");
    private static final Calendar CALENDAR_1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"), Locale.US);
    private static final Calendar CALENDAR_2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+3"), Locale.US);
    {
        CALENDAR_1.set(2016, Calendar.NOVEMBER, 15, 8, 20, 30);
        CALENDAR_2.set(2015, Calendar.JULY, 31, 19, 10, 20);
    }
    private static final Date DATE_1 = CALENDAR_1.getTime();
    private static final Date DATE_2 = CALENDAR_2.getTime();

    @Test
    public void testDateToString() {
        Convert.from(STRING_1, STRING_2, String.class).to(STRING_1, STRING_2, String.class).test();
        Convert.from(BOOLEAN_1, BOOLEAN_2, boolean.class).to(Boolean.toString(BOOLEAN_1), Boolean.toString(BOOLEAN_2), String.class).test();
        Convert.from(BOOLEAN_1, BOOLEAN_2, Boolean.class).to(Boolean.toString(BOOLEAN_1), Boolean.toString(BOOLEAN_2), String.class).test();
        Convert.from(BYTE_1, BYTE_2, byte.class).to(Byte.toString(BYTE_1), Byte.toString(BYTE_2), String.class).test();
        Convert.from(BYTE_1, BYTE_2, Byte.class).to(Byte.toString(BYTE_1), Byte.toString(BYTE_2), String.class).test();
        Convert.from(SHORT_1, SHORT_2, short.class).to(Short.toString(SHORT_1), Short.toString(SHORT_2), String.class).test();
        Convert.from(SHORT_1, SHORT_2, Short.class).to(Short.toString(SHORT_1), Short.toString(SHORT_2), String.class).test();
        Convert.from(INT_1, INT_2, int.class).to(Integer.toString(INT_1), Integer.toString(INT_2), String.class).test();
        Convert.from(INT_1, INT_2, Integer.class).to(Integer.toString(INT_1), Integer.toString(INT_2), String.class).test();
        Convert.from(LONG_1, LONG_2, long.class).to(Long.toString(LONG_1), Long.toString(LONG_2), String.class).test();
        Convert.from(LONG_1, LONG_2, Long.class).to(Long.toString(LONG_1), Long.toString(LONG_2), String.class).test();
        Convert.from(FLOAT_1, FLOAT_2, float.class).to(Float.toString(FLOAT_1), Float.toString(FLOAT_2), String.class).test();
        Convert.from(FLOAT_1, FLOAT_2, float.class).to(Float.toString(FLOAT_1), Float.toString(FLOAT_2), String.class).test();
        Convert.from(DOUBLE_1, DOUBLE_2, double.class).to(Double.toString(DOUBLE_1), Double.toString(DOUBLE_2), String.class).test();
        Convert.from(DOUBLE_1, DOUBLE_2, Double.class).to(Double.toString(DOUBLE_1), Double.toString(DOUBLE_2), String.class).test();
        Convert.from(BIGDECIMAL_1, BIGDECIMAL_2, BigDecimal.class).to(BIGDECIMAL_1.toString(), BIGDECIMAL_2.toString(), String.class).test();
        Convert.from(CALENDAR_1, CALENDAR_2, Calendar.class).to(calendarToString(CALENDAR_1), calendarToString(CALENDAR_2), String.class).test();
        Convert.from(DATE_1, DATE_2, Date.class).to(calendarToString(toCalendar(DATE_1)), calendarToString(toCalendar(DATE_2)), String.class).test();
    }

    private String calendarToString(Calendar calendar) {
        return calendar.getTime().toInstant().toString();
    }

    private Calendar toCalendar(Date date1) {
        Calendar response = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"));
        response.setTime(date1);
        return response;
    }

    @Test
    public void testToBoolean() {
        Convert.from(BOOLEAN_1, BOOLEAN_2, boolean.class).to(BOOLEAN_1, BOOLEAN_2, Boolean.class).test();
        Convert.from(BOOLEAN_1, BOOLEAN_2, Boolean.class).to(BOOLEAN_1, BOOLEAN_2, Boolean.class).test();
        Convert.from(Boolean.toString(BOOLEAN_1), Boolean.toString(BOOLEAN_2), String.class).to(BOOLEAN_1, BOOLEAN_2, Boolean.class).test();
        Convert.from(INT_1, INT_2, int.class).to(true, true, boolean.class).test();
        Convert.from(1, 0, int.class).to(true, false, boolean.class).test();
        Convert.from(DATE_1, DATE_2, Date.class).to(false, false, boolean.class).test();
    }

    @Test
    public void testToByte() {
        Convert.from(BYTE_1, BYTE_2, byte.class).to(BYTE_1, BYTE_2, Byte.class).test();
        Convert.from(BYTE_1, BYTE_2, Byte.class).to(BYTE_1, BYTE_2, Byte.class).test();
        Convert.from(Byte.toString(BYTE_1), Byte.toString(BYTE_2), String.class).to(BYTE_1, BYTE_2, byte.class).test();

        // test conversion from other number types
        Convert.from(INT_1, INT_2, Integer.class).to((byte)INT_1, (byte)INT_2, byte.class).test();
        Convert.from(INT_1, INT_2, int.class).to((byte)INT_1, (byte)INT_2, Byte.class).test();

        // test other types that should not be converted
        Convert.from(DATE_1, DATE_2, Date.class).to(null, null, Byte.class).test();
    }

    @Test
    public void testToShort() {
        Convert.from(SHORT_1, SHORT_2, short.class).to(SHORT_1, SHORT_2, Short.class).test();
        Convert.from(SHORT_1, SHORT_2, Short.class).to(SHORT_1, SHORT_2, short.class).test();
        Convert.from(Short.toString(SHORT_1), Short.toString(SHORT_2), String.class).to(SHORT_1, SHORT_2, short.class).test();

        // test conversion from other number types
        Convert.from(INT_1, INT_2, Integer.class).to((short)INT_1, (short)INT_2, short.class).test();
        Convert.from(INT_1, INT_2, int.class).to((short)INT_1, (short)INT_2, Short.class).test();

        // test other types that should not be converted
        Convert.from(DATE_1, DATE_2, Date.class).to(null, null, Short.class).test();
    }

    @Test
    public void testToInteger() {
        Convert.from(INT_1, INT_2, int.class).to(INT_1, INT_2, int.class).test();
        Convert.from(INT_1, INT_2, Integer.class).to(INT_1, INT_2, int.class).test();
        Convert.from(Integer.toString(INT_1), Integer.toString(INT_2), String.class).to(INT_1, INT_2, int.class).test();

        // test conversion from other number types
        Convert.from(SHORT_1, SHORT_2, Short.class).to((int)SHORT_1, (int)SHORT_2, Integer.class).test();
        Convert.from(SHORT_1, SHORT_2, short.class).to((int)SHORT_1, (int)SHORT_2, int.class).test();

        // test other types that should not be converted
        Convert.from(DATE_1, DATE_2, Date.class).to(null, null, Integer.class).test();
    }

    @Test
    public void testToLong() {
        Convert.from(LONG_1, LONG_2, long.class).to(LONG_1, LONG_2, long.class).test();
        Convert.from(LONG_1, LONG_2, Long.class).to(LONG_1, LONG_2, Long.class).test();
        Convert.from(Long.toString(LONG_1), Long.toString(LONG_2), String.class).to(LONG_1, LONG_2, long.class).test();

        // test conversion from other number types
        Convert.from(SHORT_1, SHORT_2, Short.class).to((long)SHORT_1, (long)SHORT_2, long.class).test();
        Convert.from(SHORT_1, SHORT_2, short.class).to((long)SHORT_1, (long)SHORT_2, Long.class).test();

        // test conversion from DATE to LONG
        Convert.from(DATE_1, DATE_2, Date.class).to(DATE_1.getTime(), DATE_2.getTime(), long.class).test();
    }

    @Test
    public void testToFloat() {
        Convert.from(FLOAT_1, FLOAT_2, float.class).to(FLOAT_1, FLOAT_2, float.class).test();
        Convert.from(FLOAT_1, FLOAT_2, Float.class).to(FLOAT_1, FLOAT_2, float.class).test();
        Convert.from(Float.toString(FLOAT_1), Float.toString(FLOAT_2), String.class).to(FLOAT_1, FLOAT_2, float.class).test();

        // test conversion from other number types
        Convert.from(SHORT_1, SHORT_2, Short.class).to((float)SHORT_1, (float)SHORT_2, Float.class).test();
        Convert.from(SHORT_1, SHORT_2, short.class).to((float)SHORT_1, (float)SHORT_2, float.class).test();

        // test other types that should not be converted
        Convert.from(DATE_1, DATE_2, Date.class).to(null, null, Float.class).test();
    }

    @Test
    public void testToDouble() {
        Convert.from(DOUBLE_1, DOUBLE_2, double.class).to(DOUBLE_1, DOUBLE_2, double.class).test();
        Convert.from(DOUBLE_1, DOUBLE_2, Double.class).to(DOUBLE_1, DOUBLE_2, Double.class).test();
        Convert.from(Double.toString(DOUBLE_1), Double.toString(DOUBLE_2), String.class).to(DOUBLE_1, DOUBLE_2, double.class).test();

        // test conversion from other number types
        Convert.from(SHORT_1, SHORT_2, Short.class).to((double)SHORT_1, (double)SHORT_2, Double.class).test();
        Convert.from(SHORT_1, SHORT_2, short.class).to((double)SHORT_1, (double)SHORT_2, double.class).test();

        // test other types that should not be converted
        Convert.from(DATE_1, DATE_2, Date.class).to(null, null, Double.class).test();
    }

    @Test
    public void testToBigDecimal() {
        Convert.from(BIGDECIMAL_1, BIGDECIMAL_2, BigDecimal.class).to(BIGDECIMAL_1, BIGDECIMAL_2, BigDecimal.class).test();
        Convert.from(BIGDECIMAL_1.toString(), BIGDECIMAL_2.toString(), String.class).to(BIGDECIMAL_1, BIGDECIMAL_2, BigDecimal.class).test();

        // test conversion from other number types
        Convert.from(LONG_1, LONG_2, Long.class).to(BigDecimal.valueOf(LONG_1), BigDecimal.valueOf(LONG_2), BigDecimal.class).test();
        Convert.from(LONG_1, LONG_2, Long.class).to(BigDecimal.valueOf(LONG_1), BigDecimal.valueOf(LONG_2), BigDecimal.class).test();
        Convert.from(DOUBLE_1, DOUBLE_2, Double.class).to(BigDecimal.valueOf(DOUBLE_1), BigDecimal.valueOf(DOUBLE_2), BigDecimal.class).test();
        Convert.from(DOUBLE_1, DOUBLE_2, double.class).to(BigDecimal.valueOf(DOUBLE_1), BigDecimal.valueOf(DOUBLE_2), BigDecimal.class).test();

        // test other types that should not be converted
        Convert.from(DATE_1, DATE_2, Date.class).to(null, null, BigDecimal.class).test();
    }

    @Test
    public void testToCalendar() {
        Convert.from(CALENDAR_1, CALENDAR_2, Calendar.class).to(CALENDAR_1, CALENDAR_2, Calendar.class).test();
        Convert.from(calendarToString(CALENDAR_1), calendarToString(CALENDAR_2), String.class).to(CALENDAR_1, CALENDAR_2, Calendar.class).test();

        // test conversion from other date types
        Convert.from(DATE_1, DATE_2, Date.class).to(toCalendar(DATE_1), toCalendar(DATE_2), Calendar.class).test();

        // test other types that should not be converted
        Convert.from(STRING_1, STRING_2, String.class).to(null, null, Calendar.class).test();
        Convert.from(BOOLEAN_1, BOOLEAN_2, Boolean.class).to(null, null, Calendar.class).test();
    }

    @Test
    public void testToDate() {
        Convert.from(DATE_1, DATE_2, Date.class).to(DATE_1, DATE_2, Date.class).test();
        Convert.from(dateToString(DATE_1), dateToString(DATE_2), String.class).to(DATE_1, DATE_2, Date.class).test();

        // test conversion from other date types
        Convert.from(CALENDAR_1, CALENDAR_2, Calendar.class).to(toDate(CALENDAR_1), toDate(CALENDAR_2), Date.class).test();

        // test other types that should not be converted
        Convert.from(STRING_1, STRING_2, String.class).to(null, null, Date.class).test();
        Convert.from(BOOLEAN_1, BOOLEAN_2, Boolean.class).to(null, null, Date.class).test();
    }

    private Date toDate(Calendar calendar1) {
        return calendar1.getTime();
    }

    private String dateToString(Date date1) {
        return date1.toInstant().toString();
    }

    @Test
    public void testPrimitiveByteArray() {
        byte[] array = new byte[] { 0x01, 0x02, 0x03 };
        assertArrayEquals(array, ObjectConverter.convert(array, byte[].class));
        assertArrayEquals(new byte[0], ObjectConverter.convert(new byte[0], byte[].class));
        assertNull(ObjectConverter.convert(null, byte[].class));
    }

    @Test
    public void testJcrStringValue() throws Exception {
        Value stringValue = mock(Value.class);
        when(stringValue.getString()).thenReturn("42");
        when(stringValue.getLong()).thenReturn(42L);
        when(stringValue.getDouble()).thenReturn(42.0);
        BigDecimal bigDecimal = new BigDecimal(42);
        when(stringValue.getDecimal()).thenReturn(bigDecimal);
        assertEquals("42", ObjectConverter.convert(stringValue, String.class));
        assertEquals(42L, (long) ObjectConverter.convert(stringValue, Long.class));
        assertEquals(42.0, ObjectConverter.convert(stringValue, Double.class), 0);
        assertEquals(bigDecimal, ObjectConverter.convert(stringValue, BigDecimal.class));
    }

    @Test
    public void testJcrStreamValue() throws Exception {
        Value streamValue = mock(Value.class);
        InputStream stream = mock(InputStream.class);
        Binary bin = mock(Binary.class);
        when(streamValue.getBinary()).thenReturn(bin);
        when(bin.getStream()).thenReturn(stream);
        assertEquals(stream, ObjectConverter.convert(streamValue, InputStream.class));
    }

    @Test
    public void testJcrBinaryValue() throws Exception {
        Value binaryValue = mock(Value.class);
        Binary binary = mock(Binary.class);
        when(binaryValue.getBinary()).thenReturn(binary);
        assertEquals(binary, ObjectConverter.convert(binaryValue, Binary.class));
    }

    @Test
    public void testJcrNumericValue() throws Exception {
        Value numericValue = mock(Value.class);
        when(numericValue.getLong()).thenReturn(42L);
        when(numericValue.getString()).thenReturn("42");
        when(numericValue.getDouble()).thenReturn(42.0);
        BigDecimal bigDecimal = new BigDecimal(42);
        when(numericValue.getDecimal()).thenReturn(bigDecimal);
        assertEquals(42L, (long) ObjectConverter.convert(numericValue, Long.class));
        assertEquals("42", ObjectConverter.convert(numericValue, String.class));
        assertEquals(42.0, ObjectConverter.convert(numericValue, Double.class), 0);
        assertEquals(bigDecimal, ObjectConverter.convert(numericValue, BigDecimal.class));
    }

    @Test
    public void testJcrDateValue() throws Exception {
        Value dateValue = mock(Value.class);
        Calendar calendar = Calendar.getInstance();
        when(dateValue.getDate()).thenReturn(calendar);
        assertEquals(calendar, ObjectConverter.convert(dateValue, Calendar.class));
    }

    @Test
    public void testJcrDateValueWithTimeZone() throws Exception {
        Value dateValue = mock(Value.class);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"));
        when(dateValue.getDate()).thenReturn(calendar);
        assertEquals(calendar, ObjectConverter.convert(dateValue, Calendar.class));
    }

    @Test
    public void testBooleanValue() throws Exception {
        Value value = mock(Value.class);
        when(value.getBoolean()).thenReturn(true);
        when(value.getString()).thenReturn("true");
        assertTrue(ObjectConverter.convert(value, Boolean.class));
        assertEquals("true", ObjectConverter.convert(value, String.class));
    }

    @Test
    public void testCalendarWithTimeZone() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"));
        calendar.set(2022, Calendar.AUGUST, 19, 11, 46, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        assertEquals("2022-08-19T11:46:00.001+02:00", ObjectConverter.convert(calendar, String.class));
    }

    @Test
    public void testZonedDateTimeToString() {
        ZoneId zoneId = ZoneId.of("UTC+1");
        ZonedDateTime zdt = ZonedDateTime.of(2015, 11, 30, 23, 45, 59,999999999, zoneId);
        assertEquals("2015-11-30T23:45:59.999999999+01:00", ObjectConverter.convert(zdt, String.class));
        assertEquals(String.class, ObjectConverter.convert(zdt, String.class).getClass());
    }

    @Test
    public void testToZonedDateTime() {
        ZoneId zoneId = ZoneId.of("GMT+2").normalized();
        ZonedDateTime zdt = ZonedDateTime.of(2022, 8, 19, 11, 46, 0, 1000000, zoneId);
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+2"));
        calendar.set(2022, Calendar.AUGUST, 19, 11, 46, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        assertEquals(ZonedDateTime.class, ObjectConverter.convert(calendar, ZonedDateTime.class).getClass());
        assertEquals(zdt, ObjectConverter.convert(calendar, ZonedDateTime.class));
    }
}
