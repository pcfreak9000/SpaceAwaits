package de.pcfreak9000.spaceawaits.util;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.utils.LongMap;

import de.pcfreak9000.spaceawaits.util.TaskScheduler.Task;
@Deprecated
public class ChainedScheduler {
    
    private TaskScheduler scheduler;
    private LongMap<Chain> chainMap = new LongMap<>();
    private ConcurrentLinkedQueue<Runnable> runOnMainThread = new ConcurrentLinkedQueue<>();
    
    public ChainedScheduler(TaskScheduler taskScheduler) {
        this.scheduler = taskScheduler;
    }
    
    public void flush() {
        while (!runOnMainThread.isEmpty()) {
            runOnMainThread.poll().run();
        }
    }
    
    public Chain submit(long key, boolean blocking, ChainLink... links) {
        Chain chain = new Chain(links);
        chain.mainthread = blocking;
        
        Chain prev = chainMap.put(key, chain);
        if (prev != null) {
            prev.next = chain;
            chain.previous = prev;
        } else {
            submitNestedChain(key, 0, chain);
        }
        return chain;
    }
    
    private void submitNestedChain(long key, int subindex, Chain chain) {
        //TODO dynamic chains
        for (int i = subindex; i < chain.links.length; i++) {
            ChainLink link = chain.links[i];
            boolean block = link.waitForPrevious || chain.mainthread;
            if (i + 1 < chain.links.length && (chain.links[i + 1].waitForPrevious || chain.mainthread)
                    && !link.waitForPrevious) {
                final int subi = i + 1;
                chain.latestTask = scheduler.submit(key, block, () -> {
                    link.runnable.run();
                    runOnMainThread.add(() -> submitNestedChain(key, subi, chain));
                });
                flush();
                return;
            } else {
                chain.latestTask = scheduler.submit(key, block, link.runnable);
            }
            flush();
        }
        if (chain.next != null) {
            chain.awaitFinished();
            submitNestedChain(key, 0, chain.next);
        }
    }
    
    public class Chain {
        private ChainLink[] links;
        private Task latestTask;
        private boolean mainthread;
        
        private Chain previous;
        private Chain next;
        
        private Chain(ChainLink[] links) {
            this.links = links;
        }
        
        public void awaitFinished() {
            if (previous != null) {
                previous.awaitFinished();
            }
            mainthread = true;
            latestTask.awaitFinished();
            flush();
        }
        
    }
    
    public class ChainLink {
        private boolean waitForPrevious;
        private Runnable runnable;
        
        public ChainLink(boolean blocking, Runnable run) {
            this.waitForPrevious = blocking;
            this.runnable = run;
        }
    }
    
}
