package de.bht.jvr.logger;

import java.util.HashSet;
import java.util.Set;

/**
 * This file is part of jVR.
 *
 * Copyright 2013 Marc Roßbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * The logging system receives all log messages and notifies all attached log
 * listeners.
 * 
 * @author Marc Roßbach
 */

public class Log {
    /** The listeners. */
    private static Set<LogListener> listeners = new HashSet<LogListener>();

    /**
     * Adds a log listener.
     * 
     * @param listener
     *            the listener
     */
    public static void addLogListener(LogListener listener) {
        listeners.add(listener);
    }

    /**
     * Error.
     * 
     * @param clazz
     *            the clazz
     * @param txt
     *            the error message
     */
    public static void error(Class<?> clazz, String txt) {
        for (LogListener listener : listeners)
            listener.error(clazz, txt);
    }

    /**
     * Info.
     * 
     * @param clazz
     *            the clazz
     * @param txt
     *            the info message
     */
    public static void info(Class<?> clazz, String txt) {
        for (LogListener listener : listeners)
            listener.info(clazz, txt);
    }

    /**
     * Removes a log listener.
     * 
     * @param listener
     *            the listener
     */
    public static void removeLogListener(LogListener listener) {
        listeners.remove(listener);
    }

    /**
     * Warning.
     * 
     * @param clazz
     *            the clazz
     * @param txt
     *            the warning message
     */
    public static void warning(Class<?> clazz, String txt) {
        for (LogListener listener : listeners)
            listener.warning(clazz, txt);
    }
}
