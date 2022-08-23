package de.pcfreak9000.spaceawaits.comp;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
@Deprecated
public class CompositeManager {
    
    private final Builder builder = new Builder();
    
    private final ObjectMap<String, Composite> composites = new ObjectMap<>();
    
    public Builder create(int level, String id) {
        return builder.reset(level, id);
    }
    
    public Composite getCompositeForName(String id) {
        return composites.get(id);
    }
    
    public class Builder {
        
        private int level;
        private String id;
        private Array<CompositeData> composition;
        
        private Builder reset(int level, String id) {
            this.level = level;
            this.id = id;
            this.composition = new Array<>();
            if (composites.containsKey(id)) {
                throw new RuntimeException();
            }
            return this;
        }
        
        //float or int amount?
        public Builder add(float amount, String id) {
            Composite comp = composites.get(id);
            if (comp == null) {
                throw new RuntimeException();
            }
            if (comp.getLevel() >= this.level) {
                throw new RuntimeException();
            }
            if (amount < 0) {
                throw new RuntimeException();
            }
            //adding the same Composition multiple times is possible but inefficient
            composition.add(new CompositeData(amount, comp));
            return this;
        }
        
        public Composite build() {
            Composite comp = new Composite(level, new ImmutableArray<>(this.composition), id);
            composites.put(id, comp);
            return comp;
        }
    }
}
