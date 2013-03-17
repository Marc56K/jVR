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
 * A immutable 3x3 matrix.
 * 
 * @author Marc Roßbach
 */

public class Matrix3 {
    /**
     * Create a rotation matrix.
     * 
     * @param axis
     *            the axis
     * @param angle
     *            the angle
     * @return the rotation matrix
     */
    public static Matrix3 rotate(Vector3 axis, float angle) {
        Vector3 ax = axis.normalize();

        float x, y, z, s, c, t;
        x = ax.x();
        y = ax.y();
        z = ax.z();
        s = (float) Math.sin(angle);
        c = (float) Math.cos(angle);
        t = 1 - c;

        return new Matrix3(c + x * x * t, x * y * t - z * s, x * z * t + y * s, y * x * t + z * s, c + y * y * t, y * z * t - x * s, z * x * t - y * s, z * y * t + x * s, c + z * z * t);
    }

    /**
     * Create a scale matrix.
     * 
     * @param scaleX
     *            scale x
     * @param scaleY
     *            scale y
     * @param scaleZ
     *            scale z
     * @return the scale matrix
     */
    public static Matrix3 scale(float scaleX, float scaleY, float scaleZ) {
        Matrix3 scl = new Matrix3();
        scl.matrix[0] = scaleX;
        scl.matrix[4] = scaleY;
        scl.matrix[8] = scaleZ;

        return scl;
    }

    /** The matrix. */
    protected float[] matrix = new float[9];

    /**
     * Instantiates a new matrix3.
     */
    public Matrix3() {
        matrix[0] = 1;
        matrix[4] = 1;
        matrix[8] = 1;
    }

    /**
     * Instantiates a new matrix3.
     * 
     * @param v00
     *            The first matrix element in the first row.
     * @param v01
     *            The second matrix element in the first row.
     * @param v02
     *            The third matrix element in the first row.
     * @param v10
     *            The first matrix element in the second row.
     * @param v11
     *            The second matrix element in the second row.
     * @param v12
     *            The third matrix element in the second row.
     * @param v20
     *            The first matrix element in the third row.
     * @param v21
     *            The second matrix element in the third row.
     * @param v22
     *            The third matrix element in the third row.
     */
    public Matrix3(float v00, float v01, float v02, float v10, float v11, float v12, float v20, float v21, float v22) {
        matrix[0] = v00;
        matrix[1] = v01;
        matrix[2] = v02;

        matrix[3] = v10;
        matrix[4] = v11;
        matrix[5] = v12;

        matrix[6] = v20;
        matrix[7] = v21;
        matrix[8] = v22;
    }

    /**
     * Instantiates a new matrix3.
     * 
     * @param array
     *            the row majored matrix data
     */
    public Matrix3(float[] array) {
        if (array.length != 9)
            throw new RuntimeException("Invalid array length.");

        matrix = array.clone();
    }

