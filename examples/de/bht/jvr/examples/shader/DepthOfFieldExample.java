package de.bht.jvr.examples.shader;

import de.bht.jvr.util.Color;
import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.pipeline.PipelineCommandPtr;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.examples.input.MouseKeyboardNavigationExample;
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
 * - frame buffer object (fbo)
 * - loading and setting textures
 * - setting uniforms
 * - pipeline command pointer
 * 
 * @author Marc Roßbach
 */

public class DepthOfFieldExample extends MouseKeyboardNavigationExample {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new DepthOfFieldExample();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public float intensity = 5; // blur intensity

    public DepthOfFieldExample() throws Exception {
        // load ground plane
        SceneNode plane = ColladaLoader.load(new File("./data/meshes/plane.dae"));
        // transform the ground plane
        plane.setTransform(Transform.scale(45).mul(Transform.translate(0, 0, -0.4f)).mul(Transform.rotateXDeg(-90)));
        // load a ground texture
        Texture2D groundTex = new Texture2D(new File("./data/textures/weltgrund.jpg"));
        // extract the material of the ground plane
        ShaderMaterial groundMat = (ShaderMaterial) Finder.findMaterial(plane, null);
        // set texture to material (for every shader context)
        groundMat.setTexture("AMBIENT", "jvr_Texture0", groundTex);
        groundMat.setUniform("AMBIENT", "jvr_UseTexture0", new UniformBool(true));
        groundMat.setTexture("LIGHTING", "jvr_Texture0", groundTex);
        groundMat.setUniform("LIGHTING", "jvr_UseTexture0", new UniformBool(true));

        // load teapot
        SceneNode teapot = ColladaLoader.load(new File("./data/meshes/teapot.dae"));
        teapot.setTransform(Transform.scale(0.5f));
        // load a texture
        Texture2D teapotTex = new Texture2D(new File("./data/textures/checkered.jpg"));
        // extract the material of the teapot
        ShaderMaterial teapotMat = (ShaderMaterial) Finder.findMaterial(teapot, null);
        // set texture to material (for every shader context)
        teapotMat.setTexture("AMBIENT", "jvr_Texture0", teapotTex);
        teapotMat.setUniform("AMBIENT", "jvr_UseTexture0", new UniformBool(true));
        teapotMat.setTexture("LIGHTING", "jvr_Texture0", teapotTex);
        teapotMat.setUniform("LIGHTING", "jvr_UseTexture0", new UniformBool(true));

        // create some teapots
        GroupNode teapots = new GroupNode("MyTeapots");
        for (int i = 0; i < 2; i++) {
            GroupNode g = new GroupNode();
            g.setTransform(Transform.translate(3 * i, 0, -30 * i).mul(Transform.scale(5 * (i + 1)))); // scale and translate
            g.addChildNode(teapot);
            teapots.addChildNode(g);
        }

        // create two lights
        DirectionalLightNode dLight = new DirectionalLightNode("MyDirectionLight");
        dLight.setTransform(Transform.rotateXDeg(-45));
        dLight.setIntensity(1.5f);

        // create a camera
        CameraNode cam = new CameraNode("MyCamera", 4f / 3f, 60);
        cam.setTransform(Transform.translate(2.5f, 2.2f, 3)); // transform the camera
        super.cam = cam;

        // create the scene graph
        GroupNode root = new GroupNode("MyRoot");
        root.addChildNodes(cam, dLight, teapots, plane);

        // pipeline //////////////////////////////////////////////////////////////
        // load dof shader
        ShaderProgram sp = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/dof.fs"));
        ShaderMaterial sm = new ShaderMaterial("DOFPass", sp);

        Pipeline p = new Pipeline(root);
        // switch to camera
        p.switchCamera(cam);
        // create fbo with depth and color buffer
        p.createFrameBufferObject("SceneMap", true, 1, 1.0f, 4);
        // switch to fbo
        p.switchFrameBufferObject("SceneMap");
        // clear fbo buffers
        p.clearBuffers(true, true, new Color(0.5f, 0.7f, 1.0f));
        // render to fbo
        p.drawGeometry("AMBIENT", null);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", null, false);

        // switch to p-buffer
        p.switchFrameBufferObject(null);
        // clear p-buffer
        p.clearBuffers(true, true, new Color(0, 0, 0));
        // set uniforms & buffers
        PipelineCommandPtr ptr = p.setUniform("intensity", new UniformFloat(intensity)); // set the blur intensity
        p.bindColorBuffer("jvr_Texture1", "SceneMap", 0); // bind color buffer from fbo to uniform
        p.bindDepthBuffer("jvr_Texture0", "SceneMap"); // bind depth buffer from fbo to uniform
        // render quad with dof shader
        p.drawQuad(sm, "DOFPass");

        // //////////////////////////////////////////////////////////////////////

        // create a render window to render the pipeline
        RenderWindow win = new AwtRenderWindow(p, 800, 600);
        win.addKeyListener(this); // set the key listener for the window
        win.addMouseListener(this); // set the mouse listener for the window

        // create a viewer
        Viewer v = new Viewer(win); // the viewer manages all render windows

        // main loop
        while (v.isRunning()) {
            long start = System.currentTimeMillis(); // save system time before
                                                     // rendering
            ptr.setUniform("intensity", new UniformFloat(intensity)); // set new render intensity to the pipeline
            v.display(); // render the scene
            double delta = System.currentTimeMillis() - start; // calculate render duration
            move(delta);
        }
    }

    @Override
    public void move(double renderDuration) {
        super.move(renderDuration);

        synchronized (pressedKeys) {
            for (Character key : pressedKeys)
                switch (key) {
                case 'E': // decrease blur intensity
                    intensity -= 0.01f * renderDuration;
                    if (intensity < 0)
                        intensity = 0;
                    break;
                case 'R': // increase blur intensity
                    intensity += 0.01f * renderDuration;
                    break;
                }
        }
    }
}
