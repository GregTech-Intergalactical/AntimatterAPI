package muramasa.gtu.common.tileentities.multi;

import muramasa.gtu.api.tileentities.multi.TileEntityBasicItemMultiMachine;
import muramasa.gtu.api.util.int3;
import net.minecraft.init.Blocks;

public class TileEntityPrimitiveBlastFurnace extends TileEntityBasicItemMultiMachine {

    @Override
    public void onStructureIntegrity(boolean valid) {
        if (valid) {
            int3 controller = new int3(getPos(), getFacing());
            controller.back(1);
            getWorld().setBlockState(controller.asBP(), Blocks.LAVA.getDefaultState(), 3);
            controller.up(1);
            getWorld().setBlockState(controller.asBP(), Blocks.LAVA.getDefaultState(), 3);
        }
    }
}
