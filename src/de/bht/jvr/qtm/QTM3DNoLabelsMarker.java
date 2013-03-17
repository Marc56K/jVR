package de.bht.jvr.qtm;

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

public class QTM3DNoLabelsMarker implements QTMDataFrameComponentData {
    /** The x. */
    private double x = 0;

    /** The y. */
    private double y = 0;

    /** The z. */
    private double z = 0;

    /** The id. */
    private int id = 0;

    /**
     * An ID that serves to identify markers between frames.
     * 
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the x.
     * 
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y.
     * 
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the z.
     * 
     * @return the z
     */
    public double getZ() {
        return z;
    }

    /**
     * An ID that serves to identify markers between frames.
     * 
     * @param id
     *            the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the x.
     * 
     * @param x
     *            the new x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the y.
     * 
     * @param y
     *            the new y
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Sets the z.
     * 
     * @param z
     *            the new z
     */
    public void setZ(double z) {
        this.z = z;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[X: " + x + " Y: " + y + " Z: " + z + " ID: " + id + "]";
    }
}
