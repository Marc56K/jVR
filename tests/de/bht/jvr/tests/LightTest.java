package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.input.KeyEvent;
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
public class LightTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(0, 0, -1));
        try {
            new LightTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DirectionalLightNode dLight;
    private PointLightNode pLight;
    private SpotLightNode sLight;

    public LightTest() throws Exception {
        // camera
        CameraNode cam = new CameraNode("cam", 4f / 3f, 60f);
        cam.setTransform(Transform.translate(0, 20, 60));
        cams.add(cam);

        // load collada files
        SceneNode sphereScene = ColladaLoader.load(new File("./data/meshes/teapot.dae"));
        SceneNode planeScene = ColladaLoader.load(new File("./data/meshes/plane.dae"));

        // extract geometries
        Geometry sphereGeo = Finder.findGeometry(sphereScene, ".*");
        Geometry planeGeo = Finder.findGeometry(planeScene, ".*");

        // create shader programs
        ShaderProgram ambientProg = new ShaderProgram(new File("./data/shader/ambient.vs"), new File("./data/shader/ambient.fs"));
        ShaderProgram lightingProg = new ShaderProgram(new File("./data/shader/phong.vs"), new File("./data/shader/phong.fs"));

        // create shader material
        ShaderMaterial mat = new ShaderMaterial();
        mat.setShaderProgram("AMBIENT", ambientProg);
        mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0, 0.2f, 0, 1)));
        mat.setShaderProgram("LIGHTING", lightingProg);
        mat.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new Color(0, 1, 0, 1)));
        mat.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(new Color(1, 1, 1, 1)));
        mat.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(90.0f));

        // create shape nodes
        ShapeNode sphereShape = new ShapeNode("sphere", sphereGeo, mat);
        sphereShape.setTransform(Transform.scale(20).mul(Transform.rotateX((float) (-Math.PI / 2))));
        ShapeNode planeShape = new ShapeNode("plane", planeGeo, mat);
        planeShape.setTransform(Transform.scale(700).mul(Transform.rotateX((float) (-Math.PI / 2))));

        // create directional light
        dLight = new DirectionalLightNode();
        dLight.setTransform(Transform.rotateY((float) (-Math.PI / 4)).mul(Transform.rotateX((float) (-Math.PI / 4))));
        dLight.setEnabled(false);

        // create point light
        pLight = new PointLightNode();
        pLight.setTransform(Transform.translate(10, 80, 70));
        pLight.setEnabled(false);

        // create spot light
        sLight = new SpotLightNode();
        sLight.setTransform(Transform.translate(30, 60, 60).mul(Transform.rotateY((float) (20 * Math.PI / 180))).mul(Transform.rotateX((float) (-Math.PI / 4))));
        sLight.setSpotCutOff(10);

        // create scene graph
        GroupNode root = new GroupNode("root");
        root.addChildNodes(cam, sphereShape, planeShape, dLight, pLight, sLight);

        // pipeline
        Pipeline p = new Pipeline(root);
        p.switchCamera(cam);
        p.clearBuffers(true, true, new Color(0, 0, 0, 0));
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null, false);

        // viewer
        RenderWindow w = new AwtRenderWindow(p, 800, 600);
        w.addKeyListener(this);
        w.addMouseListener(this);
        Viewer v = new Viewer(w);

        while (v.isRunning() && run) {
            long start = System.currentTimeMillis();
            v.display();
            move(System.currentTimeMillis() - start, 0.1);
        }
        v.close();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        switch (new Character((char) e.getKeyCode())) {
        case 'E':
            dLight.setEnabled(!dLight.isEnabled());
            break;
        case 'R':
            pLight.setEnabled(!pLight.isEnabled());
            break;
        case 'T':
            sLight.setEnabled(!sLight.isEnabled());
            break;
        }
    }
}
