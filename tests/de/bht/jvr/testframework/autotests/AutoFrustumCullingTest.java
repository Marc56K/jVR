package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
//import de.bht.jvr.core.pipeline.PipelineCommandPtr;
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

public class AutoFrustumCullingTest extends AbstractAutoTest {

    public AutoFrustumCullingTest() {
        super("AutoFrustumCullingTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        int k = 5;
        GroupNode root = new GroupNode();
        SceneNode teapot = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        teapot.setTransform(Transform.scale(10));
        for (int x = -k; x < k; x++)
            for (int y = -k; y < k; y++)
                for (int z = -k; z < k; z++) {
                    GroupNode n = new GroupNode();
                    n.setTransform(Transform.translate(x * 50, y * 50, z * 50));
                    n.addChildNode(teapot);
                    root.addChildNode(n);
                }
        // root.addChildNode(teapot);
        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        camera = new CameraNode("cam1", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 0, 0));
        root.addChildNode(camera);

        // Pipeline//////////////////////////////////////
        pipeline = new Pipeline(root);
//        PipelineCommandPtr vfc = pipeline.setViewFrustumCullingMode(0);
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, new Color(0.5f, 0.5f, 0.5f));
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        return pipeline;
    }
    
}
