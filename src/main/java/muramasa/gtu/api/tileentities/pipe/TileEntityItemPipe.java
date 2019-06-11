package muramasa.gtu.api.tileentities.pipe;

import net.minecraft.tileentity.TileEntity;

public class TileEntityItemPipe extends TileEntityPipe {

    protected boolean restrictive;

    public final void init(boolean restrictive) {
        this.restrictive = restrictive;
    }

    public boolean isRestrictive() {
        return restrictive;
    }

    @Override
    public boolean canConnect(TileEntity tile) {
        return tile instanceof TileEntityItemPipe;
    }
}
