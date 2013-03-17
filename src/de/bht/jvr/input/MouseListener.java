package de.bht.jvr.input;

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
 * The listener interface for receiving mouse events. The class that is
 * interested in processing a mouse event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addMouseListener</code> method. When the mouse event
 * occurs, that object's appropriate method is invoked.
 * 
 * @see MouseEvent
 * @author Marc Roßbach
 */

public interface MouseListener {
    /**
     * Mouse clicked.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseClicked(MouseEvent e);

    /**
     * Mouse dragged.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseDragged(MouseEvent e);

    /**
     * Mouse entered.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseEntered(MouseEvent e);

    /**
     * Mouse exited.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseExited(MouseEvent e);

    /**
     * Mouse moved.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseMoved(MouseEvent e);

    /**
     * Mouse pressed.
     * 
     * @param e
     *            the mouse event
     */
    public void mousePressed(MouseEvent e);

    /**
     * Mouse released.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseReleased(MouseEvent e);

    /**
     * Mouse wheel moved.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseWheelMoved(MouseEvent e);
}
