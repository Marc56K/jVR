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

public class SetBlendFunc implements PipelineCommand {
    private Integer sFactor;
    private Integer dFactor;

    public SetBlendFunc(Integer sFactor, Integer dFactor) {
        this.sFactor = sFactor;
        this.dFactor = dFactor;
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        if (sFactor != null && dFactor != null)
            pipelineState.setBlendFunc(new int[] { sFactor, dFactor });
        else
            pipelineState.setBlendFunc(null);
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
