package de.bht.jvr.core;

import java.io.Serializable;

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
 *
 *
 * A wrapper for a transformation matrix and its inverse.
 * 
 * @author Marc Roßbach
 */

public class Transform implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8895122908760361175L;

    /**
     * Create an idenity transformation.
     * 
     * @return The transformation.
     */
    public static Transform identity() {
        return new Transform();
    }

    /**
     * Rotate.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param z
     *            the z
     * @param angle
     *            the angle in radian
     * @return the transform
     */
    public static Transform rotate(float x, float y, float z, float angle) {
        return Transform.rotate(new Vector3(x, y, z), angle);
    }

    /**
     * Rotate.
     * 
     * @param axis
     *            the axis
     * @param angle
     *            the angle in radian
     * @return the transform
     */
    public static Transform rotate(Vector3 axis, float angle) {
        return new Transform(Matrix4.rotate(axis, angle), Matrix4.rotate(axis, -angle));
    }

    /**
     * Rotate.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param z
     *            the z
     * @param angle
     *            the angle in degree
     * @return the transform
     */
    public static Transform rotateDeg(float x, float y, float z, float angle) {
        return Transform.rotate(x, y, z, (float) (Math.PI * angle / 180));
    }

    /**
     * Rotate.
     * 
     * @param axis
     *            the axis
     * @param angle
     *            the angle in degree
     * @return the transform
     */
    public static Transform rotateDeg(Vector3 axis, float angle) {
        return Transform.rotate(axis, (float) (Math.PI * angle / 180));
    }

    /**
     * Rotate x.
     * 
     * @param angle
     *            the angle in radian
     * @return the transform
     */
    public static Transform rotateX(float angle) {
        return Transform.rotate(new Vector3(1, 0, 0), angle);
    }

    /**
     * Rotate x.
     * 
     * @param angle
     *            the angle in degree
     * @return the transform
     */
    public static Transform rotateXDeg(float angle) {
        return Transform.rotateX((float) (Math.PI * angle / 180));
    }

    /**
     * Rotate y.
     * 
     * @param angle
     *            the angle in radian
     * @return the transform
     */
    public static Transform rotateY(float angle) {
        return Transform.rotate(new Vector3(0, 1, 0), angle);
    }

    /**
     * Rotate y.
     * 
     * @param angle
     *            the angle in degree
     * @return the transform
     */
    public static Transform rotateYDeg(float angle) {
        return Transform.rotateY((float) (Math.PI * angle / 180));
    }

    /**
     * Rotate z.
     * 
     * @param angle
     *            the angle in radian
     * @return the transform
     */
    public static Transform rotateZ(float angle) {
        return Transform.rotate(new Vector3(0, 0, 1), angle);
    }

    /**
     * Rotate z.
     * 
     * @param angle
     *            the angle in degree
     * @return the transform
     */
    public static Transform rotateZDeg(float angle) {
        return Transform.rotateZ((float) (Math.PI * angle / 180));
    }

    /**
     * Scale.
     * 
     * @param scale
     *            the scale
     * @return the transform
     */
    public static Transform scale(float scale) {
        return Transform.scale(scale, scale, scale);
    }

    /**
     * Scale.
     * 
     * @param scaleX
     *            the scale x
     * @param scaleY
     *            the scale y
     * @param scaleZ
     *            the scale z
     * @return the transform
     */
    public static Transform scale(float scaleX, float scaleY, float scaleZ) {
        return new Transform(Matrix4.scale(scaleX, scaleY, scaleZ), Matrix4.scale(1f / scaleX, 1f / scaleY, 1f / scaleZ));
    }

    public static Transform scale(Vector3 scale) {
        return new Transform(Matrix4.scale(scale), Matrix4.scale(1f / scale.x(), 1f / scale.y(), 1f / scale.z()));
    }

    /**
     * Translate.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param z
     *            the z
     * @return the transform
     */
    public static Transform translate(float x, float y, float z) {
        return Transform.translate(new Vector3(x, y, z));
    }

    /**
     * Translate.
     * 
     * @param v
     *            the position
     * @return the transform
     */
    public static Transform translate(Vector3 v) {
        return new Transform(Matrix4.translate(v), Matrix4.translate(v.neg()));
    }

    /** The transform. */
    final private Matrix4 transform;

    /** The inverse transform. */
    final private Matrix4 invTransform;

    /**
     * Instantiates a new transform.
     */
    public Transform() {
        transform = new Matrix4();
        invTransform = new Matrix4();
    }

    /**
     * Instantiates a new transform.
     * 
     * @param m
     *            the transform matrix
     */
    public Transform(Matrix4 m) {
        transform = m;
        invTransform = m.inverse();
    }

    /**
     * Instantiates a new transform.
     */
    private Transform(Matrix4 m, Matrix4 i) {
        transform = m;
        invTransform = i;
    }

    /**
     * Gets the rotation of this matrix.
     * 
     * @return the rotation matrix
     */
    public Transform extractRotation() {
        return new Transform(transform.extractRotation(), invTransform.extractRotation());
    }

    /**
     * Gets the translation of this matrix.
     * 
     * @return the translation matrix
     */
    public Transform extractTranslation() {
        return new Transform(transform.extractTranslation(), invTransform.extractTranslation());
    }

    /**
     * Gets the inverse matrix.
     * 
     * @return the inverse matrix
     */
    public Matrix4 getInverseMatrix() {
        return invTransform;
    }

    /**
     * Gets the matrix.
     * 
     * @return the matrix
     */
    public Matrix4 getMatrix() {
        return transform;
    }

    /**
     * Invert.
     * 
     * @return the transform
     */
    public Transform invert() {
        return new Transform(invTransform, transform);
    }

    /**
     * Multiply.
     * 
     * @param t
     *            the transform
     * @return the transform
     */
    public Transform mul(Transform t) {
        return new Transform(transform.mul(t.transform), t.invTransform.mul(invTransform));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return transform.toString();
    }

}
