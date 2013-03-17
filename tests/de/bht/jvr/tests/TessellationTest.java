package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
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
public class TessellationTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new TessellationTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    CameraNode cam1;

    public TessellationTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode plane = ColladaLoader.load(new File("data/meshes/plane.dae"));
        ShapeNode planeShape = Finder.find(plane, ShapeNode.class, ".*");
        planeShape.setTransform(Transform.scale(5.5f).mul(Transform.rotateXDeg(-45)));
        root.addChildNode(planeShape);

        ShaderProgram shader = null;
        ShaderMaterial mat = null;
        Shader vs = new Shader(new File("data/shader/tessellation.vs"));
        Shader tc = new Shader(new File("data/shader/tessellation.tc"));
        Shader te = new Shader(new File("data/shader/tessellation.te"));
        Shader gs = new Shader(new File("data/shader/tessellation.gs"));
        Shader fs = new Shader(new File("data/shader/tessellation.fs"));
        shader = new ShaderProgram(vs, tc, te, gs, fs);

        mat = new ShaderMaterial();
        mat.setShaderProgram("TESS", shader);
        mat.setTexture("TESS", "jvr_Texture0", new Texture2D(new File("data/textures/layingrock-height.jpg")));
        mat.setTexture("TESS", "jvr_Texture1", new Texture2D(new File("data/textures/layingrock.jpg")));
        mat.setTexture("TESS", "jvr_Texture2", new Texture2D(new File("data/textures/layingrock-normals.jpg")));
        mat.setUniform("TESS", "offset", new UniformFloat(0.07f));
        mat.setUniform("TESS", "tessLevel", new UniformFloat(64));
        planeShape.setMaterial(mat);

        cam1 = new CameraNode("cam1", 16f / 10f, 60f);
        cam1.setTransform(Transform.translate(0, -0.2f, 5));
        root.addChildNode(cam1);
        cams.add(cam1);

        Pipeline p = new Pipeline(root);
        p.setViewFrustumCullingMode(0);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, Color.black);
        p.drawGeometry("TESS", null);

        // /////////////////////////////////////////////
        RenderWindow w = new AwtRenderWindow(p, 1280, 800);
        w.addKeyListener(this);
        w.addMouseListener(this);

        Viewer viewer = new Viewer(w);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                move(System.currentTimeMillis() - start, 0.005);
            }
            // viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
