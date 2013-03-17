package de.bht.jvr.core;

import java.nio.IntBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;

import com.jogamp.common.nio.Buffers;

import de.bht.jvr.core.attributes.AttributeArray;
import de.bht.jvr.core.attributes.AttributeUpdate;

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

public class VertexBuffer {
    /** The triangle mesh is built. */
    private ContextValueMap<Boolean> built = new ContextValueMap<Boolean>(false);

    /** The gl indices id. */
    private ContextValueMap<Integer> indicesId = new ContextValueMap<Integer>(-1);

    /** The indices. */
    private int[] indices;

    /** The attribute arrays. */
    private Map<String, AttributeArray> attribArrays = Collections.synchronizedMap(new HashMap<String, AttributeArray>());
    private ContextValueMap<Map<String, AttributeArray>> activeAttribArrays = new ContextValueMap<Map<String, AttributeArray>>();
    private ContextValueMap<Map<String, AttributeUpdate>> updates = new ContextValueMap<Map<String, AttributeUpdate>>();

    private void applyUpdates(Context ctx) throws Exception {
        Map<String, AttributeUpdate> u = updates.get(ctx);
        if (u != null) {
            Map<String, AttributeArray> attribArrays = activeAttribArrays.get(ctx);
            for (Entry<String, AttributeUpdate> entry : u.entrySet()) {
                String name = entry.getKey();
                AttributeUpdate update = entry.getValue();
                AttributeArray arr = attribArrays.get(name);
                if (arr != null)
                    // update the attribute array
                    arr.setUpdate(ctx, update);
                else {
                    // create a new attribute array
                    arr = update.createAttributeArray(ctx);
                    attribArrays.put(name, arr);
                }
            }
        }
    }

    public void bind(Context ctx) throws Exception {
        GL2GL3 gl = ctx.getGL();

        if (!isBuilt(ctx))
            // create vbo
            build(ctx);

        ShaderProgram program = ctx.getShaderProgram();
        if (program != null) {
            // update vertex attribute arrays
            applyUpdates(ctx);

            // enable indices
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesId.get(ctx));

            // enable vertex attribute arrays
            for (Entry<String, AttributeArray> entry : activeAttribArrays.get(ctx).entrySet())
                program.bindAttribArray(ctx, entry.getKey(), entry.getValue());
        } else
            throw new Exception("No active shader program to bind vbo.");
    }

    /**
     * Builds the vertexbuffer object.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public void build(Context ctx) throws Exception {
        GL2GL3 gl = ctx.getGL();
        built.put(ctx, new Boolean(false));

        // Indices
        int[] vboId = new int[1];
        gl.glGenBuffers(1, vboId, 0);
        indicesId.put(ctx, new Integer(vboId[0]));

        IntBuffer indicesb = Buffers.newDirectIntBuffer(indices);
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesId.get(ctx));
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesb.capacity() * Buffers.SIZEOF_INT, indicesb, GL.GL_STATIC_DRAW);

        // set attribute arrays active
        Map<String, AttributeArray> attribArrays = new HashMap<String, AttributeArray>();
        activeAttribArrays.put(ctx, attribArrays);

        for (Entry<String, AttributeArray> entry : this.attribArrays.entrySet()) {
            if (!entry.getValue().isBuilt(ctx))
                entry.getValue().build(ctx);
            attribArrays.put(entry.getKey(), entry.getValue());
        }

        built.put(ctx, true);
    }

    public void enqueueUpdates(Context ctx, Map<String, AttributeUpdate> updates) {
        if (updates != null) {
            Map<String, AttributeUpdate> u = this.updates.get(ctx);
            if (u == null) {
                u = new HashMap<String, AttributeUpdate>();
                this.updates.put(ctx, u);
            }

            u.putAll(updates);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() {
        List<Context> ctxList = built.getContextList();
        for (Context ctx : ctxList)
            if (isBuilt(ctx))
                ctx.deleteVbo(indicesId.get(ctx));
    }

    public int getIndex(int i) {
        return indices[i];
    }

    public int[] getIndices() {
        return indices.clone();
    }

    public int getSize() {
        return indices.length;
    }

    /**
     * Checks if the vbo is built.
     * 
     * @param ctx
     *            the context
     * @return true, if is built
     */
    public boolean isBuilt(Context ctx) {
        return built.get(ctx);
    }

    /**
     * Sets the index array of the vbo.
     * 
     * @param indices
     */
    public void setIndices(int[] indices) {
        this.indices = indices.clone();
    }

    /**
     * Sets a vertex attribute array.
     * 
     * @param attributeName
     *            the attribute name
     * @param attribArray
     *            the attribute array
     */
    public void setVertexAttribArray(String attributeName, AttributeArray attribArray) {
        if (built.getSize() == 0)
            attribArrays.put(attributeName, attribArray);
        else
            throw new RuntimeException("VBO have already been initialized!");
    }

    public void unbind(Context ctx) throws Exception {
        if (isBuilt(ctx)) {
            GL2GL3 gl = ctx.getGL();
            ShaderProgram program = ctx.getShaderProgram();
            if (program != null) {
                // disable vertex attribute arrays
                for (Entry<String, AttributeArray> entry : activeAttribArrays.get(ctx).entrySet())
                    program.unbindAttribArray(ctx, entry.getKey(), entry.getValue());

                // disable indices
                gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
            } else
                throw new Exception("No active shader program to unbind vbo.");
        }
    }
}
