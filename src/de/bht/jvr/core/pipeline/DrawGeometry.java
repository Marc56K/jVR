package de.bht.jvr.core.pipeline;

import java.util.regex.Pattern;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.Context;

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

public class DrawGeometry implements PipelineCommand {
    private String shaderContext;
    private Pattern materialClass;
    private boolean orderBackToFront;

    public DrawGeometry(String shaderContext, String materialClass, boolean orderBackToFront) {
        this.shaderContext = shaderContext;
        this.orderBackToFront = orderBackToFront;

        if (materialClass == null)
            materialClass = ".*";

        this.materialClass = Pattern.compile(materialClass);
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        GL2GL3 gl = ctx.getGL();
        
        // set render target
        ctx.bindFbo(pipelineState.getActiveFbo());

        // set back face culling
        gl.glCullFace(GL.GL_BACK);
        if (pipelineState.getBackFaceCulling())
            gl.glEnable(GL.GL_CULL_FACE);
        else
            gl.glDisable(GL.GL_CULL_FACE);

        // set front faces
        if (pipelineState.getFrontFace())
            gl.glFrontFace(GL.GL_CCW);
        else
            gl.glFrontFace(GL.GL_CW);

        // set depth testing
        if (pipelineState.getDepthTest()) {
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthFunc(GL.GL_LEQUAL);
            gl.glDepthMask(pipelineState.getDepthMask());
        } else
            gl.glDisable(GL.GL_DEPTH_TEST);

        // set stencil testing
        if (pipelineState.getStencilTest()) {
            gl.glEnable(GL.GL_STENCIL_TEST);
            int[] func = pipelineState.getStencilFunc();
            if (func != null)
                gl.glStencilFunc(func[0], func[1], func[2]);

            int[] op = pipelineState.getStencilOp();
            if (op != null)
                gl.glStencilOp(op[0], op[1], op[2]);

            if (pipelineState.getStencilMask() != -1)
                gl.glStencilMask(pipelineState.getStencilMask());
        } else
            gl.glDisable(GL.GL_STENCIL_TEST);

        // set blending
        gl.glEnable(GL.GL_BLEND);

        int[] blendFunc = pipelineState.getBlendFunc();
        if (blendFunc == null) {
            if (pipelineState.getActiveLightElement() == null)
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); // reduce
                                                                            // color
                                                                            // in
                                                                            // frame
                                                                            // buffer
                                                                            // an
                                                                            // add
                                                                            // the
                                                                            // new
                                                                            // color
            else
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE); // add to color in
                                                            // frame buffer
        } else
            gl.glBlendFunc(blendFunc[0], blendFunc[1]);

        // render geometry
        if (pipelineState.getActiveClipPlaneList() != null && pipelineState.getActiveGeoList() != null) {
            pipelineState.getActiveClipPlaneList().bind(ctx, pipelineState);
            pipelineState.getActiveGeoList().render(ctx, shaderContext, materialClass, pipelineState, orderBackToFront);
        } else
            throw new RuntimeException("[SwitchCamera] or [SwitchLightCamera] is missing before [DrawGeometry].");
    }

    @Override
    public PipelineCommand getRenderClone() {
        return this; // immutable class
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(PipelineState pipelineState) {
        // TODO Auto-generated method stub
    }

    @Override
    public void prescan(PipelineState pipelineState) {
        PrescanInfo info = pipelineState.getPrescanInfo();
        if (info == null) {
            info = new PrescanInfo();
            pipelineState.setPrescanInfo(info);
        }
        
        if (orderBackToFront) {            
            info.registerSortedMaterialClass(this.materialClass);
        }
    }
}
