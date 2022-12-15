package muramasa.antimatter.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import tesseract.api.context.TesseractItemContext;

public interface IFluidItem {
    IFluidHandlerItem getFluidHandlerItem(ItemStack context);
}
