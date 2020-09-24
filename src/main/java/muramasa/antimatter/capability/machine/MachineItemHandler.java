package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.TagInput;
import muramasa.antimatter.capability.item.ItemStackWrapper;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import tesseract.Tesseract;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;
import tesseract.api.item.IItemNode;
import tesseract.api.item.ItemData;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static muramasa.antimatter.machine.MachineFlag.ENERGY;
import static muramasa.antimatter.machine.MachineFlag.FLUID;

public class MachineItemHandler<T extends TileEntityMachine> implements IItemNode<ItemStack>, IMachineHandler, IGuiHandler, ICapabilityHandler, ITickHost {

    protected T tile;
    protected ITickingController controller;
    //Machine GUI wrappers. If a machine (e.g. multiblock) wants to extend getInputWrapper() these need to be a part of the combined inventory.
    protected ItemStackWrapper inputWrapper, outputWrapper, cellWrapper, chargeWrapper;
    protected int[] priority = new int[]{0, 0, 0, 0, 0, 0};
    protected boolean isEject;

    public MachineItemHandler(T tile, CompoundNBT tag) {
        this.tile = tile;
        inputWrapper = new ItemStackWrapper(tile, tile.getMachineType().getGui().getSlots(SlotType.IT_IN, tile.getMachineTier()).size(), ContentEvent.ITEM_INPUT_CHANGED);
        outputWrapper = new ItemStackWrapper(tile, tile.getMachineType().getGui().getSlots(SlotType.IT_OUT, tile.getMachineTier()).size() + tile.getMachineType().getGui().getSlots(SlotType.CELL_OUT, tile.getMachineTier()).size(), ContentEvent.ITEM_OUTPUT_CHANGED);
        if (tile.getMachineType().has(FLUID)) {
            cellWrapper = new ItemStackWrapper(tile, tile.getMachineType().getGui().getSlots(SlotType.CELL_IN, tile.getMachineTier()).size(), ContentEvent.ITEM_CELL_CHANGED);
        }
        if (tile.getMachineType().has(ENERGY)) {
            chargeWrapper = new ItemStackWrapper(tile, tile.getMachineType().getGui().getSlots(SlotType.ENERGY, tile.getMachineTier()).size(), ContentEvent.ENERGY_SLOT_CHANGED);
        }
        if (tag != null) deserialize(tag);
        if (tile.isServerSide()) Tesseract.ITEM.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
    }

    public void onUpdate() {
        if (controller != null) controller.tick();
    }

    public void onRemove() {
        if (tile.isServerSide()) Tesseract.ITEM.remove(tile.getDimension(), tile.getPos().toLong());
    }

    public void onReset() {
        if (tile.isServerSide()) {
            Tesseract.ITEM.remove(tile.getDimension(), tile.getPos().toLong());
            Tesseract.ITEM.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
        }
    }

    @Override
    public void onGuiEvent(IGuiEvent event, int... data) {
        if (event instanceof GuiEvent) {
            switch ((GuiEvent)event) {
                case ITEM_EJECT:
                    // TODO: Finish ejection
                    isEject = data[0] != 0;
                    break;
            }
        }
    }

    /** Handler Access **/
    public IItemHandlerModifiable getInputWrapper() {
        return inputWrapper;
    }

    public IItemHandlerModifiable getOutputWrapper() {
        return outputWrapper;
    }

    public IItemHandlerModifiable getCellWrapper() {
        return cellWrapper;
    }

    public IItemHandlerModifiable getChargeWrapper() {
        return chargeWrapper;
    }

    public int getInputCount() {
        return getInputWrapper().getSlots();
    }

    public int getOutputCount() {
        return getOutputWrapper().getSlots();
    }

    public int getCellCount() {
        return getCellWrapper().getSlots();
    }

    public ItemStack[] getInputs() {
        return getInputList().toArray(new ItemStack[0]);
    }

    public ItemStack[] getOutputs() {
        return getOutputList().toArray(new ItemStack[0]);
    }

    public ItemStack getCellInput() {
        return getCellWrapper().getStackInSlot(0);
    }

    public ItemStack getCellOutput() {
        return getCellWrapper().getStackInSlot(1);
    }

