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

public class QTMAnalogSample implements QTMDataFrameComponentData {

    /** The v. */
    private double v = 0;

    /**
     * Gets the voltage.
     * 
     * @return the voltage
     */
    public double getVoltage() {
        return v;
    }

    /**
     * Sets the voltage.
     * 
     * @param v
     *            the new voltage
     */
    public void setVoltage(double v) {
        this.v = v;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + v + "V]";
    }
}
