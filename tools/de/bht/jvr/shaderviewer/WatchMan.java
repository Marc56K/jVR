package de.bht.jvr.shaderviewer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

public class WatchMan extends Thread {

    interface Listener {
        void dirChanged(File dir, File[] files);
    }

    private File watchedDir;
    private int interval = 100; // ms
    private boolean done = false;
    private Listener listener;

    private Map<File, Long> lastTimes;

    public WatchMan(WatchMan.Listener l) {
        listener = l;
    }

    @Override
    public void run() {
        while (!done) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {} finally {
                if (watchedDir == null)
                    continue;
            }

            File[] files = watchedDir.listFiles();

            Map<File, Long> current = new HashMap<File, Long>();
            for (File f : files)
                current.put(f, f.lastModified());

            if (!current.equals(lastTimes))
                listener.dirChanged(watchedDir, files);

            lastTimes = current;
        }
    }

    public void shutdown() {
        done = true;
    }

    public void watchDir(File dir) {
        watchedDir = dir;
    }
}
