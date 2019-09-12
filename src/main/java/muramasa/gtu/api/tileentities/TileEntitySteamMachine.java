package muramasa.gtu.api.tileentities;

import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.util.Utils;
import net.minecraftforge.fluids.FluidStack;

public class TileEntitySteamMachine extends TileEntityRecipeMachine {

    protected static FluidStack[] STEAM = new FluidStack[]{Materials.Steam.getGas(1)};

    @Override
    public void consumeInputs() {
        //Only consume items here, consume STEAM on consumeResourceForRecipe
        itemHandler.ifPresent(h -> h.consumeInputs(activeRecipe.getInputItems()));
    }

    @Override
    public boolean consumeResourceForRecipe() {
        STEAM[0].amount = (int) activeRecipe.getPower();
        if (Utils.doFluidsMatchAndSizeValid(STEAM, fluidHandler.get().getInputs())) {
            fluidHandler.get().consumeInputs(STEAM);
            return true;
        }
        return false;
    }
}
