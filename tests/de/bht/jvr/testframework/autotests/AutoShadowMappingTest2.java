package de.bht.jvr.testframework.autotests;

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
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.testframework.AbstractAutoTest;
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
 */

public class AutoShadowMappingTest2 extends AbstractAutoTest{
    
    public AutoShadowMappingTest2() {
        super("AutoShadowMappingTest2");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
     // camera
        camera = new CameraNode("cam", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 20, 60));

        // load collada files
        SceneNode teapotScene = ColladaLoader.load(new File("./data/meshes/teapot.dae"));
        SceneNode sphereScene = ColladaLoader.load(new File("./data/meshes/sphere.dae"));
        SceneNode planeScene = ColladaLoader.load(new File("./data/meshes/plane.dae"));

        // extract geometries
        Geometry teapotGeo = Finder.findGeometry(teapotScene, null);
        Geometry sphereGeo = Finder.findGeometry(sphereScene, null);
        Geometry planeGeo = Finder.findGeometry(planeScene, null);

        // create shader programs
        ShaderProgram ambientProg = new ShaderProgram(new File("./data/shader/ambient.vs"), new File("./data/shader/ambient.fs"));
        ShaderProgram lightingProg = new ShaderProgram(new File("./data/shader/phong.vs"), new File("./data/shader/phong.fs"));

        // create shader material
        ShaderMaterial mat = new ShaderMaterial();
        mat.setShaderProgram("AMBIENT", ambientProg);
        mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0, 0, 0, 1)));
        mat.setShaderProgram("LIGHTING", lightingProg);
        mat.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new Color(0, 1, 0, 1)));
        mat.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(new Color(1, 1, 1, 1)));
        mat.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(20.0f));

        // create shape nodes
        ShapeNode teapotShape = new ShapeNode("teapot", teapotGeo, mat);
        teapotShape.setTransform(Transform.translate(-50, 0, 0).mul(Transform.scale(20)).mul(Transform.rotateXDeg(-90)));

        ShapeNode sphereShape = new ShapeNode("sphere", sphereGeo, mat);
        sphereShape.setTransform(Transform.translate(0, 20, 0).mul(Transform.scale(25)));

        ShapeNode planeShape = new ShapeNode("plane", planeGeo, mat);
        planeShape.setTransform(Transform.scale(700).mul(Transform.rotateXDeg(-90)));

        // create spot light
        SpotLightNode sLight = new SpotLightNode();
        sLight.setTransform(Transform.translate(30, 60, 60).mul(Transform.rotateYDeg(20)).mul(Transform.rotateXDeg(-45)));
        sLight.setSpotCutOff(30);
        sLight.setShadowBias(0.7f);
        sLight.setCastShadow(true);

        SpotLightNode sLight2 = new SpotLightNode();
        sLight2.setTransform(Transform.translate(-90, 50, 40).mul(Transform.rotateYDeg(-40)).mul(Transform.rotateXDeg(-45)));
        sLight2.setSpotCutOff(30);
        sLight2.setShadowBias(0.7f);
        sLight2.setCastShadow(true);

        // create scene graph
        GroupNode root = new GroupNode("root");
        root.addChildNodes(camera, teapotShape, sphereShape, planeShape, sLight, sLight2);

        // pipeline
        // ////////////////////////////////////////////////////////////////////////
        pipeline = new Pipeline(root);

        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, new Color(0, 0, 0, 0));
        pipeline.drawGeometry("AMBIENT", null);

        Pipeline lp = pipeline.doLightLoop(false, true);
        lp.switchLightCamera();

        // create shadow map buffer (1024x1024)
        lp.createFrameBufferObject("ShadowMap", true, 0, 1024, 1024, 0);

        // switch to shadow map buffer
        lp.switchFrameBufferObject("ShadowMap");
        // clear shadow map buffer
        lp.clearBuffers(true, false, null);

        // render to fbo
        lp.drawGeometry("AMBIENT", null);

        // switch to p-buffer
        lp.switchFrameBufferObject(null);

        lp.switchCamera(camera);
        lp.bindDepthBuffer("jvr_ShadowMap", "ShadowMap");
        lp.drawGeometry("LIGHTING", null);
        return pipeline;
    }
    
}
