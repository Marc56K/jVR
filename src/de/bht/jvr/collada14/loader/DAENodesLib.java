package de.bht.jvr.collada14.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bht.jvr.collada14.LibraryNodes;
import de.bht.jvr.collada14.Node;
import de.bht.jvr.core.SceneNode;

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

public class DAENodesLib {
    private Map<String, DAENode> nodesMap = new HashMap<String, DAENode>();

    public SceneNode getJVRSceneGraph(String nodeId, DAEGeometryLib geometryLib, DAEEffectLib effectLib, DAEMaterialLib materialLib, DAEImageLib imageLib, DAENodesLib nodesLib, DAELightLib lightLib, DAEAnimationLib animationLib) throws Exception {
        DAENode n = nodesMap.get(nodeId);
        if (n != null)
            return n.getJVRSceneGraph(geometryLib, effectLib, materialLib, imageLib, nodesLib, lightLib, animationLib);
        return null;
    }

    public void setNodesLib(LibraryNodes lib) {
        List<Node> nodes = lib.getNodes();
        for (Node node : nodes) {
            DAENode n = new DAENode(node);
            nodesMap.put(n.getId(), n);
        }
    }
}
