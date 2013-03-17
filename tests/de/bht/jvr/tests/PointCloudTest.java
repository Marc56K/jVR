package de.bht.jvr.tests;

import de.bht.jvr.util.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import de.bht.jvr.core.AttributeCloud;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.attributes.AttributeVector3;
import de.bht.jvr.core.attributes.AttributeVector4;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.math.Vector4;
import de.bht.jvr.renderer.AwtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
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
public class PointCloudTest extends TestBase {
    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new PointCloudTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public PointCloudTest() throws Exception {
        GroupNode root = new GroupNode();

        ShapeNode cloudShape = new ShapeNode();
        cloudShape.setMaterial(new ShaderMaterial("AMBIENT", new ShaderProgram(new File("data/shader/points.vs"), new File("data/shader/points.fs"))));

        AttributeCloud cloud = new AttributeCloud(10000, GL.GL_LINE_LOOP); // <---
        // cloud.setBBox(new BBox(new Vector3(-1,-1,-1), new Vector3(1,1,1)));
        cloudShape.setGeometry(cloud);

        root.addChildNode(cloudShape);

        CameraNode cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 100));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.setBackFaceCulling(false);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, Color.white);
        p.drawGeometry("AMBIENT", null);
        // p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////
        RenderWindow w = new AwtRenderWindow(p, 1024, 768);
        w.addKeyListener(this);
        w.addMouseListener(this);

        Viewer viewer = new Viewer(w);

        List<Vector4> vertices = new ArrayList<Vector4>();
        List<Vector3> colors = new ArrayList<Vector3>();
        // boolean init = false;
        try {
            while (viewer.isRunning() && run) {
                long start = System.currentTimeMillis();

                // if(!init)
                {
                    for (int i = 0; i < 10000; i++) {
                        vertices.add(new Vector4(80 * (float) Math.random() - 40, 80 * (float) Math.random() - 40, 80 * (float) Math.random() - 40, 1));
                        colors.add(new Vector3((float) Math.random(), (float) Math.random(), (float) Math.random()));
                    }
                    cloud.setAttribute("jvr_Vertex", new AttributeVector4(vertices));
                    cloud.setAttribute("jvr_Color", new AttributeVector3(colors));
                    vertices.clear();
                    colors.clear();
                    // init = true;
                }
                viewer.display();
                move(System.currentTimeMillis() - start, 0.1);
            }
            viewer.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
