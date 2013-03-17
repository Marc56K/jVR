package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;
import java.io.IOException;

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
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.renderer.AwtRenderWindow;
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
public class BlurPipeline extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, 0, -1));
        try {
            new BlurPipeline();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private float intensity = 4;

    public BlurPipeline() throws IOException {
        GroupNode root = new GroupNode();

        try {
            SceneNode scene = ColladaLoader.load(new File("data/meshes/house.dae"));
            scene.setTransform(Transform.scale(0.1f));
            root.addChildNode(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Printer.print(root);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 40, 20));
        root.addChildNode(pLight);

        CameraNode cam = new CameraNode("cam1", 4f / 3f, 60f);
        cam.setTransform(Transform.translate(0, 10, 40));
        root.addChildNode(cam);
        cams.add(cam);

        // Pipeline//////////////////////////////////////

        // create the blur shader material
        ShaderProgram blurShaderProg = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/blur.fs"));
        ShaderMaterial blurMat = new ShaderMaterial("BLURPASS", blurShaderProg);
        // create rendering pipeline
        Pipeline p = new Pipeline(root);
        // select camera
        p.switchCamera(cam);
        // create fbo with one color texture
        p.createFrameBufferObject("MyRenderTarget", false, 1, 1.0f, 4);
        // switch to fbo
        p.switchFrameBufferObject("MyRenderTarget");
        // clear fbo buffers
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        // render ambient pass to fbo
        p.drawGeometry("AMBIENT", null);
        // render lighting pass to fbo
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null, false);
        // switch to screen buffer
        p.switchFrameBufferObject(null);
        // clear screen buffers
        p.clearBuffers(true, true, new Color(0, 0, 0));
        // set the blur factor
        p.setUniform("iterations", new UniformInt(10));
        // bind the color texture from fbo to a uniform sampler
        p.bindColorBuffer("jvr_Texture0", "MyRenderTarget", 0);
        // p.bindDepthBuffer("jvr_Texture0", "MyRenderTarget");
        // render fullscreen quad
        p.drawQuad(blurMat, "BLURPASS");

        // /////////////////////////////////////////////

        RenderWindow w = new AwtRenderWindow(p, 800, 600);
        w.addKeyListener(this);
        w.addMouseListener(this);
        Viewer viewer = new Viewer(w);

        try {
            while (viewer.isRunning() && run) {
                // ptr.setUniform("intensity", new
                // UniformFloat(this.intensity));
                long start = System.currentTimeMillis();
                viewer.display();
                move(System.currentTimeMillis() - start, 0.01);
            }
            viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void move(double renderDuration, double speed) {
        super.move(renderDuration, speed);

        synchronized (pressedKeys) {
            for (Character key : pressedKeys)
                switch (key) {
                case 'E':
                    intensity -= 1.0f * renderDuration * speed;
                    if (intensity < 0)
                        intensity = 0;
                    break;
                case 'R':
                    intensity += 1.0f * renderDuration * speed;
                    break;
                }
        }
    }
}