    public IItemHandler getHandlerForSide(Direction side) {
        return side != tile.getOutputFacing() ? getInputWrapper() : getOutputWrapper();
    }

    /** Gets a list of non empty input Items **/
    public List<ItemStack> getInputList() {
        List<ItemStack> list = new ObjectArrayList<>();
        for (int i = 0; i < getInputWrapper().getSlots(); i++) {
            if (!getInputWrapper().getStackInSlot(i).isEmpty()) list.add(getInputWrapper().getStackInSlot(i).copy());
        }
        return list;
    }

    /** Returns a non copied list of chargeable items. **/
    public List<IEnergyHandler> getChargeableItems() {
        List<IEnergyHandler> list = new ObjectArrayList<>();
        if (tile.isServerSide()) {
            for (int i = 0; i < chargeWrapper.getSlots(); i++) {
                ItemStack item = chargeWrapper.getStackInSlot(i);
                if (!item.isEmpty()) list.add(item.getCapability(AntimatterCaps.ENERGY_HANDLER_CAPABILITY).orElse(null));
            }
        }
        return list;
    }

    /** Gets a list of non empty output Items **/
    public List<ItemStack> getOutputList() {
        List<ItemStack> list = new ObjectArrayList<>();
        for (int i = 0; i < getOutputWrapper().getSlots(); i++) {
            ItemStack item = getOutputWrapper().getStackInSlot(i);
            if (!item.isEmpty()) list.add(item.copy());
        }
        return list;
    }

    public boolean consumeInputs(Recipe recipe, boolean simulate) {
        boolean success = true;
        Set<Integer> skipSlots = new HashSet<>();
        if (recipe.getInputItems() != null && recipe.getInputItems().length > 0) {
            for (ItemStack input : recipe.getInputItems()) {
                IItemHandler wrap = getInputWrapper();
                for (int i = 0; i < wrap.getSlots(); i++) {
                    ItemStack item = wrap.getStackInSlot(i);
                    if (Utils.equals(input, item) && !Utils.hasNoConsumeTag(input) && !skipSlots.contains(i)) {
                        wrap.extractItem(i, input.getCount(), simulate);
                        skipSlots.add(i);
                        break;
                    }
                    if (i == wrap.getSlots()-1) {
                        success = false;
                    }
                }
            }
        }

        if (recipe.getTagInputs() != null && recipe.getTagInputs().length > 0) {
            for (TagInput input : recipe.getTagInputs()) {
                IItemHandler wrap = getInputWrapper();
                for (int i = 0; i < wrap.getSlots(); i++) {
                    ItemStack item = wrap.getStackInSlot(i);
                    if (input.tag.contains(item.getItem()) && !skipSlots.contains(i) /*&& !Utils.hasNoConsumeTag(input)*/) {
                        wrap.extractItem(i, input.count, simulate);
                        skipSlots.add(i);
                        break;
                    }
                    if (i == wrap.getSlots()-1) {
                        success = false;
                    }
                }
            }
        }
        if (!simulate) tile.markDirty();
        return success;
    }

    public void addOutputs(ItemStack... outputs) {
        if (getOutputWrapper() == null || outputs == null || outputs.length == 0) return;
        for (ItemStack output : outputs) {
            for (int i = 0; i < getOutputWrapper().getSlots(); i++) {
                ItemStack result = getOutputWrapper().insertItem(i, output.copy(), false);
                if (result.isEmpty()) break;
            }
        }
        tile.markDirty();
    }

    /** Helpers **/
    public boolean canOutputsFit(ItemStack[] a) {
        return getSpaceForOutputs(a) >= a.length;
    }

