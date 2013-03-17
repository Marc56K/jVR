package de.bht.jvr.core.pipeline;

import de.bht.jvr.util.Color;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.uniforms.UniformValue;
import de.bht.jvr.logger.Log;

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
 * The pipeline command pointer allows the modification of the pipeline after
 * its creation.
 * 
 * @author Marc Roßbach
 */

public class PipelineCommandPtr {

    /** The pipeline. */
    private Pipeline pipeline;

    /** The command index. */
    private int commandIndex;

    /** The command. */
    private PipelineCommand command;

    /**
     * Instantiates a new pipeline command ptr.
     * 
     * @param pipeline
     *            the pipeline
     * @param commandIndex
     *            the command index
     * @param command
     *            the command
     */
    protected PipelineCommandPtr(Pipeline pipeline, int commandIndex, PipelineCommand command) {
        this.pipeline = pipeline;
        this.commandIndex = commandIndex;
        this.command = command;
    }
    
    /**
     * Adds a value to the current value of a variable.
     * 
     * @param variableName
     *            the name of the variable
     * @param value
     *            the value to add
     */
    public void addToVariable(String variableName, Number value) {
        set(new AddToVariable(variableName, value));
    }
    
    /**
     * Adds the value of a variable to the current value of another variable.
     * 
     * @param dstVariableName
     *            the name of the destination variable
     * @param srcVariableName
     *            the name of the source variable
     */
    public void addToVariable(String dstVariableName, String srcVariableName) {
        set(new AddToVariable(dstVariableName, srcVariableName));
    }

    /**
     * Bind color buffer.
     * 
     * @param uniformSampler
     *            the name of the uniform sampler
     * @param fboName
     *            the fbo name
     * @param bufIndex
     *            the color buffer index
     */
    public void bindColorBuffer(String uniformSampler, String fboName, int bufIndex) {
        set(new BindColorBuffer(uniformSampler, fboName, bufIndex));
    }

    /**
     * Bind depth buffer.
     * 
     * @param uniformSampler
     *            the name of the uniform sampler
     * @param fboName
     *            the fbo name
     */
    public void bindDepthBuffer(String uniformSampler, String fboName) {
        set(new BindDepthBuffer(uniformSampler, fboName));
    }

    /**
     * Clear color and depth buffer.
     * 
     * @param depthBuf
     *            clear the depth buffer
     * @param firstColBuf
     *            clear the first color buffer
     * @param clearColor
     *            the clear color
     */
    public void clearBuffers(boolean depthBuf, boolean firstColBuf, Color clearColor) {
        set(new ClearBuffers(depthBuf, new boolean[] { firstColBuf }, clearColor, false, 0));
    }

    /**
     * Clear color, depth and stencil buffers.
     * 
     * @param depthBuf
     *            clear the depth buffer
     * @param firstColBuf
     *            clear the first color buffer
     * @param clearColor
     *            the clear color
     * @param stencilBuf
     *            clear the stencil buffer
     * @param stencilClearValue
     *            the stencil clear value
     */
    public void clearBuffers(boolean depthBuf, boolean firstColBuf, Color clearColor, boolean stencilBuf, int stencilClearValue) {
        set(new ClearBuffers(depthBuf, new boolean[] { firstColBuf }, clearColor, stencilBuf, stencilClearValue));
    }

    /**
     * Clear color and depth buffers.
     * 
     * @param depthBuf
     *            clear the depth buffer
     * @param colBuf
     *            clear the color buffers
     * @param clearColor
     *            the clear color
     */
    public void clearBuffers(boolean depthBuf, boolean[] colBuf, Color clearColor) {
        set(new ClearBuffers(depthBuf, colBuf, clearColor, false, 0));
    }

