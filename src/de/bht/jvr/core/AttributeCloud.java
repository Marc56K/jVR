package de.bht.jvr.core;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.attributes.AttributeUpdate;
import de.bht.jvr.core.attributes.AttributeValues;
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
 *
 * 
 * The attribute cloud can be everything.
 * 
 * @author Marc Roßbach
 */

public class AttributeCloud implements Geometry {
    /** The vertexbuffer object. */
    private VertexBuffer vbo;

    private Map<String, AttributeUpdate> updates;

    private BBox bBox;

    private int numPoints = 0;
    private float pointSize = 1.0f;
    private int primitiveType = GL.GL_POINTS;

    /**
     * Instantiates a new attribute cloud.
     */
    private AttributeCloud() {}

    /**
     * Instantiates a new point cloud with point size 1.0
     * 
     * @param numPoints
     *            the number of points
     */
    public AttributeCloud(int numPoints) {
        this(numPoints, 1, GL.GL_POINTS);
    }

    /**
     * Instantiates a new point cloud.
     * 
     * @param numPoints
     *            the number of points
     * @param pointSize
     *            the size of the points in pixel
     * @param primitiveType
     *            GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES,
     *            GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES,
     *            GL_QUAD_STRIP, GL_QUADS or GL_POLYGON
     */
    public AttributeCloud(int numPoints, float pointSize, int primitiveType) {
        if (numPoints >= 0) {
            vbo = new VertexBuffer();
            updates = new HashMap<String, AttributeUpdate>();
            bBox = BBox.infinite();

            this.pointSize = pointSize;
            this.numPoints = numPoints;
            this.primitiveType = primitiveType;
            int[] indices = new int[numPoints];
            for (int i = 0; i < numPoints; i++)
                indices[i] = i;

            if (indices != null)
                vbo.setIndices(indices);
        } else
            throw new RuntimeException("Invalid number of points: " + numPoints);
    }

    /**
     * Instantiates a new attribute cloud.
     * 
     * @param numAttributesPoints
     *            the number of attributes points
     * @param primitiveType
     *            GL_POINTS, GL_LINE_STRIP, GL_LINE_LOOP, GL_LINES,
     *            GL_TRIANGLE_STRIP, GL_TRIANGLE_FAN, GL_TRIANGLES,
     *            GL_QUAD_STRIP, GL_QUADS or GL_POLYGON
     */
    public AttributeCloud(int numAttributesPoints, int primitiveType) {
        this(numAttributesPoints, 1, primitiveType);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Geometry#getAABB()
     */
    @Override
    public BBox getBBox() {
        return bBox;
    }

    /**
     * Gets the number of points.
     * 
     * @return the size
     */
    public int getNumPoints() {
        return numPoints;
    }

    @Override
    public Geometry getRenderClone() {
        AttributeCloud clone = new AttributeCloud();
        clone.bBox = getBBox();
        clone.vbo = vbo;
        clone.updates = getUpdates();
        clone.primitiveType = primitiveType;
        clone.pointSize = pointSize;

        return clone;
    }

    private Map<String, AttributeUpdate> getUpdates() {
        Map<String, AttributeUpdate> updates = new HashMap<String, AttributeUpdate>();
        updates.putAll(this.updates);
        return updates;
    }

    @Override
    public boolean isBuilt(Context ctx) {
        return vbo.isBuilt(ctx);
    }

    @Override
    public Vector3 pick(PickRay ray) {
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
            vbo.enqueueUpdates(ctx, updates);
            vbo.bind(ctx);

            // draw mesh
            gl.glPointSize(pointSize);
            gl.glDrawElements(primitiveType, vbo.getSize(), GL.GL_UNSIGNED_INT, 0);

            vbo.unbind(ctx);
        } else
            throw new Exception("No active shader program to render the cloud.");
    }

    public void setAttribute(String name, AttributeValues values) {
        if (values.getSize() == getNumPoints())
            // update attribute array
            updates.put(name, new AttributeUpdate(values));
        else
            throw new RuntimeException("Invalid number of list elements: " + values.getSize());
    }

    public void setBBox(BBox bBox) {
        this.bBox = bBox;
    }
}
