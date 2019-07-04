package muramasa.gtu.common.tileentities.multi;

import muramasa.gtu.api.structure.StructureResult;
import muramasa.gtu.api.tileentities.multi.TileEntityBasicMultiMachine;
import muramasa.gtu.api.util.int3;
import net.minecraft.init.Blocks;

public class TileEntityPrimitiveBlastFurnace extends TileEntityBasicMultiMachine {

    @Override
    public boolean onStructureFormed(StructureResult result) {
        int3 controller = new int3(getPos(), getFacing());
        controller.back(1);
        getWorld().setBlockState(controller.asBP(), Blocks.LAVA.getDefaultState(), 3);
        controller.up(1);
        getWorld().setBlockState(controller.asBP(), Blocks.LAVA.getDefaultState(), 3);
        return true;
    }
}
