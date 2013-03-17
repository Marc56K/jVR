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

public class MouseEvent {
    /** The x position. */
    private int x;

    /** The y position. */
    private int y;

    /** The normalized x position. */
    private float nx;

    /** The normalized y position. */
    private float ny;

    /** The wheel rotation. */
    private int wheelRotation;

    /** The pressed button. */
    private int button;

    /**
     * Instantiates a new mouse event.
     * 
     * @param x
     *            the x position
     * @param y
     *            the y position
     * @param nx
     *            the normalized x position
     * @param ny
     *            the normalized y position
     * @param wheelRotation
     *            the wheel rotation
     * @param button
     *            the button
     */
    public MouseEvent(int x, int y, float nx, float ny, int wheelRotation, int button) {
        this.x = x;
        this.y = y;
        this.nx = nx;
        this.ny = ny;
        this.wheelRotation = wheelRotation;
        this.button = button;
    }

    /**
     * Gets the pressed button.
     * 
     * @return the pressed button
     */
    public int getButton() {
        return button;
    }

    /**
     * Gets the normalized x position.
     * 
     * @return the normalized x position
     */
    public float getNormalizedX() {
        return nx;
    }

    /**
     * Gets the normalized y position.
     * 
     * @return the normalized y position
     */
    public float getNormalizedY() {
        return ny;
    }

    /**
     * Gets the wheel rotation.
     * 
     * @return the wheel rotation
     */
    public int getWheelRotation() {
        return wheelRotation;
    }

    /**
     * Gets the x position.
     * 
     * @return the x position
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y position.
     * 
     * @return the y position
     */
    public int getY() {
        return y;
    }
}
