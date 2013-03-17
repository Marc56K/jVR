package de.bht.jvr.collada14.loader;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bht.jvr.collada14.Accessor;
import de.bht.jvr.collada14.Geometry;
import de.bht.jvr.collada14.InputLocal;
import de.bht.jvr.collada14.InputLocalOffset;
import de.bht.jvr.collada14.Mesh;
import de.bht.jvr.collada14.Polylist;
import de.bht.jvr.collada14.Source;
import de.bht.jvr.collada14.Triangles;
import de.bht.jvr.collada14.Vertices;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.TriangleMesh;
import de.bht.jvr.logger.Log;

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

public class DAEGeometry {
    private String name = "";
    private List<DAETriangles> trianglesList = new ArrayList<DAETriangles>();

    public DAEGeometry(Geometry geo) throws Exception {
        name = geo.getName();
        Mesh mesh = geo.getMesh();
        if (mesh == null) {
            Log.warning(this.getClass(), "Empty geometry.");
            return;
        }

        // load sources of the mesh
        HashMap<String, DAESource> meshSources = new HashMap<String, DAESource>();

        List<Source> sources = mesh.getSources();
        for (Source source : sources) {
            String sourceId = source.getId();
            List<Double> sourceValues = source.getFloatArray().getValues();
            Accessor accessor = source.getTechniqueCommon().getAccessor();

            // collect all sources of the mesh in a hash map
            meshSources.put(sourceId, new DAESource(sourceValues, accessor.getStride()));
        }

        // load vertices of the mesh
        Vertices vertices = mesh.getVertices();
        String verticesId = vertices.getId();
        List<InputLocal> verticesInputs = vertices.getInputs();
        for (InputLocal input : verticesInputs)
            // looking for positions
            if (input.getSemantic().equals("POSITION")) {
                DAESource verticesSource = meshSources.get(input.getSource().replace("#", ""));
                meshSources.put(verticesId, verticesSource);
                break;
            }

        // load polygons of the mesh
        List<Object> polygonsList = mesh.getLinesAndLinestripsAndPolygons();
        for (Object polygons : polygonsList) {
            // load polygons and perform triangulation
            if (polygons instanceof Polylist) {
                Log.info(this.getClass(), "Loading geometry polygons [" + name + "].");
                Polylist p = (Polylist) polygons;
                int pCount = p.getCount().intValue();
                String pMaterial = p.getMaterial();

                List<InputLocalOffset> pInputs = p.getInputs();
                int stepSize = 0;
                for (InputLocalOffset pInput : pInputs)
                    stepSize = Math.max(stepSize, pInput.getOffset().intValue());
                stepSize++;

                List<BigInteger> polySizeList = p.getVcount();
                List<BigInteger> pIndices = p.getP();

                List<BigInteger> newIndices = new ArrayList<BigInteger>();
                int i = 0;
                for (BigInteger polySize : polySizeList) {
                    int vert = 0;
                    for (int j = 0; j < polySize.intValue(); j++) {
                        if (vert == 3) {
                            vert = 0;
                            j--;

                            // repeat first vertex
                            for (int k = 0; k < stepSize; k++)
                                newIndices.add(pIndices.get(i + k));
                            vert++;
                        }

                        for (int k = j * stepSize; k < (j + 1) * stepSize; k++)
                            newIndices.add(pIndices.get(i + k));

                        vert++;
                    }
                    i += polySize.intValue() * stepSize;
                }

                // attribute arrays
                int[] indices = new int[newIndices.size() / stepSize];
                float[] positions = null;
                float[] normals = null;
                float[] texCoords = null;
                float[] tangents = null;
                float[] binormals = null;

                // generate indices
                for (int x = 0; x < indices.length; x++)
                    indices[x] = x;

                // fill attribute arrays
                pCount = newIndices.size() / (3 * stepSize);
                for (InputLocalOffset pInput : pInputs) {
                    String semantic = pInput.getSemantic();
                    int offset = pInput.getOffset().intValue();

                    DAESource source = meshSources.get(pInput.getSource().replace("#", ""));

                    if (semantic.equals("VERTEX"))
                        positions = makeAttribArray(source, newIndices, stepSize, pCount, 3, offset);
                    else if (semantic.equals("NORMAL"))
                        normals = makeAttribArray(source, newIndices, stepSize, pCount, 3, offset);
                    else if (semantic.equals("TEXCOORD")) {
                        texCoords = makeAttribArray(source, newIndices, stepSize, pCount, 2, offset);
                        // flip texture coords
                        for (int j = 1; j < texCoords.length; j += 2)
                            texCoords[j] = 1 - texCoords[j];
                    } else if (semantic.equals("TEXTANGENT"))
                        tangents = makeAttribArray(source, newIndices, stepSize, pCount, 3, offset);
                    else if (semantic.equals("TEXBINORMAL"))
                        binormals = makeAttribArray(source, newIndices, stepSize, pCount, 3, offset);
                }

                trianglesList.add(new DAETriangles(indices, positions, normals, texCoords, tangents, binormals, pMaterial));
            }

            // load triangles
            if (polygons instanceof Triangles) {
                Log.info(this.getClass(), "Loading geometry triangles [" + name + "].");

                Triangles t = (Triangles) polygons;
                int tCount = t.getCount().intValue();
                String tMaterial = t.getMaterial();
                List<BigInteger> tIndices = t.getP();

                List<InputLocalOffset> tInputs = t.getInputs();

                // System.out.println("tInputCount: "+tInputs.size()+" tCount: "+tCount+" tIndicesSize: "+tIndices.size());

                // attribute arrays
                int[] indices = new int[tCount * 3];
                float[] positions = null;
                float[] normals = null;
                float[] texCoords = null;
                float[] tangents = null;
                float[] binormals = null;

                // generate indices
                for (int i = 0; i < indices.length; i++)
                    indices[i] = i;

                // fill attribute arrays
                if (tCount > 0) {
                    int stepSize = tIndices.size() / (tCount * 3);
                    for (InputLocalOffset tInput : tInputs) {
                        String semantic = tInput.getSemantic();
                        int offset = tInput.getOffset().intValue();

                        DAESource source = meshSources.get(tInput.getSource().replace("#", ""));

                        if (semantic.equals("VERTEX"))
                            positions = makeAttribArray(source, tIndices, stepSize, tCount, 3, offset);
                        else if (semantic.equals("NORMAL"))
                            normals = makeAttribArray(source, tIndices, stepSize, tCount, 3, offset);
                        else if (semantic.equals("TEXCOORD")) {
                            texCoords = makeAttribArray(source, tIndices, stepSize, tCount, 2, offset);
                            // flip texture coords
                            for (int j = 1; j < texCoords.length; j += 2)
                                texCoords[j] = 1 - texCoords[j];
                        } else if (semantic.equals("TEXTANGENT"))
                            tangents = makeAttribArray(source, tIndices, stepSize, tCount, 3, offset);
                        else if (semantic.equals("TEXBINORMAL"))
                            binormals = makeAttribArray(source, tIndices, stepSize, tCount, 3, offset);
                    }
                    trianglesList.add(new DAETriangles(indices, positions, normals, texCoords, tangents, binormals, tMaterial));
                }
            }
        }
    }

