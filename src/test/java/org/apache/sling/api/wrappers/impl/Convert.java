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

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;

/**
 * Tests all permutations of object conversions between single values and array types, and null handling.
 */
final class Convert {

    private Convert() {
        // static methods only
    }

    public static class ConversionInput<T> {

        private final T input1;

        private final T input2;

        private final Class<? super T> inputType;

        private ConversionInput(T input1, T input2, Class<? super T> inputType) {
            this.input1 = input1;
            this.input2 = input2;
            this.inputType = inputType;
        }

        /**
         * @param expected1 Singleton or first array expected result value
         * @param expected2 Second array expected result value
         * @param expectedType The (super)class or interface used for the conversion.
         * @return this
         */
        public <U> ConversionAssert<T, U> to(U expected1, U expected2, Class<? super U> expectedType) {
            return new ConversionAssert<T, U>(input1, input2, inputType, expected1, expected2, expectedType);
        }
    }

    @SuppressWarnings("unchecked")
    public static class ConversionAssert<T,U> {

        private final T input1;
        private final T input2;
        private final Class<? super T> inputType;

        private final U expected1;
        private final U expected2;
        private final Class<? super U> expectedType;
        private final U nullValue;

        private ConversionAssert(T input1, T input2, Class<? super T> inputType, U expected1, U expected2, Class<? super U> expectedType) {
            this.input1 = input1;
            this.input2 = input2;
            this.inputType = inputType;

            this.expected1 = expected1;
            this.expected2 = expected2;
            this.expectedType = expectedType;
            this.nullValue = null;
        }

        /**
         * Do assertion
         */
        public void test() {
            Class<? super U[]> expectedArrayType = (Class<? super U[]>)Array.newInstance(this.expectedType, 0).getClass();
            assertPermuations(input1, input2, inputType, expected1, expected2, nullValue, expectedType, expectedArrayType);
        }
    }

    /**
     * @param input1 Singleton or first array input value
     * @param input2 Second array input value
     */
    public static <T> ConversionInput<T> from(T input1, T input2, Class<? super T> inputType) {
        return new ConversionInput<>(input1, input2, inputType);
    }

    private static <T, U> void assertPermuations(T input1, T input2, Class<? super T> inputType,
            U expected1, U expected2, U nullValue, Class<? super U> expectedType, Class<? super U[]> expectedArrayType) {

        // single value to single value
        assertConversion(expected1, input1, expectedType);

        // single value to array
        Object expectedSingletonArray;
        if (expected1 == null && expected2 == null) {
            expectedSingletonArray = null;
        }
        else {
            expectedSingletonArray = Array.newInstance(expectedType, 1);
            Array.set(expectedSingletonArray, 0, expected1);
        }
        assertConversion(expectedSingletonArray, input1, expectedArrayType);

        // array to array
        Object inputDoubleArray = Array.newInstance(inputType, 2);
        Array.set(inputDoubleArray, 0, input1);
        Array.set(inputDoubleArray, 1, input2);
        Object expectedDoubleArray;
        if (expected1 == null && expected2 == null) {
            expectedDoubleArray = null;
        }
        else {
            expectedDoubleArray = Array.newInstance(expectedType, 2);
            Array.set(expectedDoubleArray, 0,  expected1);
            Array.set(expectedDoubleArray, 1,  expected2);
        }
        assertConversion(expectedDoubleArray, inputDoubleArray, expectedArrayType);

        // array to single (first) value
        assertConversion(expected1, inputDoubleArray, expectedType);

        // null to single value
        assertConversion(nullValue, null, expectedType);

        // null to array
        // assertConversion(null, null, expectedArrayType);

        // empty array to single value
        Object inputEmptyArray = Array.newInstance(inputType, 0);
        assertConversion(nullValue, inputEmptyArray, expectedType);

        // empty array to array
        Object expectedEmptyArray = Array.newInstance(expectedType, 0);
        assertConversion(expectedEmptyArray, inputEmptyArray, expectedArrayType);
    }

    private static <T,U> void assertConversion(Object expected, Object input, Class<U> expectedType) {
        U result = ObjectConverter.convert(input, expectedType);
        String msg = "Convert '" + toString(input) + "' to " + expectedType.getSimpleName();
        if (expected == null) {
            assertNull(msg, result);
        }
        else if (expectedType.isArray() && !expectedType.getComponentType().isPrimitive()) {
            assertArrayEquals(msg, toStringIfDate((Object[]) expected), toStringIfDate((Object[]) result));
        }
        else {
            assertEquals(msg, toStringIfDate(expected), toStringIfDate(result));
        }
    }

    private static String toString(Object input) {
        if (input == null) {
            return "null";
        }
        else if (input.getClass().isArray()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i=0; i<Array.getLength(input); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(toString(Array.get(input, i)));
            }
            sb.append("]");
            return sb.toString();
        }
        else {
            return toStringIfDate(input);
        }
    }

    private static String toStringIfDate(Object input) {
        if (input instanceof Calendar) {
            return "(Calendar)" + ((Calendar)input).getTime().toInstant().toString();
        }
        if (input instanceof Date) {
            return "(Date)" + ((Date)input).toInstant().toString();
        }
        return null;
    }

    private static String[] toStringIfDate(Object[] input) {
        if (Calendar.class.isAssignableFrom(input.getClass().getComponentType())
                || input.getClass().getComponentType() == Date.class) {
            String[] resultArray = new String[Array.getLength(input)];
            for (int i=0; i<Array.getLength(input); i++) {
                resultArray[i] = toStringIfDate(Array.get(input, i));
            }
            return resultArray;
        }
        return null;
    }

}
