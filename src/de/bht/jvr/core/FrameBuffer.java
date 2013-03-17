package de.bht.jvr.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.logger.Log;
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
 * The frame buffer object is used for offscreen rendering.
 * 
 * @author Marc Roßbach
 */
public class FrameBuffer {
    private ContextValueMap<Boolean> built = new ContextValueMap<Boolean>(false);

    /** FBO with RBOs (multi sampling supported). */
    private ContextValueMap<Integer> fboMultId = new ContextValueMap<Integer>(-1);

    /** Depth-RBO for FBO-Mult. */
    private ContextValueMap<Integer> depthBufMultId = new ContextValueMap<Integer>();

    /** Stencil-RBO for FBO-Mult. */
    private ContextValueMap<Integer> stencilBufMultId = new ContextValueMap<Integer>();

    /** Color-RBO for FBO-Mult. */
    private ContextValueMap<Integer[]> colBufIds = new ContextValueMap<Integer[]>();

    /** FBO with Textures (multi sampling not supported). */
    private ContextValueMap<Integer> fboTexId = new ContextValueMap<Integer>(-1);

    /** Depth-RBO for FBO-Tex. */
    private ContextValueMap<Integer> depthBufTexId = new ContextValueMap<Integer>();

    /** Stencil-RBO for FBO-Tex. */
    private ContextValueMap<Integer> stencilBufTexId = new ContextValueMap<Integer>();

    /** Textures for FBO-Tex. */
    private ContextValueMap<Map<Integer, Texture2D>> textures = new ContextValueMap<Map<Integer, Texture2D>>();

    /** The old viewport. */
    private ContextValueMap<Vector4> oldViewport = new ContextValueMap<Vector4>();

    /** The old draw buffer. */
    private ContextValueMap<Integer> oldDrawBuffer = new ContextValueMap<Integer>();

    /** The width. */
    private ContextValueMap<Integer> width = new ContextValueMap<Integer>();

    /** The height. */
    private ContextValueMap<Integer> height = new ContextValueMap<Integer>();

    /** Render the depth buffer to texture. */
    private boolean depthBuf;

    /** Use a stencil buffer */
    private boolean stencilBuf;

    /** The number of color buffer. */
    private int numColBufs;

    /** The texture format. */
    private int format;

    /** The width of the FBO. */
    private int staticWidth;

    /** The height of the FBO. */
    private int staticHeight;

    /** The dimensions of the FBO depending on window size. */
    private float scale;

    /** The samples. */
    private int samples;

    /**
     * Instantiates a new frame buffer.
     * 
     * @param depthBuf
     *            render depth buffer to texture
     * @param stencilBuf
     *            add a stencil buffer
     * @param numColBufs
     *            the number of color buffer
     * @param format
     *            the format
     * @param scale
     *            the scale
     * @param samples
     *            the samples
     */
    public FrameBuffer(boolean depthBuf, boolean stencilBuf, int numColBufs, int format, float scale, int samples) {
        this(depthBuf, stencilBuf, numColBufs, format, 0, 0, samples);
        this.scale = scale;
    }

    /**
     * Instantiates a new frame buffer.
     * 
     * @param depthBuf
     *            render depth buffer to texture
     * @param stencilBuf
     *            add a stencil buffer
     * @param numColBufs
     *            the number of color buffer
     * @param format
     *            the format
     * @param width
     *            the width
     * @param height
     *            the height
     * @param samples
     *            the samples
     */
    public FrameBuffer(boolean depthBuf, boolean stencilBuf, int numColBufs, int format, int width, int height, int samples) {
        this.depthBuf = depthBuf;
        this.stencilBuf = stencilBuf;
        this.numColBufs = numColBufs;
        this.format = format;
        staticWidth = width;
        staticHeight = height;
        scale = 1;
        this.samples = samples;
    }

    /**
     * Attach a texture to the attachment point of the fbo.
     * 
     * @param ctx
     *            the context
     * @param attachmentPoint
     *            GL_COLOR_ATTACHMENT0..., GL_DEPTH_ATTACHMENT
     * @param tex
     *            the texture
     * @throws Exception
     *             the exception
     */
    private void attachTexture2D(Context ctx, int attachmentPoint, Texture2D tex) throws Exception {
        if (tex.getWidth() != getWidth(ctx) || tex.getHeight() != getHeight(ctx))
            throw new Exception("invalid texture size (" + tex.getWidth() + "x" + tex.getHeight() + ") should be (" + getWidth(ctx) + "x" + getHeight(ctx) + ")");
        else
            textures.get(ctx).put(attachmentPoint, tex);
    }

