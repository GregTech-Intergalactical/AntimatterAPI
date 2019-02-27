package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.gui.SlotType;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class MachineItemHandler {

    private GTItemHandler inputHandler, outputHandler, cellHandler;

    /** Constructor **/
    public MachineItemHandler(TileEntityMachine tile) {
        inputHandler = new GTItemHandler(tile.getType().getGui().getCount(SlotType.IT_IN)) {
            @Override
            protected void onContentsChanged(int slot) {
                tile.onContentsChanged(0, slot);
            }
        };
        outputHandler = new GTItemHandler(tile.getType().getGui().getCount(SlotType.IT_OUT)) {
            @Override
            protected void onContentsChanged(int slot) {
                //TODO maybe differentiate a input/output slot update?
                tile.onContentsChanged(0, slot);
            }
        };
        if (tile.getType().hasFlag(MachineFlag.FLUID)) {
            cellHandler = new GTItemHandler(tile.getType().getGui().getCount(SlotType.CELL_IN) + tile.getType().getGui().getCount(SlotType.CELL_OUT)) {
                @Override
                protected void onContentsChanged(int slot) {
                    tile.onContentsChanged(2, slot);
                }
            };
        }
    }

    public MachineItemHandler(TileEntityMachine tile, NBTTagCompound itemData) {
        this(tile);
        if (itemData != null) {
            deserialize(itemData);
        }
    }

    public int getInputCount() {
        return inputHandler.stacks.length;
    }

    public int getOutputCount() {
        return outputHandler.stacks.length;
    }

    public int getCellCount() {
        return cellHandler.stacks.length;
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

    public List<ItemStack> getInputList() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < inputHandler.stacks.length; i++) {
            if (!inputHandler.stacks[i].isEmpty()) {
                list.add(inputHandler.stacks[i].copy());
            }
        }
        return list;
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
    public NBTTagCompound serialize() {
        NBTTagCompound nbt = new NBTTagCompound();
        if (inputHandler != null) {
            NBTTagList inputTagList = new NBTTagList();
            for (int i = 0; i < inputHandler.stacks.length; i++) {
                if (!inputHandler.stacks[i].isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setInteger("Slot", i);
                    inputHandler.stacks[i].writeToNBT(itemTag);
                    inputTagList.appendTag(itemTag);
                }
            }
            nbt.setTag("Input-Items", inputTagList);
            nbt.setInteger("Input-Size", inputHandler.stacks.length);
        }

        if (outputHandler != null) {
            NBTTagList outputTagList = new NBTTagList();
            for (int i = 0; i < outputHandler.stacks.length; i++) {
                if (!outputHandler.stacks[i].isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setInteger("Slot", i);
                    outputHandler.stacks[i].writeToNBT(itemTag);
                    outputTagList.appendTag(itemTag);
                }
            }
            nbt.setTag("Output-Items", outputTagList);
            nbt.setInteger("Output-Size", outputHandler.stacks.length);
        }

        if (cellHandler != null) {
            NBTTagList cellTagList = new NBTTagList();
            for (int i = 0; i < cellHandler.stacks.length; i++) {
                if (!cellHandler.stacks[i].isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setInteger("Slot", i);
                    cellHandler.stacks[i].writeToNBT(itemTag);
                    cellTagList.appendTag(itemTag);
                }
            }
            nbt.setTag("Cell-Items", cellTagList);
            nbt.setInteger("Cell-Size", cellHandler.stacks.length);
        }
        return nbt;
    }

    public void deserialize(NBTTagCompound nbt) {
        if (inputHandler != null) {
            inputHandler.setSize(nbt.hasKey("Input-Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Input-Size") : inputHandler.stacks.length);
            NBTTagList inputTagList = nbt.getTagList("Input-Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < inputTagList.tagCount(); i++) {
                NBTTagCompound itemTags = inputTagList.getCompoundTagAt(i);
                int slot = itemTags.getInteger("Slot");

                if (slot >= 0 && slot < inputHandler.stacks.length) {
                    inputHandler.stacks[slot] = new ItemStack(itemTags);
                }
            }
            inputHandler.onLoad();
        }

        if (outputHandler != null) {
            outputHandler.setSize(nbt.hasKey("Output-Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Output-Size") : outputHandler.stacks.length);
            NBTTagList outputTagList = nbt.getTagList("Output-Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < outputTagList.tagCount(); i++) {
                NBTTagCompound itemTags = outputTagList.getCompoundTagAt(i);
                int slot = itemTags.getInteger("Slot");

                if (slot >= 0 && slot < outputHandler.stacks.length) {
                    outputHandler.stacks[slot] = new ItemStack(itemTags);
                }
            }
            outputHandler.onLoad();
        }

        if (cellHandler != null) {
           cellHandler.setSize(nbt.hasKey("Cell-Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Cell-Size") : cellHandler.stacks.length);
           NBTTagList cellTagList = nbt.getTagList("Cell-Items", Constants.NBT.TAG_COMPOUND);
           for (int i = 0; i < cellTagList.tagCount(); i++) {
               NBTTagCompound itemTags = cellTagList.getCompoundTagAt(i);
               int slot = itemTags.getInteger("Slot");

               if (slot >= 0 && slot < cellHandler.stacks.length) {
                   cellHandler.stacks[slot] = new ItemStack(itemTags);
               }
           }
           cellHandler.onLoad();
        }
    }
}
