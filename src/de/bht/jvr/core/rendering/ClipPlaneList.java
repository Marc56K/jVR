package de.bht.jvr.core.rendering;

import java.util.ArrayList;
import java.util.List;

import de.bht.jvr.core.ClipPlaneNode;
import de.bht.jvr.core.Context;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.PipelineState;
import de.bht.jvr.core.pipeline.PrescanInfo;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformVector4;
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
 * The clipping plane list contains all clipping planes for the next frame.
 * 
 * @author Marc Roßbach
 */
public class ClipPlaneList extends DrawList {
    /** The clipping plane list. */
    private List<Transform> clipPlaneList = new ArrayList<Transform>();

    @Override
    public boolean accept(ClipPlaneNode node) {
        return true;
    }

    @Override
    public void add(ClipPlaneNode node, Transform worldTrans, PrescanInfo prescanInfo) {
        addClipPlaneTransform(worldTrans);
    }

    public void addClipPlaneTransform(Transform transform) {
        clipPlaneList.add(transform);
    }

    public void bind(Context ctx, PipelineState pipelineState) {
        ctx.setClipPlaneCount(this.clipPlaneList.size());
        int count = 0;
        for (Transform trans : clipPlaneList) {
            trans = pipelineState.getActiveCamTransform().invert().mul(trans); // transform
                                                                               // to
                                                                               // camera
                                                                               // space
            Matrix4 m = trans.getMatrix();
            Vector3 pos = m.translation();

            Vector4 n = m.mul(new Vector4(0, 0, -1, 0)).normalize();
            float d = n.x() * pos.x() + n.y() * pos.y() + n.z() * pos.z();

            Vector4 plane = new Vector4(n.x(), n.y(), n.z(), d);

            pipelineState.setClipPlane("jvr_ClipPlane" + count, new UniformVector4(plane));
            pipelineState.setClipPlane("jvr_UseClipPlane" + count, new UniformBool(true));

            count++;
        }
    }
}
