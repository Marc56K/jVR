package de.bht.jvr.core.uniforms;

import javax.media.opengl.GL2ES2;

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

public class UniformSamplerCube extends UniformSampler2D {
    public UniformSamplerCube(int value) {
        super(value);
    }

    /**
     * Instantiates a new uniform sampler2 d.
     * 
     * @param values
     *            the values
     */
    public UniformSamplerCube(int[] values) {
        super(values);
    }

    @Override
    public boolean equals(Object val) {
        if (val instanceof UniformSamplerCube) {
            for (int i = 0; i < super.values.length; i++)
                if (values[i] != ((UniformSamplerCube) val).values[i])
                    return false;
            return true;
        }
        return false;
    }

    @Override
    public int getType() {
        return GL2ES2.GL_SAMPLER_CUBE;
    }
}
