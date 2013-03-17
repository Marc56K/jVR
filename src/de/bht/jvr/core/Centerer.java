package de.bht.jvr.core;

import java.util.ArrayList;

import de.bht.jvr.math.Vector3;

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
 * This class centers the Geometry under a SceneNode. example: GroupNode root =
 * new GroupNode("root"); SceneNode scene = ColladaLoader.load(...);
 * scene.setTransform
 * (scene.getTransform().mul(Centerer.getCenteredTransform(scene)));
 * root.addChild(scene);
 * 
 * @author Thomas Grottker
 */
public class Centerer {

    /**
     * Centers a SceneNode to a given center. The center of the Geometry is in
     * its center.
     * 
     * @param sn
     *            The given SceneNode.
     * @param center
     *            The given Center.
     * @return
     */
    public static Transform centerToCenter(SceneNode sn, Vector3 center) {
        Vector3 trans = getCenter(sn);
        Vector3 ret = new Vector3(center.x() - trans.x(), center.y() - trans.y(), center.z() - trans.z());
        return Transform.translate(ret);
    }

    /**
     * Centers a SceneNode to the point of origin. The center of the Geometry is
     * in its center.
     * 
     * @param sn
     *            The given SceneNode.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToCenter(SceneNode sn) {
        return centerToCenter(sn, new Vector3(0, 0, 0));
    }

    /**
     * Centers a SceneNode to a given center. The center of the Geometry is in
     * its bottom.
     * 
     * @param sn
     *            The given SceneNode.
     * @param center
     *            The given Center.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToBottom(SceneNode sn, Vector3 center) {
        Vector3 trans = getBottom(sn);
        Vector3 ret = new Vector3(center.x() - trans.x(), center.y() - trans.y(), center.z() - trans.z());
        return Transform.translate(ret);
    }

    /**
     * Centers a SceneNode to the point of origin. The center of the Geometry is
     * in its bottom plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToBottom(SceneNode sn) {
        return centerToBottom(sn, new Vector3(0, 0, 0));
    }

    /**
     * Centers a SceneNode to a given center. The center of the Geometry is in
     * its top plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @param center
     *            The given Center.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToTop(SceneNode sn, Vector3 center) {
        Vector3 trans = getTop(sn);
        Vector3 ret = new Vector3(center.x() - trans.x(), center.y() - trans.y(), center.z() - trans.z());
        return Transform.translate(ret);
    }

    /**
     * Centers a SceneNode to the point of origin. The center of the Geometry is
     * in its top plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToTop(SceneNode sn) {
        return centerToTop(sn, new Vector3(0, 0, 0));
    }

    /**
     * Centers a SceneNode to a given center. The center of the Geometry is in
     * its left plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @param center
     *            The given Center.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToLeft(SceneNode sn, Vector3 center) {
        Vector3 trans = getLeft(sn);
        Vector3 ret = new Vector3(center.x() - trans.x(), center.y() - trans.y(), center.z() - trans.z());
        return Transform.translate(ret);
    }

    /**
     * Centers a SceneNode to the point of origin. The center of the Geometry is
     * in its left plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToLeft(SceneNode sn) {
        return centerToLeft(sn, new Vector3(0, 0, 0));
    }

    /**
     * Centers a SceneNode to a given center. The center of the Geometry is in
     * its right plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @param center
     *            The given Center.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToRight(SceneNode sn, Vector3 center) {
        Vector3 trans = getRight(sn);
        Vector3 ret = new Vector3(center.x() - trans.x(), center.y() - trans.y(), center.z() - trans.z());
        return Transform.translate(ret);
    }

    /**
     * Centers a SceneNode to the point of origin. The center of the Geometry is
     * in its right plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToRight(SceneNode sn) {
        return centerToRight(sn, new Vector3(0, 0, 0));
    }

    /**
     * Centers a SceneNode to a given center. The center of the Geometry is in
     * its far plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @param center
     *            The given Center.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToFar(SceneNode sn, Vector3 center) {
        Vector3 trans = getFar(sn);
        Vector3 ret = new Vector3(center.x() - trans.x(), center.y() - trans.y(), center.z() - trans.z());
        return Transform.translate(ret);
    }

    /**
     * Centers a SceneNode to the point of origin. The center of the Geometry is
     * in its far plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToFar(SceneNode sn) {
        return centerToFar(sn, new Vector3(0, 0, 0));
    }

    /**
     * Centers a SceneNode to a given center. The center of the Geometry is in
     * its near plane.
     * 
     * @param sn
     *            The given SceneNode.
     * @param center
     *            The given Center.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToNear(SceneNode sn, Vector3 center) {
        Vector3 trans = getNear(sn);
        Vector3 ret = new Vector3(center.x() - trans.x(), center.y() - trans.y(), center.z() - trans.z());
        return Transform.translate(ret);
    }

    /**
     * Centers a SceneNode to the point of origin. The center of the Geometry is
     * in its center.
     * 
     * @param sn
     *            The given SceneNode.
     * @return The Transform for centering the Scene.
     */
    public static Transform centerToNear(SceneNode sn) {
        return centerToNear(sn, new Vector3(0, 0, 0));
    }

