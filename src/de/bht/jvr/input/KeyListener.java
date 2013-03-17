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
 * The listener interface for receiving key events. The class that is interested
 * in processing a key event implements this interface, and the object created
 * with that class is registered with a component using the component's
 * <code>addKeyListener</code> method. When the key event occurs, that object's
 * appropriate method is invoked.
 * 
 * @see KeyEvent
 * @author Marc Roßbach
 */
public interface KeyListener {
    /**
     * Key pressed.
     * 
     * @param e
     *            the key event
     */
    public void keyPressed(KeyEvent e);

    /**
     * Key released.
     * 
     * @param e
     *            the key event
     */
    public void keyReleased(KeyEvent e);

    /**
     * Key typed.
     * 
     * @param e
     *            the key event
     */
    public void keyTyped(KeyEvent e);
}
