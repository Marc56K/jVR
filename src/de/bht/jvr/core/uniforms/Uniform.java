package de.bht.jvr.core.uniforms;

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
 *
 *
 * Extracted uniform variable of the shader program.
 * 
 * @author Marc Roßbach
 */
public class Uniform {
    /**
     * Gets the type name.
     * 
     * @param type
     *            the type
     * @return the type name
     */
    public static String getTypeName(int type) {
        switch (type) {
        case 0x1400:
            return "GL_BYTE";
        case 0x1401:
            return "GL_UNSIGNED_BYTE";
        case 0x1402:
            return "GL_SHORT";
        case 0x1403:
            return "GL_UNSIGNED_SHORT";
        case 0x1404:
            return "GL_INT";
        case 0x1405:
            return "GL_UNSIGNED_INT";
        case 0x1406:
            return "GL_FLOAT";
        case 0x1407:
            return "GL_2_BYTES";
        case 0x1408:
            return "GL_3_BYTES";
        case 0x1409:
            return "GL_4_BYTES";
        case 0x140A:
            return "GL_DOUBLE";
        case 0x8B50:
            return "GL_FLOAT_VEC2";
        case 0x8B51:
            return "GL_FLOAT_VEC3";
        case 0x8B52:
            return "GL_FLOAT_VEC4";
        case 0x8B53:
            return "GL_INT_VEC2";
        case 0x8B54:
            return "GL_INT_VEC3";
        case 0x8B55:
            return "GL_INT_VEC4";
        case 0x8B56:
            return "GL_BOOL";
        case 0x8B57:
            return "GL_BOOL_VEC2";
        case 0x8B58:
            return "GL_BOOL_VEC3";
        case 0x8B59:
            return "GL_BOOL_VEC4";
        case 0x8B5A:
            return "GL_FLOAT_MAT2";
        case 0x8B5B:
            return "GL_FLOAT_MAT3";
        case 0x8B5C:
            return "GL_FLOAT_MAT4";
        case 0x8B5D:
            return "GL_SAMPLER_1D";
        case 0x8B5E:
            return "GL_SAMPLER_2D";
        case 0x8B5F:
            return "GL_SAMPLER_3D";
        case 0x8B60:
            return "GL_SAMPLER_CUBE";
        case 0x8B61:
            return "GL_SAMPLER_1D_SHADOW";
        case 0x8B62:
            return "GL_SAMPLER_2D_SHADOW";
        }
        return "UNKOWN TYPE: " + type;
    }

    /** The uniform location. */
    private int location = -1;

    /** The type of the uniform. */
    private int type = 0;

    /** The array size of the uniform. */
    private int size = 0;

    /**
     * Instantiates a new uniform.
     * 
     * @param ctx
     *            the context
     * @param location
     *            the location
     * @param type
     *            the type
     * @param size
     *            the size
     */
    public Uniform(Context ctx, int location, int type, int size) {
        this.location = location;
        this.type = type;
        this.size = size;
    }

    /**
     * Bind a uniform value.
     * 
     * @param ctx
     *            the context
     * @param value
     *            the value
     * @throws Exception
     *             the exception
     */
    public void bind(Context ctx, UniformValue value) throws Exception {
        if (value.getType() != type)
            throw new Exception("Invalid uniform type " + getTypeName(value.getType()) + " should be " + getTypeName(type));

        if (value.getSize() > size)
            throw new Exception("Invalid uniform size " + value.getSize() + " schould be " + size);
        value.bind(ctx, location);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Location: " + location + " Type: " + getTypeName(type) + " Size: " + size;
    }
}
