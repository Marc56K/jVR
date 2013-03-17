package de.bht.jvr.collada14.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import de.bht.jvr.collada14.COLLADA;
import de.bht.jvr.collada14.LibraryAnimations;
import de.bht.jvr.collada14.LibraryEffects;
import de.bht.jvr.collada14.LibraryGeometries;
import de.bht.jvr.collada14.LibraryImages;
import de.bht.jvr.collada14.LibraryLights;
import de.bht.jvr.collada14.LibraryMaterials;
import de.bht.jvr.collada14.LibraryNodes;
import de.bht.jvr.collada14.LibraryVisualScenes;
import de.bht.jvr.core.SceneNode;
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
 *
 *
 * The collada loader loads DAE files and creates a jvr scene graph.
 * 
 * @author Marc Roßbach
 */
public class ColladaLoader {

    /**
     * Loads a DAE file with user defined image path.
     * 
     * @param doc
     * @param daeFile
     * @param imagePath
     * @param imagePackage
     * @return The root of the scene graph.
     * @throws Exception
     */
    public static SceneNode load(COLLADA doc, String daeFilePath, String imagePath, String imagePackage) throws Exception {
        Log.info(ColladaLoader.class, "Loading collada document: " + (daeFilePath != null ? daeFilePath : doc.toString()));

        boolean invertAlpha = false;

        // create libs
        DAELightLib lightLib = new DAELightLib();
        DAEEffectLib effectLib = new DAEEffectLib();
        DAEMaterialLib materialLib = new DAEMaterialLib();
        DAEImageLib imageLib = new DAEImageLib();
        DAEGeometryLib geometryLib = new DAEGeometryLib();
        DAENodesLib nodesLib = new DAENodesLib();
        DAEAnimationLib animationLib = new DAEAnimationLib();
        DAEVisualScenesLib visualScenesLib = new DAEVisualScenesLib();

        // load libs
        List<Object> libs = doc.getLibraryAnimationsAndLibraryAnimationClipsAndLibraryCameras();
        for (Object lib : libs) {
            // load lights
            if (lib instanceof LibraryLights)
                lightLib.setLightLib((LibraryLights) lib);

            // load effects
            if (lib instanceof LibraryEffects)
                effectLib.setEffectLib((LibraryEffects) lib, invertAlpha);

            // load materials
            if (lib instanceof LibraryMaterials)
                materialLib.setMaterialLib((LibraryMaterials) lib);

            // load images
            if (lib instanceof LibraryImages)
                imageLib.setImageLib((LibraryImages) lib, daeFilePath, imagePath, imagePackage);

            // load geometries
            if (lib instanceof LibraryGeometries)
                geometryLib.setGeometryLib((LibraryGeometries) lib);

            // load nodes
            if (lib instanceof LibraryNodes)
                nodesLib.setNodesLib((LibraryNodes) lib);
            
            // load animations
            if (lib instanceof LibraryAnimations)
                animationLib.setAnimationLib((LibraryAnimations) lib);

            // load visual scene
            if (lib instanceof LibraryVisualScenes)
                visualScenesLib.setVisualScenesLib((LibraryVisualScenes) lib);
        }

        String sceneName = doc.getScene().getInstanceVisualScene().getUrl().replace("#", "");

        return visualScenesLib.getJVRSceneGraph(sceneName, geometryLib, effectLib, materialLib, imageLib, nodesLib, lightLib, animationLib);
    }

    /**
     * Loads a DAE file.
     * 
     * @param daeFile
     *            the DAE file
     * @return the scene node
     * @throws Exception
     *             the exception
     */
    public static SceneNode load(File daeFile) throws Exception {
        COLLADA doc = loadDoc(new BufferedInputStream(new FileInputStream(daeFile)));
        return load(doc, daeFile.getPath(), null, null);
    }

    public static SceneNode load(InputStream is) throws Exception {
        return load(is, null);
    }

    public static SceneNode load(InputStream is, String imagePackage) throws Exception {
        COLLADA doc = loadDoc(is);
        return load(doc, null, null, imagePackage);
    }

    public static COLLADA loadDoc(InputStream is) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance("de.bht.jvr.collada14");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (COLLADA) unmarshaller.unmarshal(is);
    }
}
