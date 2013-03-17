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

public class QTM6DOFEulerBody implements QTMDataFrameComponentData {
    /** The x. */
    private double x = 0;

    /** The y. */
    private double y = 0;

    /** The z. */
    private double z = 0;

    /** The angle1. */
    private double angle1 = 0;

    /** The angle2. */
    private double angle2 = 0;

    /** The angle3. */
    private double angle3 = 0;

    /**
     * Gets the angle1.
     * 
     * @return the angle1
     */
    public double getAngle1() {
        return angle1;
    }

    /**
     * Gets the angle2.
     * 
     * @return the angle2
     */
    public double getAngle2() {
        return angle2;
    }

    /**
     * Gets the angle3.
     * 
     * @return the angle3
     */
    public double getAngle3() {
        return angle3;
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
     * Sets the angle1.
     * 
     * @param angle1
     *            the new angle1
     */
    public void setAngle1(double angle1) {
        this.angle1 = angle1;
    }

    /**
     * Sets the angle2.
     * 
     * @param angle2
     *            the new angle2
     */
    public void setAngle2(double angle2) {
        this.angle2 = angle2;
    }

    /**
     * Sets the angle3.
     * 
     * @param angle3
     *            the new angle3
     */
    public void setAngle3(double angle3) {
        this.angle3 = angle3;
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
        return "[X: " + x + " Y: " + y + " Z: " + z + " Angle1: " + angle1 + " Angle2: " + angle2 + " Angle3: " + angle3 + "]";
    }
}
