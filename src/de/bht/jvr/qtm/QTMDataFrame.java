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

public class QTMDataFrame extends QTMMessage {

    /** The time stamp. */
    private long timeStamp = 0;

    /** The frame number. */
    private int frameNumber = 0;

    /** The component count. */
    private int componentCount = 0;

    /** The components. */
    Vector<QTMDataFrameComponent> components = new Vector<QTMDataFrameComponent>();

    /**
     * Instantiates a new qTM data frame.
     */
    public QTMDataFrame() {
        super.set(null);
        set(null);
    }

    /**
     * Instantiates a new qTM data frame.
     * 
     * @param data
     *            the data
     */
    public QTMDataFrame(byte[] data) {
        super.set(data);
        set(data);
    }

    /**
     * Gets the components.
     * 
     * @return the components
     */
    public Vector<QTMDataFrameComponent> getComponents() {
        return components;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.qtm.QTMMessage#set(byte[])
     */
    @Override
    public void set(byte[] data) {
        timeStamp = 0;
        frameNumber = 0;
        componentCount = 0;

        // header
        if (data != null && data.length > 15) // need 16 byte for a complete
                                              // frame header
        {
            timeStamp = QTMUtility.LittleEndianByteArrayToLong(data); // 64bit
            frameNumber = QTMUtility.LittleEndianByteArrayToInt(data, 8); // 32bit
            componentCount = QTMUtility.LittleEndianByteArrayToInt(data, 12); // 32bit
        }

        // components
        int totalComponentSize = 0;
        for (int i = 0; i < componentCount; i++) {
            int componentSize = QTMUtility.LittleEndianByteArrayToInt(data, 16 + totalComponentSize);
            int componentType = QTMUtility.LittleEndianByteArrayToInt(data, 16 + totalComponentSize + 4);

            byte[] componentData = new byte[componentSize - 8];
            QTMUtility.copyBytes(data, 16 + totalComponentSize + 8, componentData, 0);

            switch (componentType) {
            case 0:// ignore UNKNOWN
                break;
            case 1: // handle _3D
                components.add(new QTM3DDataFrameComponent(componentData));
                break;
            case 2: // handle _3D_NO_LABELS
                components.add(new QTM3DNoLabelsDataFrameComponent(componentData));
                break;
            case 3: // handle ANALOG
                components.add(new QTMAnalogDataFrameComponent(componentData));
                break;
            case 4: // handle FORCE
                components.add(new QTMForceDataFrameComponent(componentData));
                break;
            case 5: // handle 6D
                components.add(new QTM6DOFDataFrameComponent(componentData));
                break;
            case 6: // handle 6D EULER
                components.add(new QTM6DOFEulerDataFrameComponent(componentData));
                break;
            }

            totalComponentSize += componentSize;
        }
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.qtm.QTMMessage#toString()
     */
    @Override
    public String toString() {
        String c = "";
        for (QTMDataFrameComponent qtmDataFrameComponent : components)
            c += qtmDataFrameComponent.toString();

        return "[TimeStamp: " + timeStamp + "][Frame: " + frameNumber + "][ComponentCount: " + componentCount + "][Components: " + c + "]";
    }
}
