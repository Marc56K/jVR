package de.bht.jvr.qtm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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

public class QTMRTClient extends Thread {
    /**
     * Example application using the QTMRTClient.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        QTMRTClient rtClient = new QTMRTClient();
        rtClient.debug(false);
        try {
            System.out.println(rtClient.connect("localhost", 22222, "6D"));
            rtClient.start();

            for (int i = 0; i < 900000; i++)
                System.out.println(">>" + rtClient.getDataPacket());
            rtClient.disconnect();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    /** The connected. */
    private boolean connected = false;

    /** The socket. */
    private Socket socket = null;

    /** The out. */
    private OutputStream out = null;

    /** The in. */
    private InputStream in = null;

    /** The debug. */
    private boolean debug = false;

    /** The data packet. */
    private QTMDataPacket dataPacket = new QTMDataPacket();

    /** The parameters. */
    private String parameters = "";

    /**
     * Connect to QTM-Server.
     * 
     * @param host
     *            the host
     * @param port
     *            the port
     * @return The settings for the requested component(s) of QTM in XML format.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public String connect(String host, int port) throws IOException {
        return connect(host, port, "6D");
    }

    /**
     * Connect to QTM-Server.
     * 
     * @param host
     *            the host
     * @param port
     *            the port
     * @param streamParameters
     *            All, 3D, 3DnoLabels, Analog, Force, 6D, 6DEuler
     * @return The settings for the requested component(s) of QTM in XML format.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public String connect(String host, int port, String streamParameters) throws IOException {
        parameters = "";
        connected = false;
        socket = new Socket(host, port); // connect
        socket.setTcpNoDelay(true); // disable nagle's algorithm for better
                                    // performance

        if (socket.isConnected()) {
            out = socket.getOutputStream();
            in = socket.getInputStream();
            QTMDataPacket p = new QTMDataPacket();

            // get welcome message from server
            receivePacket();

            // set protocol
            p.set("Version 1.0");
            sendPacket(p);
            receivePacket();

            // get parameters
            p.set("SendParameters All");
            sendPacket(p);
            String msg = receivePacket().getMessage();
            if (msg != null)
                parameters = msg;

            // set byte order
            p.set("ByteOrder LittleEndian");
            sendPacket(p);
            receivePacket();

            // set stream parameters
            p.set("StreamFrames AllFrames " + streamParameters);
            sendPacket(p);

            connected = true;
        }

        return parameters;
    }

    /**
     * Debug.
     * 
     * @param on
     *            the on
     */
    public void debug(boolean on) {
        debug = on;
    }

    /**
     * Disconnect from QTM-Server.
     */
    public void disconnect() {
        try {
            connected = false;
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    /**
     * Get the components of the last received QTMDataPacket.
     * 
     * @return the components
     */
    public Vector<QTMDataFrameComponent> getComponents() {
        synchronized (dataPacket) {
            if (dataPacket != null)
                return dataPacket.getComponents();
        }

        return null;
    }

    /**
     * Get last received QTMDataPacket.
     * 
     * @return the data packet
     */
    public QTMDataPacket getDataPacket() {
        return dataPacket;
    }

    /**
     * Checks if is connected.
     * 
     * @return true, if is connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Receives QTM-Packet.
     * 
     * @return Received QTM-Packet
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private QTMDataPacket receivePacket() throws IOException {
        if (socket.isConnected()) {
            byte[] buffer = new byte[4];

            // read size
            in.read(buffer);
            int size = QTMUtility.BigEndianByteArrayToInt(buffer);

            // read type
            in.read(buffer);
            int type = QTMUtility.BigEndianByteArrayToInt(buffer);

            buffer = new byte[size - 8];
            in.read(buffer);

            QTMDataPacket newPacket = new QTMDataPacket();
            newPacket.set(type, buffer);

            if (debug)
                System.out.println("IN: " + newPacket.toString());

            return newPacket;
        } else
            return null;
    }

    /**
     * Tracking loop.
     */
    @Override
    public void run() {
        while (connected)
            try {
                QTMDataPacket newPacket = receivePacket();
                if (newPacket != null)
                    dataPacket = newPacket;
            } catch (IOException e) {
                connected = false;
                System.err.println(e);
            }
    }

    /**
     * Send QTM-Packet.
     * 
     * @param p
     *            QTM-Packet
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void sendPacket(QTMDataPacket p) throws IOException {
        if (socket.isConnected()) {
            out.write(p.getBytes());
            out.flush();

            if (debug)
                System.out.println("OUT: " + p.toString());
        }
    }

}
