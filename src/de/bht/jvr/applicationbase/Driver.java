package de.bht.jvr.applicationbase;

import java.awt.event.KeyEvent;

import de.bht.jvr.math.Matrix4;
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

public class Driver {

    private float speed;
    private Vector3 position;
    float angleY = 0;
    float angleX = 0;

    Matrix4 driverMatrix = new Matrix4();

    public Driver(Vector3 start, float s) {
        speed = s;
        position = start;
    }

    public Matrix4 getViewMatrix() {
        return driverMatrix;
    }

    public void simulate(float elapsed, Input input) {
        float pi4 = (float) (Math.PI / 2);
        float da = pi4 * elapsed;

        if (input.isKeyDown(KeyEvent.VK_RIGHT))
            angleY -= da;
        if (input.isKeyDown(KeyEvent.VK_LEFT))
            angleY += da;
        if (input.isKeyDown(KeyEvent.VK_DOWN))
            angleX = Math.max(angleX - da, -pi4);
        if (input.isKeyDown(KeyEvent.VK_UP))
            angleX = Math.min(angleX + da, pi4);

        Matrix4 xRot = Matrix4.rotate(Vector3.X, angleX);
        Matrix4 yRot = Matrix4.rotate(Vector3.Y, angleY);

        Vector3 moveDirection = yRot.mulDir(Vector3.Z.mul(-1));

        if (input.isKeyDown(KeyEvent.VK_W))
            position = position.add(moveDirection.mul(speed * elapsed));
        if (input.isKeyDown(KeyEvent.VK_S))
            position = position.sub(moveDirection.mul(speed * elapsed));
        if (input.isKeyDown(KeyEvent.VK_A))
            position = position.add(Vector3.Y.cross(moveDirection).mul(speed * elapsed));
        if (input.isKeyDown(KeyEvent.VK_D))
            position = position.sub(Vector3.Y.cross(moveDirection).mul(speed * elapsed));

        driverMatrix = Matrix4.translate(position).mul(yRot.mul(xRot));
    }
}