    /**
     * Bind the frame buffer object.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public void bind(Context ctx) throws Exception {
        GL2GL3 gl = ctx.getGL();

        if (!built.get(ctx))
            build(ctx);

        if (samples > 0)
            // bind FBOMult
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fboMultId.get(ctx));
        else
            // bind FBOTex
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fboTexId.get(ctx));

        // save old viewport
        int[] vp = getViewportDim(ctx);
        oldViewport.put(ctx, new Vector4(vp[0], vp[1], vp[2], vp[3]));

        // save old draw buffer
        int[] dBuf = new int[1];
        gl.glGetIntegerv(GL2GL3.GL_DRAW_BUFFER, dBuf, 0);
        oldDrawBuffer.put(ctx, new Integer(dBuf[0]));

        // set new viewport
        gl.glViewport(0, 0, getWidth(ctx), getHeight(ctx));

        // set new draw buffers
        setDrawBuffers(ctx);
    }

    /**
     * Builds the gl objects.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public void build(Context ctx) throws Exception {
        Log.info(this.getClass(), "Building framebuffer object: [" + getWidth(ctx) + "x" + getHeight(ctx) + "] DepthBuffer: " + depthBuf + " ColorBuffer: " + numColBufs + " Samples: " + samples);

        GL2GL3 gl = ctx.getGL();

        // create textures
        createColorAndDepthTextures(ctx);

        // create FBOMult
        // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (samples > 0) {
            int[] fboMultId = new int[1];
            gl.glGenFramebuffers(1, fboMultId, 0);
            gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fboMultId[0]);
            this.fboMultId.put(ctx, new Integer(fboMultId[0]));

            if (stencilBuf) {
                // create multi sampled depth stencil RBO
                int[] stencilBufMultId = new int[1];
                gl.glGenRenderbuffers(1, stencilBufMultId, 0);
                gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, stencilBufMultId[0]);
                gl.glRenderbufferStorageMultisample(GL.GL_RENDERBUFFER, samples, GL.GL_DEPTH_STENCIL, getWidth(ctx), getHeight(ctx));
                this.stencilBufMultId.put(ctx, new Integer(stencilBufMultId[0]));

                // attach depth stencil buffer to FBO
                gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL2GL3.GL_DEPTH_STENCIL_ATTACHMENT, GL.GL_RENDERBUFFER, stencilBufMultId[0]);
            } else {
                // create multi sampled depth RBO
                int[] depthBufMultId = new int[1];
                gl.glGenRenderbuffers(1, depthBufMultId, 0);
                gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, depthBufMultId[0]);
                gl.glRenderbufferStorageMultisample(GL.GL_RENDERBUFFER, samples, GL.GL_DEPTH_COMPONENT32, getWidth(ctx), getHeight(ctx));
                this.depthBufMultId.put(ctx, new Integer(depthBufMultId[0]));

                // attach depth buffer to FBO
                gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER, depthBufMultId[0]);
            }

            // create multi sampled color RBOs
            Integer[] colBufIds = new Integer[numColBufs];
            this.colBufIds.put(ctx, colBufIds);
            for (int i = 0; i < numColBufs; i++) {
                int[] colBufId = new int[1];
                gl.glGenRenderbuffers(1, colBufId, 0);
                gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, colBufId[0]);
                gl.glRenderbufferStorageMultisample(GL.GL_RENDERBUFFER, samples, format, getWidth(ctx), getHeight(ctx));
                colBufIds[i] = new Integer(colBufId[0]);

                // attach color buffer to FBO
                gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0 + i, GL.GL_RENDERBUFFER, colBufId[0]);
            }

            if (numColBufs == 0) {
                gl.glDrawBuffer(GL.GL_FALSE);
                gl.glReadBuffer(GL.GL_FALSE);
            }

            // check FBO for error
            checkFBO(ctx);
        }
        // create FBOTex
        // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        int[] fboTexId = new int[1];
        gl.glGenFramebuffers(1, fboTexId, 0);
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fboTexId[0]);
        this.fboTexId.put(ctx, new Integer(fboTexId[0]));

        if (samples == 0)
            if (stencilBuf) {
                // create depth stencil RBO
                int[] stencilBufTexId = new int[1];
                gl.glGenRenderbuffers(1, stencilBufTexId, 0);
                gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, stencilBufTexId[0]);
                gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL.GL_DEPTH_STENCIL, getWidth(ctx), getHeight(ctx));
                this.stencilBufTexId.put(ctx, new Integer(stencilBufTexId[0]));

                // attach depth stencil buffer to FBO
                gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL2GL3.GL_DEPTH_STENCIL_ATTACHMENT, GL.GL_RENDERBUFFER, stencilBufTexId[0]);
            } else if (!depthBuf) {
                // create depth RBO
                int[] depthBufTexId = new int[1];
                gl.glGenRenderbuffers(1, depthBufTexId, 0);
                gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, depthBufTexId[0]);
                gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL.GL_DEPTH_COMPONENT32, getWidth(ctx), getHeight(ctx));
                this.depthBufTexId.put(ctx, new Integer(depthBufTexId[0]));

                // attach depth buffer to FBO
                gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, GL.GL_DEPTH_ATTACHMENT, GL.GL_RENDERBUFFER, depthBufTexId[0]);
            }

        // attach textures to FBO
        for (Entry<Integer, Texture2D> texEntry : textures.get(ctx).entrySet()) {
            int attachmentPoint = texEntry.getKey();
            Texture2D tex = texEntry.getValue();
            tex.bind(ctx);

            gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, attachmentPoint, GL.GL_TEXTURE_2D, tex.getId(ctx), 0);
        }

        if (numColBufs == 0) {
            gl.glDrawBuffer(GL.GL_FALSE);
            gl.glReadBuffer(GL.GL_FALSE);
        }

        // check FBO for error
        checkFBO(ctx);

        // FBO is built
        built.put(ctx, new Boolean(true));
    }

    /**
     * Check the frame buffer object.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public void checkFBO(Context ctx) throws Exception {
        GL2GL3 gl = ctx.getGL();

        int error = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
        switch (error) {
        case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
            throw new Exception("FBO: Incomplete attachment");
        case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
            throw new Exception("FBO: Missing attachment");
        case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
            throw new Exception("FBO: Incomplete dimensions");
        case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
            throw new Exception("FBO: Incomplete formats");
        case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
            throw new Exception("FBO: Incomplete draw buffer");
        case GL2GL3.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
            throw new Exception("FBO: Incomplete read buffer");
        case GL.GL_FRAMEBUFFER_UNSUPPORTED:
            throw new Exception("FBO: Framebufferobjects unsupported");
        }
    }

    /**
     * Creates the color and depth textures.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    private void createColorAndDepthTextures(Context ctx) throws Exception {
        textures.put(ctx, new HashMap<Integer, Texture2D>());

        if (depthBuf) {
            Texture2D tex = new Texture2D(getWidth(ctx), getHeight(ctx), GL2ES2.GL_DEPTH_COMPONENT, samples);
            attachTexture2D(ctx, GL.GL_DEPTH_ATTACHMENT, tex);
        }

        for (int i = 0; i < numColBufs; i++) {
            Texture2D tex = new Texture2D(getWidth(ctx), getHeight(ctx), format, samples);
            attachTexture2D(ctx, GL.GL_COLOR_ATTACHMENT0 + i, tex);
        }
    }

    /**
     * Delete the frame buffer object and render buffer objects.
     * 
     * @param ctx
     *            the context
     */
    public void delete(Context ctx) {
        if (built.get(ctx)) {
            ctx.deleteFbo(fboMultId.get(ctx));
            ctx.deleteFbo(fboTexId.get(ctx));

            // delete depth RBOs
            Integer depthBufMultId = this.depthBufMultId.get(ctx);
            if (depthBufMultId != null)
                ctx.deleteRbo(depthBufMultId.intValue());
            Integer depthBufTexId = this.depthBufTexId.get(ctx);
            if (depthBufTexId != null)
                ctx.deleteRbo(depthBufTexId.intValue());

            Integer stencilBufMultId = this.stencilBufMultId.get(ctx);
            if (stencilBufMultId != null)
                ctx.deleteRbo(stencilBufMultId.intValue());

            Integer stencilBufTexId = this.stencilBufTexId.get(ctx);
            if (stencilBufTexId != null)
                ctx.deleteRbo(stencilBufTexId.intValue());

            // delete color RBOs
            Integer[] colBufIds = this.colBufIds.get(ctx);
            if (numColBufs > 0 && colBufIds != null)
                for (Integer colBufId : colBufIds)
                    ctx.deleteRbo(colBufId.intValue());
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() {
        List<Context> ctxList = built.getContextList();
        for (Context ctx : ctxList)
            delete(ctx);
    }

    /**
     * Gets the height.
     * 
     * @param ctx
     *            the context
     * @return the height
     */
    public int getHeight(Context ctx) {
        if (staticHeight == 0) {
            Integer height = this.height.get(ctx);
            if (height == null) {
                int[] vp = getViewportDim(ctx);
                height = new Integer((int) (scale * (vp[3] - vp[1])));
                this.height.put(ctx, height);
            }
            return height.intValue();
        } else
            return staticHeight;
    }

    /**
     * Gets a texture from the attachment point of the fbo.
     * 
     * @param ctx
     *            the context
     * @param attachmentPoint
     *            GL_COLOR_ATTACHMENT0..., GL_DEPTH_ATTACHMENT
     * @return the texture
     */
    public Texture2D getTexture2D(Context ctx, int attachmentPoint) {
        for (Entry<Integer, Texture2D> tex : textures.get(ctx).entrySet())
            if (tex.getKey().intValue() == attachmentPoint)
                return tex.getValue();

        return null;
    }

    /**
     * Gets the viewport dim.
     * 
     * @param ctx
     *            the context
     * @return the viewport dim
     */
    public int[] getViewportDim(Context ctx) {
        int[] vp = new int[4];
        ctx.getGL().glGetIntegerv(GL.GL_VIEWPORT, vp, 0);

        return vp;
    }

    /**
     * Gets the width.
     * 
     * @param ctx
     *            the context
     * @return the width
     */
    public int getWidth(Context ctx) {
        if (staticWidth == 0) {
            Integer width = this.width.get(ctx);
            if (width == null) {
                int[] vp = getViewportDim(ctx);
                width = new Integer((int) (scale * (vp[2] - vp[0])));
                this.width.put(ctx, width);
            }
            return width.intValue();
        } else
            return staticWidth;
    }

    /**
     * set all color attachments (glDrawBuffers).
     * 
     * @param ctx
     *            the new draw buffers
     */
    public void setDrawBuffers(Context ctx) {
        if (numColBufs > 0) {
            int[] dBufs = new int[numColBufs];

            for (int i = 0; i < numColBufs; i++)
                dBufs[i] = GL.GL_COLOR_ATTACHMENT0 + i;
            ctx.getGL().glDrawBuffers(dBufs.length, dBufs, 0);
        } else
            ctx.getGL().glDrawBuffer(GL.GL_NONE);
    }

    /**
     * Unbind the frame buffer object.
     * 
     * @param ctx
     *            the ctx
     */
    public void unbind(Context ctx) {
        GL2GL3 gl = ctx.getGL();

        if (built.get(ctx) && samples > 0) // copy data from FBOMult to FBOTex
                                           // to get multi sampled textures
                                           // TODO render direct to multi
                                           // sampled textures
        {
            for (int i = 0; i < numColBufs; i++) {
                gl.glBindFramebuffer(GL2GL3.GL_READ_FRAMEBUFFER, fboMultId.get(ctx));
                gl.glReadBuffer(GL.GL_COLOR_ATTACHMENT0 + i);
                gl.glBindFramebuffer(GL2GL3.GL_DRAW_FRAMEBUFFER, fboTexId.get(ctx));
                gl.glDrawBuffer(GL.GL_COLOR_ATTACHMENT0 + i);
                gl.glBlitFramebuffer(0, 0, getWidth(ctx), getHeight(ctx), 0, 0, getWidth(ctx), getHeight(ctx), GL.GL_COLOR_BUFFER_BIT, GL.GL_NEAREST);
            }

            if (depthBuf)
                gl.glBlitFramebuffer(0, 0, getWidth(ctx), getHeight(ctx), 0, 0, getWidth(ctx), getHeight(ctx), GL.GL_DEPTH_BUFFER_BIT, GL.GL_NEAREST);
        }

        // mark mipmaps of textures as invalid
        Map<Integer, Texture2D> texMap = textures.get(ctx);
        if (texMap != null) {
            for (Texture2D tex : textures.get(ctx).values())
                tex.InvalidateMipmaps(ctx);
        }

        // restore old viewport
        Vector4 vp = oldViewport.get(ctx);
        if (vp != null)
            gl.glViewport((int) vp.x(), (int) vp.y(), (int) vp.z(), (int) vp.w());

        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);

        // restore old draw buffer
        Integer dBuf = oldDrawBuffer.get(ctx);
        if (dBuf != null)
            gl.glDrawBuffer(dBuf.intValue());
    }
}
