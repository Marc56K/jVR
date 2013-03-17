package de.bht.jvr.core.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.rendering.ClipPlaneList;
import de.bht.jvr.core.rendering.DrawList;
import de.bht.jvr.core.rendering.DrawListCreator;
import de.bht.jvr.core.rendering.Frustum;
import de.bht.jvr.core.rendering.GeoList;
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

public class SwitchLightCamera implements PipelineCommand {
    private LightNode light = null;
    private LightElement lightElement = null;
    private LightList lightList = null;
    private ClipPlaneList clipPlaneList = null;
    private Map<LightElement, GeoList> geoLists = new HashMap<LightElement, GeoList>();

    public SwitchLightCamera(LightNode light) {
        this.light = light;
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        LightElement lightElement = null;
        if (this.lightElement != null)
            lightElement = this.lightElement;
        else
            lightElement = pipelineState.getActiveLightElement();

        if (lightElement != null) {
            pipelineState.setPolygonOffset(lightElement.getPolygonOffset());
            pipelineState.setActiveLightList(lightList);
            pipelineState.setActiveClipPlaneList(clipPlaneList);
            pipelineState.setActiveGeoList(geoLists.get(lightElement));
            pipelineState.setActiveCamTransform(lightElement.getTransform());
            pipelineState.setActiveProjMatrix(lightElement.getProjectionMatrix());
        }
    }

    @Override
    public PipelineCommand getRenderClone() {
        return new SwitchLightCamera(light);
    }

    @Override
    public void reset() {
        lightElement = null;
        lightList = null;
        clipPlaneList = null;
        geoLists = new HashMap<LightElement, GeoList>();
    }

    @Override
    public void update(PipelineState pipelineState) {
        List<DrawList> drawLists = new ArrayList<DrawList>();

        // create the light list
        if (pipelineState.getActiveLightList() == null) {
            lightList = new LightList();
            pipelineState.setActiveLightList(lightList);
            drawLists.add(lightList);
        } else
            lightList = pipelineState.getActiveLightList();

        // create the clipping plane list
        if (pipelineState.getActiveClipPlaneList() == null) {
            clipPlaneList = new ClipPlaneList();
            pipelineState.setActiveClipPlaneList(clipPlaneList);
            drawLists.add(clipPlaneList);
        } else
            clipPlaneList = pipelineState.getActiveClipPlaneList();

        if (light != null) {
            lightElement = new LightElement(light, light.getWorldTransform(pipelineState.getRoot()));

            // create the geometry list
            Frustum frustum = new Frustum(lightElement.getProjectionMatrix(), lightElement.getTransform());

            if (pipelineState.getViewFrustumCullingMode() == 2 || pipelineState.getActiveGeoList() == null) {
                GeoList geoList = new GeoList(frustum, pipelineState.getViewFrustumCullingMode() == 2);
                geoLists.put(lightElement, geoList);
                pipelineState.setActiveGeoList(geoList);
                drawLists.add(geoList);
            } else
                geoLists.put(lightElement, pipelineState.getActiveGeoList());
        }

        if (light == null && pipelineState.getViewFrustumCullingMode() != 2 && pipelineState.getActiveGeoList() == null) {
            GeoList geoList = new GeoList(null, false);
            pipelineState.setActiveGeoList(geoList);

            drawLists.add(geoList);
        }

        // traverse the scene graph
        DrawListCreator.updateDrawLists(pipelineState.getRoot(), drawLists, pipelineState.getPrescanInfo());

        if (light == null)
            if (pipelineState.getViewFrustumCullingMode() == 2) {
                drawLists = new ArrayList<DrawList>();
                for (int i = 0; i < lightList.getNumLights(); i++) {
                    LightElement lightElement = lightList.getLightElement(i);
                    if (lightElement.getProjectionMatrix() != null) {
                        // create the geometry list
                        Frustum frustum = new Frustum(lightElement.getProjectionMatrix(), lightElement.getTransform());

                        GeoList geoList = new GeoList(frustum, pipelineState.getViewFrustumCullingMode() == 2);
                        geoLists.put(lightElement, geoList);

                        drawLists.add(geoList);
                    } else
                        throw new RuntimeException("Invalid projection matrix.");
                }

                // traverse the scene graph
                DrawListCreator.updateDrawLists(pipelineState.getRoot(), drawLists, pipelineState.getPrescanInfo());
            } else if (pipelineState.getActiveGeoList() != null)
                for (int i = 0; i < lightList.getNumLights(); i++)
                    geoLists.put(lightList.getLightElement(i), pipelineState.getActiveGeoList());
    }

    @Override
    public void prescan(PipelineState pipelineState) {
        // TODO Auto-generated method stub
        
    }
}
