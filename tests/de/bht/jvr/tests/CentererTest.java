package de.bht.jvr.tests;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Centerer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Vector3;
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
public class CentererTest {
    
    public static void main(String[] args) throws Exception{
        SceneNode scene = ColladaLoader.load(new File("data/meshes/teapot.dae"));
        Vector3 cv = new Vector3(50.0f, 0.0f, 0.0f);
        Transform[] centers = new Transform[] {Centerer.centerToCenter(scene), Centerer.centerToCenter(scene, cv), Centerer.centerToBottom(scene), Centerer.centerToBottom(scene, cv), Centerer.centerToTop(scene), Centerer.centerToTop(scene, cv), Centerer.centerToNear(scene), Centerer.centerToNear(scene, cv), Centerer.centerToFar(scene), Centerer.centerToFar(scene, cv), Centerer.centerToLeft(scene), Centerer.centerToLeft(scene, cv), Centerer.centerToRight(scene), Centerer.centerToRight(scene, cv)};        
        
        for(Transform elem: centers){
            System.out.println(elem);
        }
    }
    
}