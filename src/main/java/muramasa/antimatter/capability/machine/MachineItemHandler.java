package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.item.*;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import tesseract.Tesseract;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static muramasa.antimatter.machine.MachineFlag.GUI;

public class MachineItemHandler<T extends TileEntityMachine<T>> implements IMachineHandler, INBTSerializable<CompoundNBT>, Dispatch.Sided<IItemHandler> {

    protected final T tile;
    protected final Object2ObjectMap<SlotType<?>, TrackedItemHandler<T>> inventories = new Object2ObjectOpenHashMap<>(); // Use SlotType instead of MachineFlag?

    public MachineItemHandler(T tile) {
        this.tile = tile;
        if (tile.has(GUI)) {
            Map<SlotType<?>, List<SlotData<?>>> map = tile.getMachineType().getSlots(tile.getMachineTier()).stream().collect(Collectors.groupingBy(SlotData::getType));
            for (Map.Entry<SlotType<?>, List<SlotData<?>>> entry : map.entrySet()) {
                SlotType<?> type = entry.getKey();
                int count = tile.getMachineType().getCount(tile.getMachineTier(), entry.getKey());
                if (type == SlotType.DISPLAY_SETTABLE || type == SlotType.DISPLAY) {
                    inventories.put(type, new FakeTrackedItemHandler<>(tile, count, type.output, type.input, type.tester, type.ev));
                } else {
                    inventories.put(type, new TrackedItemHandler<>(tile, count, type.output, type.input, type.tester, type.ev));
                }

            }
        }
        inventories.defaultReturnValue(new TrackedItemHandler<>(tile, 0, false, false, (a, b) -> false, null));
    }

    public Map<SlotType<?>, IItemHandler> getAll() {
        return (Map<SlotType<?>, IItemHandler>) (Object) inventories;
    }

