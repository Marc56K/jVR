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

public class QTMForcePlate implements QTMDataFrameComponentData {
    /** The force x. */
    private double fX = 0;

    /** The force y. */
    private double fY = 0;

    /** The force z. */
    private double fZ = 0;

    /** The m x. */
    private double mX = 0;

    /** The m y. */
    private double mY = 0;

    /** The m z. */
    private double mZ = 0;

    /** The x. */
    private double x = 0;

    /** The y. */
    private double y = 0;

    /** The z. */
    private double z = 0;

    /**
     * Gets the fX.
     * 
     * @return the fX
     */
    public double getFX() {
        return fX;
    }

    /**
     * Gets the fY.
     * 
     * @return the fY
     */
    public double getFY() {
        return fY;
    }

    /**
     * Gets the fZ.
     * 
     * @return the fZ
     */
    public double getFZ() {
        return fZ;
    }

    /**
     * Gets the mX.
     * 
     * @return the mX
     */
    public double getMX() {
        return mX;
    }

    /**
     * Gets the mY.
     * 
     * @return the mY
     */
    public double getMY() {
        return mY;
    }

    /**
     * Gets the mZ.
     * 
     * @return the mZ
     */
    public double getMZ() {
        return mZ;
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
     * Sets the fX.
     * 
     * @param fX
     *            the new fX
     */
    public void setFX(double fX) {
        this.fX = fX;
    }

    /**
     * Sets the fY.
     * 
     * @param fY
     *            the new fY
     */
    public void setFY(double fY) {
        this.fY = fY;
    }

    /**
     * Sets the fZ.
     * 
     * @param fZ
     *            the new fZ
     */
    public void setFZ(double fZ) {
        this.fZ = fZ;
    }

    /**
     * Sets the mX.
     * 
     * @param mX
     *            the new mX
     */
    public void setMX(double mX) {
        this.mX = mX;
    }

    /**
     * Sets the mY.
     * 
     * @param mY
     *            the new mY
     */
    public void setMY(double mY) {
        this.mY = mY;
    }

    /**
     * Sets the mZ.
     * 
     * @param mZ
     *            the new mZ
     */
    public void setMZ(double mZ) {
        this.mZ = mZ;
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
        return "[FX: " + fX + " FY: " + fY + " FZ: " + fZ + " MX: " + mX + " MY: " + mY + " MZ: " + mZ + " X: " + x + " Y: " + y + " Z: " + z + "]";
    }
}
