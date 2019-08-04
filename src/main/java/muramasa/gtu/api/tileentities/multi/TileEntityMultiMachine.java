package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.capability.impl.*;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.gui.GuiEvent;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.structure.IComponent;
import muramasa.gtu.api.structure.Structure;
import muramasa.gtu.api.structure.StructureCache;
import muramasa.gtu.api.structure.StructureResult;
import muramasa.gtu.api.tileentities.TileEntityRecipeMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TileEntityMultiMachine extends TileEntityRecipeMachine implements IComponent {

    protected int efficiency, efficiencyIncrease; //TODO move to BasicMachine
    protected long EUt;
    protected Optional<ControllerComponentHandler> componentHandler = Optional.empty();

    protected Optional<StructureResult> result = Optional.empty();

    @Override
    public void onLoad() {
        super.onLoad();
        componentHandler = Optional.of(new ControllerComponentHandler(getType(), this));
        configHandler = Optional.of(new ControllerConfigHandler(this));
    }

    public boolean checkStructure() {
        if (!isServerSide()) return false;
        Structure structure = getType().getStructure(getTier());
        if (structure == null) return false;
        StructureResult result = structure.evaluate(this);
        if (result.evaluate()) {
            this.result = Optional.of(result);
            if (onStructureFormed()) {
                StructureCache.add(world, pos, result.positions);
                this.result.ifPresent(r -> r.components.forEach((k, v) -> v.forEach(c -> c.onStructureFormed(this))));
                setMachineState(MachineState.IDLE);
                System.out.println("[Structure Debug] Valid Structure");
                return true;
            }
        }
        this.result = Optional.empty();
        System.out.println("[Structure Debug] Invalid Structure" + result.getError());
        return false;
    }

    public void invalidateStructure() {
        result.ifPresent(r -> r.components.forEach((k, v) -> v.forEach(c -> c.onStructureInvalidated(this))));
        result = Optional.empty();
        resetMachine();
        System.out.println("INVALIDATED STRUCTURE");
        onStructureInvalidated();
    }

    /** Returns a list of Components **/
    public List<IComponentHandler> getComponents(IGregTechObject object) {
        return getComponents(object.getId());
    }

    public List<IComponentHandler> getComponents(String id) {
        if (result.isPresent()) {
            ArrayList<IComponentHandler> list = result.get().components.get(id);
            return list != null ? list : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public List<IBlockState> getStates(String id) {
        if (result.isPresent()) {
            ArrayList<IBlockState> list = result.get().states.get(id);
            return list != null ? list : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public boolean isStructureValid() {
        return StructureCache.has(world, pos);
    }

    /** Events **/
    public boolean onStructureFormed() {
        return true;
    }

    public void onStructureInvalidated() {
        //NOOP
    }

    @Override
    public void onGuiEvent(GuiEvent event) {
        if (event == GuiEvent.MULTI_ACTIVATE) checkStructure();
    }

    @Override
    public Recipe findRecipe() {
        return getType().getRecipeMap().find(getStoredItems(), getStoredFluids());
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
        if (!hasFlag(MachineFlag.ITEM)) return new ItemStack[0];
        ArrayList<ItemStack> all = new ArrayList<>();
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_I)) {
            hatch.getItemHandler().ifPresent(h -> Utils.mergeItems(all, h.getInputList()));
        }
        System.out.println(all.toString());
        return all.toArray(new ItemStack[0]);
    }

    /** Returns list of fluids across all input hatches. Merges equal filters empty **/
    public FluidStack[] getStoredFluids() {
        if (!hasFlag(MachineFlag.FLUID)) return new FluidStack[0];
        ArrayList<FluidStack> all = new ArrayList<>();
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_I)) {
            hatch.getFluidHandler().ifPresent(h -> Utils.mergeFluids(all, h.getInputList()));
        }
        System.out.println(all.toString());
        return all.toArray(new FluidStack[0]);
    }

    /** Returns the total energy stored across all energy hatches **/
    public long getStoredEnergy() {
        long total = 0;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ENERGY)) {
            if (hatch.getEnergyHandler().isPresent()) total += hatch.getEnergyHandler().get().getEnergyStored();
        }
        return total;
    }

    /** Consumes inputs from all input hatches. Assumes Utils.doItemsMatchAndSizeValid has been used **/
    public void consumeItems(ItemStack[] items) {
        if (items == null) return;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_I)) {
            if (hatch.getItemHandler().isPresent()) {
                items = hatch.getItemHandler().get().consumeAndReturnInputs(items.clone());
                if (items.length == 0) break;
            }
        }
        if (items.length > 0) System.out.println("DID NOT CONSUME ALL: " + items.toString());
    }

    /** Consumes inputs from all input hatches. Assumes Utils.doFluidsMatchAndSizeValid has been used **/
    public void consumeFluids(FluidStack[] fluids) {
        if (fluids == null) return;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_I)) {
            if (hatch.getFluidHandler().isPresent()) {
                fluids = hatch.getFluidHandler().get().consumeAndReturnInputs(fluids);
                if (fluids.length == 0) break;
            }
        }
        if (fluids.length > 0) System.out.println("DID NOT CONSUME ALL: " + fluids.toString());
    }

    /** Consumes energy from all energy hatches. Assumes enough energy is present in hatches **/
    public void consumeEnergy(long energy) {
        if (energy <= 0) return;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ENERGY)) {
            if (hatch.getEnergyHandler().isPresent()) {
                energy -= hatch.getEnergyHandler().get().extract(energy, false);
                if (energy == 0) break;
            }
        }
    }

    /** Export items to hatches regardless of space. Assumes canOutputsFit has been used **/
    public void outputItems(ItemStack[] items) {
        if (items == null) return;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_O)) {
            if (hatch.getItemHandler().isPresent()) {
                items = hatch.getItemHandler().get().exportAndReturnOutputs(items.clone()); //WHY CLONE?!!?
                if (items.length == 0) break;
            }
        }
        if (items.length > 0) System.out.println("HATCH OVERFLOW: " + items.toString());
    }

    /** Export fluids to hatches regardless of space. Assumes canOutputsFit has been used **/
    public void outputFluids(FluidStack[] fluids) {
        if (fluids == null) return;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_O)) {
            if (hatch.getFluidHandler().isPresent()) {
                fluids = hatch.getFluidHandler().get().exportAndReturnOutputs(fluids.clone());
                if (fluids.length == 0) break;
            }
        }
        if (fluids.length > 0) System.out.println("HATCH OVERFLOW: " + fluids.toString());
    }

    /** Tests if items can fit across all output hatches **/
    public boolean canItemsFit(ItemStack[] items) {
        if (items == null) return true;
        int matchCount = 0;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_O)) {
            if (hatch.getItemHandler().isPresent()) {
                matchCount += hatch.getItemHandler().get().getSpaceForOutputs(items);
            }
        }
        return matchCount >= items.length;
    }

    /** Tests if fluids can fit across all output hatches **/
    public boolean canFluidsFit(FluidStack[] fluids) {
        if (fluids == null) return true;
        int matchCount = 0;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_O)) {
            if (hatch.getFluidHandler().isPresent()) {
                matchCount += hatch.getFluidHandler().get().getSpaceForOutputs(fluids);
            }
        }
        return matchCount >= fluids.length;
    }

    @Override
    public long getMaxInputVoltage() {
        List<IComponentHandler> hatches = getComponents(Machines.HATCH_ENERGY);
        return hatches.size() >= 1 ? hatches.get(0).getEnergyHandler().isPresent() ? hatches.get(0).getEnergyHandler().get().getMaxInsert() : Ref.V[0] : Ref.V[0];
    }

    @Override
    public Optional<ControllerComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public MachineState getDefaultMachineState() {
        return MachineState.INVALID_STRUCTURE;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        return capability == GTCapabilities.COMPONENT || super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        return capability == GTCapabilities.COMPONENT && componentHandler.isPresent() ? GTCapabilities.COMPONENT.cast(componentHandler.get()) : super.getCapability(capability, side);
    }
}
