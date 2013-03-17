package de.bht.jvr.testframework;

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
 * Class encapsuling the testdata.
 * 
 * @author Thomas
 *
 */
public class TestData {
    
    /** The path, the pictures shall be safed. */
    public final String path;
    /** The test. */
    public final AbstractAutoTest testdata;
    
    /**
     * Complete constuctor of the TestData class.
     *
     * @param path The path, the pictures shall be safed.
     * @param test An Object for the auto-test.
     * @throws IllegalArgumentException If one of the parameters is null.
     */
    public TestData(final String path, final AbstractAutoTest test){
        if(path == null || test == null){
            throw new IllegalArgumentException("One of the parameters is null.");
        }
        if(path.endsWith("/")){
            this.path   = path;
        }else{
            this.path   = path + "/";
        }
        this.testdata       = test;
    }
    
}
