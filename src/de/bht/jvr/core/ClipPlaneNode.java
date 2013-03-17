package de.bht.jvr.core;

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
 * A clipping plane.
 * 
 * @author Marc Roßbach
 */
public class ClipPlaneNode extends SceneNode {
    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.SceneNode#accept(de.bht.jvr.core.Traverser)
     */
    @Override
    public boolean accept(Traverser traverser) {
        return traverser.visit(this);
    }

    @Override
    protected void updateBBox() {
        super.bBox = BBox.infinite();
    }
}
