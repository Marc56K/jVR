package de.bht.jvr.core;

import java.util.Stack;

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
 * @author Henrik Tramberend
 */

public class TransformStack {
    /** The stack. */
    private Stack<Transform> stack = new Stack<Transform>();

    /**
     * Instantiates a new transform stack.
     */
    public TransformStack() {
        stack.push(new Transform());
    }

    /**
     * Mul.
     * 
     * @param m
     *            the transformation
     */
    public void mul(Transform m) {
        Transform t = stack.pop().mul(m);
        stack.push(t);
    }

    /**
     * Peek.
     * 
     * @return the transform
     */
    public Transform peek() {
        return stack.peek();
    }

    /**
     * Pop.
     */
    public void pop() {
        stack.pop();
    }

    /**
     * Push.
     */
    public void push() {
        stack.push(stack.peek());
    }
}
