package de.bht.jvr.core.attributes;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;

import com.jogamp.common.nio.Buffers;

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

public abstract class AttributeValues {
    protected float[] values;
    protected int elementSize;

    public void bind(Context ctx, int location) {
        ctx.getGL().glVertexAttribPointer(location, elementSize, GL.GL_FLOAT, false, 0, 0);
    }

    public int build(Context ctx) throws Exception {
        int id = -1;

        GL2GL3 gl = ctx.getGL();

        int[] vboId = new int[1];
        gl.glGenBuffers(1, vboId, 0);
        id = vboId[0];

        FloatBuffer fbuffer = Buffers.newDirectFloatBuffer(values);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, id);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, fbuffer.capacity() * Buffers.SIZEOF_FLOAT, fbuffer, GL.GL_STATIC_DRAW);

        return id;
    }

    public int getSize() {
        return values.length / elementSize;
    }

    public abstract int getType();

    public void update(Context ctx) {
        GL2GL3 gl = ctx.getGL();
        FloatBuffer buff = gl.glMapBuffer(GL.GL_ARRAY_BUFFER, GL.GL_WRITE_ONLY).asFloatBuffer();
        buff.put(values, 0, values.length);
        gl.glUnmapBuffer(GL.GL_ARRAY_BUFFER);
    }
}
