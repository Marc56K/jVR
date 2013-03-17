package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
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
 */
public class MultiThreadRendererTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(100));
        new MultiThreadRendererTest();
    }

    public MultiThreadRendererTest() {
        GroupNode root = new GroupNode();
        SceneNode scene = null;
        try {
            scene = ColladaLoader.load(new File("data/meshes/testwelt01.dae"));
            scene.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.1f)));

            root.addChildNode(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DirectionalLightNode dLight = new DirectionalLightNode();
        dLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        dLight.setIntensity(1.5f);
        dLight.setTransform(Transform.rotateYDeg(-60).mul(Transform.rotateXDeg(-40)));
        root.addChildNode(dLight);

        CameraNode cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 2, 6));
        root.addChildNode(cam1);
        cams.add(cam1);

        CameraNode cam2 = new CameraNode("cam2", 4f / 3f, 60f);
        cam2.setTransform(Transform.translate(0, 2, 6));
        root.addChildNode(cam2);
        cams.add(cam2);

        // win1/////////////////////////////////////////
        Pipeline p1 = new Pipeline(root);
        p1.switchCamera(cam1);
        p1.clearBuffers(true, new boolean[] { true }, new Color(0.5f, 0.5f, 0.5f));
        p1.drawGeometry("AMBIENT", "");
        p1.doLightLoop(true, true).drawGeometry("LIGHTING", "", false);

        RenderWindow win1 = new NewtRenderWindow(p1, 800, 600);
        win1.addKeyListener(this);
        win1.addMouseListener(this);
        win1.setVSync(false);

        // win2/////////////////////////////////////////
        Pipeline p2 = new Pipeline(root);
        p2.switchCamera(cam1);
        p2.clearBuffers(true, new boolean[] { true }, new Color(0, 0, 0, 0));
        // p2.drawGeometry("AMBIENT", "");
        p2.doLightLoop(true, true).drawGeometry("LIGHTING", "", false);

        RenderWindow win2 = new NewtRenderWindow(p2, 800, 600);
        win2.addKeyListener(this);
        win2.addMouseListener(this);
        win2.setPosition(800, 0);
        win2.setVSync(false);

        // //////////////////////////////////////////////

        Viewer viewer = new Viewer(win1, win2);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                move(System.currentTimeMillis() - start, 0.01);
            }
            viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
