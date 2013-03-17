package de.bht.jvr.core;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

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
 * The texture base.
 * 
 * @author Marc Roßbach
 */
public abstract class TextureBase implements Texture {
    /** The id of the texture sampler. */
    protected ContextValueMap<Integer> id = new ContextValueMap<Integer>(-1);

    /** The texture is built. */
    protected ContextValueMap<Boolean> built = new ContextValueMap<Boolean>(false);

    /** True, if mipmaps are invalid */
    protected ContextValueMap<Boolean> mipmapsDirty = new ContextValueMap<Boolean>(true);

    /** The width. */
    protected int width = 0;

    /** The height. */
    protected int height = 0;

    /** The format. */
    protected int format = GL.GL_RGBA;

    /** The samples. */
    protected int samples = 4;

    /** Is a half transparent texture. */
    protected boolean isSemiTransparent = true;
    
    /** Semaphore for concurrent texture loading */
    private static final Semaphore readLock = new Semaphore(1);

    /**
     * Gets the height.
     * 
     * @return the height
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Gets the texture id.
     * 
     * @param ctx
     *            the context
     * @return the tex id
     */
    @Override
    public int getId(Context ctx) {
        return id.get(ctx);
    }

    /**
     * Gets the width.
     * 
     * @return the width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Returns true if this texture has pixel with alpha channel between 0 and
     * 255.
     * 
     * @return true, if texture is half transparent
     */
    @Override
    public boolean isSemiTransparent() {
        return isSemiTransparent;
    }

    /**
     * Marks the mipmaps as invalid.
     * 
     * @param ctx
     *            the context
     */
    @Override
    public void InvalidateMipmaps(Context ctx) {
        mipmapsDirty.put(ctx, true);
    }
    
    /**
     * As ImageIO.read is not thread safe the access to this method needs
     * to be protected by a semaphore.
     * 
     * @param file The image to load.
     * @return The Raster of the image.
     * @throws InterruptedException On concurrency fault.
     * @throws IOException On IO fault.
     */
    protected Raster read(File file) throws InterruptedException, IOException {
        TextureBase.readLock.acquire();
        Raster raster = ImageIO.read( file ).getData();
        TextureBase.readLock.release();
        return raster;
    }
}
