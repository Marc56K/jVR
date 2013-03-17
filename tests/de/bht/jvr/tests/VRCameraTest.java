package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.VRCameraNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector4;
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
 */
public class VRCameraTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(40, 0, 0));
        try {
            new VRCameraTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    float z = 0;

    public VRCameraTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode teapot = ColladaLoader.load(new File("data/meshes/box.dae"));
        teapot.setTransform(Transform.translate(0, 0, -4.5f));
        root.addChildNode(teapot);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        VRCameraNode cam1 = new VRCameraNode("cam", Transform.translate(0, 0, -4), new Vector4(-2, 2, 1.5f, -1.5f), true, new Transform());
        // cam1.setTransform(Transform.translate(1, 1, 0));
        cam1.setEyeSeparation(0);
        cam1.setLeftEye(false);
        root.addChildNode(cam1);
        // this.cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, Color.gray);
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////

        RenderWindow w = new AwtRenderWindow(p, 800, 600);
        w.addKeyListener(this);
        w.addMouseListener(this);

        Viewer viewer = new Viewer(w);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                float x = (float) (mousePos.x - 400) / 200;
                float y = (float) (mousePos.y - 300) / -150;
                cam1.setHeadTransform(Transform.translate(x, y, 0).mul(Transform.rotateZ(z)));
                // cam1.setHeadTransform(Transform.rotateZ(z));
                // System.out.println("x:"+x+" y:"+y+" z:"+z);
                viewer.display();
                move(System.currentTimeMillis() - start);
            }
            viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void move(long renderDuration) {
        super.move(renderDuration);

        double d = renderDuration / 10.;
        synchronized (pressedKeys) {
            for (Character key : pressedKeys)
                switch (key) {
                case 'E':
                    z += 0.1f * d;
                    break;
                case 'R':
                    z -= 0.1f * d;
                    break;
                }
        }
    }
}
