package de.bht.jvr.core.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.Material;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.PipelineState;
import de.bht.jvr.core.pipeline.PrescanInfo;

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
 * The geo list contains all geo element groups for the next frame.
 * 
 * @author Marc Roßbach
 */
public class GeoList extends DrawList {
    /** The geometries grouped by shader context, shader program and material. */
    private Map<String, Map<ShaderProgram, Map<Material, GeoElementGroup>>> geoElementGroupsMapByShaderCtxAndProgram = new HashMap<String, Map<ShaderProgram, Map<Material, GeoElementGroup>>>();

    /** The ungrouped geometries for sorted rendering. */
    private List<GeoElementGroup> geoElementGroupsList = new ArrayList<GeoElementGroup>();

    /** Sorting finished. */
    private boolean sortingFinished = false;

    private Frustum frustum = null;

    private boolean traversalCulling = false;

    private GroupNode inactiveUntilLeave = null;

    public GeoList(Frustum frustum, boolean traversalCulling) {
        this.frustum = frustum;
        this.traversalCulling = traversalCulling;
    }

    @Override
    public boolean accept(GroupNode node) {
        return inactiveUntilLeave == null;
    }

    @Override
    public boolean accept(ShapeNode node) {
        return inactiveUntilLeave == null;
    }

    @Override
    public void add(ShapeNode node, Transform worldTrans, PrescanInfo prescanInfo) {
        if (inactiveUntilLeave == null) {
            if (frustum != null && traversalCulling)
                if (!frustum.isInViewFrustum(node.getBBox(), worldTrans))
                    return;

            addShapeNode(node, worldTrans, prescanInfo);
        }
    }

    /**
     * Adds a shape node.
     * 
     * @param node
     *            the node
     * @param transform
     *            the transformion
     */
    private void addShapeNode(ShapeNode node, Transform transform, PrescanInfo prescanInfo) {
        Material mat = node.getMaterial();
        Geometry geo = node.getGeometry();
        Map<Float, Geometry> lodGeos = node.getAllLODGeometries();

        if (mat != null && geo != null) {
            // clone the material
            Material matClone = mat.getRenderClone();
            // create the geo element
            GeoElement geoEle = new GeoElement(geo, transform);
            for (Entry<Float, Geometry> entry : lodGeos.entrySet())
                geoEle.addLODGeometry(entry.getKey(), entry.getValue()); // append
                                                                         // level
                                                                         // of
                                                                         // detail
                                                                         // geometries

            // group by by shader context, shader program and material (for
            // unsorted rendering)
            for (Entry<String, ShaderProgram> entry : mat.getShaderPrograms().entrySet()) {
                // shader context -> shader program
                String shaderCtx = entry.getKey();
                Map<ShaderProgram, Map<Material, GeoElementGroup>> geoElementGroupsMapByProgram = geoElementGroupsMapByShaderCtxAndProgram.get(shaderCtx);
                if (geoElementGroupsMapByProgram == null) {
                    geoElementGroupsMapByProgram = new HashMap<ShaderProgram, Map<Material, GeoElementGroup>>();
                    geoElementGroupsMapByShaderCtxAndProgram.put(shaderCtx, geoElementGroupsMapByProgram);
                }

                // shader program -> material
                ShaderProgram program = entry.getValue();
                Map<Material, GeoElementGroup> matGroup = geoElementGroupsMapByProgram.get(program);
                if (matGroup == null) {
                    matGroup = new HashMap<Material, GeoElementGroup>();
                    geoElementGroupsMapByProgram.put(program, matGroup);
                }

                // material -> geo group
                GeoElementGroup group0 = matGroup.get(mat);
                if (group0 == null) {
                    group0 = new GeoElementGroup(matClone);
                    matGroup.put(mat, group0);
                }

                group0.addGeoElement(geoEle);

            }
            
            if (prescanInfo == null || prescanInfo.isSortedMaterialClass(matClone.getMaterialClass())) {
                // no group (for sorted rendering)
                GeoElementGroup group1 = new GeoElementGroup(matClone);
                geoElementGroupsList.add(group1);
                group1.setGeoElement(geoEle);
            }
            sortingFinished = false;
        }
    }

