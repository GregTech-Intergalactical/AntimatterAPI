package muramasa.antimatter.cover;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.machine.Tier;
import java.util.Arrays;

//A cover that is tiered, e.g. a Conveyor or Pump.
public abstract class CoverTiered extends Cover {

    protected Tier tier;

    public CoverTiered() {
        super();
        register();
    }
    @Override
    protected void register() {
        Arrays.stream(Tier.getStandard()).forEach(t -> {
            CoverTiered tier = getTiered(t);
            AntimatterAPI.register(Cover.class, tier.getId(), tier);
            AntimatterAPI.register(getClass(), tier.getId(), tier);
        });
    }

    protected CoverTiered(Tier tier) {
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

    //Small override for covers with their actual ID since this superclass
    //Tierifies the IDs.
    protected abstract String ID();

    //To create a Tiered instance of the subclass.
    protected abstract CoverTiered getTiered(Tier tier);

}
