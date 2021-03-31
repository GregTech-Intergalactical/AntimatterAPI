package muramasa.antimatter.network.packets;

import muramasa.antimatter.gui.event.GuiEvent;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractGuiEventPacket {
    protected final GuiEvent event;
    protected final BlockPos pos;
    protected final int[] data;

    public AbstractGuiEventPacket(GuiEvent event, BlockPos pos, int... data) {
        this.event = event;
        this.pos = pos;
        this.data = data;
    }
}
