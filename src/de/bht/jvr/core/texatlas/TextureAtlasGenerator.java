package de.bht.jvr.core.texatlas;

import de.bht.jvr.util.Color;
import java.awt.Point;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.media.opengl.GL2ES2;

import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;
import de.bht.jvr.logger.Log;
import de.bht.jvr.math.Vector2;

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

public class TextureAtlasGenerator {
    private class TextureInfo {
        private int alpha;
        private Vector2 offset;

        public TextureInfo(int alpha, Vector2 offset) {
            this.alpha = alpha;
            this.offset = offset;
        }

        public int getAlpha() {
            return alpha;
        }

        public Vector2 getOffset() {
            return offset;
        }
    }

    public static ShaderMaterial makeAtlasPhongShaderMaterial(Texture2D tex) throws Exception {
        Log.info(TextureAtlasGenerator.class, "Generating atlas shader program.");

        ClassLoader cl = ShaderMaterial.class.getClassLoader();

        InputStream davs = cl.getResourceAsStream("de/bht/jvr/shaders/atlas_ambient.vs");
        InputStream dafs = cl.getResourceAsStream("de/bht/jvr/shaders/atlas_ambient.fs");
        ShaderProgram shaderProgramAmbient = new ShaderProgram(new Shader(davs, GL2ES2.GL_VERTEX_SHADER), new Shader(dafs, GL2ES2.GL_FRAGMENT_SHADER));

        InputStream plvs = cl.getResourceAsStream("de/bht/jvr/shaders/atlas_phong_lighting.vs");
        InputStream plfs = cl.getResourceAsStream("de/bht/jvr/shaders/atlas_phong_lighting.fs");
        ShaderProgram shaderProgramLighting = new ShaderProgram(new Shader(plvs, GL2ES2.GL_VERTEX_SHADER), new Shader(plfs, GL2ES2.GL_FRAGMENT_SHADER));

        ShaderMaterial sm = new ShaderMaterial();
        sm.setShaderProgram("AMBIENT", shaderProgramAmbient);
        sm.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new Color(0.2f, 0.2f, 0.2f, 1.0f)));

        sm.setShaderProgram("LIGHTING", shaderProgramLighting);
        sm.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
        sm.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(new Color(1.0f, 1.0f, 1.0f, 1.0f)));
        sm.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(10));

        sm.setUniform(null, "jvr_UseTexCoordTrans", new UniformBool(true));
        sm.setTexture(null, "jvr_Texture0", tex);
        sm.setUniform(null, "jvr_UseTexture0", new UniformBool(true));

        return sm;
    }

    private Map<Texture2D, TextureInfo> textureOffsets = new HashMap<Texture2D, TextureInfo>();
    private final int borderSize;
    private final Color fillColor;

    private AtlasLayout atlas;

    public TextureAtlasGenerator(int borderSize, Color backgroundColor) {
        this.borderSize = borderSize;
        fillColor = backgroundColor;

        atlas = new AtlasLayout();
    }

    public Vector2 addTexture(Texture2D tex, int alpha) {
        // check if the image is already in the atlas
        Vector2 offset = getTextureOffset(tex);
        if (offset != null)
            return offset;
        else {
            Point o = atlas.insert(tex.getWidth() + 2 * borderSize, tex.getHeight() + 2 * borderSize);
            offset = new Vector2(o.x, o.y).add(new Vector2(borderSize, borderSize));

            TextureInfo texInfo = new TextureInfo(alpha, offset);
            textureOffsets.put(tex, texInfo);

            return offset;
        }
    }

    public Texture2D getTextureAtlas() {
        int width = atlas.getWidth();
        int height = atlas.getHeight();

        if (textureOffsets.size() == 0)
            return null;

        Log.info(this.getClass(), "Generating texture atlas: " + width + " x " + height);

        // allocate memory for image data
        byte[] imageData = new byte[4 * width * height];
        for (int i = 0; i < imageData.length / 4; i++) {
            imageData[4 * i] = fillColor.redByte();// (byte)(Math.random()*255);
            imageData[4 * i + 1] = fillColor.greenByte();// (byte)(Math.random()*255);
            imageData[4 * i + 2] = fillColor.blueByte();// (byte)(Math.random()*255);
            imageData[4 * i + 3] = fillColor.alphaByte();
        }

        for (Entry<Texture2D, TextureInfo> entry : textureOffsets.entrySet()) {
            Texture2D tex = entry.getKey();
            Vector2 offset = entry.getValue().getOffset();

            int alpha = entry.getValue().getAlpha();
            float a = (float) alpha / 255;

            byte[] img = tex.getImageData();
            int channels = img.length / (tex.getHeight() * tex.getWidth());

            // copy the texture
            for (int y = -borderSize; y < tex.getHeight() + borderSize; y++) {
                int yOffset = (int) (offset.y() + y);
                for (int x = -borderSize; x < tex.getWidth() + borderSize; x++) {
                    int xOffset = (int) offset.x() + x;
                    int targetIdx = 4 * (yOffset * width + xOffset);

                    if (y >= 0 && y < tex.getHeight() && x >= 0 && x < tex.getWidth())
                        for (int i = 0; i < 4; i++) {
                            if (i < channels)
                                imageData[targetIdx + i] = img[channels * (y * tex.getWidth() + x) + i];
                            else
                                imageData[targetIdx + i] = (byte) 255;

                            // set alpha
                            if (i == 3 && alpha != 255)
                                imageData[targetIdx + i] = (byte) (a * (imageData[targetIdx + i] & 0xFF));
                        }
                    else // draw the borders and edges
                    {
                        if (y >= 0 && y < tex.getHeight())
                            if (x < 0)
                                for (int i = 0; i < 4; i++) {
                                    if (i < channels)
                                        imageData[targetIdx + i] = img[channels * (y * tex.getWidth() + 0) + i];
                                    else
                                        imageData[targetIdx + i] = (byte) 255;

                                    // set alpha
                                    if (i == 3 && alpha != 255)
                                        imageData[targetIdx + i] = (byte) (a * (imageData[targetIdx + i] & 0xFF));
                                }
                            else if (x >= tex.getWidth())
                                for (int i = 0; i < 4; i++) {
                                    if (i < channels)
                                        imageData[targetIdx + i] = img[channels * (y * tex.getWidth() + tex.getWidth() - 1) + i];
                                    else
                                        imageData[targetIdx + i] = (byte) 255;

                                    // set alpha
                                    if (i == 3 && alpha != 255)
                                        imageData[targetIdx + i] = (byte) (a * (imageData[targetIdx + i] & 0xFF));
                                }

                        if (x >= 0 && x < tex.getWidth())
                            if (y < 0)
                                for (int i = 0; i < 4; i++) {
                                    if (i < channels)
                                        imageData[targetIdx + i] = img[channels * x + i];
                                    else
                                        imageData[targetIdx + i] = (byte) 255;

                                    // set alpha
                                    if (i == 3 && alpha != 255)
                                        imageData[targetIdx + i] = (byte) (a * (imageData[targetIdx + i] & 0xFF));
                                }
                            else if (y >= tex.getHeight())
                                for (int i = 0; i < 4; i++) {
                                    if (i < channels)
                                        imageData[targetIdx + i] = img[channels * ((tex.getHeight() - 1) * tex.getWidth() + x) + i];
                                    else
                                        imageData[targetIdx + i] = (byte) 255;

                                    // set alpha
                                    if (i == 3 && alpha != 255)
                                        imageData[targetIdx + i] = (byte) (a * (imageData[targetIdx + i] & 0xFF));
                                }

                        if (y < 0) {
                            if (x < 0)
                                for (int i = 0; i < 4; i++) {
                                    if (i < channels)
                                        imageData[targetIdx + i] = img[i];
                                    else
                                        imageData[targetIdx + i] = (byte) 255;

                                    // set alpha
                                    if (i == 3 && alpha != 255)
                                        imageData[targetIdx + i] = (byte) (a * (imageData[targetIdx + i] & 0xFF));
                                }

                            if (x >= tex.getWidth())
                                for (int i = 0; i < 4; i++) {
                                    if (i < channels)
                                        imageData[targetIdx + i] = img[channels * (tex.getWidth() - 1) + i];
                                    else
                                        imageData[targetIdx + i] = (byte) 255;

                                    // set alpha
                                    if (i == 3 && alpha != 255)
                                        imageData[targetIdx + i] = (byte) (a * (imageData[targetIdx + i] & 0xFF));
                                }
                        }

                        if (y >= tex.getHeight()) {
                            if (x < 0)
                                for (int i = 0; i < 4; i++) {
                                    if (i < channels)
                                        imageData[targetIdx + i] = img[channels * (tex.getHeight() - 1) * tex.getWidth() + i];
                                    else
                                        imageData[targetIdx + i] = (byte) 255;

                                    // set alpha
                                    if (i == 3 && alpha != 255)
                                        imageData[targetIdx + i] = (byte) (a * (imageData[targetIdx + i] & 0xFF));
                                }

                            if (x >= tex.getWidth())
                                for (int i = 0; i < 4; i++) {
                                    if (i < channels)
                                        imageData[targetIdx + i] = img[channels * ((tex.getHeight() - 1) * tex.getWidth() + tex.getWidth() - 1) + i];
                                    else
                                        imageData[targetIdx + i] = (byte) 255;

                                    // set alpha
                                    if (i == 3 && alpha != 255)
                                        imageData[targetIdx + i] = (byte) (a * (imageData[targetIdx + i] & 0xFF));
                                }
                        }
                    }
                }
            }
        }

        Texture2D result = new Texture2D(width, height, imageData);

        // try
        // {
        // TextureToDiskWriter.writePNG(result, new File("atlas.png"));
        // }
        // catch (IOException e)
        // {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        return result;
    }

    public Vector2 getTextureOffset(Texture2D tex) {
        if (textureOffsets.containsKey(tex))
            return textureOffsets.get(tex).getOffset();

        for (Texture2D tex0 : textureOffsets.keySet()) {
            if (tex0.getWidth() != tex.getWidth())
                continue;
            if (tex0.getHeight() != tex.getHeight())
                continue;
            if (Arrays.equals(tex0.getImageData(), tex.getImageData()))
                return textureOffsets.get(tex0).getOffset();
        }

        return null;
    }
}
