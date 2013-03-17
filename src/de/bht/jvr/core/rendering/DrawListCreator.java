package de.bht.jvr.core.rendering;

import java.util.ArrayList;
import java.util.List;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.ClipPlaneNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.TransformStack;
import de.bht.jvr.core.Traverser;
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
 * The draw list creator traverse the scene graph and generates new draws list
 * for the next frame.
 * 
 * @author Marc Roßbach
 */
public class DrawListCreator implements Traverser {
    public static void updateDrawList(SceneNode root, DrawList drawList, PrescanInfo prescanInfo) {
        List<DrawList> list = new ArrayList<DrawList>();
        list.add(drawList);
        DrawListCreator.updateDrawLists(root, list, prescanInfo);
    }

    public static void updateDrawLists(SceneNode root, List<DrawList> drawLists, PrescanInfo prescanInfo) {
        if (drawLists.size() > 0) {
            DrawListCreator traverser = new DrawListCreator(drawLists, prescanInfo);
            root.accept(traverser);
        }
    }

    /** The draw lists. */
    private List<DrawList> drawLists = new ArrayList<DrawList>();
    
    private PrescanInfo prescanInfo = null;

    /** The transform stack. */
    private TransformStack transformStack = new TransformStack();

    public DrawListCreator(List<DrawList> drawLists, PrescanInfo prescanInfo) {
        this.drawLists = drawLists;
        this.prescanInfo = prescanInfo;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#enter(de.bht.jvr.core.GroupNode)
     */
    @Override
    public boolean enter(GroupNode node) {
        transformStack.push();
        transformStack.mul(node.getTransform());

        if (node.isEnabled()) {
            boolean accept = false;
            for (DrawList dl : drawLists)
                if (dl.accept(node))
                    accept = true;

            if (accept) {
                boolean enter = false;
                for (DrawList dl : drawLists)
                    if (dl.enter(node, transformStack.peek()))
                        enter = true;

                return enter;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#leave(de.bht.jvr.core.GroupNode)
     */
    @Override
    public boolean leave(GroupNode node) {
        transformStack.pop();

        for (DrawList dl : drawLists)
            dl.leave(node);

        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.CameraNode)
     */
    @Override
    public boolean visit(CameraNode node) {
        boolean accept = false;
        for (DrawList dl : drawLists)
            if (dl.accept(node))
                accept = true;

        if (accept) {
            transformStack.push();
            transformStack.mul(node.getTransform());

            for (DrawList dl : drawLists)
                dl.add(node, transformStack.peek(), prescanInfo);

            transformStack.pop();
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.ClipPlaneNode)
     */
    @Override
    public boolean visit(ClipPlaneNode node) {
        if (node.isEnabled()) {
            boolean accept = false;
            for (DrawList dl : drawLists)
                if (dl.accept(node))
                    accept = true;

            if (accept) {
                transformStack.push();
                transformStack.mul(node.getTransform());

                for (DrawList dl : drawLists)
                    dl.add(node, transformStack.peek(), prescanInfo);

                transformStack.pop();
            }

            return true;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.LightNode)
     */
    @Override
    public boolean visit(LightNode node) {
        if (node.isEnabled()) {
            boolean accept = false;
            for (DrawList dl : drawLists)
                if (dl.accept(node))
                    accept = true;

            if (accept) {
                transformStack.push();
                transformStack.mul(node.getTransform());

                for (DrawList dl : drawLists)
                    dl.add(node, transformStack.peek(), prescanInfo);

                transformStack.pop();
            }

            return true;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.ShapeNode)
     */
    @Override
    public boolean visit(ShapeNode node) {
        if (node.isEnabled()) {
            boolean accept = false;
            for (DrawList dl : drawLists)
                if (dl.accept(node))
                    accept = true;

            if (accept) {
                transformStack.push();
                transformStack.mul(node.getTransform());

                for (DrawList dl : drawLists)
                    dl.add(node, transformStack.peek(), prescanInfo);

                transformStack.pop();
            }

            return true;
        }
        return true;
    }
}