    @Override
    public void init() {
        ///registerNet();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        this.inventories.forEach((f, i) -> nbt.put(f.getId(), i.serializeNBT()));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.inventories.forEach((f, i) -> {
            if (!nbt.contains(f.getId())) return;
            i.deserializeNBT(nbt.getCompound(f.getId()));
        });
    }

    public T getTile() {
        return tile;
    }

    public void onUpdate() {

    }

    public List<ItemStack> getAllItems() {
        return inventories.values().stream().flatMap(t -> {
            List<ItemStack> stacks = new ObjectArrayList<>(t.getSlots());
            for (int i = 0; i < t.getSlots(); i++) {
                stacks.add(t.getStackInSlot(i).copy());
            }
            return stacks.stream();
        }).collect(Collectors.toList());
    }

    public void onRemove() {

    }

    public static ItemStack insertIntoOutput(IItemHandler handler, int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (handler instanceof ITrackedHandler) {
            return ((ITrackedHandler) handler).insertOutputItem(slot, stack, simulate);
        }
        return handler.insertItem(slot, stack, simulate);
    }

    public static ItemStack extractFromInput(IItemHandler handler, int slot, int amount, boolean simulate) {
        if (handler instanceof ITrackedHandler) {
            return ((ITrackedHandler) handler).extractFromInput(slot, amount, simulate);
        }
        return handler.extractItem(slot, amount, simulate);
    }

    /**
     * Handler Access
     **/
    public ITrackedHandler getInputHandler() {
        return inventories.get(SlotType.IT_IN);
    }

    public ITrackedHandler getOutputHandler() {
        return inventories.get(SlotType.IT_OUT);
    }

    public ITrackedHandler getCellInputHandler() {
        return inventories.get(SlotType.CELL_IN);
    }

    public ITrackedHandler getCellOutputHandler() {
        return inventories.get(SlotType.CELL_OUT);
    }

    public ITrackedHandler getChargeHandler() {
        return inventories.get(SlotType.ENERGY);
    }

    public ITrackedHandler getHandler(SlotType<?> type) {
        return inventories.get(type);
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

    /**
     * Gets a list of non empty input Items
     **/
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

    /**
     * Returns a non copied list of chargeable items.
     **/
    public List<IEnergyHandler> getChargeableItems() {
        List<IEnergyHandler> list = new ObjectArrayList<>();
        if (tile.isServerSide()) {
            IItemHandlerModifiable chargeables = getChargeHandler();
            for (int i = 0; i < chargeables.getSlots(); i++) {
                ItemStack item = chargeables.getStackInSlot(i);
                if (!item.isEmpty()) {
                    item.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).ifPresent(list::add);
                }
            }
        }
        return list;
    }

    /**
     * Gets a list of non empty output Items
     **/
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


    public List<ItemStack> consumeInputs(List<RecipeIngredient> items, boolean simulate) {
        if (items == null) return Collections.emptyList();
        IntSet skipSlots = new IntOpenHashSet(getInputHandler().getSlots());
        List<ItemStack> consumedItems = new ObjectArrayList<>();

        boolean success = items.stream().mapToInt(input -> {
            int failed = 0;
            ITrackedHandler wrap = getInputHandler();
            int countToReach = input.count;
            for (int i = 0; i < wrap.getSlots(); i++) {
                ItemStack item = wrap.getStackInSlot(i);
                if (input.get().test(item) && !skipSlots.contains(i)) {
                    int toConsume = Math.min(item.getCount(), Math.max(countToReach - item.getCount(), countToReach));
                    countToReach -= toConsume;
                    skipSlots.add(i);
                    ItemStack copy = item.copy();
                    copy.setCount(toConsume);
                    consumedItems.add(copy);
                    if (!input.ignoreConsume() && !simulate) wrap.extractFromInput(i, toConsume, simulate);
                    if (countToReach == 0) {
                        break;
                    }
                }
                if (i == wrap.getSlots() - 1) {
                    failed++;
                }
            }
            return failed;
        }).sum() == 0;
        //onSlotChanged should call dirty though, not sure if needed.
        if (!simulate && success) tile.markDirty();
        if (simulate) return success ? consumedItems : Collections.emptyList();
        return consumedItems;
    }

    /**
     * Consumes the inputs from the active recipe.
     *
     * @param recipe   active recipe.
     * @param simulate whether to execute or just return items.
     * @return a list of consumed items, or an empty list if it failed during simulation.
     */
    public List<ItemStack> consumeInputs(Recipe recipe, boolean simulate) {
        return consumeInputs(recipe.getInputItems(), simulate);
    }

    /**
     * Fill the output slots with @outputs items.
     *
     * @param outputs the outputs to add.
     */
    public void addOutputs(ItemStack... outputs) {
        IItemHandlerModifiable outputHandler = getOutputHandler();
        if (outputHandler == null || outputs == null || outputs.length == 0) {
            return;
        }
        for (ItemStack output : outputs) {
            for (int i = 0; i < outputHandler.getSlots(); i++) {
                output = insertIntoOutput(outputHandler, i, output.copy(), false);
                if (output.isEmpty()) {
                    break;
                }
            }
        }
    }

    /**
     * Helpers
     **/
    public boolean canOutputsFit(ItemStack[] a) {
        if (a == null) return true;
        IItemHandlerModifiable outputHandler = getOutputHandler();
        boolean[] results = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < outputHandler.getSlots(); j++) {
                results[i] |= insertIntoOutput(outputHandler, j, a[i], true).isEmpty();
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
        for (ItemStack stack : a) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack item = handler.getStackInSlot(i);
                if (item.isEmpty() || (Utils.equals(stack, item) && item.getCount() + stack.getCount() <= handler.getSlotLimit(i))) {
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
                    ItemStack result = extractFromInput(inputHandler, i, input.getCount(), false);
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
                ItemStack result = insertIntoOutput(outputHandler, j, outputs[i].copy(), false);
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

    @Override
    public LazyOptional<IItemHandler> forSide(Direction side) {
        return LazyOptional.of(() -> new SidedCombinedInvWrapper(side, tile.coverHandler.map(c -> c).orElse(null), this.inventories.values().toArray(new IItemHandlerModifiable[0])));
    }

    @Override
    public LazyOptional<? extends IItemHandler> forNullSide() {
        return LazyOptional.of(() -> new ROCombinedInvWrapper(this.inventories.values().stream().filter(t -> !(t instanceof FakeTrackedItemHandler)).toArray(IItemHandlerModifiable[]::new)));
    }

    @Override
    public void refresh() {
        Tesseract.ITEM.refreshNode(this.tile.getWorld(), this.tile.getPos().toLong());
    }
}
