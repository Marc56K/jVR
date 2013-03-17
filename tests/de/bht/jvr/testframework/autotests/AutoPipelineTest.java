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
import de.bht.jvr.core.pipeline.PipelineCommandPtr;
import de.bht.jvr.core.uniforms.UniformFloat;
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

public class AutoPipelineTest extends AbstractAutoTest{

    public AutoPipelineTest() {
        super("AutoPipelineTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode scene = ColladaLoader.load(new File("data/meshes/house.dae"));
        scene.setTransform(Transform.scale(0.1f));
        root.addChildNode(scene);
        
        // Printer.print(root);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 40, 20));
        root.addChildNode(pLight);

        camera = new CameraNode("cam1", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 0, 20));
        root.addChildNode(camera);

        // Pipeline//////////////////////////////////////

        ShaderProgram sp = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/dof.fs"));
        ShaderMaterial sm = new ShaderMaterial("PASS0", sp);

        pipeline = new Pipeline(root);

        pipeline.switchCamera(camera);

        // create fbo
        pipeline.createFrameBufferObject("fbo1", true, 1, 1.0f, 4);

        // switch to fbo
        pipeline.switchFrameBufferObject("fbo1");
        // clear fbo buffers
        pipeline.clearBuffers(true, new boolean[] { true }, new Color(0.5f, 0.5f, 0.5f));

        // render to fbo
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null, false);
        // p.doForwardLightLoop("LIGHTING", null, false);

        // switch to p-buffer
        pipeline.switchFrameBufferObject(null);
        // clear p-buffer
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));

        // set uniforms & buffers
        final float intensity = 4;
        PipelineCommandPtr ptr = pipeline.setUniform("intensity", new UniformFloat(intensity));
        pipeline.bindColorBuffer("jvr_Texture1", "fbo1", 0);
        pipeline.bindDepthBuffer("jvr_Texture0", "fbo1");

        // render quad
        pipeline.drawQuad(sm, "PASS0");
        ptr.setUniform("intensity", new UniformFloat(intensity));
        return pipeline;
    }
    
}
