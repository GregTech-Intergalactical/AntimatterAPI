package muramasa.antimatter.cover;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.machine.Tier;
import java.util.Arrays;

//A cover that is tiered, e.g. a Conveyor or Pump.
public abstract class CoverTiered extends Cover {

    protected Tier tier;

    public CoverTiered() {
        super();
        for (Tier t : Tier.getStandard()) {
            CoverTiered tier = getTiered(t);
            AntimatterAPI.register(Cover.class, tier.getId(), tier);
        }
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
        String id = ID();
        return tier == null ? id : id + "_" + tier.getId();
    }

    // Small override for covers with their actual ID since this superclass
    // Tierifies the IDs.
    protected abstract String ID();

    // To create a Tiered instance of the subclass.
    protected abstract CoverTiered getTiered(Tier tier);
}
