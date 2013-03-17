package de.bht.jvr.core;

import java.util.HashMap;
import java.util.Map;

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
 * The shape node consists of a material and a geometry.
 * 
 * @author Marc Roßbach
 */

public class ShapeNode extends SceneNode {
    /** The material. */
    private Material material = null;

    /** The geometry. */
    private Geometry geometry = null;

    /** The level of detail geometries */
    private Map<Float, Geometry> lodGeometries = new HashMap<Float, Geometry>();

    /**
     * Instantiates a new shape node.
     */
    public ShapeNode() {}

    /**
     * Instantiates a new shape node.
     * 
     * @param name
     *            the name
     */
    public ShapeNode(String name) {
        setName(name);
    }

    /**
     * Instantiates a new shape node.
     * 
     * @param name
     *            the name
     * @param geometry
     *            the geometry
     * @param material
     *            the material
     */
    public ShapeNode(String name, Geometry geometry, Material material) {
        setName(name);
        setMaterial(material);
        setGeometry(geometry);
    }

    /**
     * Instantiates a new shape node.
     * 
     * @param name
     *            the name
     * @param geometry
     *            the geometry
     * @param material
     *            the material
     * @param transform
     *            the transform
     */
    public ShapeNode(String name, Geometry geometry, Material material, Transform transform) {
        this(name, geometry, material);
        setTransform(transform);
    }

    /**
     * Instantiates a new shape node.
     * 
     * @param name
     *            the name
     * @param transform
     *            the transform
     */
    public ShapeNode(String name, Transform transform) {
        setName(name);
        setTransform(transform);
    }

    /**
     * Instantiates a new shape node.
     * 
     * @param transform
     *            the transform
     */
    public ShapeNode(Transform transform) {
        setTransform(transform);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.SceneNode#accept(de.bht.jvr.core.Traverser)
     */
    @Override
    public boolean accept(Traverser traverser) {
        return traverser.visit(this);
    }

    /**
     * Sets the level of detail geometry.
     * 
     * @param distanceToCamera
     *            the distance to camera
     * @param geometry
     *            the geometry
     * @return the shape node
     */
    public ShapeNode addLODGeometry(float distanceToCamera, Geometry geometry) {
        if (distanceToCamera == 0)
            setGeometry(geometry);
        else
            lodGeometries.put(Math.abs(distanceToCamera), geometry);
        return this;
    }

    /**
     * Gets all level of detail geometries with camera distance greater than
     * zero.
     * 
     * @return the level of detail geometries
     */
    public Map<Float, Geometry> getAllLODGeometries() {
        return new HashMap<Float, Geometry>(lodGeometries);
    }

    /**
     * Gets the geometry.
     * 
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Gets the material.
     * 
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Removes all level of detail geometries with camera distance greater than
     * zero.
     * 
     * @return the shape node
     */
    public ShapeNode removeAllLODGeometries() {
        lodGeometries.clear();
        return this;
    }

    /**
     * Sets the geometry.
     * 
     * @param geometry
     *            the geometry
     * @return the shape node
     */
    public ShapeNode setGeometry(Geometry geometry) {
        this.geometry = geometry;
        super.setBBoxDirty(false);
        return this;
    }

    /**
     * Sets the material.
     * 
     * @param material
     *            the material
     * @return the shape node
     */
    public ShapeNode setMaterial(Material material) {
        this.material = material;
        return this;
    }

    @Override
    protected void updateBBox() {
        if (geometry != null)
            super.bBox = geometry.getBBox();
    }
}
