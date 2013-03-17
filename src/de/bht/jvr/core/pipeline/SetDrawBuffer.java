package de.bht.jvr.core.pipeline;

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

public class SetDrawBuffer implements PipelineCommand {
    private int drawBuffer;

    public SetDrawBuffer(int drawBuffer) {
        this.drawBuffer = drawBuffer;
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        if (pipelineState.getActiveFbo() != null) {
            throw new Exception("Tried to set the DrawBuffer with a non window system provided frame buffer.");
        }
        pipelineState.setDrawBuffer(drawBuffer);
    }

    @Override
    public PipelineCommand getRenderClone() {
        return this;
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
