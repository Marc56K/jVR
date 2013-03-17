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

public class QTMForceDataFrameComponent extends QTMDataFrameComponent {
    /** The plate count. */
    private int plateCount = 0;

    /**
     * Instantiates a new qTM force data frame component.
     * 
     * @param data
     *            the data
     */
    public QTMForceDataFrameComponent(byte[] data) {
        super(data);
        type = ComponentType.FORCE;
        plateCount = QTMUtility.LittleEndianByteArrayToInt(data);

        for (int i = 0; i < plateCount; i++) {
            int offset = i * 72 + 8;

            QTMForcePlate fp = new QTMForcePlate();

            fp.setFX(QTMUtility.LittleEndianByteArrayToDouble(data, offset));
            fp.setFY(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 8));
            fp.setFZ(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 16));
            fp.setMX(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 24));
            fp.setMY(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 32));
            fp.setMZ(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 40));
            fp.setX(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 48));
            fp.setY(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 56));
            fp.setZ(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 64));

            dataObjects.add(fp);
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
        return "[ForcePlateCount: " + plateCount + "][ForcePlates: " + s + "]";
    }
}
