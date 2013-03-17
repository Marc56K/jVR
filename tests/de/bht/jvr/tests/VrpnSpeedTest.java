package de.bht.jvr.tests;

import vrpn.TrackerRemote;
import vrpn.TrackerRemote.PositionChangeListener;
import vrpn.TrackerRemote.TrackerUpdate;
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
public class VrpnSpeedTest implements PositionChangeListener {

    /**
     * @param args
     */
    public static void main(String[] args) {
        new VrpnSpeedTest();
    }

    private long lastTick = 0;

    public VrpnSpeedTest() {
        TrackerRemote remote;
        try {
            remote = new TrackerRemote("KinectTracker@localhost", null, null, null, null);
            remote.addPositionChangeListener(this);
            remote.setTimerPeriod(1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void trackerPositionUpdate(TrackerUpdate arg0, TrackerRemote arg1) {
        if (arg0.sensor != 0)
            return;

        long now = System.currentTimeMillis();
        System.out.println(now - lastTick);
        lastTick = now;
    }
}
