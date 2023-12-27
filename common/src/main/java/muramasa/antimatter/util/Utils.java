package muramasa.antimatter.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.energy.base.PlatformEnergyManager;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.PlatformFluidHandler;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityBase;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.ToolUtils;
import muramasa.antimatter.tool.behaviour.BehaviourTreeFelling;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tesseract.TesseractGraphWrappers;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.heat.IHeatHandler;
import tesseract.api.item.PlatformItemHandler;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.minecraft.advancements.critereon.MinMaxBounds.Ints.ANY;

public class Utils {

    private static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getInstance(Locale.US);
    private static final DecimalFormatSymbols DECIMAL_SYMBOLS = DECIMAL_FORMAT.getDecimalFormatSymbols();

    static {
        DECIMAL_SYMBOLS.setGroupingSeparator(' ');
    }

    /**
     * Returns true of A is not empty, has the same Item and damage is equal to B
     **/
    public static boolean equals(ItemStack a, ItemStack b) {
        return a.sameItem(b);
    }

    /**
     * Returns true of A has the same Fluid as B
     **/
    public static boolean equals(FluidHolder a, FluidHolder b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        return a.getFluid() == b.getFluid() && a.getCompound() == b.getCompound();
    }

    /**
     * Returns true if A equals() B and A amount >= B amount
     **/
    public static boolean contains(ItemStack a, ItemStack b) {
        return equals(a, b) && a.getCount() >= b.getCount();
    }

    /**
     * Returns true if A equals() B and A amount >= B amount
     **/
    public static boolean contains(FluidHolder a, FluidHolder b) {
        return equals(a, b) && a.getFluidAmount() >= b.getFluidAmount();
    }

