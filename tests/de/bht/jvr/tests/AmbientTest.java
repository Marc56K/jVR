package de.bht.jvr.tests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.renderer.AwtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.util.Color;
import de.bht.jvr.util.InputState;
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
 * This basic sample demonstrates how to setup a very simple jVR application. It
 * opens a single window that shows a centered box geometry which is lit by a
 * point light source and can be rotated interactively with some keys. This
 * example uses the default phong shader that is applied to geometry loaded from
 * Collada files.
 * 
 * @author Marc Roßbach
 * @author Henrik Tramberend
 */

public class AmbientTest {

    public static void main(String[] args) throws Exception {
        GroupNode root = new GroupNode("scene root");

        SceneNode teapot = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        teapot.setTransform(Transform.translate(0f, -0.6f, 0f));
        GroupNode teapotRotor = new GroupNode();
        teapotRotor.addChildNode(teapot);

        PointLightNode light0 = new PointLightNode("sun0");
        light0.setTransform(Transform.translate(3, -3, 1));
        light0.setAmbientColor(new Color(0.0f, 0.0f, 1.0f));
        light0.setDiffuseColor(new Color(1.0f, 0.0f, 0.0f));
        light0.setSpecularColor(new Color(1.0f, 1.0f, 1.0f));

        PointLightNode light1 = new PointLightNode("sun1");
        light1.setTransform(Transform.translate(-3, -3, 1));
        light1.setAmbientColor(new Color(0.0f, 0.0f, 1.0f));
        light1.setDiffuseColor(new Color(0.0f, 1.0f, 0.0f));
        light1.setSpecularColor(new Color(1.0f, 1.0f, 1.0f));

        CameraNode camera = new CameraNode("camera", 4f / 3f, 60);
        camera.setTransform(Transform.translate(0, 0, 3));

        root.addChildNodes(teapotRotor, light0, light1, camera);
        Printer.print(root);

        Pipeline pipeline = new Pipeline(root);
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));
        pipeline.switchCamera(camera);
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        InputState input = new InputState();
        RenderWindow win = new AwtRenderWindow(pipeline, 800, 600);
        win.addKeyListener(input);

        StopWatch time = new StopWatch();
        Viewer v = new Viewer(win);

        float angleY = 0;
        float angleX = 0;
        float speed = 90;

        while (v.isRunning()) {
            float elapsed = time.elapsed();

            if (input.isOneDown('W', java.awt.event.KeyEvent.VK_UP))
                angleX += elapsed * speed;
            if (input.isOneDown('S', java.awt.event.KeyEvent.VK_DOWN))
                angleX -= elapsed * speed;
            if (input.isOneDown('D', java.awt.event.KeyEvent.VK_RIGHT))
                angleY += elapsed * speed;
            if (input.isOneDown('A', java.awt.event.KeyEvent.VK_LEFT))
                angleY -= elapsed * speed;

            teapotRotor.setTransform(Transform.rotateYDeg(angleY).mul(Transform.rotateXDeg(angleX)));

            if (input.isDown('Q'))
                System.exit(0);

            v.display();
        }
    }
}
