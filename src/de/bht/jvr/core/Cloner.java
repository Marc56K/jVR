package de.bht.jvr.core;

import java.util.Stack;

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

public class Cloner implements Traverser {
    static public <R extends SceneNode> R cloneGraph(R node) {
        return new Cloner().clone(node);
    }

    Stack<GroupNode> groupStack = new Stack<GroupNode>();

    @SuppressWarnings("unchecked")
    public <R extends SceneNode> R clone(R node) {
        groupStack.clear();
        groupStack.push(new GroupNode("CloneRoot"));
        node.accept(this);

        return (R) node.getClass().cast(groupStack.pop().getChildNodes().get(0));
    }

    @Override
    public boolean enter(GroupNode node) {
        GroupNode clone = new GroupNode(node.getName());
        clone.setTransform(node.getTransform());
        clone.setEnabled(node.isEnabled());
        groupStack.peek().addChildNode(clone);
        groupStack.push(clone);
        return true;
    }

    @Override
    public boolean leave(GroupNode node) {
        groupStack.pop();
        return true;
    }

    @Override
    public boolean visit(CameraNode node) {
        CameraNode clone = new CameraNode(node.getName(), node.getAspectRatio(), node.getFieldOfView(), node.getTransform());
        clone.setNearPlane(node.getNearPlane());
        clone.setFarPlane(node.getFarPlane());
        clone.setEnabled(node.isEnabled());

        groupStack.peek().addChildNode(clone);
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
        // TODO handle dynamic geometry and material
        ShapeNode clone = new ShapeNode(node.getName(), node.getGeometry(), node.getMaterial(), node.getTransform());
        clone.setEnabled(node.isEnabled());

        groupStack.peek().addChildNode(clone);
        return true;
    }

}
