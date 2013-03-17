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
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformColor;
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
public class SpikeTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new SpikeTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    CameraNode cam1;

    public SpikeTest() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode polyDuck = ColladaLoader.load(new File("data/meshes/polyDuck.dae"));
        ShapeNode polyDuckShape = Finder.find(polyDuck, ShapeNode.class, "duck-geometry_Shape");
        polyDuckShape.setTransform(Transform.scale(0.5f));
        root.addChildNode(polyDuckShape);

        ShaderProgram shader = null;
        ShaderMaterial mat = null;
        Shader vs = new Shader(new File("data/shader/spike.vs"));
        Shader gs = new Shader(new File("data/shader/spike.gs"));
        Shader fs = new Shader(new File("data/shader/spike.fs"));
        shader = new ShaderProgram(vs, gs, fs);

        // shader.setParameter(GL3.GL_GEOMETRY_INPUT_TYPE, GL2GL3.GL_TRIANGLES);
        // shader.setParameter(GL3.GL_GEOMETRY_OUTPUT_TYPE,
        // GL2GL3.GL_TRIANGLES);
        // shader.setParameter(GL3.GL_GEOMETRY_VERTICES_OUT, 9);

        mat = new ShaderMaterial();
        mat.setUniform("SPIKE", "offset", new UniformFloat(0.1f));
        mat.setUniform("SPIKE", "jvr_Material_Ambient", new UniformColor(new Color(0.2f, 0.2f, 0.2f, 1.0f)));
        mat.setUniform("SPIKE", "jvr_Material_Diffuse", new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
        mat.setUniform("SPIKE", "jvr_Material_Specular", new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
        mat.setUniform("SPIKE", "jvr_Material_Shininess", new UniformFloat(10));
        mat.setShaderProgram("SPIKE", shader);
        polyDuckShape.setMaterial(mat);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 200));
        root.addChildNode(cam1);
        cams.add(cam1);

        Pipeline p = new Pipeline(root);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, Color.blue);
        p.setBlendFunc(GL.GL_ONE, GL.GL_ZERO);
        p.doLightLoop(true, true).drawGeometry("SPIKE", null);

        // /////////////////////////////////////////////
        RenderWindow w = new AwtRenderWindow(p, false);
        w.addKeyListener(this);
        w.addMouseListener(this);

        Viewer viewer = new Viewer(w);

        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();
                viewer.display();
                move(System.currentTimeMillis() - start, 0.1);
            }
            // viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
