package de.bht.jvr.core;

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
 *
 *
 * Contains the result of a picking traversal.
 * 
 * @author Marc Roßbach
 */
public class PickingResult {
    private ShapeNode node;
    private Vector3 pickingPoint;
    private NodePath path = null;

    public PickingResult(ShapeNode node, Vector3 pickingPoint, NodePath path) {
        this.node = node;
        this.pickingPoint = pickingPoint;
        this.path = path;
    }

    /**
     * Gets the picked point in world space.
     * 
     * @return the picked point
     */
    public Vector3 getPickingPoint() {
        return pickingPoint;
    }

    /**
     * Gets the picked shape node.
     * 
     * @return the shape node
     */
    public ShapeNode getShapeNode() {
        return node;
    }

    /**
     * Gets the path to the picked shape node.
     * 
     * @return the node path
     */
    public NodePath getNodePath() {
        return path;
    }
}
