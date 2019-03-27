package muramasa.gregtech.common.tileentities.pipe;

import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.ItemPipe;

public class TileEntityItemPipe extends TileEntityPipe {

    protected boolean restrictive;

    public final void init(ItemPipe type, PipeSize size, boolean restrictive) {
        super.init(type, size);
        this.restrictive = restrictive;
    }

    public ItemPipe getType() {
        return (ItemPipe) type;
    }

    public boolean isRestrictive() {
        return restrictive;
    }
}
