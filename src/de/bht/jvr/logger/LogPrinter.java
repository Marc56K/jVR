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
 * The log printer prints log messages to the console.
 * 
 * @author Marc Roßbach
 */

public class LogPrinter implements LogListener {
    /** The max errorg logs. */
    private int maxErrorgLogs = -1;

    /** The max warning logs. */
    private int maxWarningLogs = -1;

    /** The max info logs. */
    private int maxInfoLogs = -1;

    /** The error log count. */
    private int errorLogCount = 0;

    /** The warning log count. */
    private int warningLogCount = 0;

    /** The info log count. */
    private int infoLogCount = 0;

    /**
     * Instantiates a new log printer.
     */
    public LogPrinter() {}

    /**
     * Instantiates a new log printer.
     * 
     * @param maxLogs
     *            the maximum number of log messages (-1 = infinity)
     */
    public LogPrinter(int maxLogs) {
        maxErrorgLogs = maxLogs;
        maxWarningLogs = maxLogs;
        maxInfoLogs = maxLogs;
    }

    /**
     * Instantiates a new log printer.
     * 
     * @param maxErrorLogs
     *            the maximum number of error messages (-1 = infinity)
     * @param maxWarningLogs
     *            the maximum number of warning messages (-1 = infinity)
     * @param maxInfoLogs
     *            the maximum number of info messages (-1 = infinity)
     */
    public LogPrinter(int maxErrorLogs, int maxWarningLogs, int maxInfoLogs) {
        maxErrorgLogs = maxErrorLogs;
        this.maxWarningLogs = maxWarningLogs;
        this.maxInfoLogs = maxInfoLogs;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.logger.LogListener#error(java.lang.Class,
     * java.lang.String)
     */
    @Override
    public void error(Class<?> clazz, String txt) {
        if (errorLogCount < maxErrorgLogs || maxErrorgLogs == -1) {
            System.err.println(Thread.currentThread() + " ERROR: [" + clazz.getSimpleName() + "] " + txt);
            errorLogCount++;
        }
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.logger.LogListener#info(java.lang.Class,
     * java.lang.String)
     */
    @Override
    public void info(Class<?> clazz, String txt) {
        if (infoLogCount < maxInfoLogs || maxInfoLogs == -1) {
            System.out.println(Thread.currentThread() + " INFO: [" + clazz.getSimpleName() + "] " + txt);
            infoLogCount++;
        }
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.logger.LogListener#warning(java.lang.Class,
     * java.lang.String)
     */
    @Override
    public void warning(Class<?> clazz, String txt) {
        if (warningLogCount < maxWarningLogs || maxWarningLogs == -1) {
            System.err.println(Thread.currentThread() + " WARNING: [" + clazz.getSimpleName() + "] " + txt);
            warningLogCount++;
        }
    }

}
