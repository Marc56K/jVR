package de.bht.jvr.core.pipeline;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.FrameBuffer;

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

public class CreateFrameBufferObject implements PipelineCommand {
    private FrameBuffer fbo;
    private String name;

    public CreateFrameBufferObject(String name, boolean depthBuf, int numColBufs, int format, float scale, int maxSamples, boolean stencilBuf) {
        this.name = name;
        fbo = new FrameBuffer(depthBuf, stencilBuf, numColBufs, format, scale, maxSamples);
    }

    public CreateFrameBufferObject(String name, boolean depthBuf, int numColBufs, int format, int width, int height, int maxSamples, boolean stencilBuf) {
        this.name = name;
        fbo = new FrameBuffer(depthBuf, stencilBuf, numColBufs, format, width, height, maxSamples);
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        pipelineState.setFBO(name, fbo);
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
