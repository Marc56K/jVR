package de.bht.jvr.examples.input;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Picker;
import de.bht.jvr.core.PickingResult;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.math.Vector2;
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
 * - picking
 * 
 * @author Marc Roßbach
 */

public class PickingExample extends MouseKeyboardNavigationExample {
    public static void main(String[] args) {
        // Log.addLogListener(new LogPrinter());
        try {
            new PickingExample();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Vector2 mousePosition = new Vector2(0, 0);
    private boolean mouseClick = false;
    private SceneNode cursor;
    private GroupNode root;

    public PickingExample() throws Exception {
        root = new GroupNode("root");

        SceneNode world = ColladaLoader.load(new File("data/meshes/testwelt01.dae"));
        world.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.1f)));
        root.addChildNode(world);

        cursor = ColladaLoader.load(new File("data/meshes/sphere.dae"));
        Finder.find(cursor, ShapeNode.class, null).setName("MyCursor");
        root.addChildNode(cursor);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        cam = new CameraNode("cam", 4f / 3f, 60f);
        cam.setTransform(Transform.translate(0, 2, 0));
        root.addChildNode(cam);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.setViewFrustumCullingMode(2);
        p.switchCamera(cam);
        p.clearBuffers(true, true, new Color(0.5f, 0.7f, 1.0f));
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////
        RenderWindow w = new AwtRenderWindow(p, 1024, 768);
        w.addKeyListener(this);
        w.addMouseListener(this);

        Viewer viewer = new Viewer(w);

        // main loop
        double delta = 0;
        while (viewer.isRunning()) {
            long start = System.currentTimeMillis(); // save system time before rendering
            move(delta); // navigation
            updatePicking(); // picking
            viewer.display(); // render the scene
            delta = System.currentTimeMillis() - start; // calculate render duration

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseClick = true;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        mousePosition = new Vector2(e.getNormalizedX(), e.getNormalizedY());
    }

    public void updatePicking() {
        // perform picking and ignore the trees and the cursor
        PickingResult result = Picker.pickShapeNode(root, cam, mousePosition.x(), mousePosition.y(), "(?!MyCursor.*).*");

        // the picking result contains the picked shape node and the picking point in world space
        if (result.getPickingPoint() != null)
            // move the cursor to the picking point
            cursor.setTransform(Transform.translate(result.getPickingPoint()).mul(Transform.scale(0.5f)));

        // print the picked shape node on mouse click
        if (mouseClick && result.getShapeNode() != null) {
            System.out.println("Path to selected shape node: " + result.getNodePath());
            System.out.println("Selected shape node: " + result.getShapeNode());
            mouseClick = false;
        }
    }
}
