package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;
import java.io.IOException;

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
public class ComplexPipelineTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, 0, -1));
        try {
            new ComplexPipelineTest();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private float intensity = 2;

    public ComplexPipelineTest() throws IOException {
        GroupNode root = new GroupNode();

        try {
            SceneNode scene = ColladaLoader.load(new File("data/meshes/testwelt01.dae"));

            // extract ground
            ShaderMaterial groundMaterial = (ShaderMaterial) Finder.findMaterial(scene, "Quader01.*");
            groundMaterial.setMaterialClass("ground");

            scene.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.1f)));
            root.addChildNode(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Printer.print(root);

        // create spot light
        SpotLightNode sLight = new SpotLightNode();
        sLight.setTransform(Transform.translate(0, 50, 40).mul(Transform.rotateXDeg(-45)));
        sLight.setSpotCutOff(50);
        sLight.setCastShadow(true);
        root.addChildNode(sLight);

        CameraNode cam1 = new CameraNode("cam1", 16f / 10f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 20));
        cam1.setFarPlane(100);
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////

        ShaderProgram sp = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/dof.fs"));
        ShaderMaterial sm = new ShaderMaterial("DOF", sp);

        Pipeline p = new Pipeline(root);
        // create fbo for dof
        p.createFrameBufferObject("DOFMap", true, 1, 1.0f, 4);
        // create shadow map buffer
        p.createFrameBufferObject("ShadowMap", true, 0, 2048, 2048, 0);

        // switch to light source as camera
        p.switchLightCamera(sLight);

        // switch to shadow map buffer
        p.switchFrameBufferObject("ShadowMap");
        // clear shadow map buffer
        p.clearBuffers(true, false, null);

        // render to fbo
        p.drawGeometry("AMBIENT", null);

        // switch to camera
        p.switchCamera(cam1);

        // switch to fbo
        p.switchFrameBufferObject("DOFMap");
        // clear fbo buffers
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));

        // render to fbo
        p.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");

        p.drawGeometry("AMBIENT", "ground");
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "ground", false);
        // p.doForwardLightLoop("LIGHTING", "ground", false, false);

        p.drawGeometry("AMBIENT", "", true);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "", true);
        // p.doForwardLightLoop("LIGHTING", "", false, true);

        // switch to p-buffer
        p.switchFrameBufferObject(null);
        // clear p-buffer
        p.clearBuffers(true, true, new Color(0, 0, 0));

        // set uniforms & buffers
        PipelineCommandPtr ptr = p.setUniform("intensity", new UniformFloat(intensity));
        p.bindColorBuffer("jvr_Texture1", "DOFMap", 0);
        p.bindDepthBuffer("jvr_Texture0", "DOFMap");

        // render quad
        p.drawQuad(sm, "DOF");

        // /////////////////////////////////////////////

        RenderWindow w1 = new AwtRenderWindow(p, 960, 600);
        w1.addKeyListener(this);
        w1.addMouseListener(this);
        w1.setVSync(false);

        Viewer viewer = new Viewer(w1);

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
                    intensity -= renderDuration * speed;
                    if (intensity < 0)
                        intensity = 0;
                    break;
                case 'R':
                    intensity += renderDuration * speed;
                    break;
                }
        }
    }
}
