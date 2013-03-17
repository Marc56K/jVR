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
 */

public interface UniformValue {
    /**
     * Bind the uniform value.
     * 
     * @param ctx
     *            the context
     * @param location
     *            the location
     */
    public void bind(Context ctx, int location);

    /**
     * Compares two uniform values
     * 
     * @param val
     *            the other uniform value
     * @return true, if equals
     */
    @Override
    public boolean equals(Object val);

    /**
     * Gets the size.
     * 
     * @return the size
     */
    public int getSize();

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public int getType();
}
