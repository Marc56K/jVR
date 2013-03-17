package de.bht.jvr.applicationbase;

import de.bht.jvr.util.Color;
import java.awt.Point;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.bht.jvr.core.CameraNode;
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
 * This class provides a simple mouse keyboard navigation.
 * 
 * @author Marc Roßbach
 */

public abstract class AbstractMouseKeyboardApp implements KeyListener, MouseListener {
    protected Set<Character> pressedKeys = Collections.synchronizedSet(new HashSet<Character>()); // HashSet
                                                                                                  // with
                                                                                                  // all
                                                                                                  // pressed
                                                                                                  // keys
    protected boolean mouseDragged = false; // is the mouse in dragging mode?
    protected Point mousePos = new Point(); // cursor position
    protected float rotX = 0; // rotation of the camera (x-axis)
    protected float rotY = 0; // rotation of the camera (y-axis)
    protected double moveSpeed = 1;
    protected CameraNode cam = null;

    public abstract CameraNode getCamera() throws Exception;

    public abstract Pipeline getPipeline(SceneNode root, CameraNode cam) throws Exception;

    public abstract RenderWindow getRenderWindow(Pipeline pipeline) throws Exception;

    public abstract GroupNode getSceneGraph() throws Exception;

    public abstract void init() throws Exception;

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(Character.toUpperCase((char) e.getKeyCode())); // save
                                                                       // pressed
                                                                       // key
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(Character.toUpperCase((char) e.getKeyCode())); // remove
                                                                          // pressed
                                                                          // key
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
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

    public void move(double renderDuration) throws Exception {
        renderDuration *= 0.01; // slow down
        renderDuration *= moveSpeed;

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

                // set the new camera transformation (Transformation =
                // Translation * RotationY * RotationX)
                cam.setTransform(Transform.translate(new Vector3(pos)).mul(Transform.rotateY(rotY)).mul(Transform.rotateX(rotX)));
            }
        }
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public abstract void simulate(double renderDuration) throws Exception;

    public void start() throws Exception {
        init();

        GroupNode root = getSceneGraph();
        if (root == null)
            root = new GroupNode();

        cam = getCamera();
        if (cam == null) {
            cam = new CameraNode("cam", 4f / 3f, 60f);
            cam.setTransform(Transform.translate(0, 0, 5));
            root.addChildNode(cam);
        }

        Pipeline p = getPipeline(root, cam);
        if (p == null) {
            p = new Pipeline(root);
            p.switchCamera(cam);
            p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
            p.drawGeometry("AMBIENT", null);
            p.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        }

        RenderWindow win = getRenderWindow(p);
        if (win == null)
            win = new AwtRenderWindow(p);

        win.addKeyListener(this);
        win.addMouseListener(this);

        Viewer v = new Viewer(win);

        double delta = 0;
        while (v.isRunning()) {
            double start = System.nanoTime();
            v.display();
            move(delta);
            simulate(delta);
            delta = (System.nanoTime() - start) / 1000000;
        }

    }
}
