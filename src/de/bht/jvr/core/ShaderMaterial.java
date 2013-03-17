package de.bht.jvr.core;

import de.bht.jvr.util.Color;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;

import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.core.uniforms.UniformValue;

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
 * The shader material can contain several shader contexts.
 * 
 * @author Marc Roßbach
 */

public class ShaderMaterial implements Material {
    public static ShaderMaterial makeDeferredShadingPipelineMaterial() throws Exception {
        ClassLoader cl = ShaderMaterial.class.getClassLoader();
        InputStream vsCode = cl.getResourceAsStream("de/bht/jvr/pipelineshaders/quad.vs");
        Shader vs = new Shader(vsCode, GL2ES2.GL_VERTEX_SHADER);

        InputStream afs = cl.getResourceAsStream("de/bht/jvr/pipelineshaders/deferred_ambient.fs");
        ShaderProgram shaderProgramAmbient = new ShaderProgram(vs, new Shader(afs, GL2ES2.GL_FRAGMENT_SHADER));

        InputStream lfs = cl.getResourceAsStream("de/bht/jvr/pipelineshaders/deferred_lighting.fs");
        ShaderProgram shaderProgramLighting = new ShaderProgram(vs, new Shader(lfs, GL2ES2.GL_FRAGMENT_SHADER));

        ShaderMaterial sm = new ShaderMaterial();
        sm.setShaderProgram("DSAMBIENT", shaderProgramAmbient);
        sm.setShaderProgram("DSLIGHTING", shaderProgramLighting);
        return sm;
    }

