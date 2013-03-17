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
 * A immutable two dimensional vector.
 * 
 * @author Marc Roßbach
 */
public class Vector2 {
    /** The xy. */
    protected float xy[] = new float[2];

    /**
     * The positive X direction.
     */
    public static Vector2 X = new Vector2(1, 0);

    /**
     * The positive Y direction.
     */
    public static Vector2 Y = new Vector2(0, 1);

    /**
     * Instantiates a new vector2.
     */
    public Vector2() {
        xy[0] = 0;
        xy[1] = 0;
    }

    /**
     * Instantiates a new vector2.
     * 
     * @param x
     *            the x value
     * @param y
     *            the y value
     */
    public Vector2(float x, float y) {
        xy[0] = x;
        xy[1] = y;
    }

    /**
     * Instantiates a new vector2.
     * 
     * @param array
     *            the vector data
     */
    public Vector2(float[] array) {
        if (array.length != 2)
            throw new RuntimeException("Must create vector with 2 element array");

        xy[0] = array[0];
        xy[1] = array[1];
    }

    /**
     * Adds a vector.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector2 add(Vector2 v) {
        return new Vector2(xy[0] + v.xy[0], xy[1] + v.xy[1]);
    }

    /**
     * Divide by scalar.
     * 
     * @param c
     *            the scalar
     * @return the calculated vector
     */
    public Vector2 div(float c) {
        if (c == 0.0f)
            throw new IllegalArgumentException("Divisor must not be zero.");
        return new Vector2(xy[0] / c, xy[1] / c);
    }

    /**
     * Gets the dot product.
     * 
     * @param v
     *            the vector
     * @return the calculated dot product
     */
    public float dot(Vector2 v) {
        return xy[0] * v.xy[0] + xy[1] * v.xy[1];
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof Vector2)
            return Arrays.equals(xy, ((Vector2) val).xy);
        return false;
    }

    /**
     * Gets the vector data.
     * 
     * @return the data
     */
    public float[] getData() {
        return xy.clone();
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
     * Multiply with scalar
     * 
     * @param c
     *            the scalar
     * @return the calculated vector
     */
    public Vector2 mul(float c) {
        return new Vector2(c * xy[0], c * xy[1]);
    }

    /**
     * Gets negated vector.
     * 
     * @return the negated vector
     */
    public Vector2 neg() {
        return new Vector2(-xy[0], -xy[1]);
    }

    /**
     * Gets the normalized vector.
     * 
     * @return the normalized vector
     */
    public Vector2 normalize() {
        return div(length());
    }

    /**
     * Subtracts a vector.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector2 sub(Vector2 v) {
        return new Vector2(xy[0] - v.xy[0], xy[1] - v.xy[1]);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "( " + xy[0] + " " + xy[1] + " )";
    }

    /**
     * Gets the x value.
     * 
     * @return x
     */
    public float x() {
        return xy[0];
    }

    /**
     * Gets the y value.
     * 
     * @return y
     */
    public float y() {
        return xy[1];
    }
}
