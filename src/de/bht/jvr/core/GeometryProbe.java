package de.bht.jvr.core;

import de.bht.jvr.math.Vector3;
import de.bht.jvr.tests.Probe;
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

public class GeometryProbe extends Probe implements Geometry {

    static BBox bbox = new BBox(new Vector3(0, 0, 0), new Vector3(1, 1, 1));

    public GeometryProbe(String name) {
        super(name);
    }

    @Override
    public BBox getBBox() {
        return bbox;
    }

    @Override
    public Geometry getRenderClone() {
        cloned();
        return this;
    }

    @Override
    public boolean isBuilt(Context ctx) {
        return true;
    }

    @Override
    public Vector3 pick(PickRay ray) {
        picked();
        return null;
    }

    @Override
    public void render(Context ctx) throws Exception {
        rendered();
    }

}
