package de.bht.jvr.tests;

import de.bht.jvr.math.Matrix3;
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
public class MatrixSpeedTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Matrix4 a = new Matrix4(234, 43532, 2423, 53, 525, 2, 53534, 5323, 4, 4234, 234, 32, 432, 432, 5, 325);
        Matrix4 b = new Matrix4(234, 532, 24, 53, 525, 2, 53534, 5323, 4, 44, 234, 32, 432, 432, 5, 325);

        Matrix3 c = new Matrix3(234, 43532, 2423, 53, 525, 2, 53534, 5323, 4);
        Matrix3 d = new Matrix3(234, 532, 24, 53, 525, 2, 53534, 5323, 4);

        while (true) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 90000; i++) {
                a.mul(b);
                a.inverse();
            }
            System.err.println("Matrix4: " + (System.currentTimeMillis() - start) + " ms");

            start = System.currentTimeMillis();
            for (int i = 0; i < 90000; i++) {
                c.mul(d);
                c.inverse();
            }
            System.err.println("Matrix3: " + (System.currentTimeMillis() - start) + " ms");
        }
    }

}
