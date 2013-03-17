package de.bht.jvr.renderer;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.DebugGL2GL3;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.awt.Screenshot;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyListener;
import de.bht.jvr.input.MouseListener;
import de.bht.jvr.logger.Log;

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
 * A abstract render window.
 * 
 * @author Marc Roßbach
 */
public abstract class RenderWindow extends Thread implements GLEventListener {
    /** The context object */
    protected Context ctx;

    /** The base pipeline. */
    protected Pipeline basePipeline;

    /** The update pipeline. */
    protected Pipeline updatePipeline;

    /** The render pipeline */
    protected Pipeline renderPipeline;

    /** The pipeline queue. */
    protected PipelineQueue pipelineQueue;;

    /** Thread running. */
    protected boolean run = true;

    /** Initialized. */
    protected boolean initialized = false;

    /** Vertical synchronization. */
    protected boolean vSync = false;

    /** The glprofile. */
    protected String glprofile = "GL2GL3";

    /** The screen device. */
    protected int screenDevice = 0;

    /** The width. */
    protected int width = 800;

    /** The height. */
    protected int height = 600;

    /** Fullscreen. */
    protected boolean fullscreen = false;

    /** Window undecorated. */
    protected boolean undecorated = false;

    /** Field sequential stereo mode. */
    protected boolean stereo = false;

    /** The window x position. */
    protected int posX = 0;

    /** The window y position. */
    protected int posY = 0;

    /** Antialiasing. */
    protected int fsaa = 0;

    /** GL-Debugging. */
    protected boolean glDebug = false;

    /** The key listener. */
    protected Set<KeyListener> keyListener = new HashSet<KeyListener>();

    /** The mouse listener. */
    protected Set<MouseListener> mouseListener = new HashSet<MouseListener>();

    /** The window listener. */
    protected Set<WindowListener> windowListener = new HashSet<WindowListener>();
    
    protected File screenshotFile = null;

    /**
     * Adds a key listener.
     * 
     * @param listener
     *            the listener
     */
    public void addKeyListener(KeyListener listener) {
        checkInit();
        keyListener.add(listener);
    }

    /**
     * Adds a mouse listener.
     * 
     * @param listener
     *            the listener
     */
    public void addMouseListener(MouseListener listener) {
        checkInit();
        mouseListener.add(listener);
    }

    /**
     * Adds a window listener.
     * 
     * @param listener
     *            the listener
     */
    public void addWindowListener(WindowListener listener) {
        checkInit();
        windowListener.add(listener);
    }

    /**
     * Check initialization.
     */
    protected void checkInit() {
        if (initialized)
            throw new RuntimeException("Window have already been initialized!");
    }

    /**
     * Close window.
     */
    protected abstract void close();

