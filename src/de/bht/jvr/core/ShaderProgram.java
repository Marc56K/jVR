package de.bht.jvr.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GL3;

import de.bht.jvr.core.attributes.Attribute;
import de.bht.jvr.core.attributes.AttributeArray;
import de.bht.jvr.core.uniforms.Uniform;
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
 * The shader program.
 * 
 * @author Marc Roßbach
 */
public class ShaderProgram {
    /** The id of the shader program. */
    private ContextValueMap<HashMap<Integer, Integer>> id = new ContextValueMap<HashMap<Integer, Integer>>();

    /** The shader program is linked. */
    private ContextValueMap<HashMap<Integer, Boolean>> linked = new ContextValueMap<HashMap<Integer, Boolean>>();

    /** The shaders. */
    private Collection<Shader> shader = new ArrayList<Shader>();

    /** The uniforms. */
    private ContextValueMap<HashMap<String, Uniform>> uniforms = new ContextValueMap<HashMap<String, Uniform>>();

    /** The attributes. */
    private ContextValueMap<HashMap<String, Attribute>> attributes = new ContextValueMap<HashMap<String, Attribute>>();

    /** The program parameters **/
    private Map<Integer, Integer> parameters = new TreeMap<Integer, Integer>();

    /**
     * A list of clipping shader for a different number of clipping planes (key
     * = number of clipping planes)
     */
    private static ContextValueMap<HashMap<Integer, Shader>> clippingFragmentShader = new ContextValueMap<HashMap<Integer, Shader>>();

    /**
     * A list of clipping shader for a different number of clipping planes (key
     * = number of clipping planes)
     */
    private static ContextValueMap<HashMap<Integer, Shader>> clippingVertexShader = new ContextValueMap<HashMap<Integer, Shader>>();

    private int activeClipPlaneCount = -1;

    private boolean hasTessellationShader = false;

    /**
     * Instantiates a new shader program.
     * 
     * @param shader
     *            the shader
     */
    public ShaderProgram(Collection<Shader> shader) {
        Log.info(this.getClass(), "Created new shader program.");
        this.shader = shader;
    }

    /**
     * Instantiates a new shader program.
     * 
     * @param shaderFiles
     *            the shader files
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public ShaderProgram(File... shaderFiles) throws IOException {
        for (File file : shaderFiles)
            shader.add(new Shader(file));
    }

    /**
     * Instantiates a new shader program.
     * 
     * @param shader
     *            the shader
     */
    public ShaderProgram(Shader... shader) {
        Log.info(this.getClass(), "Created new shader program.");
        for (Shader s : shader)
            this.shader.add(s);
    }

    private boolean isLinked(Context ctx) {
        HashMap<Integer, Boolean> linkedMap = linked.get(ctx);
        if (linkedMap != null) {
            Boolean result = linkedMap.get(ctx.getClipPlaneCount());
            if (result != null)
                return result;
        }
        return false;
    }

    private void setLinked(Context ctx, boolean value) {
        HashMap<Integer, Boolean> linkedMap = linked.get(ctx);
        if (linkedMap == null) {
            linkedMap = new HashMap<Integer, Boolean>();
            linked.put(ctx, linkedMap);
        }
        linkedMap.put(ctx.getClipPlaneCount(), value);
    }

    private int getId(Context ctx) {
        HashMap<Integer, Integer> idMap = id.get(ctx);
        if (idMap != null) {
            Integer result = idMap.get(ctx.getClipPlaneCount());
            if (result != null)
                return result;
        }
        return -1;
    }

    private void setId(Context ctx, int value) {
        HashMap<Integer, Integer> idMap = id.get(ctx);
        if (idMap == null) {
            idMap = new HashMap<Integer, Integer>();
            id.put(ctx, idMap);
        }
        idMap.put(ctx.getClipPlaneCount(), value);
    }

