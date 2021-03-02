package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.item.TrackedItemHandler;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import tesseract.Tesseract;
import tesseract.api.ITickingController;
import tesseract.api.item.IItemNode;
import tesseract.api.item.ItemData;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.IntStream;

import static muramasa.antimatter.machine.MachineFlag.*;

public class MachineItemHandler<T extends TileEntityMachine> implements IItemNode<ItemStack>, IMachineHandler, INBTSerializable<CompoundNBT> {

    protected final T tile;
    protected final EnumMap<MachineFlag, TrackedItemHandler<T>> inventories = new EnumMap<>(MachineFlag.class); // Use SlotType instead of MachineFlag?

    protected final int[] priority = new int[]{0, 0, 0, 0, 0, 0}; // TODO

    protected ITickingController controller;

    public MachineItemHandler(T tile) {
        this.tile = tile;
        if (tile.getMachineType().has(ITEM)) {
            inventories.put(ITEM_INPUT, new TrackedItemHandler<>(tile, tile.getMachineType().getGui().getSlots(SlotType.IT_IN, tile.getMachineTier()).size(), ContentEvent.ITEM_INPUT_CHANGED));
            inventories.put(ITEM_OUTPUT, new TrackedItemHandler<>(tile, tile.getMachineType().getGui().getSlots(SlotType.IT_OUT, tile.getMachineTier()).size(), ContentEvent.ITEM_OUTPUT_CHANGED));
        }
        if (tile.getMachineType().has(CELL)) {
            //TODO: allow multiple?
            inventories.put(CELL_INPUT, new TrackedItemHandler<>(tile, tile.getMachineType().getGui().getSlots(SlotType.CELL_IN, tile.getMachineTier()).size(), ContentEvent.ITEM_CELL_CHANGED));
        }
        if (tile.getMachineType().has(CELL)) {
            //TODO: allow multiple?
            inventories.put(CELL_OUTPUT, new TrackedItemHandler<>(tile, tile.getMachineType().getGui().getSlots(SlotType.CELL_OUT, tile.getMachineTier()).size(), ContentEvent.ITEM_CELL_CHANGED));
        }
   //     if (tile.getMachineType().has(FLUID)) {
   //         inventories.put(FLUID_INPUT, new TrackedItemHandler<>(tile, tile.getMachineType().getGui().getSlots(SlotType.CELL_IN, tile.getMachineTier()).size(), ContentEvent.ITEM_CELL_CHANGED));
    //    }
        if (tile.getMachineType().has(ENERGY)) {
            inventories.put(ENERGY, new TrackedItemHandler<>(tile, tile.getMachineType().getGui().getSlots(SlotType.ENERGY, tile.getMachineTier()).size(), ContentEvent.ENERGY_SLOT_CHANGED));
        }
    }

    @Override
    public void init() {
        registerNet();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        this.inventories.forEach((f, i) -> nbt.put(f.name(), i.serializeNBT()));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.inventories.forEach((f, i) -> i.deserializeNBT(nbt.getCompound(f.name())));
    }

    public void onServerUpdate() {
        if (controller != null) {
            controller.tick();
        }
    }

    public void onRemove() {
        if (tile.isServerSide()) {
            deregisterNet();
        }
    }

    public void onReset() {
        if (tile.isServerSide()) {
            refreshNet();
        }
    }

    /** Handler Access **/
    public IItemHandlerModifiable getInputHandler() {
        return inventories.get(ITEM_INPUT);
    }

    public IItemHandlerModifiable getOutputHandler() {
        return inventories.get(ITEM_OUTPUT);
    }

    public IItemHandlerModifiable getCellInputHandler() {
        return inventories.get(CELL_INPUT);
    }

    public IItemHandlerModifiable getCellOutputHandler() {
        return inventories.get(CELL_OUTPUT);
    }

    public IItemHandlerModifiable getChargeHandler() {
        return inventories.get(ENERGY);
    }

    public int getInputCount() {
        return getInputHandler().getSlots();
    }

    public int getOutputCount() {
        return getOutputHandler().getSlots();
    }

    public int getCellCount() {
        return getCellInputHandler().getSlots();
    }

    @Nonnull
    public ItemStack[] getInputs() {
        return getInputList().toArray(new ItemStack[0]);
    }

    public ItemStack[] getOutputs() {
        return getOutputList().toArray(new ItemStack[0]);
    }

    public ItemStack getCellInput() {
        return getCellInputHandler().getStackInSlot(0);
    }

    public ItemStack getCellOutput() {
        return getCellInputHandler().getStackInSlot(1);
    }

