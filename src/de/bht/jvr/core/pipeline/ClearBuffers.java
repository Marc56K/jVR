package de.bht.jvr.core.pipeline;

import de.bht.jvr.util.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.Context;

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

public class ClearBuffers implements PipelineCommand {
    private boolean depthBuf;
    private boolean[] colBuf;
    private Color clearColor;
    private boolean stencilBuf;
    private int stencilClearValue;

    public ClearBuffers(boolean depthBuf, boolean[] colBuf, Color clearColor, boolean stencilBuf, int stencilClearValue) {
        this.depthBuf = depthBuf;
        this.colBuf = colBuf.clone();
        this.clearColor = clearColor;
        this.stencilBuf = stencilBuf;
        this.stencilClearValue = stencilClearValue;
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        GL2GL3 gl = ctx.getGL();
        
        // set render target
        ctx.bindFbo(pipelineState.getActiveFbo());

        if (clearColor != null)
            gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);

        if (pipelineState.getActiveFbo() == null) {
            gl.glDrawBuffer(pipelineState.getDrawBuffer());
        }

        if (pipelineState.getActiveFbo() == null) {
            int clear = 0;
            if (depthBuf)
                clear |= GL.GL_DEPTH_BUFFER_BIT;
            if (colBuf != null && colBuf.length > 0 && colBuf[0])
                clear |= GL.GL_COLOR_BUFFER_BIT;
            if (stencilBuf) {
                clear |= GL.GL_STENCIL_BUFFER_BIT;
                gl.glClearStencil(stencilClearValue);
            }

            gl.glClear(clear);
        } else // clear FBO
        {
            int clear = 0;
            if (depthBuf)
                clear |= GL.GL_DEPTH_BUFFER_BIT;

            if (stencilBuf) {
                clear |= GL.GL_STENCIL_BUFFER_BIT;
                gl.glClearStencil(stencilClearValue);
            }

            gl.glClear(clear);

            if (colBuf != null)
                for (int i = 0; i < colBuf.length; i++)
                    if (colBuf[i]) {
                        gl.glDrawBuffer(GL.GL_COLOR_ATTACHMENT0 + i);
                        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
                    }

            // gl.glDrawBuffers(oldDrawBuf[0]);
            pipelineState.getActiveFbo().setDrawBuffers(ctx);
        }
    }

    @Override
    public PipelineCommand getRenderClone() {
        return this; // immutable class
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(PipelineState pipelineState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void prescan(PipelineState pipelineState) {
        // TODO Auto-generated method stub
        
    }
}
