package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import javax.media.opengl.GL;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
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
public class StencilTest extends TestBase implements WindowListener {
    public static void main(String[] args) {
        // Log.addLogListener(new LogPrinter());
        try {
            new StencilTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    CameraNode cam1;

    public StencilTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode teapot = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        ShaderMaterial mat = (ShaderMaterial) Finder.find(teapot, ShapeNode.class, null).getMaterial();
        mat.setMaterialClass("teapot");
        SceneNode box = ColladaLoader.load(new File("data/meshes/box.dae"));
        box.setTransform(Transform.translate(0, 1, 0.5f));
        mat = (ShaderMaterial) Finder.find(box, ShapeNode.class, null).getMaterial();
        mat.setMaterialClass("box");
        root.addChildNodes(teapot, box);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 1, 3));
        root.addChildNode(cam1);
        cams.add(cam1);

        // ShaderProgram blurShaderProg = new ShaderProgram(new
        // File("data/pipeline_shader/quad.vs"), new
        // File("data/pipeline_shader/blur.fs"));
        // ShaderMaterial blurMat = new ShaderMaterial("BLURPASS",
        // blurShaderProg);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        // p.createFrameBufferObject("fbo", 1, 1, 0, true);
        // p.switchFrameBufferObject("fbo");
        p.switchCamera(cam1);
        p.setStencilTest(true);
        p.clearBuffers(true, true, Color.gray, true, 0);
        p.setStencilFunc(GL.GL_NEVER, 0, 0);
        p.setStencilOp(GL.GL_INCR, GL.GL_INCR, GL.GL_INCR);
        p.drawGeometry("AMBIENT", "box");
        p.setStencilFunc(GL.GL_EQUAL, 1, 1);
        p.setStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
        p.drawGeometry("AMBIENT", "teapot");
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "teapot");
        // p.switchFrameBufferObject(null);
        // p.setUniform("iterations", new UniformInt(2));
        // p.bindColorBuffer("jvr_Texture0", "fbo", 0);
        // p.drawQuad(blurMat, "BLURPASS");

        // /////////////////////////////////////////////
        RenderWindow w = new AwtRenderWindow(p, 800, 600);
        w.addKeyListener(this);
        w.addMouseListener(this);
        w.addWindowListener(this);
        w.setWindowTitle("ColladaPolyTest");

        Viewer viewer = new Viewer(false, w);

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
    public void windowClose(RenderWindow win) {}

    @Override
    public void windowReshape(RenderWindow win, int width, int height) {}
}
