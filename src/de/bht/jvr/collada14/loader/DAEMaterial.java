package de.bht.jvr.collada14.loader;

import de.bht.jvr.collada14.InstanceEffect;
import de.bht.jvr.collada14.Material;

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

public class DAEMaterial {
    private String instanceEffectUrl = null;

    public DAEMaterial(Material material) {
        InstanceEffect instanceEffect = material.getInstanceEffect();
        if (instanceEffect != null) {
            String url = instanceEffect.getUrl();
            if (url != null)
                instanceEffectUrl = url.replace("#", "");
        }
    }

    public String getInstanceEffectUrl() {
        return instanceEffectUrl;
    }
}
