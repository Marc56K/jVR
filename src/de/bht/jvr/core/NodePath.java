package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 */

public class NodePath {

    private final ArrayList<SceneNode> nodes;

    public NodePath(List<SceneNode> ns) {
        nodes = new ArrayList<SceneNode>(ns);
    }

    public NodePath(SceneNode... ns) {
        this(Arrays.asList(ns));
    }

    public NodePath append(SceneNode... ns) {
        NodePath np = new NodePath(nodes);
        for (SceneNode n : ns)
            np.nodes.add(n);
        return np;
    }

    public List<SceneNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public Transform getTransform() {
        Transform t = Transform.identity();
        for (SceneNode n : nodes)
            t = t.mul(n.getTransform());
        return t;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<NodePath: ");
        for (SceneNode n : nodes) {
            sb.append('"');
            sb.append(n.getName());
            sb.append('"');
            sb.append(' ');
        }
        sb.append(">");
        return sb.toString();
    }

}
