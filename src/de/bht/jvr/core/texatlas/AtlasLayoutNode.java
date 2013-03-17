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

public class AtlasLayoutNode {
    private AtlasLayoutNode child0 = null;
    private AtlasLayoutNode child1 = null;
    private Rectangle rectangle = null;
    private Rectangle image = null;

    public AtlasLayoutNode() {
        rectangle = new Rectangle();
    }

    public AtlasLayoutNode(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Point getOffset() {
        return new Point(rectangle.x, rectangle.y);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public AtlasLayoutNode insert(AtlasLayoutNode node) {
        AtlasLayoutNode n = this.insert(node.getRectangle());
        if (n != null) {
            n.child0 = node.child0;
            n.child1 = node.child1;
            n.image = node.image;
            n.rectangle = node.rectangle;
            return n;
        }

        return null;
    }

    public AtlasLayoutNode insert(Rectangle img) {
        // if we are not a leaf
        if (child0 != null && child1 != null) {
            // try insert into first child
            AtlasLayoutNode newNode = child0.insert(img);
            if (newNode != null)
                return newNode;

            // no room, insert into second
            return child1.insert(img);
        } else {
            // if there's already a texture here, return
            if (image != null)
                return null;

            // if we are too small, return
            if (img.width > rectangle.width || img.height > rectangle.height)
                return null;

            // if we are just right, accept
            if (img.width == rectangle.width && img.height == rectangle.height) {
                image = img;
                return this;
            }

            // otherwise, gotta split this node and create some kids
            // decide which way to split
            int dw = rectangle.width - img.width;
            int dh = rectangle.height - img.height;

            if (dw > dh) {
                child0 = new AtlasLayoutNode(new Rectangle(rectangle.x, rectangle.y, img.width, rectangle.height));

                child1 = new AtlasLayoutNode(new Rectangle(rectangle.x + img.width, rectangle.y, rectangle.width - img.width, rectangle.height));
            } else {
                child0 = new AtlasLayoutNode(new Rectangle(rectangle.x, rectangle.y, rectangle.width, img.height));

                child1 = new AtlasLayoutNode(new Rectangle(rectangle.x, rectangle.y + img.height, rectangle.width, rectangle.height - img.height));
            }

            // insert into first child we created
            return child0.insert(img);
        }
    }

    public void setChild0(AtlasLayoutNode node) {
        child0 = node;
    }

    public void setChild1(AtlasLayoutNode node) {
        child1 = node;
    }
}
