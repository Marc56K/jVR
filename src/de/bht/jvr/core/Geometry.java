package de.bht.jvr.core;

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

public interface Geometry {
    /**
     * Gets the bounding box.
     * 
     * @return the bounding box
     */
    public BBox getBBox();

    public Geometry getRenderClone();

    /**
     * Checks if the geometry is built.
     * 
     * @param ctx
     *            the context
     * @return true, if is built
     */
    public boolean isBuilt(Context ctx);

    /**
     * Calculates the intersection point with a picking ray.
     * 
     * @param ray
     *            the picking ray
     * @return the intersection point
     */
    public Vector3 pick(PickRay ray);

    /**
     * Render the geometry.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public void render(Context ctx) throws Exception;
}
