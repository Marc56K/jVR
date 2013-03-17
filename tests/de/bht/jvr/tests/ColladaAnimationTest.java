package de.bht.jvr.tests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Animator;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
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
 * Test loading of collada animations
 * 
 * @author Marc Roßbach
 *
 */

public class ColladaAnimationTest extends TestBase implements WindowListener {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new ColladaAnimationTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    CameraNode cam1;

    private RenderWindow win;
    public ColladaAnimationTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode model = ColladaLoader.load(new File("data/meshes/animation.dae"));
        //Finder.find(model, ShapeNode.class, null).setMaterial(ShaderMaterial.makePhongShaderMaterial());
        Printer.print(model);
        root.addChildNode(model);

        PointLightNode pLight = new PointLightNode();
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 3));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, new Color(0.2f, 0.5f, 0.7f, 0.5f));
        p.setBackFaceCulling(false);
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////
        win = new NewtRenderWindow(p, false);
        win.addKeyListener(this);
        win.addMouseListener(this);
        win.addWindowListener(this);
        win.setVSync(true);
        Viewer viewer = new Viewer(win);

        try {
            long startTime = System.nanoTime();
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                
                viewer.display();
                
                Animator.animate(model, (float) ((System.nanoTime() - startTime) / 1e+9) % 3.3f);
                
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
}
