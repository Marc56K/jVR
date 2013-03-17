package de.bht.jvr.core.texatlas;

import java.awt.Point;
import java.awt.Rectangle;

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

public class AtlasLayout {
    private AtlasLayoutNode root;

    public AtlasLayout() {
        root = new AtlasLayoutNode();
    }

    public int getHeight() {
        return root.getRectangle().height;
    }

    public int getWidth() {
        return root.getRectangle().width;
    }

    public Point insert(int width, int height) {
        AtlasLayoutNode node = root.insert(new Rectangle(width, height));
        if (node != null)
            return node.getOffset();
        else // atlas too small -> increase atlas dimensions
        {
            int newHeight;
            int newWidth;
            Rectangle rec = root.getRectangle();
            if (rec.width > rec.height) {
                newHeight = rec.height + height;
                newWidth = Math.max(rec.width, width);
            } else {
                newHeight = Math.max(rec.height, height);
                newWidth = rec.width + width;
            }

            AtlasLayoutNode newRoot = new AtlasLayoutNode(new Rectangle(newWidth, newHeight));
            newRoot.insert(root);
            root = newRoot;

            return insert(width, height);
        }
    }
}
