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

public class QTMMessage {
    /** The data. */
    private byte[] data = null;

    /**
     * Instantiates a new qTM message.
     */
    public QTMMessage() {
        data = null;
    }

    /**
     * Instantiates a new qTM message.
     * 
     * @param message
     *            the message
     */
    public QTMMessage(byte[] message) {
        set(message);
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
     * Sets the message.
     * 
     * @param message
     *            the message
     */
    public void set(byte[] message) {
        data = message;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (data == null)
            return null;
        else
            return new String(data);
    }
}
