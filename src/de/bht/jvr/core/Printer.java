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
 * The printer prints the scene graph to the console.
 * 
 * @author Henrik Tramberend
 */

public class Printer implements Traverser {
    /**
     * Prints the scene node of the scene graph.
     * 
     * @param root
     *            the root
     */
    static public void print(SceneNode root) {
        new Printer(root).print();
    }

    /** The indent. */
    private final String indent = "    ";

    /** The root. */
    private SceneNode root;

    /** The level. */
    private int level = 0;

    /** The indentation. */
    private String indentation = "";

    /**
     * Instantiates a new printer.
     * 
     * @param root
     *            the root
     */
    public Printer(SceneNode root) {
        this.root = root;
    }

    /**
     * Calculate indentation.
     */
    private void calcIndentation() {
        indentation = "";
        for (int i = 0; i != level; i++)
            indentation += indent;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#enter(de.bht.jvr.core.GroupNode)
     */
    @Override
    public boolean enter(GroupNode node) {
        System.out.println(indentation + node + " [" + node.getClass().getSimpleName() + " " + node.getBBox() + "]");
        System.out.println(indentation + "{");
        level++;
        calcIndentation();
        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#leave(de.bht.jvr.core.GroupNode)
     */
    @Override
    public boolean leave(GroupNode node) {
        level--;
        calcIndentation();

        System.out.println(indentation + "}");

        return true;
    }

    /**
     * Prints the scene node of the scene graph.
     */
    public void print() {
        root.accept(this);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.CameraNode)
     */
    @Override
    public boolean visit(CameraNode node) {
        System.out.println(indentation + node + " [" + node.getClass().getSimpleName() + " " + node.getBBox() + "]");
        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.ClipPlaneNode)
     */
    @Override
    public boolean visit(ClipPlaneNode node) {
        System.out.println(indentation + node + " [" + node.getClass().getSimpleName() + " " + node.getBBox() + "]");
        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.LightNode)
     */
    @Override
    public boolean visit(LightNode node) {
        System.out.println(indentation + node + " [" + node.getClass().getSimpleName() + " " + node.getBBox() + "]");
        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.ShapeNode)
     */
    @Override
    public boolean visit(ShapeNode node) {
        System.out.println(indentation + node + " [" + node.getClass().getSimpleName() + " " + node.getBBox() + "]");
        return true;
    }
}
