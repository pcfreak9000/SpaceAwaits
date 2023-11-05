package de.pcfreak9000.spaceawaits.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.OrderedSet;

public class TaskScheduler {
    
    private ExecutorService executorService;
    private LongMap<Task> taskMap = new LongMap<>();
    
    public TaskScheduler(ExecutorService service) {
        this.executorService = service;
    }
    
    public boolean isBusy(long id) {
        synchronized (taskMap) {
            return taskMap.containsKey(id);
        }
    }
    
    public Task submit(long id, boolean blocking, Runnable runnable) {
        Task task = blocking ? new Task() : new AsyncTask();
        Task previous = null;
        synchronized (taskMap) {
            previous = taskMap.put(id, task);
        }
        task.submit(id, runnable, previous, null);
        return task;
    }
    
    public Task submit(long id, boolean blocking, LongArray await, Runnable runnable) {
        Task task = blocking ? new Task() : new AsyncTask();
        OrderedSet<Task> previous = new OrderedSet<>(await == null ? 1 : await.size + 1);
        synchronized (taskMap) {
            Task prevTask = taskMap.put(id, task);
            if (prevTask != null) {
                previous.add(prevTask);
            }
            for (int i = 0; i < await.size; i++) {
                long l = await.get(i);
                Task prevTaskI = taskMap.put(l, task);
                if (prevTaskI != null) {
                    previous.add(prevTaskI);
                }
            }
        }
        task.submit(id, runnable, new Finishables(previous), await);
        return task;
    }
    
    private interface Finishable {
        void awaitFinished();
    }
    
    private class Finishables implements Finishable {
        private OrderedSet<? extends Finishable> previous;
        
        private Finishables(OrderedSet<? extends Finishable> previous) {
            super();
            this.previous = previous;
        }
        
        @Override
        public void awaitFinished() {
            if (previous != null) {
                for (Finishable t : previous) {
                    t.awaitFinished();
                }
            }
        }
        
    }
    
    public class AsyncTask extends Task {
        
        private final CountDownLatch localLatch = new CountDownLatch(1);
        
        private volatile Future<?> future;
        
        private AsyncTask() {
        }
        
        public void awaitReady() {
            try {
                localLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void awaitFinished() {
            try {
                localLatch.await();
                if (future instanceof RunnableFuture<?>) {
                    RunnableFuture<?> rf = (RunnableFuture<?>) future;
                    rf.run();//Make sure computation starts asap
                }
                future.get();//Wait for computation to finish
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.awaitFinished();
        }
        
        void signalReady() {
            this.localLatch.countDown();
        }
        
        @Override
        void submit(long id, Runnable runnable, Finishable previous, LongArray others) {
            Future<?> future = executorService.submit(() -> {
                //this.awaitReady();
                if (previous != null) {
                    previous.awaitFinished();
                }
                runnable.run();
                synchronized (taskMap) {
                    if (taskMap.get(id) == this) {
                        taskMap.remove(id);
                    }
                    if (others != null) {
                        for (int i = 0; i < others.size; i++) {
                            long l = others.get(i);
                            if (taskMap.get(l) == this) {
                                taskMap.remove(l);
                            }
                        }
                    }
                    this.signalFinished();
                }
            });
            this.future = future;
            signalReady();
        }
    }
    
    public class Task implements Finishable {
        private final CountDownLatch taskLatch = new CountDownLatch(1);
        
        private Task() {
        }
        
        public void awaitFinished() {
            try {
                taskLatch.await();//If the task is done on the main thread, await its completion
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        void signalFinished() {
            this.taskLatch.countDown();
        }
        
        void submit(long id, Runnable runnable, Finishable previous, LongArray others) {
            if (previous != null) {
                previous.awaitFinished();
            }
            runnable.run();
            synchronized (taskMap) {
                if (taskMap.get(id) == this) {
                    taskMap.remove(id);
                }
                if (others != null) {
                    for (int i = 0; i < others.size; i++) {
                        long l = others.get(i);
                        if (taskMap.get(l) == this) {
                            taskMap.remove(l);
                        }
                    }
                }
                signalFinished();
            }
        }
        
    }
    
}
