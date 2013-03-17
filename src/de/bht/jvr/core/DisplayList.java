package de.bht.jvr.core;

import java.util.HashMap;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GL3bc;

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
 * Yes display lists are deprecated but they can help us to reduce the
 * bottleneck with multiple native calls. But if you still think display lists
 * are crap, you can deactivate them by setting DisplayList.enabled to false.
 * 
 * @author Marc Roßbach
 */

public class DisplayList {
    /**
     * false, deactivates support of display lists
     */
    public static boolean enabled = true;

    private ContextValueMap<HashMap<Object, Integer>> displayLists;

    public DisplayList() {
        displayLists = new ContextValueMap<HashMap<Object, Integer>>();
    }

    public boolean beginList(Context ctx, Object key) {
        if (!enabled || !ctx.displayListsSupported())
            return true;

        GL3bc gl = (GL3bc) ctx.getGL();

        HashMap<Object, Integer> lists = displayLists.get(ctx);
        if (lists == null) {
            lists = new HashMap<Object, Integer>();
            displayLists.put(ctx, lists);
        }

        Integer listId = lists.get(key);

        // create new list
        if (listId == null) {
            listId = gl.glGenLists(1);
            gl.glNewList(listId, GL2.GL_COMPILE);
            lists.put(key, listId);
        } else {
            // call list
            gl.glCallList(listId);
            return false;
        }

        return true;
    }

    public void endList(Context ctx, Object key) {
        if (!enabled)
            return;

        GL3bc gl = (GL3bc) ctx.getGL();
        gl.glEndList();

        // call the generated list
        HashMap<Object, Integer> lists = displayLists.get(ctx);
        if (lists != null) {
            Integer listId = lists.get(key);
            if (listId != null) {
                gl.glCallList(listId);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() {
        List<Context> ctxList = displayLists.getContextList();
        for (Context ctx : ctxList) {
            for (Integer val : displayLists.get(ctx).values())
                ctx.deleteDisplayList(val);
        }
    }
}
