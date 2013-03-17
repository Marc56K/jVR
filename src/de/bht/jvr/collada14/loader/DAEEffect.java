package de.bht.jvr.collada14.loader;

import java.util.List;

import javax.xml.bind.JAXBElement;

import de.bht.jvr.collada14.CommonColorOrTextureType.Color;
import de.bht.jvr.collada14.CommonNewparamType;
import de.bht.jvr.collada14.Effect;
import de.bht.jvr.collada14.FxOpaqueEnum;
import de.bht.jvr.collada14.FxSurfaceCommon;
import de.bht.jvr.collada14.FxSurfaceInitFromCommon;
import de.bht.jvr.collada14.Image;
import de.bht.jvr.collada14.ProfileCOMMON;
import de.bht.jvr.collada14.ProfileCOMMON.Technique;
import de.bht.jvr.collada14.ProfileCOMMON.Technique.Blinn;
import de.bht.jvr.collada14.ProfileCOMMON.Technique.Lambert;
import de.bht.jvr.collada14.ProfileCOMMON.Technique.Phong;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.uniforms.UniformBool;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformFloat;

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

public class DAEEffect {
    private String imageId = null;
    private static DAEPhongShaderProgram phongShader = null;
    private ShaderMaterial shaderMaterial = null;
    private de.bht.jvr.util.Color ambient = new de.bht.jvr.util.Color(1.0f, 1.0f, 1.0f, 1.0f);
    private de.bht.jvr.util.Color diffuse = new de.bht.jvr.util.Color(1.0f, 1.0f, 1.0f, 1.0f);
    private de.bht.jvr.util.Color specular = new de.bht.jvr.util.Color(1.0f, 1.0f, 1.0f, 1.0f);
    private float shininess = 5.0f;
    private boolean isTransparent = false;

