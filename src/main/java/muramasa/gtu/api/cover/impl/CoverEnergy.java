package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.data.ItemType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class CoverEnergy extends Cover {

    @Override
    public String getName() {
        return "energy";
    }

    @Override
    public ItemStack getDroppedStack() {
        return ItemType.EnergyPort.get(1);
    }

    @Override
    public void onUpdate(TileEntity tile) {
        //TODO
    }
}
