package de.bht.jvr.logger;

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
 * The listener interface for receiving log messages. The class that is
 * interested in processing a log message implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addLogListener<code> method. When
 * the log message occurs, that object's appropriate
 * method is invoked.
 * 
 * @see Log
 * @author Marc Roßbach
 */

public interface LogListener {
    /**
     * Error.
     * 
     * @param clazz
     *            the clazz
     * @param txt
     *            the error message
     */
    public void error(Class<?> clazz, String txt);

    /**
     * Info.
     * 
     * @param clazz
     *            the clazz
     * @param txt
     *            the info message
     */
    public void info(Class<?> clazz, String txt);

    /**
     * Warning.
     * 
     * @param clazz
     *            the clazz
     * @param txt
     *            the warning message
     */
    public void warning(Class<?> clazz, String txt);
}
