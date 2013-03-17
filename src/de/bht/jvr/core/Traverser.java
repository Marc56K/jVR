package de.bht.jvr.core;

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
 * The base class for all scene graph traversers. Defines the visitor side
 * interface for the Visitor Pattern. The client side is defined in the scene
 * node class.
 * 
 * @author Marc Roßbach
 */
public interface Traverser {

    /**
     * Enter a group node.
     * 
     * @param node
     *            the node
     * @return true, if successful
     */
    public abstract boolean enter(GroupNode node);

    /**
     * Leave a group node.
     * 
     * @param node
     *            the node
     * @return true, if successful
     */
    public abstract boolean leave(GroupNode node);

    /**
     * Visit a camera node.
     * 
     * @param node
     *            the node
     * @return true, if successful
     */
    public abstract boolean visit(CameraNode node);

    /**
     * Visit a clipping plane.
     * 
     * @param node
     *            the node
     * @return true, if successful
     */
    public abstract boolean visit(ClipPlaneNode node);

    /**
     * Visit a light node.
     * 
     * @param node
     *            the node
     * @return true, if successful
     */
    public abstract boolean visit(LightNode node);

    /**
     * Visit a shape node.
     * 
     * @param node
     *            the node
     * @return true, if successful
     */
    public abstract boolean visit(ShapeNode node);
}
