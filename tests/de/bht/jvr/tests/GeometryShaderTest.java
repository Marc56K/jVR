package de.bht.jvr.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.AttributeCloud;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.attributes.AttributeFloat;
import de.bht.jvr.core.attributes.AttributeVector3;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.renderer.AwtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;
import de.bht.jvr.util.Color;
import de.bht.jvr.util.StopWatch;
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
public class GeometryShaderTest extends TestBase {

    private static final int count = 10000;

    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter());
        try {
            new GeometryShaderTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Random random = new Random();

    private float randomValue(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    private Vector3 randomVector3(Vector3 min, Vector3 max) {
        return new Vector3(randomValue(min.x(), max.x()), randomValue(min.y(), max.y()), randomValue(min.z(), max.z()));
    }

    public GeometryShaderTest() throws Exception {
        GroupNode root = new GroupNode();

        ShapeNode emitter = new ShapeNode("Emitter");

        AttributeCloud cloud = new AttributeCloud(count, GL.GL_POINTS);
        ShaderProgram shader = new ShaderProgram(new File("data/shader/sparks.vs"), new File("data/shader/sparks.gs"), new File("data/shader/sparks.fs"));

        shader.setParameter(GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, GL.GL_POINTS);
        shader.setParameter(GL2GL3.GL_GEOMETRY_OUTPUT_TYPE_ARB, GL2.GL_QUADS);
        shader.setParameter(GL2GL3.GL_GEOMETRY_VERTICES_OUT_ARB, 4);

        ShaderMaterial material = new ShaderMaterial("AMBIENT", shader);
        material.setMaterialClass("PARTICLE");

        emitter.setGeometry(cloud);
        emitter.setMaterial(material);

        root.addChildNode(emitter);

        ArrayList<Vector3> position = new ArrayList<Vector3>(count);
        for (int i = 0; i != count; i++)
            position.add(new Vector3(0, 0, 0));

        ArrayList<Vector3> velocity = new ArrayList<Vector3>(count);
        for (int i = 0; i != count; i++)
            velocity.add(new Vector3(0, 0, 0));

        ArrayList<Float> energy = new ArrayList<Float>(count);
        for (int i = 0; i != count; i++)
            energy.add(0.0f);

        ArrayList<Float> age = new ArrayList<Float>(count);
        for (int i = 0; i != count; i++)
            age.add(0.0f);

        cloud.setAttribute("partPosition", new AttributeVector3(position));
        cloud.setAttribute("partVelocity", new AttributeVector3(velocity));
        cloud.setAttribute("partEnergy", new AttributeFloat(energy));

        Vector3 gravity = new Vector3(0, -10, 0);
        float damping = 0.9f;

        Vector3 minVel = new Vector3(-1, 5, -1);
        Vector3 maxVel = new Vector3(1, 10, 1);

        float minEnergy = 2.0f;
        float maxEnergy = 4.0f;

        material.setUniform("AMBIENT", "maxEnergy", new UniformFloat(maxEnergy));

        float emmitRate = 1000;

        float halfLife = 0.5f;
        float initEnergy = 1f;

        CameraNode cam1 = new CameraNode("cam1", 4f / 3f, 60f);
        cam1.setTransform(Transform.translate(0, 0, 3));
        root.addChildNode(cam1);
        cams.add(cam1);

        // Pipeline//////////////////////////////////////
        Pipeline p = new Pipeline(root);
        p.setBackFaceCulling(false);
        p.switchCamera(cam1);
        p.clearBuffers(true, true, Color.black);
        p.drawGeometry("AMBIENT", null);
        // p.doLightLoop(true, true).drawGeometry("LIGHTING", null);

        // /////////////////////////////////////////////
        RenderWindow w = new AwtRenderWindow(p, 1024, 768);
        w.addKeyListener(this);
        w.addMouseListener(this);

        Viewer viewer = new Viewer(w);

        StopWatch time = new StopWatch();
        while (viewer.isRunning()) {
            float dt = time.elapsed();

            float toEmmit = (int) (emmitRate * dt);

            for (int i = 0; i != count; i++) {

                // Emmit if neccessary and possible
                if (energy.get(i) <= 0.1f && toEmmit != 0) {
                    position.set(i, new Vector3(0, 0, 0));
                    velocity.set(i, randomVector3(minVel, maxVel));
                    energy.set(i, (float) 1);
                    age.set(i, (float) 0);
                    toEmmit -= 1;
                }

                // Simulate
                age.set(i, age.get(i) + dt);
                velocity.set(i, velocity.get(i).add(gravity.mul(dt)));
                position.set(i, position.get(i).add(velocity.get(i).mul(dt)));
                energy.set(i, (float) (initEnergy * Math.pow(0.5f, age.get(i) / halfLife)));
            }
            // Set
            cloud.setAttribute("partPosition", new AttributeVector3(position));
            cloud.setAttribute("partVelocity", new AttributeVector3(velocity));
            cloud.setAttribute("partEnergy", new AttributeFloat(energy));

            viewer.display();
            move(dt, 0.1);
        }
        viewer.close();
    }
}
