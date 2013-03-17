package de.bht.jvr.core.rendering;

import java.util.ArrayList;
import java.util.List;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.PipelineState;

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
 * Contains all geo elements with the same material.
 * 
 * @author Marc Roßbach
 */
public class GeoElementGroup implements Comparable<GeoElementGroup> {
    /** The geo elements for unsorted rendering. */
    private List<GeoElement> geoElements = null;

    /** The geo element for sorted rendering */
    private GeoElement geoElement = null;

    /** The material. */
    private Material material;

    /** The distance from camera. */
    private float zDist = 0;

    private boolean zDistDirty = true;

    /**
     * Instantiates a new geo element group.
     * 
     * @param material
     *            the material
     */
    public GeoElementGroup(Material material) {
        this.material = material;
    }

    /**
     * Adds a geo element for unsorted rendering.
     * 
     * @param geoElement
     *            the geo element
     */
    public void addGeoElement(GeoElement geoElement) {
        if (geoElements == null)
            geoElements = new ArrayList<GeoElement>();
        geoElements.add(geoElement);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(GeoElementGroup group) {
        if (group.getZDistance() > getZDistance())
            return -1;
        else
            return 1;
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
     * Gets the material class.
     * 
     * @return the material class
     */
    public String getMaterialClass() {
        return material.getMaterialClass();
    }

    /**
     * Gets the z distance.
     * 
     * @return the z distance
     */
    public float getZDistance() {
        if (zDistDirty) {
            float z = 0;
            if (geoElements != null) {
                for (GeoElement geoElement : geoElements)
                    z += geoElement.getZDistance();
                zDist = z / geoElements.size();
            } else if (geoElement != null)
                zDist = geoElement.getZDistance();

            zDistDirty = false;
        }

        return zDist;
    }

    /**
     * Binds the material and the projection matrix and renders the geometries
     * in this group.
     * 
     * @param ctx
     *            the context
     * @param shaderContext
     *            the shader context
     * @param pipelineState
     *            the pipeline state
     * @param viewFrustum
     *            the view frustum
     * @throws Exception
     *             the exception
     */
    public void render(Context ctx, String shaderContext, PipelineState pipelineState, Frustum viewFrustum) throws Exception {
        ctx.setShaderContext(shaderContext);

        if (material.bind(ctx)) {
            // bind pipeline uniforms
            pipelineState.bindGlobalUniforms(ctx);

            if (geoElements != null)
                // render geometry
                for (GeoElement geoElement : geoElements)
                    geoElement.render(ctx, pipelineState, viewFrustum, material.getNumTextures(shaderContext));
            else if (geoElement != null)
                geoElement.render(ctx, pipelineState, viewFrustum, material.getNumTextures(shaderContext));
        }
    }

    /**
     * Sets the cam transform.
     * 
     * @param camTransform
     *            the new cam transform
     */
    public void setCamTransform(Transform camTransform) {
        if (geoElements != null)
            for (GeoElement geoElement : geoElements)
                geoElement.setCamTransform(camTransform);
        else if (geoElement != null)
            geoElement.setCamTransform(camTransform);

        zDistDirty = true;
    }

    /**
     * Adds a geo element for sorted rendering
     * 
     * @param geoElement
     */
    public void setGeoElement(GeoElement geoElement) {
        this.geoElement = geoElement;
    }
}
