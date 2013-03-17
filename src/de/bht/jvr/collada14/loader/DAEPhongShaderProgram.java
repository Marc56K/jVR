package de.bht.jvr.collada14.loader;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2ES2;

import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderProgram;

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
 * This class is used by de.bht.jvr.collada14.loader.DAEEffect
 * 
 * @author Marc Roßbach
 */
public class DAEPhongShaderProgram {
    private ShaderProgram shaderProgramAmbient;
    private ShaderProgram shaderProgramLighting;
    private ShaderProgram shaderProgramAttribPass;

    public DAEPhongShaderProgram() throws IOException {
        ClassLoader cl = getClass().getClassLoader();

        InputStream davs = cl.getResourceAsStream("de/bht/jvr/shaders/default_ambient.vs");
        InputStream dafs = cl.getResourceAsStream("de/bht/jvr/shaders/default_ambient.fs");
        shaderProgramAmbient = new ShaderProgram(new Shader(davs, GL2ES2.GL_VERTEX_SHADER), new Shader(dafs, GL2ES2.GL_FRAGMENT_SHADER));

        InputStream plvs = cl.getResourceAsStream("de/bht/jvr/shaders/phong_lighting.vs");
        InputStream plfs = cl.getResourceAsStream("de/bht/jvr/shaders/phong_lighting.fs");
        shaderProgramLighting = new ShaderProgram(new Shader(plvs, GL2ES2.GL_VERTEX_SHADER), new Shader(plfs, GL2ES2.GL_FRAGMENT_SHADER));

        InputStream apvs = cl.getResourceAsStream("de/bht/jvr/shaders/attribute_pass.vs");
        InputStream apfs = cl.getResourceAsStream("de/bht/jvr/shaders/attribute_pass.fs");
        shaderProgramAttribPass = new ShaderProgram(new Shader(apvs, GL2ES2.GL_VERTEX_SHADER), new Shader(apfs, GL2ES2.GL_FRAGMENT_SHADER));
    }

    public ShaderProgram getShaderProgramAmbient() {
        return shaderProgramAmbient;
    }

    public ShaderProgram getShaderProgramAttribPass() {
        return shaderProgramAttribPass;
    }

    public ShaderProgram getShaderProgramLighting() {
        return shaderProgramLighting;
    }
}
