package muramasa.antimatter.tile;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.Utils;
import net.minecraftforge.fluids.FluidStack;

//TODO change to generic "FluidPoweredMachine" and pass FluidStack somehow
public class TileEntitySteamMachine extends TileEntityRecipeMachine {

    protected static FluidStack[] STEAM = new FluidStack[]{Material.get("steam").getGas(1)};

    @Override
    public void consumeInputs() {
        //Only consume items here, consume STEAM on consumeResourceForRecipe
        itemHandler.ifPresent(h -> h.consumeInputs(activeRecipe.getInputItems()));
    }

    @Override
    public boolean consumeResourceForRecipe() {
        STEAM[0].setAmount((int) activeRecipe.getPower());
        if (Utils.doFluidsMatchAndSizeValid(STEAM, fluidHandler.get().getInputs())) {
            fluidHandler.get().consumeInputs(STEAM);
            return true;
        }
        return false;
    }
}