    public List<ShapeNode> getJVRShapeNodes(Map<String, String> instanceMaterialMap, DAEEffectLib effectLib, DAEMaterialLib materialLib, DAEImageLib imageLib) throws Exception {
        List<ShapeNode> shapeNodes = new ArrayList<ShapeNode>();
        for (DAETriangles daeTriangles : trianglesList) {
            TriangleMesh triMesh = daeTriangles.getJVRTriangleMesh();
            ShaderMaterial mat = null;

            String materialId = instanceMaterialMap.get(daeTriangles.getMaterialSymbol());
            DAEMaterial material = materialLib.getDAEMaterial(materialId);
            if (material != null) {
                DAEEffect effect = effectLib.getDAEEffect(material.getInstanceEffectUrl());
                if (effect != null)
                    mat = effect.getJVRShaderMaterial(imageLib);
            }

            shapeNodes.add(new ShapeNode(name + "_Shape", triMesh, mat));
        }

        return shapeNodes;
    }

    private float[] makeAttribArray(DAESource source, List<BigInteger> triIndices, int triIndicesStepSize, int triCount, int targetStride, int inputOffset) {
        float[] arr = new float[triCount * 3 * targetStride];
        for (int i = 0; i < triCount * 3; i++) {
            int index = triIndices.get(triIndicesStepSize * i + inputOffset).intValue();
            for (int offset = 0; offset < targetStride; offset++)
                arr[targetStride * i + offset] = source.getValue(index, offset);
        }
        return arr;
    }
}
