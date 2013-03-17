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
 *
 *
 * The VRCameraNode is a head tracked off-axis camera.
 * 
 * @author Marc Roßbach
 */
public class VRCameraNode extends CameraNode {
    /** The head transform. */
    private Transform headTransform = new Transform();

    /** The screen transform. */
    private Transform screenTransform;

    /** The screen dim. */
    private Vector4 screenDim;

    /** The eye separation. */
    private float eyeSeparation = 0.07f;

    /** The left eye. */
    private boolean leftEye;

    /**
     * Instantiates a new vr camera node.
     * 
     * @param name
     *            the name
     * @param screenTransform
     *            the transformation of the screen in relation to the camera
     *            transformation (e.g. Transform.translate(0,0,-1) defines the
     *            screen one unit in front of the camera)
     * @param screenDim
     *            (left, right, top, bottom)
     * @param leftEye
     *            false, if is right eye
     */
    public VRCameraNode(String name, Transform screenTransform, Vector4 screenDim, boolean leftEye) {
        setName(name);
        setScreenTransform(screenTransform);
        setScreenDim(screenDim);
        setLeftEye(leftEye);
    }

    /**
     * Instantiates a new vr camera node.
     * 
     * @param name
     *            the name
     * @param screenTransform
     *            the transformation of the screen in relation to the camera
     *            transformation (e.g. Transform.translate(0,0,-1) defines the
     *            screen one unit in front of the camera)
     * @param screenDim
     *            (left, right, top, bottom)
     * @param leftEye
     *            false, if is right eye
     * @param transform
     *            the transformation of the camera in the world
     */
    public VRCameraNode(String name, Transform screenTransform, Vector4 screenDim, boolean leftEye, Transform transform) {
        setName(name);
        setScreenTransform(screenTransform);
        setScreenDim(screenDim);
        setLeftEye(leftEye);
        setTransform(transform);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.CameraNode#accept(de.bht.jvr.core.Traverser)
     */
    @Override
    public boolean accept(Traverser traverser) {
        return traverser.visit(this);
    }

    /**
     * Gets the eye separation.
     * 
     * @return the eye separation
     */
    public float getEyeSeparation() {
        return eyeSeparation;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.CameraNode#getEyeTransform()
     */
    @Override
    public Transform getEyeTransform() {
        Transform eyeInCaveSpace = headTransform.mul(getRelativeEyeTransform());
        Vector3 eyePosInCaveSpace = eyeInCaveSpace.getMatrix().translation();

        return super.getTransform().mul(Transform.translate(eyePosInCaveSpace));
    }

    /*
     * (non-Javadoc)
     * @see
     * de.bht.jvr.core.CameraNode#getEyeWorldTransform(de.bht.jvr.core.SceneNode
     * )
     */
    @Override
    public Transform getEyeWorldTransform(SceneNode root) {
        List<Transform> transList = new ArrayList<Transform>();
        transList.add(getEyeTransform());
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
     * Gets the head transform.
     * 
     * @return the head transform
     */
    public Transform getHeadTransform() {
        return headTransform;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.CameraNode#getProjectionMatrix()
     */
    @Override
    public Matrix4 getProjectionMatrix() {
        Transform eyeInCaveSpace = headTransform.mul(getRelativeEyeTransform());
        Vector3 eyePosInCaveSpace = eyeInCaveSpace.getMatrix().translation();
        Transform eyeInScreenSpace = screenTransform.invert().mul(Transform.translate(eyePosInCaveSpace));

        Vector3 eyePos = eyeInScreenSpace.getMatrix().translation();

        float k = nearPlane / eyePos.z();
        float right = k * (screenDim.y() - eyePos.x());
        float left = k * (screenDim.x() - eyePos.x());
        float top = k * (screenDim.z() - eyePos.y());
        float bottom = k * (screenDim.w() - eyePos.y());
        // System.out.println("r:"+right+" l:"+left+" t:"+top+" b:"+bottom+" width:"+(right-left)+" height:"+(top-bottom));

        return makeFrustumMatrix(left, right, bottom, top);// .mul(Transform.translate(eyePosInCaveSpace).getInverseMatrix());
    }

    /**
     * Gets the eye transformation relative to the head.
     * 
     * @return the eye transform
     */
    protected Transform getRelativeEyeTransform() {
        Transform eye;
        if (isLeftEye())
            eye = Transform.translate(-eyeSeparation / 2, 0, 0); // left eye
        else
            eye = Transform.translate(eyeSeparation / 2, 0, 0); // right eye

        return eye;
    }

    /**
     * Gets the screen dimension.
     * 
     * @return the screen dim
     */
    public Vector4 getScreenDim() {
        return screenDim;
    }

    /**
     * Gets the screen transform.
     * 
     * @return the screen transform
     */
    public Transform getScreenTransform() {
        return screenTransform;
    }

    /**
     * Checks if the camera is a left eye camera.
     * 
     * @return true, if is left eye
     */
    public boolean isLeftEye() {
        return leftEye;
    }

    /**
     * Makes a frustum matrix.
     * 
     * @param left
     *            the left
     * @param right
     *            the right
     * @param bottom
     *            the bottom
     * @param top
     *            the top
     * @return the projection matrix
     */
    protected Matrix4 makeFrustumMatrix(float left, float right, float bottom, float top) {
        float a = 2 * nearPlane / (right - left);
        float b = (right + left) / (right - left);
        float c = 2 * nearPlane / (top - bottom);
        float d = (top + bottom) / (top - bottom);
        float e = -(farPlane + nearPlane) / (farPlane - nearPlane);
        float f = -2 * farPlane * nearPlane / (farPlane - nearPlane);

        float[] data = new float[] { a, 0, b, 0, 0, c, d, 0, 0, 0, e, f, 0, 0, -1, 0 };

        return new Matrix4(data);
    }

    /**
     * Sets the eye separation.
     * 
     * @param eyeSeparation
     *            the new eye separation
     */
    public void setEyeSeparation(float eyeSeparation) {
        this.eyeSeparation = eyeSeparation;
    }

    /**
     * Sets the new head transformation.
     * 
     * @param headTransform
     *            the new head transformation in relation to the camera
     *            transformation.
     */
    public void setHeadTransform(Transform headTransform) {
        this.headTransform = headTransform;
    }

    /**
     * Sets the left eye.
     * 
     * @param leftEye
     *            the new left eye
     */
    public void setLeftEye(boolean leftEye) {
        this.leftEye = leftEye;
    }

    /**
     * Sets the screen dimension.
     * 
     * @param screenDim
     *            the new screen dim
     */
    public void setScreenDim(Vector4 screenDim) {
        this.screenDim = screenDim;
    }

    /**
     * Sets the screen transform.
     * 
     * @param screenTransform
     *            the new screen transform
     */
    public void setScreenTransform(Transform screenTransform) {
        this.screenTransform = screenTransform;
    }
}
