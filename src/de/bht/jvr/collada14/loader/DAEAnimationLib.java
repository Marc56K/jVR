package de.bht.jvr.collada14.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.bht.jvr.collada14.Animation;
import de.bht.jvr.collada14.LibraryAnimations;
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
 */

public class DAEAnimationLib {
    private Map<String, ArrayList<DAEAnimation>> animations = new HashMap<String, ArrayList<DAEAnimation>>();

    public void setAnimationLib(LibraryAnimations lib) throws Exception {
        for (Animation anim : lib.getAnimations()) {
            if (anim.getAnimations().size() > 0) {
                Log.warning(this.getClass(), "Nested animations are not yet supported.");
                continue;
            }
            DAEAnimation animation = new DAEAnimation(anim);
            for (String key : animation.getJVRAnimations().keySet()) {                
                ArrayList<DAEAnimation> animList = animations.get(key);
                if (animList == null) {
                    animList = new ArrayList<DAEAnimation>();
                    animations.put(key, animList);
                }
                animList.add(animation);
            }
            
        }
    }
    
    public de.bht.jvr.core.Animation getJVRAnimation(String nodeId) {
        ArrayList<DAEAnimation> animList = animations.get(nodeId);
        if (animList != null)
            return animList.get(0).getJVRAnimation(nodeId);
        else
            return null;
    }
}
