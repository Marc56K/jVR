package de.bht.jvr.testframework;

import java.util.ArrayList;

import de.bht.jvr.testframework.autotests.*;

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
 * The AutoTest executes the automated tests from the de.bht.jvr.testframework.autotests package.
 * It might throw an error, if the JVM has less heap.
 * 
 * @author Thomas
 *
 */
public class AutoTest {
    
    /**
     * The main initializes and runs the auto-tests.
     * 
     * @param args
     */
    public static void main(String[] args){
        String path = "C:/Users/Thomas/Holodeck/test/screenshot";
        AbstractAutoTest test;
        TestData td;
        ArrayList<TestData> tdl = new ArrayList<TestData>();
        try {
            System.out.println("Initializing AutoAmbientTest");
            test = new AutoAmbientTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoAwtTest");
            test = new AutoAwtTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoBloomTest");
            test = new AutoBloomTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoBlurPipeline");
            test = new AutoBlurPipeline();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoClippingTest");
            test = new AutoClippingTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoColladaPolyTest");
            test = new AutoColladaPolyTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoColladaTest");
            test = new AutoColladaTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoComplexPipelineTest");
            test = new AutoComplexPipelineTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoDeferredShadingTest");
            test = new AutoDeferredShadingTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoDeferredShadingTest2");
            test = new AutoDeferredShadingTest2();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoFinalizerTest");
            test = new AutoFinalizerTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoFrustumCullingTest");
            test = new AutoFrustumCullingTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoLevelOfDetailTest");
            test = new AutoLevelOfDetailTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoLevelOfDetailTest2");
            test = new AutoLevelOfDetailTest2();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoLightLoaderTest");
            test = new AutoLightLoaderTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoLightTest");
            test = new AutoLightTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoMirrorTest");
            test = new AutoMirrorTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoPipelineTest");
            test = new AutoPipelineTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoShaderMaterialTest");
            test = new AutoShaderMaterialTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoShadowMappingTest");
            test = new AutoShadowMappingTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoShadowMappingTest2");
            test = new AutoShadowMappingTest2();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoShapeNodeMergerTest");
            test = new AutoShapeNodeMergerTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoTangentsGenTest");
            test = new AutoTangentsGenTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoTextOverlayTest");
            test = new AutoTextOverlayTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoTransparencyOrderTest");
            test = new AutoTransparencyOrderTest();
            td = new TestData(path, test);
            tdl.add(td);
            System.out.println("Initializing AutoUsePipelineTest");
            test = new AutoUsePipelineTest();
            td = new TestData(path, test);
            tdl.add(td);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Tester.test(tdl.toArray(new TestData[tdl.size()]));
    }
    
}
