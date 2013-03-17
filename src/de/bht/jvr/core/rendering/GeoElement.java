package de.bht.jvr.core.rendering;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.PipelineState;
import de.bht.jvr.core.uniforms.UniformMatrix3;
import de.bht.jvr.core.uniforms.UniformMatrix4;
import de.bht.jvr.math.Matrix3;
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
 * The geo element contains the camera transformation, a geometry and its
 * transformation for the next frame.
 * 
 * @author Marc Roßbach
 */
public class GeoElement implements Comparable<GeoElement> {
    /** The transformation. */
    private Transform transform;

    /** The camera transformation. */
    private Transform camTransform = null;

    /** The geometry. */
    private Geometry geometry = null;

    /** The level of detail geometries. */
    private Map<Float, Geometry> lodGeometries = null;

    /** The z dist. */
    private float zDist = 0; // distance from camera

    private boolean zDistDirty = true;

    private Matrix4 mvMatrix;

    private Matrix3 normalMatrix;

    /**
     * Instantiates a new geo element.
     * 
     * @param geometry
     *            the geometry
     * @param transform
     *            the transform
     */
    public GeoElement(Geometry geometry, Transform transform) {
        this.transform = transform;
        this.geometry = geometry.getRenderClone();

    }

    public void addLODGeometry(float dist, Geometry geometry) {
        if (lodGeometries == null)
            lodGeometries = new HashMap<Float, Geometry>();
        lodGeometries.put(dist, geometry.getRenderClone());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(GeoElement ge) {
        if (ge.getZDistance() > getZDistance())
            return -1;
        else
            return 1;
    }

    /**
     * Gets the z distance.
     * 
     * @return the z distance
     */
    public float getZDistance() {
        if (zDistDirty) {
            // update z distance
            Vector3 c3 = geometry.getBBox().getCenter();
            Vector4 c4 = new Vector4(c3, 1);
            zDist = camTransform.getInverseMatrix().mul(transform.getMatrix()).mul(c4).homogenize().z();

            zDistDirty = false;
        }

        return zDist;
    }

    /**
     * Calculates the model view and normal matrix and renders the geometry.
     * 
     * @param ctx
     * @param pipelineState
     * @param viewFrustum
     * @param tmu
     * @throws Exception
     */
    public void render(Context ctx, PipelineState pipelineState, Frustum viewFrustum, int tmu) throws Exception {
        ShaderProgram program = ctx.getShaderProgram();

        Matrix4 projMatrix = pipelineState.getActiveProjMatrix();

        // view frustum culling
        if (viewFrustum != null && geometry.isBuilt(ctx))
            if (!viewFrustum.isInViewFrustum(mvMatrix, geometry.getBBox()))
                return;

        // set model matrix
        program.setUniform(ctx, "jvr_ModelMatrix", new UniformMatrix4(transform.getMatrix()));

        // set model view matrix
        program.setUniform(ctx, "jvr_ModelViewMatrix", new UniformMatrix4(mvMatrix));

        // set model view projection matrix (camera space)
        Matrix4 pmvMatrix = projMatrix.mul(mvMatrix);
        program.setUniform(ctx, "jvr_ModelViewProjectionMatrix", new UniformMatrix4(pmvMatrix));

        // set normal matrix (camera space)
        program.setUniform(ctx, "jvr_NormalMatrix", new UniformMatrix3(normalMatrix));

        // light stuff
        // /////////////////////////////////////////////////////////////////////////////
        Transform lightTransform = null;
        Matrix4 lightProjMatrix = null;
        LightElement light = pipelineState.getActiveLightElement();
        if (light != null) {
            lightTransform = light.getTransform();
            if (light.isCastingShadow())
                lightProjMatrix = light.getProjectionMatrix();
        }

        // set model view matrix (light space)
        Matrix4 lmvMatrix = null;
        if (lightTransform != null) {
            lmvMatrix = lightTransform.getInverseMatrix().mul(transform.getMatrix());
            program.setUniform(ctx, "jvr_LightSource_ModelViewMatrix", new UniformMatrix4(lmvMatrix));
        }

        // set model view projection matrix (light space)
        if (lightProjMatrix != null) {
            if (lmvMatrix == null)
                lmvMatrix = lightTransform.getInverseMatrix().mul(transform.getMatrix());
            Matrix4 lpmvMatrix = lightProjMatrix.mul(lmvMatrix);
            program.setUniform(ctx, "jvr_LightSource_ModelViewProjectionMatrix", new UniformMatrix4(lpmvMatrix));
        }

        // bind user defined uniforms
        pipelineState.bindUniformsAndTextures(ctx, tmu);

        // render the geometry
        Geometry lodGeo = geometry;
        if (lodGeometries != null) // level of detail
        {
            float camDist = Math.abs(getZDistance());
            float closestDist = Float.MAX_VALUE;
            for (Entry<Float, Geometry> entry : lodGeometries.entrySet()) {
                float dist = entry.getKey();
                Geometry geo = entry.getValue();

                if (camDist >= dist && camDist - dist < closestDist) {
                    lodGeo = geo;
                    closestDist = camDist - dist;
                }
            }
        }

        lodGeo.render(ctx);
    }

    /**
     * Sets the camera transformation.
     * 
     * @param camTransform
     *            the new camera transformation
     */
    public void setCamTransform(Transform camTransform) {
        if (camTransform != this.camTransform) {
            this.camTransform = camTransform;
            update();
            zDistDirty = true;
        }
    }

    /**
     * Update z distance.
     */
    private void update() {
        // update model view matrix (camera space)
        mvMatrix = camTransform.getInverseMatrix().mul(transform.getMatrix());

        // update normal matrix (camera space)
        normalMatrix = mvMatrix.rotationMatrix().inverse().transpose();
    }
}
