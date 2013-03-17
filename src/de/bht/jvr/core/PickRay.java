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
 *
 *
 * The pick ray consists of a origin and a direction.
 * 
 * @author Marc Roßbach
 */
public class PickRay {

    /** The ray origin. */
    private Vector3 rayOrigin;

    /** The ray direction. */
    private Vector3 rayDirection;

    /**
     * Instantiates a new pick ray.
     * 
     * @param origin
     *            the origin
     * @param direction
     *            the direction
     */
    public PickRay(Vector3 origin, Vector3 direction) {
        rayDirection = direction;
        rayOrigin = origin;
    }

    /**
     * Gets the ray direction.
     * 
     * @return the ray direction
     */
    public Vector3 getRayDirection() {
        return rayDirection;
    }

    /**
     * Gets the ray origin.
     * 
     * @return the ray origin
     */
    public Vector3 getRayOrigin() {
        return rayOrigin;
    }

    /**
     * Sets the ray direction.
     * 
     * @param rayDirection
     *            the new ray direction
     */
    public void setRayDirection(Vector3 rayDirection) {
        this.rayDirection = rayDirection;
    }

    /**
     * Sets the ray origin.
     * 
     * @param rayOrigin
     *            the new ray origin
     */
    public void setRayOrigin(Vector3 rayOrigin) {
        this.rayOrigin = rayOrigin;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Origin: " + rayOrigin + " Direction: " + rayDirection;
    }
}
