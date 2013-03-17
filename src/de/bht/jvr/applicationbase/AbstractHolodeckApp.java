package de.bht.jvr.applicationbase;

import java.util.List;

import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.JoystickEvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.NunchukButtonsEvent;
import wiiusej.wiiusejevents.physicalevents.NunchukEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.DisconnectionEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.StatusEvent;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.VRCameraNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.KeyListener;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.input.MouseListener;
import de.bht.jvr.logger.Log;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.math.Vector4;
import de.bht.jvr.qtm.QTMRigidBodyTracking;
import de.bht.jvr.renderer.NewtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;

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
 * The abstract holodeck application provides the wiimote+nunchuk navigation and
 * the binding to the qualisys tracking system.
 * 
 * @author Marc Roßbach
 */
public abstract class AbstractHolodeckApp implements WiimoteListener, KeyListener, MouseListener {
    /** The QTM-Server. */
    private String trackerHost = "localhost";

    /** The QTM-Id of the rigid body which represents the head. */
    private int trackerHeadId = 0;

    /** The QTM-Id of the rigid body which represents the wiimote. */
    private int trackerWiimoteId = 1;

    /** The head world transformation. */
    private Transform headWorldTrans = new Transform();

    /** The head transformation. */
    private Transform headTrans = new Transform();

    /** The wiimote world transformation. */
    private Transform wiimoteWorldTrans = new Transform();

    /** The move speed. */
    private double moveSpeed = 10;

    /** Is fullscreen. */
    private boolean fullscreen = true;

    /** The root of the scene graph. */
    private GroupNode root = new GroupNode("root");

    /** The spectator. */
    private GroupNode spectator = new GroupNode("spectator");

    /** multithreading */
    private boolean threading = false;

    /** antialiasing */
    private int fsaa = 0;

    /** vertical synchronisation */
    private boolean vsync = false;

    /** The nunchuk joy x. */
    private float nunchukJoyX = 0;

    /** The nunchuk joy y. */
    private float nunchukJoyY = 0;

    /** The nunchuk pitch. */
    private float nunchukPitch = 0;

    /** The nunchuk roll. */
    private float nunchukRoll = 0;

    /** The nunchuk button z. */
    private boolean nunchukButtonZ = false;

    /** The spectator rot x. */
    private float spectatorRotX = 0; // rotation of the spectator (x-axis)

    /** The spectator rot y. */
    private float spectatorRotY = 0; // rotation of the spectator (y-axis)

