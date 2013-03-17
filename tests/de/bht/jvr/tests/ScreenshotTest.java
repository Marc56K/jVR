package de.bht.jvr.tests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.renderer.NewtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.renderer.WindowListener;
import de.bht.jvr.util.Color;

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
 * Press P to take a screenshot.
 * 
 * @author Marc Roßbach
 *
 */

public class ScreenshotTest extends TestBase implements WindowListener {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new ScreenshotTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    CameraNode cam1;

    private RenderWindow win;
    public ScreenshotTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode model = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        root.addChildNode(model);

        PointLightNode pLight = new PointLightNode();
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 1, 3));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, new Color(0.2f, 0.5f, 0.7f, 0.5f));
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////
        win = new NewtRenderWindow(p, false);
        win.addKeyListener(this);
        win.addMouseListener(this);
        win.addWindowListener(this);
        win.setWindowTitle("Press P to take screenshot");
        //win.setVSync(true);
        Viewer viewer = new Viewer(win);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();          
                move(System.currentTimeMillis() - start, 0.001);
            }
            viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void windowClose(RenderWindow win) {
    }

    @Override
    public void windowReshape(RenderWindow win, int width, int height) {
        cam1.setAspectRatio((float) width / height);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        if (Character.toUpperCase((char) e.getKeyCode()) == 'P')
            win.TakeScreenshotOfNextFrame(new File("shot_"+System.currentTimeMillis()+".png"));
    }
}
