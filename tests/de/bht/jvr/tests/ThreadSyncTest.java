package de.bht.jvr.tests;

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
public class ThreadSyncTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new ThreadSyncTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ThreadSyncTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode scene = ColladaLoader.load(new File("data/meshes/box.dae"));
        scene.setTransform(Transform.scale(9000, 1, 1));
        root.addChildNode(scene);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        CameraNode cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 200));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, Color.gray);
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////
        RenderWindow w1 = new NewtRenderWindow(p, 100, 768);
        w1.setUndecorated(true);

        RenderWindow w2 = new NewtRenderWindow(p, 100, 768);
        w2.setPosition(100, 0);
        w2.setUndecorated(true);

        RenderWindow w3 = new NewtRenderWindow(p, 100, 768);
        w3.setPosition(200, 0);
        w3.setUndecorated(true);

        RenderWindow w4 = new NewtRenderWindow(p, 100, 768);
        w4.setPosition(300, 0);
        w4.setUndecorated(true);

        RenderWindow w5 = new NewtRenderWindow(p, 100, 768);
        w5.setPosition(400, 0);
        w5.setUndecorated(true);

        // w1.setVSync(true);
        // w2.setVSync(true);
        // w3.setVSync(true);
        // w4.setVSync(true);
        // w5.setVSync(true);

        Viewer viewer = new Viewer(true, w1, w2, w3, w4, w5);

        try {
            float angle = 0;
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                double delta = System.currentTimeMillis() - start;
                angle += delta * 0.3;
                cam1.setTransform(Transform.translate(0, 0, 200).mul(Transform.rotateXDeg(angle)));
                // if(angle>90)angle = -90;
            }
            viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
