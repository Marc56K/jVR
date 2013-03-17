package de.bht.jvr.tests;

import junit.framework.Assert;
import junit.framework.TestCase;
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
public class Vector3Test extends TestCase {
    public final void testCross() {
        Vector3 v = new Vector3(1, 0, 0);
        Vector3 w = new Vector3(0, 1, 0);
        Vector3 actual = v.cross(w);
        Vector3 expect = new Vector3(0, 0, 1);

        Assert.assertTrue(expect.equals(actual));
    }

    public final void testDot() {
        Vector3 v = new Vector3(1, 2, 4);
        Vector3 w = new Vector3(5, 6, 7);
        float actual = v.dot(w);
        float expect = 1 * 5 + 2 * 6 + 4 * 7;

        Assert.assertEquals(expect, actual, 0);
    }

}
