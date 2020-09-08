package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.capability.CapabilityHolder;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.tile.pipe.TileEntityPipe;

public class PipeCapabilityHolder<T extends ICapabilityHandler> extends CapabilityHolder<TileEntityPipe, T> {

    public PipeCapabilityHolder(TileEntityPipe tile) {
        super(tile);
    }
}
