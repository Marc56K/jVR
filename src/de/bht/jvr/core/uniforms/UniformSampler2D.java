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

public class UniformSampler2D implements UniformValue {
    /** The values. */
    protected int[] values = null;

    /**
     * Instantiates a new uniform sampler2 d.
     * 
     * @param value
     *            the value
     */
    public UniformSampler2D(int value) {
        values = new int[1];
        values[0] = value;
    }

    /**
     * Instantiates a new uniform sampler2 d.
     * 
     * @param values
     *            the values
     */
    public UniformSampler2D(int[] values) {
        this.values = values.clone();
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
        if (val instanceof UniformSampler2D) {
            for (int i = 0; i < values.length; i++)
                if (values[i] != ((UniformSampler2D) val).values[i])
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
        return 0x8B5E; // GL_SAMPLER_2D
    }

    public int getValue(int index) {
        return values[index];
    }
}
