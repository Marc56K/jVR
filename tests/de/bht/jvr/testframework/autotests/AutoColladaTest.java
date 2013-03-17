package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
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

public class AutoColladaTest extends AbstractAutoTest{
    
    public AutoColladaTest() {
        super("AutoColladaTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        GroupNode root = new GroupNode();

        SceneNode model = ColladaLoader.load(new File("data/meshes/car.dae"));
        model.setTransform(Transform.rotateXDeg(-90));
        root.addChildNode(model);

        DirectionalLightNode dLight = new DirectionalLightNode();
        dLight.setTransform(Transform.rotateXDeg(-75));
        dLight.setIntensity(1.2f);
        root.addChildNode(dLight);

        camera = new CameraNode("cam1", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 0, 200));
        root.addChildNode(camera);

        // Pipeline//////////////////////////////////////
        pipeline = new Pipeline(root);
        pipeline.setBackFaceCulling(false); // disable back face culling
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        pipeline.drawGeometry("AMBIENT", "", false);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", "");
        pipeline.drawGeometry("AMBIENT", "Translucent", true);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", "Translucent");
        return pipeline;
    }
    
}
