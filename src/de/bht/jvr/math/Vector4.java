package de.bht.jvr.math;

import java.util.Arrays;

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
 * A immutable four dimensional vector.
 * 
 * @author Marc Roßbach
 */
public class Vector4 {
    /** The xyzw. */
    protected float xyzw[] = new float[4];

    /**
     * The positive X direction.
     */
    public static Vector4 X = new Vector4(1, 0, 0, 0);

    /**
     * The positive Y direction.
     */
    public static Vector4 Y = new Vector4(0, 1, 0, 0);

    /**
     * The positive Z direction.
     */
    public static Vector4 Z = new Vector4(0, 0, 1, 0);

    /**
     * Instantiates a new vector4.
     */
    public Vector4() {
        xyzw[0] = 0;
        xyzw[1] = 0;
        xyzw[2] = 0;
        xyzw[3] = 0;
    }

    /**
     * Instantiates a new vector4.
     * 
     * @param x
     *            the x value
     * @param y
     *            the y value
     * @param z
     *            the z value
     * @param w
     *            the w value
     */
    public Vector4(float x, float y, float z, float w) {
        xyzw[0] = x;
        xyzw[1] = y;
        xyzw[2] = z;
        xyzw[3] = w;
    }

    /**
     * Instantiates a new vector4.
     * 
     * @param array
     *            the vector data
     */
    public Vector4(float[] array) {
        if (array.length != 4)
            throw new RuntimeException("Must create vector with 4 element array");

        xyzw[0] = array[0];
        xyzw[1] = array[1];
        xyzw[2] = array[2];
        xyzw[3] = array[3];
    }

    /**
     * Instantiates a new vector4.
     * 
     * @param v
     *            the vector
     * @param w
     *            the w value
     */
    public Vector4(Vector3 v, float w) {
        xyzw[0] = v.x();
        xyzw[1] = v.y();
        xyzw[2] = v.z();
        xyzw[3] = w;
    }

    /**
     * Adds a vector.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector4 add(Vector4 v) {
        return new Vector4(xyzw[0] + v.xyzw[0], xyzw[1] + v.xyzw[1], xyzw[2] + v.xyzw[2], xyzw[3] + v.xyzw[3]);
    }

    /**
     * Gets the cross product.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector4 cross(Vector4 v) {
        return new Vector4(xyzw[1] * v.xyzw[2] - xyzw[2] * v.xyzw[1], xyzw[2] * v.xyzw[0] - xyzw[0] * v.xyzw[2], xyzw[0] * v.xyzw[1] - xyzw[1] * v.xyzw[0], 0);
    }

    /**
     * Divide by scalar.
     * 
     * @param c
     *            the scalar
     * @return the calculated vector
     */
    public Vector4 div(float c) {
        if (c == 0.0f)
            throw new IllegalArgumentException("Divisor must not be zero.");
        return new Vector4(xyzw[0] / c, xyzw[1] / c, xyzw[2] / c, xyzw[3] / c);
    }

    /**
     * Gets the dot product.
     * 
     * @param v
     *            the vector
     * @return the calculated dot product
     */
    public float dot(Vector4 v) {
        return xyzw[0] * v.xyzw[0] + xyzw[1] * v.xyzw[1] + xyzw[2] * v.xyzw[2] + xyzw[3] * v.xyzw[3];
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof Vector4)
            return Arrays.equals(xyzw, ((Vector4) val).xyzw);
        return false;
    }

    /**
     * Gets the vector data.
     * 
     * @return the vector data
     */
    public float[] getData() {
        return xyzw.clone();
    }

    /**
     * Get homogenized vector.
     * 
     * @return the calculated vector
     */
    public Vector4 homogenize() {
        if (xyzw[3] == 0)
            return new Vector4();
        else
            return new Vector4(xyzw[0] / xyzw[3], xyzw[1] / xyzw[3], xyzw[2] / xyzw[3], 1);
    }

    /**
     * Gets the length of the vector.
     * 
     * @return the length
     */
    public float length() {
        return (float) Math.sqrt(dot(this));
    }

    /**
     * Multiply with scalar.
     * 
     * @param c
     *            the scalar
     * @return the calculated vector
     */
    public Vector4 mul(float c) {
        return new Vector4(c * xyzw[0], c * xyzw[1], c * xyzw[2], c * xyzw[3]);
    }

    /**
     * Gets negated vector.
     * 
     * @return the calculated vector
     */
    public Vector4 neg() {
        return new Vector4(-xyzw[0], -xyzw[1], -xyzw[2], -xyzw[3]);
    }

    /**
     * Gets the normalized vector.
     * 
     * @return the normalized vector
     */
    public Vector4 normalize() {
        return div(length());
    }

    /**
     * Subtracts a vector.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector4 sub(Vector4 v) {
        return new Vector4(xyzw[0] - v.xyzw[0], xyzw[1] - v.xyzw[1], xyzw[2] - v.xyzw[2], xyzw[3] - v.xyzw[3]);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "( " + xyzw[0] + " " + xyzw[1] + " " + xyzw[2] + " " + xyzw[3] + " )";
    }

    /**
     * the w value
     * 
     * @return the w value
     */
    public float w() {
        return xyzw[3];
    }

    /**
     * The x value
     * 
     * @return the x value
     */
    public float x() {
        return xyzw[0];
    }

    /**
     * the xyz values
     * 
     * @return the xyz values
     */
    public Vector3 xyz() {
        return new Vector3(xyzw[0], xyzw[1], xyzw[2]);
    }

    /**
     * The y value
     * 
     * @return the y value
     */
    public float y() {
        return xyzw[1];
    }

    /**
     * The z value
     * 
     * @return the z value
     */
    public float z() {
        return xyzw[2];
    }
}
