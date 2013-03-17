package de.bht.jvr.logger;

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

public class BenchStep {
    private List<BenchStep> subSteps = new ArrayList<BenchStep>();
    private long startTime = 0;
    private long duration = 0;
    private int level = 0;
    private String name;
    private String indentation = "";
    private boolean isOpen = false;
    private int openCount = 0;

    public BenchStep(String name, int level) {
        this.level = level;
        this.name = name;
        for (int i = 0; i < level; i++)
            indentation += "  ";
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean beginStep(String name) {
        if (this.isOpen) {
            for (int i = this.subSteps.size() - 1; i >= 0; i--) {
                if (this.subSteps.get(i).beginStep(name))
                    return true;
            }

            BenchStep step = new BenchStep(name, level + 1);
            this.subSteps.add(step);
            return step.beginStep(name);
        } else if (name == this.name) {
            this.openCount++;
            this.isOpen = true;
            this.startTime = System.nanoTime();
            return true;
        }

        return false;
    }

    public boolean endStep(String name) {
        if (this.isOpen) {
            for (int i = this.subSteps.size() - 1; i >= 0; i--) {
                if (this.subSteps.get(i).endStep(name))
                    return true;
            }

            if (name == this.name) {
                this.isOpen = false;
                this.duration += System.nanoTime() - this.startTime;
                return true;
            }
        }

        return false;
    }

    private String nanoTimeToMs(long ms) {
        return (ms / 1000000) + " ms";
    }

    @Override
    public String toString() {
        String result = indentation + name + (this.openCount > 1 ? ": (" + this.openCount + " calls) " : ": ") + nanoTimeToMs(this.duration);
        if (this.subSteps.size() > 0) {
            result += '\n' + indentation + "{" + '\n';
            long sum = 0;
            for (BenchStep step : this.subSteps) {
                sum += step.getDuration();
                result += step.toString();
            }
            result += indentation + "} Unknown: " + nanoTimeToMs(this.duration - sum);
        }

        result += '\n';

        return result;
    }
}
