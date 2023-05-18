package de.pcfreak9000.spaceawaits.science;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.badlogic.gdx.utils.ObjectIntMap;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;

public class Science implements INBTSerializable {
    
    public static final Registry<IScienceTrigger> TRIGGER_REGISTRY = new Registry<>();
    
    private Set<ScienceFinding> submittedFindings;
    private Set<ScienceFinding> submittedFindingsImmutable;
    
    private ObjectIntMap<ScienceFinding> findingRarity;
    
    public Science() {
        this.submittedFindings = new LinkedHashSet<>();
        this.submittedFindingsImmutable = Collections.unmodifiableSet(this.submittedFindings);
    }
    
    public void submitFinding(ScienceType type, int findingRarity, String findingmsg) {
        ScienceFinding finding = new ScienceFinding(type, findingmsg);
        if (this.submittedFindings.add(finding)) {
            this.findingRarity.put(finding, findingRarity);
            //probably inefficient but ok for now
            for (IScienceTrigger trig : TRIGGER_REGISTRY.getAll()) {
                if (trig.isComplete(this)) {
                    trig.triggerComplete(this);
                }
            }
        }
    }
    
    public boolean isSubmitted(ScienceFinding finding) {
        return submittedFindings.contains(finding);
    }
    
    public Set<ScienceFinding> getSubmittedFindings() {
        return this.submittedFindingsImmutable;
    }
    
    public int getRarity(ScienceFinding finding) {
        return findingRarity.get(finding, Integer.MIN_VALUE);
    }
    
    @Override
    public void readNBT(NBTCompound nbt) {
        NBTList nbtfindings = nbt.getList("findings");
        for (int i = 0; i < nbtfindings.size(); i++) {
            NBTCompound findingnbt = nbtfindings.getCompound(i);
            ScienceFinding finding = new ScienceFinding();
            finding.readNBT(findingnbt);
            this.submittedFindings.add(finding);
        }
    }
    
    @Override
    public void writeNBT(NBTCompound nbt) {
        NBTList nbtfindings = new NBTList(NBTType.Compound);
        for (ScienceFinding find : this.submittedFindings) {
            nbtfindings.addCompound(INBTSerializable.writeNBT(find));
        }
        nbt.putList("findings", nbtfindings);
    }
}
