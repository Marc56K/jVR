package de.bht.jvr.logger;

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

public class Benchmark {
    public static boolean enabled = true;
    private static BenchStep rootStep = null;

    public static void beginFrame() {
        if (!enabled)
            return;
        rootStep = new BenchStep("Frame", 0);
        rootStep.beginStep("Frame");
    }

    public static void endFrame() {
        if (rootStep == null)
            return;
        rootStep.endStep("Frame");
        System.out.println(rootStep.toString());
        rootStep = null;
    }

    public static void beginStep(String name) {
        if (rootStep == null)
            return;
        rootStep.beginStep(name);
    }

    public static void endStep(String name) {
        if (rootStep == null)
            return;
        rootStep.endStep(name);
    }
}
