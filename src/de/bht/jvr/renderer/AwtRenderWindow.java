package de.bht.jvr.renderer;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.opengl.awt.GLCanvas;

import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.KeyListener;
import de.bht.jvr.input.MouseEvent;
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
 */

public class AwtRenderWindow extends RenderWindow implements WindowListener, java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener {

    /** The frame. */
    private Frame frame = null;

    /** The gl canvas. */
    private GLCanvas glCanvas = null;

    private String title = "";

    private GraphicsDevice graphicsDevice = null;

    boolean fullscreenCreated = false;

    /**
     * Instantiates a new awt render window.
     * 
     * @param pipeline
     *            the pipeline
     */
    public AwtRenderWindow(Pipeline pipeline) {
        basePipeline = pipeline;
    }

    /**
     * Instantiates a new awt render window.
     * 
     * @param pipeline
     *            the pipeline
     * @param fullscreen
     *            the fullscreen
     */
    public AwtRenderWindow(Pipeline pipeline, boolean fullscreen) {
        basePipeline = pipeline;
        this.fullscreen = fullscreen;
    }

    /**
     * Instantiates a new awt render window.
     * 
     * @param pipeline
     *            the pipeline
     * @param width
     *            the width
     * @param height
     *            the height
     */
    public AwtRenderWindow(Pipeline pipeline, int width, int height) {
        basePipeline = pipeline;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void close() {
        super.unregister();
        frame.dispose();
    }

    private void createFullscreen() {
        if (!fullscreenCreated) {
            frame.dispose();
            frame.setUndecorated(true);
            graphicsDevice.setFullScreenWindow(frame);
            frame.validate();
            fullscreenCreated = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.bht.jvr.renderer.RenderWindow#display()
     */
    @Override
    protected void display() {
        if (fullscreen)
            createFullscreen();
        glCanvas.display();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.bht.jvr.renderer.RenderWindow#getHeight()
     */
    @Override
    protected int getHeight() {
        return glCanvas.getHeight();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.bht.jvr.renderer.RenderWindow#getWidth()
     */
    @Override
    protected int getWidth() {
        return glCanvas.getWidth();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.bht.jvr.renderer.RenderWindow#init()
     */
    @Override
    protected void init() {
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        int device = 0;
        if (screenDevice >= 0 && screenDevice < devices.length)
            device = screenDevice;
        else
            Log.error(this.getClass(), "Invalid device (" + screenDevice + "): switching to default device.");

        graphicsDevice = devices[device];
        frame = new Frame(graphicsDevice.getDefaultConfiguration());
        frame.setTitle(title);
        frame.addWindowListener(this);
        if (!fullscreen)
            frame.setSize(width, height);

        if (undecorated)
            frame.setUndecorated(true);

        frame.setLocation(posX, posY);

        // Hide mouse cursor
        // Image image = Toolkit.getDefaultToolkit().createImage(new
        // MemoryImageSource(16, 16, new int[16 * 16], 0, 16));
        // Cursor transparentCursor
        // =Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0,
        // 0), "invisibleCursor");
        // frame.setCursor(transparentCursor);

        glCanvas = new GLCanvas(getGLCapabilities());
        glCanvas.setSize(width, height);
        glCanvas.setIgnoreRepaint(true);
        glCanvas.setAutoSwapBufferMode(false);
        glCanvas.addGLEventListener(this);
        // set event listener
        glCanvas.addKeyListener(this);
        glCanvas.addMouseListener(this);
        glCanvas.addMouseMotionListener(this);
        glCanvas.addMouseWheelListener(this);

        // frame.getContentPane().add(glCanvas);
        frame.add(glCanvas);
        frame.setVisible(true);

        glCanvas.setFocusable(true);

        super.initialized = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        for (KeyListener listener : keyListener)
            listener.keyPressed(new KeyEvent(e.getKeyCode(), (char) e.getKeyCode()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(java.awt.event.KeyEvent e) {
        for (KeyListener listener : keyListener)
            listener.keyReleased(new KeyEvent(e.getKeyCode(), (char) e.getKeyCode()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(java.awt.event.KeyEvent e) {
        for (KeyListener listener : keyListener)
            listener.keyTyped(new KeyEvent(Character.toUpperCase(e.getKeyChar()), Character.toUpperCase(e.getKeyChar())));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseClicked(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), 0, e.getButton()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
     * )
     */
    @Override
    public void mouseDragged(java.awt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseDragged(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), 0, e.getButton()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseEntered(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), 0, e.getButton()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseExited(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), 0, e.getButton()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseMoved(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), 0, e.getButton()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mousePressed(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), 0, e.getButton()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseReleased(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), 0, e.getButton()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.
     * MouseWheelEvent)
     */
    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
        for (MouseListener listener : mouseListener)
            listener.mouseWheelMoved(new MouseEvent(e.getX(), e.getY(), (float) e.getX() / getWidth(), (float) e.getY() / getHeight(), e.getWheelRotation(), e.getButton()));
    }

    @Override
    public void setWindowTitle(String title) {
        this.title = title;

        if (frame != null)
            frame.setTitle(title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.bht.jvr.renderer.RenderWindow#swapBuffers()
     */
    @Override
    protected void swapBuffers() {
        try {
            glCanvas.swapBuffers();
        } catch (Exception e) {}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    @Override
    public void windowActivated(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosed(WindowEvent arg0) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    @Override
    public void windowClosing(WindowEvent arg0) {
        close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent
     * )
     */
    @Override
    public void windowDeactivated(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent
     * )
     */
    @Override
    public void windowDeiconified(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    @Override
    public void windowIconified(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    @Override
    public void windowOpened(WindowEvent arg0) {
        // TODO Auto-generated method stub

    }
}
