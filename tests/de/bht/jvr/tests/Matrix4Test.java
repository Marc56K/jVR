package de.bht.jvr.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
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
 */
public class Matrix4Test extends TestCase {
    public final void testAdd() {
        Matrix4 m = new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Matrix4 n = new Matrix4(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
        Matrix4 g = m.add(n);

        float k = 1;
        float f = 4;
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++) {
                Assert.assertEquals(k + f, g.get(row, col), 0);
                k++;
                f++;
            }
    }

    public final void testEqualsMatrix4() {
        Matrix4 m = new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Matrix4 n = new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 12, 13, 14, 15, 16);

        Assert.assertFalse(m.equals(n));
        Assert.assertTrue(m.equals(m));
    }

    public final void testExtractRotation() {
        Matrix4 n = Matrix4.rotate(new Vector3(1, 2, 3), 0.78f);
        Matrix4 m = n.extractRotation();

        Assert.assertTrue(n.equals(m));
    }

    public final void testGet() {
        Matrix4 m = new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

        float k = 1;
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++)
                Assert.assertEquals(k++, m.get(row, col), 0);
    }

    public final void testGetData() {
        float[] expect = new float[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

        Matrix4 m = new Matrix4(expect);
        float[] actual = m.getData();

        for (int i = 0; i < expect.length; i++)
            Assert.assertEquals(expect[i], actual[i], 0);
    }

    public final void testInverse() {
        Matrix4 m = new Matrix4();
        m = Matrix4.rotate(new Vector3(2, 1, -9), 1.14324f);

        Matrix4 n = Matrix4.translate(3, 1, 7);

        Matrix4 k = m.mul(n);
        Matrix4 l = k.inverse();

        Matrix4 actual = l.mul(k);
        Matrix4 expect = new Matrix4();

        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++)
                Assert.assertEquals(expect.get(row, col), actual.get(row, col), 0.0001);
    }

    public final void testMatrix4() {
        float[] expect = new float[] { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };

        Matrix4 m = new Matrix4();
        float[] actual = m.getData();

        for (int i = 0; i < expect.length; i++)
            Assert.assertEquals(expect[i], actual[i], 0);
    }

    public final void testMatrix4FloatArray() {
        float[] expect = new float[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

        Matrix4 m = new Matrix4(expect);
        float[] actual = m.getData();

        for (int i = 0; i < expect.length; i++)
            Assert.assertEquals(expect[i], actual[i], 0);
    }

    public final void testMatrix4FloatFloatFloatFloatFloatFloatFloatFloatFloatFloatFloatFloatFloatFloatFloatFloat() {
        float[] expect = new float[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

        Matrix4 m = new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        float[] actual = m.getData();

        for (int i = 0; i < expect.length; i++)
            Assert.assertEquals(expect[i], actual[i], 0);
    }

    public final void testMulMatrix4() {
        Matrix4 m = new Matrix4(1, 0, 0, 1, 0, 1, 0, 2, 0, 0, 1, 3, 0, 0, 0, 1);

        Matrix4 n = new Matrix4(1, 0, 0, 4, 0, 1, 0, 5, 0, 0, 1, 6, 0, 0, 0, 1);

        Matrix4 actual = m.mul(n);

        Matrix4 expect = new Matrix4(1, 0, 0, 5, 0, 1, 0, 7, 0, 0, 1, 9, 0, 0, 0, 1);

        Assert.assertTrue(actual.equals(expect));
    }

    public final void testMulVector4() {
        Matrix4 m = new Matrix4(1, 0, 0, 1, 0, 1, 0, 2, 0, 0, 1, 3, 0, 0, 0, 1);

        Vector4 v = new Vector4(4, 5, 6, 1);
        Vector4 actual = m.mul(v);

        Vector4 expect = new Vector4(5, 7, 9, 1);

        Assert.assertTrue(actual.equals(expect));

    }

    public final void testNoInverse() {
        Matrix4 m = new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

        try {
            m.inverse();
            fail("singular matrix: runtimeException expected");
        } catch (RuntimeException e) {}
    }

    public final void testRotate() {
        Matrix4 m = Matrix4.rotate(new Vector3(1, 0, 0), (float) (Math.PI / 2));

        Vector4 v = new Vector4(0, 2, 0, 1);

        Vector4 actual = m.mul(v);
        Vector4 expect = new Vector4(0, 0, 2, 1);

        Assert.assertEquals(expect.x(), actual.x(), 0.001);
        Assert.assertEquals(expect.y(), actual.y(), 0.001);
        Assert.assertEquals(expect.z(), actual.z(), 0.001);
        Assert.assertEquals(expect.w(), actual.w(), 0.001);
    }

    public final void testRotateX() {
        Matrix4 m = Matrix4.rotate(new Vector3(1, 0, 0), (float) (Math.PI / 2));

        Vector4 v = new Vector4(0, 2, 0, 1);

        Vector4 actual = m.mul(v);
        Vector4 expect = new Vector4(0, 0, 2, 1);

        Assert.assertEquals(expect.x(), actual.x(), 0.001);
        Assert.assertEquals(expect.y(), actual.y(), 0.001);
        Assert.assertEquals(expect.z(), actual.z(), 0.001);
        Assert.assertEquals(expect.w(), actual.w(), 0.001);
    }

    public final void testRotateY() {
        Matrix4 m = Matrix4.rotate(new Vector3(0, 1, 0), (float) (Math.PI / 2));

        Vector4 v = new Vector4(2, 0, 0, 1);

        Vector4 actual = m.mul(v);
        Vector4 expect = new Vector4(0, 0, -2, 1);

        Assert.assertEquals(expect.x(), actual.x(), 0.001);
        Assert.assertEquals(expect.y(), actual.y(), 0.001);
        Assert.assertEquals(expect.z(), actual.z(), 0.001);
        Assert.assertEquals(expect.w(), actual.w(), 0.001);
    }

    public final void testRotateZ() {
        Matrix4 m = Matrix4.rotate(new Vector3(0, 0, 1), (float) (Math.PI / 2));

        Vector4 v = new Vector4(0, 2, 0, 1);

        Vector4 actual = m.mul(v);
        Vector4 expect = new Vector4(-2, 0, 0, 1);

        Assert.assertEquals(expect.x(), actual.x(), 0.001);
        Assert.assertEquals(expect.y(), actual.y(), 0.001);
        Assert.assertEquals(expect.z(), actual.z(), 0.001);
        Assert.assertEquals(expect.w(), actual.w(), 0.001);
    }

    public final void testScale() {
        Matrix4 actual = Matrix4.scale(5, 6, 7);

        Matrix4 expect = new Matrix4(5, 0, 0, 0, 0, 6, 0, 0, 0, 0, 7, 0, 0, 0, 0, 1);

        Assert.assertTrue(actual.equals(expect));
    }

    public final void testSub() {
        Matrix4 m = new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Matrix4 n = new Matrix4(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
        Matrix4 g = m.sub(n);

        float k = 1;
        float f = 4;
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++) {
                Assert.assertEquals(k - f, g.get(row, col), 0);
                k++;
                f++;
            }
    }

    public final void testTranslate() {
        Matrix4 actual = Matrix4.translate(5, 7, 9);

        Matrix4 expect = new Matrix4(1, 0, 0, 5, 0, 1, 0, 7, 0, 0, 1, 9, 0, 0, 0, 1);

        Assert.assertTrue(actual.equals(expect));
    }

    public final void testTranspose() {
        Matrix4 m = new Matrix4(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

        Matrix4 actual = m.transpose();
        Matrix4 expect = new Matrix4(1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 4, 8, 12, 16);

        Assert.assertTrue(actual.equals(expect));
    }
}
