package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;

public class SlotFakeFluid extends AbstractSlot {

    TileEntityMachine tile;
    public final MachineFluidHandler.FluidDirection dir;

    public SlotFakeFluid(TileEntityMachine tile, MachineFluidHandler.FluidDirection dir, int index, int x, int y) {
        super(new EmptyHandler(), index, x, y);
        this.tile = tile;
        this.dir = dir;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return false;
    }
}
