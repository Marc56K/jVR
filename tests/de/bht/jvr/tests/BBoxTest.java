package de.bht.jvr.tests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.ClipPlaneNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.Traverser;
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
public class BBoxTest implements Traverser {
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            new BBoxTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public BBoxTest() throws Exception {
        SceneNode scene = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        ShapeNode shape = Finder.find(scene, ShapeNode.class, "Teapot01_Shape");
        Geometry geo = shape.getGeometry();

        ShapeNode s1 = new ShapeNode("shape1", geo, null);
        ShapeNode s2 = new ShapeNode("shape2", geo, null);

        s1.setTransform(Transform.translate(-10, 0, 0));
        s2.setTransform(Transform.rotateY((float) (Math.PI / 4)));

        GroupNode root = new GroupNode("root");
        root.addChildNodes(s1);

        GroupNode g = new GroupNode("g");
        g.addChildNode(s2);
        g.setTransform(Transform.translate(0, 5, 0));

        root.addChildNode(g);

        Printer.print(root);
        // BBoxUpdater updater = new BBoxUpdater();
        // updater.update(root);

        root.accept(this);
    }

    @Override
    public boolean enter(GroupNode node) {
        System.out.println(node.getName() + " : " + node.getBBox());
        return true;
    }

    @Override
    public boolean leave(GroupNode node) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(CameraNode node) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(ClipPlaneNode node) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(LightNode node) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean visit(ShapeNode node) {

        System.out.println(node.getName() + " : " + node.getBBox());
        return true;
    }
}
