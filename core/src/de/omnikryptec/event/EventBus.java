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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.omnikryptec.util.Logger;

public class EventBus implements IEventListener {
    
    private static final int DEFAULT_EVENTBUS_PRIORITY = Integer.MIN_VALUE + 100;
    private static final boolean DEFAULT_EVENTBUS_CONCURRENT = false;
    
    private static final Logger LOGGER = Logger.getLogger(EventBus.class);
    
    private static final Comparator<IEventListener> LISTENER_COMP = (o1, o2) -> o2.priority() - o1.priority();
    
    private static class ObjMapping {
        private final Class<? extends Event> eventType;
        private final IEventListener handler;
        
        private ObjMapping(final IEventListener handler, final Class<? extends Event> eventType) {
            this.eventType = eventType;
            this.handler = handler;
        }
    }
    
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private final ListMultimap<Class<? extends Event>, IEventListener> listeners;
    private final ListMultimap<Object, ObjMapping> objMappings;
    private final Queue<Event> eventQueue;
    private boolean receiveConsumed = true;
    private boolean acceptEvents = true;
    private boolean verbose = false;
    private final int priority;
    
    /**
     * A read-only version of this {@link EventBus}
     */
    public final ReadableEventBus READ_ONLY = new ReadableEventBus() {
        
        @Override
        public void register(final Object object) {
            EventBus.this.register(object);
        }
        
        @Override
        public void register(final IEventListener listener, final Class<? extends Event> eventtype) {
            EventBus.this.register(listener, eventtype);
        }
        
        @Override
        public void post(final Event event) {
            EventBus.this.post(event);
        }
        
        @Override
        public void enqueue(final Event event) {
            EventBus.this.enqueue(event);
        }
    };
    
    public EventBus() {
        this(DEFAULT_EVENTBUS_CONCURRENT, DEFAULT_EVENTBUS_PRIORITY);
    }
    
    public EventBus(final boolean concurrent) {
        this(concurrent, DEFAULT_EVENTBUS_PRIORITY);
    }
    
    public EventBus(final int prio) {
        this(DEFAULT_EVENTBUS_CONCURRENT, prio);
    }
    
    public EventBus(final boolean concurrent, final int prio) {
        this.eventQueue = concurrent ? new ConcurrentLinkedQueue<>() : new ArrayDeque<>();
        this.listeners = ArrayListMultimap.create();
        this.objMappings = ArrayListMultimap.create();
        this.priority = prio;
    }
    
    public void unregister(final IEventListener listener) {
        unregister(listener, Event.class);
    }
    
    public void unregister(final IEventListener listener, final Class<? extends Event> eventtype) {
        final List<IEventListener> list = this.listeners.get(eventtype);
        list.remove(listener);
    }
    
    public void unregister(final Object object) {
        final List<ObjMapping> list = this.objMappings.removeAll(object);
        for (final ObjMapping ma : list) {
            unregister(ma.handler, ma.eventType);
        }
    }
    
    /**
     * Registers an {@link IEventListener} to ALL events.<br>
     * Useful when chaining together multiple {@link EventBus}.
     *
     * <p>
     * Note: {@link EventSubscription}s are ignored.
     * </p>
     *
     * @param listener the listener
     */
    public void register(final IEventListener listener) {
        register(listener, Event.class);
    }
    
    /**
     * Registers an {@link IEventListener} to a certain type of event.
     * <p>
     * Note: {@link EventSubscription}s are ignored.
     * </p>
     *
     * @param listener  the listener
     * @param eventtype the class of the event type
     */
    public void register(final IEventListener listener, final Class<? extends Event> eventtype) {
        if (listener == this) {
            throw new IllegalArgumentException("Can't listen to myself");
        }
        this.listeners.get(eventtype).add(listener);
    }
    
