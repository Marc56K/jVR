package de.bht.jvr.core;

import de.bht.jvr.core.uniforms.UniformColor;
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
 * The directional light emits parallel rays of light in a specified direction
 * from a source located at an infinite distance.
 * 
 * @author Marc Roßbach
 */
public class DirectionalLightNode extends LightNode {
    /**
     * Instantiates a new directional light node.
     */
    public DirectionalLightNode() {
        super();
        position = new Vector4(0, 0, -1, 0);
    }

    /**
     * Instantiates a new directional light node.
     * 
     * @param name
     *            the name
     */
    public DirectionalLightNode(String name) {
        this();
        setName(name);
    }

    /**
     * Instantiates a new directional light node.
     * 
     * @param name
     *            the name
     * @param transform
     *            the transform
     */
    public DirectionalLightNode(String name, Transform transform) {
        this();
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

        ShaderProgram p = ctx.getShaderProgram();
        p.setUniform(ctx, "jvr_LightSource_Diffuse", new UniformColor(diffuse));
        p.setUniform(ctx, "jvr_LightSource_Specular", new UniformColor(specular));
        p.setUniform(ctx, "jvr_LightSource_Position", new UniformVector4(finalPos));
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.LightNode#getProjectionMatrix()
     */
    @Override
    public Matrix4 getProjectionMatrix() {
        return new Matrix4();
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.LightNode#clone()
     */
    @Override
    public LightNode getRenderClone() {
        DirectionalLightNode clone = new DirectionalLightNode();
        clone.diffuse = diffuse;
        clone.specular = specular;
        clone.intensity = intensity;
        clone.position = position;
        clone.castShadow = castShadow;

        return clone;
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
            Log.warning(this.getClass(), "Projection matrix not yet implemented for directional lights.");
        return super.setCastShadow(castShadow);
    }
}