    /**
     * Returns the index of an item in a list, or -1 if not found
     **/
    public static int contains(List<ItemStack> list, ItemStack item) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (equals(list.get(i), item)) return i;
        }
        return -1;
    }

    public static Direction dirFromState(BlockState state) {
        if (state.hasProperty(BlockStateProperties.FACING)) return state.getValue(BlockStateProperties.FACING);
        return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public static ItemStack extractAny(PlatformItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.extractItem(i, 64, false);
            if (!stack.isEmpty()) return stack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns the index of a fluid in a list, or -1 if not found
     **/
    public static int contains(List<FluidHolder> list, FluidHolder fluid) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (equals(list.get(i), fluid)) return i;
        }
        return -1;
    }

    /**
     * Merges B into A
     **/
    public static List<ItemStack> mergeItems(List<ItemStack> a, List<ItemStack> b) {
        int position, size = b.size();
        for (ItemStack stack : b) {
            if (stack.isEmpty()) continue;
            position = contains(a, stack);
            if (position == -1) a.add(stack);
            else a.get(position).grow(stack.getCount());
        }
        return splitStacks(a);
    }

    public static void tryCondenseInventory(PlatformItemHandler itemHandler){
        tryCondenseInventory(itemHandler, 0, itemHandler.getSlots());
    }

    public static void tryCondenseInventory(PlatformItemHandler tile, int startSlot, int endSlot) {
        for (int i = startSlot; i < endSlot; ++i) {
            for (int j = startSlot; j < endSlot; ++j) {
                if (i == j) {
                    continue;
                }
                ItemStack stack1 = tile.getStackInSlot(i);
                ItemStack stack2 = tile.getStackInSlot(j);
                if (!stack1.isEmpty() && !stack2.isEmpty()
                        && (Utils.equals(stack1, stack2) && stack1.getCount() < stack1.getMaxStackSize())) {
                    int max = stack1.getMaxStackSize() - stack1.getCount();
                    int available = stack2.getCount();
                    int size = Mth.clamp(available, 1, max);
                    stack1.grow(size);
                    stack2.shrink(size);
                }
                if (stack2.isEmpty() && !stack1.isEmpty() && j < i) {
                    tile.setStackInSlot(j, stack1.copy());
                    tile.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
    }

    public static List<ItemStack> splitStacks(List<ItemStack> stacks){
        List<ItemStack> returned = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (stack.getCount() > stack.getMaxStackSize()){
                int left = stack.getCount();
                while (left > 0){
                    ItemStack toAdd = Utils.ca(Math.min(stack.getMaxStackSize(), left), stack);
                    left -= toAdd.getCount();
                    returned.add(toAdd);
                }
            } else {
                returned.add(stack);
            }
        }
        return returned;
    }

    /**
     * Merges two Lists of FluidHolders, ignoring max amount
     **/
    public static List<FluidHolder> mergeFluids(List<FluidHolder> a, List<FluidHolder> b) {
        int position, size = b.size();
        for (FluidHolder stack : b) {
            if (stack == null) continue;
            position = contains(a, stack);
            if (position == -1) a.add(stack);
            else a.get(position).setAmount(a.get(position).getFluidAmount() + stack.getFluidAmount());
        }
        return a;
    }

    public static ItemStack ca(int amount, ItemStack toCopy) {
        ItemStack stack = toCopy.copy();
        stack.setCount(amount);
        return stack;
    }

    public static FluidHolder ca(long amount, FluidHolder toCopy) {
        FluidHolder stack = toCopy.copyHolder();
        stack.setAmount(amount);
        return stack;
    }

    public static void damageStack(ItemStack stack, LivingEntity player) {
        int durability = 1;
        if (stack.getItem() instanceof IAntimatterTool) {
            durability = ((IAntimatterTool) stack.getItem()).getAntimatterToolType().getUseDurability();
        }
        damageStack(durability, stack, player);
    }


    public static void damageStack(ItemStack stack, InteractionHand hand, LivingEntity player) {
        int durability = 1;
        if (stack.getItem() instanceof IAntimatterTool) {
            durability = ((IAntimatterTool) stack.getItem()).getAntimatterToolType().getUseDurability();
        }
        stack.hurtAndBreak(durability, player, p -> {
            p.broadcastBreakEvent(hand);
        });
    }


    public static void damageStack(int durability, ItemStack stack, LivingEntity player) {
        stack.hurtAndBreak(durability, player, p -> {
            p.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
    }

    public static ItemStack mul(int amount, ItemStack stack) {
        return ca(stack.getCount() * amount, stack);
    }

    public static FluidHolder mul(long amount, FluidHolder stack) {
        return ca(stack.getFluidAmount() * amount, stack);
    }

    public static boolean hasNoConsumeTag(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(Ref.KEY_STACK_NO_CONSUME);
    }

    public static boolean hasNoConsumeTag(FluidHolder stack) {
        return stack.getCompound() != null && stack.getCompound().contains(Ref.KEY_STACK_NO_CONSUME);
    }

    public static boolean hasIgnoreNbtTag(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(Ref.KEY_STACK_IGNORE_NBT);
    }


    public static boolean getNoConsumeTag(ItemStack stack) {
        return stack.getTag().getBoolean(Ref.KEY_STACK_NO_CONSUME);
    }

    public static boolean getNoConsumeTag(FluidHolder stack) {
        return stack.getCompound().getBoolean(Ref.KEY_STACK_NO_CONSUME);
    }

    public static ItemStack addNoConsumeTag(ItemStack stack) {
        validateNBT(stack).getTag().putBoolean(Ref.KEY_STACK_NO_CONSUME, true);
        return stack;
    }

    public static FluidHolder addNoConsumeTag(FluidHolder stack) {
        validateNBT(stack).getCompound().putBoolean(Ref.KEY_STACK_NO_CONSUME, true);
        return stack;
    }

    public static ItemStack validateNBT(ItemStack stack) {
        if (!stack.hasTag()) stack.setTag(new CompoundTag());
        return stack;
    }

    public static FluidHolder validateNBT(FluidHolder stack) {
        if (stack.getCompound() == null) stack.setCompound(new CompoundTag());
        return stack;
    }

    public static boolean areItemsValid(ItemStack... items) {
        if (items == null || items.length == 0) return false;
        for (ItemStack item : items) {
            if (item.isEmpty()) return false;
        }
        return true;
    }

    public static boolean areItemsValid(ItemStack[]... itemArrays) {
        for (ItemStack[] itemArray : itemArrays) {
            if (!areItemsValid(itemArray)) return false;
        }
        return true;
    }

    public static boolean areFluidsValid(FluidHolder... fluids) {
        if (fluids == null || fluids.length == 0) return false;
        for (FluidHolder fluid : fluids) {
            if (fluid.getFluid() == Fluids.EMPTY) return false;
        }
        return true;
    }

    public static boolean areFluidsValid(FluidIngredient... fluids) {
        if (fluids == null || fluids.length == 0) return false;
        for (FluidIngredient fluid : fluids) {
            if (!areFluidsValid(fluid.getStacks())) return false;
        }
        return true;
    }


    public static boolean areFluidsValid(FluidHolder[]... fluidArrays) {
        for (FluidHolder[] fluidArray : fluidArrays) {
            if (!areFluidsValid(fluidArray)) return false;
        }
        return true;
    }

    public static boolean doItemsMatchAndSizeValid(ItemStack[] a, ItemStack[] b) {
        if (a == null || b == null) return false;
        int matchCount = 0;
        for (ItemStack stack : a) {
            for (ItemStack itemStack : b) {
                if (contains(itemStack, stack)) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= a.length;
    }

    public static boolean doItemsMatchAndSizeValid(List<Ingredient> a, ItemStack[] b) {
        if (a == null || b == null) return false;
        int matchCount = 0;
        for (Ingredient stack : a) {
            for (ItemStack itemStack : b) {
                if (stack.test(itemStack)) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= a.size();
    }

    public static boolean doFluidsMatchAndSizeValid(FluidHolder[] a, FluidHolder[] b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        int matchCount = 0;
        for (FluidHolder fluidStack : a) {
            for (FluidHolder stack : b) {
                if (contains(stack, fluidStack)) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= a.length;
    }

    public static boolean transferItems(PlatformItemHandler from, PlatformItemHandler to, boolean once) {
        return transferItems(from, to, once, stack -> true);
    }

    public static boolean transferItems(PlatformItemHandler from, PlatformItemHandler to, boolean once, Predicate<ItemStack> filter) {
        boolean successful = false;
        for (int i = 0; i < from.getSlots(); i++) {
            ItemStack toInsert = from.extractItem(i, from.getStackInSlot(i).getCount(), true);
            if (toInsert.isEmpty() || !filter.test(toInsert)) {
                continue;
            }
            ItemStack inserted = insertItem(to, toInsert, true);
            if (inserted.isEmpty()){
                insertItem(to, toInsert, false);
                from.extractItem(i, toInsert.getCount(), false);
                if (!successful) successful = true;
                if (once) break;
            } else if (inserted.getCount() < toInsert.getCount()) {
                int actual = toInsert.getCount() - inserted.getCount();
                toInsert.setCount(toInsert.getCount() - inserted.getCount());
                insertItem(to, toInsert, false);
                from.extractItem(i, actual, false);
                if (!successful) successful = true;
                if (once) break;
            }
        }
        return successful;
    }

    public static ItemStack insertItem(PlatformItemHandler to, ItemStack stack, boolean simulate){
        if (to == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < to.getSlots(); i++)
        {
            stack = to.insertItem(i, stack, simulate);
            if (stack.isEmpty())
            {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }

    /**
     * Transfers up to maxAmps between energy handlers, without loss.
     *
     * @param from the handler to extract from
     * @param to   the handler to insert
     * @return if energy was inserted
     */
    public static boolean transferEnergy(IEnergyHandler from, IEnergyHandler to) {
        boolean transferred = false;
        for (long amp = 0; amp < from.availableAmpsOutput(); amp++) {
            long extracted = from.extractEu(from.getOutputVoltage(), true);
            if (extracted > 0){
                long insertEu = to.insertEu(extracted, true);
                if (insertEu > 0){
                    from.extractEu(to.insertEu(extracted, false), false);
                    transferred = true;
                }
            }
        }
        return transferred;
    }

    public static boolean transferEnergy(PlatformEnergyManager from, PlatformEnergyManager to) {
        long extracted = from.extract(Long.MAX_VALUE, true);
        if (extracted > 0) {
            long inserted = to.insert(extracted, false);
            if (inserted > 0) {
                from.extract(inserted, false);
                return true;
            }
        }
        return false;
    }

    public static boolean transferEnergy(PlatformEnergyManager from, EnergyContainer to) {
        long extracted = from.extract(Long.MAX_VALUE, true);
        if (extracted > 0) {
            long inserted = to.insertEnergy(extracted, false);
            if (inserted > 0) {
                from.extract(inserted, false);
                return true;
            }
        }
        return false;
    }

    public static boolean transferEnergy(EnergyContainer from, PlatformEnergyManager to) {
        long extracted = from.extractEnergy(Long.MAX_VALUE, true);
        if (extracted > 0) {
            long inserted = to.insert(extracted, false);
            if (inserted > 0) {
                from.extractEnergy(inserted, false);
                return true;
            }
        }
        return false;
    }

    public static boolean transferHeat(IHeatHandler from, IHeatHandler to) {
        int extracted = from.extract(Integer.MAX_VALUE, true);
        if (extracted > 0) {
            int inserted = to.insert(extracted, false);
            if (inserted > 0) {
                from.extract(inserted, false);
                return true;
            }
        }
        return false;
    }

    public static boolean addEnergy(IEnergyHandler to, long eu) {
        return to.insertEu(eu, false) > 0;
    }

    /**
     * Transfer energy with loss.
     *
     * @param from energy handler to extract from
     * @param to   energy handler to insert from
     * @param loss energy loss
     * @return number of amps
     */
    public static boolean transferEnergyWithLoss(IEnergyHandler from, IEnergyHandler to, int loss) {
        boolean transferred = false;
        for (long amp = 0; amp < from.availableAmpsOutput(); amp++) {
            long extracted = from.extractEu(from.getOutputVoltage(), true);
            if (extracted > 0){
                long insertEu = to.insertEu(extracted - loss, true);
                if (insertEu > 0){
                    from.extractEu(to.insertEu(extracted - loss, false) + loss, false);
                    transferred = true;
                }
            }
        }
        return transferred;
    }

    public static boolean transferFluids(PlatformFluidHandler from, PlatformFluidHandler to, int cap, Predicate<FluidHolder> filter) {
        boolean successful = false;
        for (int i = 0; i < to.getTankAmount(); i++) {
            //if (i >= from.getTanks()) break;
            FluidHolder toInsert;
            for (int j = 0; j < from.getTankAmount(); j++) {
                if (cap > 0) {
                    FluidHolder fluid = from.getFluidInTank(j);
                    if (fluid.isEmpty() || !filter.test(fluid)) {
                        continue;
                    }
                    fluid = fluid.copyHolder();
                    long toDrain = Math.min(cap * TesseractGraphWrappers.dropletMultiplier, fluid.getFluidAmount());
                    fluid.setAmount(toDrain);
                    toInsert = from.extractFluid(fluid, true);
                } else {
                    toInsert = from.extractFluid(from.getFluidInTank(j), true);
                }
                long filled = to.insertFluid(toInsert, true);
                if (filled > 0) {
                    toInsert.setAmount(filled);
                    to.insertFluid(from.extractFluid(toInsert, false), false);
                    successful = true;
                }
            }
        }
        return successful;
    }

    private static final Consumer<?> SINK = a -> {};
    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> sink() {
        return (Consumer<T>) SINK;
    }

    public static boolean transferFluids(PlatformFluidHandler from, PlatformFluidHandler to, int cap) {
        return transferFluids(from, to, cap, fluidStack -> true);
    }

    public static void entitiesAround(Level level, BlockPos pos, BiConsumer<Direction, BlockEntity> cb) {
        int3 mutPos = new int3();
        for (Direction dir : Ref.DIRS) {
            mutPos.set(pos);
            mutPos = mutPos.offset(1,dir);
            BlockEntity ent = level.getBlockEntity(mutPos);
            if (ent == null) continue;
            cb.accept(dir, ent);
        }
    }

    public static boolean transferFluids(PlatformFluidHandler from, PlatformFluidHandler to) {
        return transferFluids(from, to, -1);
    }

    /**
     * Creates a new {@link EnterBlockTrigger} for use with recipe unlock criteria.
     */
    public static EnterBlockTrigger.TriggerInstance enteredBlock(Block blockIn) {
        return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, blockIn, StatePropertiesPredicate.ANY);
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    public static InventoryChangeTrigger.TriggerInstance hasItem(ItemLike itemIn) {
        return hasItem(ItemPredicate.Builder.item().of(itemIn).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having an item within the given tag.
     */
    public static InventoryChangeTrigger.TriggerInstance hasItem(TagKey<Item> tagIn) {
        return hasItem(ItemPredicate.Builder.item().of(tagIn).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    public static InventoryChangeTrigger.TriggerInstance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, ANY, ANY, ANY, predicates);
    }

//    @Nullable
//    public static Fluid getFluidById(int id) {
//        for (Map.Entry<Fluid, Integer> entry : FluidRegistry.getRegisteredFluidIDs().entrySet()) {
//            if (entry.getValue() == id) return entry.getKey();
//        }
//        return null;
//    }
//
//    public static int getIdByFluid(Fluid fluid) {
//
//        return FluidRegistry.getRegisteredFluidIDs().get(fluid);
//    }

    public static MutableComponent translatable(String key, Object... objects){
        return new TranslatableComponent(key, objects);
    }

    public static MutableComponent literal(String text){
        return new TextComponent(text);
    }

    /**
     * https://stackoverflow.com/a/1308407
     **/
    public static long getNumberOfDigits(long n, boolean in10s) {
        if (n < 10000L) {
            if (n < 100L) {
                if (n < 10L) return 1;
                else return in10s ? 10 : 2;
            } else {
                if (n < 1000L) return in10s ? 100 : 3;
                else return in10s ? 1000 : 4;
            }
        } else {
            if (n < 1000000000000L) {
                if (n < 100000000L) {
                    if (n < 1000000L) {
                        if (n < 100000L) return in10s ? 10000 : 5;
                        else return in10s ? 100000 : 6;
                    } else {
                        if (n < 10000000L) return in10s ? 1000000 : 7;
                        else return in10s ? 10000000 : 8;
                    }
                } else {
                    if (n < 10000000000L) {
                        if (n < 1000000000L) return in10s ? 100000000 : 9;
                        else return in10s ? 1000000000 : 10;
                    } else {
                        if (n < 100000000000L) return in10s ? 10000000000L : 11;
                        else return in10s ? 100000000000L : 12;
                    }
                }
            } else {
                if (n < 10000000000000000L) {
                    if (n < 100000000000000L) {
                        if (n < 10000000000000L) return in10s ? 1000000000000L : 13;
                        else return in10s ? 10000000000000L : 14;
                    } else {
                        if (n < 1000000000000000L) return in10s ? 100000000000000L : 15;
                        else return in10s ? 1000000000000000L : 16;
                    }
                } else {
                    if (n < 1000000000000000000L) {
                        if (n < 100000000000000000L) return in10s ? 10000000000000000L : 17;
                        else return in10s ? 100000000000000000L : 18;
                    } else return in10s ? 1000000000000000000L : 19;
                }
            }
        }
    }

    public static String formatNumber(long number) {
        return DECIMAL_FORMAT.format(number);
    }

    public static int getVoltageTier(long voltage) {
        int tier = 0;
        for (int i = 0; i < Ref.V.length; i++) {
            if (voltage <= Ref.V[i]) {
                tier = i;
                break;
            }
        }
        return Math.max(1, tier);
    }

    /**
     * Safe version of world.getTileEntity
     **/
    @Nullable
    public static BlockEntity getTile(@Nullable BlockGetter reader, BlockPos pos) {
        if (reader == null) return null;
        return reader.getBlockEntity(pos);
        //TODO validate and redo
//        if (pos == null || blockAccess == null) return null;
//        if (blockAccess instanceof ChunkRenderCache) {
//            return ((ChunkRenderCache) blockAccess).getTileEntity(pos, Chunk.CreateEntityType.CHECK);
//        } else {
//            return blockAccess.getTileEntity(pos);
//        }
    }

    public static boolean isForeignTile(@Nullable BlockEntity target) {
        return target != null && !(target instanceof BlockEntityBase);
    }

    @Nullable
    public static BlockEntity getTileFromBuf(FriendlyByteBuf buf) {
        return unsafeRunForDist(() -> () -> Antimatter.PROXY.getClientWorld().getBlockEntity(buf.readBlockPos()), () -> () -> {
            throw new RuntimeException("Shouldn't be called on server!");
        });
    }

    public static <T> T unsafeRunForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return switch (AntimatterAPI.getSIDE()) {
            case CLIENT -> clientTarget.get().get();
            case SERVER -> serverTarget.get().get();
        };
    }

    public static void unsafeRunForDistVoid(Supplier<Runnable> clientTarget, Supplier<Runnable> serverTarget) {
        switch (AntimatterAPI.getSIDE()) {
            case CLIENT -> clientTarget.get().run();
            case SERVER -> serverTarget.get().run();
        };
    }

    /**
     * Syncs NBT between Client & Server
     **/
    public static void markTileForNBTSync(BlockEntity tile) {
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        tile.getLevel().sendBlockUpdated(tile.getBlockPos(), state, state, 3);
    }

    /**
     * Sends block update to clients
     **/
    public static void markTileForRenderUpdate(BlockEntity tile) {
        BlockState state = tile.getLevel().getBlockState(tile.getBlockPos());
        if (tile.getLevel().isClientSide) {
            tile.getLevel().sendBlockUpdated(tile.getBlockPos(), state, state, 11);
            requestModelDataRefresh(tile);
        }
    }

    @ExpectPlatform
    public static void requestModelDataRefresh(BlockEntity tile){
        throw new AssertionError();
    }

    private static final Direction[][] TRANSFORM = new Direction[][]{
            new Direction[]{
                    Direction.SOUTH,
                    Direction.NORTH,
                    Direction.DOWN,
                    Direction.UP,
                    Direction.WEST,
                    Direction.EAST
            },
            new Direction[]{
                    Direction.NORTH,
                    Direction.SOUTH,
                    Direction.UP,
                    Direction.DOWN,
                    Direction.WEST,
                    Direction.EAST
            },
            new Direction[]{
                    Direction.DOWN,
                    Direction.UP,
                    Direction.NORTH,
                    Direction.SOUTH,
                    Direction.WEST,
                    Direction.EAST
            },
            new Direction[]{
                    Direction.DOWN,
                    Direction.UP,
                    Direction.SOUTH,
                    Direction.NORTH,
                    Direction.EAST,
                    Direction.WEST,
            },
            new Direction[]{
                    Direction.DOWN,
                    Direction.UP,
                    Direction.WEST,
                    Direction.EAST,
                    Direction.SOUTH,
                    Direction.NORTH
            },
            new Direction[]{
                    Direction.DOWN,
                    Direction.UP,
                    Direction.EAST,
                    Direction.WEST,
                    Direction.NORTH,
                    Direction.SOUTH
            }
    };

    public static Direction rotate(Direction facing, Direction side) {
        return TRANSFORM[facing.get3DDataValue()][side.get3DDataValue()];
    }

    public static Direction coverRotateFacing(Direction toRotate, Direction rotateBy) {
        Quaternion rot = null;
        switch (rotateBy.getAxis()) {
            case Z:
            case X:
                rot = new Quaternion(Vector3f.YP, rotateBy.toYRot(), true);
                break;
            case Y:
                rot = new Quaternion(Vector3f.XP, -90f*rotateBy.getNormal().getY(), true);
                break;

        }
        Vec3i vector3i = toRotate.getNormal();
        Vector4f vector4f = new Vector4f((float) vector3i.getX(), (float) vector3i.getY(), (float) vector3i.getZ(), 0.0F);
        vector4f.transform(new com.mojang.math.Matrix4f(rot));
        return Direction.getNearest(vector4f.x(), vector4f.y(), vector4f.z());
    }

    public static Direction getOffsetFacing(BlockPos center, BlockPos offset) {
        if (center.getX() == offset.getX() + 1) return Direction.WEST;
        else if (center.getX() + 1 == offset.getX()) return Direction.EAST;
        else if (center.getZ() == offset.getZ() + 1) return Direction.NORTH;
        else if (center.getZ() + 1 == offset.getZ()) return Direction.SOUTH;
        else if (center.getY() == offset.getY() + 1) return Direction.DOWN;
        else if (center.getY() + 1 == offset.getY()) return Direction.UP;
        else return null;
    }

    final static double INTERACTION_OFFSET = 0.25;

    public static Direction getInteractSide(BlockHitResult res) {
        Vec3 vec = res.getLocation();
        return getInteractSide(res.getDirection(), (float) vec.x - res.getBlockPos().getX(), (float) vec.y - res.getBlockPos().getY(), (float) vec.z - res.getBlockPos().getZ());
    }

    public static Direction getInteractSide(Direction side, float x, float y, float z) {
        Direction backSide = side.getOpposite();
        switch (side.get3DDataValue()) {
            case 0:
            case 1:
                if (x < INTERACTION_OFFSET) {
                    if (z < INTERACTION_OFFSET) return backSide;
                    if (z > 1 - INTERACTION_OFFSET) return backSide;
                    return Direction.WEST;
                }
                if (x > 1 - INTERACTION_OFFSET) {
                    if (z < INTERACTION_OFFSET) return backSide;
                    if (z > 1 - INTERACTION_OFFSET) return backSide;
                    return Direction.EAST;
                }
                if (z < INTERACTION_OFFSET) return Direction.NORTH;
                if (z > 1 - INTERACTION_OFFSET) return Direction.SOUTH;
                return side;
            case 2:
            case 3:
                if (x < INTERACTION_OFFSET) {
                    if (y < INTERACTION_OFFSET) return backSide;
                    if (y > 1 - INTERACTION_OFFSET) return backSide;
                    return Direction.WEST;
                }
                if (x > 1 - INTERACTION_OFFSET) {
                    if (y < INTERACTION_OFFSET) return backSide;
                    if (y > 1 - INTERACTION_OFFSET) return backSide;
                    return Direction.EAST;
                }
                if (y < INTERACTION_OFFSET) return Direction.DOWN;
                if (y > 1 - INTERACTION_OFFSET) return Direction.UP;
                return side;
            case 4:
            case 5:
                if (z < INTERACTION_OFFSET) {
                    if (y < INTERACTION_OFFSET) return backSide;
                    if (y > 1 - INTERACTION_OFFSET) return backSide;
                    return Direction.NORTH;
                }
                if (z > 1 - INTERACTION_OFFSET) {
                    if (y < INTERACTION_OFFSET) return backSide;
                    if (y > 1 - INTERACTION_OFFSET) return backSide;
                    return Direction.SOUTH;
                }
                if (y < INTERACTION_OFFSET) return Direction.DOWN;
                if (y > 1 - INTERACTION_OFFSET) return Direction.UP;
                return side;
        }
        return side;
    }

    public static Set<BlockPos> getCubicPosArea(int3 area, Direction side, BlockPos origin, Player player, boolean excludeAir) {
        int xRadius, yRadius, zRadius;
        BlockPos center;

        if (side == null) {
            center = origin;
            xRadius = area.getX();
            yRadius = area.getY();
            zRadius = area.getZ();
        } else {
            center = origin.relative(side.getOpposite(), area.getZ());
            if (side.getAxis() == Direction.Axis.Y) {
                xRadius = player.getDirection().getAxis() == Direction.Axis.X ? area.getY() : area.getX();
                yRadius = area.getZ();
                zRadius = player.getDirection().getAxis() == Direction.Axis.Z ? area.getY() : area.getX();
            } else {
                xRadius = player.getDirection().getAxis() == Direction.Axis.X ? area.getZ() : area.getX();
                yRadius = area.getY();
                zRadius = player.getDirection().getAxis() == Direction.Axis.Z ? area.getZ() : area.getX();
            }
        }

        Set<BlockPos> set = new ObjectOpenHashSet<>();
        BlockState state;
        for (int x = center.getX() - xRadius; x <= center.getX() + xRadius; x++) {
            for (int y = center.getY() - yRadius; y <= center.getY() + yRadius; y++) {
                for (int z = center.getZ() - zRadius; z <= center.getZ() + zRadius; z++) {
                    BlockPos harvestPos = new BlockPos(x, y, z);
                    if (harvestPos.equals(origin)) continue;
                    if (excludeAir) {
                        state = player.level.getBlockState(harvestPos);
                        if (state.isAir()) continue;
                    }
                    set.add(new BlockPos(x, y, z));
                }
            }
        }
        return set;
    }
   
    public static void createExplosion(@Nullable Level world, BlockPos pos, float explosionRadius, Explosion.BlockInteraction modeIn) {
        if (world != null) {
            if (!world.isClientSide) {
                world.explode(null, pos.getX(), pos.getY() + 0.0625D, pos.getZ(), explosionRadius, modeIn);
            } else {
                world.addParticle(ParticleTypes.SMOKE, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 0.0D, 0.0D, 0.0D);
            }
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    public static void createFireAround(@Nullable Level world, BlockPos pos) {
        if (world != null) {
            boolean fired = false;
            for (Direction side : Ref.DIRS) {
                BlockPos offset = pos.relative(side);
                if (world.getBlockState(offset) == Blocks.AIR.defaultBlockState()) {
                    world.setBlockAndUpdate(offset, Blocks.FIRE.defaultBlockState());
                    fired = true;
                }
            }
            if (!fired) world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
        }
    }

    /**
     * Custom Block Breaking implementation, normally used when breaking extra blocks during/after onBlockDestroyed
     *
     * @param world  World instance
     * @param player Player instance, preferably not a ClientPlayerEntity as BlockBreakEvent won't be fired
     * @param stack  Player's heldItemStack
     * @param pos    BlockPos of the block that is about to be destroyed
     * @param damage Damage that should be taken for the ItemStack
     * @return true if block is successfully broken, false if not
     */
    public static boolean breakBlock(Level world, @Nullable Player player, ItemStack stack, BlockPos pos, int damage) {
        if (world.isClientSide) return false;
        BlockState state = world.getBlockState(pos);
        ServerPlayer serverPlayer = player == null ? null : ((ServerPlayer) player);
        int exp = player == null ? -1 : onBlockBreakEvent(world, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer, pos);
        FluidState fluidState = world.getFluidState(pos);
        boolean destroyed = world.setBlockAndUpdate(pos, fluidState.createLegacyBlock());// world.destroyBlock(pos, !player.isCreative(), player);
        if (destroyed) {
            if (player != null && canHarvestBlock(state, world, pos, player)) {
                state.getBlock().playerDestroy(world, player, pos, state, world.getBlockEntity(pos), stack);
            }
            stack.hurtAndBreak(state.getDestroySpeed(world, pos) != 0.0F ? damage : 0, player, (onBroken) -> onBroken.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        if (exp > 0) popExperience(state.getBlock(), (ServerLevel) world, pos, exp);
        return destroyed;
    }

    @ExpectPlatform
    private static void popExperience(Block block, ServerLevel level, BlockPos pos, int exp){
    }

    @ExpectPlatform
    public static boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int onBlockBreakEvent(Level world, GameType gameType, ServerPlayer player, BlockPos pos){
        throw new AssertionError();
    }

    private static boolean LOCK;

    /**
     * Performs tree logging. If Configs.GAMEPLAY.TREE_DETECTION is true, it will do a more complex search for branches, if set to false, it will do a normal vertical loop only
     *
     * @param stack  Player's heldItem
     * @param start  onBlockDestroy's BlockPos
     * @param player ServerPlayerEntity instance
     * @param world  World instance
     * @return if tree logging was successful
     */
    public static boolean treeLogging(@NotNull IAntimatterTool tool, @NotNull ItemStack stack, @NotNull BlockPos start, @NotNull Player player, @NotNull Level world) {
        boolean[] harvested = new boolean[1];
        if (!AntimatterConfig.SMARTER_TREE_DETECTION.get()) {
            BlockState tpCompare = world.getBlockState(start);
            if (!BehaviourTreeFelling.isLog(tpCompare)) return false;
            for (int y = start.getY() + 1; y < start.getY() + world.getHeight(); y++) {
                if (stack.isEmpty()) break;
                BlockPos tempPos = new BlockPos(start.getX(), y, start.getZ());
                BlockState state = world.getBlockState(tempPos);
                if (state.is(BlockTags.LOGS)) {
                    if (breakBlock(world, player, stack, tempPos, tool.getAntimatterToolType().getUseDurability())){
                        harvested[0] = true;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
        } else {
            boolean[] stopped = new boolean[1];
            BehaviourTreeFelling.findTree(world, start).getLogs().forEach(b -> {
                if (stack.isEmpty()) return;
                if (stopped[0]) return;
                BlockState state = world.getBlockState(b);
                if (state.isAir() || !isCorrectToolForDrops(state, player))
                    return;
                else if (state.is(BlockTags.LOGS)) {
                    if (breakBlock(world, player, stack, b, tool.getAntimatterToolType().getUseDurability())){
                        harvested[0] = true;
                    } else {
                        stopped[0] = true;
                    }
                }
            });
        }
        return harvested[0];
    }

    @ExpectPlatform
    public static boolean isCorrectToolForDrops(BlockState state, Player player){
        return false;
    }

    /**
     * Gets harvestables out of a ImmutableSet of block positions, this is IAntimatterTool sensitive, and will not work for normal ItemStacks, for that, check out BlockState#isToolEffective
     *
     * @param world  World instance of the PlayerEntity
     * @param player PlayerEntity that is breaking the blocks
     * @param column vertical amount of blocks
     * @param row    horizontal amount of blocks
     * @param depth  depth amount of blocks
     * @return set of harvestable BlockPos in the specified range with specified player
     */
    public static ImmutableSet<BlockPos> getHarvestableBlocksToBreak(@NotNull Level world, @NotNull Player player, @NotNull IAntimatterTool tool, ItemStack stack, int column, int row, int depth) {
        ImmutableSet<BlockPos> totalBlocks = getBlocksToBreak(world, player, column, row, depth);
        return totalBlocks.stream().filter(b -> tool.genericIsCorrectToolForDrops(stack, world.getBlockState(b)) && world.getBlockState(b).getDestroySpeed(world, b) >= 0).collect(ImmutableSet.toImmutableSet());
    }

    /**
     * Gets blocks to be broken in a column (radius), row (radius) and depth. This is axis-sensitive
     *
     * @param world  = World instance of the PlayerEntity
     * @param player = PlayerEntity that is breaking the blocks
     * @param column = vertical amount of blocks
     * @param row    = horizontal amount of blocks
     * @param depth  = depth amount of blocks
     * @return set of BlockPos in the specified range
     */
    public static ImmutableSet<BlockPos> getBlocksToBreak(@NotNull Level world, @NotNull Player player, int column, int row, int depth) {
        Vec3 lookPos = player.getEyePosition(1), rotation = player.getViewVector(1), realLookPos = lookPos.add(rotation.x * 5, rotation.y * 5, rotation.z * 5);
        BlockHitResult result = world.clip(new ClipContext(lookPos, realLookPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        Direction playerDirection = player.getDirection();
        Direction.Axis playerAxis = playerDirection.getAxis(), faceAxis = result.getDirection().getAxis();
        Direction.AxisDirection faceAxisDir = result.getDirection().getAxisDirection();
        ImmutableSet.Builder<BlockPos> blockPositions = ImmutableSet.builder();
        if (faceAxis.isVertical()) {
            boolean isX = playerAxis == Direction.Axis.X;
            boolean isDown = faceAxisDir == Direction.AxisDirection.NEGATIVE;
            for (int y = 0; y < depth; y++) {
                for (int x = isX ? -column : -row; x <= (isX ? column : row); x++) {
                    for (int z = isX ? -row : -column; z <= (isX ? row : column); z++) {
                        if (!(x == 0 && y == 0 && z == 0))
                            blockPositions.add(result.getBlockPos().offset(x, isDown ? y : -y, z));
                    }
                }
            }
        } else { // FaceAxis - Horizontal
            boolean isX = faceAxis == Direction.Axis.X;
            boolean isNegative = faceAxisDir == Direction.AxisDirection.NEGATIVE;
            for (int x = 0; x < depth; x++) {
                for (int y = -column; y <= column; y++) {
                    for (int z = -row; z <= row; z++) {
                        if (!(x == 0 && y == 0 && z == 0))
                            blockPositions.add(result.getBlockPos().offset(isX ? (isNegative ? x : -x) : (isNegative ? z : -z), y, isX ? (isNegative ? z : -z) : (isNegative ? x : -x)));
                    }
                }
            }
        }
        return blockPositions.build();
    }

    /**
     * Scrappy but efficient way of determining an DyeColor from mere RGB values
     *
     * @param rgb int colour
     * @return DyeColor that is the closest to the RGB input
     */
    public static DyeColor determineColour(int rgb) {
        Color colour = new Color(rgb);
        Double2ObjectMap<DyeColor> distances = new Double2ObjectOpenHashMap<>();
        for (DyeColor dyeColour : DyeColor.values()) {
            Color enumColour = new Color(dyeColour.getMaterialColor().col);
            double distance = (colour.getRed() - enumColour.getRed()) * (colour.getRed() - enumColour.getRed())
                    + (colour.getGreen() - enumColour.getGreen()) * (colour.getGreen() - enumColour.getGreen())
                    + (colour.getBlue() - enumColour.getBlue()) * (colour.getBlue() - enumColour.getBlue());
            distances.put(distance, dyeColour);
        }
        return distances.get((double) Collections.min(distances.keySet()));
    }

    public static String lowerUnderscoreToUpperSpaced(String string) {
        String[] split = StringUtils.split(string, "_");
        String[] split2 = new String[split.length];
        for (int i = 0; i < split.length; i++){
            String str = split[i];
            if (str.isEmpty() || !Character.isDigit(str.charAt(0))){
                str = underscoreToUpperCamel(str);
            }
            split2[i] = str;
        }
        return StringUtils.join(split2, ' ');
    }

    public static String lowerUnderscoreToUpperSpacedRotated(String string) {
        String[] strings = StringUtils.splitByCharacterTypeCamelCase(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string));
        String[] newStrings = new String[strings.length];

        newStrings[0] = strings[strings.length - 1];
        for (int i = 1; i < strings.length; i++) {
            newStrings[i] = strings[i - 1];
        }
        return StringUtils.join(newStrings, ' ');
    }

    public static String lowerUnderscoreToUpperSpacedReversed(String string) {
        String[] strings = StringUtils.splitByCharacterTypeCamelCase(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string));
        String[] newStrings = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            newStrings[i] = strings[(strings.length - 1) - i];
        }
        return StringUtils.join(newStrings, ' ');
    }

    public static String lowerUnderscoreToUpperSpaced(String string, int offset) {
        String[] strings = StringUtils.splitByCharacterTypeCamelCase(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string));
        assert offset > strings.length;
        return StringUtils.join(Arrays.copyOfRange(strings, offset, strings.length), ' ');
    }

    public static String underscoreToUpperCamel(String string) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string);
    }

    /**
     * Used primarily in chemical formula tooltips
     *
     * @param string input
     * @return string with its digits swapped to its subscript variant
     */
    public static String digitsToSubscript(String string) {
        if (string.length() == 0) return "";
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int index = chars[i] - '0';
            if (index >= 0 && index <= 9) {
                int newChar = '\u2080' + index;
                chars[i] = (char) newChar;
            }
        }
        return new String(chars);
    }

    public static String parseString(Object o, String original) {
        return o instanceof String ? String.valueOf(o) : original;
    }

    public static int parseInt(Object o, int original) {
        if (o instanceof Integer) return (Integer) o;
        else if (o instanceof Double) return ((Double) o).intValue();
        else if (o instanceof String) {
            try {
                return Integer.parseInt((String) o);
            } catch (NumberFormatException e) {
                return original;
            }
        }
        return original;
    }

    public static String getConventionalStoneType(StoneType type) {
        String string = type.getId();
        // breaks generation in stones with underscores cause the stones are generated without the 2 below lines in the name
        /*int index = string.indexOf("_");
        if (index != -1) return String.join("", string.substring(index + 1), "_", string.substring(0, index));*/
        return string;
    }

    public static String getConventionalMaterialType(MaterialType<?> type) {
        if (type.getId().equals("raw_ore")){
            //thank forge for this stupid tag
            return "raw_materials";
        }
        if (type.getId().equals("raw_ore_block") || type.getId().equals("block")){
            return "storage_blocks";
        }
        if (type.getId().equals("ore_stone")){
            return "ore_stones";
        }
        String id = type.getId();
        int index = id.indexOf("_");
        if (index != -1 && type.isSplitName()) {
            id = String.join("", id.substring(index + 1), "_", id.substring(0, index), "s");
            if (id.contains("crushed")) id = StringUtils.replace(id, "crushed", "ore");
            return id;
        } else if (id.equals("crushed")) return StringUtils.replace(id, "crushed", "crushed_ores");
        return id.charAt(id.length() - 1) == 's' ? id.concat("es") : id.concat("s");
    }

    /**
     * Spawns a new item entity
     *
     * @param tile the active tile
     * @param item the item to spawn, 1.
     * @param dir  the direction to spawn it in.
     */
    public static void dropItemInWorldAtTile(BlockEntity tile, Item item, Direction dir) {
        ItemEntity entity = new ItemEntity(tile.getLevel(), tile.getBlockPos().getX() + dir.getStepX(), tile.getBlockPos().getY() + dir.getStepY(), tile.getBlockPos().getZ() + dir.getStepZ(), new ItemStack(item, 1));
        tile.getLevel().addFreshEntity(entity);
    }

    public static String[] getLocalizedMaterialType(MaterialType<?> type) {
        String id = type.getId();
        int index = id.indexOf("_");
        if (index != -1 && type.isSplitName()) {
            String joined = String.join("", id.substring(index + 1), "_", id.substring(0, index));
            return lowerUnderscoreToUpperSpaced(joined).split(" ");
        }
        return new String[]{lowerUnderscoreToUpperSpaced(id).replace('_', ' ')};
    }

    public static String getLocalizedType(IAntimatterObject type) {
        String id = type.getId();
        if (type instanceof Material material && material.getDisplayNameString() != null && !material.getDisplayNameString().isEmpty()){
            return material.getDisplayNameString();
        }
        int index = id.indexOf("_");
        if (index != -1) {
            if (type instanceof MaterialType<?> matType) {
                return StringUtils.join(getLocalizedMaterialType(matType));
            }
            return StringUtils.replaceChars(lowerUnderscoreToUpperSpaced(id), '_', ' ');
        }
        return StringUtils.capitalize(id);
    }

    public static String getLocalizeStoneType(StoneType type) {
        return getLocalizedType(type);
    }

    public static boolean doesStackHaveToolTypes(ItemStack stack, TagKey<Item>... types) {
        if (!stack.isEmpty()) {
            for (TagKey<Item> type : types) {
                if (stack.getItem().builtInRegistryHolder().is(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean doesStackHaveToolTypes(ItemStack stack, AntimatterToolType... types) {
        List<TagKey<Item>> ret = new ObjectArrayList<>();
        for (AntimatterToolType ty : types) {
            ret.add(ty.getForgeTag());
        }
        TagKey<Item>[] t =  (TagKey<Item>[]) ret.toArray(new TagKey[0]);
        return doesStackHaveToolTypes(stack,t);
    }


    public static boolean isPlayerHolding(Player player, InteractionHand hand, AntimatterToolType... t) {
        return doesStackHaveToolTypes(player.getItemInHand(hand), t);
    }

    @Nullable
    //@Deprecated // Ready to use the methods above instead
    //Not deprecated so you don't have to call methods multiple times.
    public static AntimatterToolType getToolType(Player player) {
        ItemStack stack = player.getMainHandItem();
        for (AntimatterToolType ty : AntimatterAPI.all(AntimatterToolType.class)) {
            if (!ty.hasOriginalTag()) continue;
            if (stack.is(ty.getTag())){
                return ty;
            }
        }
        return null;
    }

    /**
     * @return an empty instance of Recipe
     */
    public static IRecipe getEmptyRecipe() {
        return new Recipe(Collections.emptyList(), new ItemStack[0], Collections.emptyList(), new FluidHolder[0], 1, 1, 0, 1);
    }

    public static IRecipe getEmptyPoweredRecipe(int duration, long euT, int amps) {
        return new Recipe(Collections.emptyList(), new ItemStack[0], Collections.emptyList(), new FluidHolder[0], duration, euT, 0, amps);
    }

    /**
     * Returns a fluid powered recipe, that is, essentially a recipe for generators.
     *
     * @param input    fluid inputs.
     * @param output   fluid outputs (e.g. water)
     * @param duration how long to generate power for
     * @param euT      eu/T generated.
     * @param amps     amps outputted.
     * @return recipe.
     */
    public static IRecipe getFluidPoweredRecipe(List<FluidIngredient> input, FluidHolder[] output, int duration, long euT, int amps) {
        return new Recipe(Collections.emptyList(), new ItemStack[0], input, output, duration, euT, 0, amps);
    }

    /**
     * @param msg to be printed with IllegalStateException, normally used when dev/user enters invalid input of data
     */
    public static void onInvalidData(String msg) {
        if (Ref.DATA_EXCEPTIONS) throw new IllegalStateException(msg);
        Antimatter.LOGGER.error(msg);
    }

    /**
     * @param msg redirects to the main logger with a border
     */
    public static void printError(String msg) {
        Antimatter.LOGGER.error("====================================================");
        Antimatter.LOGGER.error(msg);
        Antimatter.LOGGER.error("====================================================");
    }

    public static <T> T cast(Object o){
        return (T) o;
    }
}
