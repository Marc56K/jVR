package de.bht.jvr.core.rendering;

import java.util.ArrayList;
import java.util.List;

import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.PrescanInfo;
import de.bht.jvr.util.Color;

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
 * The light list contains all light elements for the next frame
 * 
 * @author Marc Roßbach
 */
public class LightList extends DrawList {
    /** The light list. */
    private List<LightElement> lightList = new ArrayList<LightElement>();

    @Override
    public boolean accept(LightNode node) {
        return true;
    }

    @Override
    public void add(LightNode node, Transform worldTrans, PrescanInfo prescanInfo) {
        addLightNode(node, worldTrans, prescanInfo);
    }

    /**
     * Adds a light node.
     * 
     * @param node
     *            the light node
     * @param transform
     *            the transformation
     */
    public void addLightNode(LightNode node, Transform transform, PrescanInfo prescanInfo) {
        LightElement light = new LightElement(node, transform);
        lightList.add(light);
    }

    /*
     * ------------------------------ traversal methods
     * -----------------------------------
     */

    /**
     * Gets a light element.
     * 
     * @param index
     *            the index
     * @return the light element
     */
    public LightElement getLightElement(int index) {
        return lightList.get(index);
    }

    /**
     * Gets the number of lights.
     * 
     * @return the number of lights
     */
    public int getNumLights() {
        return lightList.size();
    }

    public Color globalAmbient() {
        Color global = new Color(0, 0, 0);
        for (LightElement le : lightList) {
            global = global.addRGB(le.getAmbientColor());
        }
        return global;
    }
}
