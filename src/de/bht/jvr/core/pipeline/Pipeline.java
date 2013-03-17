package de.bht.jvr.core.pipeline;

import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.util.Color;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GLProfile;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Context;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.SceneNode;
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
 * The multipass rendering pipeline.
 * 
 * @author Marc Roßbach
 */

public class Pipeline {
    /** The pipeline commands. */
    private List<PipelineCommand> commands = new ArrayList<PipelineCommand>();

    /** The pipeline state. */
    private PipelineState pipelineState = null;

    private boolean pipelineReady = true;

    /**
     * Instantiates a new pipeline.
     * 
     * @param root
     *            the root
     */
    public Pipeline(SceneNode root) {
        pipelineState = new PipelineState(root);
    }

    /**
     * Adds the command to the command list.
     * 
     * @param cmd
     *            the command
     * @return pointer to the pipeline command
     */
    private PipelineCommandPtr addCommand(PipelineCommand cmd) {
        Log.info(this.getClass(), "Appending pipeline command: [" + cmd.getClass().getSimpleName() + "]");
        commands.add(cmd);
        return new PipelineCommandPtr(this, commands.size() - 1, cmd);
    }
    
    /**
     * Adds a value to the current value of a variable.
     * 
     * @param variableName
     *            the name of the variable
     * @param value
     *            the value to add
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr addToVariable(String variableName, Number value) {
        return addCommand(new AddToVariable(variableName, value));
    }
    
    /**
     * Adds the value of a variable to the current value of another variable.
     * 
     * @param dstVariableName
     *            the name of the destination variable
     * @param srcVariableName
     *            the name of the source variable
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr addToVariable(String dstVariableName, String srcVariableName) {
        return addCommand(new AddToVariable(dstVariableName, srcVariableName));
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
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr bindColorBuffer(String uniformSampler, String fboName, int bufIndex) {
        return addCommand(new BindColorBuffer(uniformSampler, fboName, bufIndex));
    }

    /**
     * Bind depth buffer.
     * 
     * @param uniformSampler
     *            the name of the uniform sampler
     * @param fboName
     *            the fbo name
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr bindDepthBuffer(String uniformSampler, String fboName) {
        return addCommand(new BindDepthBuffer(uniformSampler, fboName));
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
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr clearBuffers(boolean depthBuf, boolean firstColBuf, Color clearColor) {
        return addCommand(new ClearBuffers(depthBuf, new boolean[] { firstColBuf }, clearColor, false, 0));
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
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr clearBuffers(boolean depthBuf, boolean firstColBuf, Color clearColor, boolean stencilBuf, int stencilClearValue) {
        return addCommand(new ClearBuffers(depthBuf, new boolean[] { firstColBuf }, clearColor, stencilBuf, stencilClearValue));
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
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr clearBuffers(boolean depthBuf, boolean[] colBuf, Color clearColor) {
        return addCommand(new ClearBuffers(depthBuf, colBuf, clearColor, false, 0));
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
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr clearBuffers(boolean depthBuf, boolean[] colBuf, Color clearColor, boolean stencilBuf, int stencilClearValue) {
        return addCommand(new ClearBuffers(depthBuf, colBuf, clearColor, stencilBuf, stencilClearValue));
    }

    /**
     * Clear the pipeline.
     */
    public void clearPipeline() {
        commands.clear();
    }

    /**
     * Creates the frame buffer object with default color texture format and
     * relative dimensions.
     * 
     * @param fboName
     *            the fbo name
     * @param depthBufAsTexture
     *            render to depth buffer
     * @param numColBufs
     *            the number of color buffers
     * @param scale
     *            the scale
     * @param maxSamples
     *            maximum samples
     */
    public void createFrameBufferObject(String fboName, boolean depthBufAsTexture, int numColBufs, float scale, int maxSamples) {
        addCommand(new CreateFrameBufferObject(fboName, depthBufAsTexture, numColBufs, GL.GL_RGBA, scale, maxSamples, false));
    }

    /**
     * Creates the frame buffer object with default color texture format and
     * relative dimensions.
     * 
     * @param fboName
     *            the fbo name
     * @param depthBufAsTexture
     *            render to depth buffer
     * @param numColBufs
     *            the number of color buffers
     * @param format
     *            format of the color textures e.g. GL_RGBA
     * @param scale
     *            the scale
     * @param maxSamples
     *            maximum samples
     */
    public void createFrameBufferObject(String fboName, boolean depthBufAsTexture, int numColBufs, int format, float scale, int maxSamples) {
        addCommand(new CreateFrameBufferObject(fboName, depthBufAsTexture, numColBufs, format, scale, maxSamples, false));
    }

