package muramasa.antimatter.capability.impl;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import tesseract.Tesseract;
import tesseract.api.item.IItemNode;
import tesseract.api.item.ItemData;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MachineItemHandler implements IItemNode, ITickHost {

    protected TileEntityMachine tile;
    protected ITickingController controller;
    protected ItemStackWrapper inputWrapper, outputWrapper, cellWrapper, chargeWrapper;
    protected int[] priority = new int[]{0, 0, 0, 0, 0, 0};

    public MachineItemHandler(TileEntityMachine tile) {
        this.tile = tile;
        inputWrapper = new ItemStackWrapper(tile, tile.getMachineType().getGui().getSlots(SlotType.IT_IN, tile.getMachineTier()).size(), ContentEvent.ITEM_INPUT_CHANGED);
        outputWrapper = new ItemStackWrapper(tile, tile.getMachineType().getGui().getSlots(SlotType.IT_OUT, tile.getMachineTier()).size(), ContentEvent.ITEM_OUTPUT_CHANGED);
        if (tile.getMachineType().has(MachineFlag.FLUID)) {
            cellWrapper = new ItemStackWrapper(tile, tile.getMachineType().getGui().getSlots(SlotType.CELL_IN, tile.getMachineTier()).size() + tile.getMachineType().getGui().getSlots(SlotType.CELL_OUT, tile.getMachineTier()).size(), ContentEvent.ITEM_CELL_CHANGED);
        }
        if (tile.getMachineType().has(MachineFlag.ENERGY)) {
            chargeWrapper = new ItemStackWrapper(tile, tile.getMachineType().getGui().getSlots(SlotType.ENERGY, tile.getMachineTier()).size(), ContentEvent.ENERGY_CHANGED);
        }
        Tesseract.ITEM.registerNode(tile.getDimention(), tile.getPos().toLong(), this);
    }

    public void onUpdate() {
        if (controller != null) controller.tick();
    }

    public void onRemove() {
        Tesseract.ITEM.remove(tile.getDimention(), tile.getPos().toLong());
    }

    /*public void onReset() {
        if (tile.isServerSide()) {
            TesseractAPI.removeItem(tile.getDimention(), tile.getPos().toLong());
            TesseractAPI.registerItemNode(tile.getDimention(), tile.getPos().toLong(), this);
        }
    }*/

    /** Handler Access **/
    public IItemHandler getInputWrapper() {
        return inputWrapper;
    }

    public IItemHandler getOutputWrapper() {
        return outputWrapper;
    }

    public IItemHandler getCellWrapper() {
        return cellWrapper;
    }

    public IItemHandler getChargeWrapper() {
        return chargeWrapper;
    }

    public int getInputCount() {
        return inputWrapper.getSlots();
    }

    public int getOutputCount() {
        return outputWrapper.getSlots();
    }

    public int getCellCount() {
        return cellWrapper.getSlots();
    }

    public ItemStack[] getInputs() {
        return getInputList().toArray(new ItemStack[0]);
    }

    public ItemStack[] getOutputs() {
        return getOutputList().toArray(new ItemStack[0]);
    }

    public ItemStack getCellInput() {
        return cellWrapper.getStackInSlot(0);
    }

    public ItemStack getCellOutput() {
        return cellWrapper.getStackInSlot(1);
    }

    public IItemHandler getHandlerForSide(Direction side) {
        return side == Direction.UP ? inputWrapper : outputWrapper;
    }

    public void onMachineEvent(IMachineEvent event, Object... data) {

    }

    /** Gets a list of non empty input Items **/
    public List<ItemStack> getInputList() {
        List<ItemStack> list = new ObjectArrayList<>();
        for (int i = 0; i < inputWrapper.getSlots(); i++) {
            if (!inputWrapper.getStackInSlot(i).isEmpty()) list.add(inputWrapper.getStackInSlot(i).copy());
        }
        return list;
    }
    /**
     Returns a non copied list of chargeable items.
     //TODO: Should this instead return the actual ItemStacks? Usually chargeable items only need the IEnergyHandler.
     **/
    public List<IEnergyHandler> getChargeableItems() {
        List<IEnergyHandler> list = new ObjectArrayList<>();
        for (int i = 0; i < chargeWrapper.getSlots(); i++) {
            //orElse: null, should always be present.
            if (!chargeWrapper.getStackInSlot(i).isEmpty()) list.add(chargeWrapper.getStackInSlot(i).getCapability(AntimatterCaps.ENERGY).orElse(null));
        }
        return list;
    }

    /** Gets a list of non empty output Items **/
    public List<ItemStack> getOutputList() {
        List<ItemStack> list = new ObjectArrayList<>();
        for (int i = 0; i < outputWrapper.getSlots(); i++) {
            if (!outputWrapper.getStackInSlot(i).isEmpty()) list.add(outputWrapper.getStackInSlot(i).copy());
        }
        return list;
    }

    public void consumeInputs(ItemStack... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputWrapper.getSlots(); j++) {
                if (Utils.equals(inputs[i], inputWrapper.getStackInSlot(j)) && !Utils.hasNoConsumeTag(inputs[i])) {
                    inputWrapper.getStackInSlot(j).shrink(inputs[i].getCount());
                    break;
                }
            }
        }
    }

    public void addOutputs(ItemStack... outputs) {
        if (outputWrapper == null || outputs == null || outputs.length == 0) return;
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputWrapper.getSlots(); j++) {
                ItemStack result = outputWrapper.insertItem(j, outputs[i].copy(), false);
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
            for (int j = 0; j < outputWrapper.getSlots(); j++) {
                if (outputWrapper.getStackInSlot(j).isEmpty() || (Utils.equals(a[i], outputWrapper.getStackInSlot(j)) && outputWrapper.getStackInSlot(j).getCount() + a[i].getCount() <= outputWrapper.getStackInSlot(j).getMaxStackSize())) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount;
    }

    public ItemStack[] consumeAndReturnInputs(ItemStack... inputs) {
        List<ItemStack> notConsumed = new ObjectArrayList<>();
        ItemStack result;
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputWrapper.getSlots(); j++) {
                if (Utils.equals(inputs[i], inputWrapper.getStackInSlot(j))) {
                    result = inputWrapper.extractItem(j, inputs[i].getCount(), false);
                    if (!result.isEmpty()) {
                        if (result.getCount() == inputs[i].getCount()) break;
                        else notConsumed.add(Utils.ca(inputs[i].getCount() - result.getCount(), inputs[i]));
                    }
                } else if (j == inputWrapper.getSlots() - 1) {
                    notConsumed.add(inputs[i]);
                }
            }
        }
        return notConsumed.toArray(new ItemStack[0]);
    }

    public ItemStack[] exportAndReturnOutputs(ItemStack... outputs) {
        List<ItemStack> notExported = new ObjectArrayList<>();
        ItemStack result;
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputWrapper.getSlots(); j++) {
                result = outputWrapper.insertItem(j, outputs[i].copy(), false);
                if (result.isEmpty()) break;
                else outputs[i] = result;
                if (j == outputWrapper.getSlots() - 1) notExported.add(result);
            }
        }
        return notExported.toArray(new ItemStack[0]);
    }

    /** NBT **/
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        if (inputWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < inputWrapper.getSlots(); i++) {
                if (!inputWrapper.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt("Slot", i);
                    inputWrapper.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put("Input-Items", list);
            tag.putInt("Input-Size", inputWrapper.getSlots());
        }
        if (outputWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < outputWrapper.getSlots(); i++) {
                if (!outputWrapper.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt("Slot", i);
                    outputWrapper.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put("Output-Items", list);
            tag.putInt("Output-Size", outputWrapper.getSlots());
        }
        if (cellWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < cellWrapper.getSlots(); i++) {
                if (!cellWrapper.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt("Slot", i);
                    cellWrapper.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put("Cell-Items", list);
            tag.putInt("Cell-Size", cellWrapper.getSlots());
        }
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        if (inputWrapper != null) {
            inputWrapper.setSize(tag.contains("Input-Size", Constants.NBT.TAG_INT) ? tag.getInt("Input-Size") : inputWrapper.getSlots());
            ListNBT inputTagList = tag.getList("Input-Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < inputTagList.size(); i++) {
                CompoundNBT itemTags = inputTagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if (slot >= 0 && slot < inputWrapper.getSlots()) {
                    inputWrapper.setStackInSlot(slot, ItemStack.read(itemTags));
                }
            }
        }
        if (outputWrapper != null) {
            outputWrapper.setSize(tag.contains("Output-Size", Constants.NBT.TAG_INT) ? tag.getInt("Output-Size") : outputWrapper.getSlots());
            ListNBT outputTagList = tag.getList("Output-Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < outputTagList.size(); i++) {
                CompoundNBT itemTags = outputTagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                if (slot >= 0 && slot < outputWrapper.getSlots()) {
                    outputWrapper.setStackInSlot(slot, ItemStack.read(itemTags));
                }
            }
        }
        if (cellWrapper != null) {
           cellWrapper.setSize(tag.contains("Cell-Size", Constants.NBT.TAG_INT) ? tag.getInt("Cell-Size") : cellWrapper.getSlots());
           ListNBT cellTagList = tag.getList("Cell-Items", Constants.NBT.TAG_COMPOUND);
           for (int i = 0; i < cellTagList.size(); i++) {
               CompoundNBT itemTags = cellTagList.getCompound(i);
               int slot = itemTags.getInt("Slot");
               if (slot >= 0 && slot < cellWrapper.getSlots()) {
                   cellWrapper.setStackInSlot(slot, ItemStack.read(itemTags));
               }
           }
        }
    }

    /** Tesseract IItemNode Implementations **/
    @Override
    public int insert(@Nonnull ItemData data, boolean simulate) {
        ItemStack stack = (ItemStack) data.getStack();
        int slot = inputWrapper.getFirstValidSlot(stack.getItem());
        if (slot == -1) {
            return 0;
        }

        ItemStack inserted = inputWrapper.insertItem(slot, stack, simulate);
        int count = stack.getCount();
        if (!inserted.isEmpty()) {
            count -= inserted.getCount() ;
        }

        return count;
    }

    @Nullable
    @Override
    public ItemData extract(int slot, int amount, boolean simulate) {
        ItemStack stack = outputWrapper.extractItem(slot, amount, simulate);
        return stack.isEmpty() ? null : new ItemData(slot, stack, stack.getItem());
    }

    @Nonnull
    @Override
    public IntList getAvailableSlots(@Nonnull Dir direction) {
        return outputWrapper.getAvailableSlots(direction.getIndex());
    }

    @Override
    public int getOutputAmount(@Nonnull Dir direction) {
        return 4;
    }

    @Override
    public int getPriority(@Nonnull Dir direction) {
        return priority[direction.getIndex()];
    }

    @Override
    public boolean isEmpty(int slot) {
        return outputWrapper.getStackInSlot(slot).isEmpty();
    }

    @Override
    public boolean canOutput() {
        return outputWrapper != null;
    }

    @Override
    public boolean canInput() {
        return inputWrapper != null;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return tile.getOutputFacing().getIndex() == direction.getIndex();
    }

    @Override
    public boolean canInput(@Nonnull Object item, @Nonnull Dir direction) {
        if (tile.getFacing().getIndex() == direction.getIndex()) return false;
        if (/*TODO: Can input into output* ||*/tile.getOutputFacing().getIndex() == direction.getIndex()) return false;
        return inputWrapper.isItemAvailable(item, direction.getIndex()) && inputWrapper.getFirstValidSlot(item) != -1;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex()/* && !(tile.getCover(Ref.DIRECTIONS[direction.getIndex()]) instanceof CoverMaterial)*/;
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }
}
