package muramasa.antimatter.network.packets;

import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractGuiEventPacket {
    protected final IGuiEvent event;
    protected final BlockPos pos;
    protected final int[] data;

    public AbstractGuiEventPacket(IGuiEvent event, BlockPos pos, int... data) {
        this.event = event;
        this.pos = pos;
        this.data = data;
    }
}
