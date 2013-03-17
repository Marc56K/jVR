package de.bht.jvr.core;

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
 */

public interface Material {
    /**
     * Binds the material.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public boolean bind(Context ctx) throws Exception;

    /**
     * Gets the material class.
     * 
     * @return the material class
     */
    public String getMaterialClass();

    /**
     * Gets the number of textures.
     * 
     * @param shaderContext
     *            the shader context
     * @return the number of textures
     */
    public int getNumTextures(String shaderContext);

    /**
     * Gets a clone of the material
     * 
     * @return the cloned material
     */
    public Material getRenderClone();

    /**
     * Gets the shader programs and its shader contexts.
     * 
     * @return the map with the shader programs and its shader contexts as key
     */
    public Map<String, ShaderProgram> getShaderPrograms();

    /**
     * Unbinds the material.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public void unbind(Context ctx) throws Exception;
}
