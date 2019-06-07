package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.machines.Tier;
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
            case "lv": return ItemType.PumpLV.get(1);
            case "mv": return ItemType.PumpMV.get(1);
            case "hv": return ItemType.PumpHV.get(1);
            case "ev": return ItemType.PumpEV.get(1);
            case "iv": return ItemType.PumpIV.get(1);
            default: return ItemType.PumpLV.get(1);
        }
    }
}
