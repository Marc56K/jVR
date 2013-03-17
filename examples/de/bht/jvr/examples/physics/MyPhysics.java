package de.bht.jvr.examples.physics;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.jogamp.common.nio.Buffers;

import de.bht.jvr.core.PickRay;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.TriangleMesh;
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
 */

public class MyPhysics {
    /**
     * Creates a static concave mesh shape.
     * 
     * @param shape
     * @return
     */
    public static BvhTriangleMeshShape makeBvhTriangleMeshShape(ShapeNode shape) {
        return new BvhTriangleMeshShape(makeTriangleIndexVertexArray((TriangleMesh) shape.getGeometry(), shape.getTransform().getMatrix()), true);
    }

    /**
     * Creates a dynamic concave mesh shape.
     * 
     * @param shape
     * @return
     */
    public static GImpactMeshShape makeGImpactMeshShape(ShapeNode shape) {
        GImpactMeshShape trimesh = new GImpactMeshShape(makeTriangleIndexVertexArray((TriangleMesh) shape.getGeometry(), shape.getTransform().getMatrix()));
        trimesh.updateBound();

        return trimesh;
    }

    public static TriangleIndexVertexArray makeTriangleIndexVertexArray(TriangleMesh geo, Matrix4 trans) {
        ByteBuffer indices = Buffers.copyIntBufferAsByteBuffer(Buffers.newDirectIntBuffer(geo.getIndices()));
        indices.rewind();

        List<Vector4> vertices = geo.getVertices();
        FloatBuffer vFloatBuf = Buffers.newDirectFloatBuffer(vertices.size() * 3);
        for (Vector4 v : vertices) {
            v = trans.mul(v).homogenize();
            vFloatBuf.put(v.x());
            vFloatBuf.put(v.y());
            vFloatBuf.put(v.z());
        }
        vFloatBuf.rewind();
        ByteBuffer vByteBuf = Buffers.copyFloatBufferAsByteBuffer(vFloatBuf);
        vByteBuf.rewind();

        return new TriangleIndexVertexArray(geo.getIndicesCount() / 3, indices, 3 * (Integer.SIZE / 8), vertices.size(), vByteBuf, 3 * (Float.SIZE / 8));
    }

    public static Vector3f toV3f(Vector3 v) {
        return new Vector3f(v.x(), v.y(), v.z());
    }

    private DiscreteDynamicsWorld dynamicsWorld;

    private List<CollisionShape> collisionShapes;

    private PickRay pickRay = null;

    private static RigidBody pickedBody = null; // for deactivation state

    private TypedConstraint pickConstraint = null; // constraint for mouse picking

    public MyPhysics() {
        // collision configuration contains default setup for memory, collision
        // setup. Advanced users can create their own configuration.
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

        // use the default collision dispatcher. For parallel processing you
        // can use a diffent dispatcher (see Extras/BulletMultiThreaded)
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

        // the maximum size of the collision world. Make sure objects stay
        // within these boundaries
        // Don't make the world BBox size too large, it will harm simulation
        // quality and performance
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        int maxProxies = 1024;
        AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
        // BroadphaseInterface overlappingPairCache = new SimpleBroadphase(
        // maxProxies);

        // the default constraint solver. For parallel processing you can use a
        // different solver (see Extras/BulletMultiThreaded)
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);

        dynamicsWorld.setGravity(new Vector3f(0, -10, 0));

