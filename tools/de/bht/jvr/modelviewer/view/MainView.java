package de.bht.jvr.modelviewer.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;

import de.bht.jvr.logger.Log;
import de.bht.jvr.modelviewer.model.Scene;
import de.bht.jvr.modelviewer.model.SceneListener;

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

public class MainView extends JFrame implements SceneListener {
    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        Scene s = new Scene();
        new MainView("jVR-ColladaViewer", s);
    }

    private Scene scene;

    private GLCanvas glCanvas;

    public MainView(String title, Scene scene) {
        setTitle(title);
        this.scene = scene;
        this.scene.addSceneListener(this);

        // init frame
        this.setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(2, 2));

        // init menu bar
        setMenuBar(GuiElementFactory.makeMenuBar(this.scene));

        // init gl canvas
        glCanvas = GuiElementFactory.makeGLCanvas(this.scene);
        this.add(glCanvas, BorderLayout.CENTER);

        // init logger
        Logger logger = new Logger(5);
        this.add(logger, BorderLayout.SOUTH);
        Log.addLogListener(logger);

        // init tree
        JTree tree = GuiElementFactory.makeSceneGraphTree(this.scene);
        JScrollPane treeView = new JScrollPane(tree);
        treeView.setPreferredSize(new Dimension(250, 100));
        this.add(treeView, BorderLayout.EAST);

        setVisible(true);
    }

    @Override
    public void dispose() {
        System.exit(0);
    }

    @Override
    public void sceneUpdated() {
        glCanvas.display();
    }
}
