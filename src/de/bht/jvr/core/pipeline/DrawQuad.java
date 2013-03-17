package de.bht.jvr.core.pipeline;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.PMVMatrix;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.TriangleMesh;
import de.bht.jvr.core.rendering.LightElement;
import de.bht.jvr.core.uniforms.UniformMatrix4;
import de.bht.jvr.logger.Log;
import de.bht.jvr.math.Matrix4;

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

public class DrawQuad implements PipelineCommand {
    private ShaderMaterial baseMaterial;
    private ShaderMaterial activeMaterial;
    private String shaderContext;
    private static Geometry quad = null;
    private static Matrix4 projMatrix = null;

    public DrawQuad(ShaderMaterial material, String shaderContext) {
        baseMaterial = material;
        this.shaderContext = shaderContext;

        try {
            if (quad == null) {
                int[] indices = new int[] { 0, 3, 1, 1, 3, 2 };
                float[] positions = new float[] { 0, 0, -1, 0, 1, -1, 1, 1, -1, 1, 0, -1 };
                float[] texCoords = new float[] { 0, 0, 0, 1, 1, 1, 1, 0 };

                quad = new TriangleMesh(indices, positions, null, texCoords, null, null);
            }

            if (projMatrix == null) {
                // create projection matrix
                PMVMatrix pmvMatrix = new PMVMatrix();
                pmvMatrix.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
                pmvMatrix.glLoadIdentity();
                pmvMatrix.glOrthof(0, 1, 0, 1, 0.1f, 10);

                float[] data = new float[16];
                pmvMatrix.glGetPMatrixf().get(data);
                projMatrix = new Matrix4(data).transpose();
            }
        } catch (Exception e) {
            Log.error(this.getClass(), e.getMessage());
        }
    }

    @Override
    public void execute(Context ctx, PipelineState pipelineState) throws Exception {
        GL2GL3 gl = ctx.getGL();
        
        // set render target
        ctx.bindFbo(pipelineState.getActiveFbo());

        // set back face culling
        gl.glCullFace(GL.GL_BACK);
        gl.glEnable(GL.GL_CULL_FACE);

        // set front faces
        gl.glFrontFace(GL.GL_CCW);

        gl.glDepthFunc(GL.GL_ALWAYS);
        gl.glDisable(GL.GL_STENCIL_TEST);

        gl.glEnable(GL.GL_BLEND);
        int[] blendFunc = pipelineState.getBlendFunc();
        if (blendFunc == null)
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        else
            gl.glBlendFunc(blendFunc[0], blendFunc[1]);

        ctx.setShaderContext(shaderContext);
        if (activeMaterial.bind(ctx)) {
            // bind light for deferred shading
            LightElement light = pipelineState.getActiveLightElement();
            if (light != null)
                light.bind(ctx, pipelineState.getActiveCamTransform()); // bind
                                                                        // active
                                                                        // light
                                                                        // to
                                                                        // program

            // user defined uniforms and buffers
            pipelineState.bindUniformsAndTextures(ctx, activeMaterial.getNumTextures(shaderContext));

            // projection matrix & model view matrix
            ctx.getShaderProgram().setUniform(ctx, "jvr_ProjectionMatrix", new UniformMatrix4(projMatrix));
            ctx.getShaderProgram().setUniform(ctx, "jvr_ModelViewProjectionMatrix", new UniformMatrix4(projMatrix));
            ctx.getShaderProgram().setUniform(ctx, "jvr_ModelViewMatrix", new UniformMatrix4(new Matrix4()));
            ctx.getShaderProgram().setUniform(ctx, "jvr_ModelMatrix", new UniformMatrix4(new Matrix4()));

            // draw quad
            quad.render(ctx);
        }
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
        activeMaterial = baseMaterial.getRenderClone();
    }

    @Override
    public void prescan(PipelineState pipelineState) {
        // TODO Auto-generated method stub
        
    }
}
