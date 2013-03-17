package de.bht.jvr.modelviewer.view;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.modelviewer.model.Scene;

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
public class Renderer implements GLEventListener {
    private Context ctx;
    private Scene scene;

    public Renderer(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        try {
            Pipeline pipeline = scene.getPipeline();
            pipeline.update();
            pipeline.render(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(GLAutoDrawable drawable) {
        ctx = new Context(drawable.getGL());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        scene.setCameraAspect((float) width / (float) height);
    }

}
