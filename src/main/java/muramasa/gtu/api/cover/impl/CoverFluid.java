package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.common.Data;
import net.minecraft.item.ItemStack;

public class CoverFluid extends Cover {

    protected Tier tier;

    public CoverFluid(Tier tier) {
        this.tier = tier;
    }

    @Override
    public String getName() {
        return "fluid";
    }

    @Override
    public ItemStack getDroppedStack() {
        switch (tier.getId()) {
            case "lv": return Data.PumpLV.get(1);
            case "mv": return Data.PumpMV.get(1);
            case "hv": return Data.PumpHV.get(1);
            case "ev": return Data.PumpEV.get(1);
            case "iv": return Data.PumpIV.get(1);
            default: return Data.PumpLV.get(1);
        }
    }
}
