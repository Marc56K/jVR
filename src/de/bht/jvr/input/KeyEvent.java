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
 */

public class KeyEvent {
    /** The key code. */
    private int keyCode;

    /** The key char. */
    private char keyChar;

    /**
     * Instantiates a new key event.
     * 
     * @param keyCode
     *            the key code
     * @param keyChar
     *            the key char
     */
    public KeyEvent(int keyCode, char keyChar) {
        this.keyChar = keyChar;
        this.keyCode = keyCode;
    }

    /**
     * Gets the key char.
     * 
     * @return the key char
     */
    public char getKeyChar() {
        return keyChar;
    }

    /**
     * Gets the key code.
     * 
     * @return the key code
     */
    public int getKeyCode() {
        return keyCode;
    }
}
