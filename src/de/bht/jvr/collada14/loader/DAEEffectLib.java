package de.bht.jvr.collada14.loader;

import java.util.HashMap;
import java.util.List;

import de.bht.jvr.collada14.Effect;
import de.bht.jvr.collada14.LibraryEffects;

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

public class DAEEffectLib {
    private HashMap<String, DAEEffect> effectLib = new HashMap<String, DAEEffect>();

    public DAEEffect getDAEEffect(String id) {
        return effectLib.get(id);
    }

    public void setEffectLib(LibraryEffects lib, boolean invertAlpha) throws Exception {
        List<Effect> effects = lib.getEffects();
        for (Effect effect : effects)
            effectLib.put(effect.getId(), new DAEEffect(effect, invertAlpha));
    }
}
