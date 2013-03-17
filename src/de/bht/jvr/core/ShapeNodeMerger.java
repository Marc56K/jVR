package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.bht.jvr.core.attributes.AttributeVector4;
import de.bht.jvr.core.texatlas.TextureAtlasGenerator;
import de.bht.jvr.core.uniforms.UniformColor;
import de.bht.jvr.core.uniforms.UniformValue;
import de.bht.jvr.core.uniforms.UniformVector4;
import de.bht.jvr.math.Matrix3;
import de.bht.jvr.math.Matrix4;
import de.bht.jvr.math.Vector2;
import de.bht.jvr.math.Vector3;
import de.bht.jvr.math.Vector4;
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
public class ShapeNodeMerger implements Traverser {
    public static void merge(GroupNode root, String filterMaterialClass, String newMaterialClass, String newShapeNodeName) throws Exception {
        new ShapeNodeMerger().mergeScene(root, filterMaterialClass, newMaterialClass, newShapeNodeName);
    }

    private Pattern filterMaterialClass;
    private TransformStack transformStack = new TransformStack();

    private List<SceneNode> nodes = new ArrayList<SceneNode>();
    private List<Vector4> positions = new ArrayList<Vector4>();
    private List<Vector3> normals = new ArrayList<Vector3>();
    private List<Vector3> tangents = new ArrayList<Vector3>();
    private List<Vector3> binormals = new ArrayList<Vector3>();
    private List<Vector2> texCoords = new ArrayList<Vector2>();

    private List<Vector4> texCoordsTrans = new ArrayList<Vector4>();

    private TextureAtlasGenerator atlas = new TextureAtlasGenerator(16, new Color(0.5f, 0.5f, 0.5f, 1));

    private ShapeNodeMerger() {}

    private Color extractColor(ShaderContext sCtx, String uniformName, Color defaultColor) {
        Color result = defaultColor;
        UniformValue val = sCtx.getUniforms().get(uniformName);
        if (val != null)
            if (val instanceof UniformVector4) {
                Vector4 col = ((UniformVector4) val).getValue(0);
                result = new Color(col.x(), col.y(), col.z(), col.w());
            } else if (val instanceof UniformColor)
                result = ((UniformColor) val).getValue(0);

        return result;
    }

    private void mergeScene(GroupNode root, String filterMaterialClass, String newMaterialClass, String newShapeNodeName) throws Exception {
        if (filterMaterialClass == null)
            filterMaterialClass = ".*";

        this.filterMaterialClass = Pattern.compile(filterMaterialClass);

        root.accept(this);

        root.removeAllChildNodes();
        for (SceneNode node : nodes)
            root.addChildNode(node);

        // create the new ShapeNode
        if (positions.size() > 0) {
            int[] idx = new int[positions.size()];
            float[] pos = new float[3 * positions.size()];
            float[] nrm = new float[3 * positions.size()];
            float[] tan = new float[3 * positions.size()];
            float[] bin = new float[3 * positions.size()];
            float[] tex = new float[2 * positions.size()];
            float[] txt = new float[4 * positions.size()];

            for (int i = 0; i < positions.size(); i++) {
                idx[i] = i;

                pos[3 * i] = positions.get(i).x();
                pos[3 * i + 1] = positions.get(i).y();
                pos[3 * i + 2] = positions.get(i).z();

                nrm[3 * i] = normals.get(i).x();
                nrm[3 * i + 1] = normals.get(i).y();
                nrm[3 * i + 2] = normals.get(i).z();

                tan[3 * i] = tangents.get(i).x();
                tan[3 * i + 1] = tangents.get(i).y();
                tan[3 * i + 2] = tangents.get(i).z();

                bin[3 * i] = binormals.get(i).x();
                bin[3 * i + 1] = binormals.get(i).y();
                bin[3 * i + 2] = binormals.get(i).z();
            }

            for (int i = 0; i < texCoords.size(); i++) {
                tex[2 * i] = texCoords.get(i).x();
                tex[2 * i + 1] = texCoords.get(i).y();
            }

            Texture2D texAtlas = atlas.getTextureAtlas();
            float tex0Width = texAtlas.getWidth();
            float tex0Height = texAtlas.getHeight();
            for (int i = 0; i < texCoordsTrans.size(); i++) {
                txt[4 * i] = texCoordsTrans.get(i).x() / tex0Width;
                txt[4 * i + 1] = texCoordsTrans.get(i).y() / tex0Width;
                txt[4 * i + 2] = texCoordsTrans.get(i).z() / tex0Height;
                txt[4 * i + 3] = texCoordsTrans.get(i).w() / tex0Height;
            }

            TriangleMesh mesh = new TriangleMesh(idx, pos, nrm, tex, tan, bin);
            // append additional vertex attributes to mesh
            mesh.setAttribute("jvr_TexCoordTrans", new AttributeVector4(txt));

            ShapeNode shape = new ShapeNode(newShapeNodeName);
            shape.setGeometry(mesh);

            ShaderMaterial mat = TextureAtlasGenerator.makeAtlasPhongShaderMaterial(texAtlas);
            mat.setMaterialClass(newMaterialClass);
            shape.setMaterial(mat);

            root.addChildNode(shape);
        }
    }

