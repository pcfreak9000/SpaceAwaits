package de.pcfreak9000.spaceawaits.knowledge;

public class Knowledge {

    private String displayname = "";

    public String getDisplayName() {
        return displayname;
    }

    public void setDisplayName(String s) {
        this.displayname = s;
    }

    public boolean hasData() {
        return false;
    }

    public UnlockProgress createDataHolder(Knowledgebase kb) {
        return null;
    }
}
