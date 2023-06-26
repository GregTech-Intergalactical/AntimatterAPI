package muramasa.antimatter.network.packets;

import muramasa.antimatter.gui.event.IGuiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import trinsdar.networkapi.api.IPacket;

public abstract class AbstractGuiEventPacket implements IPacket {
    protected final IGuiEvent event;
    protected final BlockPos pos;
    protected final ResourceLocation channelId;

    public AbstractGuiEventPacket(IGuiEvent event, BlockPos pos, ResourceLocation channelId) {
        this.event = event;
        this.pos = pos;
        this.channelId = channelId;
    }

    public ResourceLocation getChannelId() {
        return channelId;
    }
}
