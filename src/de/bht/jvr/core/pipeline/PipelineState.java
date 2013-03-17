package de.bht.jvr.core.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GL;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.FrameBuffer;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Texture;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.rendering.ClipPlaneList;
import de.bht.jvr.core.rendering.GeoList;
import de.bht.jvr.core.rendering.LightElement;
import de.bht.jvr.core.rendering.LightList;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.core.uniforms.UniformMatrix4;
import de.bht.jvr.core.uniforms.UniformSampler2D;
import de.bht.jvr.core.uniforms.UniformValue;
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

public class PipelineState {

    private SceneNode root = null;
    private PrescanInfo prescanInfo = null;
    private PipelineState parentState = null;
    private Map<String, Variable> variables = new HashMap<String, Variable>();
    private Map<String, FrameBuffer> fbos = new HashMap<String, FrameBuffer>();
    private Map<String, Texture> textures = new HashMap<String, Texture>();
    private Map<String, UniformValue> uniforms = new HashMap<String, UniformValue>();
    private Map<String, UniformValue> clipPlanes = new HashMap<String, UniformValue>();
    private FrameBuffer activeFbo = null;

    private LightList activeLightList = null;
    private ClipPlaneList activeClipPlaneList = null;
    private GeoList activeGeoList = null;

    private LightElement activeLightElement = null;
    private Transform activeCamTransform = null;
    private Matrix4 activeProjMatrix = null;
    private Integer viewFrustumCullingMode = null;
    private Boolean backFaceCulling = null;
    private Float polygonOffset = null;
    private Boolean frontFace = null;
    private Boolean depthTest = null;
    private Boolean depthMask = null;
    private Boolean stencilTest = null;
    private int[] stencilFunc = null;
    private int[] stencilOp = null;
    private Integer stencilMask = null;
    private int[] blendFunc = null;
    private Integer drawBuffer = null;

    public PipelineState(SceneNode root) {
        this.root = root;
    }

    /**
     * Binds uniforms that are independent to the GeoElements. This is called
     * once by every GeoElementGroup after the binding of the material.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     */
    public void bindGlobalUniforms(Context ctx) throws Exception {
        // reset clip planes
        int i = 0;
        while (ctx.getShaderProgram().hasUniform(ctx, "jvr_UseClipPlane" + i)) {
            ctx.getShaderProgram().setUniform(ctx, "jvr_UseClipPlane" + i, new UniformBool(false));
            i++;
        }

        // set clip planes
        for (Entry<String, UniformValue> u : clipPlanes.entrySet())
            ctx.getShaderProgram().setUniform(ctx, u.getKey(), u.getValue());

        // set polygon offset
        ctx.getShaderProgram().setUniform(ctx, "jvr_PolygonOffset", new UniformFloat(getPolygonOffset()));

        // set projection matrix
        ctx.getShaderProgram().setUniform(ctx, "jvr_ProjectionMatrix", new UniformMatrix4(getActiveProjMatrix()));

        // set global ambient
        LightList ll = getActiveLightList();
        ctx.getShaderProgram().setUniform(ctx, "jvr_Global_Ambient", new UniformColor(ll.globalAmbient()));

        // set the light
        LightElement light = getActiveLightElement();
        if (light != null)
            // bind active light to program
            light.bind(ctx, getActiveCamTransform());
    }

    /**
     * Binds the textures and uniforms of the pipeline to the active shader
     * program. This is called once by every GeoElement.
     * 
     * @param ctx
     *            the context
     * @param firstTextureUnit
     * @return next free texture unit
     * @throws Exception
     */
    public int bindUniformsAndTextures(Context ctx, int firstTextureUnit) throws Exception {
        if (parentState != null)
            firstTextureUnit = parentState.bindUniformsAndTextures(ctx, firstTextureUnit);

        // bind textures
        int tmu = firstTextureUnit;
        for (Entry<String, Texture> t : textures.entrySet()) {
            ctx.getGL().glActiveTexture(GL.GL_TEXTURE0 + tmu);
            t.getValue().bind(ctx);
            ctx.getShaderProgram().setUniform(ctx, t.getKey(), new UniformSampler2D(tmu++));
        }

        // set uniforms
        for (Entry<String, UniformValue> u : uniforms.entrySet())
            ctx.getShaderProgram().setUniform(ctx, u.getKey(), u.getValue());

        // set draw buffer
        if (this.getActiveFbo() == null) {
            ctx.getGL().glDrawBuffer(getDrawBuffer());
        }

        return tmu;
    }

