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

public interface PipelineCommand {
    /**
     * Execute the command.
     * 
     * @param ctx
     *            the context
     * @param pipelineState
     *            the pipeline state
     * @throws Exception
     *             the exception
     */
    public void execute(Context ctx, PipelineState pipelineState) throws Exception;

    /**
     * Clones mutable commands for threading.
     * 
     * @return the pipeline command
     */
    public PipelineCommand getRenderClone();

    /**
     * Reset the command to reuse it.
     */
    public void reset();

    /**
     * Update is called before execution.
     * 
     * @param pipelineState
     *            the state of the pipeline
     */
    public void update(PipelineState pipelineState);
    
    /**
     * Prescan is called before update.
     * 
     * @param pipelineState
     *            the state of the pipeline
     */
    public void prescan(PipelineState pipelineState);
}
