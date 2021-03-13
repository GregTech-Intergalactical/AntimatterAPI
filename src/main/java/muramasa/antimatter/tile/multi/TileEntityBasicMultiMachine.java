package muramasa.antimatter.tile.multi;

import muramasa.antimatter.capability.machine.BasicMultiMachineEnergyHandler;
import muramasa.antimatter.capability.machine.BasicMultiMachineFluidHandler;
import muramasa.antimatter.capability.machine.MultiMachineEnergyHandler;
import muramasa.antimatter.capability.machine.MultiMachineFluidHandler;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.util.LazyHolder;

import static muramasa.antimatter.machine.MachineFlag.ENERGY;
import static muramasa.antimatter.machine.MachineFlag.FLUID;
import static muramasa.antimatter.machine.MachineFlag.GENERATOR;

/** Allows a MultiMachine to handle GUI recipes, instead of using Hatches **/
public class TileEntityBasicMultiMachine extends TileEntityMultiMachine {

    public TileEntityBasicMultiMachine(Machine<?> type) {
        super(type);
        this.fluidHandler = type.has(FLUID) ? LazyHolder.of(() -> new BasicMultiMachineFluidHandler(this)) : LazyHolder.empty();
        this.energyHandler = type.has(ENERGY) ? LazyHolder.of(() -> new BasicMultiMachineEnergyHandler(this, type.amps(),type.has(GENERATOR))) : LazyHolder.empty();
        // TODO
        /*
        recipeHandler.setup((tile, tag) -> new MultiMachineRecipeHandler<TileEntityMultiMachine>(tile, tag) {
            @Override
            public Recipe findRecipe() { //TODO support fluids?
                return getMachineType().getRecipeMap().find(itemHandler.get(), null);
            }

            @Override
            public void consumeInputs() {
                itemHandler.ifPresent(h -> h.consumeInputs(activeRecipe,false));
            }

            @Override
            public boolean canOutput() {
                return itemHandler.isPresent() && itemHandler.get().canOutputsFit(activeRecipe.getOutputItems());
            }

            @Override
            public void addOutputs() {
                itemHandler.ifPresent(h -> h.addOutputs(activeRecipe.getOutputItems()));
            }

            @Override
            public boolean canRecipeContinue() {
                return itemHandler.isPresent() && Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), itemHandler.get().getInputs());
            }

            @Override
            public boolean consumeResourceForRecipe() {
                return true;
            }
        });
         */
    }
}
