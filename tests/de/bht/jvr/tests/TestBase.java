package de.bht.jvr.tests;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.KeyListener;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.input.MouseListener;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
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
public abstract class TestBase implements KeyListener, MouseListener {
    protected List<SceneNode> cams = new ArrayList<SceneNode>();
    protected boolean run = true;
    protected Set<Character> pressedKeys = Collections.synchronizedSet(new HashSet<Character>());
    protected boolean mouseDragged = false;
    protected Point mousePos = new Point();
    protected float rx = 0;
    protected float ry = 0;

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(Character.toUpperCase((char) e.getKeyCode()));

    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(Character.toUpperCase((char) e.getKeyCode()));

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        ry += (mousePos.getX() - e.getX()) / 200;
        rx += (mousePos.getY() - e.getY()) / 200;
        mousePos.setLocation(e.getX(), e.getY());
        mouseDragged = true;
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePos.setLocation(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        mouseDragged = false;
    }

    @Override
    public void mouseWheelMoved(MouseEvent arg0) {

    }

    protected void move(double renderDuration) {
        synchronized (pressedKeys) {
            if (mouseDragged || !pressedKeys.isEmpty())
                for (SceneNode cam : cams) {
                    float[] pos = cam.getTransform().getMatrix().translation().getData();
                    for (Character key : pressedKeys)
                        switch (key) {
                        case 'W':
                            // Move forward
                            pos[0] -= Math.sin(ry) * Math.cos(-rx) * renderDuration;
                            pos[1] -= Math.sin(-rx) * renderDuration;
                            pos[2] -= Math.cos(ry) * Math.cos(-rx) * renderDuration;
                            break;
                        case 'S':
                            // Move backward
                            pos[0] += Math.sin(ry) * Math.cos(-rx) * renderDuration;
                            pos[1] += Math.sin(-rx) * renderDuration;
                            pos[2] += Math.cos(ry) * Math.cos(-rx) * renderDuration;
                            break;
                        case 'A':
                            // Strafe left
                            pos[0] += Math.sin(ry - Math.PI / 2) * renderDuration;
                            pos[2] += Math.cos(ry - Math.PI / 2) * renderDuration;
                            break;
                        case 'D':
                            // Strafe right
                            pos[0] += Math.sin(ry + Math.PI / 2) * renderDuration;
                            pos[2] += Math.cos(ry + Math.PI / 2) * renderDuration;
                            break;
                        case 'Q':
                            System.exit(0);
                            break;
                        }

                    Matrix4 rotX = Matrix4.rotate(new Vector3(1, 0, 0), rx);
                    Matrix4 rotY = Matrix4.rotate(new Vector3(0, 1, 0), ry);
                    Matrix4 transM = Matrix4.translate(new Vector3(pos)).mul(rotY).mul(rotX);

                    cam.setTransform(new Transform(transM));
                }
        }
    }

    protected void move(double renderDuration, double speed) {
        move(renderDuration * speed);
    }
}