    /**
     * Adds a matrix.
     * 
     * @param m
     *            the matrix
     * @return the calculated matrix
     */
    public Matrix3 add(Matrix3 m) {
        Matrix3 retval = new Matrix3();

        for (int i = 0; i < 9; i++)
            retval.matrix[i] = matrix[i] + m.matrix[i];

        return retval;
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof Matrix3)
            return Arrays.equals(matrix, ((Matrix3) val).matrix);
        return false;
    }

    /**
     * Gets the value of a matrix element.
     * 
     * @param row
     *            the row
     * @param col
     *            the column
     * @return the value
     */
    public float get(int row, int col) {
        if (row < 0 || row > 2 || col < 0 || col > 2)
            throw new RuntimeException("Index out of bounds.");
        else
            return matrix[row * 3 + col];
    }

    /**
     * Gets the matrix data.
     * 
     * @return row majored matrix data
     */
    public float[] getData() {
        return matrix.clone();
    }

    /**
     * Gets a reference to the internal matrix data. >>>Don't use this
     * method.<<<
     * 
     * @return row majored matrix data
     */
    public float[] getDataRef() {
        return matrix;
    }

    /**
     * Gets the inverted matrix.
     * 
     * @return the inverted matrix
     */
    public Matrix3 inverse() {
        float det = matrix[0] * matrix[4] * matrix[8] + matrix[1] * matrix[5] * matrix[6] + matrix[2] * matrix[3] * matrix[7] - matrix[2] * matrix[4] * matrix[6] - matrix[1] * matrix[3] * matrix[8] - matrix[0] * matrix[5] * matrix[7];

        if (det == 0.0f)
            throw new RuntimeException("singular matrix is not invertible");

        Matrix3 retval = new Matrix3();
        float[] dst = retval.matrix;

        dst[0] = (matrix[4] * matrix[8] - matrix[5] * matrix[7]) / det;
        dst[1] = (matrix[2] * matrix[7] - matrix[1] * matrix[8]) / det;
        dst[2] = (matrix[1] * matrix[5] - matrix[2] * matrix[4]) / det;
        dst[3] = (matrix[5] * matrix[6] - matrix[3] * matrix[8]) / det;
        dst[4] = (matrix[0] * matrix[8] - matrix[2] * matrix[6]) / det;
        dst[5] = (matrix[2] * matrix[3] - matrix[0] * matrix[5]) / det;
        dst[6] = (matrix[3] * matrix[7] - matrix[4] * matrix[6]) / det;
        dst[7] = (matrix[1] * matrix[6] - matrix[0] * matrix[7]) / det;
        dst[8] = (matrix[0] * matrix[4] - matrix[1] * matrix[3]) / det;

        return retval;
    }

    /**
     * Multiply with matrix from left.
     * 
     * @param m
     *            the matrix
     * @return the calculated matrix
     */
    public Matrix3 leftMul(Matrix3 m) {
        return m.mul(this);
    }

    /**
     * Multiply with matrix.
     * 
     * @param m
     *            the matrix
     * @return the calculated matrix
     */
    public Matrix3 mul(Matrix3 m) {
        Matrix3 retval = new Matrix3();

        retval.matrix[0] = matrix[0] * m.matrix[0] + matrix[1] * m.matrix[3] + matrix[2] * m.matrix[6];
        retval.matrix[1] = matrix[0] * m.matrix[1] + matrix[1] * m.matrix[4] + matrix[2] * m.matrix[7];
        retval.matrix[2] = matrix[0] * m.matrix[2] + matrix[1] * m.matrix[5] + matrix[2] * m.matrix[8];
        retval.matrix[3] = matrix[3] * m.matrix[0] + matrix[4] * m.matrix[3] + matrix[5] * m.matrix[6];
        retval.matrix[4] = matrix[3] * m.matrix[1] + matrix[4] * m.matrix[4] + matrix[5] * m.matrix[7];
        retval.matrix[5] = matrix[3] * m.matrix[2] + matrix[4] * m.matrix[5] + matrix[5] * m.matrix[8];
        retval.matrix[6] = matrix[6] * m.matrix[0] + matrix[7] * m.matrix[3] + matrix[8] * m.matrix[6];
        retval.matrix[7] = matrix[6] * m.matrix[1] + matrix[7] * m.matrix[4] + matrix[8] * m.matrix[7];
        retval.matrix[8] = matrix[6] * m.matrix[2] + matrix[7] * m.matrix[5] + matrix[8] * m.matrix[8];

        return retval;
    }

    /**
     * Multiply with a vector.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector3 mul(Vector3 v) {
        Vector3 retval = new Vector3();
        float vectorOut[] = retval.xyz;
        float vectorIn[] = v.xyz;

        for (int i = 0; i < 3; i++) {
            vectorOut[i] = 0;
            for (int j = 0; j < 3; j++)
                vectorOut[i] += vectorIn[j] * matrix[i * 3 + j];
        }

        return retval;
    }

    /**
     * Subtract a matrix.
     * 
     * @param m
     *            the matrix
     * @return the calculated matrix
     */
    public Matrix3 sub(Matrix3 m) {
        Matrix3 retval = new Matrix3();

        for (int i = 0; i < 9; i++)
            retval.matrix[i] = matrix[i] - m.matrix[i];

        return retval;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "| " + get(0, 0) + " " + get(0, 1) + " " + get(0, 2) + " |\n" + "| " + get(1, 0) + " " + get(1, 1) + " " + get(1, 2) + " |\n" + "| " + get(2, 0) + " " + get(2, 1) + " " + get(2, 2) + " |\n";
    }

    /**
     * Gets the transposed matrix.
     * 
     * @return the transposed matrix
     */
    public Matrix3 transpose() {
        return new Matrix3(matrix[0], matrix[3], matrix[6], matrix[1], matrix[4], matrix[7], matrix[2], matrix[5], matrix[8]);
    }
}
