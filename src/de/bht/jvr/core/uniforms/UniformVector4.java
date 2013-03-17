package de.bht.jvr.core.uniforms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.Context;
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
 */

public class UniformVector4 implements UniformValue {
    /** The values. */
    private List<Vector4> values = new ArrayList<Vector4>();
    private float[] finalData;

    /**
     * Instantiates a new uniform vector4.
     * 
     * @param values
     *            the values
     */
    public UniformVector4(Collection<Vector4> values) {
        for (Vector4 val : values)
            this.values.add(val);
        createDataArray();
    }

    /**
     * Instantiates a new uniform vector4.
     * 
     * @param values
     *            the values
     */
    public UniformVector4(Vector4... values) {
        for (Vector4 val : values)
            this.values.add(val);
        createDataArray();
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
        if (values.size() == 1)
            gl.glUniform4f(location, finalData[0], finalData[1], finalData[2], finalData[3]);
        else
            gl.glUniform4fv(location, values.size(), finalData, 0);
    }

    /**
     * Creates the float array for uniform upload.
     */
    private void createDataArray() {
        int index = 0;
        finalData = new float[4 * values.size()];
        for (Vector4 val : values) {
            float[] data = val.getData();
            for (float element : data)
                finalData[index++] = element;
        }
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof UniformVector4) {
            for (int i = 0; i < values.size(); i++)
                if (!values.get(i).equals(((UniformVector4) val).values.get(i)))
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
        return values.size();
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.uniforms.UniformValue#getType()
     */
    @Override
    public int getType() {
        return 0x8B52; // GL_FLOAT_VEC4
    }

    public Vector4 getValue(int index) {
        return values.get(index);
    }
}
