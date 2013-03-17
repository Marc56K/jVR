package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.DisconnectionEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.StatusEvent;
import de.bht.jvr.applicationbase.AbstractHolodeckApp;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.VRCameraNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
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
public class HolodeckAppTest extends AbstractHolodeckApp {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, 0, -1));
        try {
            new HolodeckAppTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SceneNode box = null;

    public HolodeckAppTest() throws Exception {
        super.setSpectator(new Vector3(0, 2, -5), 0, 0);
        super.setFullscreen(false);
        super.start();
    }

    @Override
    public Pipeline getPipeline(VRCameraNode cam, SceneNode root) throws Exception {
        Pipeline p = new Pipeline(root);
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p.switchCamera(cam);
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        return p;
    }

    @Override
    public SceneNode getSceneGraph() throws Exception {
        GroupNode root = new GroupNode();

        DirectionalLightNode dLight = new DirectionalLightNode();
        dLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        dLight.setIntensity(1.5f);
        dLight.setTransform(Transform.rotateYDeg(-60).mul(Transform.rotateXDeg(-40)));
        root.addChildNode(dLight);

        SceneNode scene = ColladaLoader.load(new File("./data/meshes/testwelt01.dae"));
        scene.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.1f)));
        root.addChildNode(scene);

        box = ColladaLoader.load(new File("./data/meshes/box.dae"));
        root.addChildNode(box);

        return root;
    }

    @Override
    public boolean onKeyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onKeyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onKeyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseWheelMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiButtonsEvent(WiimoteButtonsEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiClassicControllerInsertedEvent(ClassicControllerInsertedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiClassicControllerRemovedEvent(ClassicControllerRemovedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiDisconnectionEvent(DisconnectionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiExpansionEvent(ExpansionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiGuitarHeroInsertedEvent(GuitarHeroInsertedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiGuitarHeroRemovedEvent(GuitarHeroRemovedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiIrEvent(IREvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiMotionSensingEvent(MotionSensingEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiNunchukInsertedEvent(NunchukInsertedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiNunchukRemovedEvent(NunchukRemovedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiStatusEvent(StatusEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void simulate(double renderDuration) {
        if (box != null)
            box.setTransform(super.getWiimoteWorldTransform().mul(Transform.scale(0.01f, 0.01f, 20)));
    }
}
