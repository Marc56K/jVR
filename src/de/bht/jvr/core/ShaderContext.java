package de.bht.jvr.core;

import java.util.HashMap;

import de.bht.jvr.core.uniforms.UniformValue;

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
 * The shader context contains the textures, the uniforms and the shader program
 * of the shader material.
 * 
 * @author Marc Roßbach
 */

public class ShaderContext {

    /** The textures. */
    private HashMap<String, Texture> textures = new HashMap<String, Texture>();

    /** The uniforms. */
    private HashMap<String, UniformValue> uniforms = new HashMap<String, UniformValue>();

    /** The shader program. */
    private ShaderProgram shaderProgram = null;

    /**
     * Gets a clone of the shader context.
     * 
     * @return the shader context clone
     */
    public ShaderContext getShaderContextClone() {
        ShaderContext sCtx = new ShaderContext();
        sCtx.textures = new HashMap<String, Texture>(textures);
        sCtx.uniforms = new HashMap<String, UniformValue>(uniforms);
        sCtx.shaderProgram = shaderProgram;

        return sCtx;
    }

    /**
     * Gets the shader program.
     * 
     * @return the shaderProgram
     */
    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    /**
     * Gets the textures.
     * 
     * @return the textures
     */
    public HashMap<String, Texture> getTextures() {
        return textures;
    }

    /**
     * Gets the uniforms.
     * 
     * @return the uniforms
     */
    public HashMap<String, UniformValue> getUniforms() {
        return uniforms;
    }

    /**
     * Checks if is valid.
     * 
     * @return true, if is valid
     */
    public boolean isValid() {
        return shaderProgram != null; // TODO ...
    }

    /**
     * Sets the shader program.
     * 
     * @param shaderProgram
     *            the shaderProgram to set
     */
    public void setShaderProgram(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
    }

    /**
     * Sets the texture.
     * 
     * @param name
     *            the name
     * @param texture
     *            the texture
     */
    public void setTexture(String name, Texture texture) {
        textures.put(name, texture);
    }

    /**
     * Sets the uniform.
     * 
     * @param name
     *            the name
     * @param value
     *            the value
     */
    public void setUniform(String name, UniformValue value) {
        uniforms.put(name, value);
    }

    @Override
    public String toString() {
        // TODO
        return shaderProgram.toString();
    }
}
