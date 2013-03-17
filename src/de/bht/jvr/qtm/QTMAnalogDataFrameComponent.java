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

public class QTMAnalogDataFrameComponent extends QTMDataFrameComponent {
    /** The channel count. */
    private int channelCount = 0;

    /** The sub sample count. */
    private int subSampleCount = 0;

    /**
     * Instantiates a new qTM analog data frame component.
     * 
     * @param data
     *            the data
     */
    public QTMAnalogDataFrameComponent(byte[] data) {
        super(data);
        type = ComponentType.ANALOG;
        channelCount = QTMUtility.LittleEndianByteArrayToInt(data);
        subSampleCount = QTMUtility.LittleEndianByteArrayToInt(data, 4);

        for (int i = 0; i < channelCount * subSampleCount; i++) {
            int offset = i * 8 + 8;

            QTMAnalogSample v = new QTMAnalogSample();

            v.setVoltage(QTMUtility.LittleEndianByteArrayToDouble(data, offset));

            dataObjects.add(v);
        }
    }

    /**
     * Gets the channel count.
     * 
     * @return the channel count
     */
    public int getChannelCount() {
        return channelCount;
    }

    /**
     * Gets the sub sample count.
     * 
     * @return the sub sample count
     */
    public int getSubSampleCount() {
        return subSampleCount;
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
        return "[AnalogChannelCount: " + channelCount + " SubSampleCount: " + subSampleCount + "][Voltages: " + s + "]";
    }
}
