package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformBool;
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

public class AutoFinalizerTest extends AbstractAutoTest {

    public AutoFinalizerTest() {
        super("AutoFinalizerTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode polyDuck = ColladaLoader.load(new File("data/meshes/polyDuck.dae"));
        ShapeNode polyDuckShape = Finder.find(polyDuck, ShapeNode.class, "duck-geometry_Shape");

        ShaderMaterial mat = ShaderMaterial.makePhongShaderMaterial();
        polyDuckShape.setMaterial(mat);

//        Texture2D tex2 = new Texture2D(new File("data/textures/flower.jpg"));
        mat.setTexture("LIGHTING", "jvr_Texture0", new Texture2D(new File("data/textures/fire.png")));
        mat.setUniform("LIGHTING", "jvr_UseTexture0", new UniformBool(true));

        polyDuckShape.setTransform(Transform.scale(0.5f));
        root.addChildNode(polyDuckShape);

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        camera = new CameraNode("cam1", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 0, 200));
        root.addChildNode(camera);

        // Pipeline//////////////////////////////////////
        pipeline = new Pipeline(root);
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        return pipeline;
    }
    
}
