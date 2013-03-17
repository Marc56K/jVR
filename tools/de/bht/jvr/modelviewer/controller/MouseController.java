package de.bht.jvr.modelviewer.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import de.bht.jvr.math.Vector2;
import de.bht.jvr.modelviewer.model.Scene;

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

public class MouseController implements MouseListener, MouseMotionListener {
    private Scene scene;
    private Vector2 mousePos = new Vector2(0, 0);
    private int mouseButton = 0;

    public MouseController(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        Vector2 pos = new Vector2(arg0.getX(), arg0.getY());
        Vector2 deltaPos = mousePos.sub(pos).mul(0.5f);
        switch (mouseButton) {
        case MouseEvent.BUTTON1:
            scene.updateRotation(deltaPos);
            break;
        case MouseEvent.BUTTON3:
            scene.updateZoom(deltaPos);
            break;
        }
        mousePos = pos;
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
    public void mousePressed(MouseEvent arg0) {
        mouseButton = arg0.getButton();
        mousePos = new Vector2(arg0.getX(), arg0.getY());
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

    }

}
