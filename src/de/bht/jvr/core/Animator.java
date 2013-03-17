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
 */

public class Animator implements Traverser {

    private float time;
    
    public static void animate(SceneNode root, float time) {
        root.accept(new Animator(time));
    }
    
    private Animator(float time) {
        this.time = time;
    }
    
    private void execute(SceneNode node) {
        Animation anim = node.getAnimation();
        if (anim != null)
            anim.execute(this.time, node);
    }
    
    @Override
    public boolean enter(GroupNode node) {
        execute(node);
        return true;
    }

    @Override
    public boolean leave(GroupNode node) {
        return true;
    }

    @Override
    public boolean visit(CameraNode node) {
        execute(node);
        return true;
    }

    @Override
    public boolean visit(ClipPlaneNode node) {
        execute(node);
        return true;
    }

    @Override
    public boolean visit(LightNode node) {
        execute(node);
        return true;
    }

    @Override
    public boolean visit(ShapeNode node) {
        execute(node);
        return true;
    }

}
