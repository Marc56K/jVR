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
import de.bht.jvr.core.pipeline.PipelineCommandPtr;
import de.bht.jvr.core.uniforms.UniformFloat;
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
public class PipelineTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, 0, -1));
        try {
            new PipelineTest();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private float intensity = 4;

    public PipelineTest() throws IOException {
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

        CameraNode cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 20));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////

        ShaderProgram sp = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/dof.fs"));
        ShaderMaterial sm = new ShaderMaterial("PASS0", sp);

        Pipeline p = new Pipeline(root);

        p.switchCamera(cam1);

        // create fbo
        p.createFrameBufferObject("fbo1", true, 1, 1.0f, 4);

        // switch to fbo
        p.switchFrameBufferObject("fbo1");
        // clear fbo buffers
        p.clearBuffers(true, new boolean[] { true }, new Color(0.5f, 0.5f, 0.5f));

        // render to fbo
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null, false);
        // p.doForwardLightLoop("LIGHTING", null, false);

        // switch to p-buffer
        p.switchFrameBufferObject(null);
        // clear p-buffer
        p.clearBuffers(true, true, new Color(0, 0, 0));

        // set uniforms & buffers
        PipelineCommandPtr ptr = p.setUniform("intensity", new UniformFloat(intensity));
        p.bindColorBuffer("jvr_Texture1", "fbo1", 0);
        p.bindDepthBuffer("jvr_Texture0", "fbo1");

        // render quad
        p.drawQuad(sm, "PASS0");

        // /////////////////////////////////////////////

        RenderWindow w = new AwtRenderWindow(p, 800, 600);
        w.addKeyListener(this);
        w.addMouseListener(this);
        Viewer viewer = new Viewer(w);

        try {
            while (viewer.isRunning() && run) {
                ptr.setUniform("intensity", new UniformFloat(intensity));
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