    @Override
    public boolean enter(GroupNode node) {
        transformStack.push();
        transformStack.mul(node.getTransform());

        return true;
    }

    @Override
    public boolean leave(GroupNode node) {
        transformStack.pop();

        return true;
    }

    @Override
    public boolean visit(CameraNode node) {
        visitSceneNode(node);

        return true;
    }

    @Override
    public boolean visit(ClipPlaneNode node) {
        visitSceneNode(node);

        return true;
    }

    @Override
    public boolean visit(LightNode node) {
        visitSceneNode(node);

        return true;
    }

    @Override
    public boolean visit(ShapeNode node) {
        transformStack.push();
        transformStack.mul(node.getTransform());

        Matrix4 m = transformStack.peek().getMatrix();
        Matrix3 nm = m.rotationMatrix().inverse().transpose();

        Geometry geo = node.getGeometry();
        Material mat = node.getMaterial();
        if (geo instanceof TriangleMesh) {
            ShaderMaterial mat0 = (ShaderMaterial) mat;

            if (!filterMaterialClass.matcher(mat0.getMaterialClass()).matches()) {
                node.setTransform(transformStack.peek());
                nodes.add(node);
                transformStack.pop();
                return true;
            }

            Texture2D tex = null;
            Vector4 texCoordTrans = new Vector4();
            boolean replaceTexCoords = false;

            ShaderContext lightingCtx = mat0.getShaderContexts().get("LIGHTING");
            ShaderContext ambientCtx = mat0.getShaderContexts().get("AMBIENT");
            if (lightingCtx != null && ambientCtx != null) {
                tex = (Texture2D) lightingCtx.getTextures().get("jvr_Texture0");

                if (tex == null) // no texture found -> create a new texture
                                 // with the diffuse color
                {
                    Color diffuseColor = extractColor(lightingCtx, "jvr_Material_Diffuse", Color.gray);

                    int size = 4;
                    byte[] texData = new byte[size * size * 4];
                    for (int i = 0; i < size * size; i++) {
                        texData[4 * i] = diffuseColor.redByte();
                        texData[4 * i + 1] = diffuseColor.greenByte();
                        texData[4 * i + 2] = diffuseColor.blueByte();
                        texData[4 * i + 3] = diffuseColor.alphaByte();
                    }
                    tex = new Texture2D(size, size, texData);
                    replaceTexCoords = true;
                }

                // add to texture baker
                Vector2 offset = atlas.addTexture(tex, extractColor(ambientCtx, "jvr_Material_Ambient", Color.black).alphaInt());
                texCoordTrans = new Vector4(offset.x(), offset.x() + tex.getWidth(), offset.y(), offset.y() + tex.getHeight());
            }

            TriangleMesh mesh = (TriangleMesh) geo;

            for (int i = 0; i < mesh.getIndicesCount(); i++) {
                // positions
                Vector4 pos = m.mul(mesh.getVertex(i)).homogenize();
                positions.add(pos);

                // normals
                Vector3 nrm = nm.mul(mesh.getNormal(i)).normalize();
                normals.add(nrm);

                // tangents
                Vector3 tan = nm.mul(mesh.getTangent(i)).normalize();
                tangents.add(tan);

                // binormals
                Vector3 bin = nm.mul(mesh.getBinormal(i)).normalize();
                binormals.add(bin);

                // texCoords
                if (!replaceTexCoords)
                    texCoords.add(mesh.getTexCoord(i));
                else
                    texCoords.add(new Vector2());

                // texCoords offsets
                texCoordsTrans.add(texCoordTrans);
            }
        }

        transformStack.pop();

        return true;
    }

    private void visitSceneNode(SceneNode node) {
        transformStack.push();
        transformStack.mul(node.getTransform());

        node.setTransform(transformStack.peek());
        nodes.add(node);

        transformStack.pop();
    }
}
