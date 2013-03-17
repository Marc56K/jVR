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
public class NewtTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(50, 0, -1));
        try {
            new NewtTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public NewtTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode house = ColladaLoader.load(new File("data/meshes/house.dae"));

        root.addChildNode(house);

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
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p.drawGeometry("AMBIENT", "");
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "", false);
        // p.doForwardLightLoop("LIGHTING", "", false);

        // /////////////////////////////////////////////

        NewtRenderWindow win1 = new NewtRenderWindow(p, 1024, 768);
        win1.addKeyListener(this);
        win1.addMouseListener(this);
        // win1.setVSync(true);
        win1.setFSAA(8);
        win1.setPosition(-0, 0);
        // win1.setUndecorated(true);
        // win1.setFullscreen(true);

        NewtRenderWindow win2 = new NewtRenderWindow(p, 800, 600);
        win2.addKeyListener(this);
        win2.addMouseListener(this);
        // win2.setVSync(true);
        // win2.setFSAA(2);
        win2.setPosition(800, 0);
        // win2.setUndecorated(true);

        Viewer viewer = new Viewer(win1);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                double delta = System.currentTimeMillis() - start;
                move(delta, 0.1);
            }
            viewer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