    /**
     * Clear color, depth and stencil buffers.
     * 
     * @param depthBuf
     *            clear the depth buffer
     * @param colBuf
     *            clear the color buffers
     * @param clearColor
     *            the clear color
     * @param stencilBuf
     *            clear the stencil buffer
     * @param stencilClearValue
     *            the stencil clear value
     */
    public void clearBuffers(boolean depthBuf, boolean[] colBuf, Color clearColor, boolean stencilBuf, int stencilClearValue) {
        set(new ClearBuffers(depthBuf, colBuf, clearColor, stencilBuf, stencilClearValue));
    }

    /**
     * Draw the geometry without order.
     * 
     * @param shaderContext
     *            the shader context
     * @param materialClass
     *            the material class
     */
    public void drawGeometry(String shaderContext, String materialClass) {
        set(new DrawGeometry(shaderContext, materialClass, false));
    }

    /**
     * Draw the geometry ordered.
     * 
     * @param shaderContext
     *            the shader context
     * @param materialClass
     *            the material class
     * @param orderBackToFront
     *            order the geometry before rendering
     */
    public void drawGeometry(String shaderContext, String materialClass, boolean orderBackToFront) {
        set(new DrawGeometry(shaderContext, materialClass, orderBackToFront));
    }

    /**
     * Drawing a fullscreen quad to the screen.
     * 
     * @param material
     *            the material
     * @param shaderContext
     *            the shader context
     */
    public void drawQuad(ShaderMaterial material, String shaderContext) {
        set(new DrawQuad(material, shaderContext));
    }

    /**
     * Sets the new command to the pipeline.
     * 
     * @param command
     *            the command
     */
    protected void set(PipelineCommand command) {
        if (pipeline.getCommand(commandIndex) == this.command) {
            if (this.command.getClass() == command.getClass()) {
                pipeline.setCommand(commandIndex, command);
                this.command = command;
            } else
                Log.error(this.getClass(), "Invalid pipeline command " + command.getClass().getSimpleName() + " expect " + this.command.getClass().getSimpleName());
        } else
            Log.error(this.getClass(), "Invalid pipeline command pointer.");
    }

    /**
     * Sets the back face culling.
     * 
     * @param enable
     * @return pointer to the pipeline command
     */
    public void setBackFaceCulling(boolean enable) {
        set(new SetBackFaceCulling(enable));
    }

    /**
     * Specifies the pixel arithmetic.
     * 
     * @param sFactor
     *            Specifies how the red, green, blue and alpha source blending
     *            factors are computed. Nine symbolic constants are accepted:
     *            GL_ZERO, GL_ONE, GL_DST_COLOR, GL_ONE_MINUS_DST_COLOR,
     *            GL_SRC_ALPHA, GL_ONE_MINUS_SRC_COLOR, GL_DST_ALPHA,
     *            GL_ONE_MINUS_DST_ALPHA, and GL_SRC_ALPHA_SATURATE. (null =
     *            automatic)
     * @param dFactor
     *            Specifies how the red, green, blue and alpha destination
     *            blending factors are computed. Eight symbolic constants are
     *            accepted: GL_ZERO, GL_ONE, GL_SCR_COLOR,
     *            GL_ONE_MINUS_SRC_COLOR, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_COLOR,
     *            GL_DST_ALPHA, and GL_ONE_MINUS_DST_ALPHA. (null = automatic)
     */
    public void setBlendFunc(Integer sFactor, Integer dFactor) {
        set(new SetBlendFunc(sFactor, dFactor));
    }

    /**
     * Enables/Disables the depth writing.
     * 
     * @param enable
     *            default is true)
     */
    public void setDepthMask(boolean enable) {
        set(new SetDepthMask(enable));
    }

    /**
     * Enables/Disables the depth test.
     * 
     * @param enable
     *            (default is true)
     */
    public void setDepthTest(boolean enable) {
        set(new SetDepthTest(enable));
    }

