package de.bht.jvr.qtm;

import java.io.IOException;
import java.util.Vector;

import de.bht.jvr.math.Matrix4;

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

public class QTMRigidBodyTracking {
    /** The host. */
    private String host = "localhost";

    /** The port. */
    private int port = 22222;

    /** The scale. */
    private double scale = 0.001; // millimeter to meter

    /** The last matrices. */
    private Vector<Matrix4> lastMatrices = new Vector<Matrix4>();

    /** The qtm real time client. */
    QTMRTClient rtClient = new QTMRTClient();

    /**
     * Instantiates a new QTM rigid body tracking.
     */
    public QTMRigidBodyTracking() {
        init(host, port, scale);
    }

    /**
     * Instantiates a new QTM rigid body tracking.
     * 
     * @param host
     *            the host
     */
    public QTMRigidBodyTracking(String host) {
        init(host, port, scale);
    }

    /**
     * Instantiates a new QTM rigid body tracking.
     * 
     * @param host
     *            the host
     * @param port
     *            the port
     */
    public QTMRigidBodyTracking(String host, int port) {
        init(host, port, scale);
    }

    /**
     * Instantiates a new QTM rigid body tracking.
     * 
     * @param host
     *            the host
     * @param port
     *            the port
     * @param scale
     *            the scale
     */
    public QTMRigidBodyTracking(String host, int port, double scale) {
        init(host, port, scale);
    }

    /**
     * Connect to QTM Server.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void connect() throws IOException {
        connect(host, port);
    }

    /**
     * Connect to QTM Server.
     * 
     * @param host
     *            the host
     * @param port
     *            the port
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void connect(String host, int port) throws IOException {
        rtClient.connect(host, port);
        rtClient.start();
    }

    /**
     * Debug.
     * 
     * @param on
     *            the on
     */
    public void debug(boolean on) {
        if (rtClient != null)
            rtClient.debug(on);
    }

    /**
     * Disconnect from QTM Server.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void disconnect() throws IOException {
        rtClient.disconnect();
    }

    /**
     * Gets the transformation matrix of a rigid body.
     * 
     * @param index
     *            the index of the rigid body
     * @return the transformation matrix
     */
    public Matrix4 getRigidBodyMatrix(int index) {
        if (index < 0)
            return null;

        // create default matrices
        while (index >= lastMatrices.size()) {
            Matrix4 m = new Matrix4();
            lastMatrices.add(m);
        }

        QTM6DOFBody trans = getTransformation(index);

        if (trans != null) {
            // System.out.println("X: "+trans.getX());
            // System.out.println("Y: "+trans.getY());
            // System.out.println("Z: "+trans.getZ());
            // System.out.println();

            // return old matrix if new transformation is invalid
            if (((Double) trans.getX()).isNaN())
                return lastMatrices.get(index);

            double[] rotation = trans.getRotation();

            float[] data = new float[16];
            data[0] = (float) rotation[0];
            data[4] = (float) rotation[1];
            data[8] = (float) rotation[2];
            data[12] = 0;

            data[1] = (float) rotation[3];
            data[5] = (float) rotation[4];
            data[9] = (float) rotation[5];
            data[13] = 0;

            data[2] = (float) rotation[6];
            data[6] = (float) rotation[7];
            data[10] = (float) rotation[8];
            data[14] = 0;

            data[3] = (float) (trans.getX() * scale);
            data[7] = (float) (trans.getY() * scale);
            data[11] = (float) (trans.getZ() * scale);
            data[15] = 1;

            Matrix4 m = new Matrix4(data);

            // remember transformation
            lastMatrices.set(index, m);

            return m;
        } else
            return lastMatrices.get(index);
    }

    /**
     * Gets the transformation.
     * 
     * @param index
     *            the index of the rigid body
     * @return the transformation
     */
    private QTM6DOFBody getTransformation(int index) {
        if (isConnected()) {
            Vector<QTMDataFrameComponent> components = rtClient.getComponents();
            if (components != null)
                for (QTMDataFrameComponent comp : components) {
                    Vector<QTMDataFrameComponentData> dataObjects = comp.getDataObjects();
                    if (comp instanceof QTM6DOFDataFrameComponent && dataObjects.size() > 0 && index < dataObjects.size())
                        return (QTM6DOFBody) dataObjects.get(index);
                }
        }
        return null;
    }

    /**
     * Initializes the settings.
     * 
     * @param host
     *            the host
     * @param port
     *            the port
     * @param scale
     *            the scale
     */
    private void init(String host, int port, double scale) {
        this.host = host;
        this.port = port;
        this.scale = scale;
    }

    /**
     * Checks if is connected to QTM Server.
     * 
     * @return true, if is connected
     */
    public boolean isConnected() {
        return rtClient.isConnected();
    }
}
