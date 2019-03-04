package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.IComponent;
import muramasa.gregtech.api.capability.impl.ControllerComponentHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.api.structure.Structure;
import muramasa.gregtech.api.structure.StructureResult;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileEntityMultiMachine extends TileEntityBasicMachine {

    //TODO set protected
    public boolean shouldCheckStructure, validStructure;
    protected int curEfficiency, maxEfficiency;
    protected HashMap<String, ArrayList<IComponent>> components;
    protected ControllerComponentHandler componentHandler;

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        components = new HashMap<>();
        componentHandler = new ControllerComponentHandler(getType(), this);
        shouldCheckStructure = true;
        shouldCheckRecipe = true;
    }

    @Override
    public void onServerUpdate() {
        if (shouldCheckStructure) {
            clearComponents();
            validStructure = checkStructure();
            shouldCheckStructure = false;
        }
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
        for (IComponent hatch : getComponents(Machines.HATCH_ITEM_INPUT)) {
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
        for (IComponent hatch : getComponents(Machines.HATCH_ITEM_OUTPUT)) {
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
        for (IComponent hatch : getComponents(Machines.HATCH_ITEM_OUTPUT)) {
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
        if (energyStorage != null && activeRecipe != null && energyStorage.energy >= activeRecipe.getPower()) {
            energyStorage.energy -= Math.max(energyStorage.energy -= activeRecipe.getPower(), 0);
            return true;
        }
        return false;
    }

    public boolean checkStructure() {
        Structure structure = getType().getStructure();
        if (structure == null) return false;
        StructureResult result = structure.evaluate(this);
        if (result.evaluate()) {
            components = result.getComponents();
            for (Map.Entry<String, ArrayList<IComponent>> entry : components.entrySet()) {
                for (IComponent component : entry.getValue()) {
                    component.linkController(this);
                }
            }
            System.out.println("[Structure Debug] Valid Structure");
            System.out.println(getStoredInputs());
            onValidStructure();
            return true;
        }
        System.out.println(result.getError());
        clearComponents();
        return false;
    }

    /** Events **/
    public void onValidStructure() {
        //NOOP
    }

    public void onComponentRemoved() {
        clearComponents();
        validStructure = false;
        System.out.println("INVALIDATED STRUCTURE");
    }

    public void clearComponents() {
        for (Map.Entry<String, ArrayList<IComponent>> entry : components.entrySet()) {
            for (IComponent component : entry.getValue()) {
                component.unlinkController(this);
            }
        }
        components.clear();
    }

    /** Returns list of stacks across all input hatches. Merges equal stacks and filters empty **/
    public ItemStack[] getStoredInputs() {
        ArrayList<ItemStack> all = new ArrayList<>();
        ArrayList<IComponent> hatches = getComponents(Machines.HATCH_ITEM_INPUT);
        if (hatches == null || hatches.size() == 0) return all.toArray(new ItemStack[0]);
        MachineItemHandler itemHandler;
        for (IComponent hatch : hatches) {
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
    public ArrayList<IComponent> getComponents(IStringSerializable serializable) {
        return components.get(serializable.getName());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == GTCapabilities.COMPONENT) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == GTCapabilities.COMPONENT) {
            return GTCapabilities.COMPONENT.cast(componentHandler);
        }
        return super.getCapability(capability, facing);
    }
}
