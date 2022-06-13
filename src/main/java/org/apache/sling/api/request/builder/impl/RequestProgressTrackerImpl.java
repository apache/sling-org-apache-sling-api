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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.request.RequestProgressTracker;

/**
 * The <code>SlingRequestProgressTracker</code> class provides the
 * functionality to track the progress of request processing. Instances of this
 * class are provided through the
 * {@link org.apache.sling.api.SlingHttpServletRequest#getRequestProgressTracker()}
 * method.
 * <p>
 * The following functionality is provided:
 * <ol>
 * <li>Track the progress of request processing through the
 * {@link #log(String)} and {@link #log(String, Object...)} methods.
 * <li>Ability to measure and track processing times of parts of request
 * processing through the {@link #startTimer(String)} and
 * {@link #logTimer(String)} methods.
 * <li>Dumping the recording messages through the
 * {@link #dump(PrintWriter)} method.
 * <li>Resetting the tracker through the {@link #reset()} method.
 * </ol>
 * <p>
 * <b>Tracking Request Processing</b>
 * <p>
 * As the request being processed, certain steps may be tracked by calling
 * either of the <code>log</code> methods. A tracking entry consists of a time
 * stamp managed by this class, and a tracking message noting the actual step being
 * tracked.
 * <p>
 * <b>Timing Processing Steps</b>
 * </p>
 * Certain steps during request processing may need to be timed in that the time
 * required for processing should be recorded. Instances of this class maintain
 * a map of named timers. Each timer is started (initialized or reset) by
 * calling the {@link #startTimer(String)} method. This method just records the
 * starting time of the named timer.
 * <p>
 * To record the number of milliseconds ellapsed since a timer has been started,
 * the {@link #logTimer(String)} method may be called. This method logs the
 * tracking entry with message
 * consisting of the name of the timer and the number of milliseconds ellapsed
 * since the timer was last {@link #startTimer(String) started}. The
 * {@link #logTimer(String)} method may be called multiple times to record
 * several timed steps.
 * <p>
 * Additional information can be logged using the {@link #logTimer(String, String, Object...)}
 * method.
 * <p>
 * Calling the {@link #startTimer(String)} method with the name of timer which
 * already exists, resets the start time of the named timer to the current
 * system time.
 * <p>
 * <b>Dumping Tracking Entries</b>
 * <p>
 * The {@link #dump(PrintWriter)} methods writes all tracking entries to the given
 * <code>PrintWriter</code>. Each entry is written on a single line
 * consisting of the following fields:
 * <ol>
 * <li>The number of milliseconds since the last {@link #reset()} (or creation)
 * of this timer.
 * <li>The absolute time of the timer in parenthesis.
 * <li>The entry message
 * </ol>
 */
public class RequestProgressTrackerImpl implements RequestProgressTracker {

    /**
     * The name of the timer tracking the processing time of the complete
     * process.
     */
    private static final String REQUEST_PROCESSING_TIMER = "Request Processing";

    /** Prefix for log messages */
    private static final String LOG_PREFIX = "LOG ";

    /** Prefix for comment messages */
    private static final String COMMENT_PREFIX = "COMMENT ";

    /** TIMER_END format explanation */
    private static final String TIMER_END_FORMAT = "{<elapsed microseconds>,<timer name>} <optional message>";

    /** The leading nanosecond number is left-padded with white-space to this width. */
    private static final int PADDING_WIDTH = 7;

    /**
     * The system time at creation of this instance or the last {@link #reset()}.
     */
    private final long processingStart;

    /**
     * The system time when {@link #done()} was called or -1 while processing is in progress.
     */
    private long processingEnd;

    /**
     * The list of tracking entries.
     */
    private final List<TrackingEntry> entries = new ArrayList<TrackingEntry>();
    /**
     * Map of named timers indexed by timer name storing the system time of
     * start of the respective timer.
     */
    private final Map<String, Long> namedTimerEntries = new HashMap<String, Long>();

    private final FastMessageFormat messageFormat = new FastMessageFormat();

    /**
     * Creates a new request progress tracker.
     */
    public RequestProgressTrackerImpl() {
        // enter initial messages
        this.processingStart = startTimerInternal(REQUEST_PROCESSING_TIMER);
        this.processingEnd = -1;

        this.entries.add(new TrackingEntry(COMMENT_PREFIX + "timer_end format is " + TIMER_END_FORMAT));
    }

