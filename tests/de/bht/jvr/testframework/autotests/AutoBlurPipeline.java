package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformInt;
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

public class AutoBlurPipeline extends AbstractAutoTest{
    
    public AutoBlurPipeline() {
        super("AutoBlurPipeline");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
GroupNode root = new GroupNode();
        
        SceneNode scene = ColladaLoader.load(new File("data/meshes/house.dae"));
        scene.setTransform(Transform.scale(0.1f));
        root.addChildNode(scene);
        
        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 40, 20));
        root.addChildNode(pLight);

        camera = new CameraNode("cam1", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 10, 40));
        root.addChildNode(camera);

        // Pipeline//////////////////////////////////////

        // create the blur shader material
        ShaderProgram blurShaderProg = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/blur.fs"));
        ShaderMaterial blurMat = new ShaderMaterial("BLURPASS", blurShaderProg);
        // create rendering pipeline
        pipeline = new Pipeline(root);
        // select camera
        pipeline.switchCamera(camera);
        // create fbo with one color texture
        pipeline.createFrameBufferObject("MyRenderTarget", false, 1, 1.0f, 4);
        // switch to fbo
        pipeline.switchFrameBufferObject("MyRenderTarget");
        // clear fbo buffers
        pipeline.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        // render ambient pass to fbo
        pipeline.drawGeometry("AMBIENT", null);
        // render lighting pass to fbo
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null, false);
        // switch to screen buffer
        pipeline.switchFrameBufferObject(null);
        // clear screen buffers
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));
        // set the blur factor
        pipeline.setUniform("iterations", new UniformInt(10));
        // bind the color texture from fbo to a uniform sampler
        pipeline.bindColorBuffer("jvr_Texture0", "MyRenderTarget", 0);
        // p.bindDepthBuffer("jvr_Texture0", "MyRenderTarget");
        // render fullscreen quad
        pipeline.drawQuad(blurMat, "BLURPASS");
        return pipeline;
    }
    
}
