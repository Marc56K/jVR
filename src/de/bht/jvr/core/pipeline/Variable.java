package de.bht.jvr.core.pipeline;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.ContextValueMap;

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

public class Variable {
    private String name;
    private ContextValueMap<Number> value;
    
    public Variable(String name, Number initialValue) {
        this.name = name;
        this.value = new ContextValueMap<Number>(initialValue);
    }
    
    public void setValue(Context ctx, Number value) {
        this.value.put(ctx, value);
    }
    
    public Number getValue(Context ctx) {
        return this.value.get(ctx);
    }
    
    public String getName() {
        return this.name;
    }
}
