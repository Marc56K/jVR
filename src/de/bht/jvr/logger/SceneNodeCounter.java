package de.bht.jvr.logger;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.ClipPlaneNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
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

public class SceneNodeCounter implements Traverser {

    private int shapeNodeCount = 0;
    private int groupNodeCount = 0;
    private int lightNodeCount = 0;
    private int clipNodeCount = 0;
    private int cameraNodeCount = 0;
    
    public static void CountSceneNodes(SceneNode root) {
        new SceneNodeCounter(root).print();
    }
    
    private SceneNodeCounter(SceneNode root){
        root.accept(this);
    }
    
    private void print() {
        System.out.println("GroupNodes:  " + this.groupNodeCount);
        System.out.println("ShapeNodes:  " + this.shapeNodeCount);        
        System.out.println("LightNodes:  " + this.lightNodeCount);
        System.out.println("ClipNodes:   " + this.clipNodeCount);
        System.out.println("CameraNodes: " + this.cameraNodeCount);
    }
    
    @Override
    public boolean enter(GroupNode node) {
        this.groupNodeCount++;
        return true;
    }

    @Override
    public boolean leave(GroupNode node) {
        return true;
    }

    @Override
    public boolean visit(CameraNode node) {
        this.cameraNodeCount++;
        return true;
    }

    @Override
    public boolean visit(ClipPlaneNode node) {
        this.clipNodeCount++;
        return true;
    }

    @Override
    public boolean visit(LightNode node) {
        this.lightNodeCount++;
        return true;
    }

    @Override
    public boolean visit(ShapeNode node) {
        this.shapeNodeCount++;
        return true;
    }

}
