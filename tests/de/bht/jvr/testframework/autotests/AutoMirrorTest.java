package de.bht.jvr.testframework.autotests;

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
import de.bht.jvr.testframework.AbstractAutoTest;
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
 */

public class AutoMirrorTest extends AbstractAutoTest{

    public AutoMirrorTest() {
        super("AutoMirrorTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
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
        camera = new CameraNode("cam1", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(-50, 0, 500));
        root.addChildNode(camera);

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
        pipeline = new Pipeline(root);
        pipeline.createFrameBufferObject("FBO", false, 1, 1.0f, 0);
        // render to texture
        pipeline.switchFrameBufferObject("FBO");
        pipeline.switchCamera(mirrorCam);
        pipeline.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        pipeline.drawGeometry("AMBIENT", "(?!MirrorClass).*");
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", "(?!MirrorClass).*");
        // render to screen
        pipeline.switchFrameBufferObject(null);
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        pipeline.setUniform("jvr_UseClipPlane0", new UniformBool(false)); // disable
                                                                   // clipping
        pipeline.drawGeometry("AMBIENT", "(?!MirrorClass).*");
        Pipeline lp = pipeline.doLightLoop(true, true);
        // lp.setUniform("jvr_UseClipPlane0", new UniformBool(false)); //
        // disable clipping
        lp.drawGeometry("LIGHTING", "(?!MirrorClass).*");
        // render mirror plane
        pipeline.bindColorBuffer("jvr_MirrorTexture", "FBO", 0);
        pipeline.drawGeometry("AMBIENT", "MirrorClass");
        
        Transform camTrans = camera.getTransform();
        camTrans = clipPlane.getTransform().invert().mul(camTrans); // transform
                                                                    // to
                                                                    // clip
                                                                    // plane
                                                                    // space
        camTrans = clipPlane.getTransform().mul(Transform.scale(1, 1, -1).mul(camTrans));
        mirrorCam.setTransform(camTrans);
        return pipeline;
    }
    
}
