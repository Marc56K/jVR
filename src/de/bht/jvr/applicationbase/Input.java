package de.bht.jvr.applicationbase;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.KeyListener;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.input.MouseListener;
import de.bht.jvr.math.Vector2;

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
 * This class represents the input state for the application. Can be queried for key and
 * button status and mouse position.
 */
public class Input implements KeyListener, MouseListener {
    private Set<Integer> keys = new HashSet<Integer>();
    private Set<Integer> buttons = new HashSet<Integer>();
    private Set<Integer> toggled = new HashSet<Integer>();
    private Point mpos = new Point();
    private int width, height;

    /**
     * Get the current position of the mouse in pixels. Position (0,0) is at the
     * upper left corner of the window.
     * 
     * @return The current mouse position.
     */
    public Point getMousePosition() {
        return mpos;
    }

    /**
     * Calculate the normalized mouse position within the window. Position (0,
     * 0) is at the center of the window. Coordinates are from the the interval
     * [-1, 1].
     * 
     * @return The normalized mouse position. Coordinate Z is always zero.
     */
    public Vector2 getNormalizedMousePosition() {
        float f = Math.max(width, height) / 2.0f;
        float cx = mpos.x - width / 2.0f;
        float cy = height - mpos.y - 1 - height / 2.0f;

        return new Vector2(cx / f, cy / f);
    }

    /**
     * Determine if the specified mouse button is currently pressed.
     * 
     * @param b
     *            The button code of as defined in class <code>MouseEvent<code>.
     * @return Returns true if the key is currently pressed.
     */
    public boolean isButtonDown(int b) {
        return buttons.contains(b);
    }

    /**
     * Determine if the specified key is currently pressed.
     * 
     * @param k
     *            The code of the key as defined in class <code>KeyEvent<code>.
     * @return Returns true if the key is currently pressed.
     */
    public boolean isKeyDown(int k) {
        return keys.contains(k);
    }

    /**
     * Determine the toggle state of the specified key.
     * 
     * @param k
     *            The code of the key as defined in class <code>KeyEvent<code>.
     * @return Returns true if the key is currently pressed.
     */
    public boolean isKeyToggled(int k) {
        return toggled.contains(k);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        keys.add(k);
        if (toggled.contains(k))
            toggled.remove(k);
        else
            toggled.add(k);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        mpos = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mpos = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mpos = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mpos = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mpos = new Point(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int b = e.getButton();
        buttons.add(b);
        mpos = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int b = e.getButton();
        buttons.remove(b);
        mpos = new Point(e.getX(), e.getY());
    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {}

    void setWindowSize(int w, int h) {
        width = w;
        height = h;
    }
}