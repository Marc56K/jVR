package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.VRCameraNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector4;
import de.bht.jvr.qtm.QTMRigidBodyTracking;
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
public class CaveHeadTrackingTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(40, 0, 0));
        try {
            new CaveHeadTrackingTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    float z = 0;

    public CaveHeadTrackingTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode teapot = ColladaLoader.load(new File("data/meshes/testwelt01.dae"));
        teapot.setTransform(Transform.translate(0, -1, -1.67f).mul(Transform.scale(0.1f)).mul(Transform.rotateXDeg(-90)));
        root.addChildNode(teapot);

        DirectionalLightNode dLight = new DirectionalLightNode();
        dLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        dLight.setIntensity(1.5f);
        dLight.setTransform(Transform.rotateYDeg(-60).mul(Transform.rotateXDeg(-40)));
        root.addChildNode(dLight);

        VRCameraNode cam1 = new VRCameraNode("cam", Transform.translate(0, 0, -1.17f), new Vector4(-1.865f, 1.865f, 1.165f, -1.165f), true, new Transform());
        cam1.setLeftEye(true);
        root.addChildNode(cam1);
        cams.add(cam1);

        VRCameraNode cam2 = new VRCameraNode("cam", Transform.translate(0, 0, -1.17f), new Vector4(-1.865f, 1.865f, 1.165f, -1.165f), true, new Transform());
        cam2.setLeftEye(false);
        root.addChildNode(cam2);
        cams.add(cam2);

        // Pipeline//////////////////////////////////////
        Pipeline p1 = new Pipeline(root);
        p1.switchCamera(cam1);
        p1.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p1.drawGeometry("AMBIENT", null);
        p1.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        Pipeline p2 = new Pipeline(root);
        p2.switchCamera(cam2);
        p2.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p2.drawGeometry("AMBIENT", null);
        p2.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////

        RenderWindow w1 = new NewtRenderWindow(p1, true);
        w1.addKeyListener(this);
        w1.addMouseListener(this);
        w1.setScreenDevice(1);
        w1.setFSAA(2);
        w1.setVSync(false);

        RenderWindow w2 = new NewtRenderWindow(p2, true);
        w2.addKeyListener(this);
        w2.addMouseListener(this);
        w2.setScreenDevice(0);
        w2.setFSAA(2);
        w2.setVSync(false);

        Viewer viewer = new Viewer(w1, w2);

        try {
            QTMRigidBodyTracking tracker = new QTMRigidBodyTracking();
            // tracker.connect("192.168.0.1", 22222);

            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                Transform headTrans = new Transform(tracker.getRigidBodyMatrix(0));

                cam1.setHeadTransform(headTrans);
                cam2.setHeadTransform(headTrans);
                viewer.display();
                move((System.currentTimeMillis() - start), 0.01);
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
