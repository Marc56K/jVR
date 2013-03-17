package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.GroupNode;
//import de.bht.jvr.core.Printer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNodeMerger;
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

public class AutoShapeNodeMergerTest extends AbstractAutoTest{
    
    public AutoShapeNodeMergerTest() {
        super("AutoShapeNodeMergerTest");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        SceneNode model = ColladaLoader.load(new File("./data/meshes/house3/models/house3.dae"));
        
        ShapeNodeMerger.merge((GroupNode) model, "(?!Translucent).*", "", "Opaque_Objects_Shape"); // <---------
        ShapeNodeMerger.merge((GroupNode) model, "Translucent", "Translucent", "Translucent_Objects_Shape"); // <---------
        
        model.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(0.05f))); // transform
        DirectionalLightNode dLight = new DirectionalLightNode();
        dLight.setTransform(Transform.rotateXDeg(-75));
        dLight.setIntensity(1.2f);

        camera = new CameraNode("MyCamera", 16f / 10f, 60);
        camera.setTransform(Transform.translate(20, 10, -25)); // transform the
                                                        // camera

        GroupNode root = new GroupNode("MyRoot");
        root.addChildNodes(camera, model, dLight);

        // pipeline
        // //////////////////////////////////////////////////////////////
        pipeline = new Pipeline(root);
        pipeline.setBackFaceCulling(false); // disable back face culling
        pipeline.setViewFrustumCullingMode(1);
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, Color.gray); // clear screen
        // render only opak objects
        // p.setUniform("jvr_Material_Ambient", new UniformColor(Color.white));
        pipeline.drawGeometry("AMBIENT", "(?!Translucent).*");
        // p.setBlendFunc(GL.GL_ONE, GL.GL_ZERO);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", "(?!Translucent).*");
        // now render the transparent objects
        pipeline.drawGeometry("AMBIENT", "Translucent", true);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", "Translucent");
        return pipeline;
    }
    
}
