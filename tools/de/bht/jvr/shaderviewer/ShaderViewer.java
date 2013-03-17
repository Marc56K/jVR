package de.bht.jvr.shaderviewer;

import java.awt.BorderLayout;
import de.bht.jvr.util.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.media.opengl.GL;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import de.bht.jvr.collada14.loader.ColladaLoader;
import de.bht.jvr.core.BBox;
import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.Context;
import de.bht.jvr.core.Finder;
import de.bht.jvr.core.GroupNode;
import de.bht.jvr.core.SceneNode;
import de.bht.jvr.core.Shader;
import de.bht.jvr.core.ShaderMaterial;
import de.bht.jvr.core.ShaderProgram;
import de.bht.jvr.core.ShapeNode;
import de.bht.jvr.core.Transform;
import de.bht.jvr.core.pipeline.Pipeline;
import de.bht.jvr.logger.Log;
import de.bht.jvr.logger.LogPrinter;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.math.Vector3;

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

public class ShaderViewer {

    private GLCanvas canvas;
    private JTextArea text = new JTextArea();
    private final int statusLength = 10000;
    private JTextField path = new JTextField();
    private JScrollPane scroll = new JScrollPane(text);

    private WatchMan watcher = new WatchMan(new FileChangeController());
    File currentProject = null;

    private Loader loader = new Loader();
    private boolean freshLoad = false;

    public static void main(String[] args) {
        Log.addLogListener(new LogPrinter(-1, 0, 0));
        new ShaderViewer().run(args);
    }

    private ShaderMaterial theMaterial;

    private CameraNode camera;

    private Pipeline pipe;

    private GroupNode root = new GroupNode();
    private ModelNode models = new ModelNode();

    private Transform cameraRotation = new Transform();
    private Transform cameraTranslation = Transform.translate(0, 0, 1.3f);

    public InputStream getResource(String filename) {
        InputStream is = getClass().getResourceAsStream("resource/" + filename);
        if (is == null)
            throw new RuntimeException("Resource not found: " + filename);
        return is;
    }

    public ShaderViewer() {
        watcher.start();

        camera = new CameraNode("Camera", 1.0f, 45.0f);
        camera.setTransform(cameraRotation.mul(cameraTranslation));

        root.addChildNodes(camera);

        pipe = new Pipeline(root);
        pipe.clearBuffers(true, true, new Color(0, 0, 0));
        pipe.switchCamera(camera);
        pipe.drawGeometry("SINGLEPASS", null);
    }

