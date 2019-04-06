package muramasa.gtu.common.tileentities.multi;

import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.util.int3;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.init.Blocks;

public class TileEntityPrimitiveBlastFurnace extends TileEntityMultiMachine {

    @Override
    public Recipe findRecipe() {
        return getType().findRecipe(itemHandler, fluidHandler);
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
    public void onStructureIntegrity(boolean valid) {
        if (valid) {
            int3 controller = new int3(getPos(), getEnumFacing());
            controller.back(1);
            getWorld().setBlockState(controller.asBP(), Blocks.LAVA.getDefaultState(), 3);
            controller.up(1);
            getWorld().setBlockState(controller.asBP(), Blocks.LAVA.getDefaultState(), 3);
        }
    }
}
