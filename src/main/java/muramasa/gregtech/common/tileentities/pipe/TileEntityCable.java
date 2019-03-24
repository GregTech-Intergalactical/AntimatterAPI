package muramasa.gregtech.common.tileentities.pipe;

import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.Cable;
import muramasa.gregtech.common.blocks.pipe.BlockCable;

public class TileEntityCable extends TileEntityPipe {

    private boolean insulated;

    public final void init(PipeSize size, boolean insulated) {
        super.init(size);
        this.insulated = insulated;
        System.out.println("CABLE");
    }

    public Cable getType() {
        return ((BlockCable) getBlockType()).getType();
    }

    public boolean isInsulated() {
        return insulated;
    }
}
