package de.bht.jvr.examples.input;

import de.bht.jvr.util.Color;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.KeyListener;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.input.MouseListener;
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
 * - MouseListener
 * - KeyListener
 * 
 * @author Marc Roßbach
 * @author Henrik Tramberend
 */

public class MouseKeyboardExample implements KeyListener, MouseListener {
    /**
     * The main entry point.
     * 
     * @param args
     *            ignored
     */
    public static void main(String[] args) {
        try {
            new MouseKeyboardExample();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // HashSet with all pressed keys.
    private Set<Character> pressedKeys = Collections.synchronizedSet(new HashSet<Character>());

    // We want to manipulate this teapot with mouse and keyboard.
    private SceneNode teapot;

    // Rotation angle of the teapot.
    private float rotationAngle = 0;

    // Teapot rotation speed in degrees per second.
    private float speed = 90;

    /**
     * Construct the example application object and run the simulation and
     * rendering loop.
     * 
     * @throws Exception
     */
    public MouseKeyboardExample() throws Exception {
        // Load a scene and generate the pipeline.
        Pipeline pipeline = makePipeline();

        // Create a render window to render the pipeline.
        RenderWindow win = new AwtRenderWindow(pipeline, 800, 600);

        // Set the key listener for the window.
        win.addKeyListener(this);

        // Set the mouse listener for the window.
        win.addMouseListener(this);

        // Create a viewer. The viewer manages all render windows.
        Viewer v = new Viewer(win);

        // Save system time before entering the loop.
        long start = System.nanoTime();

        // Main simulation and rendering loop.
        while (v.isRunning()) {

            // Calculate frame duration in seconds.
            long now = System.nanoTime();
            double delta = (now - start) * 1e-9;
            start = now;

            // perform simulation
            simulate(delta);

            // Render the scene.
            v.display();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Add key character to the set of ptressed keys.
        pressedKeys.add(Character.toUpperCase((char) e.getKeyCode()));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Remove key character from the set of ptressed keys.
        pressedKeys.remove(Character.toUpperCase((char) e.getKeyCode()));
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Generate a scene and a pipeline.
     * 
     * @return
     * @throws Exception
     */
    public Pipeline makePipeline() throws Exception {

        // Load the teapot model from a collada file.
        teapot = ColladaLoader.load(new File("data/meshes/teapot.dae"));

        // We also need some light.
        PointLightNode light = new PointLightNode("MyPointLight");
        light.setTransform(Transform.translate(1, 5, 2));

        // And a camera (aspect ration: 4:3 and field of view 60 degrees).
        CameraNode camera = new CameraNode("MyCamera", 4f / 3f, 60);
        camera.setTransform(Transform.translate(0, 1, 3));

        // Now generate the scene graph.
        GroupNode root = new GroupNode("MyRoot");
        root.addChildNodes(teapot, light, camera);

        // To render the scene we need a rendering pipeline.
        Pipeline pipeline = new Pipeline(root);

        // Clear the depth and the color buffer and set the clear color to
        // black.
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));

        // Select which camera to use because a scene can have more than one
        // camera.
        pipeline.switchCamera(camera);

        // First we have to render the ambient pass (null = all material classes)
        pipeline.drawGeometry("AMBIENT", null);

        // Now we have to render the lighting pass for every active light in the scene.
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        return pipeline;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
        // Left mouse button pressed.
        case 1:
            pressedKeys.add(new Character('E'));
            break;
        // Right mouse button pressed.
        case 3:
            pressedKeys.add(new Character('R'));
            break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (e.getButton()) {
        // Left mouse button released.
        case 1:
            pressedKeys.remove(new Character('E'));
            break;
        // Right mouse button released.
        case 3:
            pressedKeys.remove(new Character('R'));
            break;
        }
    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {

    }

    /**
     * Simulate the world for one frame. As a result a new transformation for
     * the teapot object is calculated. Takes the current input state into
     * account.
     * 
     * @param elapsed
     *            Duration of time to simulate.
     */
    private void simulate(double elapsed) {
        synchronized (pressedKeys) {

            // Advance the rotation angle depending on keys pressed.
            if (pressedKeys.contains('E'))
                rotationAngle += elapsed * speed;
            if (pressedKeys.contains('R'))
                rotationAngle -= elapsed * speed;

            // Exit the application.
            if (pressedKeys.contains('Q'))
                System.exit(0);

            // Set the the teapot transformation based on the current rotation
            // angle.
            teapot.setTransform(Transform.rotateYDeg(rotationAngle));
        }
    }
}
