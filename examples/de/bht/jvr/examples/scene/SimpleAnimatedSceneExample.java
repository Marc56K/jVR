package de.bht.jvr.examples.scene;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
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
 * - loading scenes from collada files
 * - simple scene graph with light and camera
 * - simple pipeline with ambient and lighting pass
 * - generating and rendering a render windows
 * - scene animation
 * 
 * @author Marc Roßbach
 */

public class SimpleAnimatedSceneExample {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // init logging system
            Log.addLogListener(new LogPrinter(-1, 0, -1)); // only log errors and infos

            // load a teapot from a collada file
            SceneNode teapot = ColladaLoader.load(new File("data/meshes/teapot.dae"));

            // we also need some light
            PointLightNode light = new PointLightNode("MyPointLight");
            light.setTransform(Transform.translate(1, 5, 2)); // translate the light

            // and a camera (aspect ration: 4:3 and field of view 60ß)
            CameraNode cam = new CameraNode("MyCamera", 4f / 3f, 60);
            cam.setTransform(Transform.translate(0, 1, 3));

            // now generate the scene graph
            GroupNode root = new GroupNode("MyRoot");
            root.addChildNodes(teapot, light, cam);

            // to render the scene we need a rendering pipeline
            Pipeline p = new Pipeline(root);
            p.clearBuffers(true, true, new Color(0, 0, 0)); // clear the depth and the color buffer and set the clear color to black
            p.switchCamera(cam); // because a scene can have more than one camera
            p.drawGeometry("AMBIENT", null); // first we have to render the ambient pass (null = all material classes)
            p.doLightLoop(true, true).drawGeometry("LIGHTING", null); // now we have to render the lighting pass for every active light in the scene

            // create a render window to render the pipeline
            RenderWindow win = new AwtRenderWindow(p, 800, 600);

            // create a viewer
            Viewer v = new Viewer(win); // the viewer manages all render windows

            // main loop
            float angle = 0;
            while (v.isRunning()) {
                long start = System.currentTimeMillis(); // save system time before rendering
                v.display(); // render the scene
                teapot.setTransform(Transform.rotateDeg(new Vector3(1, 1, 0), angle)); // rotate the teapot
                double delta = System.currentTimeMillis() - start; // calculate render duration
                angle += delta * 0.1f; // increase rotation angle depending on render duration
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
