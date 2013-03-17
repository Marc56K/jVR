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

public class QTM3DDataFrameComponent extends QTMDataFrameComponent {
    /** The marker count. */
    private int markerCount = 0;

    /**
     * Instantiates a new 3d data frame component.
     * 
     * @param data
     *            the data
     */
    public QTM3DDataFrameComponent(byte[] data) {
        super(data);
        type = ComponentType._3D;
        markerCount = QTMUtility.LittleEndianByteArrayToInt(data);

        for (int i = 0; i < markerCount; i++) {
            int offset = i * 24 + 8;

            QTM3DMarker marker = new QTM3DMarker();

            marker.setX(QTMUtility.LittleEndianByteArrayToDouble(data, offset));
            marker.setY(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 8));
            marker.setZ(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 16));

            dataObjects.add(marker);
        }
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.qtm.QTMDataFrameComponent#toString()
     */
    @Override
    public String toString() {
        String s = "";
        for (QTMDataFrameComponentData qtmDataFrameComponentData : dataObjects)
            s += qtmDataFrameComponentData.toString();
        return "[3DMarkerCount: " + markerCount + "][3DMarker: " + s + "]";
    }
}
