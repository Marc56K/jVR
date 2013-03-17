package de.bht.jvr.examples.input;

import de.bht.jvr.util.Color;
import java.io.File;

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
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.renderer.AwtRenderWindow;
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
 * This basic sample demonstrates the following features:
 * - WiimoteListener
 * 
 * @author Marc Roßbach
 */

public class WiimoteNunchukNavigationExample implements WiimoteListener {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new WiimoteNunchukNavigationExample().start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected float nunchukJoyX = 0;
    protected float nunchukJoyY = 0;
    protected float nunchukPitch = 0;
    protected float nunchukRoll = 0;
    protected boolean nunchukButtonZ = false;

    protected float rotX = 0; // rotation of the camera (x-axis)
    protected float rotY = 0; // rotation of the camera (y-axis)
    protected SceneNode cam; // camera node

    public void connectWiimote() {
        // connect wiimote
        Log.info(this.getClass(), "Put Wiimote in discoverable mode (press 1+2)");
        Wiimote[] wiimotes = WiiUseApiManager.getWiimotes(1, false);
        if (WiiUseApiManager.getNbConnectedWiimotes() < 1)
            Log.error(this.getClass(), "Timeout - No Wiimotes found.");
        else {
            Log.info(this.getClass(), "Wiimote connected.");
            wiimotes[0].addWiiMoteEventListeners(this);
        }
    }

    /**
     * generate a scene and a pipeline
     * 
     * @return
     * @throws Exception
     */
    public Pipeline makePipeline() throws Exception {
        // load a scene from a collada file
        SceneNode house = ColladaLoader.load(new File("data/meshes/house.dae"));
        house.setTransform(Transform.scale(0.1f));

        // we also need some light
        DirectionalLightNode light = new DirectionalLightNode("MySun");
        light.setTransform(Transform.rotateXDeg(-30)); // transform the light

        // and a camera (aspect ration: 4:3 and field of view 60ß)
        cam = new CameraNode("MyCamera", 4f / 3f, 60);
        cam.setTransform(Transform.translate(0, 12, 20)); // start position

        // now generate the scene graph
        GroupNode root = new GroupNode("MyRoot");
        root.addChildNodes(house, light, cam);

        // to render the scene we need a rendering pipeline
        Pipeline p = new Pipeline(root);
        p.clearBuffers(true, true, Color.gray); // clear the depth and the color buffer and set the clear color to gray
        p.switchCamera((CameraNode) cam); // because a scene can have more than one camera
        p.drawGeometry("AMBIENT", null); // first we have to render the ambient pass (null = all material classes)
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null); // now we have to render the lighting pass for every active light in the scene

        return p;
    }

    public void move(double renderDuration) {
        renderDuration *= 0.01; // slow down

        float deadZone = 0.1f;
        float joyX = (float) (nunchukJoyX / 2.0);
        float joyY = (float) (nunchukJoyY / 2.0);

        if (nunchukButtonZ) // Press Nunchuk-Button-Z to unlock
        {
            rotX -= renderDuration * nunchukPitch;
            rotY -= renderDuration * nunchukRoll;

            if (rotX < -Math.PI / 2)
                rotX = (float) (-Math.PI / 2); // minimum x-rotation -90ß
            if (rotX > Math.PI / 2)
                rotX = (float) (Math.PI / 2); // maximum x-rotation 90ß
        }

        float[] pos = cam.getTransform().getMatrix().translation().getData();

        if (joyY < -deadZone) // Move forward
        {
            pos[0] += joyY * Math.sin(rotY) * Math.cos(-rotX) * renderDuration;
            pos[1] += joyY * Math.sin(-rotX) * renderDuration;
            pos[2] += joyY * Math.cos(rotY) * Math.cos(-rotX) * renderDuration;
        }
        if (joyY > deadZone) // Move backward
        {
            pos[0] += joyY * Math.sin(rotY) * Math.cos(-rotX) * renderDuration;
            pos[1] += joyY * Math.sin(-rotX) * renderDuration;
            pos[2] += joyY * Math.cos(rotY) * Math.cos(-rotX) * renderDuration;
        }
        if (joyX < -deadZone) // Strafe left
        {
            pos[0] -= joyX * Math.sin(rotY - Math.PI / 2) * renderDuration;
            pos[2] -= joyX * Math.cos(rotY - Math.PI / 2) * renderDuration;
        }
        if (joyX > deadZone) // Strafe right
        {
            pos[0] += joyX * Math.sin(rotY + Math.PI / 2) * renderDuration;
            pos[2] += joyX * Math.cos(rotY + Math.PI / 2) * renderDuration;
        }

        // set the new camera transformation (Transformation = Translation *
        // RotationY * RotationX)
        cam.setTransform(Transform.translate(new Vector3(pos)).mul(Transform.rotateY(rotY)).mul(Transform.rotateX(rotX)));
    }

    @Override
    public void onButtonsEvent(WiimoteButtonsEvent arg0) {
        // exit application with Wiimote-Button-A
        if (arg0.isButtonAPressed())
            System.exit(0);
    }

    @Override
    public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDisconnectionEvent(DisconnectionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onExpansionEvent(ExpansionEvent e) {
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

    @Override
    public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onIrEvent(IREvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMotionSensingEvent(MotionSensingEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNunchukInsertedEvent(NunchukInsertedEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNunchukRemovedEvent(NunchukRemovedEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusEvent(StatusEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void start() throws Exception {
        // init logging system
        Log.addLogListener(new LogPrinter(-1, 0, -1)); // ignore warnings

        // connect the wiimote
        connectWiimote();

        // load a scene and generate the pipeline
        Pipeline p = makePipeline();

        // create a render window to render the pipeline
        RenderWindow win = new AwtRenderWindow(p, 800, 600);

        // create a viewer
        Viewer v = new Viewer(win); // the viewer manages all render windows

        // main loop
        while (v.isRunning()) {
            long start = System.currentTimeMillis(); // save system time before rendering
            v.display(); // render the scene
            double delta = System.currentTimeMillis() - start; // calculate render duration
            move(delta);
        }
    }
}
