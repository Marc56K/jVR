package de.bht.jvr.qtm;

import java.util.Vector;

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

public class QTMDataFrameComponent {
    /**
     * The Enum ComponentType.
     */
    public static enum ComponentType {
        UNKNOWN, _3D, _3D_NO_LABELS, ANALOG, FORCE, _6D, _6D_EULER
    }

    /** The data. */
    private byte[] data = null;

    /** The type. */
    protected ComponentType type = ComponentType.UNKNOWN;

    /** The data objects. */
    protected Vector<QTMDataFrameComponentData> dataObjects = new Vector<QTMDataFrameComponentData>();

    /**
     * Instantiates a new qTM data frame component.
     * 
     * @param data
     *            the data
     */
    public QTMDataFrameComponent(byte[] data) {
        this.data = data;
    }

    /**
     * Gets the bytes.
     * 
     * @return the bytes
     */
    public byte[] getBytes() {
        return data;
    }

    /**
     * Gets the component type.
     * 
     * @return the component type
     */
    public ComponentType getComponentType() {
        return type;
    }

    /**
     * Gets the data objects.
     * 
     * @return the data objects
     */
    public Vector<QTMDataFrameComponentData> getDataObjects() {
        return dataObjects;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new String(data);
    }
}
