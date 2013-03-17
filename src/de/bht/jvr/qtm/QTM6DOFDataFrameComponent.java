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

public class QTM6DOFDataFrameComponent extends QTMDataFrameComponent {
    /** The body count. */
    private int bodyCount = 0;

    /**
     * Instantiates a new 6dof data frame component.
     * 
     * @param data
     *            the data
     */
    public QTM6DOFDataFrameComponent(byte[] data) {
        super(data);
        type = ComponentType._6D;
        bodyCount = QTMUtility.LittleEndianByteArrayToInt(data);

        for (int i = 0; i < bodyCount; i++) {
            int offset = i * 96 + 8;

            QTM6DOFBody body = new QTM6DOFBody();

            body.setX(QTMUtility.LittleEndianByteArrayToDouble(data, offset));
            body.setY(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 8));
            body.setZ(QTMUtility.LittleEndianByteArrayToDouble(data, offset + 16));

            double[] rotation = body.getRotation();
            for (int j = 0; j < 9; j++)
                rotation[j] = QTMUtility.LittleEndianByteArrayToDouble(data, offset + 24 + j * 8);

            dataObjects.add(body);
        }
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.qtm.QTMDataFrameComponent#toString()
     */
    @Override
    public String toString() {
        String b = "";
        for (QTMDataFrameComponentData qtmDataFrameComponentData : dataObjects)
            b += qtmDataFrameComponentData.toString();
        return "[6DOFBodiesCount: " + bodyCount + "][6DOFBodies: " + b + "]";
    }
}
