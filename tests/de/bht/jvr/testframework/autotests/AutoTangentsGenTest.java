package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
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

public class AutoTangentsGenTest extends AbstractAutoTest{
    
    public AutoTangentsGenTest() {
        super("AutoTangentsGenTest");
    }

    public Geometry loadGeometry() throws Exception {
        // load collada-file
        SceneNode scene = ColladaLoader.load(new File("data/meshes/teapot_without_tangents.dae"));

        // extract the geometry
        Geometry geo = Finder.findGeometry(scene, null);

        return geo;
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        Geometry geo = loadGeometry();

        // create the shader program
        ShaderProgram ambientProg = new ShaderProgram(new File("data/shader/ambient.vs"), new File("data/shader/ambient.fs"));
        ShaderProgram lightingProg = new ShaderProgram(new File("data/shader/bumpmapping.vs"), new File("data/shader/bumpmapping.fs"));

        // create the shader material
        ShaderMaterial mat = new ShaderMaterial();
        mat.setShaderProgram("AMBIENT", ambientProg); // set the ambient shader
                                                      // program
        mat.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0, 0, 0, 1))); // set
                                                                                                    // ambient
                                                                                                    // color
                                                                                                    // of
                                                                                                    // the
                                                                                                    // material
                                                                                                    // to
                                                                                                    // black
        mat.setShaderProgram("LIGHTING", lightingProg); // set the lighting
                                                        // shader program
        // set the material
        mat.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new Color(0.7f, 0.6f, 0.18f, 1.0f)));
        mat.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(new Color(0.7f, 0.6f, 0.18f, 1.0f)));
        mat.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(6));

        // create the shape node
        ShapeNode shape = new ShapeNode("MyShape", geo, mat);
        shape.setTransform(Transform.rotateXDeg(-90)); // transform the shape
                                                       // node

        // create a camera
        camera = new CameraNode("MyCamera", 4f / 3f, 60);
        camera.setTransform(Transform.translate(0, 0.75f, 3)); // transform the
                                                            // camera

        // create a point light
        PointLightNode pLight = new PointLightNode();
        pLight.setTransform(Transform.translate(0, 5, 10)); // transform the
                                                            // point light

        // create the scene graph
        GroupNode root = new GroupNode("MyRoot");
        root.addChildNodes(shape, camera, pLight);

        // to render the scene we need a rendering pipeline
        pipeline = new Pipeline(root);
        pipeline.clearBuffers(true, true, Color.gray); // clear depth and color buffer
                                                // and set clear color to gray
        pipeline.switchCamera(camera);
        pipeline.drawGeometry("AMBIENT", null); // draw ambient pass
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null); // draw
                                                                  // lighting
                                                                  // pass
        return pipeline;
    }
}
