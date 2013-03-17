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
public class DepthBufferTest extends TestBase {

    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(200, 0, -1));
        try {
            new DepthBufferTest();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public DepthBufferTest() throws IOException {
        GroupNode root = new GroupNode();

        try {
            SceneNode scene = ColladaLoader.load(new File("data/meshes/house.dae"));
            // scene.getTransform().scale(50.0f);
            root.addChildNode(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Printer.print(root);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        CameraNode cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 200));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////

        ShaderProgram sp = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/depth.fs"));
        ShaderMaterial sm = new ShaderMaterial("", sp);

        Pipeline p = new Pipeline(root);

        p.switchCamera(cam1);

        // create fbo
        p.createFrameBufferObject("fbo1", true, 1, 1.0f, 4);

        // switch to fbo
        p.switchFrameBufferObject("fbo1");
        // clear fbo buffers
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));

        // render to fbo
        p.drawGeometry("AMBIENT", null);

        // switch to p-buffer
        p.switchFrameBufferObject(null);
        // clear p-buffer
        p.clearBuffers(true, true, new Color(0, 0, 0));

        // set uniforms & buffers
        p.bindDepthBuffer("jvr_Texture0", "fbo1");

        // render quad
        p.drawQuad(sm, "");

        // /////////////////////////////////////////////

        RenderWindow w = new AwtRenderWindow(p, 1024, 768);
        w.addKeyListener(this);
        w.addMouseListener(this);

        Viewer viewer = new Viewer(w);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                move(System.currentTimeMillis() - start, 0.1);
            }
            viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
