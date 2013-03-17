package de.bht.jvr.core.pipeline;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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

public class PrescanInfo {
    private Set<Pattern> sortedMaterialClasses = new HashSet<Pattern>();
    
    public void registerSortedMaterialClass(Pattern matClassPattern) {
        sortedMaterialClasses.add(matClassPattern);
    }
    
    public boolean isSortedMaterialClass(String matClass) {
        for (Pattern pat : sortedMaterialClasses) {
            if (pat.matcher(matClass).matches()) {
                return true;
            }
        }
        return false;
    }
}
