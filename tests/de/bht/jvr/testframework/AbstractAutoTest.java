package de.bht.jvr.testframework;

import de.bht.jvr.core.CameraNode;
import de.bht.jvr.core.pipeline.Pipeline;

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
 * Abstract class for tests to use AutoTest.
 * 
 * @author Thomas
 *
 */
public abstract class AbstractAutoTest {
    
    /** The pipeline. */
    protected Pipeline pipeline;
    /** The camera. */
    protected CameraNode camera;
    /** The name of the test. */
    protected final String name;
    
    /**
     * The constructor of an AbstractAutoTest.
     * 
     * @param name The name of the test.
     */
    public AbstractAutoTest(String name){
        this.name = name;
    }
    
    /**
     * Getter.
     * The pipeline should be initialized in this method.
     * 
     * @return The pipeline.
     * @throws Exception 
     */
    public abstract Pipeline getPipeline() throws Exception;
    
    /**
     * Getter.
     * 
     * @return The camera of the scene.
     */
    public CameraNode getCamera(){
        return camera;
    }
    
}
