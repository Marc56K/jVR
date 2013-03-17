package de.bht.jvr.util;

import java.io.Serializable;

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
 * A simple three component color vector. Color vectors are non-mutable and can
 * be passed around by value.
 */

public final class Color implements Comparable<Color>, Serializable {

    public final float r, g, b, a;

    public static final Color black = new Color(0, 0, 0, 1);
    public static final Color blue = new Color(0, 0, 1, 1);
    public static final Color green = new Color(0, 1, 0, 1);
    public static final Color red = new Color(1, 0, 0, 1);
    public static final Color white = new Color(1, 1, 1, 1);
    public static final Color grey = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    public static final Color gray = new Color(0.5f, 0.5f, 0.5f, 1.0f);

    /**
     * Construct a new color vector and initialize the components.
     * 
     * @param r
     *            The red component.
     * @param g
     *            The green component.
     * @param b
     *            The blue component.
     */
    public Color(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Color(Vector4 v) {
        r = v.x();
        g = v.y();
        b = v.z();
        a = v.w();
    }

    /**
     * Construct a new color vector and initialize the components.
     * 
     * @param r
     *            The red component.
     * @param g
     *            The green component.
     * @param b
     *            The blue component.
     */
    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Test this color for blackness.
     * 
     * @return True if black, else false.
     */
    public boolean isBlack() {
        return r == 0.0 && g == 0.0 && b == 0.0;
    }

    /**
     * Calculate the sum of two colors.
     * 
     * @param c
     *            The second color.
     * @return The sum.
     */
    public Color addRGB(Color c) {
        return new Color(r + c.r, g + c.g, b + c.b, a);
    }

    public Color addRGBA(Color c) {
        return new Color(r + c.r, g + c.g, b + c.b, a + c.a);
    }

    public Color subRGB(Color c) {
        return new Color(r - c.r, g - c.g, b - c.b, a);
    }

    public Color subRGBA(Color c) {
        return new Color(r - c.r, g - c.g, b - c.b, a - c.a);
    }

    /**
     * Calculate the product of this color an a scalar.
     * 
     * @param s
     *            The scalar.
     * @return The product.
     */
    public Color modulateRGB(float s) {
        return new Color(r * s, g * s, b * s, a);
    }

    public Color modulateRGBA(float s) {
        return new Color(r * s, g * s, b * s, a * s);
    }

    /**
     * Perform the component wise multiplication of two colors. This is not a
     * dot product!
     * 
     * @param c
     *            The second color.
     * @return The result of the multiplication.
     */
    public Color modulateRGB(Color c) {
        return new Color(r * c.r, g * c.g, b * c.b, a);
    }

    public Color modulateRGBA(Color c) {
        return new Color(r * c.r, g * c.g, b * c.b, a * c.a);
    }

    /**
     * Clip the color components to the interval [0.0, 1.0].
     * 
     * @return The clipped color.
     */
    public Color clip() {
        Color c = new Color(Math.min(r, 1), Math.min(g, 1), Math.min(b, 1), Math.min(a, 1));
        return new Color(Math.max(c.r, 0), Math.max(c.g, 0), Math.max(c.b, 0), Math.max(c.a, 0));
    }

    public float[] getValue() {
        float[] v = { r, g, b, a };
        return v;
    }

    public byte redByte() {
        return (byte) toInt(r);
    }

    public byte greenByte() {
        return (byte) toInt(g);
    }

    public byte blueByte() {
        return (byte) toInt(b);
    }

    public byte alphaByte() {
        return (byte) toInt(a);
    }

    public int redInt() {
        return toInt(r);
    }

    public int greenInt() {
        return toInt(g);
    }

    public int blueInt() {
        return toInt(b);
    }

    public int alphaInt() {
        return toInt(a);
    }

    public boolean isOpaque() {
        return a == 1.0f;
    }

    public boolean isTransparent() {
        return a < 1.0f;
    }

    /**
     * Return this color in a packed pixel format suitable for use with AWT.
     * 
     * @return The color as a packed pixel integer value.
     */
    public int toAwtColor() {
        Color c = clip();
        return (toInt(c.a) << 24) | (toInt(c.r) << 16) | (toInt(c.g) << 8) | toInt(c.b);
    }

    /**
     * Convert a floating point component from the interval [0.0, 1.0] to an
     * integral component in the interval [0, 255]
     * 
     * @param c
     *            The float component.
     * @return The integer component.
     */
    private static int toInt(float c) {
        return Math.round(c * 255.0f);
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "(" + r + ", " + g + ", " + b + ", " + a + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Color))
            return false;
        final Color c = (Color) o;
        return r == c.r && g == c.g && b == c.b && b == c.a;
    }

    @Override
    public int compareTo(Color o) {
        if (r != o.r)
            return (r < o.r ? -1 : 1);
        if (g != o.g)
            return (g < o.g ? -1 : 1);
        if (b != o.b)
            return (b < o.b ? -1 : 1);
        if (a != o.a)
            return (a < o.a ? -1 : 1);
        return 0;
    }

    public static int size() {
        return 4;
    }

}
