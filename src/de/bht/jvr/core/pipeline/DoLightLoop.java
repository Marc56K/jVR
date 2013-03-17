package de.bht.jvr.core.pipeline;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.rendering.DrawListCreator;
import de.bht.jvr.core.rendering.LightElement;
import de.bht.jvr.core.rendering.LightList;

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

public class DoLightLoop implements PipelineCommand {
    private Pipeline lightPipeline;
    private boolean noShadowLights;
    private boolean shadowLights;

    public DoLightLoop(Pipeline lightPipeline, boolean noShadowLights, boolean shadowLights) {
        this.lightPipeline = lightPipeline;
        this.noShadowLights = noShadowLights;
        this.shadowLights = shadowLights;
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        LightList lightList = pipelineState.getActiveLightList();

        for (int i = 0; i < lightList.getNumLights(); i++) {
            LightElement light = lightList.getLightElement(i);
            if (shadowLights && light.isCastingShadow() || noShadowLights && !light.isCastingShadow()) {
                pipelineState.setActiveLightElement(light);
                lightPipeline.render(ctx);
            }
        }

        pipelineState.setActiveLightElement(null);
    }

    @Override
    public PipelineCommand getRenderClone() {
        return new DoLightLoop(lightPipeline.getRenderClone(), noShadowLights, shadowLights);
    }

    @Override
    public void reset() {
        lightPipeline.resetPipeline();
    }

    @Override
    public void update(PipelineState pipelineState) {
        // create the light list
        LightList lightList = pipelineState.getActiveLightList();
        if (lightList == null) {
            lightList = new LightList();
            DrawListCreator.updateDrawList(pipelineState.getRoot(), lightList, pipelineState.getPrescanInfo());
            pipelineState.setActiveLightList(lightList);
        }

        lightPipeline.getPipelineState().setParentState(pipelineState);
        lightPipeline.update();
    }

    @Override
    public void prescan(PipelineState pipelineState) {
        lightPipeline.getPipelineState().setParentState(pipelineState);
        lightPipeline.prescan();        
    }
}
