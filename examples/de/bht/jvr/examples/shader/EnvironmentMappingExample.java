package de.bht.jvr.examples.shader;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.TextureCube;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.input.MouseListener;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.math.Vector3;
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
 *
 *
 * This basic sample demonstrates the following features:
 * - creating shader materials
 * - using cube maps
 * - window and mouse listener
 * 
 * @author Marc Roßbach
 */

public class EnvironmentMappingExample implements MouseListener, WindowListener {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Log.addLogListener(new LogPrinter(-1, 0, -1));
            new EnvironmentMappingExample();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CameraNode cam;
    private GroupNode root;
    private float camAspectRatio = 4f / 3f;

    public EnvironmentMappingExample() throws Exception {
        // load geometry
        Geometry geo = loadGeometry();

        // create the shader program
        ShaderProgram ambientProg = new ShaderProgram(new File("data/shader/envmapping.vs"), new File("data/shader/envmapping.fs"));

        // create the shader material
        ShaderMaterial mat = new ShaderMaterial();
        mat.setShaderProgram("AMBIENT", ambientProg); // set the shader program
        // load textures
        mat.setTexture("AMBIENT", "MyEnvMap", new TextureCube(new File("data/textures/skybox/mountain_ring_rt.jpg"), new File("data/textures/skybox/mountain_ring_lf.jpg"), new File("data/textures/skybox/mountain_ring_up.jpg"), new File("data/textures/skybox/mountain_ring_dn.jpg"), new File("data/textures/skybox/mountain_ring_bk.jpg"), new File("data/textures/skybox/mountain_ring_ft.jpg")));

        // create the shape node
        ShapeNode shape = new ShapeNode("MyShape", geo, mat);

        // create a camera
        cam = new CameraNode("MyCamera", 4f / 3f, 60);
        cam.setTransform(Transform.translate(0, 0.5f, 2.75f)); // transform the camera

        // create the scene graph
        root = new GroupNode("MyRoot");
        root.addChildNodes(shape, cam);

        // to render the scene we need a rendering pipeline
        Pipeline p = new Pipeline(root);
        p.clearBuffers(true, true, Color.gray); // clear depth and color buffer and set clear color to gray
        p.switchCamera(cam);
        p.drawGeometry("AMBIENT", null); // draw ambient pass

        // create a render windows
        RenderWindow win = new AwtRenderWindow(p, 1024, 768); // create a render window to render the pipeline
        win.addMouseListener(this); // set the mouse listener to move the light
        win.addWindowListener(this); // set the window listener for correct camera aspect ratio

        // create a viewer
        Viewer v = new Viewer(win); // the viewer manages all render windows

        // main loop
        float angle = 0;
        while (v.isRunning()) {
            double start = System.currentTimeMillis();
            shape.setTransform(Transform.rotate(new Vector3(0, 1, 0.5f), angle).mul(Transform.rotateXDeg(-90)));
            cam.setAspectRatio(camAspectRatio);
            v.display(); // render the scene
            angle += (System.currentTimeMillis() - start) * 0.0005;
        }
    }

    public Geometry loadGeometry() throws Exception {
        // load collada-file
        SceneNode scene = ColladaLoader.load(new File("data/meshes/teapot.dae"));

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
        new Vector2(e.getNormalizedX(), e.getNormalizedY());
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

    @Override
    public void windowClose(RenderWindow win) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowReshape(RenderWindow win, int width, int height) {
        camAspectRatio = (float) width / height;

    }
}
