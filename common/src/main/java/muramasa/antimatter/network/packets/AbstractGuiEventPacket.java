package muramasa.antimatter.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import muramasa.antimatter.gui.event.IGuiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractGuiEventPacket<T extends AbstractGuiEventPacket<T>> implements Packet<T> {
    protected final IGuiEvent event;
    protected final BlockPos pos;
    protected final ResourceLocation channelId;

    public AbstractGuiEventPacket(IGuiEvent event, BlockPos pos, ResourceLocation channelId) {
        this.event = event;
        this.pos = pos;
        this.channelId = channelId;
    }

    public ResourceLocation getID() {
        return channelId;
    }
}