    /**
     * Creates a new phong shader material including AMBIENT, LIGHTING and
     * ATTRIBPASS shader context.
     * 
     * @return the shader material
     * @throws Exception
     *             Signals that an exception has occurred.
     */
    public static ShaderMaterial makePhongShaderMaterial() throws Exception {
        ClassLoader cl = ShaderMaterial.class.getClassLoader();

        InputStream davs = cl.getResourceAsStream("de/bht/jvr/shaders/default_ambient.vs");
        InputStream dafs = cl.getResourceAsStream("de/bht/jvr/shaders/default_ambient.fs");
        ShaderProgram shaderProgramAmbient = new ShaderProgram(new Shader(davs, GL2ES2.GL_VERTEX_SHADER), new Shader(dafs, GL2ES2.GL_FRAGMENT_SHADER));

        InputStream plvs = cl.getResourceAsStream("de/bht/jvr/shaders/phong_lighting.vs");
        InputStream plfs = cl.getResourceAsStream("de/bht/jvr/shaders/phong_lighting.fs");
        ShaderProgram shaderProgramLighting = new ShaderProgram(new Shader(plvs, GL2ES2.GL_VERTEX_SHADER), new Shader(plfs, GL2ES2.GL_FRAGMENT_SHADER));

        InputStream apvs = cl.getResourceAsStream("de/bht/jvr/shaders/attribute_pass.vs");
        InputStream apfs = cl.getResourceAsStream("de/bht/jvr/shaders/attribute_pass.fs");
        ShaderProgram shaderProgramAttribPass = new ShaderProgram(new Shader(apvs, GL2ES2.GL_VERTEX_SHADER), new Shader(apfs, GL2ES2.GL_FRAGMENT_SHADER));

        ShaderMaterial sm = new ShaderMaterial();
        sm.setShaderProgram("AMBIENT", shaderProgramAmbient);
        sm.setUniform("AMBIENT", "jvr_UseTexture0", new UniformBool(false));
        sm.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0.2f, 0.2f, 0.2f, 1.0f)));

        sm.setShaderProgram("LIGHTING", shaderProgramLighting);
        sm.setUniform("LIGHTING", "jvr_UseTexture0", new UniformBool(false));
        sm.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
        sm.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
        sm.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(90));

        sm.setShaderProgram("ATTRIBPASS", shaderProgramAttribPass);
        sm.setUniform("ATTRIBPASS", "jvr_UseTexture0", new UniformBool(false));
        sm.setUniform("ATTRIBPASS", "jvr_Material_Ambient", new UniformColor(new Color(0.2f, 0.2f, 0.2f, 1.0f)));
        sm.setUniform("ATTRIBPASS", "jvr_Material_Diffuse", new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
        sm.setUniform("ATTRIBPASS", "jvr_Material_Specular", new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
        sm.setUniform("ATTRIBPASS", "jvr_Material_Shininess", new UniformFloat(90));

        return sm;
    }

    /** The shader context map. */
    protected Map<String, ShaderContext> shaderContextMap = new HashMap<String, ShaderContext>();

    protected Map<String, ShaderProgram> shaderPrograms = new HashMap<String, ShaderProgram>();

    /** The material class. */
    protected String materialClass = "";

    /**
     * Instantiates a new shader material.
     */
    public ShaderMaterial() {}

    /**
     * Instantiates a new shader material.
     * 
     * @param shaderContext
     *            the shader context
     * @param program
     *            the program
     */
    public ShaderMaterial(String shaderContext, ShaderProgram program) {
        setShaderProgram(shaderContext, program);
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Material#bind(de.bht.jvr.core.Context)
     */
    @Override
    public boolean bind(Context ctx) throws Exception {
        GL2GL3 gl = ctx.getGL();

        ShaderContext shaderCtx = shaderContextMap.get(ctx.getShaderContext());
        if (shaderCtx != null && shaderCtx.isValid()) {
            // activate program
            ShaderProgram sp = shaderCtx.getShaderProgram();
            ctx.useShaderProgram(sp);

            // set uniforms
            HashMap<String, UniformValue> uniforms = shaderCtx.getUniforms();
            for (Entry<String, UniformValue> u : uniforms.entrySet())
                sp.setUniform(ctx, u.getKey(), u.getValue());

            // bind textures
            int tmu = 0;
            HashMap<String, Texture> textures = shaderCtx.getTextures();
            for (Entry<String, Texture> t : textures.entrySet()) {
                String name = t.getKey();
                Texture texture = t.getValue();

                gl.glActiveTexture(GL.GL_TEXTURE0 + tmu);
                texture.bind(ctx);
                sp.setUniform(ctx, name, texture.getUniformValue(tmu));
                sp.setUniform(ctx, name + "_IsSemiTransparent", new UniformBool(texture.isSemiTransparent()));

                tmu++;
            }

            return true;
        }

        return false;
    }

    /**
     * Gets the material class.
     * 
     * @return the material class
     */
    @Override
    public String getMaterialClass() {
        return materialClass;
    }

    /**
     * Gets the number of textures.
     * 
     * @param shaderContext
     *            the shader context
     * @return the number of textures
     */
    @Override
    public int getNumTextures(String shaderContext) {
        ShaderContext sc = shaderContextMap.get(shaderContext);
        if (sc != null)
            return sc.getTextures().size();
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public ShaderMaterial getRenderClone() {
        ShaderMaterial mat = new ShaderMaterial();
        mat.materialClass = materialClass;

        for (Entry<String, ShaderContext> entry : shaderContextMap.entrySet())
            mat.shaderContextMap.put(entry.getKey(), entry.getValue().getShaderContextClone());
        mat.shaderPrograms = new HashMap<String, ShaderProgram>(shaderPrograms);

        return mat;
    }

    /**
     * Returns the shader context with selected name. If shader context isn't
     * found it will create a new one and returns it.
     * 
     * @param name
     *            name of the shader context
     * @return existing or new shader context
     */
    private ShaderContext getShaderContext(String name) {
        ShaderContext shaderCtx = shaderContextMap.get(name);
        if (shaderCtx == null) {
            shaderCtx = new ShaderContext();
            shaderContextMap.put(name, shaderCtx);
        }

        return shaderCtx;
    }

    public Map<String, ShaderContext> getShaderContexts() {
        return new HashMap<String, ShaderContext>(shaderContextMap);
    }

    @Override
    public Map<String, ShaderProgram> getShaderPrograms() {
        return shaderPrograms;
    }

    /**
     * Sets the material class.
     * 
     * @param materialClass
     *            the new material class
     */
    public void setMaterialClass(String materialClass) {
        this.materialClass = materialClass;
    }

    /**
     * Sets a shader program only for selected shader context.
     * 
     * @param shaderContext
     *            the shader context
     * @param program
     *            the program
     */
    public void setShaderProgram(String shaderContext, ShaderProgram program) {
        ShaderContext shaderCtx = getShaderContext(shaderContext);
        shaderCtx.setShaderProgram(program);

        shaderPrograms.put(shaderContext, program);
    }

    /**
     * Sets the texture only for selected shader context.
     * 
     * @param shaderContext
     *            the shader context
     * @param uniformName
     *            uniform name for fragment shader
     * @param texture
     *            the texture
     */
    public void setTexture(String shaderContext, String uniformName, Texture texture) {
        if (shaderContext != null) {
            ShaderContext shaderCtx = getShaderContext(shaderContext);
            shaderCtx.setTexture(uniformName, texture);
        } else
            for (Entry<String, ShaderContext> entry : shaderContextMap.entrySet())
                entry.getValue().setTexture(uniformName, texture);
    }

    /**
     * Sets a uniform variable for this material with the selected shader
     * context.
     * 
     * @param shaderContext
     *            the shader context
     * @param uniformName
     *            the uniform name
     * @param value
     *            the value
     */
    public void setUniform(String shaderContext, String uniformName, UniformValue value) {
        if (shaderContext != null) {
            ShaderContext shaderCtx = getShaderContext(shaderContext);
            shaderCtx.setUniform(uniformName, value);
        } else
            for (Entry<String, ShaderContext> entry : shaderContextMap.entrySet())
                entry.getValue().setUniform(uniformName, value);
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("ShaderMaterial: [[ ");
        for (Entry<String, ShaderContext> entry : shaderContextMap.entrySet())
            buff.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
        buff.append("] MaterialClass: [ ").append(materialClass).append(" ]]");
        return buff.toString();
    }

    /*
     * (non-Javadoc)
     * @see de.bht.jvr.core.Material#unbind(de.bht.jvr.core.Context)
     */
    @Override
    public void unbind(Context ctx) throws Exception {
        // TODO
    }
}
