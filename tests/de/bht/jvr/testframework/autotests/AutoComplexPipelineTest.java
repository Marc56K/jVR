package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
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

public class AutoComplexPipelineTest extends AbstractAutoTest {

    public AutoComplexPipelineTest() {
        super("AutoComplexPipelineTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode scene = ColladaLoader.load(new File("data/meshes/testwelt01.dae"));
        
        // extract ground
        ShaderMaterial groundMaterial = (ShaderMaterial) Finder.findMaterial(scene, "Quader01.*");
        groundMaterial.setMaterialClass("ground");

        scene.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.1f)));
        root.addChildNode(scene);
        
        SpotLightNode sLight = new SpotLightNode();
        sLight.setTransform(Transform.translate(0, 50, 40).mul(Transform.rotateXDeg(-45)));
        sLight.setSpotCutOff(50);
        sLight.setCastShadow(true);
        root.addChildNode(sLight);

        camera = new CameraNode("cam1", 16f / 10f, 60f);
        camera.setTransform(Transform.translate(0, 0, 20));
        camera.setFarPlane(100);
        root.addChildNode(camera);

        // Pipeline//////////////////////////////////////

        ShaderProgram sp = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/dof.fs"));
        ShaderMaterial sm = new ShaderMaterial("DOF", sp);

        pipeline = new Pipeline(root);
        // create fbo for dof
        pipeline.createFrameBufferObject("DOFMap", true, 1, 1.0f, 4);
        // create shadow map buffer
        pipeline.createFrameBufferObject("ShadowMap", true, 0, 2048, 2048, 0);

        // switch to light source as camera
        pipeline.switchLightCamera(sLight);

        // switch to shadow map buffer
        pipeline.switchFrameBufferObject("ShadowMap");
        // clear shadow map buffer
        pipeline.clearBuffers(true, false, null);

        // render to fbo
        pipeline.drawGeometry("AMBIENT", null);

        // switch to camera
        pipeline.switchCamera(camera);

        // switch to fbo
        pipeline.switchFrameBufferObject("DOFMap");
        // clear fbo buffers
        pipeline.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));

        // render to fbo
        pipeline.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");

        pipeline.drawGeometry("AMBIENT", "ground");
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", "ground", false);
        // p.doForwardLightLoop("LIGHTING", "ground", false, false);

        pipeline.drawGeometry("AMBIENT", "", true);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", "", true);
        // p.doForwardLightLoop("LIGHTING", "", false, true);

        // switch to p-buffer
        pipeline.switchFrameBufferObject(null);
        // clear p-buffer
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));

        // set uniforms & buffers
        pipeline.bindColorBuffer("jvr_Texture1", "DOFMap", 0);
        pipeline.bindDepthBuffer("jvr_Texture0", "DOFMap");

        // render quad
        pipeline.drawQuad(sm, "DOF");
        return pipeline;
    }
    
}
