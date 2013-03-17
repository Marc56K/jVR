package de.bht.jvr.core.attributes;

import java.util.List;

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

public class AttributeVector4 extends AttributeValues {
    public AttributeVector4(float[] values) {
        this.values = values.clone();
        elementSize = 4;
    }

    public AttributeVector4(float[] values, int elementSize) {
        this.values = values.clone();
        this.elementSize = elementSize;
    }

    public AttributeVector4(List<Vector4> values) {
        this.values = new float[4 * values.size()];
        int j = 0;
        for (int i = 0; i < this.values.length; i += 4) {
            Vector4 v = values.get(j++);
            this.values[i] = v.x();
            this.values[i + 1] = v.y();
            this.values[i + 2] = v.z();
            this.values[i + 3] = v.w();
        }

        elementSize = 4;
    }

    public Vector4 get(int n) {
        return new Vector4(values[4 * n], values[4 * n + 1], values[4 * n + 2], values[4 * n + 3]);
    }

    @Override
    public int getType() {
        return 0x8B52; // GL_FLOAT_VEC4
    }
}
