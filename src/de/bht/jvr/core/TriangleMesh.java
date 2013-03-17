package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GL3;

import de.bht.jvr.core.attributes.AttributeArray;
import de.bht.jvr.core.attributes.AttributeUpdate;
import de.bht.jvr.core.attributes.AttributeValues;
import de.bht.jvr.core.attributes.AttributeVector2;
import de.bht.jvr.core.attributes.AttributeVector3;
import de.bht.jvr.core.attributes.AttributeVector4;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.math.Vector4;

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
 * The triangle mesh is a geometry consisting of triangles.
 * 
 * @author Marc Roßbach
 */

public class TriangleMesh implements Geometry {
    /** The vertexbuffer object. */
    private VertexBuffer vbo;

    /** The bounding box. */
    private BBox bBox;

    /** The vertex positions */
    private List<Vector4> positions;

    private AttributeVector3 normals;

    private AttributeVector2 texCoords;

    private AttributeVector3 tangents;

    private AttributeVector3 binormals;

    /** Vertices are changed */
    private boolean needUpdate = false;

    private boolean bBoxDirty = true;

    private Map<String, AttributeUpdate> updates;
    
    private DisplayList displayList;

    /**
     * Instantiates a new triangle mesh.
     */
    private TriangleMesh() {}

    /**
     * Instantiates a new triangle mesh.
     * 
     * @param indices
     *            the indices
     * @param positions
     *            the positions (x,y,z)
     * @param normals
     *            the normals
     * @param texCoords
     *            the texture coordinates
     * @param tangents
     *            the tangents
     * @param binormals
     *            the binormals
     * @throws Exception
     *             the exception
     */
    public TriangleMesh(int[] indices, float[] positions, float[] normals, float[] texCoords, float[] tangents, float[] binormals) throws Exception {
        vbo = new VertexBuffer();
        bBox = new BBox();
        updates = new HashMap<String, AttributeUpdate>();
        displayList = new DisplayList();

        if (indices != null)
            vbo.setIndices(indices);
        else
            throw new Exception("Invalid number of indices in mesh: 0");

        if (positions.length % 3 == 0) {
            this.positions = new ArrayList<Vector4>();
            for (int i = 0; i < positions.length; i += 3)
                this.positions.add(new Vector4(positions[i], positions[i + 1], positions[i + 2], 1));
            vbo.setVertexAttribArray("jvr_Vertex", new AttributeArray(new AttributeVector4(this.positions)));
            updateBBox();

            // normals
            if (normals == null)
                normals = new float[positions.length];

            if (normals.length == positions.length) {
                this.normals = new AttributeVector3(normals);
                vbo.setVertexAttribArray("jvr_Normal", new AttributeArray(this.normals));
            } else
                throw new Exception("Invalid number of normals in mesh: " + (float) normals.length / 3);

            // texCoords
            if (texCoords == null)
                texCoords = new float[positions.length / 3 * 2];

            if (texCoords.length / 2 == positions.length / 3) {
                this.texCoords = new AttributeVector2(texCoords);
                vbo.setVertexAttribArray("jvr_TexCoord", new AttributeArray(this.texCoords));
            } else
                throw new Exception("Invalid number of texture coordinates in mesh: " + (float) texCoords.length / 2);

            // tangents
            if (tangents == null)
                tangents = new float[positions.length];

            if (tangents.length == positions.length) {
                this.tangents = new AttributeVector3(tangents);
                vbo.setVertexAttribArray("jvr_Tangent", new AttributeArray(this.tangents));
            } else
                throw new Exception("Invalid number of tangents in mesh: " + (float) tangents.length / 3);

            // binormals
            if (binormals == null)
                binormals = new float[positions.length];

            if (binormals.length == positions.length) {
                this.binormals = new AttributeVector3(binormals);
                vbo.setVertexAttribArray("jvr_Binormal", new AttributeArray(this.binormals));
            } else
                throw new Exception("Invalid number of binormals in mesh: " + (float) binormals.length / 3);
        } else
            throw new Exception("Invalid positions in mesh");
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Geometry#getBBox()
     */
    @Override
    public BBox getBBox() {
        if (bBoxDirty)
            updateBBox();
        return bBox;
    }

    /**
     * Creates the bounding box.
     * 
     * @param pos
     *            the vertices
     */
    private void updateBBox() {
        bBox = new BBox();
        for (Vector4 v : positions)
            bBox = bBox.grow(v.xyz());
        bBoxDirty = false;
    }

    public Vector3 getBinormal(int n) {
        return binormals.get(n);
    }

    /**
     * Gets a index (fast);
     * 
     * @param i
     *            the i th index
     * @return the index value
     */
    public int getIndex(int i) {
        return vbo.getIndex(i);
    }

    /**
     * Gets a copy of the indices (slow).
     * 
     * @return the indices
     */
    public int[] getIndices() {
        return vbo.getIndices();
    }

    /**
     * Gets the size of the index array (fast);
     * 
     * @return the size
     */
    public int getIndicesCount() {
        return vbo.getSize();
    }

    public Vector3 getNormal(int n) {
        return normals.get(n);
    }

    public Vector3 getTangent(int n) {
        return tangents.get(n);
    }

    public Vector2 getTexCoord(int n) {
        return texCoords.get(n);
    }

    private Map<String, AttributeUpdate> getUpdates() {
        if (needUpdate) {
            updates.put("jvr_Vertex", new AttributeUpdate(new AttributeVector4(positions)));
            needUpdate = false;
        }

        Map<String, AttributeUpdate> updates = new HashMap<String, AttributeUpdate>();
        updates.putAll(this.updates);

        return updates;
    }

    /**
     * Gets a vertex position (fast).
     * 
     * @param n
     *            the n-th vertex
     * @return the vertex position
     */
    public Vector4 getVertex(int n) {
        return positions.get(n);
    }

    /**
     * Gets a copy of the vertex positions (slow).
     * 
     * @return the positions
     */
    public List<Vector4> getVertices() {
        return new ArrayList<Vector4>(positions);
    }

    /**
     * Gets the size of the vertices array (fast).
     * 
     * @return the size
     */
    public int getVerticesCount() {
        return positions.size();
    }

    public void setAttribute(String name, AttributeValues values) {
        if (values.getSize() != getVerticesCount())
            throw new RuntimeException("Invalid number of list elements: " + values.getSize());

        if (name.equals("jvr_Vertex"))
            throw new RuntimeException("You can't update vertices this way. But you can use the methode setVertices(List<Vector4> positions) instead.");

        // update attribute array
        updates.put(name, new AttributeUpdate(values));
    }

    /**
     * Sets new vertex positions (use this method to update all vertices).
     * 
     * @param positions
     */
    public void setVertices(List<Vector4> positions) {
        if (this.positions.size() == positions.size()) {
            this.positions = new ArrayList<Vector4>(positions);
            bBoxDirty = true;
            needUpdate = true;
        } else
            throw new RuntimeException("Invalid vertex count.");
    }

    @Override
    public boolean isBuilt(Context ctx) {
        return vbo.isBuilt(ctx);
    }

    @Override
    public Vector3 pick(PickRay ray) {
        Vector3 o = ray.getRayOrigin();
        Vector3 d = ray.getRayDirection().normalize();
        Vector3 closestPoint = null;
        float closestDist = 0;

        for (int i = 0; i < vbo.getSize(); i += 3) {
            int idx = vbo.getIndex(i);
            Vector3 p0 = positions.get(idx).xyz();
            idx = vbo.getIndex(i + 1);
            Vector3 p1 = positions.get(idx).xyz();
            idx = vbo.getIndex(i + 2);
            Vector3 p2 = positions.get(idx).xyz();

            Vector3 point = rayTriIntersect(o, d, p0, p1, p2);

            if (point != null) {
                float dist = o.sub(point).length();
                if (closestPoint == null || dist < closestDist) {
                    closestPoint = point;
                    closestDist = dist;
                }
            }
        }
        return closestPoint;
    }

    /**
     * Calculates the intersection of a line and a triangle.
     * 
     * @param orig
     *            origin of the picking ray
     * @param dir
     *            direction of the picking ray
     * @param vert0
     *            first triangle point
     * @param vert1
     *            second triangle point
     * @param vert2
     *            third triangle point
     * @return the intersection point or null
     */
    private Vector3 rayTriIntersect(Vector3 orig, Vector3 dir, Vector3 vert0, Vector3 vert1, Vector3 vert2) {
        // http://www.cs.virginia.edu/~gfx/Courses/2003/ImageSynthesis/papers/Acceleration/Fast%20MinimumStorage%20RayTriangle%20Intersection.pdf

        final float epsilon = 0.000001f;

        // find vectors for two edges sharing vert0
        Vector3 edge1 = vert1.sub(vert0);
        Vector3 edge2 = vert2.sub(vert0);

        // begin calculating determinant - also used to calculate U parameter
        Vector3 pvec = dir.cross(edge2);

        // if determinant is near zero, ray lies in plane of triangle
        float det = edge1.dot(pvec);

        if (det > -epsilon && det < epsilon)
            return null;
        float inv_det = 1f / det;

        // calculate distance from vert0 to ray origin
        Vector3 tvec = orig.sub(vert0);

        // calculate U parameter and test bounds
        float u = tvec.dot(pvec) * inv_det;
        if (u < 0 || u > 1)
            return null;

        // prepare to test V parameter
        Vector3 qvec = tvec.cross(edge1);

        // calculate V parameter and test bounds
        float v = dir.dot(qvec) * inv_det;
        if (v < 0 || u + v > 1)
            return null;

        // calculate t, ray intersects triangle
        // float t = edge2.dot(pvec) * inv_det;

        // if(t>=0)
        // {
        // return vert0.mul(1-u-v).add(vert1.mul(u)).add(vert2.mul(v));
        // }

        // calculate intersection point
        Vector3 e = edge1.cross(edge2);
        float d = e.dot(vert0);
        float s = (d - e.dot(orig)) / e.dot(dir);
        if (s >= 0)
            return orig.add(dir.mul(s));

        return null;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Geometry#render(de.bht.jvr.core.Context)
     */
    @Override
    public void render(Context ctx) throws Exception {
        GL2GL3 gl = ctx.getGL();
        
        ShaderProgram program = ctx.getShaderProgram();
        if (program != null) {
            // Benchmark.beginStep("VBO Rendering");
            
            if (displayList == null || displayList.beginList(ctx, program)) {
                vbo.enqueueUpdates(ctx, updates);
                vbo.bind(ctx);

                // draw mesh
                if (program.hasTessellationShader()) {
                    // tessellation only works with patches
                    gl.glDrawElements(GL3.GL_PATCHES, vbo.getSize(), GL.GL_UNSIGNED_INT, 0);
                } else {
                    gl.glDrawElements(GL3.GL_TRIANGLES, vbo.getSize(), GL.GL_UNSIGNED_INT, 0);
                }

                vbo.unbind(ctx);

                if (displayList != null)
                    displayList.endList(ctx, program);
            }
            // Benchmark.endStep("VBO Rendering");
        } else
            throw new Exception("No active shader program to render the mesh.");
    }

    @Override
    public Geometry getRenderClone() {
        TriangleMesh clone = new TriangleMesh();
        clone.bBox = getBBox();
        clone.bBoxDirty = false;
        clone.updates = getUpdates();
        clone.vbo = vbo;
        
        // If there are updates, this is a dynamic geometry so using of display lists is not recommend.      
        if (clone.updates.size() > 0)
            displayList = null;
        clone.displayList = displayList;

        return clone;
    }
}
