package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Picker;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.math.Vector2;
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
 * Press E to activate view frustum culling, and R to deactivate it.
 * 
 * @author Marc
 */

public class PickingTest extends TestBase {
    public static void main(String[] args) {
        // Log.addLogListener(new LogPrinter());
        try {
            new PickingTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CameraNode cam1;
    private SceneNode sphere;
    private GroupNode root;
    private GroupNode subroot;
    private Vector2 mousePosition = new Vector2(0, 0);

    public PickingTest() throws Exception {
        root = new GroupNode("root");
        subroot = new GroupNode("subroot");
        root.addChildNode(subroot);

        SceneNode world = ColladaLoader.load(new File("data/meshes/testwelt01.dae"));
        world.setTransform(Transform.rotateXDeg(-90));
        subroot.addChildNode(world);

        sphere = ColladaLoader.load(new File("data/meshes/sphere.dae"));
        root.addChildNode(sphere);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        subroot.addChildNode(pLight);

        cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 0));
        subroot.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.setViewFrustumCullingMode(2);
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

                synchronized (this) {
                    Vector3 pickPoint = Picker.pickShapeNode(subroot, cam1, mousePosition.x(), mousePosition.y(), "(?!Pflanze.*).*").getPickingPoint();
                    if (pickPoint != null)
                        sphere.setTransform(Transform.translate(pickPoint).mul(Transform.scale(2f)));
                }

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
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        mousePosition = new Vector2(e.getNormalizedX(), e.getNormalizedY());

    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() == 1) {
            // TODO this is not thread safe
            SceneNode node = Picker.pickShapeNode(subroot, cam1, e.getNormalizedX(), e.getNormalizedY(), null).getShapeNode();
            if (node != null)
                node.setEnabled(false);
        }
    }
}
