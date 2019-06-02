package muramasa.gtu.api.tileentities.pipe;

import muramasa.gtu.api.pipe.types.ItemPipe;
import net.minecraft.tileentity.TileEntity;

public class TileEntityItemPipe extends TileEntityPipe {

    protected boolean restrictive;

    public final void init(boolean restrictive) {
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