    public DAEEffect(Effect effect, boolean invertAlpha) {
        List<JAXBElement<?>> profiles = effect.getFxProfileAbstracts();
        for (JAXBElement<?> profile : profiles)
            if (profile.getValue() instanceof ProfileCOMMON) {
                ProfileCOMMON profileCommon = (ProfileCOMMON) profile.getValue();
                List<Object> imgsNewParams = profileCommon.getImagesAndNewparams();
                for (Object param : imgsNewParams)
                    if (param instanceof CommonNewparamType) {
                        FxSurfaceCommon surface = ((CommonNewparamType) param).getSurface();
                        if (surface != null) {
                            List<FxSurfaceInitFromCommon> initFroms = surface.getInitFroms();
                            for (FxSurfaceInitFromCommon initFrom : initFroms)
                                if (initFrom.getValue() instanceof Image)
                                    imageId = ((Image) initFrom.getValue()).getId();
                        }
                    }

                Technique technique = profileCommon.getTechnique();

                // phong
                if (technique.getPhong() != null) {
                    Phong p = technique.getPhong();

                    float opacity = 1;
                    if (p.getTransparent() != null) {
                        if (p.getTransparency() != null)
                            if (p.getTransparent().getOpaque() == FxOpaqueEnum.RGB_ZERO)
                                opacity = 1.0f - (float) p.getTransparency().getFloat().getValue();
                            else
                                opacity = (float) p.getTransparency().getFloat().getValue();

                        // OpenCollada bug workaround
                        if (invertAlpha)
                            opacity = 1.0f - opacity;

                        if (opacity < 1)
                            isTransparent = true;
                    }

                    if (p.getAmbient() != null) {
                        Color c = p.getAmbient().getColor();
                        if (c != null && c.getValues().size() > 3)
                            ambient = new de.bht.jvr.util.Color(c.getValues().get(0).floatValue(), c.getValues().get(1).floatValue(), c.getValues().get(2).floatValue(), opacity * c.getValues().get(3).floatValue());
                    }

                    if (p.getDiffuse() != null) {
                        Color c = p.getDiffuse().getColor();
                        if (c != null && c.getValues().size() > 3)
                            diffuse = new de.bht.jvr.util.Color(c.getValues().get(0).floatValue(), c.getValues().get(1).floatValue(), c.getValues().get(2).floatValue(), opacity * c.getValues().get(3).floatValue());
                    }

                    if (p.getSpecular() != null) {
                        Color c = p.getSpecular().getColor();
                        if (c != null && c.getValues().size() > 3)
                            specular = new de.bht.jvr.util.Color(c.getValues().get(0).floatValue(), c.getValues().get(1).floatValue(), c.getValues().get(2).floatValue(), opacity * c.getValues().get(3).floatValue());
                    }

                    if (p.getShininess() != null && p.getShininess().getFloat() != null)
                        shininess = (float) p.getShininess().getFloat().getValue();
                } else if (technique.getBlinn() != null) // blinn
                {
                    Blinn p = technique.getBlinn();

                    float opacity = 1;
                    if (p.getTransparent() != null) {
                        if (p.getTransparency() != null)
                            if (p.getTransparent().getOpaque() == FxOpaqueEnum.RGB_ZERO)
                                opacity = 1.0f - (float) p.getTransparency().getFloat().getValue();
                            else
                                opacity = (float) p.getTransparency().getFloat().getValue();

                        // OpenCollada bug workaround
                        if (invertAlpha)
                            opacity = 1.0f - opacity;

                        if (opacity < 1)
                            isTransparent = true;
                    }

                    if (p.getAmbient() != null) {
                        Color c = p.getAmbient().getColor();
                        if (c != null && c.getValues().size() > 3)
                            ambient = new de.bht.jvr.util.Color(c.getValues().get(0).floatValue(), c.getValues().get(1).floatValue(), c.getValues().get(2).floatValue(), opacity * c.getValues().get(3).floatValue());
                    }

                    if (p.getDiffuse() != null) {
                        Color c = p.getDiffuse().getColor();
                        if (c != null && c.getValues().size() > 3)
                            diffuse = new de.bht.jvr.util.Color(c.getValues().get(0).floatValue(), c.getValues().get(1).floatValue(), c.getValues().get(2).floatValue(), opacity * c.getValues().get(3).floatValue());
                    }

                    if (p.getSpecular() != null) {
                        Color c = p.getSpecular().getColor();
                        if (c != null && c.getValues().size() > 3) {
                            // normalize rgb-values
                            float r = c.getValues().get(0).floatValue();
                            float g = c.getValues().get(0).floatValue();
                            float b = c.getValues().get(0).floatValue();

                            float div = Math.max(r, Math.max(g, b));
                            r /= div;
                            g /= div;
                            b /= div;

                            specular = new de.bht.jvr.util.Color(r, g, b, opacity * c.getValues().get(3).floatValue());
                        }
                    }

                    if (p.getShininess() != null && p.getShininess().getFloat() != null)
                        shininess = (float) p.getShininess().getFloat().getValue();
                } else if (technique.getLambert() != null) // lambert
                {
                    Lambert p = technique.getLambert();

                    float opacity = 1;
                    if (p.getTransparent() != null) {
                        if (p.getTransparency() != null)
                            if (p.getTransparent().getOpaque() == FxOpaqueEnum.RGB_ZERO)
                                opacity = 1.0f - (float) p.getTransparency().getFloat().getValue();
                            else
                                opacity = (float) p.getTransparency().getFloat().getValue();

                        // OpenCollada bug workaround
                        if (invertAlpha)
                            opacity = 1.0f - opacity;

                        if (opacity < 1)
                            isTransparent = true;
                    }

                    if (p.getAmbient() != null) {
                        Color c = p.getAmbient().getColor();
                        if (c != null && c.getValues().size() > 3)
                            ambient = new de.bht.jvr.util.Color(c.getValues().get(0).floatValue(), c.getValues().get(1).floatValue(), c.getValues().get(2).floatValue(), opacity * c.getValues().get(3).floatValue());
                    }

                    if (p.getDiffuse() != null) {
                        Color c = p.getDiffuse().getColor();
                        if (c != null && c.getValues().size() > 3) {
                            diffuse = new de.bht.jvr.util.Color(c.getValues().get(0).floatValue(), c.getValues().get(1).floatValue(), c.getValues().get(2).floatValue(), opacity * c.getValues().get(3).floatValue());

                            specular = new de.bht.jvr.util.Color(0, 0, 0, 0);
                        }
                    }
                }
            }
    }

