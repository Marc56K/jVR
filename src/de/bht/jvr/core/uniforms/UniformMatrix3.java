package de.bht.jvr.core.uniforms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.Context;
import de.bht.jvr.math.Matrix3;

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

public class UniformMatrix3 implements UniformValue {
    /** The values. */
    private List<Matrix3> values = new ArrayList<Matrix3>();
    private float[] finalData;

    /**
     * Instantiates a new uniform matrix3.
     * 
     * @param values
     *            the values
     */
    public UniformMatrix3(Collection<Matrix3> values) {
        for (Matrix3 val : values)
            this.values.add(val);
        createDataArray();
    }

    /**
     * Instantiates a new uniform matrix3.
     * 
     * @param values
     *            the values
     */
    public UniformMatrix3(Matrix3... values) {
        for (Matrix3 val : values)
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
        gl.glUniformMatrix3fv(location, values.size(), true, finalData, 0);
    }

    /**
     * Creates the float array for uniform upload.
     */
    private void createDataArray() {
        finalData = null;
        if (values.size() > 1) {
            int index = 0;
            finalData = new float[9 * values.size()];
            for (Matrix3 val : values) {
                float[] data = val.getDataRef();
                for (float element : data)
                    finalData[index++] = element;
            }
        } else if (values.size() == 1)
            finalData = values.get(0).getDataRef();
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof UniformMatrix3) {
            for (int i = 0; i < values.size(); i++)
                if (!values.get(i).equals(((UniformMatrix3) val).values.get(i)))
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
        return 0x8B5B; // GL_FLOAT_MAT3
    }

    public Matrix3 getValue(int index) {
        return values.get(index);
    }
}
