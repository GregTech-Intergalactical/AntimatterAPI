package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.gui.SlotType;
import muramasa.gtu.api.machines.ContentUpdateType;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class MachineItemHandler {

    private ItemStackHandler inputHandler, outputHandler, cellHandler;

    /** Constructor **/
    public MachineItemHandler(TileEntityMachine tile) {
        inputHandler = new ItemStackHandler(tile.getType().getGui().getSlots(SlotType.IT_IN, tile.getTier()).size()) {
            @Override
            protected void onContentsChanged(int slot) {
                tile.onContentsChanged(ContentUpdateType.ITEM_INPUT, slot, stacks.get(slot).isEmpty());
            }
        };
        outputHandler = new ItemStackHandler(tile.getType().getGui().getSlots(SlotType.IT_OUT, tile.getTier()).size()) {
            @Override
            protected void onContentsChanged(int slot) {
                tile.onContentsChanged(ContentUpdateType.ITEM_OUTPUT, slot, stacks.get(slot).isEmpty());
            }
        };
        if (tile.getType().hasFlag(MachineFlag.FLUID)) {
            cellHandler = new ItemStackHandler(tile.getType().getGui().getSlots(SlotType.CELL_IN, tile.getTier()).size() + tile.getType().getGui().getSlots(SlotType.CELL_OUT, tile.getTier()).size()) {
                @Override
                protected void onContentsChanged(int slot) {
                    tile.onContentsChanged(ContentUpdateType.ITEM_CELL, slot, stacks.get(slot).isEmpty());
                }
            };
        }
    }

    public MachineItemHandler(TileEntityMachine tile, NBTTagCompound itemData) {
        this(tile);
        if (itemData != null) deserialize(itemData);
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

    public int getInputCount() {
        return inputHandler.getSlots();
    }

    public int getOutputCount() {
        return outputHandler.getSlots();
    }

    public int getCellCount() {
        return cellHandler.getSlots();
    }

    public ItemStack[] getInputs() {
        return getInputList().toArray(new ItemStack[0]);
    }

    public ItemStack[] getOutputs() {
        return getOutputList().toArray(new ItemStack[0]);
    }

    public ItemStack getCellInput() {
        return cellHandler.getStackInSlot(0);
    }

    public ItemStack getCellOutput() {
        return cellHandler.getStackInSlot(1);
    }

    /** Gets a list of non empty input Items **/
    public List<ItemStack> getInputList() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < inputHandler.getSlots(); i++) {
            if (!inputHandler.getStackInSlot(i).isEmpty()) list.add(inputHandler.getStackInSlot(i).copy());
        }
        return list;
    }

    /** Gets a list of non empty output Items **/
    public List<ItemStack> getOutputList() {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < outputHandler.getSlots(); i++) {
            if (!outputHandler.getStackInSlot(i).isEmpty()) list.add(outputHandler.getStackInSlot(i).copy());
        }
        return list;
    }

    public void consumeInputs(ItemStack... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputHandler.getSlots(); j++) {
                if (Utils.equals(inputs[i], inputHandler.getStackInSlot(j)) && !Utils.hasNoConsumeTag(inputs[i])) {
                    inputHandler.getStackInSlot(j).shrink(inputs[i].getCount());
                    break;
                }
            }
        }
    }

    public void addOutputs(ItemStack... outputs) {
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputHandler.getSlots(); j++) {
                ItemStack result = outputHandler.insertItem(j, outputs[i].copy(), false);
                if (result.isEmpty()) break;
            }
        }
