package de.bht.jvr.examples.shader;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.SpotLightNode;
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
 * - frame buffer object (fbo)
 * 
 * @author Marc Roßbach
 */

public class ShadowMappingExample extends MouseKeyboardNavigationExample {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new ShadowMappingExample();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ShadowMappingExample() throws Exception {
        // load collada files
        SceneNode teapot = ColladaLoader.load(new File("./data/meshes/teapot.dae"));
        teapot.setTransform(Transform.translate(-5, 0, 0).mul(Transform.scale(2))); // transform the teapot

        SceneNode sphere = ColladaLoader.load(new File("./data/meshes/sphere.dae"));
        sphere.setTransform(Transform.translate(0, 2, 0).mul(Transform.scale(2.5f))); // transform the sphere

        SceneNode plane = ColladaLoader.load(new File("./data/meshes/plane.dae"));
        plane.setTransform(Transform.scale(100).mul(Transform.rotateXDeg(-90))); // transform the ground plane

        // create two lights
        SpotLightNode sLight1 = new SpotLightNode();
        sLight1.setTransform(Transform.translate(3, 6, 6).mul(Transform.rotateYDeg(20)).mul(Transform.rotateXDeg(-45)));
        sLight1.setSpotCutOff(30);
        sLight1.setCastShadow(true); // set the uniform jvr_LightSource_CastShadow = 1
        sLight1.setShadowBias(0.5f); // increase this value to avoid self shadowing

        SpotLightNode sLight2 = new SpotLightNode();
        sLight2.setTransform(Transform.translate(-9, 5, 4).mul(Transform.rotateYDeg(-40)).mul(Transform.rotateXDeg(-45)));
        sLight2.setSpotCutOff(30);
        sLight2.setCastShadow(true); // set the uniform jvr_LightSource_CastShadow = 1
        sLight2.setShadowBias(0.5f); // increase this value to avoid self shadowing

        // create a camera
        CameraNode cam = new CameraNode("MyCamera", 4f / 3f, 60);
        cam.setTransform(Transform.translate(-3, 2, 6)); // transform the camera
        super.cam = cam;

        // create the scene graph
        GroupNode root = new GroupNode("MyRoot");
        root.addChildNodes(cam, sLight1, sLight2, teapot, sphere, plane);

        // pipeline
        // //////////////////////////////////////////////////////////////
        Pipeline p = new Pipeline(root);

        p.switchCamera(cam);
        p.clearBuffers(true, true, new Color(0, 0, 0, 0));

        // render the geometry without light
        p.drawGeometry("AMBIENT", null);

        // iterate all lights with shadow
        Pipeline lp = p.doLightLoop(false, true);
        // use the light source as camera
        lp.switchLightCamera();
        // create a depth map (1024x1024)
        lp.createFrameBufferObject("ShadowMap", true, 0, 1024, 1024, 0);
        // switch to depth map
        lp.switchFrameBufferObject("ShadowMap");
        // clear the depth map
        lp.clearBuffers(true, false, null);
        // render to the depth map
        lp.drawGeometry("AMBIENT", null);
        // switch back to screen buffer
        lp.switchFrameBufferObject(null);
        // switch back to normal camera
        lp.switchCamera(cam);
        // bind the depth map to the uniform jvr_ShadowMap
        lp.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
        // render the geometry for the active light
        lp.drawGeometry("LIGHTING", null);

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
