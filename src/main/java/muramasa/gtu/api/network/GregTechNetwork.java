package muramasa.gtu.api.network;

import muramasa.gtu.GregTech;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GregTechNetwork {

    public static void syncMachineTanks(TileEntityMachine tile) {
        if (tile == null || tile.getFluidHandler() == null) return;
        GregTech.NETWORK.sendToAllTracking(new FluidStackMessage(tile.getFluidHandler().getInputsRaw(), tile.getFluidHandler().getOutputsRaw()), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 1));
    }
}
