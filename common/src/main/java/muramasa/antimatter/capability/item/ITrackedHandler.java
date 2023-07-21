package muramasa.antimatter.capability.item;

import net.minecraft.world.item.ItemStack;
import tesseract.api.item.ExtendedItemContainer;

import javax.annotation.Nonnull;

public interface ITrackedHandler extends ExtendedItemContainer {
    @Nonnull
    ItemStack insertOutputItem(int slot, @Nonnull ItemStack stack, boolean simulate);

    @Nonnull
    ItemStack extractFromInput(int slot, int amount, boolean simulate);
}