    @Override
    public boolean enter(GroupNode node, Transform worldTrans) {
        if (frustum != null && inactiveUntilLeave == null && traversalCulling) {
            if (frustum.isInViewFrustum(node.getBBox(), worldTrans))
                return true;

            inactiveUntilLeave = node;
            return false;
        }
        return inactiveUntilLeave == null;
    }

    @Override
    public void leave(GroupNode node) {
        if (inactiveUntilLeave == node)
            inactiveUntilLeave = null;
    }

    /**
     * Render the geo list.
     * 
     * @param ctx
     *            the context
     * @param shaderContext
     *            the shader context
     * @param materialClass
     *            the material class
     * @param pipelineState
     *            the pipeline state
     * @param orderBackToFront
     *            the order back to front
     * @throws Exception
     *             the exception
     */
    public void render(Context ctx, String shaderContext, Pattern materialClass, PipelineState pipelineState, boolean orderBackToFront) throws Exception {
        Frustum viewFrustum = null;
        if (pipelineState.getViewFrustumCullingMode() == 1)
            viewFrustum = new Frustum(pipelineState.getActiveProjMatrix(), null);

        if (orderBackToFront)
            renderSorted(ctx, shaderContext, materialClass, pipelineState, viewFrustum);
        else
            renderUnsorted(ctx, shaderContext, materialClass, pipelineState, viewFrustum);
    }

    /**
     * Render sorted.
     * 
     * @param ctx
     *            the context
     * @param shaderContext
     *            the shader context
     * @param materialClass
     *            the material class
     * @param pipelineState
     *            the pipeline state
     * @param viewFrustum
     * @throws Exception
     *             the exception
     */
    private void renderSorted(Context ctx, String shaderContext, Pattern materialClass, PipelineState pipelineState, Frustum viewFrustum) throws Exception {
        // set camera transform to groups
        for (GeoElementGroup group : geoElementGroupsList)
            group.setCamTransform(pipelineState.getActiveCamTransform());

        // sort groups
        if (!sortingFinished) {
            Collections.sort(geoElementGroupsList);
            sortingFinished = true;
        }

        // render groups
        for (GeoElementGroup group : geoElementGroupsList)
            if (materialClass.matcher(group.getMaterialClass()).matches())
                group.render(ctx, shaderContext, pipelineState, viewFrustum);
    }

    /**
     * Render unsorted.
     * 
     * @param ctx
     *            the context
     * @param shaderContext
     *            the shader context
     * @param materialClass
     *            the material class
     * @param pipelineState
     *            the pipeline state
     * @param viewFrustum
     * @throws Exception
     *             the exception
     */
    private void renderUnsorted(Context ctx, String shaderContext, Pattern materialClass, PipelineState pipelineState, Frustum viewFrustum) throws Exception {
        Map<ShaderProgram, Map<Material, GeoElementGroup>> geoElementGroupsMapByProgram = geoElementGroupsMapByShaderCtxAndProgram.get(shaderContext);

        if (geoElementGroupsMapByProgram != null)
            for (Map<Material, GeoElementGroup> geoEleGroupsMap : geoElementGroupsMapByProgram.values())
                if (geoEleGroupsMap != null) {
                    for (GeoElementGroup group : geoEleGroupsMap.values())
                        // set camera transform to groups
                        group.setCamTransform(pipelineState.getActiveCamTransform());

                    // render groups
                    for (GeoElementGroup group : geoEleGroupsMap.values())
                        if (materialClass.matcher(group.getMaterialClass()).matches())
                            group.render(ctx, shaderContext, pipelineState, viewFrustum);
                }
    }
}