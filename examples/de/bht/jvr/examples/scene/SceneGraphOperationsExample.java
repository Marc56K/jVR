package de.bht.jvr.examples.scene;

import java.io.File;
import java.util.Collection;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.Printer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;

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
 *
 *
 * This basic sample demonstrates the following features:
 * - loading scenes from collada files
 * - find named nodes in a scene graph
 * - find materials in scene graph
 * - find geometries in scene graph
 * 
 * @author Marc Roßbach
 */

public class SceneGraphOperationsExample {

    /**
     * Extracting all ShapeNodes named 'Rohr...'
     * 
     * @param scene
     */
    public static void findAllExample(SceneNode scene) {
        System.out.println("Extracting all ShapeNodes named 'Rohr...':");
        Collection<ShapeNode> myShapes = Finder.findAll(scene, ShapeNode.class, "Rohr.*"); // now we use a regex

        for (ShapeNode s : myShapes)
            System.out.println(" Extracted ShapeNode: " + s);
    }

    /**
     * Extracting all Geometries of ShapeNodes named 'Pflanze...'
     * 
     * @param scene
     */
    public static void findAllGeometriesExample(SceneNode scene) {
        System.out.println("Extracting all Geometries of ShapeNodes named 'Pflanze...':");
        Collection<Geometry> myGeos = Finder.findAllGeometries(scene, "Pflanze.*");

        for (Geometry geo : myGeos)
            System.out.println(" Extracted geometry: " + geo);
    }

    /**
     * Extracting all Materials of ShapeNodes named 'Pflanze...'
     * 
     * @param scene
     */
    public static void findAllMaterialsExample(SceneNode scene) {
        System.out.println("Extracting all Materials of ShapeNodes named 'Pflanze...':");
        Collection<Material> myMaterials = Finder.findAllMaterials(scene, "Pflanze.*");

        for (Material mat : myMaterials)
            System.out.println(" Extracted material: " + mat);
    }

    /**
     * Extracting the SceneNode named 'Boot'
     * 
     * @param scene
     */
    public static void findExample(SceneNode scene) {
        System.out.println("Extracting the ShapeNode named 'Boot':");
        SceneNode myNode = Finder.find(scene, SceneNode.class, "Boot"); // you can also use a regex if you don't know the exact name

        if (myNode != null)
            System.out.println(" Extracted SceneNode: " + myNode);
        else
            System.out.println(" SceneNode not found.");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // load a large collada scene
            SceneNode scene = ColladaLoader.load(new File("data/meshes/testwelt01.dae"));

            // let's see what's in the scene
            Printer.print(scene);

            // operation examples
            findExample(scene); // find a named scene node
            findAllExample(scene); // find all scene nodes with similar names
            findAllMaterialsExample(scene); // find all materials
            findAllGeometriesExample(scene); // find all geometries
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