    /**
     * Bind attribute array.
     * 
     * @param ctx
     *            the context
     * @param name
     *            the name
     * @param value
     *            the value
     * @return true, if successful
     * @throws Exception
     *             the exception
     */
    public boolean bindAttribArray(Context ctx, String name, AttributeArray value) throws Exception {
        if (isLinked(ctx)) {
            HashMap<String, Attribute> attribMap = attributes.get(ctx);
            Attribute attribute = attribMap.get(name);
            if (attribute == null)
                // Log.warning(this.getClass(), "No attribute named: " + name);
                return false;
            else
                try {
                    attribute.bind(ctx, value);
                    return true;
                } catch (Exception e) {
                    throw new Exception("Attribute error: [" + name + "] " + e.getMessage());
                }
        }

        return false;
    }

    /**
     * Dump attributes.
     * 
     * @param ctx
     *            the context
     */
    public void dumpAttributes(Context ctx) {
        HashMap<String, Attribute> attribMap = attributes.get(ctx);
        for (Entry<String, Attribute> entry : attribMap.entrySet())
            Log.info(this.getClass(), "Attribute: [" + entry.getKey() + "] " + entry.getValue());
    }

    /**
     * Dump uniforms.
     * 
     * @param ctx
     *            the ctx
     */
    public void dumpUniforms(Context ctx) {
        HashMap<String, Uniform> uniMap = uniforms.get(ctx);
        for (Entry<String, Uniform> entry : uniMap.entrySet())
            Log.info(this.getClass(), "Uniform: [" + entry.getKey() + "] " + entry.getValue());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() {
        List<Context> ctxList = linked.getContextList();
        for (Context ctx : ctxList)
            if (isLinked(ctx))
                ctx.deleteShaderProgram(getId(ctx));
    }

    /**
     * Checks whether the program use a attribute with the name.
     * 
     * @param ctx
     *            the context
     * @param name
     *            the name of the attribute variable
     * @return true, if program use a attribute with the name.
     */
    public boolean hasAttribArray(Context ctx, String name) {
        HashMap<String, Attribute> attribMap = attributes.get(ctx);
        return attribMap.get(name) != null;
    }

    /**
     * Checks whether the program use a uniform variable with the name.
     * 
     * @param ctx
     *            the context
     * @param name
     *            the name of the uniform variable
     * @return true, if program use a uniform variable with the name.
     */
    public boolean hasUniform(Context ctx, String name) {
        HashMap<String, Uniform> uniMap = uniforms.get(ctx);
        return uniMap.get(name) != null;
    }

    /**
     * Creates and compiles a clipping shader for the actual number of clipping
     * planes of the context
     * 
     * @param ctx
     *            the context
     * @return the compiled clipping shader
     * @throws Exception
     */
    private static Shader makeClippingFragmentShader(Context ctx) throws Exception {
        HashMap<Integer, Shader> clipFsMap = clippingFragmentShader.get(ctx);
        if (clipFsMap == null) {
            clipFsMap = new HashMap<Integer, Shader>();
            clippingFragmentShader.put(ctx, clipFsMap);
        }

        Shader shader = clipFsMap.get(ctx.getClipPlaneCount());
        if (shader == null) {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < ctx.getClipPlaneCount(); i++) {
                sb.append(String.format("uniform bool jvr_UseClipPlane%d;", i));
                sb.append(String.format("varying float jvr_ClipPlaneDistance%d;", i));
            }

            sb.append("void jvr_discardIfClipped() {");
            for (int i = 0; i < ctx.getClipPlaneCount(); i++)
                sb.append(String.format("if (jvr_UseClipPlane%d && jvr_ClipPlaneDistance%d < 0.0) discard;", i, i));
            sb.append("}");

            shader = new Shader(sb.toString(), GL2ES2.GL_FRAGMENT_SHADER);
            clipFsMap.put(ctx.getClipPlaneCount(), shader);
        }

        if (!shader.isCompiled(ctx))
            shader.compile(ctx);

        return shader;
    }

