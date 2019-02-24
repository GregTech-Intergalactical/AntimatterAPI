package muramasa.gregtech.common.tileentities.overrides.multi;

import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.api.util.int3;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.init.Blocks;

public class TileEntityPrimitiveBlastFurnace extends TileEntityMultiMachine {

    @Override
    public Recipe findRecipe() {
        return getMachineType().findRecipe(itemHandler, fluidHandler);
    }

    public void consumeInputs() {
        itemHandler.consumeInputs(activeRecipe.getInputStacks());
    }

    @Override
    public boolean canOutput() {
        return itemHandler.canStacksFit(activeRecipe.getOutputStacks());
    }

    @Override
    public void addOutputs() {
        itemHandler.addOutputs(activeRecipe.getOutputStacks());
    }

    @Override
    public boolean canRecipeContinue() {
        return Utils.doStacksMatchAndSizeValid(activeRecipe.getInputStacks(), itemHandler.getInputs());
    }

    @Override
    public boolean consumeResourceForRecipe() {
        return true;
    }

    @Override
    public void onValidStructure() {
        int3 controller = new int3(getPos(), getEnumFacing());
        controller.back(1);
        getWorld().setBlockState(controller.asBlockPos(), Blocks.LAVA.getDefaultState(), 3);
        controller.up(1);
        getWorld().setBlockState(controller.asBlockPos(), Blocks.LAVA.getDefaultState(), 3);
    }
}
