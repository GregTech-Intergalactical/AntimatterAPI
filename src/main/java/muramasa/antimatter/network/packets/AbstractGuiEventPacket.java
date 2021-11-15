package muramasa.antimatter.network.packets;

import muramasa.antimatter.gui.event.IGuiEvent;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractGuiEventPacket {
    protected final IGuiEvent event;
    protected final BlockPos pos;

    public AbstractGuiEventPacket(IGuiEvent event, BlockPos pos) {
        this.event = event;
        this.pos = pos;
    }
}