    public int getSpaceForOutputs(ItemStack[] a) {
        int matchCount = 0;
        for (ItemStack stack : a) {
            for (int i = 0; i < getOutputWrapper().getSlots(); i++) {
                ItemStack item = getOutputWrapper().getStackInSlot(i);
                if (item.isEmpty() || (Utils.equals(stack, item) && item.getCount() + stack.getCount() <= item.getMaxStackSize())) {
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
        for (ItemStack input : inputs) {
            for (int i = 0; i < getInputWrapper().getSlots(); i++) {
                if (Utils.equals(input, getInputWrapper().getStackInSlot(i))) {
                    result = getInputWrapper().extractItem(i, input.getCount(), false);
                    if (!result.isEmpty()) {
                        if (result.getCount() == input.getCount()) break;
                        else notConsumed.add(Utils.ca(input.getCount() - result.getCount(), input));
                    }
                } else if (i == getInputWrapper().getSlots() - 1) {
                    notConsumed.add(input);
                }
            }
        }
        return notConsumed.toArray(new ItemStack[0]);
    }

    public ItemStack[] exportAndReturnOutputs(ItemStack... outputs) {
        List<ItemStack> notExported = new ObjectArrayList<>();
        ItemStack result;
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < getOutputWrapper().getSlots(); j++) {
                result = getOutputWrapper().insertItem(j, outputs[i].copy(), false);
                if (result.isEmpty()) break;
                else outputs[i] = result;
                if (j == getOutputWrapper().getSlots() - 1) notExported.add(result);
            }
        }
        return notExported.toArray(new ItemStack[0]);
    }

    public boolean isEjecting() {
        return isEject;
    }

    /** NBT **/
    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean(Ref.TAG_MACHINE_EJECT_ITEM, isEject);
        if (inputWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < inputWrapper.getSlots(); i++) {
                if (!inputWrapper.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt(Ref.TAG_MACHINE_SLOT_SIZE, i);
                    inputWrapper.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put(Ref.TAG_MACHINE_INPUT_ITEM, list);
            tag.putInt(Ref.TAG_MACHINE_INPUT_SIZE, inputWrapper.getSlots());
        }
        if (outputWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < outputWrapper.getSlots(); i++) {
                if (!outputWrapper.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt(Ref.TAG_MACHINE_SLOT_SIZE, i);
                    outputWrapper.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put(Ref.TAG_MACHINE_OUTPUT_ITEM, list);
            tag.putInt(Ref.TAG_MACHINE_OUTPUT_SIZE, outputWrapper.getSlots());
        }
        if (cellWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < cellWrapper.getSlots(); i++) {
                if (!cellWrapper.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt(Ref.TAG_MACHINE_SLOT_SIZE, i);
                    cellWrapper.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put(Ref.TAG_MACHINE_CELL_ITEM, list);
            tag.putInt(Ref.TAG_MACHINE_CELL_SIZE, cellWrapper.getSlots());
        }
        if (chargeWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < chargeWrapper.getSlots(); i++) {
                if (!chargeWrapper.getStackInSlot(i).isEmpty()) {
                    CompoundNBT itemTag = new CompoundNBT();
                    itemTag.putInt(Ref.TAG_MACHINE_SLOT_SIZE, i);
                    chargeWrapper.getStackInSlot(i).write(itemTag);
                    list.add(itemTag);
                }
            }
            tag.put(Ref.TAG_MACHINE_CHARGE_ITEM, list);
            tag.putInt(Ref.TAG_MACHINE_CHARGE_SIZE, chargeWrapper.getSlots());
        }
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        isEject = tag.getBoolean(Ref.TAG_MACHINE_EJECT_ITEM);
        if (inputWrapper != null) {
            inputWrapper.setSize(tag.contains(Ref.TAG_MACHINE_INPUT_SIZE, Constants.NBT.TAG_INT) ? tag.getInt(Ref.TAG_MACHINE_INPUT_SIZE) : inputWrapper.getSlots());
            ListNBT inputTagList = tag.getList(Ref.TAG_MACHINE_INPUT_ITEM, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < inputTagList.size(); i++) {
                CompoundNBT itemTags = inputTagList.getCompound(i);
                int slot = itemTags.getInt(Ref.TAG_MACHINE_SLOT_SIZE);
                if (slot >= 0 && slot < inputWrapper.getSlots()) {
                    inputWrapper.setStackInSlot(slot, ItemStack.read(itemTags));
                }
            }
        }
        if (outputWrapper != null) {
            outputWrapper.setSize(tag.contains(Ref.TAG_MACHINE_OUTPUT_SIZE, Constants.NBT.TAG_INT) ? tag.getInt(Ref.TAG_MACHINE_OUTPUT_SIZE) : outputWrapper.getSlots());
            ListNBT outputTagList = tag.getList(Ref.TAG_MACHINE_OUTPUT_ITEM, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < outputTagList.size(); i++) {
                CompoundNBT itemTags = outputTagList.getCompound(i);
                int slot = itemTags.getInt(Ref.TAG_MACHINE_SLOT_SIZE);
                if (slot >= 0 && slot < outputWrapper.getSlots()) {
                    outputWrapper.setStackInSlot(slot, ItemStack.read(itemTags));
                }
            }
        }
        if (cellWrapper != null) {
           cellWrapper.setSize(tag.contains(Ref.TAG_MACHINE_CELL_SIZE, Constants.NBT.TAG_INT) ? tag.getInt(Ref.TAG_MACHINE_CELL_SIZE) : cellWrapper.getSlots());
           ListNBT cellTagList = tag.getList(Ref.TAG_MACHINE_CELL_ITEM, Constants.NBT.TAG_COMPOUND);
           for (int i = 0; i < cellTagList.size(); i++) {
               CompoundNBT itemTags = cellTagList.getCompound(i);
               int slot = itemTags.getInt(Ref.TAG_MACHINE_SLOT_SIZE);
               if (slot >= 0 && slot < cellWrapper.getSlots()) {
                   cellWrapper.setStackInSlot(slot, ItemStack.read(itemTags));
               }
           }
        }
        if (getChargeWrapper() != null) {
            chargeWrapper.setSize(tag.contains(Ref.TAG_MACHINE_CHARGE_SIZE, Constants.NBT.TAG_INT) ? tag.getInt(Ref.TAG_MACHINE_CHARGE_SIZE) : cellWrapper.getSlots());
            ListNBT chargeTagList = tag.getList(Ref.TAG_MACHINE_CHARGE_ITEM, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < chargeTagList.size(); i++) {
                CompoundNBT itemTags = chargeTagList.getCompound(i);
                int slot = itemTags.getInt(Ref.TAG_MACHINE_SLOT_SIZE);
                if (slot >= 0 && slot < chargeWrapper.getSlots()) {
                    chargeWrapper.setStackInSlot(slot, ItemStack.read(itemTags));
                }
            }
        }
    }

    /** Tesseract IItemNode Implementations **/
    @Override
    public int insert(ItemData<ItemStack> data, boolean simulate) {
        ItemStack stack = data.getStack();
        int slot = inputWrapper.getFirstValidSlot(stack);
        if (slot == -1) {
            return 0;
        }

        ItemStack inserted = inputWrapper.insertItem(slot, stack, simulate);
        int count = stack.getCount();
        if (!inserted.isEmpty()) {
            count -= inserted.getCount() ;
        }
        if (!simulate) tile.markDirty();

        return count;
    }

    @Nullable
    @Override
    public ItemData<ItemStack> extract(int slot, int amount, boolean simulate) {
        ItemStack stack = outputWrapper.extractItem(slot, amount, simulate);
        if (!simulate) tile.markDirty();
        return stack.isEmpty() ? null : new ItemData<>(slot, stack);
    }

    @Nonnull
    @Override
    public IntList getAvailableSlots(Dir direction) {
        if (canOutput(direction)) return outputWrapper.getAvailableSlots(direction.getIndex());
        return new IntArrayList();
    }

    @Override
    public int getOutputAmount(Dir direction) {
        return 4;
    }

    @Override
    public int getPriority(Dir direction) {
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
    public boolean canOutput(Dir direction) {
        return tile.getOutputFacing().getIndex() == direction.getIndex();
    }

    @Override
    public boolean canInput(ItemStack item, Dir direction) {
        if (tile.getFacing().getIndex() == direction.getIndex()) return false;
        if (/*TODO: Can input into output* ||*/tile.getOutputFacing().getIndex() == direction.getIndex()) return false;
        return inputWrapper.isItemAvailable(item, direction.getIndex()) && inputWrapper.getFirstValidSlot(item) != -1;
    }

    @Override
    public boolean connects(Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex()/* && !(tile.getCover(Ref.DIRECTIONS[direction.getIndex()]) instanceof CoverMaterial)*/;
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController) {
            controller = newController;
        }
    }

    @Override
    public Capability<?> getCapability() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }
}
