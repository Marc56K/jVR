package de.bht.jvr.core;

import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.core.uniforms.UniformVector4;
import de.bht.jvr.logger.Log;
import de.bht.jvr.math.Matrix4;
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
 * The point light emits light outward from a single point in 3D space in all
 * directions.
 * 
 * @author Marc Roßbach
 */
public class PointLightNode extends LightNode {
    /** The constant attenuation. */
    protected float constantAttenuation = 1.0f;

    /** The linear attenuation. */
    protected float linearAttenuation = 0;

    /** The quadratic attenuation. */
    protected float quadraticAttenuation = 0;

    /**
     * Instantiates a new point light node.
     */
    public PointLightNode() {
        super();
    }

    /**
     * Instantiates a new point light node.
     * 
     * @param name
     *            the name
     */
    public PointLightNode(String name) {
        super();
        setName(name);
    }

    /**
     * Instantiates a new point light node.
     * 
     * @param name
     *            the name
     * @param transform
     *            the transform
     */
    public PointLightNode(String name, Transform transform) {
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

        Vector4 finalPos = camTransform.getInverseMatrix().mul(transform.getMatrix()).mul(position);
        finalPos = finalPos.div(finalPos.w());

        ShaderProgram p = ctx.getShaderProgram();
        p.setUniform(ctx, "jvr_LightSource_Diffuse", new UniformColor(diffuse));
        p.setUniform(ctx, "jvr_LightSource_Specular", new UniformColor(specular));
        p.setUniform(ctx, "jvr_LightSource_Position", new UniformVector4(finalPos));

        p.setUniform(ctx, "jvr_LightSource_ConstantAttenuation", new UniformFloat(constantAttenuation));
        p.setUniform(ctx, "jvr_LightSource_LinearAttenuation", new UniformFloat(linearAttenuation));
        p.setUniform(ctx, "jvr_LightSource_QuadraticAttenuation", new UniformFloat(quadraticAttenuation));
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
        return new Matrix4();
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
        PointLightNode clone = new PointLightNode();
        clone.diffuse = diffuse;
        clone.specular = specular;
        clone.intensity = intensity;
        clone.position = position;
        clone.constantAttenuation = constantAttenuation;
        clone.linearAttenuation = linearAttenuation;
        clone.quadraticAttenuation = quadraticAttenuation;
        clone.castShadow = castShadow;

        return clone;
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
     * @return the point light node
     */
    public PointLightNode setAttenuation(float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
        this.constantAttenuation = constantAttenuation;
        this.linearAttenuation = linearAttenuation;
        this.quadraticAttenuation = quadraticAttenuation;
        return this;
    }

    /**
     * Sets the uniform jvr_LightSource_CastShadow = true
     * 
     * @param castShadow
     *            on/off
     * @return the light node
     */
    @Override
    public LightNode setCastShadow(boolean castShadow) {
        if (castShadow)
            Log.warning(this.getClass(), "Projection matrix not yet implemented for point lights.");
        return super.setCastShadow(castShadow);
    }

    /**
     * Sets the constant attenuation.
     * 
     * @param constantAttenuation
     *            the constant attenuation
     * @return the point light node
     */
    public PointLightNode setConstantAttenuation(float constantAttenuation) {
        this.constantAttenuation = constantAttenuation;
        return this;
    }

    /**
     * Sets the linear attenuation.
     * 
     * @param linearAttenuation
     *            the linear attenuation
     * @return the point light node
     */
    public PointLightNode setLinearAttenuation(float linearAttenuation) {
        this.linearAttenuation = linearAttenuation;
        return this;
    }

    /**
     * Sets the quadratic attenuation.
     * 
     * @param quadraticAttenuation
     *            the quadratic attenuation
     * @return the point light node
     */
    public PointLightNode setQuadraticAttenuation(float quadraticAttenuation) {
        this.quadraticAttenuation = quadraticAttenuation;
        return this;
    }
}
