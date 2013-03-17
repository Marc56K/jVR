package de.bht.jvr.math;

import java.io.Serializable;
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
 * A immutable 4x4 matrix.
 * 
 * @author Marc Roßbach
 */

public class Matrix4 implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5150142847320050544L;

    /** The matrix. */
    protected float[] matrix = new float[16];

    public static Matrix4 I = new Matrix4();

    /**
     * Construct a lookat matrix that can directly be used as an inverse view
     * transformation for OpenGL rendering.
     * 
     * @param eye
     *            Position of the eye.
     * @param center
     *            Position of the lookat point.
     * @param up
     *            Up direction.
     * @return
     */
    public static Matrix4 lookat(Vector3 eye, Vector3 center, Vector3 up) {
        Vector3 backwards = eye.sub(center).normalize();
        Vector3 side = up.cross(backwards).normalize();
        up = backwards.cross(side).normalize();
        Matrix4 r = new Matrix4(side, up, backwards);
        Matrix4 t = Matrix4.translate(eye.mul(-1.0f));
        return r.mul(t);
    }

    /**
     * Construct an inverted lookat matrix that can be used to position a camra
     * in a scene graph.
     * 
     * @param eye
     *            Position of the eye.
     * @param center
     *            Position of the lookat point.
     * @param up
     *            Up direction.
     * @return
     */
    public static Matrix4 lookatI(Vector3 eye, Vector3 center, Vector3 up) {
        Vector3 backwards = eye.sub(center).normalize();
        Vector3 side = up.cross(backwards).normalize();
        up = backwards.cross(side).normalize();
        Matrix4 r = new Matrix4(side, up, backwards, false);
        Matrix4 t = Matrix4.translate(eye);
        return t.mul(r);
    }

    /**
     * Create rotation matrix.
     * 
     * @param axis
     *            the axis
     * @param angle
     *            the angle
     * @return the rotation matrix
     */
    public static Matrix4 rotate(Vector3 axis, float angle) {
        if (angle == 0.0f)
            return new Matrix4();

        Vector3 ax = axis.normalize();
        float x, y, z, s, c, t;
        x = ax.x();
        y = ax.y();
        z = ax.z();
        s = (float) Math.sin(angle);
        c = (float) Math.cos(angle);
        t = 1 - c;

        return new Matrix4(c + x * x * t, x * y * t - z * s, x * z * t + y * s, 0, y * x * t + z * s, c + y * y * t, y * z * t - x * s, 0, z * x * t - y * s, z * y * t + x * s, c + z * z * t, 0, 0, 0, 0, 1);
    }

    /**
     * Create scale matrix.
     * 
     * @param scaleX
     *            scale x
     * @param scaleY
     *            scale y
     * @param scaleZ
     *            scale z
     * @return the scale matrix
     */
    public static Matrix4 scale(float scaleX, float scaleY, float scaleZ) {
        Matrix4 scl = new Matrix4();
        scl.matrix[0] = scaleX;
        scl.matrix[5] = scaleY;
        scl.matrix[10] = scaleZ;

        return scl;
    }

    /**
     * Create scale matrix.
     * 
     * @param scale
     *            Scale factors as a vector.
     * @return The scale matrix.
     */
    public static Matrix4 scale(Vector3 scale) {
        Matrix4 scl = new Matrix4();
        scl.matrix[0] = scale.x();
        scl.matrix[5] = scale.y();
        scl.matrix[10] = scale.z();

        return scl;
    }

    /**
     * Create translation matrix.
     * 
     * @param dx
     *            the x position
     * @param dy
     *            the y position
     * @param dz
     *            the z position
     * @return the translation matrix
     */
    public static Matrix4 translate(float dx, float dy, float dz) {
        Matrix4 trn = new Matrix4();
        trn.matrix[3] = dx;
        trn.matrix[7] = dy;
        trn.matrix[11] = dz;

        return trn;
    }

    /**
     * Create translation matrix.
     * 
     * @param v
     *            the translation vector
     * @return the translation matrix
     */
    public static Matrix4 translate(Vector3 v) {
        return translate(v.x(), v.y(), v.z());
    }

    /**
     * Instantiates a new matrix4.
     */
    public Matrix4() {
        matrix[0] = 1;
        matrix[5] = 1;
        matrix[10] = 1;
        matrix[15] = 1;
    }

    /**
     * Instantiates a new matrix4.
     * 
     * @param v00
     *            The first matrix element in the first row.
     * @param v01
     *            The second matrix element in the first row.
     * @param v02
     *            The third matrix element in the first row.
     * @param v03
     *            The fourth matrix element in the first row.
     * @param v10
     *            The first matrix element in the second row.
     * @param v11
     *            The second matrix element in the second row.
     * @param v12
     *            The third matrix element in the second row.
     * @param v13
     *            The fourth matrix element in the second row.
     * @param v20
     *            The first matrix element in the third row.
     * @param v21
     *            The second matrix element in the third row.
     * @param v22
     *            The third matrix element in the third row.
     * @param v23
     *            The fourth matrix element in the third row.
     * @param v30
     *            The first matrix element in the fourth row.
     * @param v31
     *            The second matrix element in the fourth row.
     * @param v32
     *            The third matrix element in the fourth row.
     * @param v33
     *            The fourth matrix element in the fourth row.
     */
    public Matrix4(float v00, float v01, float v02, float v03, float v10, float v11, float v12, float v13, float v20, float v21, float v22, float v23, float v30, float v31, float v32, float v33) {
        matrix[0] = v00;
        matrix[1] = v01;
        matrix[2] = v02;
        matrix[3] = v03;

        matrix[4] = v10;
        matrix[5] = v11;
        matrix[6] = v12;
        matrix[7] = v13;

        matrix[8] = v20;
        matrix[9] = v21;
        matrix[10] = v22;
        matrix[11] = v23;

        matrix[12] = v30;
        matrix[13] = v31;
        matrix[14] = v32;
        matrix[15] = v33;
    }

    /**
     * Instantiates a new matrix4.
     * 
     * @param array
     *            the row majored matrix data
     */
    public Matrix4(float[] array) {
        if (array.length != 16)
            throw new RuntimeException("Invalid array length.");

        matrix = array.clone();
    }

    /**
     * Construct a matrix from three row vectors.
     * 
     * @param b0
     *            First row vector.
     * @param b1
     *            Second row vector.
     * @param b2
     *            Third row vector.
     */
    public Matrix4(Vector3 b0, Vector3 b1, Vector3 b2) {
        set(0, 0, b0.x());
        set(0, 1, b0.y());
        set(0, 2, b0.z());
        set(1, 0, b1.x());
        set(1, 1, b1.y());
        set(1, 2, b1.z());
        set(2, 0, b2.x());
        set(2, 1, b2.y());
        set(2, 2, b2.z());
        set(3, 3, 1);
    }

    /**
     * Construct a matrix from three row or column vectors.
     * 
     * @param b0
     *            First row vector.
     * @param b1
     *            Second row vector.
     * @param b2
     *            Third row vector.
     * @param rows
     *            Use vectors as matrix rows if <code>true</code>. Use as
     *            columns otherwise.
     */
    public Matrix4(Vector3 b0, Vector3 b1, Vector3 b2, boolean rows) {
        if (rows) {
            set(0, 0, b0.x());
            set(0, 1, b0.y());
            set(0, 2, b0.z());
            set(1, 0, b1.x());
            set(1, 1, b1.y());
            set(1, 2, b1.z());
            set(2, 0, b2.x());
            set(2, 1, b2.y());
            set(2, 2, b2.z());
        } else {
            set(0, 0, b0.x());
            set(1, 0, b0.y());
            set(2, 0, b0.z());
            set(0, 1, b1.x());
            set(1, 1, b1.y());
            set(2, 1, b1.z());
            set(0, 2, b2.x());
            set(1, 2, b2.y());
            set(2, 2, b2.z());
        }
        set(3, 3, 1);
    }

    /**
     * Adds a matrix.
     * 
     * @param m
     *            the matrix
     * @return the calculated matrix
     */
    public Matrix4 add(Matrix4 m) {
        Matrix4 retval = new Matrix4();

        for (int i = 0; i < 16; i++)
            retval.matrix[i] = matrix[i] + m.matrix[i];

        return retval;
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof Matrix4)
            return Arrays.equals(matrix, ((Matrix4) val).matrix);
        return false;
    }

    /**
     * Gets the rotation of this matrix.
     * 
     * @return the rotation matrix
     */
    public Matrix4 extractRotation() {
        return new Matrix4(matrix[0], matrix[1], matrix[2], 0, matrix[4], matrix[5], matrix[6], 0, matrix[8], matrix[9], matrix[10], 0, 0, 0, 0, 1);
    }

    /**
     * Gets the translation of this matrix.
     * 
     * @return the translation matrix
     */
    public Matrix4 extractTranslation() {
        return translate(matrix[3], matrix[7], matrix[11]);
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
        if (row < 0 || row > 3 || col < 0 || col > 3)
            throw new RuntimeException("Index out of bounds.");
        else
            return matrix[row * 4 + col];
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
     * Gets the inverse matrix.
     * 
     * @return the inverse matrix
     */
    public Matrix4 inverse() {
        Matrix4 ret = new Matrix4();
        float[] mat = matrix;
        float[] dst = ret.matrix;
        float[] tmp = new float[12];

        /* temparray for pairs */
        float src[] = new float[16];

        /* array of transpose source matrix */
        float det;

        /* determinant */
        /* transpose matrix */
        for (int i = 0; i < 4; i++) {
            src[i] = mat[i * 4];
            src[i + 4] = mat[i * 4 + 1];
            src[i + 8] = mat[i * 4 + 2];
            src[i + 12] = mat[i * 4 + 3];
        }

        /* calculate pairs for first 8 elements (cofactors) */
        tmp[0] = src[10] * src[15];
        tmp[1] = src[11] * src[14];
        tmp[2] = src[9] * src[15];
        tmp[3] = src[11] * src[13];
        tmp[4] = src[9] * src[14];
        tmp[5] = src[10] * src[13];
        tmp[6] = src[8] * src[15];
        tmp[7] = src[11] * src[12];
        tmp[8] = src[8] * src[14];
        tmp[9] = src[10] * src[12];
        tmp[10] = src[8] * src[13];
        tmp[11] = src[9] * src[12];

        /* calculate first 8 elements (cofactors) */
        dst[0] = tmp[0] * src[5] + tmp[3] * src[6] + tmp[4] * src[7];
        dst[0] -= tmp[1] * src[5] + tmp[2] * src[6] + tmp[5] * src[7];
        dst[1] = tmp[1] * src[4] + tmp[6] * src[6] + tmp[9] * src[7];
        dst[1] -= tmp[0] * src[4] + tmp[7] * src[6] + tmp[8] * src[7];
        dst[2] = tmp[2] * src[4] + tmp[7] * src[5] + tmp[10] * src[7];
        dst[2] -= tmp[3] * src[4] + tmp[6] * src[5] + tmp[11] * src[7];
        dst[3] = tmp[5] * src[4] + tmp[8] * src[5] + tmp[11] * src[6];
        dst[3] -= tmp[4] * src[4] + tmp[9] * src[5] + tmp[10] * src[6];
        dst[4] = tmp[1] * src[1] + tmp[2] * src[2] + tmp[5] * src[3];
        dst[4] -= tmp[0] * src[1] + tmp[3] * src[2] + tmp[4] * src[3];
        dst[5] = tmp[0] * src[0] + tmp[7] * src[2] + tmp[8] * src[3];
        dst[5] -= tmp[1] * src[0] + tmp[6] * src[2] + tmp[9] * src[3];
        dst[6] = tmp[3] * src[0] + tmp[6] * src[1] + tmp[11] * src[3];
        dst[6] -= tmp[2] * src[0] + tmp[7] * src[1] + tmp[10] * src[3];
        dst[7] = tmp[4] * src[0] + tmp[9] * src[1] + tmp[10] * src[2];
        dst[7] -= tmp[5] * src[0] + tmp[8] * src[1] + tmp[11] * src[2];

        /* calculate pairs for second 8 elements (cofactors) */
        tmp[0] = src[2] * src[7];
        tmp[1] = src[3] * src[6];
        tmp[2] = src[1] * src[7];
        tmp[3] = src[3] * src[5];
        tmp[4] = src[1] * src[6];
        tmp[5] = src[2] * src[5];
        tmp[6] = src[0] * src[7];
        tmp[7] = src[3] * src[4];
        tmp[8] = src[0] * src[6];
        tmp[9] = src[2] * src[4];
        tmp[10] = src[0] * src[5];
        tmp[11] = src[1] * src[4];

        /* calculate second 8 elements (cofactors) */
        dst[8] = tmp[0] * src[13] + tmp[3] * src[14] + tmp[4] * src[15];
        dst[8] -= tmp[1] * src[13] + tmp[2] * src[14] + tmp[5] * src[15];
        dst[9] = tmp[1] * src[12] + tmp[6] * src[14] + tmp[9] * src[15];
        dst[9] -= tmp[0] * src[12] + tmp[7] * src[14] + tmp[8] * src[15];
        dst[10] = tmp[2] * src[12] + tmp[7] * src[13] + tmp[10] * src[15];
        dst[10] -= tmp[3] * src[12] + tmp[6] * src[13] + tmp[11] * src[15];
        dst[11] = tmp[5] * src[12] + tmp[8] * src[13] + tmp[11] * src[14];
        dst[11] -= tmp[4] * src[12] + tmp[9] * src[13] + tmp[10] * src[14];
        dst[12] = tmp[2] * src[10] + tmp[5] * src[11] + tmp[1] * src[9];
        dst[12] -= tmp[4] * src[11] + tmp[0] * src[9] + tmp[3] * src[10];
        dst[13] = tmp[8] * src[11] + tmp[0] * src[8] + tmp[7] * src[10];
        dst[13] -= tmp[6] * src[10] + tmp[9] * src[11] + tmp[1] * src[8];
        dst[14] = tmp[6] * src[9] + tmp[11] * src[11] + tmp[3] * src[8];
        dst[14] -= tmp[10] * src[11] + tmp[2] * src[8] + tmp[7] * src[9];
        dst[15] = tmp[10] * src[10] + tmp[4] * src[8] + tmp[9] * src[9];
        dst[15] -= tmp[8] * src[9] + tmp[11] * src[10] + tmp[5] * src[8];

        /* calculate determinant */
        det = src[0] * dst[0] + src[1] * dst[1] + src[2] * dst[2] + src[3] * dst[3];

        if (det == 0.0f)
            throw new RuntimeException("singular matrix is not invertible");

        /* calculate matrix inverse */
        det = 1 / det;

        for (int j = 0; j < 16; j++)
            dst[j] *= det;

        return ret;
    }

    /**
     * Multiply with matrix from left.
     * 
     * @param m
     *            the matrix
     * @return the calculated matrix
     */
    public Matrix4 leftMul(Matrix4 m) {
        return m.mul(this);
    }

    /**
     * Multiply with matrix.
     * 
     * @param m
     *            the matrix
     * @return the calculated matrix
     */
    public Matrix4 mul(Matrix4 m) {
        Matrix4 retval = new Matrix4();

        retval.matrix[0] = matrix[0] * m.matrix[0] + matrix[1] * m.matrix[4] + matrix[2] * m.matrix[8] + matrix[3] * m.matrix[12];
        retval.matrix[1] = matrix[0] * m.matrix[1] + matrix[1] * m.matrix[5] + matrix[2] * m.matrix[9] + matrix[3] * m.matrix[13];
        retval.matrix[2] = matrix[0] * m.matrix[2] + matrix[1] * m.matrix[6] + matrix[2] * m.matrix[10] + matrix[3] * m.matrix[14];
        retval.matrix[3] = matrix[0] * m.matrix[3] + matrix[1] * m.matrix[7] + matrix[2] * m.matrix[11] + matrix[3] * m.matrix[15];
        retval.matrix[4] = matrix[4] * m.matrix[0] + matrix[5] * m.matrix[4] + matrix[6] * m.matrix[8] + matrix[7] * m.matrix[12];
        retval.matrix[5] = matrix[4] * m.matrix[1] + matrix[5] * m.matrix[5] + matrix[6] * m.matrix[9] + matrix[7] * m.matrix[13];
        retval.matrix[6] = matrix[4] * m.matrix[2] + matrix[5] * m.matrix[6] + matrix[6] * m.matrix[10] + matrix[7] * m.matrix[14];
        retval.matrix[7] = matrix[4] * m.matrix[3] + matrix[5] * m.matrix[7] + matrix[6] * m.matrix[11] + matrix[7] * m.matrix[15];
        retval.matrix[8] = matrix[8] * m.matrix[0] + matrix[9] * m.matrix[4] + matrix[10] * m.matrix[8] + matrix[11] * m.matrix[12];
        retval.matrix[9] = matrix[8] * m.matrix[1] + matrix[9] * m.matrix[5] + matrix[10] * m.matrix[9] + matrix[11] * m.matrix[13];
        retval.matrix[10] = matrix[8] * m.matrix[2] + matrix[9] * m.matrix[6] + matrix[10] * m.matrix[10] + matrix[11] * m.matrix[14];
        retval.matrix[11] = matrix[8] * m.matrix[3] + matrix[9] * m.matrix[7] + matrix[10] * m.matrix[11] + matrix[11] * m.matrix[15];
        retval.matrix[12] = matrix[12] * m.matrix[0] + matrix[13] * m.matrix[4] + matrix[14] * m.matrix[8] + matrix[15] * m.matrix[12];
        retval.matrix[13] = matrix[12] * m.matrix[1] + matrix[13] * m.matrix[5] + matrix[14] * m.matrix[9] + matrix[15] * m.matrix[13];
        retval.matrix[14] = matrix[12] * m.matrix[2] + matrix[13] * m.matrix[6] + matrix[14] * m.matrix[10] + matrix[15] * m.matrix[14];
        retval.matrix[15] = matrix[12] * m.matrix[3] + matrix[13] * m.matrix[7] + matrix[14] * m.matrix[11] + matrix[15] * m.matrix[15];

        return retval;
    }

    /**
     * Multiply with vector.
     * 
     * @param v
     *            the vector
     * @return the calculated vector
     */
    public Vector4 mul(Vector4 v) {
        Vector4 retval = new Vector4();
        float vectorOut[] = retval.xyzw;
        float vectorIn[] = v.xyzw;

        for (int i = 0; i < 4; i++) {
            vectorOut[i] = 0;
            for (int j = 0; j < 4; j++)
                vectorOut[i] += vectorIn[j] * matrix[i * 4 + j];
        }

        return retval;
    }

    /**
     * Multiply with direction.
     * 
     * @param dir
     *            the direction
     * @return the calculated direction
     */
    public Vector3 mulDir(Vector3 dir) {
        return this.mul(new Vector4(dir, 0)).xyz();
    }

    /**
     * Multiply with point.
     * 
     * @param p
     *            the point
     * @return the calculated point
     */
    public Vector3 mulPoint(Vector3 p) {
        return this.mul(new Vector4(p, 1)).xyz();
    }

    /**
     * Gets the rotation matrix.
     * 
     * @return the rotation matrix
     */
    public Matrix3 rotationMatrix() {
        return new Matrix3(matrix[0], matrix[1], matrix[2], matrix[4], matrix[5], matrix[6], matrix[8], matrix[9], matrix[10]);
    }

    private void set(int row, int col, float v) {
        matrix[row * 4 + col] = v;
    }

    /**
     * Subtract a matrix.
     * 
     * @param m
     *            the matrix
     * @return the calculated matrixs
     */
    public Matrix4 sub(Matrix4 m) {
        Matrix4 retval = new Matrix4();

        for (int i = 0; i < 16; i++)
            retval.matrix[i] = matrix[i] - m.matrix[i];

        return retval;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "| " + get(0, 0) + " " + get(0, 1) + " " + get(0, 2) + " " + get(0, 3) + " |\n" + "| " + get(1, 0) + " " + get(1, 1) + " " + get(1, 2) + " " + get(1, 3) + " |\n" + "| " + get(2, 0) + " " + get(2, 1) + " " + get(2, 2) + " " + get(2, 3) + " |\n" + "| " + get(3, 0) + " " + get(3, 1) + " " + get(3, 2) + " " + get(3, 3) + " |\n";
    }

    /**
     * Gets the translation of this matrix.
     * 
     * @return the translation vector
     */
    public Vector3 translation() {
        return new Vector3(matrix[3], matrix[7], matrix[11]);
    }

    /**
     * Gets the transposed matrix.
     * 
     * @return the transposed matrix
     */
    public Matrix4 transpose() {
        return new Matrix4(matrix[0], matrix[4], matrix[8], matrix[12], matrix[1], matrix[5], matrix[9], matrix[13], matrix[2], matrix[6], matrix[10], matrix[14], matrix[3], matrix[7], matrix[11], matrix[15]);
    }
}
