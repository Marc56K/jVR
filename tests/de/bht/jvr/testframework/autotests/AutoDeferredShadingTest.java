package de.bht.jvr.testframework.autotests;

import java.io.File;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
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

public class AutoDeferredShadingTest extends AbstractAutoTest{

    public AutoDeferredShadingTest() {
        super("AutoDeferredShadingTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode scene = ColladaLoader.load(new File("data/meshes/testwelt02.dae"));
        scene.setTransform(Transform.scale(0.01f).mul(Transform.rotateXDeg(-90)));
        ShaderMaterial mat = ShaderMaterial.makePhongShaderMaterial();
        ShaderProgram prog = new ShaderProgram(new File("data/shader/deferred.vs"), new File("data/shader/deferred.fs"));
        mat.setShaderProgram("ATTRIBPASS", prog);

        for (ShapeNode node : Finder.findAll(scene, ShapeNode.class, null))
            node.setMaterial(mat);

        root.addChildNode(scene);

        // add 50 point lights to scene
        for (int i = 0; i < 50; i++) {
            PointLightNode pLight = new PointLightNode();
            pLight.setColor(new Color((float) (Math.random()), (float) (Math.random()), (float) (Math.random())));
            pLight.setTransform(Transform.translate((float) ((Math.random() - 0.5) * 90), (float) ((Math.random() - 0.5) * 90), (float) ((Math.random() - 0.5) * 90)));
            pLight.setIntensity(0.07f);
            pLight.setSpecularColor(new Color(1f, 1f, 1f, 1f));
            root.addChildNode(pLight);
        }

        camera = new CameraNode("cam1", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 0, 3));
        root.addChildNode(camera);
        
        // Deferred-Pipeline//////////////////////////////////////
        pipeline = new Pipeline(root);
        pipeline.createFrameBufferObject("GBUFFER", false, 2, GL2GL3.GL_RGBA16F, 1.0f, 0);
        pipeline.switchFrameBufferObject("GBUFFER");
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, new boolean[] { true, true }, Color.black);
        pipeline.drawGeometry("ATTRIBPASS", null);
        pipeline.switchFrameBufferObject(null);
        pipeline.clearBuffers(true, true, new Color(121, 188, 255));
        pipeline.drawGeometry("AMBIENT", null);
        Pipeline lp = pipeline.doLightLoop(true, true);
        lp.setBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        ShaderMaterial deferredMat = new ShaderMaterial("DeferredLighting", new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/deferred_lighting.fs")));
        lp.bindColorBuffer("NormalTex", "GBUFFER", 0);
        lp.bindColorBuffer("EyeVecTex", "GBUFFER", 1);
        lp.drawQuad(deferredMat, "DeferredLighting");

        // p.bindColorBuffer("jvr_Texture0", "GBUFFER", 1);
        // ShaderMaterial texMat = new ShaderMaterial("TextureOnly", new
        // ShaderProgram(new File("data/pipeline_shader/quad.vs"), new
        // File("data/pipeline_shader/default.fs")));
        // p.drawQuad(texMat, "TextureOnly");

        // Default-Pipeline
        Pipeline p_old = new Pipeline(root);
        p_old.switchCamera(camera);
        p_old.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p_old.drawGeometry("AMBIENT", null);
        p_old.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        return pipeline;
    }
    
}
