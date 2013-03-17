package de.bht.jvr.collada14.loader;

import java.util.HashMap;
import java.util.List;

import de.bht.jvr.collada14.LibraryLights;
import de.bht.jvr.collada14.Light;
import de.bht.jvr.core.LightNode;

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

public class DAELightLib {
    private HashMap<String, DAELight> imageLib = new HashMap<String, DAELight>();

    public LightNode getJVRLightNode(String id) {
        DAELight light = imageLib.get(id);
        if (light != null)
            return light.getJVRLightNode();

        return null;
    }

    public void setLightLib(LibraryLights lib) {
        List<Light> lights = lib.getLights();
        for (Light light : lights)
            imageLib.put(light.getId(), new DAELight(light));
    }
}
