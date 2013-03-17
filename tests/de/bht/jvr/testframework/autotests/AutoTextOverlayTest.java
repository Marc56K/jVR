package de.bht.jvr.testframework.autotests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.PointLightNode;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.core.uniforms.UniformInt;
import de.bht.jvr.core.uniforms.UniformVector2;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.testframework.AbstractAutoTest;
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
 */

public class AutoTextOverlayTest extends AbstractAutoTest{

    public AutoTextOverlayTest() {
        super("AutoTextOverlayTest");
    }

    private void setScreenText(ShaderMaterial mat, float posX, float posY, float xSize, float ySize, String text) {
        List<Vector2> letters = new ArrayList<Vector2>();
        List<Vector2> positions = new ArrayList<Vector2>();
        List<Vector2> size = new ArrayList<Vector2>();
        Vector2 gridSize = new Vector2(0.0625f, 0.0625f);

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            Vector2 letterV = null;
            switch (letter) {
            case '0':
                letterV = new Vector2(0, 3);
                break;
            case '1':
                letterV = new Vector2(1, 3);
                break;
            case '2':
                letterV = new Vector2(2, 3);
                break;
            case '3':
                letterV = new Vector2(3, 3);
                break;
            case '4':
                letterV = new Vector2(4, 3);
                break;
            case '5':
                letterV = new Vector2(5, 3);
                break;
            case '6':
                letterV = new Vector2(6, 3);
                break;
            case '7':
                letterV = new Vector2(7, 3);
                break;
            case '8':
                letterV = new Vector2(8, 3);
                break;
            case '9':
                letterV = new Vector2(9, 3);
                break;
            }

            if (letterV != null) {
                letterV = new Vector2(letterV.x() * gridSize.x(), letterV.y() * gridSize.y());
                letters.add(letterV);
                positions.add(new Vector2((letters.size() - 1) * xSize + posX, posY));
                size.add(new Vector2(xSize, ySize));
            }
        }
        mat.setUniform("OVERLAY", "lettersCount", new UniformInt(letters.size()));
        mat.setUniform("OVERLAY", "letters", new UniformVector2(letters));
        mat.setUniform("OVERLAY", "positions", new UniformVector2(positions));
        mat.setUniform("OVERLAY", "size", new UniformVector2(size));
        mat.setUniform("OVERLAY", "gridSize", new UniformVector2(gridSize));
    }

    @Override
    public Pipeline getPipeline() throws Exception {
        GroupNode root = new GroupNode();
        root.addChildNode(ColladaLoader.load(new File("data/meshes/teapot_low.dae")));

        PointLightNode pLight = new PointLightNode();
        pLight.setColor(new Color(1.0f, 1.0f, 1.0f));
        pLight.setTransform(Transform.translate(0, 400, 200));
        root.addChildNode(pLight);

        camera = new CameraNode("cam1", 4f / 3f, 60f);
        camera.setTransform(Transform.translate(0, 0, 10));
        root.addChildNode(camera);

        ShaderProgram prog = new ShaderProgram(new File("data/pipeline_shader/quad.vs"), new File("data/pipeline_shader/text_overlay.fs"));
        ShaderMaterial mat = new ShaderMaterial("OVERLAY", prog);
        mat.setTexture("OVERLAY", "jvr_Texture0", new Texture2D(new File("data/textures/fonts.png")));

        // Pipeline//////////////////////////////////////
        pipeline = new Pipeline(root);
        pipeline.switchCamera(camera);
        pipeline.clearBuffers(true, true, new Color(0, 0, 0));
        pipeline.drawGeometry("AMBIENT", null);
        pipeline.doLightLoop(true, true).drawGeometry("LIGHTING", null);
        pipeline.drawQuad(mat, "OVERLAY");
        
        setScreenText(mat, 0.1f, 0.8f, 0.02f, 0.02f, "0.0");
        return pipeline;
    }
}
