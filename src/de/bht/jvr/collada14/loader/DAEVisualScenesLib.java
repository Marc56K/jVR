package de.bht.jvr.collada14.loader;

import java.util.List;

import de.bht.jvr.collada14.LibraryVisualScenes;
import de.bht.jvr.collada14.Node;
import de.bht.jvr.collada14.VisualScene;
import de.bht.jvr.core.GroupNode;
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

public class DAEVisualScenesLib {
    LibraryVisualScenes lib;

    public SceneNode getJVRSceneGraph(String sceneName, DAEGeometryLib geometryLib, DAEEffectLib effectLib, DAEMaterialLib materialLib, DAEImageLib imageLib, DAENodesLib nodesLib, DAELightLib lightLib, DAEAnimationLib animationLib) throws Exception {
        // root of the new scene graph
        GroupNode daeRoot = new GroupNode("dae_root");

        List<VisualScene> scenes = (lib).getVisualScenes();
        for (VisualScene scene : scenes)
            if (sceneName.equals(scene.getId())) {
                List<Node> nodes = scene.getNodes();
                for (Node node : nodes) {
                    DAENode n = new DAENode(node);
                    daeRoot.addChildNode(n.getJVRSceneGraph(geometryLib, effectLib, materialLib, imageLib, nodesLib, lightLib, animationLib));
                }
                break;
            }

        return daeRoot;
    }

    public void setVisualScenesLib(LibraryVisualScenes lib) {
        this.lib = lib;
    }
}
