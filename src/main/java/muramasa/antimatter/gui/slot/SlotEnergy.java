package muramasa.antimatter.gui.slot;

import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandler;

public class SlotEnergy extends AbstractSlot<SlotEnergy> {
    public SlotEnergy(SlotType<SlotEnergy> type, TileEntityMachine<?> tile, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(type, tile, itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return true;
    }
}
