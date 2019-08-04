package muramasa.gtu.api.network;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.gui.GuiEvent;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.SoundType;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class GregTechNetwork {

    public static SimpleNetworkWrapper NETWORK;

    public static void init() {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Ref.MODID);
        NETWORK.registerMessage(SoundMessage.SoundMessageHandler.class, SoundMessage.class, NetworkEvent.SOUND.ordinal(), Side.CLIENT);
        NETWORK.registerMessage(FluidStackMessage.FluidStackMessageHandler.class, FluidStackMessage.class, NetworkEvent.FLUID.ordinal(), Side.CLIENT);
        NETWORK.registerMessage(GuiEventMessage.SoundMessageHandler.class, GuiEventMessage.class, NetworkEvent.GUI.ordinal(), Side.SERVER);
    }

    public static void playSoundOnClient(SoundType type) {
        GregTech.PROXY.playSound(type);
    }

    public static void syncMachineTanks(TileEntityMachine tile) {
        if (tile == null) return;
        tile.fluidHandler.ifPresent(h -> {
            NETWORK.sendToAllTracking(new FluidStackMessage(h.getInputsRaw(), h.getOutputsRaw()), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 1));
        });
    }

    public static void sendGuiEvent(GuiEvent event, TileEntityMachine tile) {
        NETWORK.sendToServer(new GuiEventMessage(event, tile.getPos(), tile.getWorld().provider.getDimension()));
    }
}
