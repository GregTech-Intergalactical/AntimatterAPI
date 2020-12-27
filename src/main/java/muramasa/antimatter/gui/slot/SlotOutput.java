package muramasa.antimatter.gui.slot;

import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotOutput extends SlotItemHandler {
    protected TileEntityMachine tile;

    public SlotOutput(TileEntityMachine tile,IItemHandler stackHandler, int index, int x, int y) {
        super(stackHandler, index, x, y);
        this.tile = tile;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(PlayerEntity player) {
        return true;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        tile.onMachineEvent(ContentEvent.ITEM_OUTPUT_CHANGED);
    }
}
