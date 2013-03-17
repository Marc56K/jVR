package de.bht.jvr.core;

import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
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
 * The spot light emits light from a conical light source and casts an
 * elliptical pattern on objects hit by the light.
 * 
 * @author Marc Roßbach *
 */

public class SpotLightNode extends LightNode {
    /** The constant attenuation. */
    protected float constantAttenuation = 1.0f;

    /** The linear attenuation. */
    protected float linearAttenuation = 0;

    /** The quadratic attenuation. */
    protected float quadraticAttenuation = 0;

    /** The spot direction. */
    protected Vector3 spotDirection = new Vector3(0, 0, -1);

    /** The spot exponent. */
    protected float spotExponent = 1;

    /** The spot cut off. */
    protected float spotCutOff = 30;

    /**
     * Instantiates a new spot light node.
     */
    public SpotLightNode() {
        super();
    }

    /**
     * Instantiates a new spot light node.
     * 
     * @param name
     *            the name
     */
    public SpotLightNode(String name) {
        super();
        setName(name);
    }

    /**
     * Instantiates a new spot light node.
     * 
     * @param name
     *            the name
     * @param transform
     *            the transform
     */
    public SpotLightNode(String name, Transform transform) {
        super();
        setName(name);
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

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.LightNode#bind(de.bht.jvr.core.Context,
     * de.bht.jvr.core.Transform, de.bht.jvr.core.Transform)
     */
    @Override
    public void bind(Context ctx, Transform transform, Transform camTransform) throws Exception {
        super.bind(ctx, transform, camTransform); // default values

        Matrix4 finalTrans = camTransform.getInverseMatrix().mul(transform.getMatrix());

        Vector4 finalPos = finalTrans.mul(position).homogenize();

        Vector3 spotDirection = finalTrans.rotationMatrix().mul(this.spotDirection);

        float spotCutOff = (float) (this.spotCutOff * Math.PI / 180);

        ShaderProgram p = ctx.getShaderProgram();
        p.setUniform(ctx, "jvr_LightSource_Diffuse", new UniformColor(diffuse));
        p.setUniform(ctx, "jvr_LightSource_Specular", new UniformColor(specular));
        p.setUniform(ctx, "jvr_LightSource_Position", new UniformVector4(finalPos));

        p.setUniform(ctx, "jvr_LightSource_ConstantAttenuation", new UniformFloat(constantAttenuation));
        p.setUniform(ctx, "jvr_LightSource_LinearAttenuation", new UniformFloat(linearAttenuation));
        p.setUniform(ctx, "jvr_LightSource_QuadraticAttenuation", new UniformFloat(quadraticAttenuation));

        p.setUniform(ctx, "jvr_LightSource_SpotDirection", new UniformVector3(spotDirection));
        p.setUniform(ctx, "jvr_LightSource_SpotExponent", new UniformFloat(spotExponent));
        p.setUniform(ctx, "jvr_LightSource_SpotCutOff", new UniformFloat(spotCutOff));
        p.setUniform(ctx, "jvr_LightSource_SpotCosCutOff", new UniformFloat((float) Math.cos(spotCutOff)));
    }

    /**
     * Gets the constant attenuation.
     * 
     * @return the constant attenuation
     */
    public float getConstantAttenuation() {
        return constantAttenuation;
    }

    /**
     * Gets the linear attenuation.
     * 
     * @return the linear attenuation
     */
    public float getLinearAttenuation() {
        return linearAttenuation;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.LightNode#getProjectionMatrix()
     */
    @Override
    public Matrix4 getProjectionMatrix() {
        float nearPlane = 0.1f;
        float farPlane = 1000f;
        float aspectRatio = 1;
        float fov = (float) (Math.PI * 2 * spotCutOff / 180);
        float f = (float) (1.0f / Math.tan(fov / 2));
        float a = f / aspectRatio;
        float b = (farPlane + nearPlane) / (nearPlane - farPlane);
        float c = 2.0f * farPlane * nearPlane / (nearPlane - farPlane);
        float[] data = new float[] { a, 0, 0, 0, 0, f, 0, 0, 0, 0, b, c, 0, 0, -1, 0 };

        return new Matrix4(data);
    }

    /**
     * Gets the quadratic attenuation.
     * 
     * @return the quadratic attenuation
     */
    public float getQuadraticAttenuation() {
        return quadraticAttenuation;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.LightNode#clone()
     */
    @Override
    public LightNode getRenderClone() {
        SpotLightNode clone = new SpotLightNode();
        clone.diffuse = diffuse;
        clone.specular = specular;
        clone.intensity = intensity;
        clone.position = position;

        clone.constantAttenuation = constantAttenuation;
        clone.linearAttenuation = linearAttenuation;
        clone.quadraticAttenuation = quadraticAttenuation;

        clone.spotDirection = spotDirection;
        clone.spotExponent = spotExponent;
        clone.spotCutOff = spotCutOff;
        clone.castShadow = castShadow;
        clone.shadowBias = shadowBias;

        return clone;
    }

    /**
     * Gets the spot cut off.
     * 
     * @return the spot cut off
     */
    public float getSpotCutOff() {
        return spotCutOff;
    }

    /**
     * Gets the spot exponent.
     * 
     * @return the spot exponent
     */
    public float getSpotExponent() {
        return spotExponent;
    }

    /**
     * Sets the attenuation.
     * 
     * @param constantAttenuation
     *            the constant attenuation
     * @param linearAttenuation
     *            the linear attenuation
     * @param quadraticAttenuation
     *            the quadratic attenuation
     * @return the spot light node
     */
    public SpotLightNode setAttenuation(float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
        this.constantAttenuation = constantAttenuation;
        this.linearAttenuation = linearAttenuation;
        this.quadraticAttenuation = quadraticAttenuation;
        return this;
    }

    /**
     * Sets the constant attenuation.
     * 
     * @param constantAttenuation
     *            the constant attenuation
     * @return the spot light node
     */
    public SpotLightNode setConstantAttenuation(float constantAttenuation) {
        this.constantAttenuation = constantAttenuation;
        return this;
    }

    /**
     * Sets the linear attenuation.
     * 
     * @param linearAttenuation
     *            the linear attenuation
     * @return the spot light node
     */
    public SpotLightNode setLinearAttenuation(float linearAttenuation) {
        this.linearAttenuation = linearAttenuation;
        return this;
    }

    /**
     * Sets the quadratic attenuation.
     * 
     * @param quadraticAttenuation
     *            the quadratic attenuation
     * @return the spot light node
     */
    public SpotLightNode setQuadraticAttenuation(float quadraticAttenuation) {
        this.quadraticAttenuation = quadraticAttenuation;
        return this;
    }

    /**
     * Sets the spot cut off.
     * 
     * @param spotCutOff
     *            the spot cut off
     * @return the spot light node
     */
    public SpotLightNode setSpotCutOff(float spotCutOff) {
        this.spotCutOff = spotCutOff;
        return this;
    }

    /**
     * Sets the spot exponent.
     * 
     * @param spotExponent
     *            the spot exponent
     * @return the spot light node
     */
    public SpotLightNode setSpotExponent(float spotExponent) {
        this.spotExponent = spotExponent;
        return this;
    }
}
