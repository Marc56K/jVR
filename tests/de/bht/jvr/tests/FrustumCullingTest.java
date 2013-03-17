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
import de.bht.jvr.core.pipeline.PipelineCommandPtr;
import de.bht.jvr.input.KeyEvent;
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
 * Press E and R to switch between VFC modes.
 * 
 * @author Marc
 */

public class FrustumCullingTest extends TestBase {
    public static void main(String[] args) {
        // Log.addLogListener(new LogPrinter());
        try {
            new FrustumCullingTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private PipelineCommandPtr vfc;

    public FrustumCullingTest() throws Exception {
        int k = 5;
        GroupNode root = new GroupNode();
        SceneNode teapot = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        teapot.setTransform(Transform.scale(10));
        for (int x = -k; x < k; x++)
            for (int y = -k; y < k; y++)
                for (int z = -k; z < k; z++) {
                    GroupNode n = new GroupNode();
                    n.setTransform(Transform.translate(x * 50, y * 50, z * 50));
                    n.addChildNode(teapot);
                    root.addChildNode(n);
                }
        // root.addChildNode(teapot);
        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        CameraNode cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 0));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        vfc = p.setViewFrustumCullingMode(0);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////
        RenderWindow w = new AwtRenderWindow(p, 1024, 768);
        w.addKeyListener(this);
        w.addMouseListener(this);
        w.setVSync(false);
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

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        switch (new Character((char) e.getKeyCode())) {
        case 'E':
            System.out.println("DL-VFC");
            vfc.setViewFrustumCullingMode(1);
            break;
        case 'R':
            System.out.println("H-VFC");
            vfc.setViewFrustumCullingMode(2);
            break;
        }
    }
}
