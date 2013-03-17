package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PickRay;
import de.bht.jvr.core.Picker;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.input.MouseListener;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.math.Vector3;
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
 *
 *
 * This basic sample demonstrates the following features: - creating shader
 * materials - using normals, tangents and binormals
 * 
 * @author Marc Roßbach
 */

public class TangentsGenTest implements MouseListener {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Log.addLogListener(new LogPrinter(0, -1, 0));
            new TangentsGenTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private PointLightNode pLight;
    private CameraNode cam;
    private GroupNode root;
    private Vector2 mousePos = new Vector2(0, 0);

    public TangentsGenTest() throws Exception {
        // load geometry
        Geometry geo = loadGeometry();

        // create the shader program
        ShaderProgram ambientProg = new ShaderProgram(new File("data/shader/ambient.vs"), new File("data/shader/ambient.fs"));
        ShaderProgram lightingProg = new ShaderProgram(new File("data/shader/bumpmapping.vs"), new File("data/shader/bumpmapping.fs"));

        // create the shader material
        ShaderMaterial mat = new ShaderMaterial();
        mat.setShaderProgram("AMBIENT", ambientProg); // set the ambient shader
                                                      // program
        mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0, 0, 0, 1))); // set
                                                                                                    // ambient
                                                                                                    // color
                                                                                                    // of
                                                                                                    // the
                                                                                                    // material
                                                                                                    // to
                                                                                                    // black
        mat.setShaderProgram("LIGHTING", lightingProg); // set the lighting
                                                        // shader program
        // set the material
        mat.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new Color(0.7f, 0.6f, 0.18f, 1.0f)));
        mat.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(new Color(0.7f, 0.6f, 0.18f, 1.0f)));
        mat.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(6));

        // create the shape node
        ShapeNode shape = new ShapeNode("MyShape", geo, mat);
        shape.setTransform(Transform.rotateXDeg(-90)); // transform the shape
                                                       // node

        // create a camera
        cam = new CameraNode("MyCamera", 4f / 3f, 60);
        cam.setTransform(Transform.translate(0, 0.75f, 3)); // transform the
                                                            // camera

        // create a point light
        pLight = new PointLightNode();
        pLight.setTransform(Transform.translate(0, 5, 10)); // transform the
                                                            // point light

        // create the scene graph
        root = new GroupNode("MyRoot");
        root.addChildNodes(shape, cam, pLight);

        // to render the scene we need a rendering pipeline
        Pipeline p = new Pipeline(root);
        p.clearBuffers(true, true, Color.gray); // clear depth and color buffer
                                                // and set clear color to gray
        p.switchCamera(cam);
        p.drawGeometry("AMBIENT", null); // draw ambient pass
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null); // draw
                                                                  // lighting
                                                                  // pass

        // create a render windows
        RenderWindow win = new AwtRenderWindow(p, 800, 600); // create a render
                                                             // window to render
                                                             // the pipeline
        win.addMouseListener(this); // set the mouse listener to move the light

        // create a viewer
        Viewer v = new Viewer(win); // the viewer manages all render windows

        // main loop
        while (v.isRunning()) {
            updateLightPos(); // set the new light position
            v.display(); // render the scene
        }
    }

    public Geometry loadGeometry() throws Exception {
        // load collada-file
        SceneNode scene = ColladaLoader.load(new File("data/meshes/teapot_without_tangents.dae"));

        // extract the geometry
        Geometry geo = Finder.findGeometry(scene, null);

        return geo;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = new Vector2(e.getNormalizedX(), e.getNormalizedY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseWheelMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void updateLightPos() {
        // calculate the pick ray
        PickRay ray = Picker.getPickRay(root, cam, mousePos.x(), mousePos.y());

        // calculate the mouse position in world space
        Vector3 pos = ray.getRayOrigin().add(ray.getRayDirection().normalize().mul(30));

        // transform the light node
        pLight.setTransform(Transform.translate(0, 0, 40).mul(Transform.translate(pos)));
    }
}
