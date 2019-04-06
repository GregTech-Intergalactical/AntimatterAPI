package muramasa.gregtech.api.tileentities.pipe;

import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.ItemPipe;
import net.minecraft.tileentity.TileEntity;

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

    @Override
    public boolean canConnect(TileEntity tile) {
        return tile instanceof TileEntityItemPipe;
    }
}
