package muramasa.gtu.api.cover;

import muramasa.gtu.api.machines.MachineEvent;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;

public class CoverOutput extends Cover {

    @Override
    public String getId() {
        return "output";
    }

    @Override
    public void onMachineEvent(TileEntityMachine tile, MachineEvent event) {
        if (event == MachineEvent.ITEM_OUTPUT) {
            EnumFacing outputFacing = tile.getOutputFacing();
            TileEntity adjTile = tile.getWorld().getTileEntity(tile.getPos().offset(outputFacing));
            if (adjTile == null) return;
            if (!adjTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputFacing.getOpposite())) return;
            tile.itemHandler.ifPresent(h -> Utils.transferItems(h.getOutputHandler(), adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputFacing.getOpposite())));
        }
    }
}