    @Override
    public Iterator<String> getMessages() {
        return new Iterator<String>() {
            private final Iterator<TrackingEntry> entryIter = entries.iterator();

            public boolean hasNext() {
                return entryIter.hasNext();
            }

            public String next() {
                // throws NoSuchElementException if no entries any more
                final TrackingEntry entry = entryIter.next();
                final long offset = entry.getTimeStamp() - getTimeStamp();
                return formatMessage(offset, entry.getMessage());
            }

            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }

    private String formatMessage(final long offset, final String message) {
        // Set exact length to avoid array copies within StringBuilder
        final StringBuilder sb = new StringBuilder(PADDING_WIDTH + 1 +  message.length() + 1);
        final String offsetStr = Long.toString(offset / 1000);
        for (int i = PADDING_WIDTH - offsetStr.length(); i > 0; i--) {
            sb.append(' ');
        }
        sb.append(offsetStr).append(' ')
          .append(message.replace('\n', '_').replace('\r', '_'))
          .append('\n');
        return sb.toString();
    }

    @Override
    public void dump(final PrintWriter writer) {
        logTimer(REQUEST_PROCESSING_TIMER,
            "Dumping SlingRequestProgressTracker Entries");

        final Iterator<String> messages = getMessages();
        while (messages.hasNext()) {
            writer.print(messages.next());
        }
    }

    @Override
    public void log(final String message) {
        entries.add(new TrackingEntry(LOG_PREFIX + message));
    }

    @Override
    public void log(final String format, final Object... args) {
        String message = messageFormat.format(format, args);
        entries.add(new TrackingEntry(LOG_PREFIX + message));
    }

    @Override
    public void startTimer(final String name) {
        startTimerInternal(name);
    }

    /**
     * Start the named timer and returns the start time in milliseconds.
     * Logs a message with format
     * <pre>
     * TIMER_START{<name>} <optional message>
     * </pre>
     */
    private long startTimerInternal(final String name) {
        long timer = System.nanoTime();
        namedTimerEntries.put(name, timer);
        entries.add(new TrackingEntry(timer, "TIMER_START{" + name + "}"));
        return timer;
    }

    @Override
    public void logTimer(final String name) {
        if (namedTimerEntries.containsKey(name)) {
            logTimerInternal(name, null, namedTimerEntries.get(name));
        }
    }

    @Override
    public void logTimer(final String name, final String format, final Object... args) {
        if (namedTimerEntries.containsKey(name)) {
            logTimerInternal(name, messageFormat.format(format, args), namedTimerEntries.get(name));
        }
    }

    /**
     * Log a timer entry, including start, end and elapsed time using TIMER_END_FORMAT
     */
    private void logTimerInternal(final String name, final String msg, final long startTime) {
        final StringBuilder sb = new StringBuilder();
        sb.append("TIMER_END{");
        sb.append((System.nanoTime() - startTime) / 1000);
        sb.append(',');
        sb.append(name);
        sb.append('}');
        if(msg != null) {
            sb.append(' ');
            sb.append(msg);
        }
        entries.add(new TrackingEntry(sb.toString()));
    }

    @Override
    public void done() {
        if(processingEnd != -1) return;
        logTimer(REQUEST_PROCESSING_TIMER, REQUEST_PROCESSING_TIMER);
        processingEnd = System.nanoTime();
    }

    private long getTimeStamp() {
        return processingStart;
    }

    @Override
    public long getDuration() {
        if (processingEnd != -1) {
            return processingEnd - processingStart;
        }
        return System.nanoTime() - processingStart;
    }

    /** Process tracker entry keeping timestamp, tag and message */
    private static class TrackingEntry {

        // creation time stamp
        private final long timeStamp;

        // tracking message
        private final String message;

        TrackingEntry(final String message) {
            this(System.nanoTime(), message);
        }

        TrackingEntry(final long timeStamp, final String message) {
            this.timeStamp = timeStamp;
            this.message = message;
        }

        long getTimeStamp() {
            return timeStamp;
        }

        String getMessage() {
            return message;
        }
    }
}
