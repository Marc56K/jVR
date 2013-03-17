package de.bht.jvr.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
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
public class Vector4Test extends TestCase {
    public final void testCross() {
        Vector4 v = new Vector4(1, 0, 0, 0);
        Vector4 w = new Vector4(0, 1, 0, 0);
        Vector4 actual = v.cross(w);
        Vector4 expect = new Vector4(0, 0, 1, 0);

        Assert.assertTrue(expect.equals(actual));
    }

    public final void testDot() {
        Vector4 v = new Vector4(1, 2, 4, 1);
        Vector4 w = new Vector4(5, 6, 7, 1);
        float actual = v.dot(w);
        float expect = 1 * 5 + 2 * 6 + 4 * 7 + 1 * 1;

        Assert.assertEquals(expect, actual, 0);
    }

    public final void testLength() {
        Vector4 v = new Vector4(2, 3, 4, 5);
        float actual = v.length();
        float expect = (float) Math.sqrt(2 * 2 + 3 * 3 + 4 * 4 + 5 * 5);

        Assert.assertEquals(expect, actual, 0);
    }
}
