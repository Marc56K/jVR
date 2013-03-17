package de.bht.jvr.core;

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
 * A immutable oriented bounding box.
 * 
 * @author Marc Roßbach
 */

public class BBox {
    /**
     * Expand the bounding box to enclose passed bounding box.
     * 
     * @param bBox
     *            the bounding box
     * @param result
     *            the target bounding box
     */
    private static void grow(BBox bBox, BBox result) {
        grow(bBox.min, result);
        grow(bBox.max, result);
    }

    /**
     * Expand the bounding box to enclose passed transformed bounding box.
     * 
     * @param bBox
     *            the bounding box
     * @param trans
     *            the transformation
     * @param result
     *            the target bounding box
     */
    private static void grow(BBox bBox, Matrix4 trans, BBox result) {
        Vector3 min = bBox.min;
        Vector3 max = bBox.max;

        if (!min.equals(max)) // ignore volumes with zero size
        {
            Vector4[] corners = new Vector4[] { new Vector4(min.x(), min.y(), min.z(), 1), new Vector4(max.x(), min.y(), min.z(), 1), new Vector4(min.x(), max.y(), min.z(), 1), new Vector4(min.x(), min.y(), max.z(), 1), new Vector4(max.x(), max.y(), min.z(), 1), new Vector4(min.x(), max.y(), max.z(), 1), new Vector4(max.x(), min.y(), max.z(), 1), new Vector4(max.x(), max.y(), max.z(), 1) };

            for (int i = 0; i < corners.length; i++) {
                corners[i] = trans.mul(corners[i]);
                corners[i] = corners[i].div(corners[i].w());

                grow(new Vector3(corners[i].x(), corners[i].y(), corners[i].z()), result);
            }
        }

        if (bBox.isInfinite)
            result.setInfinite();
    }

    /**
     * Expand the bounding box to enclose the passed point.
     * 
     * @param pos
     *            the point
     * @param result
     *            the target bounding box
     */
    private static void grow(Vector3 pos, BBox result) {
        if (!result.isSet) {
            result.min = pos;
            result.max = pos;
            result.isSet = true;
        } else {
            if (result.min.x() > pos.x())
                result.min = new Vector3(pos.x(), result.min.y(), result.min.z());
            if (result.min.y() > pos.y())
                result.min = new Vector3(result.min.x(), pos.y(), result.min.z());
            if (result.min.z() > pos.z())
                result.min = new Vector3(result.min.x(), result.min.y(), pos.z());

            if (result.max.x() < pos.x())
                result.max = new Vector3(pos.x(), result.max.y(), result.max.z());
            if (result.max.y() < pos.y())
                result.max = new Vector3(result.max.x(), pos.y(), result.max.z());
            if (result.max.z() < pos.z())
                result.max = new Vector3(result.max.x(), result.max.y(), pos.z());
        }
    }

    /**
     * Creates a bounding box with infinite size.
     * 
     * @return the new bounding box
     */
    public static BBox infinite() {
        BBox result = new BBox();
        result.setInfinite();
        return result;
    }

    /** The min. */
    private Vector3 min = new Vector3(0, 0, 0);

    /** The max. */
    private Vector3 max = new Vector3(0, 0, 0);

    /** The bounding box is not initialized */
    private boolean isSet = false;

    /** The size of the bounding box is not infinite */
    private boolean isInfinite = false;

    /**
     * Instantiates a new bounding box.
     */
    public BBox() {}

    /**
     * Instantiates a new bounding box from existing bounding box.
     * 
     * @param bBox
     */
    private BBox(BBox bBox) {
        min = bBox.min;
        max = bBox.max;
        isSet = bBox.isSet;
        isInfinite = bBox.isInfinite;
    }

    /**
     * Instantiates a new bounding box from existing transformed bounding box.
     * 
     * @param bBox
     *            the bounding box
     * @param trans
     *            the transformation of the bounding box
     */
    public BBox(BBox bBox, Matrix4 trans) {
        grow(bBox, trans, this);
    }

    /**
     * Instantiates a new bounding box.
     * 
     * @param min
     *            the min
     * @param max
     *            the max
     */
    public BBox(Vector3 min, Vector3 max) {
        set(min, max);
    }

    /**
     * Gets the center of the bounding box.
     * 
     * @return the center
     */
    public Vector3 getCenter() {
        return min.add(max).mul(0.5f);
    }

    /**
     * Gets the depth.
     * 
     * @return the depth
     */
    public float getDepth() {
        return Math.abs(min.z() - max.z());
    }

    /**
     * Gets the height.
     * 
     * @return the height
     */
    public float getHeight() {
        return Math.abs(min.y() - max.y());
    }

    /**
     * Gets the max.
     * 
     * @return the max
     */
    public Vector3 getMax() {
        return max;
    }

    /**
     * Gets the min.
     * 
     * @return the min
     */
    public Vector3 getMin() {
        return min;
    }

    /**
     * Gets the width.
     * 
     * @return the width
     */
    public float getWidth() {
        return Math.abs(min.x() - max.x());
    }

    /**
     * Expand the bounding box to enclose passed bounding box.
     * 
     * @param bBox
     *            the bounding box
     * @return the modified bounding box
     */
    public BBox grow(BBox bBox) {
        BBox result = new BBox(this);
        grow(bBox, result);
        return result;
    }

    /**
     * Expand the bounding box to enclose passed transformed bounding box.
     * 
     * @param bBox
     *            the bounding box
     * @param trans
     *            the transformation
     * @return the modified bounding box
     */
    public BBox grow(BBox bBox, Matrix4 trans) {
        BBox result = new BBox(this);
        grow(bBox, trans, result);
        return result;
    }

    /**
     * Expand the bounding box to enclose the passed point.
     * 
     * @param pos
     *            the point
     * @return the modified bounding box
     */
    public BBox grow(Vector3 pos) {
        BBox result = new BBox(this);
        grow(pos, result);
        return result;
    }

    /**
     * Checks if the bounding box is infinite.
     * 
     * @return true, if size of bounding box is infinite
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    /**
     * Sets the.
     * 
     * @param min
     *            the min
     * @param max
     *            the max
     */
    private void set(Vector3 min, Vector3 max) {
        this.min = min;
        this.max = max;
        isSet = true;
    }

    /**
     * Sets the bounding box to infinite size
     */
    private void setInfinite() {
        isSet = true;
        isInfinite = true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BBox: Min" + min + " Max" + max + " Infinite(" + isInfinite + ")";
    }
}