    /**
     * Creates the frame buffer object with default color texture format and
     * fixed dimensions.
     * 
     * @param fboName
     *            the fbo name
     * @param depthBufAsTexture
     *            render to depth buffer
     * @param numColBufs
     *            the number of color buffers
     * @param width
     *            the width
     * @param height
     *            the height
     * @param maxSamples
     *            maximum samples
     */
    public void createFrameBufferObject(String fboName, boolean depthBufAsTexture, int numColBufs, int width, int height, int maxSamples) {
        addCommand(new CreateFrameBufferObject(fboName, depthBufAsTexture, numColBufs, GL.GL_RGBA, width, height, maxSamples, false));
    }

    /**
     * Creates the frame buffer object with default color texture format and
     * fixed dimensions.
     * 
     * @param fboName
     *            the fbo name
     * @param depthBufAsTexture
     *            render to depth buffer
     * @param numColBufs
     *            the number of color buffers
     * @param format
     *            format of the color textures e.g. GL_RGBA
     * @param width
     *            the width
     * @param height
     *            the height
     * @param maxSamples
     *            maximum samples
     */
    public void createFrameBufferObject(String fboName, boolean depthBufAsTexture, int numColBufs, int format, int width, int height, int maxSamples) {
        addCommand(new CreateFrameBufferObject(fboName, depthBufAsTexture, numColBufs, format, width, height, maxSamples, false));
    }

    /**
     * Creates a frame buffer object with relative dimensions.
     * 
     * @param fboName
     *            the fbo name
     * @param numColBufs
     *            the number of color buffers
     * @param scale
     *            the scale
     * @param maxSamples
     *            maximum samples
     * @param stencilBuf
     *            create FBO with stencil buffer
     */
    public void createFrameBufferObject(String fboName, int numColBufs, float scale, int maxSamples, boolean stencilBuf) {
        addCommand(new CreateFrameBufferObject(fboName, false, numColBufs, GL.GL_RGBA, scale, maxSamples, stencilBuf));
    }

    /**
     * Creates a frame buffer object with relative dimensions.
     * 
     * @param fboName
     *            the fbo name
     * @param numColBufs
     *            the number of color buffers
     * @param format
     *            format of the color textures e.g. GL_RGBA
     * @param scale
     *            the scale
     * @param maxSamples
     *            maximum samples
     * @param stencilBuf
     *            create FBO with stencil buffer
     */
    public void createFrameBufferObject(String fboName, int numColBufs, int format, float scale, int maxSamples, boolean stencilBuf) {
        addCommand(new CreateFrameBufferObject(fboName, false, numColBufs, format, scale, maxSamples, stencilBuf));
    }

    /**
     * Creates a frame buffer object with fixed dimensions.
     * 
     * @param fboName
     *            the name of the fbo
     * @param numColBufs
     *            number of color textures
     * @param width
     *            the width
     * @param height
     *            the height
     * @param maxSamples
     *            maximum samples
     * @param stencilBuf
     *            create FBO with stencil buffer
     */
    public void createFrameBufferObject(String fboName, int numColBufs, int width, int height, int maxSamples, boolean stencilBuf) {
        addCommand(new CreateFrameBufferObject(fboName, false, numColBufs, GL.GL_RGBA, width, height, maxSamples, stencilBuf));
    }

    /**
     * Creates a frame buffer object with fixed dimensions.
     * 
     * @param fboName
     *            the name of the fbo
     * @param numColBufs
     *            number of color textures
     * @param format
     *            format of the color textures e.g. GL_RGBA
     * @param width
     *            the width
     * @param height
     *            the height
     * @param maxSamples
     *            maximum samples
     * @param stencilBuf
     *            create FBO with stencil buffer
     */
    public void createFrameBufferObject(String fboName, int numColBufs, int format, int width, int height, int maxSamples, boolean stencilBuf) {
        addCommand(new CreateFrameBufferObject(fboName, false, numColBufs, format, width, height, maxSamples, stencilBuf));
    }
    
    /**
     * Declares a new number variable
     * 
     * @param name
     *            the name of the variable
     * @param value
     *            the initial value of the variable
     */
    public void declareVariable(String name, Number value) {
        addCommand(new DeclareVariable(name, value));
    }

    /**
     * Perform forward lighting.
     * 
     * @param noShadowLights
     *            iterate lights without shadows
     * @param shadowLights
     *            iterate lights with shadows
     * @return pipeline is executed for every light in this loop
     */
    public Pipeline doLightLoop(boolean noShadowLights, boolean shadowLights) {
        Pipeline p = new Pipeline(null);
        addCommand(new DoLightLoop(p, noShadowLights, shadowLights));
        return p;
    }

    /**
     * Use an existing pipeline in the same scope as the parent pipeline.
     * 
     * @param pipeline
     *            pipeline to be executed here
     */
    public void usePipeline(Pipeline pipeline) {
        addCommand(new UsePipeline(pipeline, false));
    }
    
