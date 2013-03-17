package de.bht.jvr.examples.physics;

import javax.vecmath.Matrix4f;

import com.bulletphysics.linearmath.MotionState;

import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Transform;
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

public class MyMotionState implements MotionState {
    private SceneNode node;

    public MyMotionState(SceneNode node) {
        this.node = node;
    }

    @Override
    public com.bulletphysics.linearmath.Transform getWorldTransform(com.bulletphysics.linearmath.Transform out) {
        float[] t = node.getTransform().getMatrix().getData();
        out.set(new Matrix4f(t));
        return out;
    }

    @Override
    public void setWorldTransform(com.bulletphysics.linearmath.Transform worldTrans) {
        Matrix4f m = new Matrix4f();
        worldTrans.getMatrix(m);
        float[] newTrans = new float[] { m.m00, m.m01, m.m02, m.m03, m.m10, m.m11, m.m12, m.m13, m.m20, m.m21, m.m22, m.m23, m.m30, m.m31, m.m32, m.m33 };
        node.setTransform(new Transform(new Matrix4(newTrans)));
    }
}
