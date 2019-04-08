package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.capability.impl.ControllerComponentHandler;
import muramasa.gtu.api.capability.impl.ControllerConfigHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.interfaces.IComponent;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.structure.Structure;
import muramasa.gtu.api.structure.StructureResult;
import muramasa.gtu.api.tileentities.TileEntityBasicMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileEntityMultiMachine extends TileEntityBasicMachine implements IComponent {

    //TODO set protected
    public boolean validStructure;
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

    @Override
    public Recipe findRecipe() {
        return RecipeMap.findRecipeItem(getType().getRecipeMap(), getStoredInputs());
    }

    /** Consumes inputs from all input hatches. Assumes doStacksMatchAndSizeValid has been used **/
    @Override
    public void consumeInputs() {
        ItemStack[] toConsume = activeRecipe.getInputStacks().clone();
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_INPUT)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            toConsume = itemHandler.consumeAndReturnInputs(toConsume);
            if (toConsume.length == 0) break;
        }
    }

    /** Tests if outputs can fit across all output hatches **/
    @Override
    public boolean canOutput() {
        ItemStack[] toOutput = activeRecipe.getOutputStacks().clone();
        MachineItemHandler itemHandler;
        int matchCount = 0;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_OUTPUT)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            matchCount += itemHandler.getSpaceForStacks(toOutput);
        }
        return matchCount >= toOutput.length;
    }

    /** Export stacks to hatches regardless of space. Assumes canOutputsFit has been used **/
    @Override
    public void addOutputs() {
        ItemStack[] toOutput = activeRecipe.getOutputStacks().clone();
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_OUTPUT)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            for (int i = 0; i < toOutput.length; i++) {
                System.out.println("Adding output...");
                itemHandler.addOutputs(toOutput[i]);
            }
        }
    }

    @Override
    public boolean canRecipeContinue() {
        return Utils.doStacksMatchAndSizeValid(activeRecipe.getInputStacks(), getStoredInputs());
    }

    @Override
    public boolean consumeResourceForRecipe() {
        return true; //TODO
    }

    public boolean checkStructure() {
        clearComponents();
        Structure structure = getType().getStructure();
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
            System.out.println(getStoredInputs());
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

    public void onHatchContentsChanged() {
        System.out.println("Hatch Content Update");
        checkRecipe();
    }

    public void clearComponents() {
        for (Map.Entry<String, ArrayList<IComponentHandler>> entry : components.entrySet()) {
            for (IComponentHandler component : entry.getValue()) {
                component.unlinkController(this);
            }
        }
        components.clear();
    }

    /** Returns list of stacks across all input hatches. Merges equal stacks and filters empty **/
    public ItemStack[] getStoredInputs() {
        ArrayList<ItemStack> all = new ArrayList<>();
        ArrayList<IComponentHandler> hatches = getComponents(Machines.HATCH_ITEM_INPUT);
        if (hatches == null || hatches.size() == 0) return all.toArray(new ItemStack[0]);
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : hatches) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            if (all.isEmpty()) {
                all.addAll(itemHandler.getInputList());
            } else {
                Utils.mergeItems(all, itemHandler.getInputList());
            }
        }
        return all.toArray(new ItemStack[0]);
    }

    /** Returns a list of Components **/
    public ArrayList<IComponentHandler> getComponents(IStringSerializable serializable) {
        return components.get(serializable.getName());
    }

    @Override
    public ControllerComponentHandler getComponentHandler() {
        return componentHandler;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COMPONENT) {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COMPONENT) {
            return GTCapabilities.COMPONENT.cast(componentHandler);
        }
        return super.getCapability(capability, side);
    }
}
