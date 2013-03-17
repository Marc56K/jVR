package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;
import java.io.IOException;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.VRCameraNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformFloat;
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
public class CaveWiimoteTest extends TestBaseWii {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, 0, -1));
        try {
            new CaveWiimoteTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private float intensity = 1.0f;

    public CaveWiimoteTest() throws Exception {
        super.connectWiimote();

        GroupNode root = new GroupNode();

        SceneNode scene = ColladaLoader.load(new File("data/meshes/testwelt01.dae"));

        // extract ground
        ShaderMaterial groundMaterial = (ShaderMaterial) Finder.findMaterial(scene, "Quader01.*");
        groundMaterial.setMaterialClass("ground");

        // scene.setTransform(Transform.rotateXDeg(270).mul(Transform.scale(0.1f)));
        // scene.getTransform().scale(50.0f);
        scene.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.1f)));
        root.addChildNode(scene);

        // Printer.print(root);

        // create spot light
        SpotLightNode sLight = new SpotLightNode();
        sLight.setTransform(Transform.translate(-50, 50, 40).mul(Transform.rotateYDeg(-20)).mul(Transform.rotateXDeg(-45)));
        sLight.setIntensity(1.5f);
        sLight.setSpotCutOff(50);
        sLight.setCastShadow(true);
        sLight.setShadowBias(0.5f);
        root.addChildNode(sLight);

        VRCameraNode cam1 = new VRCameraNode("cam", Transform.translate(0, 0, -1.17f), new Vector4(-1.865f, 1.865f, 1.165f, -1.165f), true, new Transform());
        cam1.setLeftEye(true);
        root.addChildNode(cam1);
        cams.add(cam1);

        VRCameraNode cam2 = new VRCameraNode("cam", Transform.translate(0, 0, -1.17f), new Vector4(-1.865f, 1.865f, 1.165f, -1.165f), true, new Transform());
        cam2.setLeftEye(false);
        root.addChildNode(cam2);
        cams.add(cam2);

        RenderWindow w1 = new NewtRenderWindow(makePipeline(root, sLight, cam1), true);
        w1.setScreenDevice(1);
        w1.setVSync(false);

        RenderWindow w2 = new NewtRenderWindow(makePipeline(root, sLight, cam2), true);
        w2.setScreenDevice(0);
        w2.setVSync(false);

        Viewer viewer = new Viewer(w1, w2);
        QTMRigidBodyTracking tracker = new QTMRigidBodyTracking();
        try {
            tracker.connect("192.168.0.1", 22222);
        } catch (Exception e) {
            Log.error(this.getClass(), e.getMessage());
        }

        while (viewer.isRunning() && run) {
            long start = System.currentTimeMillis();
            if (tracker.isConnected()) {
                Transform headTrans = new Transform(tracker.getRigidBodyMatrix(0));
                cam1.setHeadTransform(headTrans);
                cam2.setHeadTransform(headTrans);
            }
            viewer.display();
            move((System.currentTimeMillis() - start), 0.01);
        }
        viewer.close();

    }

    public Pipeline makePipeline(SceneNode root, LightNode sLight, CameraNode cam) throws IOException {
        ShaderProgram sp = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/dof.fs"));
        ShaderMaterial sm = new ShaderMaterial("DOF", sp);

        Pipeline p = new Pipeline(root);

        p.setBackFaceCulling(false);

        // create fbo for dof
        p.createFrameBufferObject("DOFMap", true, 1, 1.0f, 2);
        // create shadow map buffer
        p.createFrameBufferObject("ShadowMap", true, 0, 1024, 1024, 0);

        // switch to light source as camera
        p.switchLightCamera(sLight);

        // switch to shadow map buffer
        p.switchFrameBufferObject("ShadowMap");
        // clear shadow map buffer
        p.clearBuffers(true, false, null);

        // render to fbo
        p.drawGeometry("AMBIENT", null);

        // ///////////////////////////////

        // switch to camera
        p.switchCamera(cam);

        // switch to fbo
        p.switchFrameBufferObject("DOFMap");
        // clear fbo buffers
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));

        // render to fbo
        p.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");

        p.drawGeometry("AMBIENT", "ground");
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "ground", false);
        // p.doForwardLightLoop("LIGHTING", "ground", false, false);

        p.drawGeometry("AMBIENT", "", true);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "", true);
        // p.doForwardLightLoop("LIGHTING", "", false, true);

        // switch to p-buffer
        p.switchFrameBufferObject(null);
        // clear p-buffer
        p.clearBuffers(true, true, new Color(0, 0, 0));

        // set uniforms & buffers
        p.setUniform("intensity", new UniformFloat(intensity));
        p.bindColorBuffer("jvr_Texture1", "DOFMap", 0);
        p.bindDepthBuffer("jvr_Texture0", "DOFMap");

        // render quad
        p.drawQuad(sm, "DOF");

        return p;
    }
}
