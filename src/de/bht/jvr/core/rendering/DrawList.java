package de.bht.jvr.core.rendering;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.ClipPlaneNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.PrescanInfo;

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
 * A draw list for the next frame.
 * 
 * @author Marc Roßbach
 */
public abstract class DrawList {
    public boolean accept(CameraNode node) {
        return false;
    }

    public boolean accept(ClipPlaneNode node) {
        return false;
    }

    public boolean accept(GroupNode node) {
        return true;
    }

    public boolean accept(LightNode node) {
        return false;
    }

    public boolean accept(ShapeNode node) {
        return false;
    }

    public void add(CameraNode node, Transform worldTrans, PrescanInfo prescanInfo) {}

    public void add(ClipPlaneNode node, Transform worldTrans, PrescanInfo prescanInfo) {}

    public void add(LightNode node, Transform worldTrans, PrescanInfo prescanInfo) {}

    public void add(ShapeNode node, Transform worldTrans, PrescanInfo prescanInfo) {}

    public boolean enter(GroupNode node, Transform worldTrans) {
        return true;
    }

    public void leave(GroupNode node) {}
}
