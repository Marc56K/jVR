package de.bht.jvr.util.awt;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bht.jvr.util.FrameListener;

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
 * Input event handling for mouse buttons and keyboard keys. Jawa AWT pressed
 * and released events are collected into a consistent input state. The state
 * can be queried in various ways.
 */

public class InputState implements KeyListener {

    Set<Integer> down = new HashSet<Integer>();
    Set<Integer> toggled = new HashSet<Integer>();
    Set<Integer> triggered = new HashSet<Integer>();
    Map<Integer, Runnable> keyListeners = new HashMap<Integer, Runnable>();
    Set<FrameListener> frameListeners = new HashSet<FrameListener>();

    /**
     * Return true if the provided key is currently pressed.
     */
    public boolean isDown(int key) {
        return down.contains(key);
    }

    /**
     * Return true if one of the provided keys is currently pressed.
     */
    public boolean isOneDown(int... keys) {
        for (int k : keys)
            if (down.contains(k))
                return true;
        return false;
    }

    /**
     * Return true if the provided key is currently toggled.
     */
    public boolean isToggled(int key) {
        return toggled.contains(key);
    }

    /**
     * Return true if one of the provided keys is currently toggled.
     */
    public boolean isOneToggled(int... keys) {
        for (int k : keys)
            if (toggled.contains(k))
                return true;
        return false;
    }

    /**
     * Return true if the provided key has been pressed in the past and has been
     * queried since.
     */
    public boolean isTriggered(int key) {
        boolean result = triggered.contains(key);
        triggered.remove(key);
        return result;
    }

    /**
     * Register a listener for the specified key. The action is called for each
     * keypress once.
     */
    public void addKeyListener(int key, Runnable action) {
        keyListeners.put(key, action);
    }

    /**
     * Register a listener for the frame completion event.
     */
    public void addFrameListener(FrameListener action) {
        frameListeners.add(action);
    }

    /**
     * This needs to called from the main simulation function to trigger
     * notification to frame listeners.
     */
    public void frame(float elapsed) {
        for (FrameListener action : frameListeners)
            action.run(elapsed);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
        down.add(key);
        triggered.add(key);
        if (toggled.contains(key))
            toggled.remove(key);
        else
            toggled.add(key);
        if (keyListeners.containsKey(key))
            keyListeners.get(key).run();
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent ke) {
        down.remove(ke.getKeyCode());
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent ke) {}
}
