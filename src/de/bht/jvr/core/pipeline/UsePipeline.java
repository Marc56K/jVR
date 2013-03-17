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
 *
 *
 * @author Henrik Tramberend
 */
public class UsePipeline implements PipelineCommand {
    private Pipeline pipeline;
    private boolean scoped;

    public UsePipeline(Pipeline pipeline, boolean scoped) {
        this.pipeline = pipeline;
        this.scoped = scoped;
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        pipeline.render(ctx);
    }

    @Override
    public PipelineCommand getRenderClone() {
        return new UsePipeline(pipeline.getRenderClone(), this.scoped);
    }

    @Override
    public void reset() {
        pipeline.resetPipeline();
    }

    @Override
    public void update(PipelineState pipelineState) {
        if (this.scoped)
            pipeline.getPipelineState().setParentState(pipelineState);
        else
            pipeline.setPipelineState(pipelineState);
        pipeline.update();
    }

    @Override
    public void prescan(PipelineState pipelineState) {
        if (this.scoped)
            pipeline.getPipelineState().setParentState(pipelineState);
        else
            pipeline.setPipelineState(pipelineState);
        pipeline.prescan();
    }
}
