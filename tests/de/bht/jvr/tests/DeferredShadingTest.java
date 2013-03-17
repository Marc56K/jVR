package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
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
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.renderer.NewtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.renderer.WindowListener;
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
public class DeferredShadingTest extends TestBase implements WindowListener {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new DeferredShadingTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    CameraNode cam1;

    public DeferredShadingTest() throws Exception {
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

        cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 3));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Deferred-Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.createFrameBufferObject("GBUFFER", false, 2, GL2GL3.GL_RGBA16F, 1.0f, 0);
        p.switchFrameBufferObject("GBUFFER");
        p.switchCamera(cam1);
        p.clearBuffers(true, new boolean[] { true, true }, Color.black);
        p.drawGeometry("ATTRIBPASS", null);
        p.switchFrameBufferObject(null);
        p.clearBuffers(true, true, new Color(121, 188, 255));
        p.drawGeometry("AMBIENT", null);
        Pipeline lp = p.doLightLoop(true, true);
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
        p_old.switchCamera(cam1);
        p_old.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p_old.drawGeometry("AMBIENT", null);
        p_old.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////
        RenderWindow w = new NewtRenderWindow(p, false);
        w.addKeyListener(this);
        w.addMouseListener(this);
        w.addWindowListener(this);

        Viewer viewer = new Viewer(w);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                move(System.currentTimeMillis() - start, 0.001);
            }
            // viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void windowClose(RenderWindow win) {
        System.err.println("Closing: " + win);

    }

    @Override
    public void windowReshape(RenderWindow win, int width, int height) {
        System.err.println("Reshaping: " + win);
        cam1.setAspectRatio((float) width / height);
    }
}