    public void clearFBOs() {
        fbos.clear();
    }

    public void clearTextures2Ds() {
        textures.clear();
    }

    public void clearUniforms() {
        uniforms.clear();
    }

    public Transform getActiveCamTransform() {
        Transform trans = activeCamTransform;
        if (trans == null && parentState != null)
            return parentState.getActiveCamTransform();
        return trans;
    }

    public ClipPlaneList getActiveClipPlaneList() {
        ClipPlaneList dl = activeClipPlaneList;
        if (dl == null && parentState != null)
            return parentState.getActiveClipPlaneList();
        return dl;
    }

    public FrameBuffer getActiveFbo() {
        FrameBuffer fbo = activeFbo;
        if (fbo == null && parentState != null)
            return parentState.getActiveFbo();
        return fbo;
    }

    public GeoList getActiveGeoList() {
        GeoList dl = activeGeoList;
        if (dl == null && parentState != null)
            return parentState.getActiveGeoList();
        return dl;
    }

    public LightElement getActiveLightElement() {
        LightElement light = activeLightElement;
        if (light == null && parentState != null)
            return parentState.getActiveLightElement();
        return light;
    }

    public LightList getActiveLightList() {
        LightList dl = activeLightList;
        if (dl == null && parentState != null)
            return parentState.getActiveLightList();
        return dl;
    }

    public Matrix4 getActiveProjMatrix() {
        Matrix4 m = activeProjMatrix;
        if (m == null && parentState != null)
            return parentState.getActiveProjMatrix();
        return m;
    }

    public boolean getBackFaceCulling() {
        Boolean b = backFaceCulling;
        if (b == null && parentState != null)
            return parentState.getBackFaceCulling();

        if (b == null)
            return true;
        else
            return b;
    }

    public int[] getBlendFunc() {
        int[] b = blendFunc;
        if (b == null && parentState != null)
            return parentState.getBlendFunc();

        return b;
    }

    public UniformValue getClipPlane(String name) {
        UniformValue val = clipPlanes.get(name);
        if (val == null && parentState != null)
            return parentState.getUniform(name);
        return val;
    }

    public boolean getDepthMask() {
        Boolean b = depthMask;
        if (b == null && parentState != null)
            return parentState.getDepthMask();

        if (b == null)
            return true;
        else
            return b;
    }

    public boolean getDepthTest() {
        Boolean b = depthTest;
        if (b == null && parentState != null)
            return parentState.getDepthTest();

        if (b == null)
            return true;
        else
            return b;
    }

    public int getDrawBuffer() {
        Integer db = drawBuffer;
        if (db == null && parentState != null)
            return parentState.getDrawBuffer();

        if (db == null)
            return GL.GL_BACK;
        else
            return db.intValue();
    }

    public FrameBuffer getFBO(String name) {
        FrameBuffer fbo = fbos.get(name);
        if (fbo == null && parentState != null)
            return parentState.getFBO(name);
        return fbo;
    }

    public boolean getFrontFace() {
        Boolean b = frontFace;
        if (b == null && parentState != null)
            return parentState.getFrontFace();

        if (b == null)
            return true;
        else
            return b;
    }

    public PipelineState getParentState() {
        return parentState;
    }

    public float getPolygonOffset() {
        Float f = polygonOffset;
        if (f == null && parentState != null)
            return parentState.getPolygonOffset();

        if (f == null)
            return 0;
        else
            return f.floatValue();
    }

    public SceneNode getRoot() {
        SceneNode n = root;
        if (n == null && parentState != null)
            return parentState.getRoot();
        return n;
    }

    public int[] getStencilFunc() {
        int[] b = stencilFunc;
        if (b == null && parentState != null)
            return parentState.getStencilFunc();

        if (b == null)
            return new int[] { GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP };
        else
            return b;
    }

    public int getStencilMask() {
        Integer x = stencilMask;
        if (x == null && parentState != null)
            return parentState.getStencilMask();

        if (x == null)
            return 0xFFFFFFFF;
        else
            return x;
    }

