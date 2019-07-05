package muramasa.gtu.common.tileentities.multi;

import muramasa.gtu.api.blocks.BlockCasing;
import muramasa.gtu.api.tileentities.multi.TileEntityCasing;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.util.int3;
import muramasa.gtu.common.Data;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLargeTurbine extends TileEntityMultiMachine {

    @Override
    public boolean onStructureFormed() {
        applyTextureToFace(0);
        return true;
    }

    @Override
    public void onStructureInvalidated() {
        applyTextureToFace(-1);
    }

    public void applyTextureToFace(int override) {
        int3 topCorner = new int3(getPos(), getFacing()).left(1).up(1);
        int3 working = new int3();
        TileEntity tile;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                working.set(topCorner).right(x).down(y);
                if (!((tile = Utils.getTile(getWorld(), working.asBP())) instanceof TileEntityCasing)) continue;
                BlockCasing type = ((TileEntityCasing) tile).getType();
                if (type == Data.CASING_TURBINE_4) {
                    ((TileEntityCasing) tile).setTextureOverride(override);
                }
            }
        }
    }
}
