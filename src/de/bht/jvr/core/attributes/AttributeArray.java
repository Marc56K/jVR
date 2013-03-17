package de.bht.jvr.core.attributes;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.ContextValueMap;

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

public class AttributeArray {
    protected ContextValueMap<Integer> id = new ContextValueMap<Integer>(-1);
    protected ContextValueMap<Boolean> built = new ContextValueMap<Boolean>(false);
    protected ContextValueMap<AttributeUpdate> update = new ContextValueMap<AttributeUpdate>();
    protected AttributeValues values;

    public AttributeArray(AttributeValues values) {
        this.values = values;
    }

    public void bind(Context ctx, int location) throws Exception {
        if (!isBuilt(ctx))
            build(ctx);

        GL2GL3 gl = ctx.getGL();
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, getId(ctx));

        AttributeUpdate update = this.update.get(ctx);
        if (update != null)
            update.applyUpdate(ctx);

        gl.glEnableVertexAttribArray(location);
        values.bind(ctx, location);
    }

    public void build(Context ctx) throws Exception {
        int id = values.build(ctx);
        this.id.put(ctx, id);
        built.put(ctx, true);
    }

    @Override
    protected void finalize() {
        List<Context> ctxList = built.getContextList();
        for (Context ctx : ctxList)
            if (isBuilt(ctx))
                ctx.deleteVbo(getId(ctx));
    }

    public int getId(Context ctx) {
        return id.get(ctx);
    }

    public int getSize() {
        return 1;
    }

    public int getType() {
        return values.getType();
    }

    public boolean isBuilt(Context ctx) {
        return built.get(ctx);
    }

    public void setUpdate(Context ctx, AttributeUpdate update) {
        this.update.put(ctx, update);
    }

    public void unbind(Context ctx, int location) {
        if (isBuilt(ctx)) {
            GL2GL3 gl = ctx.getGL();
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            gl.glDisableVertexAttribArray(location);
        }
    }
}
