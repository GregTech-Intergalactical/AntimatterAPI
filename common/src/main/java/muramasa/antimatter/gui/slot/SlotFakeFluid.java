package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.item.EmptyContainer;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.gui.SlotType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotFakeFluid extends AbstractSlot<SlotFakeFluid> {

    public final MachineFluidHandler.FluidDirection dir;

    public SlotFakeFluid(SlotType<SlotFakeFluid> type, IGuiHandler tile, MachineFluidHandler.FluidDirection dir, int index, int x, int y) {
        super(type, tile, new EmptyContainer(), index, x, y);
        this.dir = dir;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }
}