//        for (int i = 0; i < outputs.length; i++) {
//            outputHandler.insertItem(i, outputs[i].copy(), false);
//        }
    }

    /** Helpers **/
    public boolean canOutputsFit(ItemStack[] a) {
        return getSpaceForOutputs(a) >= a.length;
    }

    public int getSpaceForOutputs(ItemStack[] a) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < outputHandler.getSlots(); j++) {
                if (outputHandler.getStackInSlot(j).isEmpty() || (Utils.equals(a[i], outputHandler.getStackInSlot(j)) && outputHandler.getStackInSlot(j).getCount() + a[i].getCount() <= outputHandler.getStackInSlot(j).getMaxStackSize())) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount;
    }

    public ItemStack[] consumeAndReturnInputs(ItemStack... inputs) {
        ArrayList<ItemStack> notConsumed = new ArrayList<>();
        ItemStack result;
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputHandler.getSlots(); j++) {
                if (Utils.equals(inputs[i], inputHandler.getStackInSlot(j))) {
                    result = inputHandler.extractItem(j, inputs[i].getCount(), false);
                    if (!result.isEmpty()) {
                        if (result.getCount() == inputs[i].getCount()) break;
                        else notConsumed.add(Utils.ca(inputs[i].getCount() - result.getCount(), inputs[i]));
                    }
                } else if (j == inputHandler.getSlots() - 1) {
                    notConsumed.add(inputs[i]);
                }
            }
        }
        return notConsumed.toArray(new ItemStack[0]);
    }

    public ItemStack[] exportAndReturnOutputs(ItemStack... outputs) {
        ArrayList<ItemStack> notExported = new ArrayList<>();
        ItemStack result;
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputHandler.getSlots(); j++) {
                result = outputHandler.insertItem(j, outputs[i].copy(), false);
                if (result.isEmpty()) break;
                else outputs[i] = result;
                if (j == outputHandler.getSlots() - 1) notExported.add(result);
            }
        }
        return notExported.toArray(new ItemStack[0]);
    }

    /** NBT **/
    public NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        if (inputHandler != null) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < inputHandler.getSlots(); i++) {
                if (!inputHandler.getStackInSlot(i).isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setInteger("Slot", i);
                    inputHandler.getStackInSlot(i).writeToNBT(itemTag);
                    list.appendTag(itemTag);
                }
            }
            tag.setTag("Input-Items", list);
            tag.setInteger("Input-Size", inputHandler.getSlots());
        }

        if (outputHandler != null) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                if (!outputHandler.getStackInSlot(i).isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setInteger("Slot", i);
                    outputHandler.getStackInSlot(i).writeToNBT(itemTag);
                    list.appendTag(itemTag);
                }
            }
            tag.setTag("Output-Items", list);
            tag.setInteger("Output-Size", outputHandler.getSlots());
        }

        if (cellHandler != null) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < cellHandler.getSlots(); i++) {
                if (!cellHandler.getStackInSlot(i).isEmpty()) {
                    NBTTagCompound itemTag = new NBTTagCompound();
                    itemTag.setInteger("Slot", i);
                    cellHandler.getStackInSlot(i).writeToNBT(itemTag);
                    list.appendTag(itemTag);
                }
            }
            tag.setTag("Cell-Items", list);
            tag.setInteger("Cell-Size", cellHandler.getSlots());
        }
        return tag;
    }

    public void deserialize(NBTTagCompound tag) {
        if (inputHandler != null) {
            inputHandler.setSize(tag.hasKey("Input-Size", Constants.NBT.TAG_INT) ? tag.getInteger("Input-Size") : inputHandler.getSlots());
            NBTTagList inputTagList = tag.getTagList("Input-Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < inputTagList.tagCount(); i++) {
                NBTTagCompound itemTags = inputTagList.getCompoundTagAt(i);
                int slot = itemTags.getInteger("Slot");

                if (slot >= 0 && slot < inputHandler.getSlots()) {
                    inputHandler.setStackInSlot(slot, new ItemStack(itemTags));
                }
            }
//            inputHandler.onLoad();
        }

        if (outputHandler != null) {
            outputHandler.setSize(tag.hasKey("Output-Size", Constants.NBT.TAG_INT) ? tag.getInteger("Output-Size") : outputHandler.getSlots());
            NBTTagList outputTagList = tag.getTagList("Output-Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < outputTagList.tagCount(); i++) {
                NBTTagCompound itemTags = outputTagList.getCompoundTagAt(i);
                int slot = itemTags.getInteger("Slot");

                if (slot >= 0 && slot < outputHandler.getSlots()) {
                    outputHandler.setStackInSlot(slot, new ItemStack(itemTags));
                }
            }
//            outputHandler.onLoad();
        }

        if (cellHandler != null) {
           cellHandler.setSize(tag.hasKey("Cell-Size", Constants.NBT.TAG_INT) ? tag.getInteger("Cell-Size") : cellHandler.getSlots());
           NBTTagList cellTagList = tag.getTagList("Cell-Items", Constants.NBT.TAG_COMPOUND);
           for (int i = 0; i < cellTagList.tagCount(); i++) {
               NBTTagCompound itemTags = cellTagList.getCompoundTagAt(i);
               int slot = itemTags.getInteger("Slot");

               if (slot >= 0 && slot < cellHandler.getSlots()) {
                   cellHandler.setStackInSlot(slot, new ItemStack(itemTags));
               }
           }
//           cellHandler.onLoad();
        }
    }
}