    /**
     * Creates and compiles a clipping shader for the actual number of clipping
     * planes of the context
     * 
     * @param ctx
     *            the context
     * @return the compiled clipping shader
     * @throws Exception
     */
    private static Shader makeClippingVertexShader(Context ctx) throws Exception {
        HashMap<Integer, Shader> clipVsMap = clippingVertexShader.get(ctx);
        if (clipVsMap == null) {
            clipVsMap = new HashMap<Integer, Shader>();
            clippingVertexShader.put(ctx, clipVsMap);
        }

        Shader shader = clipVsMap.get(ctx.getClipPlaneCount());
        if (shader == null) {
            StringBuffer sb = new StringBuffer();
            if (ctx.getClipPlaneCount() > 0) {
                for (int i = 0; i < ctx.getClipPlaneCount(); i++) {
                    sb.append(String.format("uniform bool jvr_UseClipPlane%d;", i));
                    sb.append(String.format("uniform vec4 jvr_ClipPlane%d;", i));
                    sb.append(String.format("varying float jvr_ClipPlaneDistance%d;", i));
                }

                sb.append("float jvr_calculateClipDist(vec4 vertex, vec4 plane) {");
                sb.append(" vec3 v = vertex.xyz / vertex.w;");
                sb.append(" return (plane.x * v.x + plane.y * v.y + plane.z * v.z - plane.w);");
                sb.append("}");

                sb.append("void jvr_calculateClipping(vec4 vertex) {");
                for (int i = 0; i < ctx.getClipPlaneCount(); i++) {
                    sb.append(String.format("if (jvr_UseClipPlane%d)", i));
                    sb.append(String.format("jvr_ClipPlaneDistance%d = jvr_calculateClipDist(vertex, jvr_ClipPlane%d);", i, i));
                }
                sb.append("}");
            } else
                sb.append("void jvr_calculateClipping(vec4 vertex) {}");

            shader = new Shader(sb.toString(), GL2ES2.GL_VERTEX_SHADER);
        }

        if (!shader.isCompiled(ctx))
            shader.compile(ctx);

        return shader;
    }

    public int GetActiveClipPlaneCount() {
        return this.activeClipPlaneCount;
    }

    /**
     * Link the shader program.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public void link(Context ctx) throws Exception {
        Log.info(this.getClass(), "Linking shader program: " + this);

        GL2GL3 gl = ctx.getGL();
        int id = gl.glCreateProgram();
        setId(ctx, id);

        for (Shader shader : this.shader) {
            if (!shader.isCompiled(ctx))
                shader.compile(ctx);

            if (shader.isCompiled(ctx)) {
                gl.glAttachShader(id, shader.getId(ctx));
                int type = shader.getType();
                if (type == GL3.GL_TESS_CONTROL_SHADER || type == GL3.GL_TESS_EVALUATION_SHADER) {
                    this.hasTessellationShader = true;
                }
            }
        }

        // clipping vertex shader
        Shader clipVs = makeClippingVertexShader(ctx);
        gl.glAttachShader(id, clipVs.getId(ctx));

        // clipping fragment shader
        Shader clipFs = makeClippingFragmentShader(ctx);
        gl.glAttachShader(id, clipFs.getId(ctx));

        // set paramters
        synchronized (parameters) {
            for (Entry<Integer, Integer> p : parameters.entrySet())
                gl.glProgramParameteri(id, p.getKey(), p.getValue());
        }

        gl.glLinkProgram(id);

        int[] linked = new int[1];
        gl.glGetProgramiv(id, GL2ES2.GL_LINK_STATUS, linked, 0);
        if (linked[0] == GL.GL_FALSE) {
            setLinked(ctx, false);

            int[] logLength = new int[1];
            gl.glGetProgramiv(id, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];

            String error = "Error linking GLSL-Program";
            if (logLength[0] > 0) {
                gl.glGetProgramInfoLog(id, logLength[0], (int[]) null, 0, log, 0);
                error += ": " + new String(log);
            }
            gl.glDeleteProgram(id);
            throw new Exception(error);
        } else
            setLinked(ctx, true);

        // Extract uniforms.
        HashMap<String, Uniform> uniMap = uniforms.get(ctx);
        if (uniMap == null) {
            uniMap = new HashMap<String, Uniform>();
            uniforms.put(ctx, uniMap);
        }

        Pattern pat = Pattern.compile("[\\[].*[\\]]");
        int[] ucount = new int[1];
        gl.glGetProgramiv(id, GL2ES2.GL_ACTIVE_UNIFORMS, ucount, 0);
        int[] ulength = new int[1];
        gl.glGetProgramiv(id, GL2ES2.GL_ACTIVE_UNIFORM_MAX_LENGTH, ulength, 0);
        Log.info(this.getClass(), "Found shader uniforms: " + ucount[0]);
        for (int i = 0; i != ucount[0]; i++) {
            byte[] name = new byte[ulength[0]];
            int[] namesize = new int[1];
            int[] size = new int[1];
            int[] type = new int[1];
            gl.glGetActiveUniform(id, i, ulength[0], namesize, 0, size, 0, type, 0, name, 0);
            String uname = new String(name).substring(0, namesize[0]);

            // Mac OS X fix
            uname = pat.matcher(uname).replaceAll("");

            int location = gl.glGetUniformLocation(id, uname);

            Uniform uni = new Uniform(ctx, location, type[0], size[0]);
            uniMap.put(uname, uni);
        }
        dumpUniforms(ctx);

        // Extract attributes.
        HashMap<String, Attribute> attribMap = attributes.get(ctx);
        if (attribMap == null) {
            attribMap = new HashMap<String, Attribute>();
            attributes.put(ctx, attribMap);
        }

        int[] acount = new int[1];
        gl.glGetProgramiv(id, GL2ES2.GL_ACTIVE_ATTRIBUTES, acount, 0);
        int[] alength = new int[1];
        gl.glGetProgramiv(id, GL2ES2.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, alength, 0);
        Log.info(this.getClass(), "Found shader attributes: " + acount[0]);
        for (int i = 0; i != acount[0]; i++) {
            byte[] name = new byte[alength[0]];
            int[] namesize = new int[1];
            int[] size = new int[1];
            int[] type = new int[1];
            gl.glGetActiveAttrib(id, i, alength[0], namesize, 0, size, 0, type, 0, name, 0);
            String aname = new String(name).substring(0, namesize[0]);

            // Mac OS X fix
            aname = pat.matcher(aname).replaceAll("");

            int location = gl.glGetAttribLocation(id, aname);

            Attribute attrib = new Attribute(ctx, location, type[0], size[0]);
            attribMap.put(aname, attrib);
        }
        dumpAttributes(ctx);
    }

    /**
     * Sets compiling parameters.
     * 
     * @param name
     * @param value
     */
    public void setParameter(int name, int value) {
        if (linked.getSize() == 0)
            parameters.put(name, value);
        else
            throw new RuntimeException("Program has already been linked.");
    }

