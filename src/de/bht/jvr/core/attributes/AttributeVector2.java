package de.bht.jvr.core.attributes;

import java.util.List;

import de.bht.jvr.math.Vector2;

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

public class AttributeVector2 extends AttributeValues {
    public AttributeVector2(float[] values) {
        this.values = values.clone();
        elementSize = 2;
    }

    public AttributeVector2(float[] values, int elementSize) {
        this.values = values.clone();
        this.elementSize = elementSize;
    }

    public AttributeVector2(List<Vector2> values) {
        this.values = new float[2 * values.size()];
        int j = 0;
        for (int i = 0; i < this.values.length; i += 2) {
            Vector2 v = values.get(j++);
            this.values[i] = v.x();
            this.values[i + 1] = v.y();
        }

        elementSize = 2;
    }

    public Vector2 get(int n) {
        return new Vector2(values[2 * n], values[2 * n + 1]);
    }

    @Override
    public int getType() {
        return 0x8B50; // GL_FLOAT_VEC2
    }
}
