package de.bht.jvr.core.attributes;

import java.util.List;

import javax.media.opengl.GL;

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

public class AttributeFloat extends AttributeValues {
    public AttributeFloat(float[] values) {
        this.values = values.clone();
        elementSize = 1;
    }

    public AttributeFloat(float[] values, int elementSize) {
        this.values = values.clone();
        this.elementSize = elementSize;
    }

    public AttributeFloat(List<Float> values) {
        this.values = new float[values.size()];
        int j = 0;
        for (int i = 0; i < this.values.length; i += 1)
            this.values[i] = values.get(j++);

        elementSize = 1;
    }

    public float get(int n) {
        return values[n];
    }

    @Override
    public int getType() {
        return GL.GL_FLOAT;
    }
}
