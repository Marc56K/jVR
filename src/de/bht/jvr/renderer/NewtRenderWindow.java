package de.bht.jvr.renderer;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;

import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.KeyListener;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.input.MouseListener;

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

public class NewtRenderWindow extends RenderWindow implements WindowListener, com.jogamp.newt.event.KeyListener, com.jogamp.newt.event.MouseListener {
    private static KeyEvent createKeyEvent(com.jogamp.newt.event.KeyEvent e) {
        char chr;

        if (e.getKeyCode() < 0)
            chr = Character.toUpperCase(e.getKeyChar());
        else
            chr = Character.toUpperCase((char) e.getKeyCode());

        return new KeyEvent(chr, chr);
    }

    /** The window. */
    private GLWindow window = null;

    /** The perf log. */
    private boolean perfLog = true;

    private String title = "";

    /**
     * Instantiates a new newt render window.
     * 
     * @param pipeline
     *            the pipeline
     */
    public NewtRenderWindow(Pipeline pipeline) {
        basePipeline = pipeline;
    }

    /**
     * Instantiates a new newt render window.
     * 
     * @param pipeline
     *            the pipeline
     * @param fullscreen
     *            the fullscreen
     */
    public NewtRenderWindow(Pipeline pipeline, boolean fullscreen) {
        basePipeline = pipeline;
        this.fullscreen = fullscreen;
    }

    /**
     * Instantiates a new newt render window.
     * 
     * @param pipeline
     *            the pipeline
     * @param width
     *            the width
     * @param height
     *            the height
     */
    public NewtRenderWindow(Pipeline pipeline, int width, int height) {
        basePipeline = pipeline;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void close() {
        super.unregister();
        window.destroy();
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.renderer.RenderWindow#display()
     */
    @Override
    protected void display() {
        if (window != null)
            window.display();
    }

    /**
     * Enable performance logging.
     * 
     * @param enable
     *            the enable
     */
    public void enablePerformanceLog(boolean enable) {
        checkInit();
        perfLog = enable;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.renderer.RenderWindow#getHeight()
     */
    @Override
    protected int getHeight() {
        return window.getHeight();
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.renderer.RenderWindow#getWidth()
     */
    @Override
    protected int getWidth() {
        return window.getWidth();
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.renderer.RenderWindow#init()
     */
    @Override
    protected void init() {
        // create window and attach renderer
        Display display = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(display, screenDevice);
        Window win = NewtFactory.createWindow(screen, getGLCapabilities());
        window = GLWindow.create(win);
        window.setTitle(title);
        window.setPosition(posX, posY);
        window.addGLEventListener(this);
        window.setVisible(true);

        // window settings
        window.setVisible(true);
        window.setSize(width, height);
        window.enablePerfLog(perfLog);
        window.setFullscreen(fullscreen);
        window.setUndecorated(undecorated);
        window.addWindowListener(this);
        window.setAutoSwapBufferMode(false);

        // set event listener
        window.addKeyListener(this);
        window.addMouseListener(this);

        super.initialized = true;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.KeyListener#keyPressed(com.jogamp.newt.event.KeyEvent
     * )
     */
    @Override
    public void keyPressed(com.jogamp.newt.event.KeyEvent e) {
        for (KeyListener listener : keyListener)
            listener.keyPressed(createKeyEvent(e));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.KeyListener#keyReleased(com.jogamp.newt.event.KeyEvent
     * )
     */
    @Override
    public void keyReleased(com.jogamp.newt.event.KeyEvent e) {
        for (KeyListener listener : keyListener)
            listener.keyReleased(createKeyEvent(e));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.KeyListener#keyTyped(com.jogamp.newt.event.KeyEvent
     * )
     */
    @Override
    public void keyTyped(com.jogamp.newt.event.KeyEvent e) {
        for (KeyListener listener : keyListener)
            listener.keyTyped(createKeyEvent(e));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.MouseListener#mouseClicked(com.jogamp.newt.event
     * .MouseEvent)
     */
    @Override
    public void mouseClicked(com.jogamp.newt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseClicked(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), e.getWheelRotation(), e.getButton()));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.MouseListener#mouseDragged(com.jogamp.newt.event
     * .MouseEvent)
     */
    @Override
    public void mouseDragged(com.jogamp.newt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseDragged(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), e.getWheelRotation(), e.getButton()));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.MouseListener#mouseEntered(com.jogamp.newt.event
     * .MouseEvent)
     */
    @Override
    public void mouseEntered(com.jogamp.newt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseEntered(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), e.getWheelRotation(), e.getButton()));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.MouseListener#mouseExited(com.jogamp.newt.event
     * .MouseEvent)
     */
    @Override
    public void mouseExited(com.jogamp.newt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseExited(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), e.getWheelRotation(), e.getButton()));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.MouseListener#mouseMoved(com.jogamp.newt.event.
     * MouseEvent)
     */
    @Override
    public void mouseMoved(com.jogamp.newt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseMoved(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), e.getWheelRotation(), e.getButton()));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.MouseListener#mousePressed(com.jogamp.newt.event
     * .MouseEvent)
     */
    @Override
    public void mousePressed(com.jogamp.newt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mousePressed(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), e.getWheelRotation(), e.getButton()));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.MouseListener#mouseReleased(com.jogamp.newt.event
     * .MouseEvent)
     */
    @Override
    public void mouseReleased(com.jogamp.newt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseReleased(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), e.getWheelRotation(), e.getButton()));
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.MouseListener#mouseWheelMoved(com.jogamp.newt.event
     * .MouseEvent)
     */
    @Override
    public void mouseWheelMoved(com.jogamp.newt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseWheelMoved(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), e.getWheelRotation(), e.getButton()));
    }

    @Override
    public void setWindowTitle(String title) {
        this.title = title;

        if (window != null)
            window.setTitle(title);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.renderer.RenderWindow#swapBuffers()
     */
    @Override
    protected void swapBuffers() {
        if (window != null)
            try {
                window.swapBuffers();
            } catch (Exception e) {}
    }

    @Override
    public void windowDestroyed(WindowEvent arg0) {}

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.WindowListener#windowDestroyNotify(com.jogamp.newt
     * .event.WindowEvent)
     */
    @Override
    public void windowDestroyNotify(WindowEvent arg0) {
        super.unregister();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.WindowListener#windowGainedFocus(com.jogamp.newt
     * .event.WindowEvent)
     */
    @Override
    public void windowGainedFocus(WindowEvent arg0) {}

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.WindowListener#windowLostFocus(com.jogamp.newt.
     * event.WindowEvent)
     */
    @Override
    public void windowLostFocus(WindowEvent arg0) {}

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.WindowListener#windowMoved(com.jogamp.newt.event
     * .WindowEvent)
     */
    @Override
    public void windowMoved(WindowEvent arg0) {}

    @Override
    public void windowRepaint(WindowUpdateEvent arg0) {}

    /*
     * (non-Javadoc)
     * @see
     * com.jogamp.newt.event.WindowListener#windowResized(com.jogamp.newt.event
     * .WindowEvent)
     */
    @Override
    public void windowResized(WindowEvent arg0) {}
}
