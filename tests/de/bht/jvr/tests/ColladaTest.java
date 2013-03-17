package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
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
 */
public class ColladaTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(40, 0, 0));
        try {
            new ColladaTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ColladaTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode model = ColladaLoader.load(new File("data/meshes/car.dae"));
        model.setTransform(Transform.rotateXDeg(-90));
        Printer.print(model);
        root.addChildNode(model);

        DirectionalLightNode dLight = new DirectionalLightNode();
        dLight.setTransform(Transform.rotateXDeg(-75));
        dLight.setIntensity(1.2f);
        root.addChildNode(dLight);

        CameraNode cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 200));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.setBackFaceCulling(false); // disable back face culling
        p.switchCamera(cam1);
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p.drawGeometry("AMBIENT", "", false);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "");
        p.drawGeometry("AMBIENT", "Translucent", true);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "Translucent");

        // /////////////////////////////////////////////

        RenderWindow w = new AwtRenderWindow(p, 1024, 768);
        w.addKeyListener(this);
        w.addMouseListener(this);

        Viewer viewer = new Viewer(w);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                move(System.currentTimeMillis() - start, 0.1);
            }
            viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
