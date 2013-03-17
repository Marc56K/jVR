package de.bht.jvr.util;

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
 * Use a <code>StopWatch</code> to measure the passing of time and along the way
 * determine the current frame rate.
 */

public class StopWatch {
    private long last = System.nanoTime();
    private float time = 0;
    private long frames = 0;

    /**
     * The number of frames currently rendered per second.
     */
    public float fps = 0;

    /**
     * Determine the time that passed since the last call to this function. On
     * the first call, the time since instance construction is returned.
     * 
     * @return The elapsed time in seconds.
     */
    public float elapsed() {
        long now = System.nanoTime();
        float elapsed = (now - last) * 1e-9f;
        time += elapsed;
        frames += 1;
        last = now;

        if (time >= 1f) {
            fps = frames / time;
            time = 0;
            frames = 0;
        }

        return elapsed;
    }

    /**
     * Reset internal time keeping state.
     */
    public void reset() {
        last = System.nanoTime();
        time = 0;
        frames = 0;
    }
}