    /**
     *  Use an existing pipeline.
     * 
     * @param pipeline
     *            pipeline to be executed here
     * @param scoped
     *            if true, the pipeline is executed in its own scope
     */
    public void usePipeline(Pipeline pipeline, boolean scoped) {
        addCommand(new UsePipeline(pipeline, scoped));
    }

    /**
     * Draw the geometry without order.
     * 
     * @param shaderContext
     *            the shader context
     * @param materialClass
     *            the material class
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr drawGeometry(String shaderContext, String materialClass) {
        return addCommand(new DrawGeometry(shaderContext, materialClass, false));
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
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr drawGeometry(String shaderContext, String materialClass, boolean orderBackToFront) {
        return addCommand(new DrawGeometry(shaderContext, materialClass, orderBackToFront));
    }

    /**
     * Drawing a fullscreen quad to the screen.
     * 
     * @param material
     *            the material
     * @param shaderContext
     *            the shader context
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr drawQuad(ShaderMaterial material, String shaderContext) {
        return addCommand(new DrawQuad(material, shaderContext));
    }

    /**
     * Completes all previously called GL commands. Use this command for better
     * swap synchronization in stereo applications. Be careful with this
     * command, it lowers the framerate.
     */
    public void forceFrameFinish() {
        addCommand(new ForceFrameFinish());
    }

    /**
     * Gets the command.
     * 
     * @param index
     *            the index
     * @return the command
     */
    protected PipelineCommand getCommand(int index) {
        return commands.get(index);
    }

    protected void setPipelineState(PipelineState ps) {
        pipelineState = ps;
    }

    /**
     * Gets the pipeline state.
     * 
     * @return the pipeline state
     */
    protected PipelineState getPipelineState() {
        return pipelineState;
    }

    /**
     * creates a new pipeline with the same command list.
     * 
     * @return the pipeline
     */
    public Pipeline getRenderClone() {
        Pipeline clone = new Pipeline(pipelineState.getRoot());

        // clone command list
        for (PipelineCommand cmd : commands)
            clone.commands.add(cmd.getRenderClone());

        return clone;
    }
    
    /**
     * Executes a sub pipeline if the arguments are equal
     * 
     * @param variableName
     *            the name of the variable
     * @param value
     *            the value to compare
     * @return the "than" pipeline
     */
    public Pipeline IfEqual(String variableName, Number value) {
        Pipeline p = new Pipeline(null);
        addCommand(new IfEqual(p, variableName, value));
        return p;      
    }
    
    /**
     * Executes a sub pipeline if the arguments are equal
     * 
     * @param firstVariableName
     *            the name of the first variable
     * @param secondVariableName
     *            the name of the second variable
     * @return the "than" pipeline
     */
    public Pipeline IfEqual(String firstVariableName, String secondVariableName) {
        Pipeline p = new Pipeline(null);
        addCommand(new IfEqual(p, firstVariableName, secondVariableName));
        return p;      
    }

    /**
     * Sets the back face culling.
     * 
     * @param enable
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setBackFaceCulling(boolean enable) {
        return addCommand(new SetBackFaceCulling(enable));
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
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setBlendFunc(Integer sFactor, Integer dFactor) {
        return addCommand(new SetBlendFunc(sFactor, dFactor));
    }

    /**
     * Sets the command.
     * 
     * @param index
     *            the index
     * @param cmd
     *            the command
     */
    protected void setCommand(int index, PipelineCommand cmd) {
        commands.set(index, cmd);
    }

