package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.ClipPlaneNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformBool;
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
public class MirrorTest extends TestBase {
    public static void main(String[] args) {
        // Log.addLogListener(new LogPrinter());
        try {
            new MirrorTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public MirrorTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode scene = ColladaLoader.load(new File("data/meshes/testwelt01.dae"));
        scene.setTransform(Transform.rotateXDeg(-90));
        root.addChildNode(scene);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        // mirror plane
        SceneNode mirrorPlane = ColladaLoader.load(new File("data/meshes/plane.dae"));
        mirrorPlane.setTransform(Transform.translate(0, -30, 0).mul(Transform.rotateXDeg(-90).mul(Transform.scale(10000))));
        root.addChildNode(mirrorPlane);

        // clipping plane
        ClipPlaneNode clipPlane = new ClipPlaneNode();
        clipPlane.setTransform(Transform.translate(0, -30, 0).mul(Transform.rotateXDeg(90)));
        root.addChildNode(clipPlane);

        // camera
        CameraNode cam = new CameraNode("cam1", 4f / 3f, 60f);
        cam.setTransform(Transform.translate(0, 0, 200));
        root.addChildNode(cam);
        cams.add(cam);

        // mirror camera
        CameraNode mirrorCam = new CameraNode("cam2", -4f / 3f, 60f);
        mirrorCam.setTransform(Transform.translate(0, 0, 0));
        root.addChildNode(mirrorCam);

        // mirror material
        ShaderProgram prog = new ShaderProgram(new File("data/shader/simple_mirror.vs"), new File("data/shader/simple_mirror.fs"));
        ShaderMaterial mat = new ShaderMaterial("AMBIENT", prog);
        mat.setMaterialClass("MirrorClass");

        ShapeNode mirrorShape = Finder.find(mirrorPlane, ShapeNode.class, null);
        mirrorShape.setMaterial(mat);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.createFrameBufferObject("FBO", false, 1, 1.0f, 0);
        // render to texture
        p.switchFrameBufferObject("FBO");
        p.switchCamera(mirrorCam);
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p.drawGeometry("AMBIENT", "(?!MirrorClass).*");
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "(?!MirrorClass).*");
        // render to screen
        p.switchFrameBufferObject(null);
        p.switchCamera(cam);
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p.setUniform("jvr_UseClipPlane0", new UniformBool(false)); // disable
                                                                   // clipping
        p.drawGeometry("AMBIENT", "(?!MirrorClass).*");
        Pipeline lp = p.doLightLoop(true, true);
        // lp.setUniform("jvr_UseClipPlane0", new UniformBool(false)); //
        // disable clipping
        lp.drawGeometry("LIGHTING", "(?!MirrorClass).*");
        // render mirror plane
        p.bindColorBuffer("jvr_MirrorTexture", "FBO", 0);
        p.drawGeometry("AMBIENT", "MirrorClass");
        // /////////////////////////////////////////////
        RenderWindow w = new NewtRenderWindow(p, 1024, 768);
        w.addKeyListener(this);
        w.addMouseListener(this);

        Viewer viewer = new Viewer(w);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();

                // update mirror camera transformation
                Transform camTrans = cam.getTransform();
                camTrans = clipPlane.getTransform().invert().mul(camTrans); // transform
                                                                            // to
                                                                            // clip
                                                                            // plane
                                                                            // space
                camTrans = clipPlane.getTransform().mul(Transform.scale(1, 1, -1).mul(camTrans));
                mirrorCam.setTransform(camTrans);

                viewer.display();
                move(System.currentTimeMillis() - start, 0.1);
            }
            viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
