package muramasa.gregtech.api.tileentities;

import muramasa.gregtech.api.data.ItemType;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.item.ItemStack;

public class TileEntityItemFluidMachine extends TileEntityItemMachine {

    @Override
    public void consumeInputs() {
        if (activeRecipe.hasInputFluids()) {
            super.consumeInputs();
            fluidHandler.consumeInputs(activeRecipe.getInputFluids());
        }
        super.consumeInputs();
    }

    @Override
    public boolean canOutput() {
        if (activeRecipe.hasOutputFluids()) {
            return super.canOutput() && fluidHandler.canFluidsFit(activeRecipe.getOutputFluids());
        }
        return super.canOutput();
    }

    @Override
    public void addOutputs() {
        if (activeRecipe.hasOutputFluids()) {
            fluidHandler.addOutputs(activeRecipe.getOutputFluids());
        }
        super.addOutputs();
    }

    @Override
    public boolean canRecipeContinue() {
        if (activeRecipe.hasInputFluids()) {
            return super.canRecipeContinue() && Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), fluidHandler.getInputs());
        }
        return super.canRecipeContinue();
    }

    @Override
    public void onContentsChanged(int type, int slot) {
        super.onContentsChanged(type, slot);
        if (type == 2) {
            if (slot == 0) { //Input slot
                ItemStack stack = itemHandler.getCellInput();
                if (stack.getItem() instanceof MaterialItem) {
                    MaterialItem item = (MaterialItem) stack.getItem();
                    if (item.getPrefix() == Prefix.Cell) {
                        fluidHandler.addInputs(item.getMaterial().getLiquid(1000));
                    } else if (item.getPrefix() == Prefix.CellGas) {
                        fluidHandler.addInputs(item.getMaterial().getGas(1000));
                    } else if (item.getPrefix() == Prefix.CellPlasma) {
                        fluidHandler.addInputs(item.getMaterial().getPlasma(1000));
                    }
                    itemHandler.getCellInput().setCount(0);
                } else if (ItemType.EmptyCell.isEqual(stack)) {
                    System.out.println("Empty Cell");
                    fluidHandler.getInput(0).setFluid(null);
                }
            } else if (slot == 1) { //Output slot

            }
        }
    }
}
