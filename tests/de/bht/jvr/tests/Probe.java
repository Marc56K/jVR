package de.bht.jvr.tests;

import java.util.HashMap;
import java.util.Map;
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
public class Probe {

    class CallCount {
        private Map<Thread, Integer> counts = new HashMap<Thread, Integer>();

        public synchronized void called() {
            Thread t = Thread.currentThread();
            Integer i = counts.get(t);
            if (i == null)
                counts.put(t, 1);
            else
                counts.put(t, i + 1);
        }

        @Override
        public synchronized String toString() {
            StringBuffer sb = new StringBuffer();
            for (Thread t : counts.keySet()) {
                sb.append(t);
                sb.append("(");
                sb.append(counts.get(t));
                sb.append(")");
            }
            return sb.toString();
        }
    }

    CallCount t = new CallCount();
    CallCount r = new CallCount();
    CallCount c = new CallCount();
    CallCount p = new CallCount();

    String name;

    public Probe(String n) {
        name = n;
    }

    protected void cloned() {
        c.called();
    }

    protected void picked() {
        p.called();
    }

    protected void rendered() {
        r.called();
    }

    @Override
    public String toString() {
        return String.format("Probe: %s: t: %s, r: %s, c: %s, p: %s", name, t, r, c, p);
    }

    protected void traversed() {
        t.called();
    }
}
