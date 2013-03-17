package de.bht.jvr.util;

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

public final class None<T> implements Option<T> {

    public None() {}

    @Override
    public T get() {
        throw new UnsupportedOperationException("Cannot resolve value on None");
    }

    @Override
    public T getOrDefault(T d) {
        return d;
    }

    @Override
    public T getOrNull() {
        return null;
    }

    @Override
    public boolean isDefined() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}