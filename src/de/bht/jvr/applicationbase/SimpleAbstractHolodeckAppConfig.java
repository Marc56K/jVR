package de.bht.jvr.applicationbase;

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
 * Configuration data for the SimpleAbstractHolodeckApp. Contains reasonable
 * conservative default values.
 * 
 * @author henrik
 */
public class SimpleAbstractHolodeckAppConfig {
    /** The QTM-Server (default: localhost). */
    public String trackerHost = "localhost";

    /** The QTM-Id of the rigid body which represents the head (default: 0). */
    public int trackerHeadId = 0;

    /** The QTM-Id of the rigid body which represents the wiimote (default: 1). */
    public int trackerWiimoteId = 1;

    /** Open windows fullscreen (default: true). */
    public boolean fullscreen = true;

    /** Enable multithreaded rendering (default: false). */
    public boolean threading = false;

    /**
     * Enable full-sceen anti-aliasing with given number of samples (default:
     * 0).
     */
    public int fsaa = 0;

    /** Enable vertical synchronisation (default: true). */
    public boolean vsync = true;

    /** Simulate running on the Holodeck (default: true). */
    public boolean holoSim = true;
}