    /**
     * Draw the window.
     */
    protected abstract void display();

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable
     * )
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        try {
            if (renderPipeline != null)
                renderPipeline.render(ctx);

            if (screenshotFile != null) {
                Screenshot.writeToFile(screenshotFile, getWidth(), getHeight());
                screenshotFile = null;
            }

        } catch (Exception e) {
            Log.error(this.getClass(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable
     * )
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        if (windowListener != null)
            for (WindowListener listener : windowListener)
                listener.windowClose(this);
    }

    /**
     * Execute the pipeline.
     */
    protected void enqueuePipeline() {
        pipelineQueue.put(this, updatePipeline);
    }

    /**
     * Gets the GL capabilities.
     * 
     * @return the GL capabilities
     */
    protected GLCapabilities getGLCapabilities() {
        GLProfile glp = GLProfile.get(glprofile);
        Log.info(this.getClass(), "RUN " + Thread.currentThread());
        Log.info(this.getClass(), glp.toString());

        GLCapabilities caps = new GLCapabilities(glp);
        caps.setStencilBits(8);
        if (fsaa > 0) {
            caps.setSampleBuffers(true);
            caps.setNumSamples(fsaa);
        }
        caps.setStereo(stereo);

        return caps;
    }

    /**
     * Gets the height.
     * 
     * @return the height
     */
    protected abstract int getHeight();

    /**
     * Gets the width.
     * 
     * @return the width
     */
    protected abstract int getWidth();

    /**
     * Initialize the window.
     */
    protected abstract void init();

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable
     * )
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        if (glDebug)
            drawable.setGL(new DebugGL2GL3(drawable.getGL().getGL2GL3()));

        GL gl = drawable.getGL().getGL();
        ctx = new Context(gl);

        // OpenGL Render Settings
        gl.setSwapInterval(vSync ? 1 : 0);
    }

    /**
     * Checks if is running.
     * 
     * @return true, if is running
     */
    protected boolean isRunning() {
        return run;
    }

    /**
     * Render.
     * 
     * @param swapBuffers
     *            swap buffers before rendering
     * @throws Exception
     */
    protected void render(boolean swapBuffers) throws Exception {
        Pipeline p = pipelineQueue.get(this);

        if (swapBuffers) {
            pipelineQueue.waitForSwap();
            swapBuffers();
        }

        if (p != null) {
            renderPipeline = p;
            this.display();
        } else
            run = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable
     * , int, int, int, int)
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        if (windowListener != null)
            for (WindowListener listener : windowListener)
                listener.windowReshape(this, width, height);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        try {
            this.init();

            if (initialized)
                synchronized (this) {
                    notifyAll(); // Notify the viewer the window is created. So
                                 // the viewer can create the next window.
                                 // (workaround for jogl 2.0 >= b3)
                }

            while (run)
                render(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets antialiasing.
     * 
     * @param s
     *            samples
     */
    public void setFSAA(int s) {
        checkInit();
        fsaa = s;
    }

    /**
     * Sets fullscreen.
     * 
     * @param b
     *            fullscreen
     */
    public void setFullscreen(boolean b) {
        checkInit();
        fullscreen = b;
    }

    /**
     * Sets GL debugging.
     * 
     * @param b
     *            debug
     */
    public void setGLDebug(boolean b) {
        checkInit();
        glDebug = b;
    }

    /**
     * Sets the pipeline queue for communication with the viewer
     * 
     * @param q
     */
    protected void setPipelineQueue(PipelineQueue q) {
        pipelineQueue = q;
    }

    /**
     * Sets the window position.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void setPosition(int x, int y) {
        checkInit();
        posX = x;
        posY = y;
    }

    /**
     * Sets the screen device.
     * 
     * @param device
     *            the new screen device
     */
    public void setScreenDevice(int device) {
        checkInit();
        screenDevice = device;
    }

    public void setStereo(boolean stereo) {
        checkInit();
        this.stereo = stereo;
    }

    /**
     * Sets the window undecorated.
     * 
     * @param b
     *            undecorated
     */
    public void setUndecorated(boolean b) {
        checkInit();
        undecorated = b;
    }

    /**
     * Sets vertical synchronization.
     * 
     * @param b
     *            vertical synchronization
     */
    public void setVSync(boolean b) {
        checkInit();
        vSync = b;
    }

    /**
     * Sets the window title.
     * 
     * @param title
     */
    public abstract void setWindowTitle(String title);

    /**
     * Swap buffers.
     */
    protected abstract void swapBuffers();

    /**
     * Unregister from pipeline queue
     */
    protected void unregister() {
        pipelineQueue.unregisterWindow(this);
    }

    /**
     * Builds the pipeline.
     */
    protected void updatePipeline() {
        updatePipeline = basePipeline.getRenderClone();
        updatePipeline.prescan();
        updatePipeline.update();
    }

    public synchronized void waitForInit() throws InterruptedException {
        while (!initialized)
            wait();
    }
    
    /**
     * Safes a Screenshot of the next rendered frame to the given file.
     * 
     * @param file The file, the screenshot shall be safed.
     */
    public void TakeScreenshotOfNextFrame(File file) {
        this.screenshotFile = file;
    }
}
