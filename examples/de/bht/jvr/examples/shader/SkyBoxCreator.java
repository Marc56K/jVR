package de.bht.jvr.examples.shader;

import java.io.File;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.Geometry;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;

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

public class SkyBoxCreator {
    private Texture2D bk, dn, ft, lf, rt, up;
    private Geometry planeGeo;
    private ShaderProgram textureProg;

    public SkyBoxCreator(String path) throws Exception {
        SceneNode plane = ColladaLoader.load(new File("data/meshes/plane.dae"));
        planeGeo = Finder.findGeometry(plane, null);

        bk = new Texture2D(new File(path + "_bk.jpg"));
        dn = new Texture2D(new File(path + "_dn.jpg"));
        ft = new Texture2D(new File(path + "_ft.jpg"));
        lf = new Texture2D(new File(path + "_lf.jpg"));
        rt = new Texture2D(new File(path + "_rt.jpg"));
        up = new Texture2D(new File(path + "_up.jpg"));

        textureProg = new ShaderProgram(new File("data/shader/ambient.vs"), new File("data/shader/sky.fs"));

    }

    public SceneNode getSkyBox(String shaderContext) {
        GroupNode skyBox = new GroupNode("SkyBox");

        // back
        ShaderMaterial backMat = new ShaderMaterial(shaderContext, textureProg);
        backMat.setTexture(shaderContext, "jvr_Texture0", bk);
        ShapeNode backShape = new ShapeNode("", planeGeo, backMat);
        backShape.setTransform(Transform.translate(0, 0, 0.499f).mul(Transform.rotateYDeg(180)));
        skyBox.addChildNodes(backShape);

        // down
        ShaderMaterial downMat = new ShaderMaterial(shaderContext, textureProg);
        downMat.setTexture(shaderContext, "jvr_Texture0", dn);
        ShapeNode downShape = new ShapeNode("", planeGeo, downMat);
        downShape.setTransform(Transform.translate(0, -0.499f, 0).mul(Transform.rotateYDeg(180)).mul(Transform.rotateXDeg(-90)));
        skyBox.addChildNodes(downShape);

        // front
        ShaderMaterial frontMat = new ShaderMaterial(shaderContext, textureProg);
        frontMat.setTexture(shaderContext, "jvr_Texture0", ft);
        ShapeNode frontShape = new ShapeNode("", planeGeo, frontMat);
        frontShape.setTransform(Transform.translate(0, 0, -0.499f));
        skyBox.addChildNodes(frontShape);

        // left
        ShaderMaterial leftMat = new ShaderMaterial(shaderContext, textureProg);
        leftMat.setTexture(shaderContext, "jvr_Texture0", lf);
        ShapeNode leftShape = new ShapeNode("", planeGeo, leftMat);
        leftShape.setTransform(Transform.translate(0.499f, 0, 0).mul(Transform.rotateYDeg(-90)));
        skyBox.addChildNodes(leftShape);

        // right
        ShaderMaterial rightMat = new ShaderMaterial(shaderContext, textureProg);
        rightMat.setTexture(shaderContext, "jvr_Texture0", rt);
        ShapeNode rightShape = new ShapeNode("", planeGeo, rightMat);
        rightShape.setTransform(Transform.translate(-0.499f, 0, 0).mul(Transform.rotateYDeg(90)));
        skyBox.addChildNodes(rightShape);

        // up
        ShaderMaterial upMat = new ShaderMaterial(shaderContext, textureProg);
        upMat.setTexture(shaderContext, "jvr_Texture0", up);
        ShapeNode upShape = new ShapeNode("", planeGeo, upMat);
        upShape.setTransform(Transform.translate(0, 0.499f, 0).mul(Transform.rotateYDeg(180)).mul(Transform.rotateXDeg(90)));
        skyBox.addChildNodes(upShape);

        return skyBox;
    }
}
