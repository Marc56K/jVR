package de.bht.jvr.modelviewer.model;

import de.bht.jvr.util.Color;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.BBox;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.SpotLightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.math.Vector2;
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
 */

public class Scene {
    private Set<SceneListener> sceneListener = new HashSet<SceneListener>();
    private Pipeline pipeline;
    private GroupNode sceneGraph;
    private CameraNode cam;
    private Transform centerTransform = new Transform();
    private SpotLightNode spotLight;
    private float xAngle = 0;
    private float yAngle = 0;
    private float scale = 1;

    public Scene() {
        sceneGraph = new GroupNode("sceneRoot");

        cam = new CameraNode("camera", 4f / 3f, 60);
        cam.setTransform(Transform.translate(0, 0, 1));

        spotLight = new SpotLightNode("spotLight");
        spotLight.setTransform(Transform.translate(10, 10, 10).mul(Transform.rotateYDeg(45)).mul(Transform.rotateXDeg(-45)));

        GroupNode root = new GroupNode("root");
        root.addChildNodes(cam, spotLight, sceneGraph);

        pipeline = makePipeline(cam, root);
    }

    public void addSceneListener(SceneListener listener) {
        sceneListener.add(listener);
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public SceneNode getSceneGraph() {
        return sceneGraph;
    }

    public void loadScene(String path) throws Exception {
        sceneGraph.removeAllChildNodes();
        SceneNode scene = ColladaLoader.load(new File(path));
        sceneGraph.addChildNode(scene);

        spotLight.setEnabled(Finder.find(sceneGraph, LightNode.class, null) == null);

        updateCenterTransform();
    }

    private Pipeline makePipeline(CameraNode cam, SceneNode root) {
        Pipeline p = new Pipeline(root);
        p.setBackFaceCulling(false); // disable back face culling
        p.switchCamera(cam);
        p.clearBuffers(true, true, Color.gray); // clear screen
        // render only opak objects
        p.drawGeometry("AMBIENT", "(?!Translucent).*", false);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "(?!Translucent).*");
        // now render the transparent objects
        p.drawGeometry("AMBIENT", "Translucent", true);
        p.doLightLoop(true, true).drawGeometry("LIGHTING", "Translucent");
        return p;
    }

    private void notifySceneListener() {
        for (SceneListener l : sceneListener)
            l.sceneUpdated();
    }

    public void setCameraAspect(float aspectRatio) {
        cam.setAspectRatio(aspectRatio);
    }

    private void updateCenterTransform() {
        xAngle = 0;
        yAngle = 0;
        scale = 1;

        BBox bBox = sceneGraph.getBBox();
        Vector3 center = bBox.getCenter();
        Vector3 max = bBox.getMax();
        float radius = center.sub(max).length();
        centerTransform = Transform.scale(1f / (2f * radius)).mul(Transform.translate(center.neg()));

        updateTransform();
    }

    public void updateRotation(Vector2 deltaPos) {
        xAngle -= deltaPos.x();
        yAngle -= deltaPos.y();

        updateTransform();
    }

    private void updateTransform() {
        sceneGraph.setTransform(Transform.rotateXDeg(yAngle).mul(Transform.rotateYDeg(xAngle)).mul(Transform.scale(scale)).mul(centerTransform));

        notifySceneListener();
    }

    public void updateZoom(Vector2 deltaPos) {
        scale += deltaPos.y() * 0.01f;
        if (scale < 0.1f)
            scale = 0.1f;

        updateTransform();
    }
}
