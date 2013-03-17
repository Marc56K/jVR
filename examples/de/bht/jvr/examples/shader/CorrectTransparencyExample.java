package de.bht.jvr.examples.shader;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.examples.input.MouseKeyboardNavigationExample;
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
 * - rendering of material classes
 * 
 * @author Marc Roßbach
 */

public class CorrectTransparencyExample extends MouseKeyboardNavigationExample {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new CorrectTransparencyExample();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public CorrectTransparencyExample() throws Exception {
        // load the model
        SceneNode model = ColladaLoader.load(new File("data/meshes/car.dae"));
        model.setTransform(Transform.rotateYDeg(45).mul(Transform.rotateXDeg(-90)).mul(Transform.scale(0.1f))); // transform the model

        // create a directional light
        DirectionalLightNode dLight = new DirectionalLightNode();
        dLight.setTransform(Transform.rotateXDeg(-75));
        dLight.setIntensity(1.2f);

        // create a camera
        CameraNode cam = new CameraNode("MyCamera", 4f / 3f, 60);
        cam.setTransform(Transform.translate(1, 5, 2)); // transform the camera
        super.cam = cam;

        // create the scene graph
        GroupNode root = new GroupNode("MyRoot");
        root.addChildNodes(cam, model, dLight);

        // pipeline
        // //////////////////////////////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.setBackFaceCulling(false); // disable back face culling
        p.switchCamera(cam);
        p.clearBuffers(true, true, new Color(0.5f, 0.7f, 1.0f)); // clear screen
        // render only opak objects
        p.drawGeometry("AMBIENT", "(?!Translucent).*", false);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "(?!Translucent).*");
        // now render the transparent objects
        p.drawGeometry("AMBIENT", "Translucent", true);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "Translucent");

        // //////////////////////////////////////////////////////////////////////

        // create a render window to render the pipeline
        RenderWindow win = new AwtRenderWindow(p, 800, 600);
        win.addKeyListener(this); // set the key listener for the window
        win.addMouseListener(this); // set the mouse listener for the window

        // create a viewer
        Viewer v = new Viewer(win); // the viewer manages all render windows

        // main loop
        while (v.isRunning()) {
            long start = System.currentTimeMillis(); // save system time before rendering
            v.display(); // render the scene
            double delta = System.currentTimeMillis() - start; // calculate render duration
            super.move(delta);
        }
    }
}
