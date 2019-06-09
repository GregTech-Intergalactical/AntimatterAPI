package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.common.Data;
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
        return Data.EnergyPort.get(1);
    }
}
