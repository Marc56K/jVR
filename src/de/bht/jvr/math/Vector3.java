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
 * A immutable three dimensional vector.
 * 
 * @author Marc Roßbach
 */
public class Vector3 {

    /** The xyz. */
    protected float xyz[] = new float[3];

    /**
     * The positive X direction.
     */
    public static Vector3 X = new Vector3(1, 0, 0);

    /**
     * The positive Y direction.
     */
    public static Vector3 Y = new Vector3(0, 1, 0);

    /**
     * The positive Z direction.
     */
    public static Vector3 Z = new Vector3(0, 0, 1);

    /**
     * Instantiates a new vector3.
     */
    public Vector3() {
        xyz[0] = 0;
        xyz[1] = 0;
        xyz[2] = 0;
    }

    /**
     * Instantiates a new vector3.
     * 
     * @param x
     *            the x value
     * @param y
     *            the y value
     * @param z
     *            the z value
     */
    public Vector3(float x, float y, float z) {
        xyz[0] = x;
        xyz[1] = y;
        xyz[2] = z;
    }

    /**
     * Instantiates a new vector3.
     * 
     * @param array
     *            the vector data
     */
    public Vector3(float[] array) {
        if (array.length != 3)
            throw new RuntimeException("Must create vector with 3 element array");

        xyz[0] = array[0];
        xyz[1] = array[1];
        xyz[2] = array[2];
    }

    /**
     * Adds a vector.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector3 add(Vector3 v) {
        return new Vector3(xyz[0] + v.xyz[0], xyz[1] + v.xyz[1], xyz[2] + v.xyz[2]);
    }

    /**
     * Gets the cross product.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector3 cross(Vector3 v) {
        return new Vector3(xyz[1] * v.xyz[2] - xyz[2] * v.xyz[1], xyz[2] * v.xyz[0] - xyz[0] * v.xyz[2], xyz[0] * v.xyz[1] - xyz[1] * v.xyz[0]);
    }

    /**
     * Divide by scalar.
     * 
     * @param c
     *            the scalar
     * @return the calculated vector
     */
    public Vector3 div(float c) {
        /*
         * if (c == 0.0f) throw new
         * IllegalArgumentException("Divisor must not be zero.");
         */
        return new Vector3(xyz[0] / c, xyz[1] / c, xyz[2] / c);
    }

    /**
     * Gets the dot product.
     * 
     * @param v
     *            the vector
     * @return the calculated dot product
     */
    public float dot(Vector3 v) {
        return xyz[0] * v.xyz[0] + xyz[1] * v.xyz[1] + xyz[2] * v.xyz[2];
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof Vector3)
            return Arrays.equals(xyz, ((Vector3) val).xyz);
        return false;
    }

    /**
     * Gets the vector data.
     * 
     * @return the vector data
     */
    public float[] getData() {
        return xyz.clone();
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
    public Vector3 mul(float c) {
        return new Vector3(c * xyz[0], c * xyz[1], c * xyz[2]);
    }

    /**
     * Gets negated vector.
     * 
     * @return the negated vector
     */
    public Vector3 neg() {
        return new Vector3(-xyz[0], -xyz[1], -xyz[2]);
    }

    /**
     * Gets the normalized vector.
     * 
     * @return the normalized vector
     */
    public Vector3 normalize() {
        return div(length());
    }

    /**
     * Subtracts a vector.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector3 sub(Vector3 v) {
        return new Vector3(xyz[0] - v.xyz[0], xyz[1] - v.xyz[1], xyz[2] - v.xyz[2]);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "( " + xyz[0] + " " + xyz[1] + " " + xyz[2] + " )";
    }

    /**
     * The x value
     * 
     * @return x
     */
    public float x() {
        return xyz[0];
    }

    /**
     * The y value
     * 
     * @return y
     */
    public float y() {
        return xyz[1];
    }

    /**
     * The z value
     * 
     * @return z
     */
    public float z() {
        return xyz[2];
    }
}
