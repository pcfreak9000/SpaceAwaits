/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventHandler implements IEventListener {
    
    private final Object handler;
    private final Method m;
    private final boolean receiveConsumed;
    private final int prio;
    
    public EventHandler(final Object handler, final Method m, final boolean receiveConsumed, final int prio) {
        this.handler = handler;
        this.m = m;
        this.receiveConsumed = receiveConsumed;
        this.prio = prio;
    }
    
    public Object getHandler() {
        return this.handler;
    }
    
    public Method getMethod() {
        return this.m;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof EventHandler) {
            final EventHandler sec = (EventHandler) obj;
            if (sec.m.equals(this.m) && sec.handler.equals(this.handler)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void invoke(final Event ev) {
        try {
            //make this faster (ASM/bytecode manipulation)?
            this.m.invoke(this.handler, ev);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.err.println("Exception in handler " + this.handler + ", for the event " + ev + ": ");
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean receiveConsumed() {
        return this.receiveConsumed;
    }
    
    @Override
    public int priority() {
        return this.prio;
    }
    
}
