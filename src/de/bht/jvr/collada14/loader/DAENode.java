package de.bht.jvr.collada14.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bht.jvr.collada14.BindMaterial;
import de.bht.jvr.collada14.BindMaterial.TechniqueCommon;
import de.bht.jvr.collada14.InstanceGeometry;
import de.bht.jvr.collada14.InstanceMaterial;
import de.bht.jvr.collada14.InstanceWithExtra;
import de.bht.jvr.collada14.Lookat;
import de.bht.jvr.collada14.Matrix;
import de.bht.jvr.collada14.Node;
import de.bht.jvr.collada14.Rotate;
import de.bht.jvr.collada14.Scale;
import de.bht.jvr.collada14.Skew;
import de.bht.jvr.collada14.Translate;
import de.bht.jvr.core.Animation;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.logger.Log;
import de.bht.jvr.math.Matrix4;
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
 */

public class DAENode {
    private String name = "";
    private String id = "";
    private List<Matrix4> transforms = new ArrayList<Matrix4>();
    private List<DAENode> children = new ArrayList<DAENode>();
    private Map<String, String> instanceMaterialMap = new HashMap<String, String>();
    private List<String> instanceNodesList = new ArrayList<String>();
    private List<String> instanceLightList = new ArrayList<String>();
    private List<String> instanceGeometryList = new ArrayList<String>();

    public DAENode(Node n) {
        // System.out.println(n.getName());
        name = n.getName();
        id = n.getId();

        // load sub scene graphs
        List<InstanceWithExtra> instanceNodes = n.getInstanceNodes();
        if (instanceNodes != null)
            for (InstanceWithExtra instanceNode : instanceNodes)
                instanceNodesList.add(instanceNode.getUrl().replaceFirst("#", ""));

        // load lights
        List<InstanceWithExtra> instanceLights = n.getInstanceLights();
        if (instanceLights != null)
            for (InstanceWithExtra instanceLight : instanceLights)
                instanceLightList.add(instanceLight.getUrl().replaceFirst("#", ""));

        // load geometry of the node
        List<InstanceGeometry> geometries = n.getInstanceGeometries();
        for (InstanceGeometry geo : geometries) {
            String geometryId = geo.getUrl();
            if (geometryId != null && geometryId != "") {
                geometryId = geometryId.replace("#", "");
                instanceGeometryList.add(geometryId);
            }

            BindMaterial mat = geo.getBindMaterial();
            if (mat != null) {
                TechniqueCommon techCommon = mat.getTechniqueCommon();
                if (techCommon != null) {
                    List<InstanceMaterial> instanceMatList = techCommon.getInstanceMaterials();
                    for (InstanceMaterial instanceMat : instanceMatList)
                        instanceMaterialMap.put(instanceMat.getSymbol(), instanceMat.getTarget().replace("#", ""));
                }
            }
        }

        // load transformations of the node
        List<Object> transforms = n.getLookatsAndMatrixesAndRotates();
        for (Object trans : transforms) {
            // System.out.println(trans.getClass());

            Matrix4 m = new Matrix4();

            if (trans instanceof Lookat)
                Log.warning(this.getClass(), "Transformation <lookat> not yet implemented."); // TODO
            else if (trans instanceof Matrix) {
                List<Double> tm = ((Matrix) trans).getValues();
                float[] mData = new float[16];
                for (int i = 0; i < mData.length; i++)
                    mData[i] = tm.get(i).floatValue();
                m = new Matrix4(mData);
            } else if (trans instanceof Rotate) {
                List<Double> r = ((Rotate) trans).getValues();
                Vector3 axis = new Vector3(r.get(0).floatValue(), r.get(1).floatValue(), r.get(2).floatValue());
                axis = axis.normalize();
                m = Matrix4.rotate(axis, (float) (r.get(3).floatValue() * Math.PI / 180.));
            } else if (trans instanceof Skew)
                Log.warning(this.getClass(), "Transformation <skew> not yet implemented."); // TODO
            else if (trans instanceof Scale) {
                List<Double> s = ((Scale) trans).getValues();
                m = Matrix4.scale(s.get(0).floatValue(), s.get(1).floatValue(), s.get(2).floatValue());
            } else if (trans instanceof Translate) {
                List<Double> t = ((Translate) trans).getValues();
                m = Matrix4.translate(t.get(0).floatValue(), t.get(1).floatValue(), t.get(2).floatValue());
            }

            this.transforms.add(m);
        }

        // load children
        List<Node> childNodes = n.getNodes();
        for (Node cn : childNodes)
            children.add(new DAENode(cn));
    }

    public String getId() {
        return id;
    }

    public SceneNode getJVRSceneGraph(DAEGeometryLib geometryLib, DAEEffectLib effectLib, DAEMaterialLib materialLib, DAEImageLib imageLib, DAENodesLib nodesLib, DAELightLib lightLib, DAEAnimationLib animationLib) throws Exception {
        GroupNode g = new GroupNode(name);
        g.setTransform(new Transform(getTransformMatrix()));
        
        Animation anim = animationLib.getJVRAnimation(id);
        if (anim != null)
            g.setAnimation(anim);

        for (String instanceGeometry : instanceGeometryList)
            if (instanceGeometry != "") {
                List<ShapeNode> shapeNodes = geometryLib.getJVRShapeNodes(instanceGeometry, instanceMaterialMap, effectLib, materialLib, imageLib);
                for (ShapeNode shapeNode : shapeNodes)
                    g.addChildNode(shapeNode);
            }

        for (DAENode child : children) {
            SceneNode n = child.getJVRSceneGraph(geometryLib, effectLib, materialLib, imageLib, nodesLib, lightLib, animationLib);
            if (n != null)
                g.addChildNode(n);
        }

        for (String instanceNode : instanceNodesList)
            if (instanceNode != "") {
                SceneNode n = nodesLib.getJVRSceneGraph(instanceNode, geometryLib, effectLib, materialLib, imageLib, nodesLib, lightLib, animationLib);
                if (n != null)
                    g.addChildNode(n);
            }

        for (String instanceLight : instanceLightList)
            if (instanceLight != "") {
                SceneNode n = lightLib.getJVRLightNode(instanceLight);
                if (n != null)
                    g.addChildNode(n);
            }

        return g;
    }

    public Matrix4 getTransformMatrix() {
        Matrix4 m = new Matrix4();

        for (Matrix4 n : transforms)
            m = m.mul(n);

        return m;
    }
}
