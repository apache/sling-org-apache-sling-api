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
package org.apache.sling.api.request.builder.impl;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;

import org.apache.sling.api.request.RequestProgressTracker;

/**
 * Internal {@link RequestProgressTracker} implementation.
 */
public class RequestProgressTrackerImpl implements RequestProgressTracker {

    @Override
    public void log(String message) {
        // does nothing in this mock class
    }

    @Override
    public void log(String format, Object... args) {
        // does nothing in this mock class
    }

    @Override
    public void startTimer(String timerName) {
        // does nothing in this mock class
    }

    @Override
    public void logTimer(String timerName) {
        // does nothing in this mock class
    }

    @Override
    public void logTimer(String timerName, String format, Object... args) {
        // does nothing in this mock class
    }

    @Override
    public Iterator<String> getMessages() {
        return Collections.emptyIterator();
    }

    @Override
    public void dump(PrintWriter writer) {
        // does nothing in this mock class
    }

    @Override
    public void done() {
        // does nothing in this mock class
    }
}
