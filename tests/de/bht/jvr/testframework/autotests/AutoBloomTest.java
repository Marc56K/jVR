package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.Texture2D;
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

public class AutoBloomTest extends AbstractAutoTest{
    
    public AutoBloomTest() {
        super("AutoBloomTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode scene = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        ShaderMaterial mat = (ShaderMaterial) Finder.findMaterial(scene, null);
        Texture2D tex = new Texture2D(new File("data/textures/rusty.jpg"));
        mat.setTexture("AMBIENT", "jvr_Texture0", tex);
        mat.setUniform("AMBIENT", "jvr_UseTexture0", new UniformBool(true));
        mat.setTexture("LIGHTING", "jvr_Texture0", tex);
        mat.setUniform("LIGHTING", "jvr_UseTexture0", new UniformBool(true));
        root.addChildNode(scene);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(5, 2, 5));
        root.addChildNode(pLight);

        camera = new CameraNode("cam1", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 1, 3));
        root.addChildNode(camera);
        
        // Pipeline//////////////////////////////////////
        pipeline = new Pipeline(root);
        pipeline.createFrameBufferObject("FullFBO", false, 1, 1024, 768, 0);
        pipeline.createFrameBufferObject("SmallFBO", false, 1, 512, 384, 0);
        pipeline.switchFrameBufferObject("FullFBO");
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, Color.gray);
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        pipeline.switchFrameBufferObject("SmallFBO");
        ShaderMaterial downSamplingMat = new ShaderMaterial("DownSampling", new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/bright-pass_filter.fs")));
        pipeline.bindColorBuffer("jvr_Texture0", "FullFBO", 0);
        pipeline.drawQuad(downSamplingMat, "DownSampling");
        pipeline.switchFrameBufferObject(null);
        pipeline.bindColorBuffer("jvr_Texture0", "SmallFBO", 0);
        pipeline.bindColorBuffer("jvr_Texture1", "FullFBO", 0);
        ShaderMaterial bloomMat = new ShaderMaterial("Bloom", new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/bloom.fs")));
        pipeline.drawQuad(bloomMat, "Bloom");
        return pipeline;
    }
    
}
