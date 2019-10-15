package muramasa.gtu.api.cover;

import muramasa.gtu.api.machines.MachineEvent;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
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
