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

public abstract class PathTraverser implements Traverser {

    private Stack<SceneNode> nodeStack = new Stack<SceneNode>();

    @Override
    public boolean enter(GroupNode node) {
        nodeStack.push(node);
        return enter(new NodePath(nodeStack), node);
    }

    public abstract boolean enter(NodePath nodePath, GroupNode node);

    @Override
    public boolean leave(GroupNode node) {
        boolean r = leave(new NodePath(nodeStack), node);
        nodeStack.pop();
        return r;
    }

    public abstract boolean leave(NodePath nodePath, GroupNode node);

    @Override
    public boolean visit(CameraNode node) {
        nodeStack.push(node);
        boolean r = visit(new NodePath(nodeStack), node);
        nodeStack.pop();
        return r;
    }

    @Override
    public boolean visit(ClipPlaneNode node) {
        nodeStack.push(node);
        boolean r = visit(new NodePath(nodeStack), node);
        nodeStack.pop();
        return r;
    }

    @Override
    public boolean visit(LightNode node) {
        nodeStack.push(node);
        boolean r = visit(new NodePath(nodeStack), node);
        nodeStack.pop();
        return r;
    }

    public abstract boolean visit(NodePath nodePath, CameraNode node);

    public abstract boolean visit(NodePath nodePath, ClipPlaneNode node);

    public abstract boolean visit(NodePath nodePath, LightNode node);

    public abstract boolean visit(NodePath nodePath, ShapeNode node);

    @Override
    public boolean visit(ShapeNode node) {
        nodeStack.push(node);
        boolean r = visit(new NodePath(nodeStack), node);
        nodeStack.pop();
        return r;
    }

}
