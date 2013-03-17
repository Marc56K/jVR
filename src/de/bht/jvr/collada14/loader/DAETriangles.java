package de.bht.jvr.collada14.loader;

import de.bht.jvr.core.TriangleMesh;
import de.bht.jvr.logger.Log;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.math.Vector3;

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

public class DAETriangles {
    /**
     * Generates tangents for a triangle. Source:
     * http://download.autodesk.com/us
     * /maya/2009help/index.html?url=Appendix_A_Tangent_and_binormal_vectors
     * .htm,topicNumber=d0e216694
     * 
     * @param v
     *            Vertices of the triangle
     * @param n
     *            Normals of the triangle
     * @param t
     *            Texture coords of the triangle
     * @return the tangents
     */
    public static Vector3[] generateTangents(Vector3[] v, Vector3[] n, Vector2[] t) {
        if (v.length == 3 && n.length == 3 && t.length == 3) {
            Vector3[] finalTangentArray = new Vector3[3];
            float[][] tangentArray = new float[3][3];
            float[] edge1 = new float[3];
            float[] edge2 = new float[3];
            Vector3 crossP = null;
            // ==============================================
            // x, s, t
            // S & T vectors get used several times in this vector,
            // but are only computed once.
            // ==============================================
            edge1[0] = v[1].x() - v[0].x();
            edge1[1] = t[1].x() - t[0].x(); // s-vector - don't need to compute
                                            // this multiple times
            edge1[2] = t[1].y() - t[0].y(); // t-vector
            edge2[0] = v[2].x() - v[0].x();
            edge2[1] = t[2].x() - t[0].x(); // another s-vector
            edge2[2] = t[2].y() - t[0].y(); // another t-vector
            crossP = new Vector3(edge1).cross(new Vector3(edge2)).normalize();
            boolean degnerateUVTangentPlane = crossP.x() == 0.0f;
            if (degnerateUVTangentPlane)
                crossP = new Vector3(1, crossP.y(), crossP.z());
            float tanX = -crossP.y() / crossP.x();
            tangentArray[0][0] = tanX;
            tangentArray[1][0] = tanX;
            tangentArray[2][0] = tanX;
            // --------------------------------------------------------
            // y, s, t
            // --------------------------------------------------------
            edge1[0] = v[1].y() - v[0].y();
            edge2[0] = v[2].y() - v[0].y();
            crossP = new Vector3(edge1).cross(new Vector3(edge2)).normalize();
            degnerateUVTangentPlane = crossP.x() == 0.0f;
            if (degnerateUVTangentPlane)
                crossP = new Vector3(1, crossP.y(), crossP.z());
            float tanY = -crossP.y() / crossP.x();
            tangentArray[0][1] = tanY;
            tangentArray[1][1] = tanY;
            tangentArray[2][1] = tanY;
            // ------------------------------------------------------
            // z, s, t
            // ------------------------------------------------------
            edge1[0] = v[1].z() - v[0].z();
            edge2[0] = v[2].z() - v[0].z();
            crossP = new Vector3(edge1).cross(new Vector3(edge2)).normalize();
            degnerateUVTangentPlane = crossP.x() == 0.0f;
            if (degnerateUVTangentPlane)
                crossP = new Vector3(1, crossP.y(), crossP.z());
            float tanZ = -crossP.y() / crossP.x();
            tangentArray[0][2] = tanZ;
            tangentArray[1][2] = tanZ;
            tangentArray[2][2] = tanZ;
            // Orthnonormalize to normal
            for (int i = 0; i < 3; i++) {
                Vector3 tangent = new Vector3(tangentArray[i][0], tangentArray[i][1], tangentArray[i][2]);
                finalTangentArray[i] = tangent.sub(n[i].mul(tangent.dot(n[i]))).normalize();
            }

            return finalTangentArray;
        } else
            return null;
    }

    private int[] indices;
    private float[] positions;
    private float[] normals;
    private float[] texCoords;
    private float[] tangents;

    private float[] binormals;
    private String material;

    private TriangleMesh triMesh = null;

    public DAETriangles(int[] indices, float[] positions, float[] normals, float[] texCoords, float[] tangents, float[] binormals, String material) {
        this.indices = indices;
        this.positions = positions;
        this.normals = normals;
        this.texCoords = texCoords;
        this.tangents = tangents;
        this.binormals = binormals;
        this.material = material;
    }

    public TriangleMesh getJVRTriangleMesh() throws Exception {
        if (triMesh == null) {
            if (tangents == null || binormals == null)
                if (positions != null && normals != null && texCoords != null) {
                    Log.warning(this.getClass(), "Generating tangents from normals and texture coordinates.");

                    tangents = new float[normals.length];
                    binormals = new float[normals.length];
                    // iterate the triangles
                    for (int i = 0; i < indices.length; i += 3) {
                        Vector3[] v = new Vector3[3];
                        Vector3[] n = new Vector3[3];
                        Vector2[] t = new Vector2[3];
                        for (int j = 0; j < 3; j++) {
                            v[j] = new Vector3(positions[3 * indices[i + j]], positions[3 * indices[i + j] + 1], positions[3 * indices[i + j] + 2]);
                            n[j] = new Vector3(normals[3 * indices[i + j]], normals[3 * indices[i + j] + 1], normals[3 * indices[i + j] + 2]);
                            t[j] = new Vector2(texCoords[2 * indices[i + j]], texCoords[2 * indices[i + j] + 1]);
                        }

                        Vector3[] newTangents = generateTangents(v, n, t);
                        Vector3[] newBinormals = new Vector3[3];

                        if (newTangents != null) {
                            // generate binormals
                            for (int k = 0; k < 3; k++)
                                newBinormals[k] = newTangents[k].cross(n[k]).normalize();

                            // convert to float array
                            for (int j = 0; j < 3; j++) {
                                tangents[3 * indices[i + j]] = newTangents[j].x();
                                tangents[3 * indices[i + j] + 1] = newTangents[j].y();
                                tangents[3 * indices[i + j] + 2] = newTangents[j].z();

                                binormals[3 * indices[i + j]] = newBinormals[j].x();
                                binormals[3 * indices[i + j] + 1] = newBinormals[j].y();
                                binormals[3 * indices[i + j] + 2] = newBinormals[j].z();
                            }
                        } else {
                            tangents = null;
                            binormals = null;
                        }
                    }
                } else
                    Log.warning(this.getClass(), "No tangents found.");
            triMesh = new TriangleMesh(indices, positions, normals, texCoords, tangents, binormals);
        }

        return triMesh;
    }

    public String getMaterialSymbol() {
        return material;
    }
}
