package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.List;

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
 * The group node can contain several scene nodes.
 * 
 * @author Marc Roßbach
 */
public class GroupNode extends SceneNode {
    /** The child nodes. */
    private List<SceneNode> childNodes = new ArrayList<SceneNode>();

    /**
     * Instantiates a new group node.
     */
    public GroupNode() {}

    /**
     * Instantiates a new group node.
     */
    public GroupNode(SceneNode... nodes) {
        addChildNodes(nodes);
    }

    /**
     * Instantiates a new group node.
     * 
     * @param name
     *            the name
     */
    public GroupNode(String name) {
        setName(name);
    }

    /**
     * Instantiates a new group node.
     * 
     * @param name
     *            the name
     * @param transform
     *            the transform
     */
    public GroupNode(String name, Transform transform) {
        setName(name);
        setTransform(transform);
    }

    /**
     * Instantiates a new group node.
     */
    public GroupNode(String name, Transform transform, SceneNode... nodes) {
        setName(name);
        setTransform(transform);
        addChildNodes(nodes);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.SceneNode#accept(de.bht.jvr.core.Traverser)
     */
    @Override
    public boolean accept(Traverser traverser) {
        if (traverser.enter(this))
            for (SceneNode node : childNodes)
                if (!node.accept(traverser))
                    break;
        return traverser.leave(this);
    }

    /**
     * Adds the child node.
     * 
     * @param node
     *            the node
     * @return the group node
     */
    public GroupNode addChildNode(SceneNode node) {
        childNodes.add(node);
        node.setParentNode(this);
        return this;
    }

    /**
     * Adds the child nodes.
     * 
     * @param nodes
     *            the nodes
     * @return the group node
     */
    public GroupNode addChildNodes(SceneNode... nodes) {
        for (SceneNode n : nodes)
            addChildNode(n);
        return this;
    }

    /**
     * Gets the child nodes.
     * 
     * @return the child nodes
     */
    public List<SceneNode> getChildNodes() {
        return childNodes;
    }

    /**
     * Removes all child nodes.
     * 
     * @return the group node
     */
    public GroupNode removeAllChildNodes() {
        for (SceneNode node : childNodes)
            node.removeParentNode(this);
        childNodes.clear();
        return this;
    }

    /**
     * Removes the child node.
     * 
     * @param node
     *            the node
     * @return the group node
     */
    public GroupNode removeChildNode(SceneNode node) {
        childNodes.remove(node);
        node.removeParentNode(this);
        return this;
    }

    @Override
    protected void updateBBox() {
        super.bBox = new BBox();
        for (SceneNode node : childNodes)
            super.bBox = super.bBox.grow(node.getBBox(), node.getTransform().getMatrix());
    }
}
