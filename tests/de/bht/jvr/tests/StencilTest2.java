package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import javax.media.opengl.GL;

import de.bht.jvr.applicationbase.AbstractMouseKeyboardApp;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.renderer.RenderWindow;
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
public class StencilTest2 extends AbstractMouseKeyboardApp {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new StencilTest2();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public StencilTest2() throws Exception {
        start();
    }

    @Override
    public CameraNode getCamera() throws Exception {
        return cam;
    }

    @Override
    public Pipeline getPipeline(SceneNode root, CameraNode cam) throws Exception {
        Pipeline p = new Pipeline(root);
        p.switchCamera(cam);
        p.setStencilTest(true);
        p.clearBuffers(true, true, Color.gray, true, 0);
        p.drawGeometry("AMBIENT", "(?!ShadowVolume)");

        p.setDepthMask(false);
        p.setStencilFunc(GL.GL_ALWAYS, 1, 255);

        p.setStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_INCR);
        p.setFrontFace(true);
        p.drawGeometry("AMBIENT", "ShadowVolume");

        p.setStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_DECR);
        p.setFrontFace(false);
        p.drawGeometry("AMBIENT", "ShadowVolume");

        p.setFrontFace(true);
        p.setStencilFunc(GL.GL_EQUAL, 0, 255);
        p.setDepthMask(true);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "(?!ShadowVolume)");

        return p;
    }

    @Override
    public RenderWindow getRenderWindow(Pipeline pipeline) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GroupNode getSceneGraph() throws Exception {
        // use a torus as shadow volume
        SceneNode torus = ColladaLoader.load(new File("data/meshes/torus.dae"));
        torus.setTransform(Transform.translate(0, 0.1f, 0));
        ShaderMaterial mat = (ShaderMaterial) Finder.findMaterial(torus, null);
        mat.setMaterialClass("ShadowVolume");
        mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0, 0, 0, 0))); // make
                                                                                                    // it
                                                                                                    // invisible

        // the ground plane
        SceneNode plane = ColladaLoader.load(new File("data/meshes/plane.dae"));
        plane.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(500)));

        // the teapot
        SceneNode teapot = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        teapot.setTransform(Transform.scale(0.5f).mul(Transform.translate(-1, 0, 0)));

        // the light
        LightNode light = new PointLightNode();
        light.setTransform(Transform.translate(5, 5, 5));

        cam = new CameraNode("cam", 4f / 3f, 60f);
        cam.setTransform(Transform.translate(0, 1, 5));

        GroupNode root = new GroupNode();
        root.addChildNodes(torus, teapot, plane, light, cam);
        return root;
    }

    @Override
    public void init() throws Exception {
        Log.addLogListener(new LogPrinter());
        super.setMoveSpeed(0.5f);
    }

    @Override
    public void simulate(double renderDuration) throws Exception {
        // TODO Auto-generated method stub
        // System.out.println(renderDuration);
    }
}