    public IItemHandlerModifiable getHandlerForSide(Direction side) {
        return side != tile.getOutputFacing() ? getInputHandler() : getOutputHandler();
    }

    /** Gets a list of non empty input Items **/
    public List<ItemStack> getInputList() {
        List<ItemStack> list = new ObjectArrayList<>();
        IItemHandlerModifiable inputs = getInputHandler();
        for (int i = 0; i < inputs.getSlots(); i++) {
            if (!inputs.getStackInSlot(i).isEmpty()) {
                list.add(inputs.getStackInSlot(i).copy());
            }
        }
        return list;
    }

    /** Returns a non copied list of chargeable items. **/
    public List<IEnergyHandler> getChargeableItems() {
        List<IEnergyHandler> list = new ObjectArrayList<>();
        if (tile.isServerSide()) {
            IItemHandlerModifiable chargeables = getChargeHandler();
            for (int i = 0; i < chargeables.getSlots(); i++) {
                ItemStack item = chargeables.getStackInSlot(i);
                if (!item.isEmpty()) {
                    AntimatterCaps.getCustomEnergyHandler(item).ifPresent(list::add);
                }
            }
        }
        return list;
    }

    /** Gets a list of non empty output Items **/
    public List<ItemStack> getOutputList() {
        List<ItemStack> list = new ObjectArrayList<>();
        IItemHandlerModifiable outputs = getOutputHandler();
        for (int i = 0; i < outputs.getSlots(); i++) {
            ItemStack slot = outputs.getStackInSlot(i);
            if (!slot.isEmpty()) {
                list.add(slot.copy());
            }
        }
        return list;
    }
    /**
     * Consumes the inputs from the active recipe.
     * @param recipe active recipe.
     * @param simulate whether to execute or just return items.
     * @return a list of consumed items, or an empty list if it failed during simulation.
     */
    public List<ItemStack> consumeInputs(Recipe recipe, boolean simulate) {
        Set<Integer> skipSlots = new HashSet<>();
        List<AntimatterIngredient> items = recipe.getInputItems();
        if (items == null) return Collections.emptyList();
        List<ItemStack> consumedItems = new ObjectArrayList<>();

        boolean success = items.stream().mapToInt(input -> {
            int failed = 0;
            IItemHandler wrap = getInputHandler();
            for (int i = 0; i < wrap.getSlots(); i++) {
                ItemStack item = wrap.getStackInSlot(i);
                if (input.test(item) && !skipSlots.contains(i) && item.getCount() >= input.count/*&& !Utils.hasNoConsumeTag(input)*/) {
                    if (!Utils.hasNoConsumeTag(input)) wrap.extractItem(i, input.count, simulate);
                    ItemStack cloned = item.copy();
                    cloned.setCount(input.count);
                    consumedItems.add(cloned);
                    skipSlots.add(i);
                    break;
                }
                if (i == wrap.getSlots() - 1) {
                    failed++;
                }
            }
            return failed;
        }).sum() == 0;
        //onSlotChanged should call dirty though, not sure if needed.
        if (!simulate && success && recipe.hasInputItems()) tile.markDirty();
        if (simulate) return success ? consumedItems : Collections.emptyList();
        return consumedItems;
    }

