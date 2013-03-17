package de.bht.jvr.testframework;

import java.io.File;

import de.bht.jvr.renderer.AwtRenderWindow;
import de.bht.jvr.renderer.RenderWindow;
import de.bht.jvr.renderer.Viewer;

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
 * This class gives methodes for automatically generation of testpictures.
 * 
 * @author Thomas Grottker
 *
 */
public class Tester {
    
    /**
     * Takes a picture for each test date.
     * 
     * @param data The test data.
     */
    public static void test(TestData... data){
        int count = -1;
        for(TestData test: data){
            RenderWindow win;
            try {
                System.out.println("Executing " + test.testdata.name);
                win = new AwtRenderWindow(test.testdata.getPipeline(), 800, 600);
                Viewer viewer = new Viewer(false, win);
                count++;
                String nextPic = generateName(test.path, count);
                win.TakeScreenshotOfNextFrame(new File(nextPic));
                viewer.display();
                viewer.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }
    
    /**
     * Name genarator for pictures.
     * 
     * @param path The path to the directory, the picture shall be safed.
     * @param i The number of the picture.
     * @return
     */
    private static String generateName(String path, int i){
        String name = String.format(path + "picture_%03d" + ".jpg", i);
        File f = new File(name);
        int count = 0;
        while(f.exists()){
            count++;
            name = String.format(path + "picture_%03d_" + count + ".jpg", i);
            f = new File(name);
        }
        return name;
    }
    
}
