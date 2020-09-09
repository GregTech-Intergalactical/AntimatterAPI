package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.capability.CapabilityHandler;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.tile.pipe.TileEntityPipe;

public class PipeCapabilityHandler<T extends ICapabilityHandler> extends CapabilityHandler<TileEntityPipe, T> {

    public PipeCapabilityHandler(TileEntityPipe tile) {
        super(tile);
    }
}
