package de.pcfreak9000.spaceawaits.knowledge;

public abstract class UnlockProgress {
    
    protected final Knowledge knowledge;
    protected final Knowledgebase knowledgebase;
    
    public UnlockProgress(Knowledge knowledge, Knowledgebase knowledgebase) {
        this.knowledge = knowledge;
        this.knowledgebase = knowledgebase;
    }
    
    public boolean tryUnlockKnowledge() {
        if (canUnlockKnowledge()) {
            unlockKnowledge();
            return true;
        }
        return false;
    }
    
    protected boolean canUnlockKnowledge() {
        return false;
    }
    
    protected void unlockKnowledge() {
        knowledgebase.unlock(knowledge);
    }
    
}
