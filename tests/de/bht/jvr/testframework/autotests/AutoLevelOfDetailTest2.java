package de.bht.jvr.testframework.autotests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
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

public class AutoLevelOfDetailTest2 extends AbstractAutoTest{

    public AutoLevelOfDetailTest2() {
        super("AutoLevelOfDetailTest2");
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        GroupNode root = new GroupNode();

        Geometry lod4 = Finder.findGeometry(ColladaLoader.load(new File("data/meshes/teapot_very_low.dae")), null);
        Geometry lod3 = Finder.findGeometry(ColladaLoader.load(new File("data/meshes/teapot_lower.dae")), null);
        Geometry lod2 = Finder.findGeometry(ColladaLoader.load(new File("data/meshes/teapot_low.dae")), null);
        Geometry lod1 = Finder.findGeometry(ColladaLoader.load(new File("data/meshes/teapot_medium.dae")), null);
        Geometry lod0 = Finder.findGeometry(ColladaLoader.load(new File("data/meshes/teapot.dae")), null);
        Material mat = ShaderMaterial.makePhongShaderMaterial();

        ShapeNode teapot = new ShapeNode();
        teapot.setTransform(Transform.rotateXDeg(-90).mul(Transform.scale(10)));
        teapot.setMaterial(mat);
        teapot.addLODGeometry(0, lod0);
        teapot.addLODGeometry(40, lod1);
        teapot.addLODGeometry(80, lod2);
        teapot.addLODGeometry(120, lod3);
        teapot.addLODGeometry(160, lod4);

        GroupNode all = new GroupNode();

        GroupNode cluster = new GroupNode();
        int k = 2;
        for (int x = 0; x < k; x++)
            for (int y = 0; y < k; y++)
                for (int z = 0; z < k; z++) {
                    GroupNode n = new GroupNode();
                    n.setTransform(Transform.translate(x * 50, y * 50, z * 50));
                    n.addChildNode(teapot);
                    cluster.addChildNode(n);
                }

        int h = 5;
        for (int xx = 0; xx < h; xx++)
            for (int yy = 0; yy < h; yy++)
                for (int zz = 0; zz < h; zz++) {
                    GroupNode g = new GroupNode();
                    g.setTransform(Transform.translate(xx * 50 * k, yy * 50 * k, zz * 50 * k));
                    g.addChildNode(cluster);
                    all.addChildNode(g);
                }
//        System.out.println("Count: " + h * h * h * k * k * k);
        all.setTransform(Transform.translate(-25 * k * h, -25 * k * h, -25 * k * h));
        root.addChildNode(all);

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
//        PipelineCommandPtr vfc;
//        vfc = pipeline.setViewFrustumCullingMode(2);
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, new Color(121, 188, 255));
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        return pipeline;
    }
    
}
