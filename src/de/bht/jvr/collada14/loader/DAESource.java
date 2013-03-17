package de.bht.jvr.collada14.loader;

import java.math.BigInteger;
import java.util.List;

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

public class DAESource {
    private List<Double> sourceValues = null;
    private int stride = 0;

    public DAESource(List<Double> sourceValues, BigInteger stride) {
        this.sourceValues = sourceValues;
        this.stride = stride.intValue();
    }

    public List<Double> getSourceValues() {
        return sourceValues;
    }

    public int getStride() {
        return stride;
    }

    public float getValue(int index, int offset) {
        Double val = sourceValues.get(index * stride + offset);
        return val.floatValue();
    }
}
