package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
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

public class AutoTransparencyOrderTest extends AbstractAutoTest{
    
    public AutoTransparencyOrderTest() {
        super("AutoTransparencyOrderTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        camera = new CameraNode("cam", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 20, 60));

        // load collada files
        SceneNode sphereScene = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        SceneNode planeScene = ColladaLoader.load(new File("data/meshes/plane.dae"));

        // extract geometries
        Geometry sphereGeo = Finder.findGeometry(sphereScene, ".*");
        Geometry planeGeo = Finder.findGeometry(planeScene, ".*");

        // create shader programs
        ShaderProgram ambientProg = new ShaderProgram(new File("data/shader/ambient.vs"), new File("data/shader/ambient.fs"));
        ShaderProgram lightingProg = new ShaderProgram(new File("data/shader/phong.vs"), new File("data/shader/phong.fs"));

        // create shader material
        ShaderMaterial mat = new ShaderMaterial();
        mat.setShaderProgram("AMBIENT", ambientProg);
        mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0, 1, 0, 0.1f)));
        mat.setShaderProgram("LIGHTING", lightingProg);
        mat.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new Color(0, 1, 0, 0.1f)));
        mat.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(new Color(1, 1, 1, 0.1f)));
        mat.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(20.0f));

        ShaderMaterial mat2 = new ShaderMaterial();
        mat2.setShaderProgram("AMBIENT", ambientProg);
        mat2.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(1, 0, 0, 1)));
        mat2.setShaderProgram("LIGHTING", lightingProg);
        mat2.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new Color(1, 0, 0, 1)));
        mat2.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(new Color(1, 1, 1, 1)));
        mat2.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(20.0f));

        // create root
        GroupNode root = new GroupNode("root");

        // create shape nodes
        for (int i = 1; i < 6; i++) {
            ShapeNode sphereShape = new ShapeNode("sphere", sphereGeo, mat);
            sphereShape.setTransform(Transform.translate(0, 0, -i * 40).mul(Transform.scale(20).mul(Transform.rotateX((float) (-Math.PI / 2)))));
            root.addChildNode(sphereShape);
        }

        ShapeNode planeShape = new ShapeNode("plane", planeGeo, mat2);
        planeShape.setTransform(Transform.scale(700).mul(Transform.rotateX((float) (-Math.PI / 2))));

        // create spot light
        DirectionalLightNode sLight = new DirectionalLightNode();
        sLight.setTransform(Transform.rotateXDeg(-75));

        root.addChildNodes(camera, planeShape, sLight);

        // pipeline
        pipeline = new Pipeline(root);
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, new Color(1, 1, 1, 0));
        pipeline.drawGeometry("AMBIENT", null, true);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null, true);
        return pipeline;
    }
    
}
