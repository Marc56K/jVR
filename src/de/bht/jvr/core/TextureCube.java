package de.bht.jvr.core;

import java.awt.image.Raster;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.uniforms.UniformSamplerCube;
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
 * The cube texture.
 * 
 * @author Henrik Tramberend
 */
public class TextureCube extends TextureBase {
    /** The image data. */
    private byte[][] imageData = null;
    private int channels;

    private int[] target = { GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };

    /**
     * Load cube map images from six files. The image order is +x, -x,, +y, -y,
     * +z, -z. All files need to have the same size and format.
     * 
     * @param rightImage
     * @param leftImage
     * @param topImage
     * @param downImage
     * @param backImage
     * @param frontImage
     * @throws Exception
     */
    public TextureCube(File rightImage, File leftImage, File topImage, File downImage, File backImage, File frontImage) throws Exception {
        this(new File[] { rightImage, leftImage, topImage, downImage, backImage, frontImage });
    }

    /**
     * Load cube map images from six files. The image order is +x, -x,, +y, -y,
     * +z, -z. All files need to have the same size and format.
     * 
     * @param imageFiles
     *            The image files: right, left, top, bottom, back, front
     * @throws Exception
     */
    public TextureCube(File[] imageFiles) throws Exception {
        if (imageFiles.length != 6)
            throw new RuntimeException("TextureCube() needs 6 texture files.");

        for (int s = 0; s != 6; s++) {
            Log.info(this.getClass(), "Load image: " + imageFiles[s].getPath());
            Raster raster = read(imageFiles[s]);

            if (imageData == null) {
                // read image properties
                width = raster.getWidth();
                height = raster.getHeight();
                channels = raster.getNumDataElements();

                // allocate memory for image data
                imageData = new byte[6][4 * width * height];

                isSemiTransparent = false;
            } else if (width != raster.getWidth() || height != raster.getHeight() || channels != raster.getNumDataElements())
                throw new RuntimeException("TextureCube(): All textures need to be the same size.");

            // copy image data
            int n = 0;
            int[] pixel = new int[] { 0, 0, 0, 255 }; // RGBA
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) {
                    raster.getPixel(x, y, pixel);

                    if (channels > 3)
                        pixel[3] = raster.getSample(x, y, 3);

                    if (pixel[3] > 0 && pixel[3] < 255)
                        isSemiTransparent = true;

                    for (int i = 0; i < 4; i++)
                        imageData[s][n++] = (byte) pixel[i];
                }
        }
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

        gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, id.get(ctx));

        if (format != GL2ES2.GL_DEPTH_COMPONENT && mipmapsDirty.get(ctx)) {
            gl.glGenerateMipmap(GL.GL_TEXTURE_CUBE_MAP);
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
            gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, id.get(ctx));
            gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S, GL.GL_MIRRORED_REPEAT);
            gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T, GL.GL_MIRRORED_REPEAT);
            gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);

            for (int i = 0; i != 6; i++)
                gl.glTexImage2D(target[i], 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(imageData[i]));
        } else {
            gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, id.get(ctx));
            gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S, GL.GL_MIRRORED_REPEAT);
            gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T, GL.GL_MIRRORED_REPEAT);
            gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

            if (format == GL2ES2.GL_DEPTH_COMPONENT) {
                gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                for (int i = 0; i != 6; i++)
                    gl.glTexImage2D(target[i], 0, format, width, height, 0, GL2ES2.GL_DEPTH_COMPONENT, GL.GL_FLOAT, null);
            } else {
                gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
                for (int i = 0; i != 6; i++)
                    gl.glTexImage2D(target[i], 0, format, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
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
    public byte[][] getImageData() {
        return imageData;
    }

    @Override
    public UniformValue getUniformValue(int unit) {
        return new UniformSamplerCube(unit);
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
        gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, 0);
        gl.glDisable(GL.GL_TEXTURE_CUBE_MAP);
    }
}
