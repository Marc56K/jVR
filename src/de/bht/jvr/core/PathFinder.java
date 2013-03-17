package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

public class PathFinder extends PathTraverser {

    /**
     * Finds a scene node in the scene graph. The node class and the name are
     * checked against the provided arguments.
     * 
     * @param class The node type we are looking for.
     * @param namePattern
     *            The name pattern we are looking for. This is a regular
     *            expression pattern.
     * @return A list of pathes to nodes that fit the search pattern.
     */
    static public List<NodePath> find(SceneNode root, Class<? extends SceneNode> c, String namePattern) {
        return new PathFinder(root).find(c, namePattern);
    }

    private SceneNode root;
    private Class<? extends SceneNode> clazz;
    private Pattern pattern;

    private List<NodePath> pathes;

    /**
     * Instantiates a new path finder.
     * 
     * @param root
     *            The search starts here.
     */
    public PathFinder(SceneNode r) {
        root = r;
    }

    private boolean check(NodePath p, SceneNode n) {
        if ((clazz == null || clazz.isInstance(n)) && (pattern == null || pattern.matcher(n.getName()).matches()))
            pathes.add(p);
        return true;
    }

    @Override
    public boolean enter(NodePath nodePath, GroupNode node) {
        return check(nodePath, node);
    }

    /**
     * Finds a scene node in the scene graph. The node class is checked against
     * the provided argument.
     * 
     * @param class The node type we are looking for.
     * @return A list of pathes to nodes that fit the search pattern.
     */
    public List<NodePath> find(Class<? extends SceneNode> c) {
        return find(c, null);
    }

    /**
     * Finds a scene node in the scene graph. The node class and the name are
     * checked against the provided arguments.
     * 
     * @param class The node type we are looking for.
     * @param namePattern
     *            The name pattern we are looking for. This is a regular
     *            expression pattern.
     * @return A list of pathes to nodes that fit the search pattern.
     */
    public List<NodePath> find(Class<? extends SceneNode> c, String p) {
        clazz = c;
        pattern = p == null ? null : Pattern.compile(p);
        pathes = new ArrayList<NodePath>();

        root.accept(this);

        return pathes;
    }

    /**
     * Finds a scene node in the scene graph. The node name is checked against
     * the provided argument.
     * 
     * @param namePattern
     *            The name pattern we are looking for. This is a regular
     *            expression pattern.
     * @return A list of pathes to nodes that fit the search pattern.
     */
    public List<NodePath> find(String namePattern) {
        return find(null, namePattern);
    }

    @Override
    public boolean leave(NodePath nodePath, GroupNode node) {
        return true;
    }

    @Override
    public boolean visit(NodePath nodePath, CameraNode node) {
        return check(nodePath, node);
    }

    @Override
    public boolean visit(NodePath nodePath, ClipPlaneNode node) {
        return check(nodePath, node);
    }

    @Override
    public boolean visit(NodePath nodePath, LightNode node) {
        return check(nodePath, node);
    }

    @Override
    public boolean visit(NodePath nodePath, ShapeNode node) {
        return check(nodePath, node);
    }

}
