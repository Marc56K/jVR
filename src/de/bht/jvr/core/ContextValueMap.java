package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * The ContextValueMap is used to store context dependent values.
 * 
 * @param <T>
 *            the generic type
 * @author Marc Roßbach
 */
public class ContextValueMap<T> {
    @SuppressWarnings("unchecked")
    private T[] values = (T[]) new Object[1];
    private Set<Context> contextSet = Collections.synchronizedSet(new HashSet<Context>());
    private T defaultValue = null;

    /**
     * 
     */
    public ContextValueMap() {}

    /**
     * @param defaultValue
     */
    public ContextValueMap(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the stored value.
     * 
     * @param ctx
     *            the context
     * @return the value
     */
    public T get(Context ctx) {
        if (ctx.getId() >= values.length)
            return defaultValue;

        T value = values[ctx.getId()];
        if (value == null)
            return defaultValue;

        return value;
    }

    /**
     * Gets the context list.
     * 
     * @return the context list
     */
    public List<Context> getContextList() {
        List<Context> list = new ArrayList<Context>();

        synchronized (contextSet) {
            list.addAll(contextSet);
        }

        return list;
    }

    public int getSize() {
        return contextSet.size();
    }

    /**
     * Stores a value for the passed context.
     * 
     * @param ctx
     *            the context
     * @param value
     *            the value
     */
    public synchronized void put(Context ctx, T value) {
        if (ctx.getId() >= values.length) {
            @SuppressWarnings("unchecked")
            T[] newValues = (T[]) new Object[ctx.getId() + 1];
            System.arraycopy(values, 0, newValues, 0, values.length);
            this.values = newValues;
        }

        values[ctx.getId()] = value;
        contextSet.add(ctx);
    }
}
