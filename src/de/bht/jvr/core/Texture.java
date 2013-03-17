package de.bht.jvr.core;

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
 * The texture interface.
 * 
 * @author Marc Roßbach
 */
public interface Texture {
    /**
     * Binds the texture.
     * 
     * @param ctx
     *            the context
     */
    public void bind(Context ctx);

    /**
     * Builds the texture.
     * 
     * @param ctx
     *            the context
     */
    public void build(Context ctx);

    /**
     * Gets the height.
     * 
     * @return the height
     */
    public int getHeight();

    /**
     * Gets the texture id.
     * 
     * @param ctx
     *            the context
     * @return the tex id
     */
    public int getId(Context ctx);

    public UniformValue getUniformValue(int unit);

    /**
     * Gets the width.
     * 
     * @return the width
     */
    public int getWidth();

    /**
     * Returns true if this texture has pixel with alpha channel between 0 and
     * 255.
     * 
     * @return true, if texture is half transparent
     */
    public boolean isSemiTransparent();

    /**
     * Unbind all textures.
     * 
     * @param ctx
     *            the context
     */
    public void unbind(Context ctx);

    /**
     * Marks the mipmaps as invalid.
     * 
     * @param ctx
     *            the context
     */
    public void InvalidateMipmaps(Context ctx);
}
