package de.bht.jvr.modelviewer.view;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.awt.GLCanvas;

import de.bht.jvr.modelviewer.controller.FileOpenController;
import de.bht.jvr.modelviewer.controller.MouseController;
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

public class GuiElementFactory {
    public static GLCanvas makeGLCanvas(Scene scene) {
        GLCanvas glCanvas = new GLCanvas();
        MouseController mouseController = new MouseController(scene);
        glCanvas.addGLEventListener(new Renderer(scene));
        glCanvas.addMouseListener(mouseController);
        glCanvas.addMouseMotionListener(mouseController);

        return glCanvas;
    }

    public static MenuBar makeMenuBar(Scene scene) {
        MenuBar mb = new MenuBar();

        // file
        Menu file = new Menu("File");
        mb.add(file);

        // open
        MenuItem open = new MenuItem("Open");
        file.add(open);
        open.addActionListener(new FileOpenController(scene));

        // ----
        file.addSeparator();

        // exit
        MenuItem exit = new MenuItem("Exit");
        file.add(exit);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });

        return mb;
    }

    public static SceneGraphTree makeSceneGraphTree(Scene scene) {
        SceneGraphTree tree = new SceneGraphTree(scene);
        scene.addSceneListener(tree);
        return tree;
    }
}
