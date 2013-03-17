package de.bht.jvr.examples.input;

import de.bht.jvr.util.Color;
import java.awt.Point;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.KeyListener;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.input.MouseListener;
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
 * This basic sample demonstrates the following features:
 * - MouseListener
 * - KeyListener
 * 
 * @author Marc Roßbach
 */

public class MouseKeyboardNavigationExample implements KeyListener, MouseListener {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new MouseKeyboardNavigationExample().start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected Set<Character> pressedKeys = Collections.synchronizedSet(new HashSet<Character>()); // HashSet with all pressed keys
    protected boolean mouseDragged = false; // is the mouse in dragging mode?
    protected Point mousePos = new Point(); // cursor position
    protected float rotX = 0; // rotation of the camera (x-axis)
    protected float rotY = 0; // rotation of the camera (y-axis)
    protected CameraNode cam; // camera node

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(Character.toUpperCase((char) e.getKeyCode())); // save pressed key
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(Character.toUpperCase((char) e.getKeyCode())); // remove pressed key
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * generate a scene and a pipeline
     * 
     * @return
     * @throws Exception
     */
    public Pipeline makePipeline() throws Exception {
        // load a scene from a collada file
        SceneNode house = ColladaLoader.load(new File("data/meshes/house.dae"));
        house.setTransform(Transform.scale(0.1f));

        // we also need some light
        DirectionalLightNode light = new DirectionalLightNode("MySun");
        light.setTransform(Transform.rotateXDeg(-30)); // transform the light

        // and a camera (aspect ration: 4:3 and field of view 60ß)
        cam = new CameraNode("MyCamera", 4f / 3f, 60);
        cam.setTransform(Transform.translate(0, 12, 20)); // start position

        // now generate the scene graph
        GroupNode root = new GroupNode("MyRoot");
        root.addChildNodes(house, light, cam);

        // to render the scene we need a rendering pipeline
        Pipeline p = new Pipeline(root);
        p.clearBuffers(true, true, Color.gray); // clear the depth and the color buffer and set the clear color to gray
        p.switchCamera(cam); // because a scene can have more than one camera
        p.drawGeometry("AMBIENT", null); // first we have to render the ambient pass (null = all material classes)
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null); // now we have to render the lighting pass for every active light in the scene

        return p;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        rotY += (mousePos.getX() - e.getX()) / 200; // rotate camera (y-axis)
        rotX += (mousePos.getY() - e.getY()) / 200; // rotate camera (x-axis)
        mousePos.setLocation(e.getX(), e.getY()); // save mouse position
        mouseDragged = true; // activate dragging mode
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePos.setLocation(e.getX(), e.getY()); // save mouse position
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDragged = false; // disable dragging mode
    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void move(double renderDuration) {
        renderDuration *= 0.01; // slow down

        synchronized (pressedKeys) {
            if (mouseDragged || !pressedKeys.isEmpty()) {
                float[] pos = cam.getTransform().getMatrix().translation().getData(); // extract
                                                                                      // camera
                                                                                      // translation
                for (Character key : pressedKeys)
                    switch (key) {
                    case 'W': // Move forward
                        pos[0] -= Math.sin(rotY) * Math.cos(-rotX) * renderDuration;
                        pos[1] -= Math.sin(-rotX) * renderDuration;
                        pos[2] -= Math.cos(rotY) * Math.cos(-rotX) * renderDuration;
                        break;
                    case 'S': // Move backward
                        pos[0] += Math.sin(rotY) * Math.cos(-rotX) * renderDuration;
                        pos[1] += Math.sin(-rotX) * renderDuration;
                        pos[2] += Math.cos(rotY) * Math.cos(-rotX) * renderDuration;
                        break;
                    case 'A': // Strafe left
                        pos[0] += Math.sin(rotY - Math.PI / 2) * renderDuration;
                        pos[2] += Math.cos(rotY - Math.PI / 2) * renderDuration;
                        break;
                    case 'D': // Strafe right
                        pos[0] += Math.sin(rotY + Math.PI / 2) * renderDuration;
                        pos[2] += Math.cos(rotY + Math.PI / 2) * renderDuration;
                        break;
                    case 'Q': // exit program
                        System.exit(0);
                        break;
                    }

                // set the new camera transformation (Transformation = Translation * RotationY * RotationX)
                cam.setTransform(Transform.translate(new Vector3(pos)).mul(Transform.rotateY(rotY)).mul(Transform.rotateX(rotX)));
            }
        }
    }

    public void start() throws Exception {
        // load a scene and generate the pipeline
        Pipeline p = makePipeline();

        // create a render window to render the pipeline
        RenderWindow win = new AwtRenderWindow(p, 800, 600);
        win.addKeyListener(this); // set the key listener for the window
        win.addMouseListener(this); // set the mouse listener for the window

        // create a viewer
        Viewer v = new Viewer(win); // the viewer manages all render windows

        // main loop
        while (v.isRunning()) {
            long start = System.currentTimeMillis(); // save system time before rendering
            v.display(); // render the scene
            double delta = System.currentTimeMillis() - start; // calculate render duration
            move(delta);
        }
    }
}
