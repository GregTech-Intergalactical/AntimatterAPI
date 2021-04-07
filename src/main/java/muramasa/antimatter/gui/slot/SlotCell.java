package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.machine.MachineItemHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotCell extends SlotItemHandler {

    private int index;

    public SlotCell(IItemHandler stackHandler, int index, int x, int y) {
        super(stackHandler, index, x, y);
        this.index = index;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int amount)
    {
        return MachineItemHandler.extractFromInput(this.getItemHandler(), index, amount, false);
    }

}
