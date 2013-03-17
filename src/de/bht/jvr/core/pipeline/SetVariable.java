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

public class SetVariable implements PipelineCommand {

    private String dstVarName = null;
    private String srcVarName = null;
    private Number value = null;
    
    public SetVariable(String dstVarName, String srcVarName) {
        this.dstVarName = dstVarName;
        this.srcVarName = srcVarName;
    }
    
    public SetVariable(String dstVarName, Number value) {
        this.dstVarName = dstVarName;
        this.value = value;
    }
    
    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        Variable dstVar = pipelineState.getVariable(dstVarName);           
        
        if (srcVarName == null)
        {
            dstVar.setValue(ctx, value);
        }
        else
        {
            Variable srcVar = pipelineState.getVariable(srcVarName);            
            dstVar.setValue(ctx, srcVar.getValue(ctx));
        }
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
