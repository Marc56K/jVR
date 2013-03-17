package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

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
 * A type-safe traverser for finding nodes by type and name.
 * 
 * @author Henrik Tramberend
 * @author Marc Roßbach
 */
public class Finder implements Traverser {
    /**
     * Finds a scene node in the scene graph.
     * 
     * @param <R>
     *            the generic type
     * @param root
     *            the scene root
     * @param c
     *            the node type
     * @param namePattern
     *            the name pattern
     * @return the found node
     */
    static public <R extends SceneNode> R find(SceneNode root, Class<R> c, String namePattern) {
        return new Finder(root).find(c, namePattern);
    }

    /**
     * Finds all scene nodes which matches the name pattern.
     * 
     * @param <R>
     *            the generic type
     * @param root
     *            the scene root
     * @param c
     *            the node type
     * @param namePattern
     *            the name pattern
     * @return the found nodes
     */
    static public <R extends SceneNode> Collection<R> findAll(SceneNode root, Class<R> c, String namePattern) {
        return new Finder(root).findAll(c, namePattern);
    }

    /**
     * Finds all geometries of shape nodes which matches the name pattern.
     * 
     * @param root
     *            the scene root
     * @param namePattern
     *            the name pattern
     * @return the collection
     */
    static public Collection<Geometry> findAllGeometries(SceneNode root, String namePattern) {
        Collection<ShapeNode> shapes = new Finder(root).findAll(ShapeNode.class, namePattern);
        Collection<Geometry> geos = new ArrayList<Geometry>();
        for (ShapeNode shape : shapes) {
            Geometry geo = shape.getGeometry();
            if (geo != null)
                geos.add(geo);
        }

        return geos;
    }

    /**
     * Finds all materials of shape nodes which matches the name pattern.
     * 
     * @param root
     *            the root
     * @param namePattern
     *            the name pattern
     * @return the collection
     */
    static public Collection<Material> findAllMaterials(SceneNode root, String namePattern) {
        Collection<ShapeNode> shapes = new Finder(root).findAll(ShapeNode.class, namePattern);
        Collection<Material> materials = new ArrayList<Material>();
        for (ShapeNode shape : shapes) {
            Material mat = shape.getMaterial();
            if (mat != null)
                materials.add(mat);
        }

        return materials;
    }

    /**
     * Finds the geometry of shape node which matches the name pattern.
     * 
     * @param root
     *            the scene root
     * @param namePattern
     *            the name pattern
     * @return the geometry
     */
    static public Geometry findGeometry(SceneNode root, String namePattern) {
        ShapeNode shape = new Finder(root).find(ShapeNode.class, namePattern);
        if (shape != null)
            return shape.getGeometry();

        return null;
    }

    /**
     * Finds the material of shape node which matches the name pattern.
     * 
     * @param root
     *            the scene root
     * @param namePattern
     *            the name pattern
     * @return the material
     */
    static public Material findMaterial(SceneNode root, String namePattern) {
        ShapeNode shape = new Finder(root).find(ShapeNode.class, namePattern);
        if (shape != null)
            return shape.getMaterial();

        return null;
    }

    /** The scene root. */
    private SceneNode root;

    /** The node type. */
    private Class<? extends SceneNode> clazz;

    /** The name pattern. */
    private Pattern pattern;

    /** The found node. */
    private SceneNode found = null;

    /** List of found nodes. */
    private List<SceneNode> allFound;

    /** Seach all. */
    private boolean all;

    /**
     * Instantiates a new finder.
     * 
     * @param root
     *            the root
     */
    public Finder(SceneNode root) {
        this.root = root;
    }

    /**
     * Checks the scene node whether it's matching the name pattern .
     * 
     * @param node
     *            the node
     * @return true, if successful
     */
    public boolean check(SceneNode node) {
        if (clazz.isInstance(node) && pattern.matcher(node.getName()).matches()) {
            if (all) {
                if (!allFound.contains(node))
                    allFound.add(node);
                return true;
            } else {
                found = node;
                return false;
            }
        } else
            return !all && found == null || all;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#enter(de.bht.jvr.core.GroupNode)
     */
    @Override
    public boolean enter(GroupNode node) {
        return check(node);
    }

    /*--------------------------------------------------------------------------------------*/

    /**
     * Finds a scene node in the scene graph.
     * 
     * @param <R>
     *            the generic type
     * @param c
     *            the node type
     * @param namePattern
     *            the name pattern
     * @return the found node
     */
    public <R extends SceneNode> R find(Class<R> c, String namePattern) {
        clazz = c;

        if (namePattern == null)
            namePattern = ".*";

        pattern = Pattern.compile(namePattern);

        found = null;
        all = false;
        root.accept(this);

        return c.cast(found);
    }

    /**
     * Finds all scene nodes which matches the name pattern.
     * 
     * @param <R>
     *            the generic type
     * @param c
     *            the node type
     * @param namePattern
     *            the name pattern
     * @return the found nodes
     */
    @SuppressWarnings({ "unchecked" })
    public <R extends SceneNode> Collection<R> findAll(Class<R> c, String namePattern) {
        clazz = c;

        if (namePattern == null)
            namePattern = ".*";

        pattern = Pattern.compile(namePattern);

        allFound = new ArrayList<SceneNode>();
        all = true;
        root.accept(this);

        return (Collection<R>) allFound;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#leave(de.bht.jvr.core.GroupNode)
     */
    @Override
    public boolean leave(GroupNode node) {
        return check(node);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.CameraNode)
     */
    @Override
    public boolean visit(CameraNode node) {
        return check(node);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.ClipPlaneNode)
     */
    @Override
    public boolean visit(ClipPlaneNode node) {
        return check(node);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.LightNode)
     */
    @Override
    public boolean visit(LightNode node) {
        return check(node);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.ShapeNode)
     */
    @Override
    public boolean visit(ShapeNode node) {
        return check(node);
    }
}
