package muramasa.gregtech.api.tileentities.pipe;

import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.Cable;

public class TileEntityCable extends TileEntityPipe {

    protected boolean insulated;

    public final void init(Cable type, PipeSize size, boolean insulated) {
        super.init(type, size);
        this.insulated = insulated;
    }

    public Cable getType() {
        return (Cable) type;
    }

    public boolean isInsulated() {
        return insulated;
    }
}
