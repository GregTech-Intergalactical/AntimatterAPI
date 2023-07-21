package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.SlotType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tesseract.api.item.ExtendedItemContainer;

import javax.annotation.Nonnull;

public class SlotOutput extends AbstractSlot<SlotOutput> {

    public SlotOutput(SlotType<SlotOutput> type, IGuiHandler tile, ExtendedItemContainer stackHandler, int index, int x, int y) {
        super(type, tile, stackHandler, index, x, y);
    }

    @Override
    public boolean mayPickup(Player player) {
        return true;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }
}
