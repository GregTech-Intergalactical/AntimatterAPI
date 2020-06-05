package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.AntimatterCaps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotEnergy extends SlotItemHandler {
    public SlotEnergy(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return stack.getCapability(AntimatterCaps.ENERGY).isPresent();
    }


    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return true;
    }
}
