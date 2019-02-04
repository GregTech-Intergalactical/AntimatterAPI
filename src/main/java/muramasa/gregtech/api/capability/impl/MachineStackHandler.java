package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.machines.Machine;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO need a container?
public class MachineStackHandler extends StackHandler {

    private TileEntityMachine tile;
    public int inputCount, outputCount;

    public MachineStackHandler(TileEntityMachine tile) {
        Machine type = tile.getMachineType();
        if (type != null) {
            setSize(type.getSlotCount());
            this.tile = tile;
            this.inputCount = type.getInputCount();
            this.outputCount = type.getOutputCount();
        } else {
            setSize(1);
        }
    }

    public List<ItemStack> getInputList() {
        ArrayList<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < inputCount; i++) {
            if (!stacks[i].isEmpty()) {
                inputs.add(stacks[i]);
            }
        }
        return inputs;
    }

    public ItemStack[] getInputs() {
        return Arrays.copyOfRange(stacks, 0, inputCount);
    }

    public ItemStack[] getOutputs() {
        return Arrays.copyOfRange(stacks, inputCount, stacks.length);
    }

    public void consumeInputs(ItemStack... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < stacks.length; j++) {
                if (Utils.equals(inputs[i], stacks[j])) {
                    stacks[j].shrink(inputs[i].getCount());
                    break;
                }
            }
        }
    }

    public ItemStack[] consumeAndReturnInputs(ItemStack... inputs) {
        ArrayList<ItemStack> notConsumed = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < stacks.length; j++) {
                if (Utils.equals(inputs[i], stacks[j])) {
                    stacks[j].shrink(inputs[i].getCount());
                } else {
                    notConsumed.add(inputs[i]);
                }
            }
        }
        return notConsumed.toArray(new ItemStack[0]);
    }

    public void addOutputs(ItemStack... outputs) {
        for (int i = 0; i < outputs.length; i++) {
            insertItem(inputCount + i, outputs[i].copy(), false);
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
//        if (slot >= inputCount) return ItemStack.EMPTY;
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
//        if (slot < inputCount) return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(int slot) {
        tile.onContentsChanged(slot);
    }
}