        // keep track of the shapes, we release memory at exit.
        // make sure to re-use collision shapes among rigid bodies whenever
        // possible!
        collisionShapes = new ArrayList<CollisionShape>();
    }

    public void addGroundPlane(float yPos) {
        CollisionShape groundShape = new BoxShape(new Vector3f(10000.f, 50.f, 10000.f));

        collisionShapes.add(groundShape);

        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        groundTransform.origin.set(new Vector3f(0.f, -50.f + yPos, 0.f));

        // using motionstate is recommended, it provides interpolation
        // capabilities, and only synchronizes 'active' objects
        DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, myMotionState, groundShape, new Vector3f(0, 0, 0));
        RigidBody body = new RigidBody(rbInfo);

        // add the body to the dynamics world
        dynamicsWorld.addRigidBody(body);
    }

    public void addRigidBody(SceneNode node, CollisionShape colShape, float mass) {
        collisionShapes.add(colShape);

        // Create Dynamic Objects
        Transform startTransform = new Transform();
        startTransform.setIdentity();

        // rigidbody is dynamic if and only if mass is non zero,
        // otherwise static
        boolean isDynamic = mass != 0f;

        Vector3f localInertia = new Vector3f(0, 0, 0);
        if (isDynamic)
            colShape.calculateLocalInertia(mass, localInertia);

        // a motion state is a link between the graphic and the physics
        MyMotionState myMotionState = new MyMotionState(node);

        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);
        RigidBody body = new RigidBody(rbInfo);
        dynamicsWorld.addRigidBody(body);
    }

    public void drag() {
        Vector3f rayTo = toV3f(pickRay.getRayOrigin().add(pickRay.getRayDirection().mul(1e10f)));
        Vector3f rayFrom = toV3f(pickRay.getRayOrigin());

        if (pickConstraint != null) {
            // move the constraint pivot
            Point2PointConstraint p2p = (Point2PointConstraint) pickConstraint;
            if (p2p != null) {
                // keep it at the same picking distance

                Vector3f newRayTo = rayTo;
                Vector3f fromPos = new Vector3f(rayFrom);
                Vector3f dir = new Vector3f();
                dir.sub(newRayTo, fromPos);
                dir.normalize();
                dir.scale(BulletStats.gOldPickingDist);

                Vector3f newPos = new Vector3f();
                newPos.add(fromPos, dir);
                p2p.setPivotB(newPos);
            }
        }
    }

    public void pick() {
        Vector3f rayTo = toV3f(pickRay.getRayOrigin().add(pickRay.getRayDirection().mul(1e10f)));
        Vector3f rayFrom = toV3f(pickRay.getRayOrigin());
        // add a point to point constraint for picking
        if (dynamicsWorld != null) {
            CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(rayFrom, rayTo);
            dynamicsWorld.rayTest(rayFrom, rayTo, rayCallback);
            if (rayCallback.hasHit()) {
                RigidBody body = RigidBody.upcast(rayCallback.collisionObject);

                if (body != null)
                    // other exclusions?
                    if (!(body.isStaticObject() || body.isKinematicObject())) {
                        pickedBody = body;
                        pickedBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

                        Vector3f pickPos = new Vector3f(rayCallback.hitPointWorld);

                        Transform tmpTrans = body.getCenterOfMassTransform(new Transform());
                        tmpTrans.inverse();
                        Vector3f localPivot = new Vector3f(pickPos);
                        tmpTrans.transform(localPivot);

                        Point2PointConstraint p2p = new Point2PointConstraint(body, localPivot);
                        dynamicsWorld.addConstraint(p2p);
                        pickConstraint = p2p;
                        // save mouse position for dragging
                        BulletStats.gOldPickingPos.set(rayTo);
                        Vector3f fromPos = new Vector3f(rayFrom);
                        Vector3f tmp = new Vector3f();
                        tmp.sub(pickPos, fromPos);
                        BulletStats.gOldPickingDist = tmp.length();
                        // very weak constraint for picking
                        p2p.setting.tau = 0.1f;
                    }
            }
        }
    }

    public void pickOrDrag() {
        if (pickedBody != null)
            drag();
        else
            pick();
    }

    public void setPickRay(PickRay pickRay) {
        this.pickRay = pickRay;
    }

    public void unPick() {
        if (pickConstraint != null && dynamicsWorld != null) {
            dynamicsWorld.removeConstraint(pickConstraint);
            pickConstraint = null;
            pickedBody.forceActivationState(CollisionObject.ACTIVE_TAG);
            pickedBody.setDeactivationTime(0f);
            pickedBody = null;
        }
    }

    public void update(double renderDuration) {
        dynamicsWorld.stepSimulation((float) (0.001f * renderDuration), 10);
    }
}
