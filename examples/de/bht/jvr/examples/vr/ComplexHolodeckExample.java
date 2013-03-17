package de.bht.jvr.examples.vr;

import de.bht.jvr.util.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.ClassicControllerRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.DisconnectionEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.GuitarHeroRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.StatusEvent;
import de.bht.jvr.applicationbase.AbstractHolodeckApp;
import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.ClipPlaneNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.VRCameraNode;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.examples.shader.SkyBoxCreator;
import de.bht.jvr.input.KeyEvent;
import de.bht.jvr.input.MouseEvent;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.math.Vector4;

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

public class ComplexHolodeckExample extends AbstractHolodeckApp {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, 0, -1));
        try {
            new ComplexHolodeckExample();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GroupNode root = new GroupNode();

    private GroupNode mirrorSpectator = new GroupNode("mirrorSpectator");
    private Map<String, VRCameraNode> mirrorCams = new HashMap<String, VRCameraNode>();
    private Map<String, SceneNode> skyBoxes = new HashMap<String, SceneNode>();
    private ClipPlaneNode clipPlane = null;
    private ShaderMaterial mirrorMat = null;
    private float waveScaleValue = 0.04f;
    private long t0 = System.nanoTime();

    public ComplexHolodeckExample() throws Exception {
        super.setSpectator(new Vector3(0, 2, 40), 0, 0);
        super.setTrackerHost("192.168.0.1");
        // super.setFullscreen(false);
        super.setFSAA(4);
        super.setMultiThreading(true);
        super.setVSync(true);
        super.start();
    }

    @Override
    public Pipeline getPipeline(VRCameraNode cam, SceneNode root) throws Exception {
        Pipeline p = new Pipeline(root);
        p.setBackFaceCulling(false);
        // create shadow map
        p.createFrameBufferObject("ShadowMap", true, 0, 2048, 2048, 0);
        // create mirror map
        p.createFrameBufferObject("MirrorMap", false, 1, 0.50f, 0);

        // render from normal camera
        p.clearBuffers(true, false, null);
        p.switchCamera(cam);
        p.setDepthTest(false);
        // draw sky box (use the camera name as key)
        p.drawGeometry(cam.getName(), null);
        p.setDepthTest(true);
        // disable clipping
        p.setUniform("jvr_UseClipPlane0", new UniformBool(false));
        // render the geometry without light
        p.drawGeometry("AMBIENT", null);
        // iterate all lights with shadow
        Pipeline lp = p.doLightLoop(false, true);
        // disable clipping
        lp.setUniform("jvr_UseClipPlane0", new UniformBool(false));
        // use the light source as camera
        lp.switchLightCamera();
        // switch to depth map
        lp.switchFrameBufferObject("ShadowMap");
        // clear the depth map
        lp.clearBuffers(true, false, null);
        // render to the depth map
        lp.drawGeometry("AMBIENT", null);
        // switch back to screen buffer
        lp.switchFrameBufferObject(null);
        // switch back to normal camera
        lp.switchCamera(cam);
        // bind the depth map to the uniform jvr_ShadowMap
        lp.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
        // render the geometry for the active light
        lp.drawGeometry("LIGHTING", null);

        // render from mirror camera to texture
        p.unsetUniforms();
        p.switchFrameBufferObject("MirrorMap");
        p.clearBuffers(true, true, Color.white);
        p.switchCamera(mirrorCams.get("mirror_" + cam.getName()));
        p.setDepthTest(false);
        // draw sky box (use the camera name as key)
        p.drawGeometry("mirror_" + cam.getName(), null);
        p.setDepthTest(true);
        // render the geometry without light
        p.drawGeometry("AMBIENT", null);
        // iterate all lights with shadow
        lp = p.doLightLoop(false, true);
        // bind the depth map to the uniform jvr_ShadowMap
        lp.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
        // render the geometry for the active light
        lp.drawGeometry("LIGHTING", null);

        // render mirror plane
        p.switchFrameBufferObject(null);
        p.switchCamera(cam);
        p.bindColorBuffer("jvr_MirrorTexture", "MirrorMap", 0);
        p.drawGeometry("MIRRORPASS", null);

        return p;
    }

    @Override
    public SceneNode getSceneGraph() throws Exception {
        // load sky box
        SkyBoxCreator sbc = new SkyBoxCreator("data/textures/skybox/mountain_ring");

        SceneNode skyL = sbc.getSkyBox("cam_front_l");
        skyBoxes.put("cam_front_l", skyL);
        SceneNode skyML = sbc.getSkyBox("mirror_cam_front_l");
        skyBoxes.put("mirror_cam_front_l", skyML);

        SceneNode skyR = sbc.getSkyBox("cam_front_r");
        skyBoxes.put("cam_front_r", skyR);
        SceneNode skyMR = sbc.getSkyBox("mirror_cam_front_r");
        skyBoxes.put("mirror_cam_front_r", skyMR);

        root.addChildNodes(skyL, skyML, skyR, skyMR);

        // load the world
        SceneNode scene = ColladaLoader.load(new File("data/meshes/testwelt02.dae"));
        scene.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.05f)));
        root.addChildNode(scene);

        // create spot light
        SpotLightNode sLight = new SpotLightNode();
        sLight.setTransform(Transform.translate(-25, 40, 25).mul(Transform.rotateYDeg(-45).mul(Transform.rotateXDeg(-65))));
        sLight.setIntensity(1.2f);
        sLight.setSpotCutOff(40);
        sLight.setCastShadow(true);
        sLight.setShadowBias(0.15f);
        root.addChildNode(sLight);

        // clipping plane
        clipPlane = new ClipPlaneNode();
        clipPlane.setTransform(Transform.translate(0, -1.42f, 0).mul(Transform.rotateXDeg(90)));
        root.addChildNode(clipPlane);

        // mirror plane
        SceneNode mirrorPlane = ColladaLoader.load(new File("data/meshes/plane.dae"));
        mirrorPlane.setTransform(Transform.translate(0, -1.5f, 0).mul(Transform.rotateXDeg(-90).mul(Transform.scale(1000))));
        root.addChildNode(mirrorPlane);

        // mirror cameras
        VRCameraNode mirrorCamLeft = new VRCameraNode("mirror_cam_front_l", Transform.translate(0, 0, -1.17f), new Vector4(1.865f, -1.865f, 1.165f, -1.165f), true, new Transform());
        mirrorCams.put("mirror_cam_front_l", mirrorCamLeft);
        VRCameraNode mirrorCamRight = new VRCameraNode("mirror_cam_front_r", Transform.translate(0, 0, -1.17f), new Vector4(1.865f, -1.865f, 1.165f, -1.165f), false, new Transform());
        mirrorCams.put("mirror_cam_front_r", mirrorCamRight);
        mirrorSpectator.addChildNodes(mirrorCamLeft, mirrorCamRight);
        root.addChildNode(mirrorSpectator);

        // mirror material
        ShaderProgram prog = new ShaderProgram(new File("data/shader/mirror.vs"), new File("data/shader/mirror.fs"));
        mirrorMat = new ShaderMaterial("MIRRORPASS", prog);

        Finder.find(mirrorPlane, ShapeNode.class, null).setMaterial(mirrorMat);

        return root;
    }

    @Override
    public boolean onKeyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onKeyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onKeyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onMouseWheelMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiButtonsEvent(WiimoteButtonsEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiClassicControllerInsertedEvent(ClassicControllerInsertedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiClassicControllerRemovedEvent(ClassicControllerRemovedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiDisconnectionEvent(DisconnectionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiExpansionEvent(ExpansionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiGuitarHeroInsertedEvent(GuitarHeroInsertedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiGuitarHeroRemovedEvent(GuitarHeroRemovedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiIrEvent(IREvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiMotionSensingEvent(MotionSensingEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiNunchukInsertedEvent(NunchukInsertedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiNunchukRemovedEvent(NunchukRemovedEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onWiiStatusEvent(StatusEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void simulate(double renderDuration) {
        // update waves
        if (mirrorMat != null) {
            float tns = (float) ((System.nanoTime() - t0) * 1e-9);
            mirrorMat.setUniform("MIRRORPASS", "waveTime", new UniformFloat(tns));
            mirrorMat.setUniform("MIRRORPASS", "waveScale", new UniformFloat(waveScaleValue));
        }

        // update mirror spectator transformation
        if (clipPlane != null) {
            Transform camTrans = super.getSpectator().getTransform();
            camTrans = clipPlane.getTransform().invert().mul(camTrans); // transform to clip plane space
            camTrans = clipPlane.getTransform().mul(Transform.scale(1, 1, -1).mul(camTrans));
            mirrorSpectator.setTransform(camTrans);
        }

        // update head transformation to mirror cams
        for (VRCameraNode cams : mirrorCams.values())
            cams.setHeadTransform(super.getHeadTransform());

        // update sky boxes
        for (SceneNode cam : super.getSpectator().getChildNodes()) {
            SceneNode skyBox = skyBoxes.get(cam.getName());
            if (skyBox != null && cam instanceof VRCameraNode) {
                VRCameraNode vrCam = (VRCameraNode) cam;
                skyBox.setTransform(Transform.translate(vrCam.getEyeWorldTransform(root).getMatrix().translation()));
            }
        }

        for (String camsName : mirrorCams.keySet()) {
            SceneNode skyBox = skyBoxes.get(camsName);
            SceneNode cam = mirrorCams.get(camsName);
            if (skyBox != null && cam instanceof VRCameraNode) {
                VRCameraNode vrCam = (VRCameraNode) cam;
                skyBox.setTransform(Transform.translate(vrCam.getEyeWorldTransform(root).getMatrix().translation()));
            }
        }
    }
}
