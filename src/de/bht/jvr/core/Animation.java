package de.bht.jvr.core;

import java.util.ArrayList;
import java.util.List;

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

public class Animation {
    private class KeyFrame<T> {
        private float time = 0;
        private T value;
        public KeyFrame(float time, T value) {
            this.time = time;
            this.value = value;
        }
        
        float getTime() {
            return time;
        }
        
        T getValue() {
            return value;
        }
    }
    
    private float maxTime = 0;
    private float minTime = 0;
    private List<KeyFrame<Transform>> transformTimeline = new ArrayList<KeyFrame<Transform>>();
    
    public void setKeyFrame(float time, Transform trans) {
        maxTime = Math.max(maxTime, time);
        minTime = Math.min(minTime, time);
        
        int idx = 0;
        boolean replace = false;
        for (KeyFrame<Transform> keyFrame : transformTimeline) {
            if (keyFrame.getTime() < time)
                idx++;
            else {
                replace = keyFrame.getTime() == time;
                break;
            } 
        }
        if (replace)
            transformTimeline.set(idx, new KeyFrame<Transform>(time, trans));
        else
            transformTimeline.add(idx, new KeyFrame<Transform>(time, trans));
    }
    
    public void clearKeyFrames() {
        maxTime = 0;
        minTime = 0;
        transformTimeline.clear();
    }
    
    public float getMaxTime() {
        return maxTime;
    }
    
    public float getMinTime() {
        return minTime;
    }
    
    public void execute(float time, SceneNode node) {
        // TODO interpolate between transformations
        // TODO use a binary tree instead of a list for faster execution
        
        KeyFrame<Transform> bestKeyFrame = null;
        float bestDist = Float.MAX_VALUE;
        for (KeyFrame<Transform> keyFrame : transformTimeline) {
            float dist = Math.abs(time - keyFrame.getTime());
            if (dist < bestDist) {
                bestDist = dist;
                bestKeyFrame = keyFrame;
            } else if (dist > bestDist)
                break;
        }
        
        if (bestKeyFrame != null) {
            node.setTransform(bestKeyFrame.getValue());
        }
    }
}
