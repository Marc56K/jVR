package de.bht.jvr.core;

import java.util.Stack;
import java.util.regex.Pattern;

import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.math.Vector4;

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
 * The picker calculates a pick ray for mouse picking.
 * 
 * @author Marc Roßbach
 */

public class Picker implements Traverser {

    /**
     * Gets the pick ray.
     * 
     * @param root
     *            root of the scene graph
     * @param cam
     *            camera used for picking
     * @param nwx
     *            normalized x-window coordinate
     * @param nwy
     *            normalized y-window coordinate
     * @return calculated pick ray
     */
    public static PickRay getPickRay(SceneNode root, CameraNode cam, float nwx, float nwy) {
        Transform camWorldTrans = cam.getEyeWorldTransform(root);

        nwx = 2 * (nwx - 0.5f);
        nwy = -2 * (nwy - 0.5f);

        Vector4 p1 = new Vector4(nwx, nwy, 1, 1);
        Vector4 p2 = new Vector4(nwx, nwy, 0, 1);

        Matrix4 m = cam.getProjectionMatrix().inverse();

        p1 = m.mul(p1).homogenize();
        p2 = m.mul(p2).homogenize();
        Vector4 dir = p1.sub(p2);

        Vector3 o = camWorldTrans.getMatrix().translation();
        dir = camWorldTrans.getMatrix().mul(dir);

        return new PickRay(o, dir.xyz().normalize());
    }

    /**
     * Picks a shape node.
     * 
     * @param root
     *            root of the scene graph
     * @param cam
     *            camera used for picking
     * @param nwx
     *            normalized x-window coordinate
     * @param nwy
     *            normalized y-window coordinate
     * @param namePattern
     *            only pick shape nodes matching the pattern
     * @return the picking result
     */
    public static PickingResult pickShapeNode(SceneNode root, CameraNode cam, float nwx, float nwy, String namePattern) {
        return new Picker().pick(root, Picker.getPickRay(root, cam, nwx, nwy), namePattern);
    }

    /**
     * Picks a shape node.
     * 
     * @param root
     *            the root of the scene graph
     * @param ray
     *            the picking ray
     * @param namePattern
     *            only pick shape nodes matching the pattern
     * @return the picking result
     */
    public static PickingResult pickShapeNode(SceneNode root, PickRay ray, String namePattern) {
        return new Picker().pick(root, ray, namePattern);
    }

    /** The transform stack. */
    private TransformStack transformStack = new TransformStack();

    /** The pick ray. */
    private PickRay pickRay;

    /** The closest node. */
    private ShapeNode closestNode = null;

    /** The closest z dist. */
    private float closestZDist = 0;

    /** The pick point. */
    private Vector3 pickPoint = null;

    /** The name pattern. */
    private Pattern pattern;

    /** The parent stack. */
    private Stack<SceneNode> activeNodeStack = new Stack<SceneNode>();

    /** The parent stack to the closest shape node */
    private Stack<SceneNode> bestNodeStack = null;

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#enter(de.bht.jvr.core.GroupNode)
     */
    @Override
    public boolean enter(GroupNode node) {
        transformStack.push();
        transformStack.mul(node.getTransform());
        activeNodeStack.push(node);
        return node.isEnabled() && testIntersection(transformStack.peek(), node);
    }

    /**
     * Tests if picking ray intersects the bounding box. Shape nodes additional
     * tested for geometry intersections.
     * 
     * @param trans
     *            the world transformation of the node
     * @param node
     *            the node
     * @return true, if intersects bounding box
     */
    private boolean testIntersection(Transform trans, SceneNode node) {
        if (node.getBBox().isInfinite() && !(node instanceof ShapeNode)) {
            return true;
        }

        // http://www.ina-de-brabandt.de/vektoren/basis/abstand-punkt-gerade-lot.html
        Vector3 max = trans.getMatrix().mul(new Vector4(node.getBBox().getMax(), 1.0f)).homogenize().xyz();
        Vector3 center = trans.getMatrix().mul(new Vector4(node.getBBox().getCenter(), 1.0f)).homogenize().xyz();
        float radius = center.sub(max).length();
        Vector3 o = pickRay.getRayOrigin();
        Vector3 u = pickRay.getRayDirection().normalize();

        // plane equation
        float d = u.dot(center);

        // intersection point on plane = o + u * s
        float s = (d - u.dot(o)) / u.dot(u);

        if (s >= 0 || o.sub(center).length() < radius) {
            Vector3 f = o.add(u.mul(s));
            float dist = f.sub(center).length(); // distance of ray from sphere
                                                 // center
            if (dist < radius) // ray intersects bounding sphere
            {
                if (node instanceof ShapeNode) {
                    // transform pick ray to object space
                    Vector4 localOrigin = trans.getInverseMatrix().mul(new Vector4(pickRay.getRayOrigin(), 1));
                    Vector4 localDir = trans.getInverseMatrix().mul(new Vector4(pickRay.getRayDirection(), 0));
                    PickRay localRay = new PickRay(localOrigin.xyz(), localDir.xyz());

                    Vector3 pickPoint = ((ShapeNode) node).getGeometry().pick(localRay);

                    if (pickPoint != null) {
                        pickPoint = trans.getMatrix().mul(new Vector4(pickPoint, 1)).homogenize().xyz();
                        float zDist = pickRay.getRayOrigin().sub(pickPoint).length();
                        if (zDist < closestZDist || closestNode == null) {
                            this.closestZDist = zDist;
                            this.closestNode = (ShapeNode) node;
                            this.pickPoint = pickPoint;
                            this.bestNodeStack = new Stack<SceneNode>();
                            this.bestNodeStack.addAll(this.activeNodeStack);
                        }
                    }
                }
                return true; // is in bounding sphere
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
        activeNodeStack.pop();
        return true;
    }

    /**
     * Picks a shape node.
     * 
     * @param root
     *            the root of the scene graph
     * @param ray
     *            the picking ray
     * @param namePattern
     *            only pick shape nodes matching the pattern
     * @return the picking result
     */
    private PickingResult pick(SceneNode root, PickRay ray, String namePattern) {
        if (namePattern == null)
            namePattern = ".*";

        pattern = Pattern.compile(namePattern);

        pickRay = ray;
        root.accept(this);

        NodePath path = null;
        if (bestNodeStack != null && !bestNodeStack.isEmpty())
            path = new NodePath(bestNodeStack);

        return new PickingResult(closestNode, pickPoint, path);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.CameraNode)
     */
    @Override
    public boolean visit(CameraNode node) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.ClipPlaneNode)
     */
    @Override
    public boolean visit(ClipPlaneNode node) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.LightNode)
     */
    @Override
    public boolean visit(LightNode node) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Traverser#visit(de.bht.jvr.core.ShapeNode)
     */
    @Override
    public boolean visit(ShapeNode node) {
        if (node.isEnabled() && pattern.matcher(node.getName()).matches()) {
            transformStack.push();
            transformStack.mul(node.getTransform());

            testIntersection(transformStack.peek(), node);

            transformStack.pop();
        }
        return true;
    }
}
