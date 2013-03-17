package de.bht.jvr.renderer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import de.bht.jvr.core.pipeline.Pipeline;

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
 * A queue holding the new pipeline object for the render window.
 * (Producer/Consumer-Pattern)
 * 
 * @author Marc Roßbach
 */

public class PipelineQueue {
    /** The new pipelines. */
    private Map<RenderWindow, Pipeline> pipelines = Collections.synchronizedMap(new HashMap<RenderWindow, Pipeline>());

    private Set<RenderWindow> windows = Collections.synchronizedSet(new HashSet<RenderWindow>());

    private CyclicBarrier swapBarrier = null;

    public synchronized void commit() {
        notifyAll();
    }

    /**
     * Gets the pipeline from the queue.
     * 
     * @return the pipeline
     */
    public synchronized Pipeline get(RenderWindow win) {
        while (pipelines.get(win) == null && windows.contains(win))
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        if (windows.contains(win)) {
            Pipeline pipe = pipelines.get(win);
            pipelines.remove(win);
            notifyAll();
            return pipe;
        }

        return null;
    }

    /**
     * Puts a new pipeline object to the queue.
     * 
     * @param pipeline
     *            the pipeline
     */
    public synchronized void put(RenderWindow win, Pipeline pipeline) {
        while (pipelines.get(win) != null && windows.contains(win))
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        if (windows.contains(win))
            pipelines.put(win, pipeline);
        // notifyAll();
    }

    public void registerWindow(RenderWindow win) {
        windows.add(win);
        updateSwapBarrier();
    }

    public synchronized void unregisterWindow(RenderWindow win) {
        windows.remove(win);
        pipelines.remove(win);
        updateSwapBarrier();

        notifyAll();
    }

    private void updateSwapBarrier() {
        if (windows.size() > 0)
            swapBarrier = new CyclicBarrier(windows.size());
    }

    public synchronized void waitForEmpty() {
        while (pipelines.size() > 0)
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public void waitForSwap() throws Exception {
        if (swapBarrier != null)
            try {
                swapBarrier.await(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                // the swap barrier was waiting for windows that are already
                // closed
            }
    }
}
