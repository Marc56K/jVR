package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.renderer.NewtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.renderer.WindowListener;
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
public class BoxSpeedTest extends TestBase implements WindowListener {
    public static void main(String[] args) {
        // Log.addLogListener(new LogPrinter());
        try {
            new BoxSpeedTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    CameraNode cam1;

    public BoxSpeedTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode polyDuck = ColladaLoader.load(new File("data/meshes/box.dae"));
        Material mat = ShaderMaterial.makePhongShaderMaterial();
        Geometry geo = Finder.findGeometry(polyDuck, null);

        for (int i = 0; i < 1000; i++)
            root.addChildNode(new ShapeNode("box", geo, mat));

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 5));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.setViewFrustumCullingMode(0);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        for (int i = 0; i < 1; i++)
            // multi pass test
            p.drawGeometry("AMBIENT", null);

        // p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////
        RenderWindow w = new NewtRenderWindow(p, 640, 480);
        w.addKeyListener(this);
        w.addMouseListener(this);
        w.addWindowListener(this);

        Viewer viewer = new Viewer(false, w);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                move(System.currentTimeMillis() - start, 0.1);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void windowClose(RenderWindow win) {
        System.err.println("Closing: " + win);

    }

    @Override
    public void windowReshape(RenderWindow win, int width, int height) {
        System.err.println("Reshaping: " + win);
        cam1.setAspectRatio((float) width / height);
    }
}
