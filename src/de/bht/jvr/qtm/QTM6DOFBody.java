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

public class QTM6DOFBody implements QTMDataFrameComponentData {

    /** The x. */
    private double x = 0;

    /** The y. */
    private double y = 0;

    /** The z. */
    private double z = 0;

    /** The rotation. */
    private double[] rotation = new double[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 };

    /**
     * Gets the rotation.
     * 
     * @return the rotation
     */
    public double[] getRotation() {
        return rotation;
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
     * Sets the rotation.
     * 
     * @param rotation
     *            the new rotation
     */
    public void setRotation(double[] rotation) {
        if (rotation != null && rotation.length == 9)
            this.rotation = rotation;
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
        String r = "";
        for (int i = 0; i < 9; i++)
            r += " " + rotation[i];

        return "[X: " + x + " Y: " + y + " Z: " + z + " Rotation:" + r + "]";
    }
}