    /**
     * Returns a Vector3, which represents the center of the Geometry.
     * 
     * @param sn
     *            The given SceneNode.
     * @return A Vector3, representing the center.
     */
    private static Vector3 getCenter(SceneNode sn) {
        Float[] ext = getMaxAndMin(sn);
        return new Vector3((ext[0] + ext[1]) * 0.5f, (ext[2] + ext[3]) * 0.5f, (ext[4] + ext[5]) * 0.5f);
    }

    /**
     * Returns a Vector3, which represents the center on the bottom plane of the
     * Geometry.
     * 
     * @param sn
     *            The given SceneNode.
     * @return A Vector3, representing the center on the bottom plane.
     */
    private static Vector3 getBottom(SceneNode sn) {
        Float[] ext = getMaxAndMin(sn);
        return new Vector3((ext[0] + ext[1] * 0.5f), ext[3], (ext[4] + ext[5]) * 0.5f);
    }

    /**
     * Returns a Vector3, which represents the center on the top plane of the
     * Geometry.
     * 
     * @param sn
     *            The given SceneNode.
     * @return A Vecto, representing the center on the top plane.
     */
    private static Vector3 getTop(SceneNode sn) {
        Float[] ext = getMaxAndMin(sn);
        return new Vector3((ext[0] + ext[1] * 0.5f), ext[2], (ext[4] + ext[5]) * 0.5f);
    }

    /**
     * Returns a Vector3, which represents the center on the left plane of the
     * Geometry.
     * 
     * @param sn
     *            The given SceneNode.
     * @return A Vector3, representing the center on the left plane.
     */
    private static Vector3 getLeft(SceneNode sn) {
        Float[] ext = getMaxAndMin(sn);
        return new Vector3(ext[1], (ext[2] + ext[3]) * 0.5f, (ext[4] + ext[5]) * 0.5f);
    }

    /**
     * Returns a Vector3, which represents the center on the right plane of the
     * Geometry.
     * 
     * @param sn
     *            The given SceneNode.
     * @return A Vector3, representing the center on the right plane.
     */
    private static Vector3 getRight(SceneNode sn) {
        Float[] ext = getMaxAndMin(sn);
        return new Vector3(ext[0], (ext[2] + ext[3]) * 0.5f, (ext[4] + ext[5]) * 0.5f);
    }

    /**
     * Returns a Vector3, which represents the center on the far plane of the
     * Geometry.
     * 
     * @param sn
     *            The given SceneNode.
     * @return A Vector3, representing the center on the far plane.
     */
    private static Vector3 getFar(SceneNode sn) {
        Float[] ext = getMaxAndMin(sn);
        return new Vector3((ext[0] + ext[1]) * 0.5f, (ext[2] + ext[3]) * 0.5f, ext[5]);
    }

    /**
     * Returns a Vector3, which represents the center on the near plane of the
     * Geometry.
     * 
     * @param sn
     *            The given SceneNode.
     * @return A Vector3, representing the center on the near plane.
     */
    private static Vector3 getNear(SceneNode sn) {
        Float[] ext = getMaxAndMin(sn);
        return new Vector3((ext[0] + ext[1]) * 0.5f, (ext[2] + ext[3]) * 0.5f, ext[4]);
    }

    /**
     * Returns a Float-Array, where the mins and maxes of the Geometry under the
     * SceneNode are contained. The form is {maxX, minX, maxY, minY, maxZ, minZ}
     * 
     * @param sn
     *            The given SceneNode.
     * @return A Float-Array, where the mins and maxes are contained.
     */
    private static Float[] getMaxAndMin(SceneNode sn) {
        ArrayList<Geometry> geos = (ArrayList<Geometry>) Finder.findAllGeometries(sn, null);
        if (geos == null || geos.isEmpty()) {
            return null;
        }
        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        for (Geometry elem : geos) {
            vertices.add(elem.getBBox().getCenter());
        }
        float minX = vertices.get(0).x();
        float minY = vertices.get(0).y();
        float minZ = vertices.get(0).z();
        float maxX = vertices.get(0).x();
        float maxY = vertices.get(0).y();
        float maxZ = vertices.get(0).z();
        for (Vector3 elem : vertices) {
            minX = Math.min(elem.x(), minX);
            minY = Math.min(elem.y(), minY);
            minZ = Math.min(elem.z(), minZ);
            maxX = Math.max(elem.x(), maxX);
            maxY = Math.max(elem.y(), maxY);
            maxZ = Math.max(elem.z(), maxZ);
        }
        return new Float[] { maxX, minX, maxY, minY, maxZ, minZ };
    }

}