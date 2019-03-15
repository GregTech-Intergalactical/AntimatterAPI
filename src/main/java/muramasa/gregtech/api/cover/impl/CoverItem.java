package muramasa.gregtech.api.cover.impl;

import muramasa.gregtech.api.cover.Cover;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class CoverItem extends Cover {

    @Override
    public String getName() {
        return "item";
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
