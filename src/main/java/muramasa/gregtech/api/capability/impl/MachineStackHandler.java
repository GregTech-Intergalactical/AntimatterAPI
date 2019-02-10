package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

public class MachineStackHandler {

    private GTItemHandler inputHandler, outputHandler;

    /** Constructor **/
    public MachineStackHandler(TileEntityMachine tile, int type) {
        Machine machine = tile.getMachineType();
        if (machine != null) {
            inputHandler = new GTItemHandler(machine.getInputCount()) {
                @Override
                protected void onContentsChanged(int slot) {
                    tile.onContentsChanged(type, slot);
                }
            };
            outputHandler = new GTItemHandler(machine.getOutputCount()) {
                @Override
                protected void onContentsChanged(int slot) {
                    tile.onContentsChanged(type, slot);
                }
            };
        }
    }

    public ItemStack[] getInputs() {
        return inputHandler.stacks;
    }

    public ItemStack[] getOutputs() {
        return outputHandler.stacks;
    }

    public void consumeInputs(ItemStack... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputHandler.stacks.length; j++) {
                if (Utils.equals(inputs[i], inputHandler.stacks[j])) {
                    inputHandler.stacks[j].shrink(inputs[i].getCount());
                    break;
                }
            }
        }
    }

    public void addOutputs(ItemStack... outputs) {
        for (int i = 0; i < outputs.length; i++) {
            outputHandler.insertItem(i, outputs[i].copy(), false);
        }
    }

    /** Helpers **/
    public boolean canStacksFit(ItemStack[] a, ItemStack[] b) {
        return getSpaceForStacks(a, b) >= a.length;
    }

    public int getSpaceForStacks(ItemStack[] a, ItemStack[] b) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (b[j].isEmpty() || (Utils.equals(a[i], b[j]) && b[j].getCount() + a[i].getCount() <= b[j].getMaxStackSize())) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount;
    }

    /** Handler Access **/
    public IItemHandler getInputHandler() {
        return inputHandler;
    }

    public IItemHandler getOutputHandler() {
        return outputHandler;
    }

    /** NBT **/
    public NBTTagCompound serializeInput() {
        return inputHandler.serializeNBT();
    }

    public NBTTagCompound serializeOutput() {
        return outputHandler.serializeNBT();
    }

    public void deserializeInput(NBTTagCompound nbt) {
        inputHandler.deserializeNBT(nbt);
    }

    public void deserializeOutput(NBTTagCompound nbt) {
        outputHandler.deserializeNBT(nbt);
    }
}
