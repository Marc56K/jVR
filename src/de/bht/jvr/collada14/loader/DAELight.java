package de.bht.jvr.collada14.loader;

import de.bht.jvr.util.Color;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.bht.jvr.collada14.Extra;
import de.bht.jvr.collada14.Light;
import de.bht.jvr.collada14.Light.TechniqueCommon;
import de.bht.jvr.collada14.TargetableFloat3;
import de.bht.jvr.collada14.Technique;
import de.bht.jvr.core.DirectionalLightNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.SpotLightNode;

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

public class DAELight {
    LightNode lightNode = null;

    public DAELight(Light light) {
        String name = "";
        if (light.getName() != null)
            name = light.getName();

        TechniqueCommon tech = light.getTechniqueCommon();

        if (tech != null)
            if (tech.getDirectional() != null) {
                lightNode = new DirectionalLightNode(name);
                setLightColor(tech.getDirectional().getColor());
            } else if (tech.getPoint() != null) {
                PointLightNode pLight = new PointLightNode(name);
                lightNode = pLight;
                setLightColor(tech.getPoint().getColor());

                float[] att = new float[] { 1, 0, 0 };
                if (tech.getPoint().getConstantAttenuation() != null)
                    att[0] = (float) tech.getPoint().getConstantAttenuation().getValue();
                if (tech.getPoint().getLinearAttenuation() != null)
                    att[1] = (float) tech.getPoint().getLinearAttenuation().getValue();
                if (tech.getPoint().getQuadraticAttenuation() != null)
                    att[2] = (float) tech.getPoint().getQuadraticAttenuation().getValue();

                pLight.setAttenuation(att[0], att[1], att[2]);
            } else if (tech.getSpot() != null) {
                SpotLightNode sLight = new SpotLightNode(name);
                lightNode = sLight;
                setLightColor(tech.getSpot().getColor());

                float[] att = new float[] { 1, 0, 0 };
                if (tech.getSpot().getConstantAttenuation() != null)
                    att[0] = (float) tech.getSpot().getConstantAttenuation().getValue();
                if (tech.getSpot().getLinearAttenuation() != null)
                    att[1] = (float) tech.getSpot().getLinearAttenuation().getValue();
                if (tech.getSpot().getQuadraticAttenuation() != null)
                    att[2] = (float) tech.getSpot().getQuadraticAttenuation().getValue();

                sLight.setAttenuation(att[0], att[1], att[2]);

                // extract spot angle
                if (tech.getSpot().getFalloffAngle() != null)
                    sLight.setSpotCutOff((float) tech.getSpot().getFalloffAngle().getValue() / 2);
                else if (light.getExtras() != null)
                    for (Extra extra : light.getExtras())
                        if (extra.getTechniques() != null)
                            for (Technique extraTech : extra.getTechniques())
                                if (extraTech.getAnies() != null)
                                    for (Element ele : extraTech.getAnies()) {
                                        NodeList lightExtras = ele.getChildNodes();
                                        for (int i = 0; i < lightExtras.getLength(); i++) {
                                            Node n = lightExtras.item(i);
                                            if (n.getNodeName().equals("falloff")) {
                                                float angle = new Float(n.getFirstChild().getNodeValue());
                                                sLight.setSpotCutOff(angle / 2);
                                            }
                                        }
                                    }
            }
    }

    public LightNode getJVRLightNode() {
        return lightNode;
    }

    private void setLightColor(TargetableFloat3 color) {
        if (color != null) {
            List<Double> c = color.getValues();
            lightNode.setColor(new Color(c.get(0).floatValue(), c.get(1).floatValue(), c.get(2).floatValue()));
        }
    }
}
