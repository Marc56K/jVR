package de.bht.jvr.examples.physics;

import de.bht.jvr.util.Color;
import java.io.File;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PickRay;
import de.bht.jvr.core.Picker;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.DefaultInputListener;
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
 * - jBullet integration
 * - jBullet picking Dependencies: MyMotionState, MyPhysics
 * 
 * @author Marc Roßbach
 */

public class SimplePhysicsExample extends DefaultInputListener {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new SimplePhysicsExample();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CameraNode cam;
    private GroupNode root;
    private MyPhysics physics;
    private Vector2 mousePos = new Vector2(0, 0);
    private boolean mousePressed = false;

    public SimplePhysicsExample() throws Exception {
        // init physics
        physics = new MyPhysics();

        // load a plane from collada file
        SceneNode plane = ColladaLoader.load(new File("data/meshes/plane.dae"));
        plane.setTransform(Transform.scale(100).mul(Transform.rotateXDeg(-90))); // rotate and scale the plane
        physics.addGroundPlane(0);

        // create boxes
        GroupNode boxes = new GroupNode();
        SceneNode box = ColladaLoader.load(new File("data/meshes/box.dae"));
        for (int x = -2; x < 3; x++)
            for (int y = 2; y < 6; y++) {
                GroupNode boxRoot = new GroupNode();
                boxRoot.addChildNode(box);
                boxRoot.setTransform(Transform.translate(x * 1.2f, y * 1.2f, -5)); // set start transformation for this box
                physics.addRigidBody(boxRoot, new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)), 1); // add the box to physics
                boxes.addChildNode(boxRoot);
            }

        // create teapot
        SceneNode teapot = ColladaLoader.load(new File("data/meshes/teapot_low.dae"));
        ShapeNode teapotShape = Finder.find(teapot, ShapeNode.class, null);
        teapotShape.setTransform(Transform.rotateXDeg(-90));
        GroupNode teapotRoot = new GroupNode();
        teapotRoot.addChildNode(teapotShape);
        teapotRoot.setTransform(Transform.translate(0, 5, -3).mul(Transform.rotateZDeg(45)));
        GImpactMeshShape teapotColShape = MyPhysics.makeGImpactMeshShape(teapotShape);
        physics.addRigidBody(teapotRoot, teapotColShape, 1);
        boxes.addChildNode(teapotRoot);

        // we also need some light
        PointLightNode light = new PointLightNode("MyPointLight");
        light.setTransform(Transform.translate(1, 5, 2)); // translate the light

        // and a camera (aspect ration: 4:3 and field of view 60ß)
        cam = new CameraNode("MyCamera", 4f / 3f, 60);
        cam.setTransform(Transform.translate(0, 1, 3));

        // now generate the scene graph
        root = new GroupNode("MyRoot");
        root.addChildNodes(plane, boxes, light, cam);

        // to render the scene we need a rendering pipeline
        Pipeline p = new Pipeline(root);
        p.clearBuffers(true, true, new Color(0, 0, 0)); // clear the depth and the color buffer and set the clear color to black
        p.switchCamera(cam); // because a scene can have more than one camera
        p.drawGeometry("AMBIENT", null); // first we have to render the ambient pass (null = all material classes)
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null); // now we have to render the lighting pass for every active light in the scene

        // create a render window
        RenderWindow win = new AwtRenderWindow(p, 800, 600);
        win.addMouseListener(this);

        // create a viewer
        Viewer v = new Viewer(win); // the viewer manages all render windows

        // main loop
        double delta = 0;
        while (v.isRunning()) {
            long start = System.currentTimeMillis(); // save system time before rendering
            updatePicking();
            physics.update(delta);
            v.display();
            delta = System.currentTimeMillis() - start; // calculate render duration
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePos = new Vector2(e.getNormalizedX(), e.getNormalizedY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
    }

    public void updatePicking() {
        PickRay ray = Picker.getPickRay(root, cam, mousePos.x(), mousePos.y());

        physics.setPickRay(ray);

        if (mousePressed)
            physics.pickOrDrag();
        else
            physics.unPick();
    }
}
