package de.pcfreak9000.spaceawaits.util;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.utils.LongArray;

import de.pcfreak9000.spaceawaits.util.TaskScheduler.Task;

public class Pipeline<C> implements Runnable {
    
    private C context;
    private long key;
    
    private ConcurrentLinkedQueue<PE<C>> entries = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Runnable> runOnMainThread = new ConcurrentLinkedQueue<>();
    
    private TaskScheduler scheduler;
    
    private boolean running = false;
    
    private Task currentTask;
    private boolean forceNow;
    
    public Pipeline(C context, long key, TaskScheduler scheduler) {
        this.context = context;
        this.key = key;
        this.scheduler = scheduler;
    }
    
    public C getContext() {
        return context;
    }
    
    public void submit(PipelineEntry<C> pe) {
        entries.add(new PE<>(pe, null));
    }
    
    public void submit(PipelineEntry<C> pe, LongArray await) {
        entries.add(new PE<>(pe, await));
    }
    
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public void run() {
        running = true;
        while (!entries.isEmpty()) {
            flush();
            PE<C> next = entries.poll();
            if (next.await == null || next.await.size == 0) {
                dealWith(next.pe);
            } else {
                dealWith(next.pe, next.await);
            }
        }
        running = false;
    }
    
    private void dealWith(PipelineEntry<C> next) {
        if (forceNow) {
            currentTask = scheduler.submit(key, true, () -> next.run(this, context));
        } else if (next.mainthread()) {
            currentTask = scheduler.submit(key, false, () -> runOnMainThread.add(() -> {
                next.run(this, context);
                scheduler.submit(key, true, this);
            }));
            return;
        } else {
            currentTask = scheduler.submit(key, false, () -> next.run(this, context));
        }
    }
    
    private void dealWith(PipelineEntry<C> next, LongArray await) {
        if (forceNow) {
            currentTask = scheduler.submit(key, true, await, () -> next.run(this, context));
        } else if (next.mainthread()) {
            currentTask = scheduler.submit(key, false, await, () -> runOnMainThread.add(() -> {
                next.run(this, context);
                scheduler.submit(key, true, this);
            }));
            return;
        } else {
            currentTask = scheduler.submit(key, false, await, () -> next.run(this, context));
        }
    }
    
    public boolean isForceNow() {
        return forceNow;
    }
    
    public void forceNow() {
        forceNow = true;
        while (currentTask != null) {
            Task current = currentTask;
            current.awaitFinished();
            flush();
            if (current == currentTask) {
                break;
            }
        }
        currentTask = null;
        forceNow = false;
    }
    
    public void flush() {
        while (!runOnMainThread.isEmpty()) {
            runOnMainThread.poll().run();
        }
    }
    
    private static class PE<C> {
        PipelineEntry<C> pe;
        LongArray await;
        
        private PE(PipelineEntry<C> pe, LongArray await) {
            this.pe = pe;
            this.await = await;
        }
        
    }
    
    public static interface PipelineEntry<C> {
        
        boolean mainthread();
        
        void run(Pipeline<C> pipeline, C context);
        
    }
    
}
