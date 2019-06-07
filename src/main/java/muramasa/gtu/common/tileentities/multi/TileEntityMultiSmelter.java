package muramasa.gtu.common.tileentities.multi;

import muramasa.gtu.api.blocks.BlockCoil;
import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.structure.StructureResult;
import muramasa.gtu.api.tileentities.multi.TileEntityCoil;
import muramasa.gtu.api.tileentities.multi.TileEntityItemMultiMachine;
import muramasa.gtu.api.util.Utils;

import java.util.List;

public class TileEntityMultiSmelter extends TileEntityItemMultiMachine {

    private int level = 1, discount = 1;

    @Override
    public void onRecipeFound() {
//        this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
//        this.mEfficiencyIncrease = 10000;

        int tier = Utils.getVoltageTier(getMaxInputVoltage());
        this.EUt = (-4 * (1 << tier - 1) * (1 << tier - 1) * this.level / this.discount);
        this.maxProgress = Math.max(1, 512 / (1 << tier - 1));
    }

    @Override
    public boolean onStructureValid(StructureResult result) {
        List<IComponentHandler> coils = getComponents("coil");
        BlockCoil firstType = ((TileEntityCoil) coils.get(0).getTile()).getType();
        if (coils.stream().allMatch(c -> ((TileEntityCoil) c.getTile()).getType() == firstType)) {
            setCoilValues(firstType);
            return true;
        } else {
            result.withError("all coils do not match");
            return false;
        }
    }

    public void setCoilValues(BlockCoil coil) {
        switch (coil.getId()) {
            case "kanthal":
                level = 2;
                break;
            case "nichrome":
                level = 4;
                break;
            case "tungstensteel":
                level = 8;
                break;
            case "hssg":
                level = 16;
                discount = 2;
                break;
            case "naquadah":
                level = 16;
                discount = 4;
                break;
            case "naquadah_alloy":
                level = 16;
                discount = 8;
                break;
        }
    }
}
