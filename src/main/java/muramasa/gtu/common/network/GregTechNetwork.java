package muramasa.gtu.common.network;

import muramasa.gtu.Ref;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class GregTechNetwork {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Ref.MODID);

    public enum Type {
        FLUID,
        TEXTURE
    }

    static {
        INSTANCE.registerMessage(FluidStackMessage.FluidStackMessageHandler.class, FluidStackMessage.class, Type.FLUID.ordinal(), Side.CLIENT);
    }

    public static void sendTileTankToClient(TileEntityMachine tile) {
        if (tile == null || tile.getFluidHandler() == null) return;
        FluidStack[] inputs = tile.getFluidHandler().getInputsRaw();
        if (inputs == null) {
            System.out.println("inputs null");
            return;
        }
        INSTANCE.sendToAllTracking(new FluidStackMessage(inputs), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 1));
    }
}
