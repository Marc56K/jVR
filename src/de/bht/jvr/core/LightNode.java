package de.bht.jvr.core;

import de.bht.jvr.util.Color;

import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.core.uniforms.UniformMatrix4;
import de.bht.jvr.core.uniforms.UniformVector3;
import de.bht.jvr.core.uniforms.UniformVector4;
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
 * The abstract light node.
 * 
 * @author Marc Roßbach
 */

public abstract class LightNode extends SceneNode {
    /** The ambient intensity contribution of this light source. */
    protected Color ambient = new Color(0.1f, 0.1f, 0.1f, 1.0f);

    /** The diffuse color. */
    protected Color diffuse = new Color(0.7f, 0.7f, 0.7f, 1.0f);

    /** The specular clor. */
    protected Color specular = new Color(0.7f, 0.7f, 0.7f, 1.0f);

    /** The light intensity. */
    protected float intensity = 1;

    /** The light position. */
    protected Vector4 position = new Vector4(0, 0, 0, 1);

    /** The light casts shadow. */
    protected boolean castShadow = false;

    /** The shadow bias. */
    protected float shadowBias = 0.5f;

    /**
     * Instantiates a new light node.
     */
    public LightNode() {}

    /**
     * Binds the light.
     * 
     * @param ctx
     *            the context
     * @param transform
     *            the transform
     * @param camTransform
     *            the camera transform
     * @throws Exception
     *             the exception
     */
    public void bind(Context ctx, Transform transform, Transform camTransform) throws Exception {
        ShaderProgram p = ctx.getShaderProgram();
        p.setUniform(ctx, "jvr_LightSource_Diffuse", new UniformColor(diffuse));
        p.setUniform(ctx, "jvr_LightSource_Specular", new UniformColor(specular));
        p.setUniform(ctx, "jvr_LightSource_Intensity", new UniformFloat(intensity));
        p.setUniform(ctx, "jvr_LightSource_Position", new UniformVector4(new Vector4()));

        p.setUniform(ctx, "jvr_LightSource_ConstantAttenuation", new UniformFloat(1));
        p.setUniform(ctx, "jvr_LightSource_LinearAttenuation", new UniformFloat(0));
        p.setUniform(ctx, "jvr_LightSource_QuadraticAttenuation", new UniformFloat(0));

        p.setUniform(ctx, "jvr_LightSource_SpotDirection", new UniformVector3(new Vector3()));
        p.setUniform(ctx, "jvr_LightSource_SpotExponent", new UniformFloat(0));
        p.setUniform(ctx, "jvr_LightSource_SpotCutOff", new UniformFloat(0));
        p.setUniform(ctx, "jvr_LightSource_SpotCosCutOff", new UniformFloat(1));

        // shadow mapping stuff
        p.setUniform(ctx, "jvr_LightSource_ModelViewMatrix", new UniformMatrix4(transform.getInverseMatrix()));
        p.setUniform(ctx, "jvr_LightSource_ProjectionMatrix", new UniformMatrix4(getProjectionMatrix()));
        p.setUniform(ctx, "jvr_LightSource_ModelViewProjectionMatrix", new UniformMatrix4(getProjectionMatrix().mul(transform.getInverseMatrix())));

        p.setUniform(ctx, "jvr_LightSource_ShadowBias", new UniformFloat(shadowBias));
        p.setUniform(ctx, "jvr_LightSource_CastShadow", new UniformBool(castShadow));
    }

    /**
     * Gets the diffuse color.
     * 
     * @return the diffuse color
     */
    public Color getDiffuseColor() {
        return diffuse;
    }

    /**
     * Gets the diffuse color.
     * 
     * @return the diffuse color
     */
    public Color getAmbientColor() {
        return ambient;
    }

    /**
     * Gets the intensity.
     * 
     * @return the intensity
     */
    public float getIntensity() {
        return intensity;
    }

    /**
     * Gets the projection matrix.
     * 
     * @return the projection matrix
     */
    public abstract Matrix4 getProjectionMatrix();

    public abstract LightNode getRenderClone();

    /**
     * Gets the shadow mapping bias.
     * 
     * @return the shadow mapping bias
     */
    public float getShadowBias() {
        return shadowBias;
    }

    /**
     * Gets the specular color.
     * 
     * @return the specular color
     */
    public Color getSpecularColor() {
        return specular;
    }

    /**
     * Checks if is casting shadow.
     * 
     * @return true, if is casting shadow
     */
    public boolean isCastingShadow() {
        return castShadow;
    }

    /**
     * Sets the uniform jvr_LightSource_CastShadow = true
     * 
     * @param castShadow
     *            on/off
     * @return the light node
     */
    public LightNode setCastShadow(boolean castShadow) {
        this.castShadow = castShadow;
        return this;
    }

    /**
     * Sets the color.
     * 
     * @param c
     *            the color
     * @return the light node
     */
    public LightNode setColor(Color c) {
        ambient = c.modulateRGB(0.1f);
        diffuse = c.modulateRGB(0.7f);
        specular = c.modulateRGB(0.7f);
        return this;
    }

    /**
     * Sets the diffuse color.
     * 
     * @param diffuseColor
     *            the diffuse color
     * @return the light node
     */
    public LightNode setDiffuseColor(Color diffuseColor) {
        diffuse = diffuseColor;
        return this;
    }

    /**
     * Sets the ambient color.
     * 
     * @param ambientColor
     *            the diffuse color
     * @return the light node
     */
    public LightNode setAmbientColor(Color ambientColor) {
        ambient = ambientColor;
        return this;
    }

    /**
     * Sets the intensity.
     * 
     * @param intensity
     *            the new intensity
     * @return the light node
     */
    public LightNode setIntensity(float intensity) {
        this.intensity = intensity;
        return this;
    }

    /**
     * Sets the shadow mapping bias.
     * 
     * @param shadowBias
     *            the new shadow mapping bias (default is 0.5)
     * @return the light node
     */
    public LightNode setShadowBias(float shadowBias) {
        this.shadowBias = shadowBias;
        return this;
    }

    /**
     * Sets the specular color.
     * 
     * @param specularColor
     *            the specular color
     * @return the light node
     */
    public LightNode setSpecularColor(Color specularColor) {
        specular = specularColor;
        return this;
    }

    @Override
    protected void updateBBox() {
        super.bBox = BBox.infinite();
    }
}
