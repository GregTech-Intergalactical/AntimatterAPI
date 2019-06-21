package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.capability.impl.*;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.structure.IComponent;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.structure.Structure;
import muramasa.gtu.api.structure.StructureResult;
import muramasa.gtu.api.tileentities.TileEntityRecipeMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class TileEntityMultiMachine extends TileEntityRecipeMachine implements IComponent {

    //TODO set protected
    public boolean validStructure;
    //TODO move to BasicMachine
    protected int efficiency, efficiencyIncrease;
    protected long EUt;
    protected HashMap<String, ArrayList<IComponentHandler>> components = new HashMap<>();
    protected ControllerComponentHandler componentHandler;

    @Override
    public void onLoad() {
        super.onLoad();
        componentHandler = new ControllerComponentHandler(getType(), this);
        configHandler = new ControllerConfigHandler(this);
    }

    public boolean checkStructure() {
        clearComponents();
        Structure structure = getType().getStructure(getTier());
        if (structure == null) return false;
        StructureResult result = structure.evaluate(this);
        if (result.evaluate()) {
            components = result.getComponents();
            if (onStructureValid(result)) {
                components.forEach((k, v) -> v.forEach(c -> c.linkController(this)));
                System.out.println("[Structure Debug] Valid Structure");
                return (validStructure = true);
            }
        }
        System.out.println("[Structure Debug] Invalid Structure" + result.getError());
        clearComponents();
        return (validStructure = false);
    }

    /** Events **/
    public boolean onStructureValid(StructureResult result) {
        return true;
    }

    public void onStructureInvalid() {
        validStructure = false;
        clearComponents();
        resetMachine();
        System.out.println("INVALIDATED STRUCTURE");
    }

    @Override
    public Recipe findRecipe() {
        if (hasFlag(MachineFlag.ITEM)) return RecipeMap.findRecipeItem(getType().getRecipeMap(), getMaxInputVoltage(), getStoredItems());
        else if (hasFlag(MachineFlag.FLUID)) return RecipeMap.findRecipeFluid(getType().getRecipeMap(), getMaxInputVoltage(), getStoredFluids());
        return null;
    }

    @Override
    public void consumeInputs() {
        if (activeRecipe.hasInputItems()) consumeItems(activeRecipe.getInputItems());
        if (activeRecipe.hasInputFluids()) consumeFluids(activeRecipe.getInputFluids());
    }

    @Override
    public boolean canOutput() {
        if ((hasFlag(MachineFlag.ITEM) && !canItemsFit(activeRecipe.getOutputItems())) ||
            (hasFlag(MachineFlag.FLUID) && !canFluidsFit(activeRecipe.getOutputFluids()))) {
            return false;
        }
        return true;
    }

    @Override
    public void addOutputs() {
        if (hasFlag(MachineFlag.ITEM)) outputItems(activeRecipe.getOutputItems());
        if (hasFlag(MachineFlag.FLUID)) outputFluids(activeRecipe.getOutputFluids());
    }

    @Override
    public boolean canRecipeContinue() {
        if ((hasFlag(MachineFlag.ITEM) && !Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), getStoredItems())) ||
            (hasFlag(MachineFlag.FLUID) && !Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), getStoredFluids()))) {
            return false;
        }
        return true;
    }

    @Override
    public boolean consumeResourceForRecipe() {
        //TODO breaks generators like combustion engine
//        if (getStoredEnergy() >= activeRecipe.getPower()) {
//            consumeEnergy(activeRecipe.getPower());
//            return true;
//        }
//        return false;
        return true;
    }

    /** Returns list of items across all input hatches. Merges equal filters empty **/
    public ItemStack[] getStoredItems() {
        ArrayList<ItemStack> all = new ArrayList<>();
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_I)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            Utils.mergeItems(all, itemHandler.getInputList());
        }
        System.out.println(all.toString());
        return all.toArray(new ItemStack[0]);
    }

    /** Returns list of fluids across all input hatches. Merges equal filters empty **/
    public FluidStack[] getStoredFluids() {
        ArrayList<FluidStack> all = new ArrayList<>();
        MachineFluidHandler fluidHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_I)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            Utils.mergeFluids(all, fluidHandler.getInputList());
        }
        System.out.println(all.toString());
        return all.toArray(new FluidStack[0]);
    }

    /** Returns the total energy stored across all energy hatches **/
    public long getStoredEnergy() {
        long total = 0;
        MachineEnergyHandler energyHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ENERGY)) {
            energyHandler = hatch.getEnergyHandler();
            if (energyHandler == null) continue;
            total += energyHandler.getEnergyStored();
        }
        return total;
    }

    /** Consumes inputs from all input hatches. Assumes Utils.doItemsMatchAndSizeValid has been used **/
    public void consumeItems(ItemStack[] items) {
        if (items == null) return;
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_I)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            items = itemHandler.consumeAndReturnInputs(items.clone());
            if (items.length == 0) break;
        }
        if (items.length > 0) System.out.println("DID NOT CONSUME ALL: " + items.toString());
    }

    /** Consumes inputs from all input hatches. Assumes Utils.doFluidsMatchAndSizeValid has been used **/
    public void consumeFluids(FluidStack[] fluids) {
        if (fluids == null) return;
        MachineFluidHandler fluidHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_I)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            fluids = fluidHandler.consumeAndReturnInputs(fluids);
            if (fluids.length == 0) break;
        }
        if (fluids.length > 0) System.out.println("DID NOT CONSUME ALL: " + fluids.toString());
    }

    /** Consumes energy from all energy hatches. Assumes enough energy is present in hatches **/
    public void consumeEnergy(long energy) {
        if (energy <= 0) return;
        MachineEnergyHandler energyHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ENERGY)) {
            energyHandler = hatch.getEnergyHandler();
            if (energyHandler == null) return;
            energy -= energyHandler.extract(energy, false);
            if (energy == 0) break;
        }
    }

    /** Export items to hatches regardless of space. Assumes canOutputsFit has been used **/
    public void outputItems(ItemStack[] items) {
        if (items == null) return;
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_O)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            items = itemHandler.exportAndReturnOutputs(items.clone()); //WHY CLONE?!!?
            if (items.length == 0) break;
        }
        if (items.length > 0) System.out.println("HATCH OVERFLOW: " + items.toString());
    }

    /** Export fluids to hatches regardless of space. Assumes canOutputsFit has been used **/
    public void outputFluids(FluidStack[] fluids) {
        if (fluids == null) return;
        MachineFluidHandler fluidHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_O)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            fluids = fluidHandler.exportAndReturnOutputs(fluids.clone());
            if (fluids.length == 0) break;
        }
        if (fluids.length > 0) System.out.println("HATCH OVERFLOW: " + fluids.toString());
    }

    /** Tests if items can fit across all output hatches **/
    public boolean canItemsFit(ItemStack[] items) {
        if (items == null) return true;
        int matchCount = 0;
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_O)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            matchCount += itemHandler.getSpaceForOutputs(items);
        }
        return matchCount >= items.length;
    }

    /** Tests if fluids can fit across all output hatches **/
    public boolean canFluidsFit(FluidStack[] fluids) {
        if (fluids == null) return true;
        int matchCount = 0;
        MachineFluidHandler fluidHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_O)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            matchCount += fluidHandler.getSpaceForOutputs(fluids);
        }
        return matchCount >= fluids.length;
    }

    @Override
    public long getMaxInputVoltage() {
        List<IComponentHandler> hatches = getComponents(Machines.HATCH_ENERGY);
        return hatches.size() >= 1 ? hatches.get(0).getEnergyHandler().getMaxInsert() : Ref.V[0];
    }

    /** Returns a list of Components **/
    public List<IComponentHandler> getComponents(IGregTechObject object) {
        return getComponents(object.getId());
    }

    public List<IComponentHandler> getComponents(String id) {
        ArrayList<IComponentHandler> list = components.get(id);
        return list != null ? list : Collections.emptyList();
    }

    /** Clear the cached component map **/
    public void clearComponents() {
        components.forEach((k, v) -> v.forEach(c -> c.unlinkController(this)));
        components.clear();
    }

    @Override
    public ControllerComponentHandler getComponentHandler() {
        return componentHandler;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        return capability == GTCapabilities.COMPONENT || super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        return capability == GTCapabilities.COMPONENT ? GTCapabilities.COMPONENT.cast(componentHandler) : super.getCapability(capability, side);
    }
}
