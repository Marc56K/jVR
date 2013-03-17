package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
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

public class AutoAmbientTest extends AbstractAutoTest{
    
    public AutoAmbientTest() {
        super("AutoAmbientTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        GroupNode root = new GroupNode("scene root");
        
        SceneNode teapot = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        teapot.setTransform(Transform.translate(0f, -0.6f, 0f));
        GroupNode teapotRotor = new GroupNode();
        teapotRotor.addChildNode(teapot);
        
        PointLightNode light0 = new PointLightNode("sun0");
        light0.setTransform(Transform.translate(3, -3, 1));
        light0.setAmbientColor(new Color(0.0f, 0.0f, 1.0f));
        light0.setDiffuseColor(new Color(1.0f, 0.0f, 0.0f));
        light0.setSpecularColor(new Color(1.0f, 1.0f, 1.0f));

        PointLightNode light1 = new PointLightNode("sun1");
        light1.setTransform(Transform.translate(-3, -3, 1));
        light1.setAmbientColor(new Color(0.0f, 0.0f, 1.0f));
        light1.setDiffuseColor(new Color(0.0f, 1.0f, 0.0f));
        light1.setSpecularColor(new Color(1.0f, 1.0f, 1.0f));

        camera = new CameraNode("camera", 4f / 3f, 60);
        camera.setTransform(Transform.translate(0, 0, 3));

        root.addChildNodes(teapotRotor, light0, light1, camera);
        
        this.pipeline = new Pipeline(root);
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));
        pipeline.switchCamera(camera);
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        return pipeline;
    }

}
