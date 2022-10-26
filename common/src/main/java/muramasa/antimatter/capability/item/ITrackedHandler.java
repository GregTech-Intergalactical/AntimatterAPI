package muramasa.antimatter.capability.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public interface ITrackedHandler extends IItemHandlerModifiable {
    @Nonnull
    ItemStack insertOutputItem(int slot, @Nonnull ItemStack stack, boolean simulate);

    @Nonnull
    ItemStack extractFromInput(int slot, int amount, boolean simulate);
}
