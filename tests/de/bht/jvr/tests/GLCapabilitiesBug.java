package de.bht.jvr.tests;

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
public class GLCapabilitiesBug implements GLEventListener {
    /**
     * @param args
     */
    public static void main(String[] args) {
        new GLCapabilitiesBug();
    }

    private GLWindow window;

    public GLCapabilitiesBug() {
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
        caps.setStencilBits(8);
        // caps.setSampleBuffers(true);
        return caps;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        String code = "void main(void){gl_Position = vec4(0,0,0,1);}";

        GL2GL3 gl = drawable.getGL().getGL2GL3();

        int id = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER);

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
    }

    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {}
}
