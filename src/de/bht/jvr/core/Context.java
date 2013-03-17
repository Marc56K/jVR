package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GL3bc;

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
 * Every render window have its own context object.
 * 
 * @author Marc Roßbach
 */
public class Context {
    /** A static counter to generate a id for every context */
    private static int idCounter = 0;

    /** The id of this context */
    private int id;

    /** The gl object. */
    private GL gl = null;

    /** The active shader program. */
    private ShaderProgram shaderProgram = null;
    
    private FrameBuffer fbo = null;

    /** The active shader context. */
    private String shaderContext = "";

    /** The number of active clipping planes */
    private int clipPlaneCount = 0;
    
    private boolean enableDisplayLists = false;

    /**
     * Instantiates a new context.
     * 
     * @param gl
     *            the gl object
     */
    public Context(GL gl) {
        id = idCounter++;
        this.gl = gl;
        
        String vendor = this.getGL().glGetString(0x1F00);
        enableDisplayLists = vendor.contains("NVIDIA");
    }

    /*--------------------------- garbage collection for OpenGL objects ------------------------------*/

    private List<Integer> delTextures = new ArrayList<Integer>();

    private List<Integer> delShaderProgram = new ArrayList<Integer>();

    private List<Integer> delShader = new ArrayList<Integer>();

    private List<Integer> delFbos = new ArrayList<Integer>();

    private List<Integer> delRbos = new ArrayList<Integer>();

    private List<Integer> delVbos = new ArrayList<Integer>();

    private List<Integer> delDisplayLists = new ArrayList<Integer>();

    public void deleteFbo(int glId) {
        synchronized (delFbos) {
            delFbos.add(glId);
        }
    }

    public void deleteRbo(int glId) {
        synchronized (delRbos) {
            delRbos.add(glId);
        }
    }

    public void deleteShader(int glId) {
        synchronized (delShader) {
            delShader.add(glId);
        }
    }

    public void deleteShaderProgram(int glId) {
        synchronized (delShaderProgram) {
            delShaderProgram.add(glId);
        }
    }

    public void deleteTexture(int glId) {
        synchronized (delTextures) {
            delTextures.add(glId);
        }
    }

    public void deleteVbo(int glId) {
        synchronized (delVbos) {
            delVbos.add(glId);
        }
    }

    public void deleteDisplayList(int glId) {
        synchronized (delDisplayLists) {
            delDisplayLists.add(glId);
        }
    }

    public void doGarbageCollection() {
        synchronized (delTextures) {
            for (Integer id : delTextures)
                getGL().glDeleteTextures(1, new int[] { id }, 0);
            delTextures.clear();
        }

        synchronized (delShaderProgram) {
            for (Integer id : delShaderProgram)
                getGL().glDeleteProgram(id);
            delShaderProgram.clear();
        }

        synchronized (delShader) {
            for (Integer id : delShader)
                getGL().glDeleteShader(id);
            delShader.clear();
        }

        synchronized (delFbos) {
            for (Integer id : delFbos)
                getGL().glDeleteFramebuffers(1, new int[] { id }, 0);
            delFbos.clear();
        }

        synchronized (delRbos) {
            for (Integer id : delRbos)
                getGL().glDeleteRenderbuffers(1, new int[] { id }, 0);
            delRbos.clear();
        }

        synchronized (delVbos) {
            for (Integer id : delVbos)
                getGL().glDeleteBuffers(1, new int[] { id }, 0);
            delVbos.clear();
        }

        synchronized (delDisplayLists) {
            for (Integer id : delDisplayLists)
                ((GL3bc) getGL()).glDeleteLists(id, 1);
			delDisplayLists.clear();
        }
    }

    /**
     * Gets the gl object.
     * 
     * @return the gl object
     */
    public GL2GL3 getGL() {
        return (GL2GL3) gl;
    }

    /**
     * Gets the id of the context
     * 
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the active shader context.
     * 
     * @return the shader context
     */
    public String getShaderContext() {
        return shaderContext;
    }

    /**
     * Sets the shader context.
     * 
     * @param shaderContext
     *            the new shader context
     */
    public void setShaderContext(String shaderContext) {
        this.shaderContext = shaderContext;
    }

    /**
     * Gets the number of active clipping planes
     * 
     * @return the number of clipping planes
     */
    public int getClipPlaneCount() {
        return this.clipPlaneCount;
    }

    /**
     * Sets the number of active clipping planes.
     * 
     * @param count
     *            the number of clipping planes
     */
    public void setClipPlaneCount(int count) {
        this.clipPlaneCount = count;
    }

    /**
     * Gets the active shader program.
     * 
     * @return the shader program
     */
    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    /**
     * Use shader program.
     * 
     * @param shaderProgram
     *            the shader program
     * @throws Exception
     *             the exception
     */
    public void useShaderProgram(ShaderProgram shaderProgram) throws Exception {
        if (gl != null && shaderProgram != null && (shaderProgram != this.shaderProgram || shaderProgram.GetActiveClipPlaneCount() != this.getClipPlaneCount())) {
            this.shaderProgram = shaderProgram;
            shaderProgram.use(this);
        }
    }
    
    public void bindFbo(FrameBuffer fbo) throws Exception {
        if (this.fbo != fbo) {
            if (this.fbo != null)
                this.fbo.unbind(this);
            if (fbo != null)
                fbo.bind(this);
            this.fbo = fbo;
        }            
    }
    
    public boolean displayListsSupported() {
        return enableDisplayLists;
    }
}