    /**
     * Registers any {@link EventSubscription}s found in this object:
     * <ul>
     * <li>If the object is a class object, every static method of that class object
     * that is subscribed to an event will be added to this {@link EventBus}. Any
     * non-static method that is subscribed to an event in that class will throw an
     * exception.</li>
     * <li>If the object is an object every method that is subscribed to an event
     * (including static ones) will be added to this {@link EventBus}.</li>
     * </ul>
     * <p>
     * Note: If the object does not contain any subscriptions, an exception is
     * thrown. (No, not anymore)
     * </p>
     *
     * @param object the object to search for subscriptions in
     */
    public void register(Object object) {
        if (this.verbose) {
            LOGGER.debug("Trying to register @EventSubscriptions in " + object);
        }
        if (objMappings.containsKey(object)) {
            throw new IllegalArgumentException("Object is already registered: " + object);
        }
        Method[] methods;
        if (object instanceof Class<?>) {
            methods = ((Class<?>) object).getDeclaredMethods();
            object = null;
        } else {
            //Hmmm
            List<Method> ms = new ArrayList<>();
            Class<?> someclazz = object.getClass();
            do {
                ms.addAll(Arrays.asList(someclazz.getDeclaredMethods()));
                someclazz = someclazz.getSuperclass();
            } while (someclazz != Object.class && someclazz != null);
            methods = ms.toArray(Method[]::new);
        }
        boolean annotationFound = false;
        for (final Method m : methods) {
            if (object == null && !Modifier.isStatic(m.getModifiers())) {
                throw new IllegalArgumentException("Nonstatic event listener registered by class");
            }
            final EventSubscription esub = m.getAnnotation(EventSubscription.class);
            if (esub != null) {
                annotationFound = true;
                m.setAccessible(true);
                final EventHandler handler = new EventHandler(object, m, esub.receiveConsumed(), esub.priority());
                final Class<?>[] cls = m.getParameterTypes();
                if (cls.length != 1) {
                    throw new IllegalArgumentException("Wrong amount of parameter types in event listener: " + object);
                } else {
                    if (Event.class.isAssignableFrom(cls[0])) {
                        if (this.listeners.containsKey(cls[0]) && this.listeners.containsEntry(cls[0], handler)) {
                            continue;
                        }
                        final Class<? extends Event> eventType = (Class<? extends Event>) cls[0];
                        final ObjMapping ma = new ObjMapping(handler, eventType);
                        this.objMappings.put(object, ma);
                        register(handler, eventType);
                        if (this.verbose) {
                            LOGGER.debug("Found and registered @EventSubscription for event " + eventType);
                        }
                    } else {
                        throw new IllegalArgumentException("Wrong type of parameter in event listener: " + object);
                    }
                }
            }
        }
        if (!annotationFound && this.verbose) {
            LOGGER.debug("No @EventSubscriptions were found");
        }
    }
    
    /**
     * Immediately processes the given {@code event}.
     *
     * @param event the event
     */
    public void post(final Event event) {
        if (this.acceptEvents) {
            processEvent(event);
        }
    }
    
    /**
     * Enqueues the given event so it can be processed later via
     * {@link #processQueuedEvents()}.
     *
     * @param event the event
     */
    public void enqueue(final Event event) {
        if (this.acceptEvents) {
            this.eventQueue.add(event);
        }
    }
    
    /**
     * Process events added through {@link #enqueue(Event)}.
     */
    public void processQueuedEvents() {
        if (this.processing.get()) {
            throw new IllegalStateException("Already processing!");
        }
        this.processing.set(true);
        while (!this.eventQueue.isEmpty()) {
            processEvent(this.eventQueue.poll());
        }
        this.processing.set(false);
    }
    
    public boolean isProcessing() {
        return this.processing.get();
    }
    
    private void processEvent(final Event event) {
        if (this.verbose) {
            LOGGER.debugf("Processing event: %s", event.toString());
        }
        Class<?> someclazz = event.getClass();
        //maybe this could be cached?
        List<IEventListener> filteredListeners = getFilteredListeners(someclazz);
        if (this.verbose) {
            LOGGER.debugf("Found %d listeners listening for an event of this type", filteredListeners.size());
        }
        for (final IEventListener l : filteredListeners) {
            if (!event.isConsumeable() || !event.isConsumed() || l.receiveConsumed()) {
                l.invoke(event);
            }
        }
    }
    
    private List<IEventListener> getFilteredListeners(Class<?> someclazz) {
        List<IEventListener> filteredListeners = new ArrayList<>();
        do {
            Class<? extends Event> casted = (Class<? extends Event>) someclazz;
            filteredListeners.addAll(this.listeners.get(casted));
            someclazz = someclazz.getSuperclass();
        } while (someclazz != Object.class && someclazz != null);
        filteredListeners.sort(LISTENER_COMP);
        return filteredListeners;
    }
    
    public void setAcceptEvents(boolean b) {
        this.acceptEvents = b;
    }
    
    /**
     * Internal use so an EventBus can act as an IEventListener for another EventBus
     *
     * @see #post(Event)
     */
    @Override
    public void invoke(final Event ev) {
        post(ev);
    }
    
    @Override
    public int priority() {
        return this.priority;
    }
    
    @Override
    public boolean receiveConsumed() {
        return this.receiveConsumed;
    }
    
    public void setReceiveConsumed(final boolean b) {
        this.receiveConsumed = b;
    }
    
    public void setVerbose(boolean b) {
        this.verbose = b;
    }
}
