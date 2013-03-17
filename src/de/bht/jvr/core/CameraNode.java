package de.bht.jvr.core;

import de.bht.jvr.math.Matrix4;

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
 * The perspective camera.
 * 
 * @author Marc Roßbach
 */
public class CameraNode extends SceneNode {

    /** The aspect ratio. */
    protected float aspectRatio = 4.0f / 3.0f;

    /** The field of view. */
    protected float fov = 60.0f;

    /** The near plane. */
    protected float nearPlane = 0.1f;

    /** The far plane. */
    protected float farPlane = 1000f;

    /**
     * Instantiates a new camera node.
     */
    protected CameraNode() {}

    /**
     * Instantiates a new camera node.
     * 
     * @param name
     *            the name
     * @param aspectRatio
     *            the aspect ratio
     * @param fov
     *            the field of view in degree
     */
    public CameraNode(String name, float aspectRatio, float fov) {
        setName(name);
        setAspectRatio(aspectRatio);
        setFieldOfView(fov);
    }

    /**
     * Instantiates a new camera node.
     * 
     * @param name
     *            the name
     * @param aspectRatio
     *            the aspect ratio
     * @param fov
     *            the field of view in degree
     * @param transform
     *            the transform
     */
    public CameraNode(String name, float aspectRatio, float fov, Transform transform) {
        this(name, aspectRatio, fov);
        setTransform(transform);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.SceneNode#accept(de.bht.jvr.core.Traverser)
     */
    @Override
    public boolean accept(Traverser traverser) {
        return traverser.visit(this);
    }

    /**
     * Gets the aspect ratio.
     * 
     * @return the aspect ratio
     */
    public float getAspectRatio() {
        return aspectRatio;
    }

    /**
     * Gets the transformation of the eye.
     * 
     * @return the transformation of the eye
     */
    public Transform getEyeTransform() {
        return super.getTransform();
    }

    /**
     * Gets the world transformation of the eye.
     * 
     * @param root
     * @return
     */
    public Transform getEyeWorldTransform(SceneNode root) {
        return super.getWorldTransform(root);
    }

    /**
     * Gets the far plane.
     * 
     * @return the far plane
     */
    public float getFarPlane() {
        return farPlane;
    }

    /**
     * Gets the field of view.
     * 
     * @return the field of view
     */
    public float getFieldOfView() {
        return fov;
    }

    /**
     * Gets the near plane.
     * 
     * @return the near plane
     */
    public float getNearPlane() {
        return nearPlane;
    }

    /**
     * http://wiki.delphigl.com/images/a/a8/GluPerspective.png
     * 
     * @return the projection matrix
     */
    public Matrix4 getProjectionMatrix() {
        float fov = (float) (Math.PI * this.fov / 180);
        float f = (float) (1.0f / Math.tan(fov / 2));
        float a = f / aspectRatio;
        float b = (farPlane + nearPlane) / (nearPlane - farPlane);
        float c = 2.0f * farPlane * nearPlane / (nearPlane - farPlane);
        float[] data = new float[] { a, 0, 0, 0, 0, f, 0, 0, 0, 0, b, c, 0, 0, -1, 0 };

        return new Matrix4(data);
    }

    /**
     * Sets the aspect ratio.
     * 
     * @param aspectRatio
     *            the aspect ratio
     * @return the camera node
     */
    public CameraNode setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        return this;
    }

    /**
     * Sets the far plane.
     * 
     * @param farPlane
     *            the new far plane
     */
    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }

    /**
     * Sets the field of view.
     * 
     * @param fov
     *            the field of view in degree
     * @return the camera node
     */
    public CameraNode setFieldOfView(float fov) {
        this.fov = fov;
        return this;
    }

    /**
     * Sets the near plane.
     * 
     * @param nearPlane
     *            the new near plane
     */
    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    @Override
    protected void updateBBox() {
        // TODO Auto-generated method stub

    }
}
