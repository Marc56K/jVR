package de.bht.jvr.tests;

import java.util.ArrayList;
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
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.logger.Log;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
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
public class TestBaseWii implements WiimoteListener {
    protected List<SceneNode> cams = new ArrayList<SceneNode>();
    protected boolean run = true;

    protected double nunchukJoyX = 0;
    protected double nunchukJoyY = 0;
    protected double nunchukPitch = 0;
    protected double nunchukRoll = 0;
    protected boolean nunchukButtonZ = false;
    protected boolean wiimoteButtonHome = false;
    protected double rx = 0;
    protected double ry = 0;

    public void connectWiimote() {
        // Connect Wiimote
        Log.info(this.getClass(), "Put Wiimote in discoverable mode (press 1+2)");
        Wiimote[] wiimotes = WiiUseApiManager.getWiimotes(1, false);
        if (WiiUseApiManager.getNbConnectedWiimotes() < 1)
            Log.error(this.getClass(), "Timeout - No Wiimotes found.");
        else {
            Log.info(this.getClass(), "Wiimote connected.");
            wiimotes[0].addWiiMoteEventListeners(this);
        }
    }

    protected void move(double renderDuration) {
        double deadZone = 0.1;
        double joyX = nunchukJoyX / 2.0;
        double joyY = nunchukJoyY / 2.0;

        if (nunchukButtonZ) // Press Nunchuk-Button-Z to unlock
        {
            rx -= renderDuration * nunchukPitch;
            ry -= renderDuration * nunchukRoll;

            if (rx < -Math.PI / 2)
                rx = -Math.PI / 2;
            if (rx > Math.PI / 2)
                rx = Math.PI / 2;
        }

        if (wiimoteButtonHome)// Press Wiimote-Home to center view
        {
            rx = 0;
            ry = 0;
        }

        for (SceneNode cam : cams) {
            float[] pos = cam.getTransform().getMatrix().translation().getData();
            // Move forward
            if (joyY < -deadZone) {
                pos[0] += joyY * Math.sin(ry) * Math.cos(-rx) * renderDuration;
                pos[1] += joyY * Math.sin(-rx) * renderDuration;
                pos[2] += joyY * Math.cos(ry) * Math.cos(-rx) * renderDuration;
            }
            if (joyY > deadZone) {
                // Move backward
                pos[0] += joyY * Math.sin(ry) * Math.cos(-rx) * renderDuration;
                pos[1] += joyY * Math.sin(-rx) * renderDuration;
                pos[2] += joyY * Math.cos(ry) * Math.cos(-rx) * renderDuration;
            }
            if (joyX < -deadZone) {
                // Strafe left
                pos[0] -= joyX * Math.sin(ry - Math.PI / 2) * renderDuration;
                pos[2] -= joyX * Math.cos(ry - Math.PI / 2) * renderDuration;
            }
            if (joyX > deadZone) {
                // Strafe right
                pos[0] += joyX * Math.sin(ry + Math.PI / 2) * renderDuration;
                pos[2] += joyX * Math.cos(ry + Math.PI / 2) * renderDuration;
            }

            Matrix4 rotX = Matrix4.rotate(new Vector3(1, 0, 0), (float) rx);
            Matrix4 rotY = Matrix4.rotate(new Vector3(0, 1, 0), (float) ry);
            Matrix4 transM = Matrix4.translate(new Vector3(pos)).mul(rotY).mul(rotX);

            cam.setTransform(new Transform(transM));
        }
    }

    protected void move(double renderDuration, double speed) {
        move(renderDuration * speed);
    }

    @Override
    public void onButtonsEvent(WiimoteButtonsEvent arg0) {
        wiimoteButtonHome = arg0.isButtonHomePressed();
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
    public void onExpansionEvent(ExpansionEvent arg0) {
        // Handle Nunchuk-Events
        if (arg0 instanceof NunchukEvent) {
            NunchukEvent nunchukEvent = (NunchukEvent) arg0;

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
            nunchukJoyX = x * joystickEvent.getMagnitude();
            nunchukJoyY = -y * joystickEvent.getMagnitude();
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

}