    /**
     * Fill the output slots with @outputs items.
     * @param outputs the outputs to add.
     */
    public void addOutputs(ItemStack... outputs) {
        IItemHandlerModifiable outputHandler = getOutputHandler();
        if (outputHandler == null || outputs == null || outputs.length == 0) {
            return;
        }
        for (ItemStack output : outputs) {
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                ItemStack result = outputHandler.insertItem(i, output.copy(), false);
                if (result.isEmpty()) {
                    break;
                }
            }
        }
    }

    /** Helpers **/
    public boolean canOutputsFit(ItemStack[] a) {
        IItemHandlerModifiable outputHandler = getOutputHandler();
        boolean[] results = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < outputHandler.getSlots(); j++) {
                results[i] |= outputHandler.insertItem(j, a[i], true).isEmpty();
            }
        }
        for (boolean value : results) {
            if (!value) {
                return false;
            }
        }
        return true;
        // return getSpaceForOutputs(a) >= a.length;
    }

    public int getSpaceForOutputs(ItemStack[] a) {
        int matchCount = 0;
        //Here, cast to use stack limit
        IItemHandlerModifiable handler = getOutputHandler();
        if (!(handler instanceof TrackedItemHandler)) {
            return 0;
        }
        TrackedItemHandler<T> outputHandler = (TrackedItemHandler<T>) handler;
        for (ItemStack stack : a) {
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                ItemStack item = outputHandler.getStackInSlot(i);
                if (item.isEmpty() || (Utils.equals(stack, item) && item.getCount() + stack.getCount() <= outputHandler.getStackLimit(i, item))) {
                    matchCount++;
                    break;
                }
            }
        }
        
        return matchCount;
    }

    public ItemStack[] consumeAndReturnInputs(ItemStack... inputs) {
        List<ItemStack> notConsumed = new ObjectArrayList<>();
        IItemHandlerModifiable inputHandler = getInputHandler();
        for (ItemStack input : inputs) {
            for (int i = 0; i < inputHandler.getSlots(); i++) {
                if (Utils.equals(input, inputHandler.getStackInSlot(i))) {
                    ItemStack result = inputHandler.extractItem(i, input.getCount(), false);
                    if (!result.isEmpty()) {
                        if (result.getCount() == input.getCount()) {
                            break;
                        } else {
                            notConsumed.add(Utils.ca(input.getCount() - result.getCount(), input));
                        }
                    }
                } else if (i == inputHandler.getSlots() - 1) {
                    notConsumed.add(input);
                }
            }
        }
        return notConsumed.toArray(new ItemStack[0]);
    }

    public ItemStack[] exportAndReturnOutputs(ItemStack... outputs) {
        List<ItemStack> notExported = new ObjectArrayList<>();
        IItemHandlerModifiable outputHandler = getOutputHandler();
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputHandler.getSlots(); j++) {
                ItemStack result = outputHandler.insertItem(j, outputs[i].copy(), false);
                if (result.isEmpty()) {
                    break;
                } else {
                    outputs[i] = result;
                }
                if (j == outputHandler.getSlots() - 1) {
                    notExported.add(result);
                }
            }
        }
        return notExported.toArray(new ItemStack[0]);
    }

    /** Tesseract IItemNode Implementations **/
    @Override
    public int insert(ItemData data, boolean simulate) {
        ItemStack stack = (ItemStack) data.getStack();
        IItemHandlerModifiable inputHandler = getInputHandler();
        int slot = getFirstValidSlot(stack.getItem());
        if (slot != -1) {
            ItemStack inserted = inputHandler.insertItem(slot, stack, simulate);
            if (!inserted.isEmpty()) {
                return stack.getCount() - inserted.getCount();
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public ItemData<ItemStack> extract(int slot, int amount, boolean simulate) {
        ItemStack stack = getOutputHandler().extractItem(slot, amount, simulate);
        return stack.isEmpty() ? null : new ItemData<>(slot, stack);
    }

    @Nonnull
    @Override
    public IntList getAvailableSlots(Dir direction) {
        if (canOutput(direction)) {
            return new IntArrayList(IntStream.range(0, getHandlerForSide(tile.getOutputFacing()).getSlots()).iterator());
        }
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
        return getOutputHandler().getStackInSlot(slot).isEmpty();
    }

    @Override
    public boolean canOutput() {
        return this.inventories.containsKey(ITEM_OUTPUT);
    }

    @Override
    public boolean canInput() {
        return this.inventories.containsKey(ITEM_INPUT);
    }

    @Override
    public boolean canOutput(Dir direction) {
        return tile.getOutputFacing().getIndex() == direction.getIndex();
    }

    @Override
    public boolean canInput(Object item, Dir direction) {
        if (tile.getFacing().getIndex() == direction.getIndex()) return false;
        if (/*TODO: Can input into output* ||*/tile.getOutputFacing().getIndex() == direction.getIndex()) return false;
        return getFirstValidSlot(item) != -1;
    }

    @Override
    public boolean connects(Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex()/* && !(tile.getCover(Ref.DIRECTIONS[direction.getIndex()]) instanceof CoverMaterial)*/;
    }

    private int getFirstValidSlot(Object item) {
        int slot = -1;
        IItemHandlerModifiable inputHandler = getInputHandler();
        for (int i = 0; i < inputHandler.getSlots(); i++) {
            ItemStack stack = inputHandler.getStackInSlot(i);
            if (stack.isEmpty()) {
                slot = i;
            } else {
                if (stack.getItem().equals(item) && stack.getMaxStackSize() > stack.getCount()) {
                    return i;
                }
            }
        }
        return slot;
    }

    @Override
    public void deregisterNet() {
        if (tile.getWorld() == null) return;
        Tesseract.ITEM.remove(tile.getDimension(), tile.getPos().toLong());
    }

    @Override
    public void registerNet() {
        if (tile.getWorld() == null) return;
        Tesseract.ITEM.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
    }
}
