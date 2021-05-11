package muramasa.antimatter.gui.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class SlotCell extends AbstractSlot {

    public SlotCell(IItemHandler stackHandler, int index, int x, int y) {
        super(stackHandler, index, x, y);
    }

}
