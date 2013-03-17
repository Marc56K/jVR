package de.bht.jvr.core.pipeline;

import javax.media.opengl.GL;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.FrameBuffer;
import de.bht.jvr.core.Texture2D;
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
 */

public class BindDepthBuffer implements PipelineCommand {
    private String uniformSampler;
    private String fboName;

    public BindDepthBuffer(String uniformSampler, String fboName) {
        this.uniformSampler = uniformSampler;
        this.fboName = fboName;
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        FrameBuffer fbo = pipelineState.getFBO(fboName);
        if (fbo != null) {
            Texture2D tex = fbo.getTexture2D(ctx, GL.GL_DEPTH_ATTACHMENT);
            if (tex != null)
                pipelineState.setTexture2D(uniformSampler, tex);
            else
                Log.error(this.getClass(), "No depth buffer in FBO " + fboName);
        } else
            Log.error(this.getClass(), "No FBO named: " + fboName);
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
