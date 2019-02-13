package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.IComponent;
import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.capability.impl.ControllerComponentHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.structure.StructurePattern;
import muramasa.gregtech.api.structure.StructureResult;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TileEntityMultiMachine extends TileEntityMachine {

    public boolean shouldCheckRecipe, shouldCheckStructure, validStructure;
    public int curProgress, maxProgress;
    private Recipe activeRecipe;

    private HashMap<String, ArrayList<IComponent>> components;

    private ControllerComponentHandler componentHandler;

    @Override
    public void init(String type, String tier, int facing) {
        super.init(type, tier, facing);
        components = new HashMap<>();
        componentHandler = new ControllerComponentHandler(type, this);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        shouldCheckStructure = true;
        shouldCheckRecipe = true;
    }

    @Override
    public void onServerUpdate() {
        if (shouldCheckStructure) {
            clearComponents();
            if (checkStructure()) {
                validStructure = true;
            } else {
                validStructure = false;
                clearComponents();
            }
            shouldCheckStructure = false;
        }
        if (shouldCheckRecipe) {
            checkRecipe();
            shouldCheckRecipe = false;
        }
        advanceRecipe();
    }

    public void checkRecipe() {
        if (activeRecipe == null) { //No active recipes, see of contents match one
            ArrayList<ItemStack> hatchStacks = getHatchItems();
            if (hatchStacks.size() == 0) return; //Escape if machine inputs are empty
//            Recipe recipe = getMachineType().findRecipe(hatchStacks.toArray(new ItemStack[0]));
            Recipe recipe = null;
            if (recipe != null) {
                activeRecipe = recipe;
                curProgress = 0;
                maxProgress = recipe.getDuration();
            }
        }
    }

    public void advanceRecipe() {
        if (activeRecipe != null) { //Found a valid recipe, process it
            if (curProgress == maxProgress) { //End of current recipe cycle, deposit items
                if (canOutputsFit(activeRecipe.getOutputStacks())) {
                    //Add outputs and reset to process next recipe cycle
                    addOutputs(activeRecipe.getOutputStacks());
                    curProgress = 0;
                } else {
                    return; //Return and loop until outputs can be added
                }

                //Check if has enough stack count for next recipe cycle
                if (!Utils.doStacksMatchAndSizeValid(activeRecipe.getInputStacks(), getHatchItems().toArray(new ItemStack[0]))) {
                    activeRecipe = null;
                }
            } else {
                //Calculate per recipe tick so user has risk of losing items
                if (hasResourceForRecipe()) { //Has enough power to process recipe
                    consumeResourceForRecipe();
                    if (curProgress == 0) { //Consume recipe inputs on first recipe tick
                        consumeInputs(activeRecipe.getInputStacks());
                    }
                    curProgress++;
                } else {
                    //TODO machine out of power/steam
                    //TODO maybe not null recipe, but keep cache for user using hammer to restart?
                    activeRecipe = null;
                }
            }
        }
    }

    public boolean hasResourceForRecipe() { //Return if Machine can process 1 recipe tick
//        return energyStorage != null && activeRecipe != null && energyStorage.energy >= activeRecipe.getPower();
        return true;
    }

    public void consumeResourceForRecipe() {
//        energyStorage.energy -= Math.max(energyStorage.energy -= activeRecipe.getPower(), 0);
    }

    private boolean checkStructure() {
        StructurePattern pattern = getMachineType().getPattern();
        StructureResult result = pattern.evaluate(this);
        if (result.evaluate()) {
            components = result.getComponents();
            for (Map.Entry<String, ArrayList<IComponent>> entry : components.entrySet()) {
                for (IComponent component : entry.getValue()) {
                    component.linkController(this);
                }
            }
            System.out.println("[Structure Debug] Valid Structure");
            System.out.println(getHatchItems());
            return true;
        }
        System.out.println(result.getError());
        return false;
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

    //TODO merge equal stacks
    public ArrayList<ItemStack> getHatchItems() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        ArrayList<IComponent> hatches = components.get("itemhatchinput");
        if (hatches == null || hatches.size() == 0) return stacks;
        MachineItemHandler itemHandler;
        for (IComponent hatch : hatches) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            for (ItemStack stack : itemHandler.getInputs()) {
                if (stacks.contains(stack)) {

                } else {
                    stacks.add(stack);
                }
            }
            stacks.addAll(itemHandler.getInputList());
        }
        return stacks;
    }

    public void consumeInputs(ItemStack... inputs) {
        ItemStack[] toConsume = inputs.clone();
        MachineItemHandler itemHandler;
        for (IComponent hatch : components.get("itemhatchinput")) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            toConsume = itemHandler.consumeAndReturnInputs(toConsume);
            if (toConsume.length == 0) break;
        }
    }

    public boolean canOutputsFit(ItemStack... outputs) {
        MachineItemHandler itemHandler;
        int matchCount = 0;
        for (IComponent hatch : components.get("itemhatchoutput")) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            matchCount += itemHandler.getSpaceForStacks(outputs);
        }
        return matchCount >= outputs.length;
    }


    public void addOutputs(ItemStack... outputs) {
        MachineItemHandler itemHandler;
        for (IComponent hatch : components.get("itemhatchoutput")) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            for (int i = 0; i < outputs.length; i++) {



//                if (Utils.getSpaceForStacks())
//                if (Utils.canStacksFit(new ItemStack[]{outputs[i]}, itemHandler.getOutputStacks())) {
                    System.out.println("addOutput");
                    itemHandler.addOutputs(outputs[i].copy());
//                }
            }

        }
    }

//    public boolean consume(ItemStack... stacks) {
////        IItemHandler itemHandler;
////        for (IComponent hatch : components.get("itemhatch")) {
////            itemHandler = hatch.getTile().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
////            if (itemHandler == null) continue;
////            for (ItemStack stack : stacks) {
////                if (itemHandler.)
////            }
////        }
//
//        MachineStackHandler itemHandler;
//        for (IComponent hatch : components.get("itemhatch")) {
//            itemHandler = hatch.getItemHandler();
//            if (itemHandler == null) continue;
//            for (ItemStack stack : stacks) {
//
//            }
//        }
//    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ITechCapabilities.COMPONENT) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ITechCapabilities.COMPONENT) {
            return ITechCapabilities.COMPONENT.cast(componentHandler);
        }
        return super.getCapability(capability, facing);
    }
}
