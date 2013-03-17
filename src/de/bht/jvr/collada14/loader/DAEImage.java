package de.bht.jvr.collada14.loader;

import java.io.File;
import java.io.InputStream;

import de.bht.jvr.collada14.Image;
import de.bht.jvr.core.Texture2D;
import de.bht.jvr.logger.Log;

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

public class DAEImage {
    boolean fromInputStream = false;
    String initFrom = null;
    Texture2D texture = null;

    public DAEImage(Image image, String daePath, String imagePath, String imagePackage) {
        if (daePath != null) {
            int len = daePath.lastIndexOf('/');
            if (len < 0)
                len = daePath.lastIndexOf('\\');
            daePath = daePath.substring(0, len).replace('\\', '/');
        }

        initFrom = image.getInitFrom();
        if (initFrom != null && initFrom != "")
            if (imagePath == null && imagePackage == null) // image path from
                                                           // collada file
            {
                if (initFrom.startsWith("file:"))
                    initFrom = initFrom.replaceFirst("file:///", "");
                else
                    initFrom = daePath + '/' + initFrom.replaceFirst("file:///", "");
            } else if (imagePath != null) // user defined path
            {
                String[] pathSplit = initFrom.split("/");
                if (pathSplit.length > 0)
                    initFrom = imagePath + "/" + pathSplit[pathSplit.length - 1];
                else
                    initFrom = imagePath + "/" + initFrom;
            } else if (imagePackage != null) {
                fromInputStream = true;
                String[] pathSplit = initFrom.split("/");
                initFrom = imagePackage + "/" + pathSplit[pathSplit.length - 1];
            }
    }

    public Texture2D getJVRTexture2D() throws Exception {
        if (texture == null && initFrom != null && initFrom != "") {
            Log.info(this.getClass(), "Creating DAEImage: " + initFrom);
            if (!fromInputStream)
                texture = new Texture2D(new File(initFrom));
            else {
                InputStream is = this.getClass().getResourceAsStream(initFrom);
                if (is != null)
                    texture = new Texture2D(is);
                else
                    throw new Exception("Can't load image: " + initFrom);
            }
        }

        return texture;
    }
}
