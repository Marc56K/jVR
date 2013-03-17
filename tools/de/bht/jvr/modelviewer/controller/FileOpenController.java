package de.bht.jvr.modelviewer.controller;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class FileOpenController implements ActionListener {
    private Scene scene;

    public FileOpenController(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        Frame f = new Frame();
        FileDialog fileDialog = new FileDialog(f);
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setTitle("Select Collada-File");
        fileDialog.setVisible(true);

        if (fileDialog.getFile() != null)
            try {
                scene.loadScene(fileDialog.getDirectory() + fileDialog.getFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
