package de.bht.jvr.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
import de.bht.jvr.math.Matrix3;
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
 */
public class Matrix3Test extends TestCase {
    public final void testAdd() {
        Matrix3 m = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Matrix3 n = new Matrix3(4, 5, 6, 7, 8, 9, 10, 11, 12);
        Matrix3 g = m.add(n);

        float k = 1;
        float f = 4;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++) {
                Assert.assertEquals(k + f, g.get(row, col), 0);
                k++;
                f++;
            }
    }

    public final void testEqualsMatrix3() {
        Matrix3 m = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Matrix3 n = new Matrix3(1, 2, 3, 4, 5, 6, 0, 8, 9);

        Assert.assertFalse(m.equals(n));
        Assert.assertTrue(m.equals(m));
    }

    public final void testGet() {
        Matrix3 m = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9);

        float k = 1;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                Assert.assertEquals(k++, m.get(row, col), 0);
    }

    public final void testGetData() {
        float[] expect = new float[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        Matrix3 m = new Matrix3(expect);
        float[] actual = m.getData();

        for (int i = 0; i < expect.length; i++)
            Assert.assertEquals(expect[i], actual[i], 0);
    }

    public final void testInverse() {
        Matrix3 m = new Matrix3();
        m = Matrix3.rotate(new Vector3(2, 1, -9), 1.14324f);
        Matrix3 i = m.inverse();

        Matrix3 actual = i.mul(m);

        Matrix3 expect = new Matrix3();

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                Assert.assertEquals(expect.get(row, col), actual.get(row, col), 0.0001);
    }

    public final void testMatrix3() {
        float[] expect = new float[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 };

        Matrix3 m = new Matrix3();
        float[] actual = m.getData();

        for (int i = 0; i < expect.length; i++)
            Assert.assertEquals(expect[i], actual[i], 0);
    }

    public final void testMatrix3FloatArray() {
        float[] expect = new float[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        Matrix3 m = new Matrix3(expect);
        float[] actual = m.getData();

        for (int i = 0; i < expect.length; i++)
            Assert.assertEquals(expect[i], actual[i], 0);
    }

    public final void testMatrix3FloatFloatFloatFloatFloatFloatFloatFloatFloat() {
        float[] expect = new float[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

        Matrix3 m = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9);
        float[] actual = m.getData();

        for (int i = 0; i < expect.length; i++)
            Assert.assertEquals(expect[i], actual[i], 0);
    }

    public final void testMulMatrix3() {
        Matrix3 m = new Matrix3(1, 0, 2, 0, 1, 3, 0, 0, 1);

        Matrix3 n = new Matrix3(1, 0, 4, 0, 1, 5, 0, 0, 1);

        Matrix3 actual = m.mul(n);

        Matrix3 expect = new Matrix3(1, 0, 6, 0, 1, 8, 0, 0, 1);

        Assert.assertTrue(actual.equals(expect));
    }

    public final void testMulVector3() {
        Matrix3 m = new Matrix3(1, 0, 2, 0, 1, 3, 0, 0, 1);

        Vector3 v = new Vector3(4, 5, 1);
        Vector3 actual = m.mul(v);

        Vector3 expect = new Vector3(6, 8, 1);

        Assert.assertTrue(actual.equals(expect));
    }

    public final void testNoInverse() {
        Matrix3 m = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9);

        try {
            m.inverse();
            fail("singular matrix: runtimeException expected");
        } catch (RuntimeException e) {}
    }

    public final void testRotate() {
        Matrix3 m = Matrix3.rotate(new Vector3(1, 0, 0), (float) (Math.PI / 2));

        Vector3 v = new Vector3(0, 2, 0);

        Vector3 actual = m.mul(v);
        Vector3 expect = new Vector3(0, 0, 2);

        Assert.assertEquals(expect.x(), actual.x(), 0.001);
        Assert.assertEquals(expect.y(), actual.y(), 0.001);
        Assert.assertEquals(expect.z(), actual.z(), 0.001);
    }

    public final void testRotateX() {
        Matrix3 m = new Matrix3();
        m = Matrix3.rotate(new Vector3(1, 0, 0), (float) (Math.PI / 2));

        Vector3 v = new Vector3(0, 2, 0);

        Vector3 actual = m.mul(v);
        Vector3 expect = new Vector3(0, 0, 2);

        Assert.assertEquals(expect.x(), actual.x(), 0.001);
        Assert.assertEquals(expect.y(), actual.y(), 0.001);
        Assert.assertEquals(expect.z(), actual.z(), 0.001);
    }

    public final void testRotateY() {
        Matrix3 m = Matrix3.rotate(new Vector3(0, 1, 0), (float) (Math.PI / 2));

        Vector3 v = new Vector3(2, 0, 0);

        Vector3 actual = m.mul(v);
        Vector3 expect = new Vector3(0, 0, -2);

        Assert.assertEquals(expect.x(), actual.x(), 0.001);
        Assert.assertEquals(expect.y(), actual.y(), 0.001);
        Assert.assertEquals(expect.z(), actual.z(), 0.001);
    }

    public final void testRotateZ() {
        Matrix3 m = Matrix3.rotate(new Vector3(0, 0, 1), (float) (Math.PI / 2));

        Vector3 v = new Vector3(0, 2, 0);

        Vector3 actual = m.mul(v);
        Vector3 expect = new Vector3(-2, 0, 0);

        Assert.assertEquals(expect.x(), actual.x(), 0.001);
        Assert.assertEquals(expect.y(), actual.y(), 0.001);
        Assert.assertEquals(expect.z(), actual.z(), 0.001);
    }

    public final void testScale() {
        Matrix3 actual = Matrix3.scale(5, 6, 7);

        Matrix3 expect = new Matrix3(5, 0, 0, 0, 6, 0, 0, 0, 7);

        Assert.assertTrue(actual.equals(expect));
    }

    public final void testSub() {
        Matrix3 m = new Matrix3(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Matrix3 n = new Matrix3(4, 5, 6, 7, 8, 9, 10, 11, 12);
        Matrix3 g = m.sub(n);

        float k = 1;
        float f = 4;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++) {
                Assert.assertEquals(k - f, g.get(row, col), 0);
                k++;
                f++;
            }
    }

    public final void testTranspose() {
        Matrix3 m = new Matrix3(1, 2, 3, 5, 6, 7, 9, 10, 11);

        Matrix3 actual = m.transpose();
        Matrix3 expect = new Matrix3(1, 5, 9, 2, 6, 10, 3, 7, 11);

        Assert.assertTrue(actual.equals(expect));
    }

}