    public ShaderMaterial getJVRShaderMaterial(DAEImageLib imageLib) throws Exception {
        if (shaderMaterial == null) {
            if (phongShader == null)
                phongShader = new DAEPhongShaderProgram();

            shaderMaterial = new ShaderMaterial();

            if (isTransparent)
                shaderMaterial.setMaterialClass("Translucent");

            shaderMaterial.setShaderProgram("AMBIENT", phongShader.getShaderProgramAmbient());
            shaderMaterial.setShaderProgram("LIGHTING", phongShader.getShaderProgramLighting());
            shaderMaterial.setShaderProgram("ATTRIBPASS", phongShader.getShaderProgramAttribPass());

            Texture2D texture = null;
            if (imageId != null && imageId != "") {
                DAEImage img = imageLib.getDAEImage(imageId);
                if (img != null)
                    texture = img.getJVRTexture2D();
            }

            float ambientLight = 0.2f;
            ambient = ambient.modulateRGB(ambientLight);
            shaderMaterial.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(ambient));
            shaderMaterial.setUniform("AMBIENT", "jvr_UseTexture0", new UniformBool(false));

            shaderMaterial.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(diffuse));
            shaderMaterial.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(specular));
            shaderMaterial.setUniform("LIGHTING", "jvr_Material_Shininess", new UniformFloat(shininess));
            shaderMaterial.setUniform("LIGHTING", "jvr_UseTexture0", new UniformBool(false));

            shaderMaterial.setUniform("ATTRIBPASS", "jvr_Material_Ambient", new UniformColor(ambient));
            shaderMaterial.setUniform("ATTRIBPASS", "jvr_Material_Diffuse", new UniformColor(diffuse));
            shaderMaterial.setUniform("ATTRIBPASS", "jvr_Material_Specular", new UniformColor(specular));
            shaderMaterial.setUniform("ATTRIBPASS", "jvr_Material_Shininess", new UniformFloat(shininess));
            shaderMaterial.setUniform("ATTRIBPASS", "jvr_UseTexture0", new UniformBool(false));

            if (texture != null) {
                if (texture.isSemiTransparent())
                    shaderMaterial.setMaterialClass("Translucent");

                shaderMaterial.setTexture("AMBIENT", "jvr_Texture0", texture);
                shaderMaterial.setUniform("AMBIENT", "jvr_UseTexture0", new UniformBool(true));
                shaderMaterial.setUniform("AMBIENT", "jvr_Material_Ambient", new UniformColor(new de.bht.jvr.util.Color(ambientLight, ambientLight, ambientLight, ambient.a)));

                shaderMaterial.setTexture("LIGHTING", "jvr_Texture0", texture);
                shaderMaterial.setUniform("LIGHTING", "jvr_UseTexture0", new UniformBool(true));
                shaderMaterial.setUniform("LIGHTING", "jvr_Material_Diffuse", new UniformColor(new de.bht.jvr.util.Color(1.0f, 1.0f, 1.0f, 1.0f)));
                shaderMaterial.setUniform("LIGHTING", "jvr_Material_Specular", new UniformColor(new de.bht.jvr.util.Color(1.0f, 1.0f, 1.0f, 1.0f)));

                shaderMaterial.setTexture("ATTRIBPASS", "jvr_Texture0", texture);
                shaderMaterial.setUniform("ATTRIBPASS", "jvr_UseTexture0", new UniformBool(true));
                shaderMaterial.setUniform("ATTRIBPASS", "jvr_Material_Ambient", new UniformColor(new de.bht.jvr.util.Color(ambientLight, ambientLight, ambientLight, ambient.a)));
                shaderMaterial.setUniform("ATTRIBPASS", "jvr_Material_Diffuse", new UniformColor(new de.bht.jvr.util.Color(1.0f, 1.0f, 1.0f, 1.0f)));
                shaderMaterial.setUniform("ATTRIBPASS", "jvr_Material_Specular", new UniformColor(new de.bht.jvr.util.Color(1.0f, 1.0f, 1.0f, 1.0f)));
            }
        }

        return shaderMaterial;
    }
}
