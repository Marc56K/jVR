package de.bht.jvr.renderer;

import java.util.ArrayList;
import java.util.List;

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
 * The viewer manages all render windows.
 * 
 * @author Marc Roßbach
 */

public class Viewer {
    /** The windows. */
    private List<RenderWindow> windows = new ArrayList<RenderWindow>();

    /** The viewer is running. */
    private boolean run = true;

    /** Use multi threading. */
    private boolean multithreading = false;

    private PipelineQueue pipelineQueue = new PipelineQueue();

    /**
     * Instantiates a new viewer.
     * 
     * @param enableMultithreading
     *            enable multithreading
     * @param windows
     *            the windows
     */
    public Viewer(boolean enableMultithreading, RenderWindow... windows) {
        multithreading = enableMultithreading;
        for (RenderWindow window : windows) {
            pipelineQueue.registerWindow(window);
            window.setPipelineQueue(pipelineQueue);
            this.windows.add(window);
            if (multithreading)
                window.start();
            else
                window.init();
        }
    }

    /**
     * Instantiates a new viewer without multithreading support.
     * 
     * @param windows
     *            the windows
     */
    public Viewer(RenderWindow... windows) {
        this(false, windows);
    }

    /**
     * Close all render windows.
     */
    public void close() {
        for (RenderWindow win : windows)
            win.close();
    }

    /**
     * Display next frame.
     * 
     * @throws Exception
     *             the exception
     */
    public void display() throws Exception {
        if (windows.size() == 0)
            throw new Exception("No windows defined.");

        if (multithreading)
            displayAsync();
        else
            displaySync();
    }

    /**
     * Render next frame with multithreading.
     */
    private void displayAsync() {
        // create new draw lists
        for (RenderWindow win : windows)
            if (win.isRunning())
                win.updatePipeline();

        pipelineQueue.waitForEmpty();

        // attach new draw lists
        run = false;
        for (RenderWindow win : windows)
            if (win.isRunning()) {
                win.enqueuePipeline();
                run = true;
            }

        pipelineQueue.commit();
    }

    /**
     * Render next frame in main thread.
     * 
     * @throws Exception
     */
    private void displaySync() throws Exception {
        // Benchmark.beginFrame();

        // create new draw lists
        for (RenderWindow win : windows)
            if (win.isRunning())
                win.updatePipeline();

        // attach new draw lists
        run = false;
        for (RenderWindow win : windows)
            if (win.isRunning()) {
                win.enqueuePipeline();
                run = true;
            }

        // render windows
        for (RenderWindow win : windows)
            if (win.isRunning())
                win.render(false);

        // swap all buffers
        for (RenderWindow win : windows)
            if (win.isRunning())
                win.swapBuffers();

        // Benchmark.endFrame();
    }

    /**
     * Checks if is running.
     * 
     * @return true, if is running
     */
    public boolean isRunning() {
        return run;
    }
}
