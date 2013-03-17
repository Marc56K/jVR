package de.bht.jvr.tests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import com.jogamp.newt.opengl.GLWindow;
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
public class MacShaderLinkerBug implements GLEventListener {
    /**
     * @param args
     */
    public static void main(String[] args) {
        new MacShaderLinkerBug();
    }

    private GLWindow window;

    public MacShaderLinkerBug() {
        window = GLWindow.create(getGLCapabilities());
        window.addGLEventListener(this);
        window.setVisible(true);

        while (true)
            window.display();
    }

    @Override
    public void display(GLAutoDrawable drawable) {

    }

    @Override
    public void dispose(GLAutoDrawable arg0) {}

    protected GLCapabilities getGLCapabilities() {
        GLProfile glp = GLProfile.get("GL2GL3");
        GLCapabilities caps = new GLCapabilities(glp);
        return caps;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2GL3 gl = drawable.getGL().getGL2GL3();

        // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // create vertex shader
        int vs1 = compileShader(gl, loadShaderFile("data/shader/ambient.vs"), GL2ES2.GL_VERTEX_SHADER);
        int vs2 = compileShader(gl, "void jvr_calculateClipping(vec4 vertex){}", GL2ES2.GL_VERTEX_SHADER);

        // create fragment shader
        int fs1 = compileShader(gl, loadShaderFile("data/shader/ambient.fs"), GL2ES2.GL_FRAGMENT_SHADER);
        int fs2 = compileShader(gl, "void jvr_discardIfClipped(){}", GL2ES2.GL_FRAGMENT_SHADER);

        // link shader program
        int prog = gl.glCreateProgram();
        gl.glAttachShader(prog, vs1);
        gl.glAttachShader(prog, fs1);
        gl.glAttachShader(prog, vs2);
        gl.glAttachShader(prog, fs2);
        linkShaderProgram(gl, prog);

        gl.glUseProgram(prog); // <-- crashes on mac
        // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    public static String loadShaderFile(String path) {
        StringBuilder s = new StringBuilder();
        try {

            BufferedReader in = new BufferedReader(new FileReader(path));
            String line = null;
            while ((line = in.readLine()) != null) {
                s.append(line);
                s.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }

    public static void linkShaderProgram(GL2GL3 gl, int id) {
        gl.glLinkProgram(id);
        int[] linked = new int[1];
        gl.glGetProgramiv(id, GL2ES2.GL_LINK_STATUS, linked, 0);
        if (linked[0] == GL.GL_FALSE) {

            int[] logLength = new int[1];
            gl.glGetProgramiv(id, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];

            String error = "Error linking GLSL-Program";
            if (logLength[0] > 0) {
                gl.glGetProgramInfoLog(id, logLength[0], (int[]) null, 0, log, 0);
                error += ": " + new String(log);
            }
            gl.glDeleteProgram(id);
            System.err.println(error);
        } else
            System.out.println("Program linked: id=" + id);
    }

    public static int compileShader(GL2GL3 gl, String code, int type) {
        int id = gl.glCreateShader(type);

        gl.glShaderSource(id, 1, new String[] { code }, (int[]) null, 0);
        gl.glCompileShader(id);

        int[] compiled = new int[1];
        gl.glGetShaderiv(id, GL2ES2.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == GL.GL_FALSE) {
            int[] logLength = new int[1];
            gl.glGetShaderiv(id, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(id, logLength[0], (int[]) null, 0, log, 0);

            System.err.println("Error compiling the shader: " + new String(log));

            gl.glDeleteShader(id);
        } else
            System.out.println("Shader compiled: id=" + id);

        return id;
    }

    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {}
}
