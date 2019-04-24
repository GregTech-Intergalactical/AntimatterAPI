package muramasa.gtu.common.tileentities.multi;

import muramasa.gtu.api.tileentities.multi.TileEntityFluidMultiMachine;

public class TileEntityFusionReactor extends TileEntityFluidMultiMachine {

    @Override
    public void onRecipeFound() {
        consumeEnergy(activeRecipe.getSpecialValue());
        System.out.println("Consumed Starting Energy");
    }

    @Override
    public boolean consumeResourceForRecipe() {
        if (getStoredEnergy() >= activeRecipe.getPower()) {
            consumeEnergy(activeRecipe.getPower());
            return true;
        }
        return false;
    }
}