    /**
     * Sets the function for the stencil test.
     * 
     * @param func
     *            Specifies the test function. Eight symbolic constants are
     *            valid: GL_NEVER, GL_LESS, GL_LEQUAL, GL_GREATER, GL_GEQUAL,
     *            GL_EQUAL, GL_NOTEQUAL, and GL_ALWAYS. The initial value is
     *            GL_ALWAYS.
     * @param ref
     *            The reference value for the stencil test. The ref parameter is
     *            clamped to the range [0, (2^n)-1], where n is the number of
     *            bitplanes in the stencil buffer.
     * @param mask
     *            A mask that is ANDed with both the reference value and the
     *            stored stencil value when the test is done.
     */
    public void setStencilFunc(int func, int ref, int mask) {
        set(new SetStencilFunc(func, ref, mask));
    }

    /**
     * Sets the writable bits of the stencil buffer.
     * 
     * @param mask
     *            pecifies a bit mask to enable and disable writing of
     *            individual bits in the stencil planes. Initially, the mask is
     *            all 1's.
     */
    public void setStencilMask(int mask) {
        set(new SetStencilMask(mask));
    }

    /**
     * Stets the stencil test actions.
     * 
     * @param fail
     *            Specifies the action to take when the stencil test fails.
     *            Eight symbolic constants are accepted: GL_KEEP, GL_ZERO,
     *            GL_REPLACE, GL_INCR, GL_INCR_WRAP, GL_DECR, GL_DECR_WRAP, and
     *            GL_INVERT. The initial value is GL_KEEP.
     * @param zFail
     *            Specifies the stencil action when the stencil test passes, but
     *            the depth test fails. dpfail accepts the same symbolic
     *            constants as sfail. The initial value is GL_KEEP.
     * @param zPass
     *            Specifies the stencil action when both the stencil test and
     *            the depth test pass, or when the stencil test passes and
     *            either there is no depth buffer or depth testing is not
     *            enabled. dppass accepts the same symbolic constants as sfail.
     *            The initial value is GL_KEEP.
     */
    public void setStencilOp(int fail, int zFail, int zPass) {
        set(new SetStencilOp(fail, zFail, zPass));
    }

    /**
     * Enables/Disables the stencil test.
     * 
     * @param enable
     *            (default is false)
     */
    public void setStencilTest(boolean enable) {
        set(new SetStencilTest(enable));
    }

    /**
     * Sets a uniform.
     * 
     * @param uniformName
     *            the uniform name
     * @param value
     *            the value
     */
    public void setUniform(String uniformName, UniformValue value) {
        set(new SetUniform(uniformName, value));
    }
    
    /**
     * Sets a variable.
     * 
     * @param variableName
     *            the name of the variable
     * @param value
     *            the new value of the variable
     */
    public void setVariable(String variableName, Number value) {
        set(new SetVariable(variableName, value));
    }
    
    /**
     * Copies a value from one variable to another.
     * 
     * @param dstVariableName
     *            the name of the destination variable
     * @param srcVariableName
     *            the name of the source variable
     */
    public void setVariable(String dstVariableName, String srcVariableName) {
        set(new SetVariable(dstVariableName, srcVariableName));
    }

    /**
     * Sets the view frustum culling mode.
     * 
     * @param mode
     *            0: disable view frustum culling 1: view frustum culling on
     *            draw list in render thread (recommend for flat scene graph) 2:
     *            hierarchical view frustum culling in main thread (recommend
     *            for complex scene graph)
     * @return pointer to the pipeline command
     */
    public void setViewFrustumCullingMode(int mode) {
        set(new SetViewFrustumCullingMode(mode));
    }

    /**
     * Switch to camera.
     * 
     * @param camera
     *            the camera
     */
    public void switchCamera(CameraNode camera) {
        set(new SwitchCamera(camera));
    }

    /**
     * Switch frame buffer object.
     * 
     * @param fboName
     *            the fbo name (null = output buffer)
     */
    public void switchFrameBufferObject(String fboName) {
        set(new SwitchFrameBufferObject(fboName));
    }

    /**
     * Switch to light viewpoint.
     * 
     * @param light
     *            the light
     */
    public void switchLightCamera(LightNode light) {
        set(new SwitchLightCamera(light));
    }
}
