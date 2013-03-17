package de.bht.jvr.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GL3;

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
 * The OpenGL shader can either be a vertex, geometry or fragment shader.
 * 
 * @author Marc Roßbach
 */
public class Shader {
    private static String streamToString(InputStream is) throws IOException {
        StringBuffer buff = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = in.readLine()) != null) {
            buff.append(line);
            buff.append("\n");
        }
        return buff.toString();
    }

    /**
     * WORKAROUND for a known bug in the Mac OS X shader compiler and the Nvidia
     * GeForce Driver v270.61 for Windows. This function replaces all "discard"
     * statements in a fragment shader with "_dcrd()". The new function
     * "_dcrd()" is inserted in the first line of the shader code.
     * 
     * @param code
     *            The code of a fragment shader
     * @return The modified shader code
     */
    private static StringBuffer applyDiscardBugWorkaround(StringBuffer code) {
        Pattern mainPat = Pattern.compile("void\\W+main\\W");
        // check if this is a "main" shader
        if (mainPat.matcher(code).find(0)) {
            Pattern discardPat = Pattern.compile("\\Wdiscard\\W");
            Matcher discardMat = discardPat.matcher(code);
            boolean foundDiscard = false;
            // find all "discard" statements
            while (discardMat.find()) {
                foundDiscard = true;
                String found = code.substring(discardMat.start(), discardMat.end());
                // replace with a function with the same string length
                code.replace(discardMat.start(), discardMat.end(), found.replaceFirst("discard", "_dcrd()"));
            }

            if (foundDiscard)
                code.insert(0, "void _dcrd(){discard;} ");
        }
        return code;
    }

    /** The gl id. */
    private ContextValueMap<Integer> id = new ContextValueMap<Integer>(-1);

    /** Is compiled. */
    private ContextValueMap<Boolean> compiled = new ContextValueMap<Boolean>(false);

    /** The shader code. */
    private String code;

    /** The shader type. */
    private int type = 0;

    /** The shader name. */
    private String name;

    /**
     * Instantiates a new shader.
     * 
     * @param file
     *            the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public Shader(File file) throws IOException {
        String shaderType = file.getName().substring(file.getName().length() - 3);

        // set file name as shader name
        name = "\"" + file.getName() + "\"";

        // detect shader type
        if (shaderType.equals(".vs"))
            type = GL2ES2.GL_VERTEX_SHADER;
        if (shaderType.equals(".fs"))
            type = GL2ES2.GL_FRAGMENT_SHADER;
        if (shaderType.equals(".gs"))
            type = GL3.GL_GEOMETRY_SHADER;
        if (shaderType.equals(".tc"))
            type = GL3.GL_TESS_CONTROL_SHADER;
        if (shaderType.equals(".te"))
            type = GL3.GL_TESS_EVALUATION_SHADER;

        if (type != 0) {
            Log.info(this.getClass(), "Reading shader code from file: " + file.getName());
            StringBuffer buff = new StringBuffer();
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = in.readLine()) != null) {
                buff.append(line);
                buff.append("\n");
            }
            if (type == GL2ES2.GL_FRAGMENT_SHADER)
                code = applyDiscardBugWorkaround(buff).toString();
            else
                code = buff.toString();
        } else
            Log.error(this.getClass(), "Unknown shader type: " + shaderType);
    }

    /**
     * Instantiates a new shader.
     * 
     * @param shaderCode
     *            the shader code
     * @param shaderType
     *            the shader type
     */
    public Shader(InputStream shaderCode, int shaderType) throws IOException {
        this(streamToString(shaderCode), shaderType);
    }

    /**
     * Instantiates a new shader.
     * 
     * @param shaderCode
     *            the shader code
     * @param shaderType
     *            the shader type
     */
    public Shader(String shaderCode, int shaderType) {
        switch (shaderType) {
        case GL2ES2.GL_VERTEX_SHADER:
            name = "[VERTEX SHADER]";
            break;
        case GL2ES2.GL_FRAGMENT_SHADER:
            name = "[FRAGMENT SHADER]";
            break;
        case GL3.GL_GEOMETRY_SHADER:
            name = "[GEOMETRY SHADER]";
            break;
        case GL3.GL_TESS_CONTROL_SHADER:
            name = "[TESSELATION CONTROL SHADER]";
            break;
        case GL3.GL_TESS_EVALUATION_SHADER:
            name = "[TESSELATION EVALUATION SHADER]";
            break;
        default:
            name = "[UNKNOWN]";
            Log.warning(this.getClass(), "Unknown shader type: " + shaderType);
        }
        type = shaderType;
        if (type == GL2ES2.GL_FRAGMENT_SHADER)
            code = applyDiscardBugWorkaround(new StringBuffer(shaderCode)).toString();
        else
            code = shaderCode;
    }

    /**
     * Compile the shader code.
     * 
     * @param ctx
     *            the ctx
     * @throws Exception
     *             the exception
     */
    public void compile(Context ctx) throws Exception {
        Log.info(this.getClass(), "Compiling shader: " + name);
        GL2GL3 gl = ctx.getGL();

        int id = gl.glCreateShader(type);
        this.id.put(ctx, new Integer(id));
        gl.glShaderSource(id, 1, new String[] { code }, (int[]) null, 0);
        gl.glCompileShader(id);

        int[] compiled = new int[1];
        gl.glGetShaderiv(id, GL2ES2.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == GL.GL_FALSE) {
            this.compiled.put(ctx, new Boolean(false));

            int[] logLength = new int[1];
            gl.glGetShaderiv(id, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            String error = "Error compiling GLSL-Shader " + name;
            if (logLength[0] > 0) {
                byte[] log = new byte[logLength[0]];
                gl.glGetShaderInfoLog(id, logLength[0], (int[]) null, 0, log, 0);
                error += ": " + new String(log);
            }
            gl.glDeleteShader(id);
            throw new Exception(error);
        } else
            this.compiled.put(ctx, new Boolean(true));
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() {
        List<Context> ctxList = compiled.getContextList();
        for (Context ctx : ctxList)
            if (isCompiled(ctx))
                ctx.deleteShader(getId(ctx));
    }

    /**
     * Gets the id of the shader.
     * 
     * @param ctx
     *            the context
     * @return the id
     */
    public int getId(Context ctx) {
        return id.get(ctx);
    }

    /**
     * Checks if is compiled.
     * 
     * @param ctx
     *            the context
     * @return true, if is compiled
     */
    public boolean isCompiled(Context ctx) {
        return compiled.get(ctx);
    }

    /**
     * Gets the type of this shader
     * 
     * @return the type
     */
    protected int getType() {
        return this.type;
    }
}
