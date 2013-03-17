package de.bht.jvr.tests;

import java.util.ArrayList;
import java.util.List;

import de.bht.jvr.core.GeometryProbe;
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
public class ProbeCentral extends Thread {

    private List<Probe> probes = new ArrayList<Probe>();
    private long started;

    ProbeCentral() {
        started = System.currentTimeMillis();
    }

    public synchronized GeometryProbe newGeoemtryProbe(String name) {
        GeometryProbe p = new GeometryProbe(name);
        probes.add(p);
        return p;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                Log.info(getClass(), toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public synchronized String toString() {
        StringBuffer sb = new StringBuffer();
        float td = (System.currentTimeMillis() - started) / 1000.0f;
        sb.append(String.format("Probes after %f seconds:\n", td));
        for (Probe p : probes) {
            sb.append("  ");
            sb.append(p);
            sb.append("\n");
        }
        return sb.toString();
    }
}
