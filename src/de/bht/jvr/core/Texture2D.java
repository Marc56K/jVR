package de.bht.jvr.core;

import java.awt.image.Raster;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.uniforms.UniformSampler2D;
import de.bht.jvr.core.uniforms.UniformValue;
import de.bht.jvr.logger.Log;

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
 * The 2D texture.
 * 
 * @author Marc Roßbach
 */
public class Texture2D extends TextureBase {
    public static boolean isPowerOfTwo(int n) {
        return (n & n - 1) == 0;
    }

    /** The image data. */
    private byte[] imageData = null;

    private int imageDataChannels = 4;

    /**
     * load texture from file.
     * 
     * @param imageFile
     *            the image file
     * @throws Exception
     *             the exception
     */
    public Texture2D(File imageFile) throws Exception {
        try {
            Log.info(this.getClass(), "Loading image: " + imageFile.getPath());
            Raster raster = read(imageFile);
            load(raster);
        } catch (Exception e) {
            Log.error(this.getClass(), "Can't load image: " + imageFile.getPath());
            throw new Exception("Can't load image: " + imageFile.getPath(), e);
        }
    }

    /**
     * load texture from input stream.
     * 
     * @param is
     *            the input stream
     * @throws Exception
     *             the exception
     */
    public Texture2D(InputStream is) throws Exception {
        String isName = is != null ? is.toString() : "null";
        try {
            Log.info(this.getClass(), "Loading image: " + isName);
            Raster raster = ImageIO.read(is).getData();
            load(raster);
        } catch (Exception e) {
            Log.error(this.getClass(), "Can't load image: " + isName);
            throw new Exception("Can't load image: " + isName, e);
        }
    }

    public Texture2D(int width, int height, byte[] imageData) {
        this.width = width;
        this.height = height;
        this.imageData = imageData;
        imageDataChannels = imageData.length / (width * height);

        isSemiTransparent = false;
        if (imageDataChannels == 4)
            // looking for semi transparent pixels
            for (int i = 3; i < this.imageData.length; i += 4) {
                byte alpha = this.imageData[i];
                if (alpha != -1 && alpha != 0) {
                    isSemiTransparent = true;
                    break;
                }
            }
    }

    /**
     * create empty texture.
     * 
     * @param width
     *            the width
     * @param height
     *            the height
     * @param format
     *            the format
     * @param samples
     *            the samples
     */
    public Texture2D(int width, int height, int format, int samples) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.samples = samples;
    }

    /**
     * Binds the texture.
     * 
     * @param ctx
     *            the context
     */
    @Override
    public void bind(Context ctx) {
        GL2GL3 gl = ctx.getGL();

        if (!built.get(ctx))
            build(ctx);

        gl.glBindTexture(GL.GL_TEXTURE_2D, id.get(ctx));

        if (format != GL2GL3.GL_DEPTH_COMPONENT && mipmapsDirty.get(ctx)) {
            gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
            mipmapsDirty.put(ctx, false);
        }
    }

    /**
     * Builds the texture.
     * 
     * @param ctx
     *            the context
     */
    @Override
    public void build(Context ctx) {
        GL2GL3 gl = ctx.getGL();

        int[] texId = new int[1];
        gl.glGenTextures(1, texId, 0);
        id.put(ctx, new Integer(texId[0]));

        if (imageData != null) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, id.get(ctx));
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);

            float[] maxAni = new float[1];
            gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAni, 0);
            // Log.info(this.getClass(), "Using " + maxAni[0] +
            // "x anisotropic filtering.");
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAni[0]);

            int format = GL.GL_RGBA;
            switch (imageDataChannels) {
            case 1:
                format = GL2GL3.GL_LUMINANCE;
                break;
            case 2:
                format = GL.GL_LUMINANCE_ALPHA;
                break;
            case 3:
                format = GL.GL_RGB;
                break;
            }

            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, format, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(imageData));
        } else {
            gl.glBindTexture(GL.GL_TEXTURE_2D, id.get(ctx));
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

            switch (format) {
            case GL2ES2.GL_DEPTH_COMPONENT:
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, format, width, height, 0, GL2ES2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, null);
                break;
            default:
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
                gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, format, width, height, 0, GL.GL_RGBA, GL.GL_FLOAT, null);
            }
        }

        built.put(ctx, new Boolean(true));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() {
        List<Context> ctxList = built.getContextList();
        for (Context ctx : ctxList)
            if (built.get(ctx))
                ctx.deleteTexture(id.get(ctx));
    }

    /**
     * Gets the image data.
     * 
     * @return the image data
     */
    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public UniformValue getUniformValue(int unit) {
        return new UniformSampler2D(unit);
    }

    /**
     * load texture from raster.
     * 
     * @param raster
     *            the raster
     * @throws Exception
     *             the exception
     */
    private void load(Raster raster) throws Exception {
        // read image properties
        width = raster.getWidth();
        height = raster.getHeight();
        int channels = raster.getNumDataElements();
        if (isPowerOfTwo(width) && isPowerOfTwo(height) && width > 1 && height > 1) // only
                                                                                    // GL_RGBA
                                                                                    // supports
                                                                                    // non-power-of-two
                                                                                    // textures
        {
            if (channels < 4)
                imageDataChannels = channels;
        } else if (width != 1 && height != 1)
            Log.warning(this.getClass(), "Texture dimensions are not power of two.");

        // allocate memory for image data
        imageData = new byte[imageDataChannels * width * height];

        // copy image data
        isSemiTransparent = false;
        int n = 0;
        int[] pixel = new int[Math.max(channels, 4)];
        pixel[3] = 255; // default alpha is 100%
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                raster.getPixel(x, y, pixel);

                if (pixel[3] > 0 && pixel[3] < 255)
                    isSemiTransparent = true;

                for (int i = 0; i < imageDataChannels; i++)
                    imageData[n++] = (byte) pixel[i];
            }

    }

    /**
     * Unbind all textures.
     * 
     * @param ctx
     *            the context
     */
    @Override
    public void unbind(Context ctx) {
        GL2GL3 gl = ctx.getGL();
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        gl.glDisable(GL.GL_TEXTURE_2D);
    }
}
