package de.bht.jvr.tests;

import de.bht.jvr.util.Color;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GeometryProbe;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
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
public class ProbeTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, -1, -1));
        try {
            new ProbeTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProbeTest() throws Exception {
        ProbeCentral pc = new ProbeCentral();
        pc.start();

        GroupNode root = new GroupNode();

        GeometryProbe geoprobe = pc.newGeoemtryProbe("Probe1");

        ShapeNode probe = new ShapeNode("ProbeShape", geoprobe, ShaderMaterial.makePhongShaderMaterial());

        root.addChildNode(probe);

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
        // p.doLightLoop(true, true).drawGeometry("LIGHTING", "", false);

        // /////////////////////////////////////////////

        // RenderWindow win = new NewtRenderWindow(p, 800, 600);
        RenderWindow win = new AwtRenderWindow(p, 800, 600);
        Viewer viewer = new Viewer(true, win);

        try {
            while (viewer.isRunning() && run) {
                geoprobe.traversed();
                viewer.display();
            }
            viewer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
