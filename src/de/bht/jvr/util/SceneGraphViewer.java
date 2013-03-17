package de.bht.jvr.util;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
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
 *
 *
 * Class for making the scenegraph visible as a tree in an own window.
 * 
 * @author Thomas Grottker
 */

public class SceneGraphViewer extends JFrame implements MouseListener, TreeSelectionListener {

    /**
     * The tree of the scenegraph.
     */
    private JTree tree;

    /**
     * The root node of the given scenegraph.
     */
    private SceneNode root;

    /**
     * The textarea displaying the infromation of an element.
     */
    private JTextArea info;

    /**
     * Constructor of the class SceneGraphViewer. Creates a new window,
     * displaying the scenegraph in a tree.
     * 
     * @param root
     *            The root node of the given scenegraph.
     * @throws IllegalArgumentException
     *             If the given SceneNode is null.
     */
    public SceneGraphViewer(SceneNode root) throws IllegalArgumentException {
        if (root == null) {
            throw new IllegalArgumentException("Can not display a not existing tree.");
        }
        System.out.println("Creating new SceneGraphViewer");
        this.setLayout(new BorderLayout());
        this.root = root;

        tree = new JTree(getSceneTree(root));
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);

        JButton refresh = new JButton("refresh");
        refresh.setName("refresh");
        refresh.addMouseListener(this);

        this.add(new TreeLabelPanel(), "Center");
        this.add(refresh, "South");

        this.setSize(500, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocation(800, 0);
    }

    /**
     * Creating recursively the scenegraphs tree.
     * 
     * @param sn
     *            The root node of the current itteration.
     * @return The current node wrapped in a DefaultMutableTreeNode.
     */
    private DefaultMutableTreeNode getSceneTree(SceneNode sn) {
        if (sn instanceof GroupNode) {
            GroupNode root = (GroupNode) sn;
            DefaultMutableTreeNode top = new DefaultMutableTreeNode(root);
            for (SceneNode child : root.getChildNodes()) {
                top.add(getSceneTree(child));
            }
            return top;
        } else {
            return new DefaultMutableTreeNode(sn);
        }
    }

    /**
     * In case the model has changed, this methode updates the SceneGraphViewer.
     */
    private void updateTree() {
        DefaultTreeModel treeModel = new DefaultTreeModel(getSceneTree(root));
        tree.setModel(treeModel);
    }

    /**
     * Gives the worldcoordinats of a node in the tree.
     * 
     * @param node
     *            The node to locate.
     * @return The Transform of the node in worldcoordinats.
     */
    private Transform getPosition(DefaultMutableTreeNode node) {
        Transform trans = new Transform();
        SceneNode sceneNode = null;
        do {
            sceneNode = (SceneNode) node.getUserObject();
            trans = sceneNode.getTransform().mul(trans);
            node = (DefaultMutableTreeNode) node.getParent();
        } while (sceneNode != root);
        return trans;
    }

    /**
     * Nested-class containing tree and info.
     * 
     * @author Thomas Grottker
     */
    private class TreeLabelPanel extends JPanel {

        /**
         * The constructor.
         */
        private TreeLabelPanel() {
            this.setLayout(new GridLayout(0, 1));
            info = new JTextArea();
            info.setEditable(false);
            add(new JScrollPane(tree));
            add(new JScrollPane(info));
        }

    }

    /*
     * (non-Javadoc)
     * @see
     * javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event
     * .TreeSelectionEvent)
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        Object nodeInfo = node.getUserObject();
        if (nodeInfo instanceof SceneNode) {
            SceneNode scene = (SceneNode) nodeInfo;
            info.setText("Name:\t" + scene.getName() + "\n\nTransformation:\n" + scene.getTransform() + "\nPosition:\n" + getPosition(node));
        }
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 1) {
            if (e.getComponent().getName().equals("refresh")) {
                updateTree();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}