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

public class QTMDataPacket {

    /**
     * The Enum PacketType.
     */
    public static enum PacketType {
        ERROR, CMD, XML, DATA_FRAME, NO_DATA, C3D_FILE
    }

    /** The type. */
    private PacketType type;

    /** The data. */
    private QTMMessage data = null;

    /**
     * Instantiates a new qTM data packet.
     */
    QTMDataPacket() {
        set(PacketType.NO_DATA, null);
    }

    /**
     * Instantiates a new qTM data packet.
     * 
     * @param message
     *            the message
     */
    QTMDataPacket(String message) {
        set(message);
    }

    /**
     * Gets the bytes.
     * 
     * @return the bytes
     */
    public byte[] getBytes() {
        byte[] erg;
        if (data != null) {
            byte[] dataBytes = data.getBytes();
            erg = new byte[dataBytes.length + 8];
            QTMUtility.copyBytes(dataBytes, 0, erg, 8);
        } else
            erg = new byte[8];

        // packet size
        QTMUtility.copyBytes(QTMUtility.intToBigEndianByteArray(erg.length), 0, erg, 0);
        // packet type
        QTMUtility.copyBytes(QTMUtility.intToBigEndianByteArray(type.ordinal()), 0, erg, 4);

        return erg;
    }

    /**
     * Gets the components.
     * 
     * @return the components
     */
    public Vector<QTMDataFrameComponent> getComponents() {
        if (data != null && data instanceof QTMDataFrame)
            return ((QTMDataFrame) data).getComponents();
        return null;
    }

    /**
     * Gets the message.
     * 
     * @return the message
     */
    public String getMessage() {
        if (data != null)
            return data.toString();
        return null;
    }

    /**
     * Gets the packet type.
     * 
     * @return the packet type
     */
    public PacketType getPacketType() {
        return type;
    }

    /**
     * Sets the packet type and data.
     * 
     * @param type
     *            the type
     * @param data
     *            the data
     */
    public void set(int type, byte[] data) {
        switch (type) {
        case 0:
            this.type = PacketType.ERROR;
            break;
        case 1:
            this.type = PacketType.CMD;
            break;
        case 2:
            this.type = PacketType.XML;
            break;
        case 3:
            this.type = PacketType.DATA_FRAME;
            break;
        case 4:
            this.type = PacketType.NO_DATA;
            break;
        case 5:
            this.type = PacketType.C3D_FILE;
            break;
        }

        set(this.type, data);
    }

    /**
     * Sets the packet type and data.
     * 
     * @param type
     *            the type
     * @param data
     *            the data
     */
    public void set(PacketType type, byte[] data) {
        this.type = type;
        if (this.type == PacketType.DATA_FRAME && data != null)
            this.data = new QTMDataFrame(data);
        else
            this.data = new QTMMessage(data);
    }

    /**
     * Sets the message.
     * 
     * @param message
     *            the message
     */
    public void set(String message) {
        set(PacketType.CMD, message.getBytes());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + type.toString() + "][" + data + "]";
    }
}
