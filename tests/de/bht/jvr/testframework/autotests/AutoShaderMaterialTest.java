package de.bht.jvr.testframework.autotests;

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
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformBool;
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

public class AutoShaderMaterialTest extends AbstractAutoTest{

    public AutoShaderMaterialTest() {
        super("AutoShaderMaterialTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
     // camera
        camera = new CameraNode("cam", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 20, 60));

        // load collada files
        SceneNode sphereScene = ColladaLoader.load(new File("./data/meshes/teapot.dae"));
        SceneNode planeScene = ColladaLoader.load(new File("./data/meshes/plane.dae"));

        // extract geometries
        Geometry sphereGeo = Finder.findGeometry(sphereScene, ".*");

        // create the ambient shader program
        Shader ambientVS = new Shader(new File("data/shader/ambient.vs"));
        Shader ambientFS = new Shader(new File("data/shader/ambient.fs"));
        ShaderProgram ambientProg = new ShaderProgram(ambientVS, ambientFS);
        // create the lighting shader program
        Shader lightingVS = new Shader(new File("data/shader/phong.vs"));
        Shader lightingFS = new Shader(new File("data/shader/phong.fs"));
        ShaderProgram lightingProg = new ShaderProgram(lightingVS, lightingFS);
        // load the texture
        Texture2D tex = new Texture2D(new File("data/textures/oldwood.jpg"));
        // create the shader material
        ShaderMaterial mat = new ShaderMaterial();
        mat.setShaderProgram("AMBIENT", ambientProg);
        mat.setTexture("AMBIENT", "jvr_Texture0", tex);
        mat.setUniform("AMBIENT", "jvr_UseTexture0", new UniformBool(true));
        mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0.2f, 0.2f, 0.2f, 1)));
        mat.setShaderProgram("LIGHTING", lightingProg);
        mat.setTexture("LIGHTING", "jvr_Texture0", tex);
        mat.setUniform("LIGHTING", "jvr_UseTexture0", new UniformBool(true));
        mat.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(10.0f));
        mat.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(Color.white));
        mat.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(Color.white));

        // create shape nodes
        ShapeNode sphereShape = new ShapeNode("sphere", sphereGeo, mat);
        sphereShape.setTransform(Transform.scale(20).mul(Transform.rotateX((float) (-Math.PI / 2))));
        planeScene.setTransform(Transform.scale(700).mul(Transform.rotateX((float) (-Math.PI / 2))));

        // create spot light
        SpotLightNode sLight = new SpotLightNode();
        sLight.setTransform(Transform.translate(30, 600, 600).mul(Transform.rotateYDeg(20)).mul(Transform.rotateXDeg(-45)));
        sLight.setSpotCutOff(270);
        // sLight.setCastShadow(true);

        // this.cams.add(sLight);

        // create scene graph
        GroupNode root = new GroupNode("root");
        root.addChildNodes(camera, sphereShape, planeScene, sLight);

        // pipeline
        pipeline = new Pipeline(root);
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, Color.white);
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null, false);
        return pipeline;
    }
    
}
