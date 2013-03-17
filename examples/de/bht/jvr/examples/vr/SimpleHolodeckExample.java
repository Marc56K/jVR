package de.bht.jvr.examples.vr;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.applicationbase.Driver;
import de.bht.jvr.applicationbase.Input;
import de.bht.jvr.applicationbase.SimpleAbstractHolodeckApp;
import de.bht.jvr.applicationbase.SimpleAbstractHolodeckAppConfig;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.VRCameraNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformColor;
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
 *
 *
 * This basic sample demonstrates the following features:
 * - off-axis projection with VRCameraNode
 * - real-time motion tracking (e.g. head tracking)
 * 
 * For tracking you need a running QTM-Server with two rigid bodies.
 * 
 * @author Marc Roßbach
 */

public class SimpleHolodeckExample extends SimpleAbstractHolodeckApp {

    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, 0, -1));
        try {
            SimpleAbstractHolodeckAppConfig conf = new SimpleAbstractHolodeckAppConfig();
            conf.holoSim = true;
            conf.fullscreen = false;
            conf.vsync = false;
            new SimpleHolodeckExample(conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Input input = new Input();
    private Driver driver = new Driver(new Vector3(0f, 0.0f, 4f), 2);
    private SceneNode pointer;

    public SimpleHolodeckExample(SimpleAbstractHolodeckAppConfig conf) throws Exception {
        super(conf);

        setHolodeckTransform(new Transform(driver.getViewMatrix()));

        frontRightEyeWin.addKeyListener(input);
        frontRightEyeWin.addMouseListener(input);

        start();
    }

    @Override
    public Pipeline getPipeline(VRCameraNode cam, SceneNode root) throws Exception {
        Pipeline p = new Pipeline(root);
        p.clearBuffers(true, true, new Color(0.5f, 0.7f, 1.0f));
        p.switchCamera(cam);
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        return p;
    }

    @Override
    public SceneNode getSceneGraph() throws Exception {
        SceneNode box = ColladaLoader.load(new File("./data/meshes/box.dae"));
        SceneNode red = ColladaLoader.load(new File("./data/meshes/box.dae"));
        for (Material m : Finder.findAllMaterials(red, null))
            if (m instanceof ShaderMaterial) {
                ShaderMaterial sm = (ShaderMaterial) m;
                sm.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0.2f, 0.0f, 0.0f, 1.0f)));
                sm.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new Color(1.0f, 0.0f, 0.0f, 1.0f)));
            }

        GroupNode root = new GroupNode();

        DirectionalLightNode dLight = new DirectionalLightNode();
        dLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        dLight.setTransform(Transform.rotateYDeg(-60).mul(Transform.rotateXDeg(-40)));
        root.addChildNode(dLight);

        GroupNode ground = new GroupNode(box);
        ground.setTransform(Transform.scale(100, 1, 100).mul(Transform.translate(0.0f, -0.5f, 0.0f)));

        GroupNode block = new GroupNode(red);
        block.setTransform(Transform.translate(0.0f, 0.5f, 1.0f));

        SceneNode pointer0 = new GroupNode(box);
        pointer0.setTransform(Transform.translate(0, 0, -2).mul(Transform.scale(0.01f, 0.01f, 2.0f)));
        pointer = new GroupNode(pointer0);

        root.addChildNodes(ground, block, pointer);

        root.addChildNode(pointer);

        return root;
    }

    @Override
    public void simulate(float elapsed) {
        // Set the new transformation for the whole Holodeck thing in world space.
        driver.simulate(elapsed, input);
        setHolodeckTransform(new Transform(driver.getViewMatrix()));

        // Set the current position of the tracked pointer.
        pointer.setTransform(getWiimoteWorldTransform());
    }
}
