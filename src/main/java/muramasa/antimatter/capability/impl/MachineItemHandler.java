package muramasa.antimatter.capability.impl;

import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.ContentEvent;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class MachineItemHandler {

    private ItemStackHandler inputHandler, outputHandler, cellHandler;

    /** Constructor **/
    public MachineItemHandler(TileEntityMachine tile) {
        inputHandler = new ItemStackHandler(tile.getMachineType().getGui().getSlots(SlotType.IT_IN, tile.getTier()).size()) {
            @Override
            protected void onContentsChanged(int slot) {
                tile.onContentsChanged(ContentEvent.ITEM_INPUT, slot);
            }
        };
        outputHandler = new ItemStackHandler(tile.getMachineType().getGui().getSlots(SlotType.IT_OUT, tile.getTier()).size()) {
            @Override
            protected void onContentsChanged(int slot) {
                tile.onContentsChanged(ContentEvent.ITEM_OUTPUT, slot);
            }
        };
        if (tile.getMachineType().hasFlag(MachineFlag.FLUID)) {
            cellHandler = new ItemStackHandler(tile.getMachineType().getGui().getSlots(SlotType.CELL_IN, tile.getTier()).size() + tile.getMachineType().getGui().getSlots(SlotType.CELL_OUT, tile.getTier()).size()) {
                @Override
                protected void onContentsChanged(int slot) {
                    tile.onContentsChanged(ContentEvent.ITEM_CELL, slot);
                }
            };
        }
    }

    public MachineItemHandler(TileEntityMachine tile, CompoundNBT itemData) {
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
        if (outputHandler == null || outputs == null || outputs.length == 0) return;
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputHandler.getSlots(); j++) {
                ItemStack result = outputHandler.insertItem(j, outputs[i].copy(), false);
                if (result.isEmpty()) break;
            }
        }
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
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        if (inputHandler != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < inputHandler.getSlots(); i++) {
                if (!inputHandler.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt("Slot", i);
                    inputHandler.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put("Input-Items", list);
            tag.putInt("Input-Size", inputHandler.getSlots());
        }
        if (outputHandler != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                if (!outputHandler.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt("Slot", i);
                    outputHandler.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put("Output-Items", list);
            tag.putInt("Output-Size", outputHandler.getSlots());
        }
        if (cellHandler != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < cellHandler.getSlots(); i++) {
                if (!cellHandler.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt("Slot", i);
                    cellHandler.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put("Cell-Items", list);
            tag.putInt("Cell-Size", cellHandler.getSlots());
        }
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        if (inputHandler != null) {
            inputHandler.setSize(tag.contains("Input-Size", Constants.NBT.TAG_INT) ? tag.getInt("Input-Size") : inputHandler.getSlots());
            ListNBT inputTagList = tag.getList("Input-Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < inputTagList.size(); i++) {
                CompoundNBT itemTags = inputTagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if (slot >= 0 && slot < inputHandler.getSlots()) {
                    inputHandler.setStackInSlot(slot, ItemStack.read(itemTags));
                }
            }
        }
        if (outputHandler != null) {
            outputHandler.setSize(tag.contains("Output-Size", Constants.NBT.TAG_INT) ? tag.getInt("Output-Size") : outputHandler.getSlots());
            ListNBT outputTagList = tag.getList("Output-Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < outputTagList.size(); i++) {
                CompoundNBT itemTags = outputTagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if (slot >= 0 && slot < outputHandler.getSlots()) {
                    outputHandler.setStackInSlot(slot, ItemStack.read(itemTags));
                }
            }
        }
        if (cellHandler != null) {
           cellHandler.setSize(tag.contains("Cell-Size", Constants.NBT.TAG_INT) ? tag.getInt("Cell-Size") : cellHandler.getSlots());
           ListNBT cellTagList = tag.getList("Cell-Items", Constants.NBT.TAG_COMPOUND);
           for (int i = 0; i < cellTagList.size(); i++) {
               CompoundNBT itemTags = cellTagList.getCompound(i);
               int slot = itemTags.getInt("Slot");
               if (slot >= 0 && slot < cellHandler.getSlots()) {
                   cellHandler.setStackInSlot(slot, ItemStack.read(itemTags));
               }
           }
        }
    }
}
