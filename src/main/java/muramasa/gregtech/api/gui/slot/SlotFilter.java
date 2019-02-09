package muramasa.gregtech.api.gui.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotFilter extends SlotItemHandler {

    public SlotFilter(IItemHandler itemHandler, int index, int x, int y) {
        super(itemHandler, index, x, y);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return true; //TODO
    }
}
