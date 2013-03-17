package de.bht.jvr.core;

import junit.framework.Assert;
import junit.framework.TestCase;
import de.bht.jvr.math.Matrix4;
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
public class TransformTest extends TestCase {
    public final void testInverseMatrix() {
        Transform t = Transform.translate(30, 60, 60).mul(Transform.rotateY(20)).mul(Transform.rotateX(-45).mul(Transform.scale(0.1f)));

        Matrix4 actual = t.getMatrix().mul(t.getInverseMatrix());
        Matrix4 expect = new Matrix4();

        for (int row = 0; row < 4; row++)
            for (int col = 0; col < 4; col++)
                Assert.assertEquals(expect.get(row, col), actual.get(row, col), 0.0001);

        System.err.println(actual);
    }
}
