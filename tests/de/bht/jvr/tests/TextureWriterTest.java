package de.bht.jvr.tests;

import java.io.File;

import de.bht.jvr.core.Texture2D;
import de.bht.jvr.core.TextureToDiskWriter;
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
public class TextureWriterTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Texture2D tex = new Texture2D(new File("data/textures/emily.jpg"));
            TextureToDiskWriter.writePNG(tex, new File("test.png"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
