package muramasa.antimatter.cover;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.machine.Tier;
import speiger.src.collections.objects.maps.impl.hash.Object2ObjectOpenHashMap;

import java.util.Arrays;
import java.util.Map;

//A cover that is tiered, e.g. a Conveyor or Pump.
public abstract class CoverTiered extends BaseCover {

    protected Tier tier;
    protected Map<Tier, CoverTiered> COVERS;

    public CoverTiered() {
        super();
        this.COVERS = new Object2ObjectOpenHashMap<>();
        register();
    }
    @Override
    protected void register() {
        Arrays.stream(Tier.getStandard()).forEach(t -> {
            CoverTiered tier = getTiered(t);
            AntimatterAPI.register(ICover.class, tier.getId(), tier);
            AntimatterAPI.register(getClass(), tier.getId(), tier);
            this.COVERS.put(t, tier);
        });
    }

    protected CoverTiered(Tier tier) {
        super();
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    final public String getId() {
        String i = ID();
        //TODO: this should never happen
        if (tier == null) {
            return i;
        }
        return i + "_" + tier.getId();
    }

    public CoverTiered getCover(Tier tier) {
        return this.COVERS.get(tier);
    }

    public Map<Tier, CoverTiered> getAllCovers() {
        return this.COVERS;
    }

    //Small override for covers with their actual ID since this superclass
    //Tierifies the IDs.
    protected abstract String ID();

    //To create a Tiered instance of the subclass.
    protected abstract CoverTiered getTiered(Tier tier);

}
