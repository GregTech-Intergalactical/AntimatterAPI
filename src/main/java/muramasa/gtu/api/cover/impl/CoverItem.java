package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.common.Data;
import net.minecraft.item.ItemStack;

public class CoverItem extends Cover {

    protected Tier tier;

    public CoverItem(Tier tier) {
        this.tier = tier;
    }

    @Override
    public String getName() {
        return "item";
    }

    @Override
    public ItemStack getDroppedStack() {
        switch (tier.getId()) { //TODO maybe a better way to do this? but for now, it works.
            case "lv": return Data.ConveyorLV.get(1);
            case "mv": return Data.ConveyorMV.get(1);
            case "hv": return Data.ConveyorHV.get(1);
            case "ev": return Data.ConveyorEV.get(1);
            case "iv": return Data.ConveyorIV.get(1);
            default: return Data.ConveyorLV.get(1);
        }
    }
}
