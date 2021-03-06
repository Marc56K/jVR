package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.renderer.NewtRenderWindow;
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
public class DeferredShadingTest2 extends TestBase implements WindowListener {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new DeferredShadingTest2();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    CameraNode cam;

    public DeferredShadingTest2() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode polyDuck = ColladaLoader.load(new File("data/meshes/testwelt02.dae"));
        // ShapeNode polyDuckShape = Finder.find(polyDuck, ShapeNode.class,
        // "duck-geometry_Shape");
        // polyDuckShape.setMaterial(ShaderMaterial.makePhongShaderMaterial());
        polyDuck.setTransform(Transform.scale(1.0f).mul(Transform.rotateXDeg(-90)));
        root.addChildNode(polyDuck);

        for (int x = 0; x < 7; x++)
            for (int z = 0; z < 7; z++) {
                SpotLightNode sLight = new SpotLightNode();
                sLight.setCastShadow(true);
                sLight.setShadowBias(0.9f);
                sLight.setTransform(Transform.translate(-x * 130 + 300, 100, -z * 130 + 300).mul(Transform.rotateXDeg(-90)));
                root.addChildNode(sLight);
            }

        cam = new CameraNode("cam", 4f / 3f, 60f);
        cam.setTransform(Transform.translate(0, 0, 5));
        root.addChildNode(cam);
        cams.add(cam);

        // deferred pipeline //////////////////////////////////////
        ShaderMaterial deferredMat = ShaderMaterial.makeDeferredShadingPipelineMaterial();
        Pipeline p = new Pipeline(root);
        p.createFrameBufferObject("SHADOWMAP", true, 0, 1024, 1024, 0);
        p.createFrameBufferObject("GBUFFER", false, 6, GL2GL3.GL_RGBA16F, 1.0f, 0);
        p.switchFrameBufferObject("GBUFFER");
        p.switchCamera(cam);
        p.setBlendFunc(GL.GL_ONE, GL.GL_ZERO);
        p.clearBuffers(true, new boolean[] { true, true, true, true, true, true }, new Color(0, 0, 0, 0));
        p.drawGeometry("ATTRIBPASS", null);
        p.switchFrameBufferObject(null);
        p.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        p.bindColorBuffer("jvr_DS_AmbientColor", "GBUFFER", 0);
        p.bindColorBuffer("jvr_DS_DiffuseColor", "GBUFFER", 1);
        p.bindColorBuffer("jvr_DS_SpecularColor", "GBUFFER", 2);
        p.bindColorBuffer("jvr_DS_Position", "GBUFFER", 3);
        p.bindColorBuffer("jvr_DS_WorldPosition", "GBUFFER", 4);
        p.bindColorBuffer("jvr_DS_Normal", "GBUFFER", 5);
        p.drawQuad(deferredMat, "DSAMBIENT");
        p.setBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        Pipeline lp = p.doLightLoop(false, true);
        lp.switchFrameBufferObject("SHADOWMAP");
        lp.clearBuffers(true, false, null);
        lp.switchLightCamera();
        lp.drawGeometry("AMBIENT", null);
        lp.switchFrameBufferObject(null);
        lp.switchCamera(cam);
        lp.bindDepthBuffer("jvr_ShadowMap", "SHADOWMAP");
        lp.drawQuad(deferredMat, "DSLIGHTING");
        // /////////////////////////////////////////////

        // classic pipeline
        // //////////////////////////////////////////////////////////////
        Pipeline op = new Pipeline(root);
        op.switchCamera(cam);
        op.clearBuffers(true, true, new Color(0, 0, 0, 0));
        // render the geometry without light
        op.drawGeometry("AMBIENT", null);
        // iterate all lights with shadow
        Pipeline olp = op.doLightLoop(false, true);
        // use the light source as camera
        olp.switchLightCamera();
        // create a depth map (1024x1024)
        olp.createFrameBufferObject("ShadowMap", true, 0, 1024, 1024, 0);
        // switch to depth map
        olp.switchFrameBufferObject("ShadowMap");
        // clear the depth map
        olp.clearBuffers(true, false, null);
        // render to the depth map
        olp.drawGeometry("AMBIENT", null);
        // switch back to screen buffer
        olp.switchFrameBufferObject(null);
        // switch back to normal camera
        olp.switchCamera(cam);
        // bind the depth map to the uniform jvr_ShadowMap
        olp.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
        // render the geometry for the active light
        olp.drawGeometry("LIGHTING", null);

        // //////////////////////////////////////////////////////////////////////

        RenderWindow w = new NewtRenderWindow(p, false);
        w.addKeyListener(this);
        w.addMouseListener(this);
        w.addWindowListener(this);
        w.setWindowTitle("ColladaPolyTest");

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

    @Override
    public void windowClose(RenderWindow win) {
        System.err.println("Closing: " + win);

    }

    @Override
    public void windowReshape(RenderWindow win, int width, int height) {
        System.err.println("Reshaping: " + win);
        cam.setAspectRatio((float) width / height);
    }
}
