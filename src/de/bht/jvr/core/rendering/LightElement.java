package de.bht.jvr.core.rendering;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.util.Color;

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
 * The light element contains a light and its world transformation for the next
 * frame.
 * 
 * @author Marc Roßbach
 */
public class LightElement {

    /** The light. */
    private LightNode light;

    /** The transform. */
    private Transform transform;

    /**
     * Instantiates a new light element.
     * 
     * @param lightNode
     *            the light node
     * @param transform
     *            the transform
     */
    public LightElement(LightNode lightNode, Transform transform) {
        light = lightNode.getRenderClone();
        this.transform = transform;
    }

    /**
     * Binds the light.
     * 
     * @param ctx
     *            the context
     * @param camTransform
     *            the camera transformation
     * @throws Exception
     *             the exception
     */
    public void bind(Context ctx, Transform camTransform) throws Exception {
        light.bind(ctx, transform, camTransform);
    }

    public float getPolygonOffset() {
        return light.getShadowBias();
    }

    public Color getAmbientColor() {
        return light.getAmbientColor();
    }

    /**
     * Gets the projection matrix.
     * 
     * @return the projection matrix
     */
    public Matrix4 getProjectionMatrix() {
        return light.getProjectionMatrix();
    }

    /**
     * Gets the transform.
     * 
     * @return the transform
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * Checks if the light is casting shadow.
     * 
     * @return true, if is casting shadow
     */
    public boolean isCastingShadow() {
        return light.isCastingShadow();
    }
}
