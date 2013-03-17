package de.bht.jvr.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
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
public class TextureToDiskWriter {
    public static void writePNG(Texture2D tex, File file) throws IOException {
        BufferedImage bi = new BufferedImage(tex.getWidth(), tex.getHeight(), BufferedImage.TYPE_INT_ARGB);
        byte[] imageData = tex.getImageData();
        int channels = imageData.length / (tex.getWidth() * tex.getHeight());

        int[] pixel = new int[4];
        pixel[3] = 255;
        int i = 0;
        for (int y = 0; y < tex.getHeight(); y++)
            for (int x = 0; x < tex.getWidth(); x++) {
                for (int k = 0; k < channels; k++)
                    pixel[k] = imageData[i++];

                int col = (pixel[3] << 24) + (pixel[0] << 16) + (pixel[1] << 8) + pixel[2];
                bi.setRGB(x, y, col);
            }

        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("png");
        ImageWriter imageWriter = imageWriters.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(file);
        imageWriter.setOutput(ios);
        imageWriter.write(bi);
    }
}
