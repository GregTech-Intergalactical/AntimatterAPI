package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.machine.MachineItemHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import tesseract.api.capability.TesseractGTCapability;

import javax.annotation.Nonnull;

public class SlotEnergy extends SlotItemHandler {
    int index;

    public SlotEnergy(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.index = index;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return stack.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).isPresent();
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int amount) {
        return MachineItemHandler.extractFromInput(this.getItemHandler(), index, amount, false);
    }
}
