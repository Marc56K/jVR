package de.bht.jvr.core.rendering;

import de.bht.jvr.core.BBox;
import de.bht.jvr.core.Transform;
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
 *
 *
 * The view frustum contains the clipping planes
 * 
 * @author Marc Roßbach
 */
public class Frustum {
    private Vector4[] planes = new Vector4[6];
    private Transform camTransform;

    public Frustum(Matrix4 projMatrix, Transform camTransform) {
        this.camTransform = camTransform;

        float[] clip = projMatrix.transpose().getData();

        // near plane
        float a = clip[3] + clip[2];
        float b = clip[7] + clip[6];
        float c = clip[11] + clip[10];
        float d = clip[15] + clip[14];
        Vector3 abc = new Vector3(a, b, c);
        planes[0] = new Vector4(abc.normalize(), d / abc.length());

        // far plane
        a = clip[3] - clip[2];
        b = clip[7] - clip[6];
        c = clip[11] - clip[10];
        d = clip[15] - clip[14];
        abc = new Vector3(a, b, c);
        planes[1] = new Vector4(abc.normalize(), d / abc.length());

        // left plane
        a = clip[3] + clip[0];
        b = clip[7] + clip[4];
        c = clip[11] + clip[8];
        d = clip[15] + clip[12];
        abc = new Vector3(a, b, c);
        planes[2] = new Vector4(abc.normalize(), d / abc.length());

        // right plane
        a = clip[3] - clip[0];
        b = clip[7] - clip[4];
        c = clip[11] - clip[8];
        d = clip[15] - clip[12];
        abc = new Vector3(a, b, c);
        planes[3] = new Vector4(abc.normalize(), d / abc.length());

        // bottom plane
        a = clip[3] + clip[1];
        b = clip[7] + clip[5];
        c = clip[11] + clip[9];
        d = clip[15] + clip[13];
        abc = new Vector3(a, b, c);
        planes[4] = new Vector4(abc.normalize(), d / abc.length());

        // top plane
        a = clip[3] - clip[1];
        b = clip[7] - clip[5];
        c = clip[11] - clip[9];
        d = clip[15] - clip[13];
        abc = new Vector3(a, b, c);
        planes[5] = new Vector4(abc.normalize(), d / abc.length());
    }

    public boolean isInViewFrustum(BBox bBox, Transform worldTrans) {
        if (bBox.isInfinite())
            return true;

        Matrix4 mvMatrix = camTransform.getInverseMatrix().mul(worldTrans.getMatrix());

        // transform bounding box to camera space
        Vector4 center = new Vector4(bBox.getCenter(), 1);
        center = mvMatrix.mul(center).homogenize();
        Vector4 max = new Vector4(bBox.getMax(), 1);
        max = mvMatrix.mul(max).homogenize();

        float radius = center.sub(max).length();

        for (Vector4 plane : planes)
            if (plane.dot(center) + radius < 0)
                return false;

        return true;
    }

    public boolean isInViewFrustum(Matrix4 mvMatrix, BBox bBox) {
        if (bBox.isInfinite())
            return true;

        Vector4 center = new Vector4(bBox.getCenter(), 1);
        center = mvMatrix.mul(center).homogenize();
        Vector4 max = new Vector4(bBox.getMax(), 1);
        max = mvMatrix.mul(max).homogenize();

        float radius = center.sub(max).length();

        for (Vector4 plane : planes)
            if (plane.dot(center) + radius < 0)
                return false;

        return true;
    }
}
