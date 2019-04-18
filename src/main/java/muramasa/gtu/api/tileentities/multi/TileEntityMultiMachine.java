package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.capability.impl.ControllerComponentHandler;
import muramasa.gtu.api.capability.impl.ControllerConfigHandler;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.interfaces.IComponent;
import muramasa.gtu.api.interfaces.IGregTechObject;
import muramasa.gtu.api.structure.Structure;
import muramasa.gtu.api.structure.StructureResult;
import muramasa.gtu.api.tileentities.TileEntityBasicMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class TileEntityMultiMachine extends TileEntityBasicMachine implements IComponent {

    //TODO set protected
    public boolean validStructure;
    //TODO move to BasicMachine
    protected int curEfficiency, maxEfficiency;
    protected HashMap<String, ArrayList<IComponentHandler>> components;
    protected ControllerComponentHandler componentHandler;

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        components = new HashMap<>();
        componentHandler = new ControllerComponentHandler(getType(), this);
        configHandler = new ControllerConfigHandler(this);
    }

    @Override
    public void onServerUpdate() {
        super.onServerUpdate();
    }

    //TODO break recipe on invalid structure
//    @Override
//    public void onRecipeTick() {
//        if (!validStructure) activeRecipe
//    }

    public boolean checkStructure() {
        clearComponents();
        Structure structure = getType().getStructure(getTier());
        if (structure == null) return false;
        StructureResult result = structure.evaluate(this);
        if (result.evaluate()) {
            components = result.getComponents();
            for (Map.Entry<String, ArrayList<IComponentHandler>> entry : components.entrySet()) {
                for (IComponentHandler component : entry.getValue()) {
                    component.linkController(this);
                }
            }
            System.out.println("[Structure Debug] Valid Structure");
            System.out.println(getStoredItems());
            onStructureIntegrity(true);
            return (validStructure = true);
        }
        System.out.println(result.getError());
        clearComponents();
        return (validStructure = false);
    }

    /** Events **/
    public void onComponentRemoved() {
        clearComponents();
        validStructure = false;
        System.out.println("INVALIDATED STRUCTURE");
        onStructureIntegrity(false);
    }

    public void onStructureIntegrity(boolean valid) {
        //NOOP
    }

    /** Returns list of items across all input hatches. Merges equal filters empty **/
    public ItemStack[] getStoredItems() {
        ArrayList<ItemStack> all = new ArrayList<>();
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_INPUT)) {
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
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_INPUT)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            Utils.mergeFluids(all, fluidHandler.getInputList());
        }
        System.out.println(all.toString());
        return all.toArray(new FluidStack[0]);
    }

    /** Tests if items can fit across all output hatches **/
    public boolean canItemsFit(ItemStack[] items) {
        if (items == null) return true;
        int matchCount = 0;
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_OUTPUT)) {
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
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_OUTPUT)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            matchCount += fluidHandler.getSpaceForOutputs(fluids);
        }
        return matchCount >= fluids.length;
    }

    /** Consumes inputs from all input hatches. Assumes Utils.doItemsMatchAndSizeValid has been used **/
    public void consumeItems(ItemStack[] items) {
        if (items == null) return;
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_INPUT)) {
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
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_INPUT)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            fluids = fluidHandler.consumeAndReturnInputs(fluids);
            if (fluids.length == 0) break;
        }
        if (fluids.length > 0) System.out.println("DID NOT CONSUME ALL: " + fluids.toString());
    }

    /** Export items to hatches regardless of space. Assumes canOutputsFit has been used **/
    public void outputItems(ItemStack[] items) {
        if (items == null) return;
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_OUTPUT)) {
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
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_OUTPUT)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            fluids = fluidHandler.exportAndReturnOutputs(fluids.clone());
            if (fluids.length == 0) break;
        }
        if (fluids.length > 0) System.out.println("HATCH OVERFLOW: " + fluids.toString());
    }

    /** Returns a list of Components **/
    public List<IComponentHandler> getComponents(IGregTechObject object) {
        ArrayList<IComponentHandler> list = components.get(object.getName());
        return list != null ? list : Collections.emptyList();
    }

    /** Clear the cached component map **/
    public void clearComponents() {
        for (Map.Entry<String, ArrayList<IComponentHandler>> entry : components.entrySet()) {
            for (IComponentHandler component : entry.getValue()) {
                component.unlinkController(this);
            }
        }
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