    /**
     * Enables/Disables the depth writing.
     * 
     * @param enable
     *            default is true)
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setDepthMask(boolean enable) {
        return addCommand(new SetDepthMask(enable));
    }

    /**
     * Enables/Disables the depth test.
     * 
     * @param enable
     *            (default is true)
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setDepthTest(boolean enable) {
        return addCommand(new SetDepthTest(enable));
    }

    /**
     * Specifies up to four color buffers to be drawn into.
     * 
     * @param drawBuffer
     *            the draw buffer to draw in: GL_NONE, GL_FRONT_LEFT,
     *            GL_FRONT_RIGHT, GL_BACK_LEFT, GL_BACK_RIGHT, GL_FRONT,
     *            GL_BACK, GL_LEFT, GL_RIGHT, GL_FRONT_AND_BACK
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setDrawBuffer(int drawBuffer) {
        return addCommand(new SetDrawBuffer(drawBuffer));
    }

    /**
     * Specifies the orientation of front-facing polygons.
     * 
     * @param counterclockwise
     *            false, if orientation is clockwise (default is true)
     */
    public void setFrontFace(boolean counterclockwise) {
        addCommand(new SetFrontFace(counterclockwise));
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
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setStencilFunc(int func, int ref, int mask) {
        return addCommand(new SetStencilFunc(func, ref, mask));
    }

    /**
     * Sets the writable bits of the stencil buffer.
     * 
     * @param mask
     *            pecifies a bit mask to enable and disable writing of
     *            individual bits in the stencil planes. Initially, the mask is
     *            all 1's.
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setStencilMask(int mask) {
        return addCommand(new SetStencilMask(mask));
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
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setStencilOp(int fail, int zFail, int zPass) {
        return addCommand(new SetStencilOp(fail, zFail, zPass));
    }

    /**
     * Enables/Disables the stencil test.
     * 
     * @param enable
     *            (default is false)
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setStencilTest(boolean enable) {
        return addCommand(new SetStencilTest(enable));
    }

    /**
     * Sets a uniform.
     * 
     * @param uniformName
     *            the uniform name
     * @param value
     *            the value
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setUniform(String uniformName, UniformValue value) {
        return addCommand(new SetUniform(uniformName, value));
    }
    
    /**
     * Sets a variable.
     * 
     * @param variableName
     *            the name of the variable
     * @param value
     *            the new value of the variable
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setVariable(String variableName, Number value) {
        return addCommand(new SetVariable(variableName, value));
    }
    
    /**
     * Copies a value from one variable to another.
     * 
     * @param dstVariableName
     *            the name of the destination variable
     * @param srcVariableName
     *            the name of the source variable
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setVariable(String dstVariableName, String srcVariableName) {
        return addCommand(new SetVariable(dstVariableName, srcVariableName));
    }

    /**
     * Sets the view frustum culling mode.
     * 
     * @param mode
     *            (default is 1) <br>
     *            0: disable view frustum culling <br>
     *            1: view frustum culling on draw list in render thread
     *            (recommend for flat scene graph) <br>
     *            2: hierarchical view frustum culling in main thread (recommend
     *            for complex scene graph) <br>
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr setViewFrustumCullingMode(int mode) {
        return addCommand(new SetViewFrustumCullingMode(mode));
    }

    /**
     * Switch to camera.
     * 
     * @param camera
     *            the camera
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr switchCamera(CameraNode camera) {
        return addCommand(new SwitchCamera(camera));
    }

    /**
     * Switch frame buffer object.
     * 
     * @param fboName
     *            the fbo name (null = output buffer)
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr switchFrameBufferObject(String fboName) {
        return addCommand(new SwitchFrameBufferObject(fboName));
    }

    /**
     * Switch to light viewpoint.
     */
    public void switchLightCamera() {
        addCommand(new SwitchLightCamera(null));
    }

    /**
     * Switch to light viewpoint.
     * 
     * @param light
     *            the light
     * @return pointer to the pipeline command
     */
    public PipelineCommandPtr switchLightCamera(LightNode light) {
        return addCommand(new SwitchLightCamera(light));
    }

    /**
     * Unbind all color and depth buffers.
     */
    public void unbindBuffers() {
        addCommand(new UnbindBuffers());
    }

    /**
     * Unset all uniforms.
     */
    public void unsetUniforms() {
        addCommand(new UnsetUniforms());
    }

    /*------------------- rendering stuff -----------------------------*/

    /**
     * render the pipeline.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public void render(Context ctx) throws Exception {
        // Benchmark.beginStep("Pipeline rendering");
        for (PipelineCommand cmd : commands)
            cmd.execute(ctx, pipelineState);

        // clean up old OpenGL objects
        ctx.doGarbageCollection();

        // force to finish all buffered GL commands.
        // ctx.getGL().glFinish();

        pipelineReady = false;
        // Benchmark.endStep("Pipeline rendering");
    }

    protected void resetPipeline() {
        // reset pipeline state
        pipelineState = new PipelineState(pipelineState.getRoot());

        // reset commands
        for (PipelineCommand cmd : commands)
            cmd.reset();

        pipelineReady = true;
    }
    
    /**
     * scan pipeline.
     */
    public void prescan() {
        // Benchmark.beginStep("Pipeline prescan");
        if (!pipelineReady)
            resetPipeline();

        for (PipelineCommand cmd : commands)
            cmd.prescan(pipelineState);
        // Benchmark.endStep("Pipeline prescan");
    }

    /**
     * create new draw lists.
     */
    public void update() {
        // Benchmark.beginStep("Pipeline update");
        if (!pipelineReady)
            resetPipeline();

        for (PipelineCommand cmd : commands)
            cmd.update(pipelineState);
        // Benchmark.endStep("Pipeline update");
    }

    static {
        Log.info(RenderWindow.class, "Initializing GLProfile-Singleton");
        
        boolean isUnix = isUnix(); 
        
        GLProfile.initSingleton(!isUnix);
    }
    
    private static boolean isUnix() {
        
        String os = System.getProperty("os.name").toLowerCase();
        // linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0); 
    }
}
