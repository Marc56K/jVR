package de.bht.jvr.core.uniforms;

import javax.media.opengl.GL2GL3;

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

public class UniformBool implements UniformValue {
    /** The values. */
    private int[] values = null;

    /**
     * Instantiates a new uniform boolean.
     * 
     * @param value
     *            the value
     */
    public UniformBool(boolean value) {
        values = new int[1];
        values[0] = value ? 1 : 0;
    }

    /**
     * Instantiates a new uniform boolean.
     * 
     * @param values
     *            the values
     */
    public UniformBool(boolean[] values) {
        this.values = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            this.values[i] = values[i] ? 1 : 0;;
        }
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.uniforms.UniformValue#bind(de.bht.jvr.core.Context,
     * int)
     */
    @Override
    public void bind(Context ctx, int location) {
        GL2GL3 gl = ctx.getGL();

        // set the uniform
        if (values.length == 1)
            gl.glUniform1i(location, values[0]);
        else
            gl.glUniform1iv(location, values.length, values, 0);
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof UniformBool) {
            for (int i = 0; i < values.length; i++)
                if (values[i] != ((UniformBool) val).values[i])
                    return false;
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.uniforms.UniformValue#getSize()
     */
    @Override
    public int getSize() {
        return values.length;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.uniforms.UniformValue#getType()
     */
    @Override
    public int getType() {
        return 0x8B56; // GL_BOOL
    }

    public boolean getValue(int index) {
        return values[index] != 0;
    }
}
