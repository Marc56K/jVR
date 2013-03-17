package de.bht.jvr.tests;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.KeyListener;
import de.bht.jvr.renderer.AwtRenderWindow;
import de.bht.jvr.renderer.NewtRenderWindow;
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
 */
public class KeyEventTest implements KeyListener {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new KeyEventTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public KeyEventTest() throws Exception {
        RenderWindow win1 = new NewtRenderWindow(new Pipeline(new GroupNode()));
        RenderWindow win2 = new AwtRenderWindow(new Pipeline(new GroupNode()));
        win1.addKeyListener(this);
        win2.addKeyListener(this);
        win2.setPosition(700, 0);

        Viewer v = new Viewer(win1, win2);
        while (v.isRunning())
            v.display();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.err.println("keyPressed: " + e.getKeyChar() + " " + e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        System.err.println("keyReleased: " + e.getKeyChar() + " " + e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.err.println("keyTyped: " + e.getKeyChar() + " " + e.getKeyCode());
    }

}
