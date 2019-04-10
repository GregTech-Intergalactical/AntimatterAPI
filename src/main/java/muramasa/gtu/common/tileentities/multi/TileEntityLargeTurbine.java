package muramasa.gtu.common.tileentities.multi;

import muramasa.gtu.api.data.Casing;
import muramasa.gtu.api.tileentities.multi.TileEntityCasing;
import muramasa.gtu.api.tileentities.multi.TileEntityFluidMultiMachine;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.util.int3;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLargeTurbine extends TileEntityFluidMultiMachine {

    @Override
    public void onStructureIntegrity(boolean valid) {
        applyTextureToFace(valid ? 0 : -1);
    }

    public void applyTextureToFace(int override) {
        int3 topCorner = new int3(getPos(), getEnumFacing()).left(1).up(1);
        int3 working = new int3();
        TileEntity tile;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                working.set(topCorner).right(x).down(y);
                if (!((tile = Utils.getTile(getWorld(), working.asBP())) instanceof TileEntityCasing)) continue;
                Casing type = ((TileEntityCasing) tile).getType();
                if (type == Casing.TURBINE_4) {
                    ((TileEntityCasing) tile).setTextureOverride(override);
                }
            }
        }
    }
}