    public int[] getStencilOp() {
        int[] b = stencilOp;
        if (b == null && parentState != null)
            return parentState.getStencilOp();

        if (b == null)
            return new int[] { GL.GL_ALWAYS, 0, 0 };
        else
            return b;
    }

    public boolean getStencilTest() {
        Boolean b = stencilTest;
        if (b == null && parentState != null)
            return parentState.getStencilTest();

        if (b == null)
            return false;
        else
            return b;
    }

    public Texture getTexture(String name) {
        Texture tex = textures.get(name);
        if (tex == null && parentState != null)
            return parentState.getTexture(name);
        return tex;
    }

    public UniformValue getUniform(String name) {
        UniformValue val = uniforms.get(name);
        if (val == null && parentState != null)
            return parentState.getUniform(name);
        return val;
    }
    
    public Variable getVariable(String name) {
        Variable var = variables.get(name);
        if (var == null)
        {
            if (parentState != null)
                return parentState.getVariable(name);
            else
                throw new RuntimeException("Undeclared pipeline variable: " + name);
        }
        
        return var;
    }

    public int getViewFrustumCullingMode() {
        Integer x = viewFrustumCullingMode;
        if (x == null && parentState != null)
            return parentState.getViewFrustumCullingMode();

        if (x == null)
            return 1; // default DL-VFC
        else
            return x;
    }
    
    public PrescanInfo getPrescanInfo() {
        if (prescanInfo == null && parentState != null)
            return parentState.getPrescanInfo();
        
        return prescanInfo;
    }
    
    public void registerVariable(Variable var) {
        this.variables.put(var.getName(), var);
    }

    public void setActiveCamTransform(Transform activeCamTransform) {
        this.activeCamTransform = activeCamTransform;
    }

    public void setActiveClipPlaneList(ClipPlaneList activeClipPlaneList) {
        this.activeClipPlaneList = activeClipPlaneList;
    }

    public void setActiveFbo(FrameBuffer activeFbo) {
        this.activeFbo = activeFbo;
    }

    public void setActiveGeoList(GeoList activeGeoList) {
        this.activeGeoList = activeGeoList;
    }

    public void setActiveLightElement(LightElement lightElement) {
        activeLightElement = lightElement;
    }

    public void setActiveLightList(LightList activeLightList) {
        this.activeLightList = activeLightList;
    }

    public void setActiveProjMatrix(Matrix4 activeProjMatrix) {
        this.activeProjMatrix = activeProjMatrix;
    }

    public void setBackFaceCulling(boolean enable) {
        backFaceCulling = enable;
    }

    public void setBlendFunc(int[] func) {
        blendFunc = func;
    }

    public void setClipPlane(String name, UniformValue value) {
        clipPlanes.put(name, value);
    }

    public void setDepthMask(boolean enable) {
        depthMask = enable;
    }

    public void setDepthTest(boolean enable) {
        depthTest = enable;
    }

    public void setDrawBuffer(int db) {
        drawBuffer = db;
    }

    public void setFBO(String name, FrameBuffer fbo) {
        fbos.put(name, fbo);
    }

    public void setFrontFace(boolean counterclockwise) {
        frontFace = counterclockwise;
    }

    public void setParentState(PipelineState parentState) {
        this.parentState = parentState;
    }

    public void setPolygonOffset(float offset) {
        polygonOffset = offset;
    }

    public void setRoot(SceneNode root) {
        this.root = root;
    }

    public void setStencilFunc(int[] func) {
        stencilFunc = func;
    }

    public void setStencilMask(int mask) {
        stencilMask = mask;
    }

    public void setStencilOp(int[] operation) {
        stencilOp = operation;
    }

    public void setStencilTest(boolean enable) {
        stencilTest = enable;
    }

    public void setTexture2D(String name, Texture tex) {
        textures.put(name, tex);
    }

    public void setUniform(String name, UniformValue value) {
        uniforms.put(name, value);
    }

    public void setViewFrustumCullingMode(int mode) {
        viewFrustumCullingMode = mode;
    }
    
    public void setPrescanInfo(PrescanInfo prescanInfo) {
        this.prescanInfo = prescanInfo;
    }
}
