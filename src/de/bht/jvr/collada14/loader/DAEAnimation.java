package de.bht.jvr.collada14.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bht.jvr.collada14.Animation;
import de.bht.jvr.collada14.Channel;
import de.bht.jvr.collada14.FloatArray;
import de.bht.jvr.collada14.InputLocal;
import de.bht.jvr.collada14.Sampler;
import de.bht.jvr.collada14.Source;
import de.bht.jvr.core.Transform;
import de.bht.jvr.logger.Log;
import de.bht.jvr.math.Matrix4;

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

public class DAEAnimation {
    private Map<String, de.bht.jvr.core.Animation> animations = new HashMap<String, de.bht.jvr.core.Animation>();
    
    public DAEAnimation(Animation anim) {
        // sources
        Map<String, Source> sources = new HashMap<String, Source>();
        for (Source src : anim.getSources()) {
            sources.put("#" + src.getId(), src);
        }        
        
        // sampler
        Map<String, Sampler> sampler = new HashMap<String, Sampler>();
        for (Sampler s : anim.getSamplers()) {
            sampler.put("#" + s.getId(), s);
        }
        
        // channels
        for (Channel c : anim.getChannels()) {
            String samplerName = c.getSource();
            String[] target = c.getTarget().split("/");
            if (target.length == 2 && target[1].equals("matrix")) {
                String nodeId = target[0];
                Log.info(this.getClass(), "Loading animation channel [" + c.getTarget() + "].");
                
                List<Double> frameTimes = null;
                List<Matrix4> matrices = null;
                Sampler s = sampler.get(samplerName);
                
                if (s != null) {
                    for (InputLocal input : s.getInputs()) {
                        if (input.getSemantic().equals("INPUT")) {
                            Source src = sources.get(input.getSource());
                            FloatArray arr = src.getFloatArray();
                            if (arr != null)
                                frameTimes = arr.getValues();
                        }
                        else if (input.getSemantic().equals("OUTPUT")) {
                            Source src = sources.get(input.getSource());
                            FloatArray arr = src.getFloatArray();
                            if (arr != null) {
                                List<Double> values = arr.getValues();
                                if (values.size() % 16 == 0) {
                                    matrices = new ArrayList<Matrix4>();
                                    for (int i=0; i<values.size(); i+=16) {
                                        Matrix4 m = new Matrix4(values.get(i).floatValue(), values.get(i + 1).floatValue(), values.get(i + 2).floatValue(), values.get(i + 3).floatValue(), 
                                                                values.get(i + 4).floatValue(), values.get(i + 5).floatValue(), values.get(i + 6).floatValue(), values.get(i + 7).floatValue(),
                                                                values.get(i + 8).floatValue(), values.get(i + 9).floatValue(), values.get(i + 10).floatValue(), values.get(i + 11).floatValue(),
                                                                values.get(i + 12).floatValue(), values.get(i + 13).floatValue(), values.get(i + 14).floatValue(), values.get(i + 15).floatValue());
                                        matrices.add(m);
                                    }
                                }
                            }                                
                        }
                    }
                    if (frameTimes != null && matrices != null) {
                        de.bht.jvr.core.Animation jvrAnim = null;
                        for (int i=0; i<Math.min(frameTimes.size(), matrices.size()); i++) {
                            if (jvrAnim == null)
                                jvrAnim = new de.bht.jvr.core.Animation();
                            
                            float t = frameTimes.get(i).floatValue();
                            Matrix4 m = matrices.get(i);                            
                            jvrAnim.setKeyFrame(t, new Transform(m));
                        }
                        if (jvrAnim != null)
                            animations.put(nodeId, jvrAnim);
                    }
                }
                
            } else {
                Log.warning(this.getClass(), "Animation type not supported yet.");
            }
        }
    }

    public de.bht.jvr.core.Animation getJVRAnimation(String nodeId) {
        return animations.get(nodeId);
    }
    
    public Map<String, de.bht.jvr.core.Animation> getJVRAnimations() {
        return animations;
    }
}
