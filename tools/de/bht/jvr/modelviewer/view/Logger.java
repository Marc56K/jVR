package de.bht.jvr.modelviewer.view;

import java.awt.List;

import de.bht.jvr.logger.LogListener;

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
 */

@SuppressWarnings("serial")
public class Logger extends List implements LogListener {
    public Logger(int rows) {
        super(rows);
    }

    @Override
    public void error(Class<?> clazz, String txt) {
        this.add("ERROR: [" + clazz.getSimpleName() + "] " + txt);
        scrollToLastItem();
    }

    @Override
    public void info(Class<?> clazz, String txt) {
        this.add("INFO: [" + clazz.getSimpleName() + "] " + txt);
        scrollToLastItem();
    }

    private void scrollToLastItem() {
        select(getItemCount() - 1);
    }

    @Override
    public void warning(Class<?> clazz, String txt) {
        this.add("WARNING: [" + clazz.getSimpleName() + "] " + txt);
        scrollToLastItem();
    }
}
