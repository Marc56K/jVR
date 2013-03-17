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

public class IfEqual implements PipelineCommand {

    private Pipeline thanPipeline = null;
    private String firstVarName = null;
    private String secondVarName = null;
    private Number value = null;
    
    public IfEqual(Pipeline thanPipeline, String firstVarName, String secondVarName) {
        this.thanPipeline = thanPipeline;
        this.firstVarName = firstVarName;
        this.secondVarName = secondVarName;
    }
    
    public IfEqual(Pipeline thanPipeline, String firstVarName, Number value) {
        this.thanPipeline = thanPipeline;
        this.firstVarName = firstVarName;
        this.value = value;        
    }
    
    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        Variable firstVar = pipelineState.getVariable(firstVarName);           
        
        if (secondVarName == null)
        {
            if (firstVar.getValue(ctx).doubleValue() == value.doubleValue())
            {
                thanPipeline.render(ctx);
            }
        }
        else
        {
            Variable secondVar = pipelineState.getVariable(secondVarName);            
            if (firstVar.getValue(ctx).doubleValue() == secondVar.getValue(ctx).doubleValue())
            {
                thanPipeline.render(ctx);
            }           
        }
    }

    @Override
    public PipelineCommand getRenderClone() {
        if (secondVarName != null)
            return new IfEqual(thanPipeline.getRenderClone(), firstVarName, secondVarName);
        else
            return new IfEqual(thanPipeline.getRenderClone(), firstVarName, value);
    }

    @Override
    public void reset() {
        thanPipeline.resetPipeline();
    }

    @Override
    public void update(PipelineState pipelineState) {
        thanPipeline.getPipelineState().setParentState(pipelineState);
        thanPipeline.update();
    }

    @Override
    public void prescan(PipelineState pipelineState) {
        thanPipeline.getPipelineState().setParentState(pipelineState);
        thanPipeline.prescan();
    }
}
