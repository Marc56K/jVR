package de.bht.jvr.applicationbase;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.VRCameraNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.logger.Log;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector4;
import de.bht.jvr.qtm.QTMRigidBodyTracking;
import de.bht.jvr.renderer.AwtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.util.StopWatch;

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
 * 
 * 
 * The simple abstract holodeck application provides the binding to the qualisys
 * tracking system and manages windows and cameras.
 * 
 * @author Henrik Tramberend
 * @author Marc Roßbach
 */
public abstract class SimpleAbstractHolodeckApp {

    /** The head world transformation. */
    private Transform headWorldTrans = new Transform();

    /** The head transformation. */
    private Transform headTrans = new Transform();

    /** The wiimote world transformation. */
    private Transform wiimoteWorldTrans = new Transform();

    /** The root of the scene graph. */
    private GroupNode root = new GroupNode("root");

    /** The spectator. */
    private GroupNode holodeck = new GroupNode("spectator");

    protected RenderWindow frontLeftEyeWin;

    protected RenderWindow frontRightEyeWin;

    protected VRCameraNode frontRightEye;

    protected VRCameraNode frontLeftEye;

    protected SimpleAbstractHolodeckAppConfig config;

    public SimpleAbstractHolodeckApp(SimpleAbstractHolodeckAppConfig conf) throws Exception {
        config = conf;

        frontLeftEye = new VRCameraNode("cam_front_l", Transform.translate(0, 0, -1.17f), new Vector4(-1.865f, 1.865f, 1.165f, -1.165f), true, new Transform());

        frontRightEye = new VRCameraNode("cam_front_r", Transform.translate(0, 0, -1.17f), new Vector4(-1.865f, 1.865f, 1.165f, -1.165f), false, new Transform());

        frontLeftEyeWin = new AwtRenderWindow(getPipeline(frontLeftEye, root), config.fullscreen);
        if (config.fsaa > 0)
            frontLeftEyeWin.setFSAA(config.fsaa);
        frontLeftEyeWin.setScreenDevice(1);
        frontLeftEyeWin.setVSync(config.vsync);

        frontRightEyeWin = new AwtRenderWindow(getPipeline(frontRightEye, root), config.fullscreen);
        if (config.fsaa > 0)
            frontRightEyeWin.setFSAA(config.fsaa);
        frontRightEyeWin.setScreenDevice(0);
        frontRightEyeWin.setVSync(config.vsync);

        // create scene graph
        root.addChildNode(getSceneGraph());

        holodeck.addChildNode(frontLeftEye);

        holodeck.addChildNode(frontRightEye);

        root.addChildNode(holodeck);
    }

    /**
     * Gets the local head transform
     * 
     * @return head transformation in camera space
     */
    public Transform getHeadTransform() {
        return headTrans;
    }

    /**
     * Gets the head world transform.
     * 
     * @return head transformation in world space
     */
    public Transform getHeadWorldTransform() {
        return headWorldTrans;
    }

    /**
     * Derived classes need to return a fully configured rendering pipeline. Is
     * called 2*n-times (n = number of sides in the cave).
     * 
     * @param cam
     *            the camera
     * @param root
     *            the scene root
     * @return the pipeline
     * @throws Exception
     *             the exception
     */
    public abstract Pipeline getPipeline(VRCameraNode cam, SceneNode root) throws Exception;

    /**
     * Derived classes return the root node of the application scene graph
     * without cameras.
     * 
     * @return the scene graph
     * @throws Exception
     *             the exception
     */
    public abstract SceneNode getSceneGraph() throws Exception;

    /**
     * Gets the wiimote world transform.
     * 
     * @return wiimote transformation in world space
     */
    public Transform getWiimoteWorldTransform() {
        return wiimoteWorldTrans;
    }

    /**
     * Set the transformation of the Holodeck in world space. Use this to move
     * the Holodeck around in the scene. Just like a camera.
     * 
     * @param pos
     *            The new transformation.
     */
    public void setHolodeckTransform(Transform pos) {
        holodeck.setTransform(pos);
    }

    /**
     * Called once for every frame on the main thread.
     * 
     * @param elapsed
     *            rendering time for the last frame in seconds
     */
    public abstract void simulate(float elapsed);

    /**
     * Start the application. Connects to the tracking server and enters into
     * the main application loop.
     * 
     * @throws Exception
     *             the exception
     */
    public void start() throws Exception {

        // connect tracking system
        QTMRigidBodyTracking tracker = new QTMRigidBodyTracking();
        if (!config.holoSim)
            try {
                tracker.connect(config.trackerHost, 22222);
            } catch (Exception e) {
                Log.error(this.getClass(), "QTM: " + e.getMessage());
            }

        Viewer v = new Viewer(config.threading, frontLeftEyeWin, frontRightEyeWin);

        StopWatch clock = new StopWatch();
        while (v.isRunning()) {

            Transform headTransform = Transform.translate(0.0f, 1.8f, 0.0f);
            Transform wiimoteTransform = Transform.translate(-0.3f, 1.1f, -0.5f);

            if (tracker.isConnected()) {
                // Read the tracked positions.
                Matrix4 headTransM = tracker.getRigidBodyMatrix(config.trackerHeadId);
                headTransform = new Transform(headTransM);
                Matrix4 wiimoteTransM = tracker.getRigidBodyMatrix(config.trackerWiimoteId);
                wiimoteTransform = new Transform(wiimoteTransM);
            }

            // Set the eyes.
            frontLeftEye.setHeadTransform(headTransform);
            frontRightEye.setHeadTransform(headTransform);

            headWorldTrans = root.getTransform().mul(holodeck.getTransform()).mul(headTransform);
            headTrans = headTransform;

            wiimoteWorldTrans = root.getTransform().mul(holodeck.getTransform()).mul(wiimoteTransform);

            simulate(clock.elapsed());
            v.display();
        }
    }
}