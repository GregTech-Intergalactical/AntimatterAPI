package muramasa.antimatter.capability.machine;

import earth.terrarium.botarium.common.energy.base.PlatformItemEnergyManager;
import earth.terrarium.botarium.common.energy.util.EnergyHooks;
import earth.terrarium.botarium.util.Serializable;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.item.FakeTrackedItemHandler;
import muramasa.antimatter.capability.item.ITrackedHandler;
import muramasa.antimatter.capability.item.ROCombinedInvWrapper;
import muramasa.antimatter.capability.item.SidedCombinedInvWrapper;
import muramasa.antimatter.capability.item.TrackedItemHandler;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import tesseract.TesseractCapUtils;
import tesseract.api.gt.IEnergyHandlerItem;
import tesseract.api.item.ExtendedItemContainer;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static muramasa.antimatter.machine.MachineFlag.GUI;

public class MachineItemHandler<T extends TileEntityMachine<T>> implements IMachineHandler, Serializable, Dispatch.Sided<ExtendedItemContainer> {

    protected final T tile;
    protected final Object2ObjectMap<SlotType<?>, TrackedItemHandler<T>> inventories = new Object2ObjectOpenHashMap<>();

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

    public Map<SlotType<?>, ExtendedItemContainer> getAll() {
        return (Map<SlotType<?>, ExtendedItemContainer>) (Object) inventories;
    }

    @Override
    public void init() {
        ///registerNet();
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        this.inventories.forEach((f, i) -> nbt.put(f.getId(), i.serialize(new CompoundTag())));
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.inventories.forEach((f, i) -> {
            if (!nbt.contains(f.getId())) return;
            i.deserialize(nbt.getCompound(f.getId()));
        });
    }

    public T getTile() {
        return tile;
    }

    public void onUpdate() {

    }

    public List<ItemStack> getAllItems() {
        return inventories.values().stream().filter(t -> !(t instanceof FakeTrackedItemHandler)).flatMap(t -> {
            List<ItemStack> stacks = new ObjectArrayList<>(t.getContainerSize());
            for (int i = 0; i < t.getContainerSize(); i++) {
                stacks.add(t.getItem(i).copy());
            }
            return stacks.stream();
        }).collect(Collectors.toList());
    }

    public void onRemove() {

    }

    public static ItemStack insertIntoOutput(ExtendedItemContainer handler, int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (handler instanceof ITrackedHandler trackedHandler) {
            return trackedHandler.insertOutputItem(slot, stack, simulate);
        }
        return handler.insertItem(slot, stack, simulate);
    }

