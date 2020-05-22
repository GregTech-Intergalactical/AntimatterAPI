package muramasa.antimatter.cover;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.machine.Tier;
import java.util.Arrays;

public abstract class CoverTiered extends Cover {

    protected Tier tier;

    public CoverTiered() {
        Arrays.stream(Tier.getStandard()).forEach(t -> {
            CoverTiered tier = getTiered(t);
            AntimatterAPI.register(Cover.class,tier.getId() ,tier);
        });
    }

    protected CoverTiered(Tier tier) {
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public String getId() {
        String i = ID();
        if (tier == null) {
            return i;
        }
        return i + "_" + tier.getId();
    }

    protected abstract String ID();
    protected abstract CoverTiered getTiered(Tier tier);

}
