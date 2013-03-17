package de.bht.jvr.modelviewer.view;

import java.awt.Component;
import java.util.List;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.ClipPlaneNode;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.LightNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Traverser;
import de.bht.jvr.modelviewer.model.Scene;
import de.bht.jvr.modelviewer.model.SceneListener;

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

@SuppressWarnings("serial")
public class SceneGraphTree extends JTree implements SceneListener, Traverser {
    public class CellRenderer extends DefaultTreeCellRenderer {
        private ImageIcon groupNodeIcon = createImageIcon("images/group.gif");
        private ImageIcon shapeNodeIcon = createImageIcon("images/shape.gif");
        private ImageIcon lightNodeIcon = createImageIcon("images/light.gif");

        protected ImageIcon createImageIcon(String path) {
            java.net.URL imgURL = CellRenderer.class.getResource(path);
            if (imgURL != null)
                return new ImageIcon(imgURL);
            else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (row == 0)
                setIcon(null);
            else if (value instanceof TreeNode) {
                if (((TreeNode) value).getSceneNode() instanceof LightNode)
                    setIcon(lightNodeIcon);
                if (((TreeNode) value).getSceneNode() instanceof ShapeNode)
                    setIcon(shapeNodeIcon);
                if (((TreeNode) value).getSceneNode() instanceof GroupNode)
                    setIcon(groupNodeIcon);
            }

            return this;
        }
    }

    public class TreeNode extends DefaultMutableTreeNode {
        private SceneNode node;

        public TreeNode(SceneNode n) {
            super(n);
            node = n;
        }

        public SceneNode getSceneNode() {
            return node;
        }
    }

    private Scene scene;
    private DefaultMutableTreeNode treeNodeRoot;

    private DefaultTreeModel treeModel;

    private Stack<DefaultMutableTreeNode> treeNodeStack = new Stack<DefaultMutableTreeNode>();

    private SceneNode lastSceneGraph = null;

    private SceneGraphTree(DefaultMutableTreeNode treeNode) {
        this(new DefaultTreeModel(treeNode));
        treeNodeRoot = treeNode;
    }

    private SceneGraphTree(DefaultTreeModel treeModel) {
        super(treeModel);
        this.treeModel = treeModel;
        setCellRenderer(new CellRenderer());
    }

    public SceneGraphTree(Scene scene) {
        this(new DefaultMutableTreeNode("SceneGraph"));
        this.scene = scene;
    }

    private void clearTree() {
        treeNodeRoot.removeAllChildren();
        treeModel.reload();
    }

    @Override
    public boolean enter(GroupNode node) {
        DefaultMutableTreeNode n = new TreeNode(node);
        treeNodeStack.peek().add(n);
        treeNodeStack.push(n);
        return true;
    }

    private void expandTree() {
        for (int i = 0; i < getRowCount(); i++)
            expandRow(i);
    }

    @Override
    public boolean leave(GroupNode node) {
        treeNodeStack.pop();
        return true;
    }

    @Override
    public void sceneUpdated() {
        GroupNode sceneRoot = (GroupNode) scene.getSceneGraph();
        List<SceneNode> daeRoot = sceneRoot.getChildNodes();
        if (daeRoot.size() > 0) {
            if (daeRoot.get(0) != lastSceneGraph) {
                lastSceneGraph = daeRoot.get(0);

                clearTree();
                treeNodeStack.removeAllElements();
                treeNodeStack.push(treeNodeRoot);
                sceneRoot.accept(this);
                expandTree();
                this.repaint();
            }
        } else {
            clearTree();
            this.repaint();
        }
    }

    @Override
    public boolean visit(CameraNode node) {
        DefaultMutableTreeNode n = new TreeNode(node);
        treeNodeStack.peek().add(n);
        return true;
    }

    @Override
    public boolean visit(ClipPlaneNode node) {
        DefaultMutableTreeNode n = new TreeNode(node);
        treeNodeStack.peek().add(n);
        return true;
    }

    @Override
    public boolean visit(LightNode node) {
        DefaultMutableTreeNode n = new TreeNode(node);
        treeNodeStack.peek().add(n);
        return true;
    }

    @Override
    public boolean visit(ShapeNode node) {
        DefaultMutableTreeNode n = new TreeNode(node);
        treeNodeStack.peek().add(n);
        return true;
    }
}
