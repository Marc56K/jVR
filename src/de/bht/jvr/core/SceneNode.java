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
 * The abstract scene node.
 * 
 * @author Marc Roßbach
 */
public abstract class SceneNode {
    /** The name of the node. */
    private String name = "";

    /** The transformation of the node. */
    private Transform transform = new Transform();

    /** Render/Traverse the node. */
    private boolean enabled = true;

    /** The bounding box. */
    protected BBox bBox = new BBox();

    /** The bounding box needs an update. */
    protected boolean bBoxDirty = true;

    /** The parents of the scene node. */
    private List<SceneNode> parentNodes = new ArrayList<SceneNode>();
    
    private Animation animation = null;

    /**
     * Accept the traverser.
     * 
     * @param traverser
     *            the traverser
     * @return true, if successful
     */
    public abstract boolean accept(Traverser traverser);

    /**
     * Gets all parent nodes.
     * 
     * @return the parent nodes
     */
    public List<SceneNode> getAllParentNodes() {
        return new ArrayList<SceneNode>(parentNodes);
    }
    
    /**
     * Gets the animation.
     * 
     * @return the animation
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * Gets the bounding box.
     * 
     * @return the bounding box
     */
    public BBox getBBox() {
        if (bBoxDirty) {
            updateBBox();
            bBoxDirty = false;
        }
        return bBox;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the number of parent nodes.
     * 
     * @return
     */
    public int getNumParentNodes() {
        return parentNodes.size();
    }

    /**
     * Gets the last parent node.
     * 
     * @return the parent node
     */
    public SceneNode getParentNode() {
        if (parentNodes.size() > 0)
            return parentNodes.get(parentNodes.size() - 1);

        return null;
    }

    /**
     * Gets a parent node
     * 
     * @param index
     *            the index of the parent node
     * @return the parent node
     */
    public SceneNode getParentNode(int index) {
        if (index >= 0 && index < parentNodes.size())
            return parentNodes.get(index);

        return null;
    }

    /**
     * Gets the transform.
     * 
     * @return the transform
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * Gets the world transformation of the node. The result can be ambiguous if
     * there is more than one path to the node.
     * 
     * @param root
     *            the root of the scene graph
     * @return the world transformation
     */
    public Transform getWorldTransform(SceneNode root) {
        List<Transform> transList = new ArrayList<Transform>();
        transList.add(transform);
        SceneNode node = this;
        while (node.getParentNode() != null && node.getParentNode() != root) {
            node = node.getParentNode();
            transList.add(node.getTransform());
        }

        Transform trans = new Transform();
        for (int i = transList.size() - 1; i >= 0; i--)
            trans = trans.mul(transList.get(i));

        return trans;
    }

    /**
     * Checks if the node is enabled.
     * 
     * @return true, if is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Removes a parent node.
     * 
     * @param node
     */
    protected void removeParentNode(SceneNode node) {
        setBBoxDirty(true);
        parentNodes.remove(node);
    }
    
    /**
     * Sets the animation.
     * 
     * @param animation
     */
    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    /**
     * Sets the bBoxDirty flag for this node and all parent nodes.
     */
    protected void setBBoxDirty(boolean onlyParents) {
        if (!onlyParents)
            bBoxDirty = true;
        for (SceneNode node : parentNodes)
            node.setBBoxDirty(false);
    }

    /**
     * Enable the node.
     * 
     * @param enabled
     *            the new enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the name
     * @return the scene node
     */
    public SceneNode setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the parent node.
     * 
     * @param node
     *            the parent node
     */
    protected void setParentNode(SceneNode node) {
        if (!parentNodes.contains(node)) {
            parentNodes.add(node);
            setBBoxDirty(true);
        }
    }

    /**
     * Sets the transform.
     * 
     * @param transform
     *            the transform
     * @return the scene node
     */
    public SceneNode setTransform(Transform transform) {
        this.transform = transform;
        setBBoxDirty(true);
        return this;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Updates the bounding box.
     */
    protected abstract void updateBBox();
}