    /**
     * Connect wiimote.
     */
    private void connectWiimote() {
        // Connect Wiimote
        Log.info(this.getClass(), "Put Wiimote in discoverable mode (press 1+2)");
        Wiimote[] wiimotes = WiiUseApiManager.getWiimotes(1, false);
        if (WiiUseApiManager.getNbConnectedWiimotes() < 1)
            Log.error(this.getClass(), "Timeout - No Wiimotes found.");
        else {
            Log.info(this.getClass(), "Wiimote connected.");
            wiimotes[0].addWiiMoteEventListeners(this);
            wiimotes[0].activateMotionSensing();
        }
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
     * Gets the movement speed of the spectator.
     * 
     * @return the move speed
     */
    public double getMoveSpeed() {
        return moveSpeed;
    }

    /**
     * Is called 2*n-times (n = number of sides in the cave).
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
     * The scene graph without cameras.
     * 
     * @return the scene graph
     * @throws Exception
     *             the exception
     */
    public abstract SceneNode getSceneGraph() throws Exception;

    /**
     * Returns the group node containing all cameras.
     * 
     * @return the spectator group node
     */
    public GroupNode getSpectator() {
        return spectator;
    }

    /**
     * Gets the wiimote world transform.
     * 
     * @return wiimote transformation in world space
     */
    public Transform getWiimoteWorldTransform() {
        return wiimoteWorldTrans;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.input.KeyListener#keyPressed(de.bht.jvr.input.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (onKeyPressed(e))
            return;

        if (Character.toUpperCase((char) e.getKeyCode()) == 'Q')
            System.exit(0); // exit application
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.input.KeyListener#keyReleased(de.bht.jvr.input.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
        onKeyReleased(e);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.input.KeyListener#keyTyped(de.bht.jvr.input.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e) {
        onKeyTyped(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * de.bht.jvr.input.MouseListener#mouseClicked(de.bht.jvr.input.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        onMouseClicked(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * de.bht.jvr.input.MouseListener#mouseDragged(de.bht.jvr.input.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        onMouseDragged(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * de.bht.jvr.input.MouseListener#mouseEntered(de.bht.jvr.input.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        onMouseEntered(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * de.bht.jvr.input.MouseListener#mouseExited(de.bht.jvr.input.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        onMouseExited(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * de.bht.jvr.input.MouseListener#mouseMoved(de.bht.jvr.input.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        onMouseMoved(e);

    }

    /*
     * (non-Javadoc)
     * @see
     * de.bht.jvr.input.MouseListener#mousePressed(de.bht.jvr.input.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        onMousePressed(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * de.bht.jvr.input.MouseListener#mouseReleased(de.bht.jvr.input.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        onMouseReleased(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * de.bht.jvr.input.MouseListener#mouseWheelMoved(de.bht.jvr.input.MouseEvent
     * )
     */
    @Override
    public void mouseWheelMoved(MouseEvent e) {
        onMouseWheelMoved(e);
    }

    /**
     * Move the spectator.
     * 
     * @param renderDuration
     *            rendering duration for the last frame in seconds
     */
    public void move(double renderDuration) {
        float deadZone = 0.1f;
        float joyX = (float) (nunchukJoyX / 2.0);
        float joyY = (float) (nunchukJoyY / 2.0);

        double d = renderDuration * moveSpeed;

        if (nunchukButtonZ) // Press Nunchuk-Button-Z to unlock
        {
            spectatorRotX -= nunchukPitch * d;
            spectatorRotY -= nunchukRoll * d;

            if (spectatorRotX < -Math.PI / 2)
                spectatorRotX = (float) (-Math.PI / 2); // minimum x-rotation
                                                        // -90ß
            if (spectatorRotX > Math.PI / 2)
                spectatorRotX = (float) (Math.PI / 2); // maximum x-rotation 90ß
        }

        float[] pos = spectator.getTransform().getMatrix().translation().getData();

        if (joyY < -deadZone) // Move forward
        {
            pos[0] += joyY * Math.sin(spectatorRotY) * Math.cos(-spectatorRotX) * d;
            pos[1] += joyY * Math.sin(-spectatorRotX) * d;
            pos[2] += joyY * Math.cos(spectatorRotY) * Math.cos(-spectatorRotX) * d;
        }
        if (joyY > deadZone) // Move backward
        {
            pos[0] += joyY * Math.sin(spectatorRotY) * Math.cos(-spectatorRotX) * d;
            pos[1] += joyY * Math.sin(-spectatorRotX) * d;
            pos[2] += joyY * Math.cos(spectatorRotY) * Math.cos(-spectatorRotX) * d;
        }
        if (joyX < -deadZone) // Strafe left
        {
            pos[0] -= joyX * Math.sin(spectatorRotY - Math.PI / 2) * d;
            pos[2] -= joyX * Math.cos(spectatorRotY - Math.PI / 2) * d;
        }
        if (joyX > deadZone) // Strafe right
        {
            pos[0] += joyX * Math.sin(spectatorRotY + Math.PI / 2) * d;
            pos[2] += joyX * Math.cos(spectatorRotY + Math.PI / 2) * d;
        }

        // set the new spectator transformation (Transformation = Translation *
        // RotationY * RotationX)
        spectator.setTransform(Transform.translate(new Vector3(pos)).mul(Transform.rotateY(spectatorRotY)).mul(Transform.rotateX(spectatorRotX)));
    }

    /*
     * (non-Javadoc)
     * @see wiiusej.wiiusejevents.utils.WiimoteListener#onButtonsEvent(wiiusej.
     * wiiusejevents.physicalevents.WiimoteButtonsEvent)
     */
    @Override
    public void onButtonsEvent(WiimoteButtonsEvent e) {
        onWiiButtonsEvent(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onClassicControllerInsertedEvent
     * (wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent)
     */
    @Override
    public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent e) {
        onWiiClassicControllerInsertedEvent(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onClassicControllerRemovedEvent
     * (wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent)
     */
    @Override
    public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent e) {
        onWiiClassicControllerRemovedEvent(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onDisconnectionEvent(wiiusej
     * .wiiusejevents.wiiuseapievents.DisconnectionEvent)
     */
    @Override
    public void onDisconnectionEvent(DisconnectionEvent e) {
        onWiiDisconnectionEvent(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onExpansionEvent(wiiusej.
     * wiiusejevents.physicalevents.ExpansionEvent)
     */
    @Override
    public void onExpansionEvent(ExpansionEvent e) {
        if (onWiiExpansionEvent(e))
            return; // discard the event

        // Handle Nunchuk-Events
        if (e instanceof NunchukEvent) {
            NunchukEvent nunchukEvent = (NunchukEvent) e;

            // Handle buttons
            NunchukButtonsEvent buttonsEvent = nunchukEvent.getButtonsEvent();
            nunchukButtonZ = buttonsEvent.isButtonZPressed();

            // Handle motions
            MotionSensingEvent motionEvent = nunchukEvent.getNunchukMotionSensingEvent();
            nunchukPitch = motionEvent.getGforce().getY();
            nunchukRoll = motionEvent.getGforce().getX();

            // Handle joystick
            JoystickEvent joystickEvent = nunchukEvent.getNunchukJoystickEvent();
            double angle = joystickEvent.getAngle() * Math.PI / 180;
            double x = Math.sin(angle);
            double y = Math.cos(angle);
            nunchukJoyX = (float) (x * joystickEvent.getMagnitude());
            nunchukJoyY = (float) (-y * joystickEvent.getMagnitude());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onGuitarHeroInsertedEvent
     * (wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent)
     */
    @Override
    public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent e) {
        onWiiGuitarHeroInsertedEvent(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onGuitarHeroRemovedEvent(
     * wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent)
     */
    @Override
    public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent e) {
        onWiiGuitarHeroRemovedEvent(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onIrEvent(wiiusej.wiiusejevents
     * .physicalevents.IREvent)
     */
    @Override
    public void onIrEvent(IREvent e) {
        onWiiIrEvent(e);
    }

    /**
     * On key pressed.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onKeyPressed(KeyEvent e);

    /**
     * On key released.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onKeyReleased(KeyEvent e);

    /**
     * On key typed.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onKeyTyped(KeyEvent e);

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onMotionSensingEvent(wiiusej
     * .wiiusejevents.physicalevents.MotionSensingEvent)
     */
    @Override
    public void onMotionSensingEvent(MotionSensingEvent e) {
        onWiiMotionSensingEvent(e);
    }

    /**
     * On mouse clicked.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onMouseClicked(MouseEvent e);

    /**
     * On mouse dragged.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onMouseDragged(MouseEvent e);

    /**
     * On mouse entered.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onMouseEntered(MouseEvent e);

    /**
     * On mouse exited.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onMouseExited(MouseEvent e);

    /**
     * On mouse moved.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onMouseMoved(MouseEvent e);

    /**
     * On mouse pressed.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onMousePressed(MouseEvent e);

    /**
     * On mouse released.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onMouseReleased(MouseEvent e);

    /**
     * On mouse wheel moved.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onMouseWheelMoved(MouseEvent e);

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onNunchukInsertedEvent(wiiusej
     * .wiiusejevents.wiiuseapievents.NunchukInsertedEvent)
     */
    @Override
    public void onNunchukInsertedEvent(NunchukInsertedEvent e) {
        onWiiNunchukInsertedEvent(e);
    }

    /*
     * (non-Javadoc)
     * @see
     * wiiusej.wiiusejevents.utils.WiimoteListener#onNunchukRemovedEvent(wiiusej
     * .wiiusejevents.wiiuseapievents.NunchukRemovedEvent)
     */
    @Override
    public void onNunchukRemovedEvent(NunchukRemovedEvent e) {
        onWiiNunchukRemovedEvent(e);
    }

    /*
     * (non-Javadoc)
     * @see wiiusej.wiiusejevents.utils.WiimoteListener#onStatusEvent(wiiusej.
     * wiiusejevents.wiiuseapievents.StatusEvent)
     */
    @Override
    public void onStatusEvent(StatusEvent e) {
        onWiiStatusEvent(e);
    }

    /**
     * On wii buttons event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiButtonsEvent(WiimoteButtonsEvent e);

    /**
     * On wii classic controller inserted event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiClassicControllerInsertedEvent(ClassicControllerInsertedEvent e);

    /**
     * On wii classic controller removed event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiClassicControllerRemovedEvent(ClassicControllerRemovedEvent e);

    /**
     * On wii disconnection event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiDisconnectionEvent(DisconnectionEvent e);

    /**
     * On wii expansion event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiExpansionEvent(ExpansionEvent e);

    /**
     * On wii guitar hero inserted event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiGuitarHeroInsertedEvent(GuitarHeroInsertedEvent e);

    /**
     * On wii guitar hero removed event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiGuitarHeroRemovedEvent(GuitarHeroRemovedEvent e);

    /**
     * On wii ir event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiIrEvent(IREvent e);

    /**
     * On wii motion sensing event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiMotionSensingEvent(MotionSensingEvent e);

    /**
     * On wii nunchuk inserted event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiNunchukInsertedEvent(NunchukInsertedEvent e);

    /**
     * On wii nunchuk removed event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiNunchukRemovedEvent(NunchukRemovedEvent e);

    /**
     * On wii status event.
     * 
     * @param e
     *            the e
     * @return discard this event
     */
    public abstract boolean onWiiStatusEvent(StatusEvent e);

    /**
     * Sets antialiasing.
     * 
     * @param s
     *            samples
     */
    public void setFSAA(int s) {
        fsaa = s;
    }

    /**
     * Set fullscreen before starting.
     * 
     * @param fullscreen
     *            the new fullscreen
     */
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    /**
     * Movement speed of the spectator.
     * 
     * @param moveSpeed
     *            the new move speed
     */
    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * Sets multithreading to render every RenderWindow in its own render
     * thread.
     * 
     * @param enable
     *            (Default is false)
     */
    public void setMultiThreading(boolean enable) {
        threading = enable;
    }

    /**
     * Set the transformation of the cave in world space.
     * 
     * @param pos
     *            the pos
     * @param xRot
     *            the x rot
     * @param yRot
     *            the y rot
     */
    public void setSpectator(Vector3 pos, float xRot, float yRot) {
        spectatorRotX = xRot;
        spectatorRotY = yRot;
        spectator.setTransform(Transform.translate(pos).mul(Transform.rotateY(spectatorRotY)).mul(Transform.rotateX(spectatorRotX)));
    }

    /**
     * Sets the QTM-Server.
     * 
     * @param host
     *            IP or host name of the QTM-Server (Default: localhost)
     */
    public void setTrackerHost(String host) {
        trackerHost = host;
    }

    /**
     * Sets the vertical synchronisation.
     * 
     * @param enable
     *            (Default is false)
     */
    public void setVSync(boolean enable) {
        vsync = enable;
    }

    /**
     * Is called after each frame.
     * 
     * @param renderDuration
     *            rendering time for the last frame in seconds
     */
    public abstract void simulate(double renderDuration);

    /**
     * Start the application.
     * 
     * @throws Exception
     *             the exception
     */
    public void start() throws Exception {
        // connect wiimote
        connectWiimote();

        // connect tracking system
        QTMRigidBodyTracking tracker = new QTMRigidBodyTracking();
        try {
            tracker.connect(trackerHost, 22222);
        } catch (Exception e) {
            Log.error(this.getClass(), "QTM: " + e.getMessage());
        }

        // create scene graph
        root.addChildNode(getSceneGraph());

        VRCameraNode frontLeftEye = new VRCameraNode("cam_front_l", Transform.translate(0, 0, -1.17f), new Vector4(-1.865f, 1.865f, 1.165f, -1.165f), true, new Transform());
        spectator.addChildNode(frontLeftEye);

        VRCameraNode frontRightEye = new VRCameraNode("cam_front_r", Transform.translate(0, 0, -1.17f), new Vector4(-1.865f, 1.865f, 1.165f, -1.165f), false, new Transform());
        spectator.addChildNode(frontRightEye);

        root.addChildNode(spectator);

        // create windows
        RenderWindow frontLeftEyeWin = new NewtRenderWindow(getPipeline(frontLeftEye, root), fullscreen);
        frontLeftEyeWin.addKeyListener(this);
        frontLeftEyeWin.addMouseListener(this);
        if (fsaa > 0)
            frontLeftEyeWin.setFSAA(fsaa);
        frontLeftEyeWin.setScreenDevice(1);
        frontLeftEyeWin.setVSync(vsync);

        RenderWindow frontRightEyeWin = new NewtRenderWindow(getPipeline(frontRightEye, root), fullscreen);
        frontRightEyeWin.addKeyListener(this);
        frontRightEyeWin.addMouseListener(this);
        if (fsaa > 0)
            frontRightEyeWin.setFSAA(fsaa);
        frontRightEyeWin.setScreenDevice(0);
        frontRightEyeWin.setVSync(vsync);

        Viewer v = new Viewer(threading, frontLeftEyeWin, frontRightEyeWin);

        double delta = 0;
        while (v.isRunning()) {
            long start = System.currentTimeMillis(); // save system time before
                                                     // rendering

            if (tracker.isConnected()) {
                // head tracking
                Matrix4 headTransM = tracker.getRigidBodyMatrix(trackerHeadId);
                Transform headTransform = new Transform(headTransM);

                List<SceneNode> cams = spectator.getChildNodes();
                for (SceneNode cam : cams)
                    if (cam instanceof VRCameraNode) {
                        VRCameraNode vrCam = (VRCameraNode) cam;
                        vrCam.setHeadTransform(headTransform); // set new head
                                                               // transformation
                                                               // to camera
                    }

                headWorldTrans = root.getTransform().mul(spectator.getTransform()).mul(headTransform);
                headTrans = headTransform;

                // wiimote tracking
                Matrix4 wiimoteTransM = tracker.getRigidBodyMatrix(trackerWiimoteId);
                Transform wiimoteTransform = new Transform(wiimoteTransM);

                wiimoteWorldTrans = root.getTransform().mul(spectator.getTransform()).mul(wiimoteTransform);
            }

            move(delta); // move spectator
            simulate(delta); // call simulation

            v.display(); // render

            delta = (double) (System.currentTimeMillis() - start) / 1000; // calculate
                                                                          // render
                                                                          // duration
                                                                          // in
                                                                          // seconds
        }
    }
}
