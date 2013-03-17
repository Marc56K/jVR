package de.bht.jvr.tests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNodeMerger;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.examples.input.MouseKeyboardNavigationExample;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.renderer.NewtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.util.Color;

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

public class ShapeNodeMergerTest extends MouseKeyboardNavigationExample {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new ShapeNodeMergerTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ShapeNodeMergerTest() throws Exception {
        // load the model
        // SceneNode model = ColladaLoader.load(new
        // File("./data/meshes/house1/models/house.dae"));
        SceneNode model = ColladaLoader.load(new File("./data/meshes/house3/models/house3.dae"));
        // SceneNode model = ColladaLoader.load(new
        // File("./data/meshes/testwelt02.dae"));

        ShapeNodeMerger.merge((GroupNode) model, "(?!Translucent).*", "", "Opaque_Objects_Shape"); // <---------
        ShapeNodeMerger.merge((GroupNode) model, "Translucent", "Translucent", "Translucent_Objects_Shape"); // <---------
        // ShapeNodeMerger.merge((GroupNode)model, null, "", "Shape");

        model.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.05f))); // transform
                                                                                   // the
                                                                                   // model

        Printer.print(model);

        // create a directional light
        DirectionalLightNode dLight = new DirectionalLightNode();
        dLight.setTransform(Transform.rotateXDeg(-75));
        dLight.setIntensity(1.2f);

        // create a camera
        CameraNode cam = new CameraNode("MyCamera", 16f / 10f, 60);
        cam.setTransform(Transform.translate(20, 10, -25)); // transform the
                                                            // camera
        super.cam = cam;

        // create the scene graph
        GroupNode root = new GroupNode("MyRoot");
        root.addChildNodes(cam, model, dLight);

        // pipeline
        // //////////////////////////////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.setBackFaceCulling(false); // disable back face culling
        p.setViewFrustumCullingMode(1);
        p.switchCamera(cam);
        p.clearBuffers(true, true, Color.gray); // clear screen
        // render only opak objects
        // p.setUniform("jvr_Material_Ambient", new UniformColor(Color.white));
        p.drawGeometry("AMBIENT", "(?!Translucent).*");
        // p.setBlendFunc(GL.GL_ONE, GL.GL_ZERO);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "(?!Translucent).*");
        // now render the transparent objects
        p.drawGeometry("AMBIENT", "Translucent", true);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "Translucent");

        // //////////////////////////////////////////////////////////////////////

        // create a render window to render the pipeline
        RenderWindow win = new NewtRenderWindow(p, 1280, 800);
        win.addKeyListener(this); // set the key listener for the window
        win.addMouseListener(this); // set the mouse listener for the window
        win.setFSAA(8);

        // create a viewer
        Viewer v = new Viewer(true, win); // the viewer manages all render
                                          // windows

        // main loop
        while (v.isRunning()) {
            long start = System.currentTimeMillis(); // save system time before
                                                     // rendering
            v.display(); // render the scene
            double delta = System.currentTimeMillis() - start; // calculate
                                                               // render
                                                               // duration
            super.move(delta);
        }
    }
}
