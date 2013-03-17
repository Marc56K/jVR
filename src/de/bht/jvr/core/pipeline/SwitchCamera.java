package de.bht.jvr.core.pipeline;

import java.util.ArrayList;
import java.util.List;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Context;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.rendering.ClipPlaneList;
import de.bht.jvr.core.rendering.DrawList;
import de.bht.jvr.core.rendering.DrawListCreator;
import de.bht.jvr.core.rendering.Frustum;
import de.bht.jvr.core.rendering.GeoList;
import de.bht.jvr.core.rendering.LightList;
import de.bht.jvr.math.Matrix4;

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

public class SwitchCamera implements PipelineCommand {
    private CameraNode camera;
    private LightList lightList = null;
    private ClipPlaneList clipPlaneList = null;
    private GeoList geoList = null;
    private Transform camTransform = null;
    private Matrix4 projMatrix = null;

    public SwitchCamera(CameraNode camera) {
        this.camera = camera;
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        pipelineState.setPolygonOffset(0);
        pipelineState.setActiveLightList(lightList);
        pipelineState.setActiveClipPlaneList(clipPlaneList);
        pipelineState.setActiveGeoList(geoList);
        pipelineState.setActiveCamTransform(camTransform);
        pipelineState.setActiveProjMatrix(projMatrix);
    }

    @Override
    public PipelineCommand getRenderClone() {
        return new SwitchCamera(camera);
    }

    @Override
    public void reset() {
        lightList = null;
        clipPlaneList = null;
        geoList = null;
        camTransform = null;
        projMatrix = null;
    }

    @Override
    public void update(PipelineState pipelineState) {
        camTransform = camera.getEyeWorldTransform(pipelineState.getRoot());
        projMatrix = camera.getProjectionMatrix();

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

        // create the geometry list
        Frustum frustum = new Frustum(projMatrix, camTransform);

        if (pipelineState.getViewFrustumCullingMode() == 2 || pipelineState.getActiveGeoList() == null) {
            geoList = new GeoList(frustum, pipelineState.getViewFrustumCullingMode() == 2);
            pipelineState.setActiveGeoList(geoList);
            drawLists.add(geoList);
        } else
            geoList = pipelineState.getActiveGeoList();

        // traverse the scene graph
        // Benchmark.beginStep("Traversal");
        DrawListCreator.updateDrawLists(pipelineState.getRoot(), drawLists, pipelineState.getPrescanInfo());
        // Benchmark.endStep("Traversal");
    }

    @Override
    public void prescan(PipelineState pipelineState) {
        // TODO Auto-generated method stub
        
    }
}
