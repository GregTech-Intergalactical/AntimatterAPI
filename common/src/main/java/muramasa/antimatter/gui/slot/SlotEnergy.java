package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.SlotType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tesseract.api.item.ExtendedItemContainer;

import javax.annotation.Nonnull;

public class SlotEnergy extends AbstractSlot<SlotEnergy> {
    public SlotEnergy(SlotType<SlotEnergy> type, IGuiHandler tile, ExtendedItemContainer itemHandler, int index, int xPosition, int yPosition) {
        super(type, tile, itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        return 1;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }
}