    /**
     * Sets the uniform.
     * 
     * @param ctx
     *            the ctx
     * @param name
     *            the name
     * @param value
     *            the value
     * @throws Exception
     *             the exception
     */
    public void setUniform(Context ctx, String name, UniformValue value) throws Exception {
        Uniform uni = uniforms.get(ctx).get(name);
        if (uni != null)
            uni.bind(ctx, value);
    }

    /**
     * Unbind attribute array.
     * 
     * @param ctx
     *            the context
     * @param name
     *            the name
     * @param value
     *            the value
     * @return true, if successful
     * @throws Exception
     *             the exception
     */
    public boolean unbindAttribArray(Context ctx, String name, AttributeArray value) throws Exception {
        if (isLinked(ctx)) {
            HashMap<String, Attribute> attribMap = attributes.get(ctx);
            Attribute attribute = attribMap.get(name);
            if (attribute == null)
                // Log.warning(this.getClass(), "No attribute named: " + name);
                return false;
            else
                try {
                    attribute.unbind(ctx, value);
                    return true;
                } catch (Exception e) {
                    throw new Exception("Attribute error: [" + name + "] " + e.getMessage());
                }
        }

        return false;
    }

    /**
     * Use the shader program.
     * 
     * @param ctx
     *            the context
     * @throws Exception
     *             the exception
     */
    public void use(Context ctx) throws Exception {
        GL2GL3 gl = ctx.getGL();

        if (!isLinked(ctx))
            link(ctx);

        gl.glUseProgram(getId(ctx));
        activeClipPlaneCount = ctx.getClipPlaneCount();
    }

    /**
     * @return True, if this program uses tessellation shader.
     */
    protected boolean hasTessellationShader() {
        return this.hasTessellationShader;
    }
}
