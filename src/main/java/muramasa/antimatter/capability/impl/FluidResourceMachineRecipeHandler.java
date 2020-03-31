package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraftforge.fluids.FluidStack;

public class FluidResourceMachineRecipeHandler<T extends TileEntityMachine> extends MachineRecipeHandler<T> {

    protected FluidStack[] resource;

    public FluidResourceMachineRecipeHandler(T tile, FluidStack resource) {
        super(tile);
        this.resource = new FluidStack[]{resource};
    }

    @Override
    public void consumeInputs() {
        //Only consume items here, consume STEAM on consumeResourceForRecipe
        tile.itemHandler.ifPresent(h -> h.consumeInputs(activeRecipe.getInputItems()));
    }

    @Override
    public boolean consumeResourceForRecipe() {
        if (!tile.fluidHandler.isPresent()) return false;
        resource[0].setAmount(getConsumptionPerRecipe());
        if (Utils.doFluidsMatchAndSizeValid(resource, tile.fluidHandler.get().getInputs())) {
            tile.fluidHandler.get().consumeInputs(resource);
            return true;
        }
        return false;
    }

    public int getConsumptionPerRecipe() {
        return (int) activeRecipe.getPower();
    }
}