    public void run(String[] args) {
        GLCapabilities caps = new GLCapabilities(GLProfile.getDefault());

        canvas = new GLCanvas(caps);
        CanvasController cc = new CanvasController();
        canvas.addGLEventListener(cc);
        canvas.addMouseMotionListener(cc);
        canvas.addMouseListener(cc);
        canvas.addMouseWheelListener(cc);

        final JFrame frame = new JFrame("CGE Shader Viewer");
        frame.setLayout(new BorderLayout());
        canvas.setSize(512, 512);
        frame.add(canvas, BorderLayout.CENTER);

        JPanel project = new JPanel();
        project.setLayout(new BorderLayout());
        JButton open = new JButton("Open Project ...");
        path.setEditable(false);
        project.add(open, BorderLayout.WEST);
        project.add(path, BorderLayout.CENTER);
        frame.add(project, BorderLayout.NORTH);

        final JPanel modelButtons = new JPanel();
        modelButtons.setLayout(new BoxLayout(modelButtons, BoxLayout.PAGE_AXIS));

        frame.add(modelButtons, BorderLayout.EAST);

        JPanel status = new JPanel();
        status.setLayout(new BorderLayout());

        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setRows(8);
        status.add(scroll, BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
        frame.setLocation(0, 0);

        frame.addWindowListener(new CloseController());

        open.addActionListener(new OpenController(frame));

        setStatus("CGE Shader Viewer started. \n");

        try {
            Shader fragmentS = new Shader(getResource("default.fs"), GL2GL3.GL_FRAGMENT_SHADER);
            Shader vertexS = new Shader(getResource("default.vs"), GL2GL3.GL_VERTEX_SHADER);
            ShaderProgram program = new ShaderProgram(vertexS, fragmentS);
            theMaterial = new ShaderMaterial("SINGLEPASS", program);

            models.load("Cube", "box.dae");
            models.load("Sphere", "sphere.dae");
            models.load("Cylinder", "cylinder.dae");
            models.load("Torus", "torus.dae");
            models.load("Teapot", "teapot.dae");
            models.load("Duck", "duck.dae");

            models.setMaterial(theMaterial);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        root.addChildNode(models);

        SwingUtilities.invokeLater(new ModelLoader(modelButtons, frame));
    }

    public void setStatus(final String s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                text.setText(s);
            }
        });
    }

    public void appendStatus(final String s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                text.append(s);
                Document d = text.getDocument();
                if (d.getLength() > statusLength) {
                    try {
                        d.remove(0, d.getLength() - statusLength);
                    } catch (BadLocationException e) {}
                }
                final JScrollBar sb = scroll.getVerticalScrollBar();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        sb.setValue(sb.getMaximum());
                    }
                });
            }
        });
    }

    private void maybeRecompileShader(Context ctx) {
        if (freshLoad) {
            Log.info(this.getClass(), "Recompiling shader project.");

            try {
                for (String name : loader.shaders.keySet()) {
                    appendStatus("Compiling shader: " + name + "\n");
                    loader.shaders.get(name).compile(ctx);
                }
                ShaderProgram p = new ShaderProgram(loader.shaders.values());
                p.link(ctx);

                ShaderMaterial mat = new ShaderMaterial("SINGLEPASS", p);
                for (String name : loader.uniforms.keySet())
                    mat.setUniform("SINGLEPASS", name, loader.uniforms.get(name));
                for (String name : loader.samplers.keySet())
                    mat.setTexture("SINGLEPASS", name, loader.samplers.get(name));

                models.setMaterial(mat);
            } catch (Exception e) {
                models.setMaterial(theMaterial);
                appendStatus(e.getMessage());
            }
        }
        freshLoad = false;
    }

    private final class ModelLoader implements Runnable {
        private final JPanel modelButtons;
        private final JFrame frame;

        private ModelLoader(JPanel modelButtons, JFrame frame) {
            this.modelButtons = modelButtons;
            this.frame = frame;
        }

        @Override
        public void run() {
            ModelSwitcher ms = new ModelSwitcher();
            ButtonGroup bg = new ButtonGroup();

            boolean first = true;
            for (Entry<String, Model> e : models.all()) {
                JRadioButton b = new JRadioButton(e.getKey(), first);
                bg.add(b);
                b.setActionCommand(e.getKey());
                b.addActionListener(ms);
                modelButtons.add(b);
                if (first) {
                    models.select(e.getKey());
                    first = false;
                }
            }
            frame.pack();
        }
    }

    private final class CloseController extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            watcher.shutdown();
            runExit();
        }
    }

    private final class OpenController implements ActionListener {
        private final JFrame frame;

        private OpenController(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Preferences prefsRoot = Preferences.userRoot();
            Preferences prefs = prefsRoot.node("ShaderViewer");
            String startdir = prefs.get("last.opened.dir", "");

            final JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setMultiSelectionEnabled(false);
            fc.setCurrentDirectory(new File(startdir));

            if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                currentProject = fc.getSelectedFile();
                prefs.put("last.opened.dir", currentProject.getParentFile().getAbsolutePath());
                watcher.watchDir(currentProject);
                path.setText(currentProject.getName());
            }
        }
    }

    public class Model {

        private Collection<ShapeNode> shapes;
        private GroupNode root = new GroupNode();

        public Model(String filename) throws Exception {

            root.addChildNode(ColladaLoader.load(getResource(filename)));

            shapes = Finder.findAll(root, ShapeNode.class, null);

            BBox box = root.getBBox();
            float size = box.getMax().sub(box.getMin()).length();

            root.setTransform(Transform.scale(1.0f / size).mul(Transform.translate(box.getCenter().mul(-1))));
        }

        public SceneNode root() {
            return root;
        }

        public void setMaterial(ShaderMaterial material) {
            for (ShapeNode shape : shapes)
                shape.setMaterial(material);
        }
    }

    class ModelNode extends GroupNode {

        private TreeMap<String, Model> models = new TreeMap<String, Model>();

        public void load(String title, String filename) throws Exception {
            appendStatus("Loading model " + title + " ...");
            models.put(title, new Model(filename));
            appendStatus(" done.\n");
            select(title);
        }

        public void select(String model) {
            Model selected = models.get(model);
            if (selected != null) {
                removeAllChildNodes();
                addChildNode(selected.root());
                scheduleRedisplay();
            }
        }

        public void setMaterial(ShaderMaterial material) {
            for (Model m : models.values())
                m.setMaterial(material);
        }

        public Set<Entry<String, Model>> all() {
            return models.entrySet();
        }
    }

    class ModelSwitcher implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            appendStatus("Switching to " + e.getActionCommand() + "\n");
            models.select(e.getActionCommand());
        }
    }

    private void scheduleRedisplay() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                canvas.display();
            }
        });
    }

    class FileChangeController implements WatchMan.Listener {

        @Override
        public void dirChanged(File dir, File[] files) {
            setStatus("Project: " + dir.getName() + "\n");
            try {
                Log.info(this.getClass(), "Reloading shader project: " + dir.getName());

                loader.load(dir);
                path.setText(loader.projectName);
                freshLoad = true;
                scheduleRedisplay();

                appendStatus(loader.shaders.size() + " shaders loaded.\n");
                appendStatus(loader.uniforms.size() + " uniforms loaded.\n");
                appendStatus(loader.samplers.size() + " textures loaded.\n");

            } catch (Exception ex) {
                appendStatus("\nerror: " + ex.getMessage() + "\n    cause: " + ex.getCause());
            }
        }
    }

    class CanvasController implements GLEventListener, MouseInputListener, MouseWheelListener {
        private Context ctx = null;

        public void init(GLAutoDrawable drawable) {
            try {
                Log.info(this.getClass(), "Initializing OpenGL.");
                GL2GL3 gl = drawable.getGL().getGL2GL3();
                ctx = new Context(gl);

                gl.setSwapInterval(1);
                gl.glEnable(GL.GL_DEPTH_TEST);
                gl.glEnable(GL.GL_BLEND);
                gl.glEnable(GL.GL_CULL_FACE);
                gl.glCullFace(GL.GL_BACK);

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        public void dispose(GLAutoDrawable drawable) {}

        public void display(GLAutoDrawable drawable) {
            try {
                maybeRecompileShader(ctx);

                pipe.update();
                pipe.render(ctx);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        int canvasWidth;
        int canvasHeight;

        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            camera.setAspectRatio((float) width / (float) height);
            canvasWidth = width;
            canvasHeight = height;
        }

        public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}

        private Vector2 pressedRelPos;
        private Vector3 pressedDir = new Vector3(0, 1, 0);
        private Transform offsetRotation = new Transform();
        private Transform offsetTranslation = new Transform();

        @Override
        public void mouseDragged(MouseEvent e) {
            if ((e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) == 0) {
                Vector3 pos = calcDirOnSphere(calcRelMousePos(e.getX(), e.getY()));
                Vector3 axis = pos.cross(pressedDir).normalize();
                float angle = (float) Math.acos(pos.dot(pressedDir));
                offsetRotation = Transform.rotate(axis, angle);
            } else if ((e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) != 0) {
                float d = calcRelMousePos(e.getX(), e.getY()).y() - pressedRelPos.y();
                offsetTranslation = Transform.translate(0, 0, d);
            }

            camera.setTransform(cameraRotation.mul(offsetRotation).mul(cameraTranslation).mul(offsetTranslation));

            scheduleRedisplay();
        }

        @Override
        public void mouseMoved(MouseEvent e) {}

        private Vector2 calcRelMousePos(int x, int y) {
            float scale = Math.min(canvasWidth, canvasHeight) / 2;
            Vector2 pos = new Vector2((x - canvasWidth / 2) / scale, -(y - canvasHeight / 2) / scale);
            return pos;
        }

        private Vector3 calcDirOnSphere(Vector2 pos) {
            float l = pos.length();
            if (l <= 1f) {
                float z = (float) Math.sqrt(1 - l * l);
                return new Vector3(pos.x(), pos.y(), z);
            } else {
                return new Vector3(pos.x(), pos.y(), 0).normalize();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            pressedRelPos = calcRelMousePos(e.getX(), e.getY());
            pressedDir = calcDirOnSphere(pressedRelPos);
            offsetRotation = new Transform();
            offsetTranslation = new Transform();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            cameraRotation = cameraRotation.mul(offsetRotation);
            cameraTranslation = cameraTranslation.mul(offsetTranslation);
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            offsetTranslation = Transform.translate(0, 0, e.getWheelRotation() * -0.01f);
            cameraTranslation = cameraTranslation.mul(offsetTranslation);
            camera.setTransform(cameraRotation.mul(cameraTranslation));

            scheduleRedisplay();
        }
    }

    private void runExit() {
        new Thread(new Runnable() {
            public void run() {
                System.exit(0);
            }
        }).start();
    }
}