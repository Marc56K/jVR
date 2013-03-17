package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
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
import de.bht.jvr.renderer.AwtRenderWindow;
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
public class BloomTest extends TestBase implements WindowListener {
    public static void main(String[] args) {
        // Log.addLogListener(new LogPrinter());
        try {
            new BloomTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    CameraNode cam1;

    public BloomTest() throws Exception {
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

        cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 1, 3));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.createFrameBufferObject("FullFBO", false, 1, 1024, 768, 0);
        p.createFrameBufferObject("SmallFBO", false, 1, 512, 384, 0);
        p.switchFrameBufferObject("FullFBO");
        p.switchCamera(cam1);
        p.clearBuffers(true, true, Color.gray);
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        p.switchFrameBufferObject("SmallFBO");
        ShaderMaterial downSamplingMat = new ShaderMaterial("DownSampling", new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/bright-pass_filter.fs")));
        p.bindColorBuffer("jvr_Texture0", "FullFBO", 0);
        p.drawQuad(downSamplingMat, "DownSampling");
        p.switchFrameBufferObject(null);
        p.bindColorBuffer("jvr_Texture0", "SmallFBO", 0);
        p.bindColorBuffer("jvr_Texture1", "FullFBO", 0);
        ShaderMaterial bloomMat = new ShaderMaterial("Bloom", new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/bloom.fs")));
        p.drawQuad(bloomMat, "Bloom");

        // /////////////////////////////////////////////
        RenderWindow w = new AwtRenderWindow(p, 1024, 768);
        w.addKeyListener(this);
        w.addMouseListener(this);
        w.addWindowListener(this);
        w.setWindowTitle("ColladaPolyTest");

        Viewer viewer = new Viewer(w);

        try {
            float angle = 0;
            double delta = 0;
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                angle += delta * 0.02;
                scene.setTransform(Transform.rotateYDeg(angle));
                delta = System.currentTimeMillis() - start;
                move(delta, 0.001);
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
