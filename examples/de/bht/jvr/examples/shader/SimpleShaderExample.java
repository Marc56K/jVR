package de.bht.jvr.examples.shader;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformColor;
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
 * This basic sample demonstrates the following features:
 * - loading scenes from collada files
 * - extracting geometry from collada file
 * - creating shader materials
 * - simple pipeline and setting of uniforms
 * 
 * @author Marc Roßbach
 */

public class SimpleShaderExample {

    public static Geometry loadGeometry() throws Exception {
        // load collada-file
        SceneNode scene = ColladaLoader.load(new File("data/meshes/teapot.dae"));

        // extract the geometry
        Geometry geo = Finder.findGeometry(scene, null);

        return geo;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // load geometry
            Geometry geo = loadGeometry();

            // load the shader
            Shader vs = new Shader(new File("data/shader/simple.vs")); // vertex shader
            Shader fs = new Shader(new File("data/shader/simple.fs")); // fragment shader

            // create the shader program
            ShaderProgram prog = new ShaderProgram(vs, fs);

            // create the shader material
            ShaderMaterial mat = new ShaderMaterial();
            mat.setMaterialClass("MyColorMaterials"); // you can group materials in material classes to filter them in the pipeline
            mat.setShaderProgram("MyShaderContext", prog); // you can also add more than one shader program with different shader context

            // now we can create the shape node
            ShapeNode shape = new ShapeNode("MyShape", geo, mat);
            shape.setTransform(Transform.rotateXDeg(-90)); // transform the shape node

            // create a camera
            CameraNode cam = new CameraNode("MyCamera", 4f / 3f, 60);
            cam.setTransform(Transform.translate(0, 0.75f, 3)); // transform the camera

            // group the camera and the shape node
            GroupNode root = new GroupNode("MyRoot");
            root.addChildNodes(shape, cam);

            // to render the scene we need a rendering pipeline
            Pipeline p = new Pipeline(root);
            p.clearBuffers(true, true, new Color(0, 0, 0)); // clear the depth and the color buffer and set the clear color to black
            p.switchCamera(cam); // because a scene can have more than one camera
            p.drawGeometry("MyShaderContext", "MyColorMaterials"); // render the geometry

            // create a render windows
            RenderWindow win = new AwtRenderWindow(p, 800, 600); // create a render window to render the pipeline

            // create a viewer
            Viewer v = new Viewer(win); // the viewer manages all render windows

            // main loop
            float red = 0;
            while (v.isRunning()) {
                long start = System.currentTimeMillis(); // save system time before rendering
                mat.setUniform("MyShaderContext", "myColor", new UniformColor(new Color(red, 1.0f - red, 0.5f, 1.0f))); // set the new color
                v.display(); // render the scene
                double delta = System.currentTimeMillis() - start; // calculate render duration
                red += delta * 0.001; // calculate a new red value
                red %= 1.0f;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
