package de.bht.jvr.shaderviewer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.bht.jvr.core.Shader;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.core.uniforms.UniformValue;
import de.bht.jvr.core.uniforms.UniformVector2;
import de.bht.jvr.core.uniforms.UniformVector3;
import de.bht.jvr.core.uniforms.UniformVector4;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.math.Vector4;

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

public class Loader {

    public class LoadException extends Exception {
        private static final long serialVersionUID = -3468191297842559118L;

        File dir;
        File file;

        public LoadException(Throwable cause, File dir, File file) {
            super(cause);
            this.dir = dir;
            this.file = file;
        }

        @Override
        public String getMessage() {
            return "Error loading project: " + dir.getName() + "  , file: " + file.getName();
        }
    }

    String projectName = "";
    Map<String, Texture2D> samplers = new HashMap<String, Texture2D>();
    Map<String, UniformValue> uniforms = new HashMap<String, UniformValue>();
    Map<String, Shader> shaders = new HashMap<String, Shader>();

    private Pattern floatP = Pattern.compile("^\\s*([0-9.-]+)\\s*$");

    private Pattern vec2fP = Pattern.compile("^\\s*([0-9.-]+)\\s+([0-9.-]+)\\s*$");

    private Pattern vec3fP = Pattern.compile("^\\s*([0-9.-]+)\\s+([0-9.-]+)\\s+([0-9.-]+)\\s*$");

    private Pattern vec4fP = Pattern.compile("^\\s*([0-9.-]+)\\s+([0-9.-]+)\\s+([0-9.-]+)\\s+([0-9.-]+)\\s*$");

    public Loader() {}

    private boolean hasExtension(String name, String[] exts) {
        for (String e : exts)
            if (name.endsWith(e) && !name.startsWith("."))
                return true;
        return false;
    }

    public void load(File projectDir) throws Exception {
        String[] uexts = { ".uniforms" };
        String[] sexts = { ".vs", ".fs", ".gs" };
        String[] texts = { ".jpg", ".png" };

        samplers = new HashMap<String, Texture2D>();
        uniforms = new HashMap<String, UniformValue>();
        shaders = new HashMap<String, Shader>();

        for (File f : projectDir.listFiles())
            try {
                if (hasExtension(f.getName(), uexts))
                    loadUniforms(projectDir, f);
                else if (hasExtension(f.getName(), sexts))
                    loadShader(f);
                else if (hasExtension(f.getName(), texts))
                    loadTexture(f);
            } catch (Exception e) {
                throw new LoadException(e, projectDir, f);
            }
    }

    private void loadShader(File f) throws Exception {
        shaders.put(f.getName(), new Shader(f));
    }

    private void loadTexture(File f) throws Exception {
        samplers.put(f.getName(), new Texture2D(f));
    }

    private void loadUniforms(File base, File f) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(f);

        NodeList project = document.getElementsByTagName("project");
        if (project.getLength() == 1)
            projectName = project.item(0).getAttributes().getNamedItem("name").getNodeValue();
        else
            projectName = f.getName();
        NodeList uniformsL = document.getElementsByTagName("uniform");
        NodeList samplersL = document.getElementsByTagName("sampler");

        for (int i = 0; i != uniformsL.getLength(); i++) {
            Node uniform = uniformsL.item(i);
            String name = uniform.getAttributes().getNamedItem("name").getNodeValue();
            String type = uniform.getAttributes().getNamedItem("type").getNodeValue();
            String value = uniform.getChildNodes().item(0).getNodeValue();

            if (type.equals("float")) {
                Matcher m = floatP.matcher(value);
                if (m.matches())
                    uniforms.put(name, new UniformFloat(Float.parseFloat(m.group(1))));
                else
                    throw new Exception("Can not parse float value: " + value);
            } else if (type.equals("vec2")) {
                Matcher m = vec2fP.matcher(value);
                if (m.matches())
                    uniforms.put(name, new UniformVector2(new Vector2(Float.parseFloat(m.group(1)), Float.parseFloat(m.group(2)))));
                else
                    throw new Exception("Can not parse vec2 value: " + value);
            } else if (type.equals("vec3")) {
                Matcher m = vec3fP.matcher(value);
                if (m.matches())
                    uniforms.put(name, new UniformVector3(new Vector3(Float.parseFloat(m.group(1)), Float.parseFloat(m.group(2)), Float.parseFloat(m.group(3)))));
                else
                    throw new Exception("Can not parse vec3 value: " + value);
            } else if (type.equals("vec4")) {
                Matcher m = vec4fP.matcher(value);
                if (m.matches())
                    uniforms.put(name, new UniformVector4(new Vector4(Float.parseFloat(m.group(1)), Float.parseFloat(m.group(2)), Float.parseFloat(m.group(3)), Float.parseFloat(m.group(4)))));
                else
                    throw new Exception("Can not parse vec4 value: " + value);
            }
        }

        for (int i = 0; i != samplersL.getLength(); i++) {
            Node sampler = samplersL.item(i);
            String name = sampler.getAttributes().getNamedItem("name").getNodeValue();
            String image = sampler.getAttributes().getNamedItem("image").getNodeValue();

            samplers.put(name, new Texture2D(new File(base, image)));
        }
    }

}
