package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.machines.Tier;
import net.minecraft.item.ItemStack;

public class CoverEnergy extends Cover {

    protected Tier tier;

    public CoverEnergy(Tier tier) {
        this.tier = tier;
    }

    @Override
    public String getName() {
        return "energy";
    }

    @Override
    public ItemStack getDroppedStack() {
        return ItemType.EnergyPort.get(1);
    }
}
