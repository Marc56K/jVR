package de.bht.jvr.collada14.loader;

import java.util.HashMap;
import java.util.List;

import de.bht.jvr.collada14.LibraryMaterials;
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

public class DAEMaterialLib {
    private HashMap<String, DAEMaterial> materialLib = new HashMap<String, DAEMaterial>();

    public DAEMaterial getDAEMaterial(String id) {
        return materialLib.get(id);
    }

    public void setMaterialLib(LibraryMaterials lib) throws Exception {
        List<Material> materials = lib.getMaterials();
        for (Material material : materials)
            materialLib.put(material.getId(), new DAEMaterial(material));
    }
}
