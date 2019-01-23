package muramasa.itech.api.capability.implementations;

import muramasa.itech.api.machines.Machine;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Arrays;

public class MachineStackHandler extends ItemStackHandler {

    private TileEntityMachine tile;
    private int inputCount, outputCount;

    public MachineStackHandler(TileEntityMachine tile, Machine type) {
        this.tile = tile;
        if (type != null) {
            setSize(type.getSlots().length);
            inputCount = type.getInputCount();
            outputCount = type.getOutputCount();
        } else {
            setSize(1);
        }
    }

    public boolean isInputEmpty() { //TODO borken
        return Arrays.asList(getInputStacks()).contains(ItemStack.EMPTY);
    }

    public ItemStack[] getInputStacks() {
        return Arrays.copyOfRange(stacks.toArray(new ItemStack[0]), 0, inputCount);
    }

    public ItemStack[] getOutputStacks() {
        return Arrays.copyOfRange(stacks.toArray(new ItemStack[0]), inputCount, stacks.size());
    }

    public void consumeInputs(ItemStack[] inputs) {
        for (int i = 0; i < inputCount; i++) {
            stacks.get(i).shrink(inputs[i].getCount());
        }
    }

    public void addOutputs(ItemStack[] outputs) {
        for (int i = 0; i < outputs.length; i++) {
            insertItem(inputCount + i, outputs[i].copy(), false);
        }
    }

    public void onInputChanged(int slot) {
//        onContentsChanged(slot);
        tile.onContentsChanged(slot);
        tile.markDirty();
    }

    @Override
    protected void onContentsChanged(int slot) {
        tile.onContentsChanged(slot);
        tile.markDirty();
    }
}
