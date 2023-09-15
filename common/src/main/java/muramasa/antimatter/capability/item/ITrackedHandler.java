package muramasa.antimatter.capability.item;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tesseract.api.item.ExtendedItemContainer;

public interface ITrackedHandler extends ExtendedItemContainer {
    @NotNull
    ItemStack insertOutputItem(int slot, @NotNull ItemStack stack, boolean simulate);

    @NotNull
    ItemStack extractFromInput(int slot, int amount, boolean simulate);
}
