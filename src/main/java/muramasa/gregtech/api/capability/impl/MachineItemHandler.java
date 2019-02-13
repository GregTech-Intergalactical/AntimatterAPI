package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MachineItemHandler {

    private GTItemHandler inputHandler, outputHandler, cellHandler;

    /** Constructor **/
    public MachineItemHandler(TileEntityMachine tile, int type) {
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
            if (machine.hasFlag(MachineFlag.FLUID)) {
                cellHandler = new GTItemHandler(2) {
                    @Override
                    protected void onContentsChanged(int slot) {
                        tile.onContentsChanged(2, slot);
                    }
                };
            }
        }
    }

    public List<ItemStack> getInputList() {
        return Arrays.asList(getOutputs());
    }

    public ItemStack[] getInputs() {
        return inputHandler.stacks;
    }

    public ItemStack[] getOutputs() {
        return outputHandler.stacks;
    }

    public ItemStack getCellInput() {
        return cellHandler.getStackInSlot(0);
    }

    public ItemStack getCellOutput() {
        return cellHandler.getStackInSlot(1);
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
    public boolean canStacksFit(ItemStack[] a) {
        return getSpaceForStacks(a) >= a.length;
    }

    public int getSpaceForStacks(ItemStack[] a) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < outputHandler.stacks.length; j++) {
                if (outputHandler.stacks[j].isEmpty() || (Utils.equals(a[i], outputHandler.stacks[j]) && outputHandler.stacks[j].getCount() + a[i].getCount() <= outputHandler.stacks[j].getMaxStackSize())) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount;
    }

    public ItemStack[] consumeAndReturnInputs(ItemStack... inputs) {
        ArrayList<ItemStack> notConsumed = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputHandler.stacks.length; j++) {
                if (Utils.equals(inputs[i], inputHandler.stacks[j])) {
                    inputHandler.stacks[j].shrink(inputs[i].getCount());
                } else {
                    notConsumed.add(inputs[i]);
                }
            }
        }
        return notConsumed.toArray(new ItemStack[0]);
    }

    /** Handler Access **/
    public IItemHandler getInputHandler() {
        return inputHandler;
    }

    public IItemHandler getOutputHandler() {
        return outputHandler;
    }

    public IItemHandler getCellHandler() {
        return cellHandler;
    }

    /** NBT **/
    public NBTTagCompound serializeInput() {
        return inputHandler.serializeNBT();
    }

    public NBTTagCompound serializeOutput() {
        return outputHandler.serializeNBT();
    }

    public NBTTagCompound serializeCell() {
        return cellHandler.serializeNBT();
    }

    public void deserializeInput(NBTTagCompound nbt) {
        inputHandler.deserializeNBT(nbt);
    }

    public void deserializeOutput(NBTTagCompound nbt) {
        outputHandler.deserializeNBT(nbt);
    }

    public void deserializeCell(NBTTagCompound nbt) {
        cellHandler.deserializeNBT(nbt);
    }
}
