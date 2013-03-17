package de.bht.jvr.modelviewer;

import de.bht.jvr.modelviewer.model.Scene;
import de.bht.jvr.modelviewer.view.MainView;

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
 * This sample demonstrates the use of jVR in a Awt/Swing application.
 * 
 * @author Marc Roßbach
 */

public class Start {
    public static void main(String[] args) {
        Scene s = new Scene();
        new MainView("jVR-ColladaViewer", s);
    }
}
