package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.machines.Tier;
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
            case "lv": return ItemType.ConveyorLV.get(1);
            case "mv": return ItemType.ConveyorMV.get(1);
            case "hv": return ItemType.ConveyorHV.get(1);
            case "ev": return ItemType.ConveyorEV.get(1);
            case "iv": return ItemType.ConveyorIV.get(1);
            default: return ItemType.ConveyorLV.get(1);
        }
    }
}