    public static ItemStack extractFromInput(ExtendedItemContainer handler, int slot, int amount, boolean simulate) {
        if (handler instanceof ITrackedHandler trackedHandler) {
            return trackedHandler.extractFromInput(slot, amount, simulate);
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
        return getInputHandler().getContainerSize();
    }

    public int getOutputCount() {
        return getOutputHandler().getContainerSize();
    }

    public int getCellCount() {
        return getCellInputHandler().getContainerSize();
    }

    @Nonnull
    public ItemStack[] getInputs() {
        return getInputList().toArray(new ItemStack[0]);
    }

    public ItemStack[] getOutputs() {
        return getOutputList().toArray(new ItemStack[0]);
    }

    public ItemStack getCellInput() {
        return getCellInputHandler().getItem(0);
    }

    public ItemStack getCellOutput() {
        return getCellInputHandler().getItem(1);
    }

    /**
     * Gets a list of non empty input Items
     **/
    public List<ItemStack> getInputList() {
        List<ItemStack> list = new ObjectArrayList<>();
        ExtendedItemContainer inputs = getInputHandler();
        for (int i = 0; i < inputs.getContainerSize(); i++) {
            if (!inputs.getItem(i).isEmpty()) {
                list.add(inputs.getItem(i).copy());
            }
        }
        return list;
    }

    /**
     * Returns a non-copied list of chargeable items.
     **/
    public List<Pair<ItemStack, IEnergyHandlerItem>> getChargeableItems() {
        List<Pair<ItemStack, IEnergyHandlerItem>> list = new ObjectArrayList<>();
        if (tile.isServerSide()) {
            ExtendedItemContainer chargeables = getChargeHandler();
            for (int i = 0; i < chargeables.getContainerSize(); i++) {
                ItemStack item = chargeables.getItem(i);
                if (!item.isEmpty()) {
                    TesseractCapUtils.getWrappedEnergyHandlerItem(item).ifPresent(e -> list.add(new ObjectObjectImmutablePair<>(item, e)));
                }
            }
        }
        return list;
    }

    public List<Pair<ItemStack, PlatformItemEnergyManager>> getRFChargeableItems() {
        List<Pair<ItemStack, PlatformItemEnergyManager>> list = new ObjectArrayList<>();
        if (tile.isServerSide()) {
            ExtendedItemContainer chargeables = getChargeHandler();
            for (int i = 0; i < chargeables.getContainerSize(); i++) {
                ItemStack item = chargeables.getItem(i);
                if (!item.isEmpty() && EnergyHooks.isEnergyItem(item)) {
                    list.add(new ObjectObjectImmutablePair<>(item, EnergyHooks.getItemEnergyManager(item)));
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
        ExtendedItemContainer outputs = getOutputHandler();
        for (int i = 0; i < outputs.getContainerSize(); i++) {
            ItemStack slot = outputs.getItem(i);
            if (!slot.isEmpty()) {
                list.add(slot.copy());
            }
        }
        return list;
    }


    public List<ItemStack> consumeInputs(List<Ingredient> items, boolean simulate) {
        if (items == null) return Collections.emptyList();
        IntSet skipSlots = new IntOpenHashSet(getInputHandler().getContainerSize());
        List<ItemStack> consumedItems = new ObjectArrayList<>();

        boolean success = items.stream().mapToInt(input -> {
            int failed = 0;
            ITrackedHandler wrap = getInputHandler();
            int countToReach = RecipeIngredient.count(input);
            for (int i = 0; i < wrap.getContainerSize(); i++) {
                ItemStack item = wrap.getItem(i);
                if (input.test(item) && !skipSlots.contains(i)) {
                    int toConsume = Math.min(item.getCount(), Math.max(countToReach - item.getCount(), countToReach));
                    countToReach -= toConsume;
                    skipSlots.add(i);
                    ItemStack copy = item.copy();
                    copy.setCount(toConsume);
                    consumedItems.add(copy);
                    if (!RecipeIngredient.ignoreConsume(input) && !simulate) wrap.extractFromInput(i, toConsume, simulate);
                    if (countToReach == 0) {
                        break;
                    }
                }
                if (i == wrap.getContainerSize() - 1) {
                    failed++;
                }
            }
            return failed;
        }).sum() == 0;
        //onSlotChanged should call dirty though, not sure if needed.
        if (!simulate && success) tile.setChanged();
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
    public List<ItemStack> consumeInputs(IRecipe recipe, boolean simulate) {
        return consumeInputs(recipe.getInputItems(), simulate);
    }

    /**
     * Fill the output slots with @outputs items.
     *
     * @param outputs the outputs to add.
     */
    public void addOutputs(ItemStack... outputs) {
        ExtendedItemContainer outputHandler = getOutputHandler();
        if (outputHandler == null || outputs == null || outputs.length == 0) {
            return;
        }
        for (ItemStack output : outputs) {
            for (int i = 0; i < outputHandler.getContainerSize(); i++) {
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
        ExtendedItemContainer outputHandler = getOutputHandler();
        boolean[] results = new boolean[a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < outputHandler.getContainerSize(); j++) {
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
        ExtendedItemContainer handler = getOutputHandler();
        if (!(handler instanceof TrackedItemHandler)) {
            return 0;
        }
        for (ItemStack stack : a) {
            for (int i = 0; i < handler.getContainerSize(); i++) {
                ItemStack item = handler.getItem(i);
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
        ExtendedItemContainer inputHandler = getInputHandler();
        for (ItemStack input : inputs) {
            for (int i = 0; i < inputHandler.getContainerSize(); i++) {
                if (Utils.equals(input, inputHandler.getItem(i))) {
                    ItemStack result = extractFromInput(inputHandler, i, input.getCount(), false);
                    if (!result.isEmpty()) {
                        if (result.getCount() == input.getCount()) {
                            break;
                        } else {
                            notConsumed.add(Utils.ca(input.getCount() - result.getCount(), input));
                        }
                    }
                } else if (i == inputHandler.getContainerSize() - 1) {
                    notConsumed.add(input);
                }
            }
        }
        return notConsumed.toArray(new ItemStack[0]);
    }

    public ItemStack[] exportAndReturnOutputs(ItemStack... outputs) {
        List<ItemStack> notExported = new ObjectArrayList<>();
        ExtendedItemContainer outputHandler = getOutputHandler();
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputHandler.getContainerSize(); j++) {
                ItemStack result = insertIntoOutput(outputHandler, j, outputs[i].copy(), false);
                if (result.isEmpty()) {
                    break;
                } else {
                    outputs[i] = result;
                }
                if (j == outputHandler.getContainerSize() - 1) {
                    notExported.add(result);
                }
            }
        }
        return notExported.toArray(new ItemStack[0]);
    }

    @Override
    public Optional<ExtendedItemContainer> forSide(Direction side) {
        return Optional.of(new SidedCombinedInvWrapper(side, tile.coverHandler.map(c -> c).orElse(null), this.inventories.values().stream().filter(t -> !(t instanceof FakeTrackedItemHandler)).toArray(ExtendedItemContainer[]::new)));
    }

    @Override
    public Optional<? extends ExtendedItemContainer> forNullSide() {
        return Optional.of(new ROCombinedInvWrapper(this.inventories.values().stream().filter(t -> !(t instanceof FakeTrackedItemHandler)).toArray(ExtendedItemContainer[]::new)));
    }
}
