package muramasa.itech.common.behaviour;

import muramasa.itech.api.behaviour.BehaviourMultiMachine;
import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;

public class BehaviourElectricBlastFurnace extends BehaviourMultiMachine {

    @Override
    public void onRecipe(TileEntityMultiMachine tile) {
        tile.maxProgress = 999;
    }
}
