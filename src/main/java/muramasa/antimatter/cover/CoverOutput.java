package muramasa.antimatter.cover;

import muramasa.antimatter.machine.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;

public class CoverOutput extends Cover {

    @Override
    public String getId() {
        return "output";
    }

    @Override
    public void onMachineEvent(TileEntityMachine tile, MachineEvent event) {
        if (event == MachineEvent.ITEM_OUTPUT) {
            Direction outputDir = tile.getOutputFacing();
            TileEntity adjTile = Utils.getTile(tile.getWorld(), tile.getPos().offset(outputDir));
            if (adjTile == null) return;
            adjTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, outputDir.getOpposite()).ifPresent(adjHandler -> {
                tile.itemHandler.ifPresent(h -> Utils.transferItems(h.getOutputHandler(), adjHandler));
            });
        }
    }
}
