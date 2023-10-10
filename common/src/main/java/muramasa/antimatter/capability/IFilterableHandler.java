package muramasa.antimatter.capability;

import muramasa.antimatter.gui.SlotType;
import net.minecraft.world.item.ItemStack;

public interface IFilterableHandler {
    boolean test(SlotType<?> slotType, int slot, ItemStack stack);
}
