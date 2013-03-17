package de.bht.jvr.logger;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

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

public class LogFilePrinter implements LogListener {
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

    private FileWriter logFile = null;

    /**
     * Instantiates a new log printer.
     */
    public LogFilePrinter() {
        openFile();
    }

    /**
     * Instantiates a new log printer.
     * 
     * @param maxLogs
     *            the maximum number of log messages (-1 = infinity)
     * @throws FileNotFoundException
     */
    public LogFilePrinter(int maxLogs) {
        maxErrorgLogs = maxLogs;
        maxWarningLogs = maxLogs;
        maxInfoLogs = maxLogs;
        openFile();
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
    public LogFilePrinter(int maxErrorLogs, int maxWarningLogs, int maxInfoLogs) {
        maxErrorgLogs = maxErrorLogs;
        this.maxWarningLogs = maxWarningLogs;
        this.maxInfoLogs = maxInfoLogs;
        openFile();
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.logger.LogListener#error(java.lang.Class,
     * java.lang.String)
     */
    @Override
    public void error(Class<?> clazz, String txt) {
        if (errorLogCount < maxErrorgLogs || maxErrorgLogs == -1) {
            println(Thread.currentThread() + " ERROR: [" + clazz.getSimpleName() + "] " + txt);
            errorLogCount++;
        }
    }

    @Override
    protected void finalize() {
        synchronized (logFile) {
            if (logFile != null)
                try {
                    logFile.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
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
            println(Thread.currentThread() + " INFO: [" + clazz.getSimpleName() + "] " + txt);
            infoLogCount++;
        }
    }

    private void openFile() {
        try {
            logFile = new FileWriter("jvr_log.txt", false);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private synchronized void println(String txt) {
        if (logFile != null)
            try {
                logFile.write(txt + "\r\n");
            } catch (IOException e) {}
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.logger.LogListener#warning(java.lang.Class,
     * java.lang.String)
     */
    @Override
    public void warning(Class<?> clazz, String txt) {
        if (warningLogCount < maxWarningLogs || maxWarningLogs == -1) {
            println(Thread.currentThread() + " WARNING: [" + clazz.getSimpleName() + "] " + txt);
            warningLogCount++;
        }
    }
}
