package muramasa.gtu.api.tileentities.pipe;

import net.minecraft.tileentity.TileEntity;

public class TileEntityItemPipe extends TileEntityPipe {

    @Override
    public boolean canConnect(TileEntity tile) {
        return tile instanceof TileEntityItemPipe;
    }
}
