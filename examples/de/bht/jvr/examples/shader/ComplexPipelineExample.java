package de.bht.jvr.examples.shader;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.ClipPlaneNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.pipeline.PipelineCommandPtr;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.examples.input.MouseKeyboardNavigationExample;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.renderer.AwtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.renderer.WindowListener;
import de.bht.jvr.util.Color;

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
 * - shadow mapping
 * - depth of field
 * - mirror
 * - sky box
 * 
 * @author Marc Roßbach
 */

public class ComplexPipelineExample extends MouseKeyboardNavigationExample implements WindowListener {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, 20, -1));
        try {
            new ComplexPipelineExample();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private PipelineCommandPtr waveTime;
    private PipelineCommandPtr waveScale;
    private float waveScaleValue = 0.1f;
    private float camAspectRatio = 16f / 10f;

    public ComplexPipelineExample() throws Exception {
        float blurFactor = 0.75f;

        GroupNode root = new GroupNode();

        // load sky box
        SkyBoxCreator sbc = new SkyBoxCreator("data/textures/skybox/mountain_ring");
        SceneNode sky1 = sbc.getSkyBox("SKY1");
        SceneNode sky2 = sbc.getSkyBox("SKY2");
        root.addChildNodes(sky1, sky2);

        // load the world
        SceneNode scene = ColladaLoader.load(new File("data/meshes/testwelt02.dae"));
        scene.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.1f)));
        root.addChildNode(scene);

        // create spot light
        SpotLightNode sLight = new SpotLightNode();
        sLight.setTransform(Transform.translate(-50, 80, 50).mul(Transform.rotateYDeg(-45).mul(Transform.rotateXDeg(-65))));
        sLight.setIntensity(1.2f);
        sLight.setSpotCutOff(40);
        sLight.setCastShadow(true);
        sLight.setShadowBias(0.3f);
        root.addChildNode(sLight);

        // create camera
        CameraNode cam = new CameraNode("cam1", 16f / 10f, 60f);
        cam.setTransform(Transform.translate(0, 2, 40));
        root.addChildNode(cam);
        super.cam = cam;

        // clipping plane
        ClipPlaneNode clipPlane = new ClipPlaneNode();
        clipPlane.setTransform(Transform.translate(0, -3, 0).mul(Transform.rotateXDeg(90)));
        root.addChildNode(clipPlane);

        // mirror plane
        SceneNode mirrorPlane = ColladaLoader.load(new File("data/meshes/plane.dae"));
        mirrorPlane.setTransform(Transform.translate(0, -3, 0).mul(Transform.rotateXDeg(-90).mul(Transform.scale(2000))));
        root.addChildNode(mirrorPlane);

        // mirror camera
        CameraNode mirrorCam = new CameraNode("cam2", -16f / 10f, 60f);
        mirrorCam.setTransform(Transform.translate(0, 0, 0));
        root.addChildNode(mirrorCam);

        // mirror material
        ShaderProgram prog = new ShaderProgram(new File("data/shader/mirror.vs"), new File("data/shader/mirror.fs"));
        ShaderMaterial mat = new ShaderMaterial("AMBIENT", prog);
        mat.setMaterialClass("MirrorMaterial");

        ShapeNode mirrorShape = Finder.find(mirrorPlane, ShapeNode.class, null);
        mirrorShape.setMaterial(mat);

        // depth of field shader
        ShaderProgram sp = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/dof.fs"));
        ShaderMaterial sm = new ShaderMaterial("DOF", sp);

        // Pipeline///////////////////////////////////////////////////////////////////////////////

        Pipeline p = new Pipeline(root);

        // create fbo for depth of field
        p.createFrameBufferObject("DOFMap", true, 1, 1.0f, 2);
        // create shadow map
        p.createFrameBufferObject("ShadowMap", true, 0, 2048, 2048, 0);
        // create mirror map
        p.createFrameBufferObject("MirrorMap", false, 1, 0.5f, 0);

        // create shadow map /////////////////////
        // declare a helper variable
        p.declareVariable("UpdateShadowMap", 1);
        // if UpdateShadowMap == 1
        Pipeline tp = p.IfEqual("UpdateShadowMap", 1); // Because this is a static scene its enough to update the shadow map only in the first frame.            
            // UpdateShadowMap = 0
            tp.setVariable("UpdateShadowMap", 0);
            // switch to light source as camera
            tp.switchLightCamera(sLight);
            // render to shadow map
            tp.switchFrameBufferObject("ShadowMap");
            tp.clearBuffers(true, false, null);
            tp.drawGeometry("AMBIENT", "(?!MirrorMaterial).*");
    
        // render mirrored scene to texture //////
            
        // switch to mirror camera
        p.switchCamera(mirrorCam);
        // render to dof map
        p.switchFrameBufferObject("DOFMap");
        p.clearBuffers(true, true, new Color(0.5f, 0.7f, 1.0f));
        p.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
        p.drawGeometry("SKY2", null); // draw sky box
        p.clearBuffers(true, false, null); // clear depth buffer
        p.drawGeometry("AMBIENT", "(?!MirrorMaterial).*");
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "(?!MirrorMaterial).*");

        // render to mirror map with depth of field shader
        p.switchFrameBufferObject("MirrorMap");
        p.clearBuffers(true, true, new Color(0, 0, 0));
        // set uniforms & buffers
        p.setUniform("intensity", new UniformFloat(blurFactor));
        p.bindColorBuffer("jvr_Texture1", "DOFMap", 0);
        p.bindDepthBuffer("jvr_Texture0", "DOFMap");
        // render quad
        p.drawQuad(sm, "DOF");
        // release color and depth buffer
        p.unbindBuffers();

        // render scene to texture ///////////////

        // switch to camera
        p.switchCamera(cam);
        // render to dof map
        p.switchFrameBufferObject("DOFMap");
        p.clearBuffers(true, true, new Color(0.5f, 0.7f, 1.0f));
        p.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
        p.setUniform("jvr_UseClipPlane0", new UniformBool(false)); // disable clipping
        p.drawGeometry("SKY1", null); // draw sky box
        p.clearBuffers(true, false, null); // clear depth buffer
        p.drawGeometry("AMBIENT", "(?!MirrorMaterial).*");
        Pipeline lp = p.doLightLoop(true, true);
        lp.setUniform("jvr_UseClipPlane0", new UniformBool(false)); // disable clipping
        lp.drawGeometry("LIGHTING", "(?!MirrorMaterial).*");

        // render mirror plane
        p.bindColorBuffer("jvr_MirrorTexture", "MirrorMap", 0);
        waveTime = p.setUniform("waveTime", new UniformFloat(0.0f));
        waveScale = p.setUniform("waveScale", new UniformFloat(waveScaleValue));
        p.drawGeometry("AMBIENT", "MirrorMaterial");

        // render final step with depth of field shader //////////

        // switch to screen buffer
        p.switchFrameBufferObject(null);
        // clear screen buffer
        p.clearBuffers(true, true, new Color(0, 0, 0));

        // set uniforms & buffers
        p.setUniform("intensity", new UniformFloat(blurFactor));
        p.bindColorBuffer("jvr_Texture1", "DOFMap", 0);
        p.bindDepthBuffer("jvr_Texture0", "DOFMap");

        // render quad
        p.drawQuad(sm, "DOF");

        // ///////////////////////////////////////////////////////////////////////////////

        RenderWindow w = new AwtRenderWindow(p, 960, 600);
        w.addKeyListener(this);
        w.addMouseListener(this);
        w.addWindowListener(this);

        Viewer viewer = new Viewer(w);

        Log.info(this.getClass(), "1 - decrease wave amplitude.");
        Log.info(this.getClass(), "2 - increase wave amplitude.");

        long t0 = System.nanoTime();
        long delta = 0;
        while (viewer.isRunning()) {
            long start = System.currentTimeMillis();

            cam.setAspectRatio(camAspectRatio);
            mirrorCam.setAspectRatio(-camAspectRatio);

            // update mirror camera transformation
            Transform camTrans = cam.getTransform();
            camTrans = clipPlane.getTransform().invert().mul(camTrans); // transform to clip plane space
            camTrans = clipPlane.getTransform().mul(Transform.scale(1, 1, -1).mul(camTrans));
            mirrorCam.setTransform(camTrans);

            long tn = System.nanoTime();
            float tns = (float) ((tn - t0) * 1e-9);
            waveTime.setUniform("waveTime", new UniformFloat(tns));
            waveScale.setUniform("waveScale", new UniformFloat(waveScaleValue));

            // update skybox
            sky1.setTransform(Transform.translate(cam.getEyeWorldTransform(root).getMatrix().translation()).mul(Transform.rotateYDeg(240)));
            sky2.setTransform(Transform.translate(mirrorCam.getEyeWorldTransform(root).getMatrix().translation()).mul(Transform.rotateYDeg(240)));
            viewer.display();

            if (pressedKeys.contains('1'))
                waveScaleValue = Math.max(0.0f, waveScaleValue - 0.2f * delta / 1000f);
            if (pressedKeys.contains('2'))
                waveScaleValue += 0.2f * delta / 1000f;

            delta = System.currentTimeMillis() - start; // calculate render duration
            super.move(delta);
        }
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
