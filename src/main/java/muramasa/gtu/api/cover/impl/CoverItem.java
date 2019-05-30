package muramasa.gtu.api.cover.impl;

import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.data.ItemType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class CoverItem extends Cover {

    @Override
    public String getName() {
        return "item";
    }

    @Override
    public ItemStack getDroppedStack() {
        return ItemType.ConveyorIV.get(1);
    }

    @Override
    public Cover onPlace(ItemStack stack) {
        //TODO allows instance sensitive data
        return super.onPlace(stack);
    }

    @Override
    public void onUpdate(TileEntity tile) {
        //TODO
    }
}
