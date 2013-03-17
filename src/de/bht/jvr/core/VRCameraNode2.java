package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.List;

import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.math.Vector4;
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

public class VRCameraNode2 extends VRCameraNode{
    
    private Transform realityToWorld;
    private Transform trackerToReality;
    
    public VRCameraNode2(String name, Transform screenTransform, Vector4 screenDim, boolean leftEye, Transform realityToWorld, Transform trackerToReality){
        super(name, screenTransform, screenDim, leftEye);
        setRealityToWorld(realityToWorld);
        setTrackerToReality(trackerToReality);
    }
    
    public VRCameraNode2(String name, Transform screenTransform, Vector4 screenDim, boolean leftEye, Transform transform, Transform realityToWorld, Transform trackerToReality){
        super(name, screenTransform, screenDim, leftEye, transform);
        setRealityToWorld(realityToWorld);
        setTrackerToReality(trackerToReality);
    }
    
    @Override
    public Transform getEyeWorldTransform(SceneNode root){
//        throw new UnsupportedOperationException();
        List<Transform> transList = new ArrayList<Transform>();
        transList.add(getEyeTransform().mul(getTrackerToWorld()));
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
    
    @Override
    public Matrix4 getProjectionMatrix(){
//        throw new UnsupportedOperationException();
        Transform eyeInCaveSpace = getHeadTransform().mul(getRelativeEyeTransform()).mul(getTrackerToReality());
        Vector3 eyePosInCaveSpace = eyeInCaveSpace.getMatrix().translation();
        Transform eyeInScreenSpace = getScreenTransform().invert().mul(Transform.translate(eyePosInCaveSpace)); // mul(getRealityToWorld)

        Vector3 eyePos = eyeInScreenSpace.getMatrix().translation();

        float k = nearPlane / eyePos.z();
        float right = k * (getScreenDim().y() - eyePos.x());
        float left = k * (getScreenDim().x() - eyePos.x());
        float top = k * (getScreenDim().z() - eyePos.y());
        float bottom = k * (getScreenDim().w() - eyePos.y());
        // System.out.println("r:"+right+" l:"+left+" t:"+top+" b:"+bottom+" width:"+(right-left)+" height:"+(top-bottom));

        return makeFrustumMatrix(left, right, bottom, top);// .mul(Transform.translate(eyePosInCaveSpace).getInverseMatrix());
    }
    
    public Transform getRealityToWorld(){
        return realityToWorld;
    }
    
    public Transform getTrackerToReality(){
        return trackerToReality;
    }
    
    public Transform getTrackerToWorld(){
        return trackerToReality.mul(realityToWorld);
    }
    
    public void setRealityToWorld(Transform transform){
        realityToWorld = transform;
    }
    
    public void setTrackerToReality(Transform transform){
        trackerToReality = transform;
    }
    
}
