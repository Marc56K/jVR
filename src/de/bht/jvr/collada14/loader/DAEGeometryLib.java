package de.bht.jvr.collada14.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bht.jvr.collada14.Geometry;
import de.bht.jvr.collada14.LibraryGeometries;
import de.bht.jvr.core.ShapeNode;

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

public class DAEGeometryLib {
    private HashMap<String, DAEGeometry> geometryLib = new HashMap<String, DAEGeometry>();

    public DAEGeometry getDAEGeometry(String id) {
        return geometryLib.get(id);
    }

    public List<ShapeNode> getJVRShapeNodes(String geometryId, Map<String, String> instanceMaterialMap, DAEEffectLib effectLib, DAEMaterialLib materialLib, DAEImageLib imageLib) throws Exception {
        DAEGeometry geometry = geometryLib.get(geometryId);
        if (geometry != null)
            return geometry.getJVRShapeNodes(instanceMaterialMap, effectLib, materialLib, imageLib);

        return new ArrayList<ShapeNode>();
    }

    public void setGeometryLib(LibraryGeometries lib) throws Exception {
        List<Geometry> geos = lib.getGeometries();
        for (Geometry geo : geos)
            geometryLib.put(geo.getId(), new DAEGeometry(geo));
    }
}
