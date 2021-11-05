package muramasa.antimatter.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.advancements.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.StringUtils;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

import static net.minecraft.advancements.criterion.MinMaxBounds.IntBound.UNBOUNDED;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

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
        return a.isItemEqual(b);
    }

    /**
     * Returns true of A has the same Fluid as B
     **/
    public static boolean equals(FluidStack a, FluidStack b) {
        return a.isFluidEqual(b);
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
    public static boolean contains(FluidStack a, FluidStack b) {
        return a.containsFluid(b);
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
        if (state.hasProperty(BlockStateProperties.FACING)) return state.get(BlockStateProperties.FACING);
        return state.get(BlockStateProperties.HORIZONTAL_FACING);
    }

    public static ItemStack extractAny(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.extractItem(i, 64, false);
            if (!stack.isEmpty()) return stack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns the index of a fluid in a list, or -1 if not found
     **/
    public static int contains(List<FluidStack> list, FluidStack fluid) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (equals(list.get(i), fluid)) return i;
        }
        return -1;
    }

    /**
     * Merges B into A, ignoring maxStackSize
     **/
    public static List<ItemStack> mergeItems(List<ItemStack> a, List<ItemStack> b) {
        int position, size = b.size();
        for (ItemStack stack : b) {
            if (stack.isEmpty()) continue;
            position = contains(a, stack);
            if (position == -1) a.add(stack);
            else a.get(position).grow(stack.getCount());
        }
        return a;
    }

    /**
     * Merges two Lists of FluidStacks, ignoring max amount
     **/
    public static List<FluidStack> mergeFluids(List<FluidStack> a, List<FluidStack> b) {
        int position, size = b.size();
        for (FluidStack stack : b) {
            if (stack == null) continue;
            position = contains(a, stack);
            if (position == -1) a.add(stack);
            else a.get(position).grow(stack.getAmount());
        }
        return a;
    }

    public static ItemStack ca(int amount, ItemStack toCopy) {
        ItemStack stack = toCopy.copy();
        stack.setCount(amount);
        return stack;
    }

    public static FluidStack ca(int amount, FluidStack toCopy) {
        FluidStack stack = toCopy.copy();
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

    public static void damageStack(int durability, ItemStack stack, LivingEntity player) {
        stack.damageItem(durability, player, p -> {
            p.sendBreakAnimation(EquipmentSlotType.MAINHAND);
        });
    }

    public static ItemStack mul(int amount, ItemStack stack) {
        return ca(stack.getCount() * amount, stack);
    }

    public static FluidStack mul(int amount, FluidStack stack) {
        return ca(stack.getAmount() * amount, stack);
    }

    public static boolean hasNoConsumeTag(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(Ref.KEY_STACK_NO_CONSUME);
    }

    public static boolean hasNoConsumeTag(FluidStack stack) {
        return stack.hasTag() && stack.getTag().contains(Ref.KEY_STACK_NO_CONSUME);
    }

    public static boolean hasIgnoreNbtTag(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(Ref.KEY_STACK_IGNORE_NBT);
    }


    public static boolean getNoConsumeTag(ItemStack stack) {
        return stack.getTag().getBoolean(Ref.KEY_STACK_NO_CONSUME);
    }

    public static boolean getNoConsumeTag(FluidStack stack) {
        return stack.getTag().getBoolean(Ref.KEY_STACK_NO_CONSUME);
    }

    public static ItemStack addNoConsumeTag(ItemStack stack) {
        validateNBT(stack).getTag().putBoolean(Ref.KEY_STACK_NO_CONSUME, true);
        return stack;
    }

    public static FluidStack addNoConsumeTag(FluidStack stack) {
        validateNBT(stack).getTag().putBoolean(Ref.KEY_STACK_NO_CONSUME, true);
        return stack;
    }

    public static ItemStack validateNBT(ItemStack stack) {
        if (!stack.hasTag()) stack.setTag(new CompoundNBT());
        return stack;
    }

    public static FluidStack validateNBT(FluidStack stack) {
        if (!stack.hasTag()) stack.setTag(new CompoundNBT());
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

    public static boolean areFluidsValid(FluidStack... fluids) {
        if (fluids == null || fluids.length == 0) return false;
        for (FluidStack fluid : fluids) {
            if (fluid.getRawFluid() == Fluids.EMPTY) return false;
        }
        return true;
    }

    public static boolean areFluidsValid(FluidStack[]... fluidArrays) {
        for (FluidStack[] fluidArray : fluidArrays) {
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

    public static boolean doItemsMatchAndSizeValid(List<RecipeIngredient> a, ItemStack[] b) {
        if (a == null || b == null) return false;
        int matchCount = 0;
        for (RecipeIngredient stack : a) {
            for (ItemStack itemStack : b) {
                if (stack.get().test(itemStack)) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= a.size();
    }

    public static boolean doFluidsMatchAndSizeValid(FluidStack[] a, FluidStack[] b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        int matchCount = 0;
        for (FluidStack fluidStack : a) {
            for (FluidStack stack : b) {
                if (contains(stack, fluidStack)) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= a.length;
    }

    public static boolean transferItems(IItemHandler from, IItemHandler to, boolean once) {
        return transferItems(from, to, once, stack -> true);
    }

    public static boolean transferItems(IItemHandler from, IItemHandler to, boolean once, Predicate<ItemStack> filter) {
        boolean successful = false;
        for (int i = 0; i < from.getSlots(); i++) {
            ItemStack toInsert = from.extractItem(i, from.getStackInSlot(i).getCount(), true);
            if (toInsert.isEmpty() || !filter.test(toInsert)) {
                continue;
            }
            ItemStack inserted = ItemHandlerHelper.insertItem(to, toInsert, true);
            if (inserted.getCount() < toInsert.getCount()) {
                int actual = toInsert.getCount() - inserted.getCount();
                toInsert.setCount(toInsert.getCount() - inserted.getCount());
                ItemHandlerHelper.insertItem(to, toInsert, false);
                from.extractItem(i, actual, false);
                if (!successful) successful = true;
                if (once) break;
            }
        }
        return successful;
    }

    /**
     * Transfers up to maxAmps between energy handlers, without loss.
     *
     * @param from the handler to extract from
     * @param to   the handler to insert
     * @return if energy was inserted
     */
    public static boolean transferEnergy(IEnergyHandler from, IEnergyHandler to) {
        long extracted = from.extract(from.getOutputVoltage(), true);
        if (extracted > 0) {
            long inputted = to.insert(extracted, true);
            if (inputted > 0) {
                to.insert(inputted, false);
                from.extract(inputted, false);
                return true;
            }
        }
        return false;
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
        long extracted = from.extract(from.getOutputVoltage(), true);
        if (extracted > 0) {
            long inputted = to.insert(extracted - loss, true);
            if (inputted > 0) {
                to.insert(inputted - loss, false);
                from.extract(inputted, false);
                return true;
            }
        }
        return false;
    }

    public static boolean transferFluids(IFluidHandler from, IFluidHandler to, int cap, Predicate<FluidStack> filter) {
        boolean successful = false;
        for (int i = 0; i < to.getTanks(); i++) {
            //if (i >= from.getTanks()) break;
            FluidStack toInsert = FluidStack.EMPTY;
            for (int j = 0; j < from.getTanks(); j++) {
                if (cap > 0) {
                    FluidStack fluid = from.getFluidInTank(j);
                    if (fluid.isEmpty() || !filter.test(fluid)) {
                        continue;
                    }
                    fluid = fluid.copy();
                    int toDrain = Math.min(cap, fluid.getAmount());
                    fluid.setAmount(toDrain);
                    toInsert = from.drain(fluid, SIMULATE);
                } else {
                    toInsert = from.drain(from.getFluidInTank(j), SIMULATE);
                }
                int filled = to.fill(toInsert, SIMULATE);
                if (filled > 0) {
                    toInsert.setAmount(filled);
                    to.fill(from.drain(toInsert, EXECUTE), EXECUTE);
                    successful = true;
                }
            }
        }
        return successful;
    }

    public static boolean transferFluids(IFluidHandler from, IFluidHandler to, int cap) {
        return transferFluids(from, to, cap, fluidStack -> true);
    }

    public static boolean transferFluids(IFluidHandler from, IFluidHandler to) {
        return transferFluids(from, to, -1);
    }

    /**
     * Creates a new {@link EnterBlockTrigger} for use with recipe unlock criteria.
     */
    public static EnterBlockTrigger.Instance enteredBlock(Block blockIn) {
        return new EnterBlockTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, blockIn, StatePropertiesPredicate.EMPTY);
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    public static InventoryChangeTrigger.Instance hasItem(IItemProvider itemIn) {
        return hasItem(ItemPredicate.Builder.create().item(itemIn).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having an item within the given tag.
     */
    public static InventoryChangeTrigger.Instance hasItem(ITag<Item> tagIn) {
        return hasItem(ItemPredicate.Builder.create().tag(tagIn).build());
    }

    /**
     * Creates a new {@link InventoryChangeTrigger} that checks for a player having a certain item.
     */
    public static InventoryChangeTrigger.Instance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, UNBOUNDED, UNBOUNDED, UNBOUNDED, predicates);
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
    public static TileEntity getTile(@Nullable IBlockReader reader, BlockPos pos) {
        if (reader == null) return null;
        return reader.getTileEntity(pos);
        //TODO validate and redo
//        if (pos == null || blockAccess == null) return null;
//        if (blockAccess instanceof ChunkRenderCache) {
//            return ((ChunkRenderCache) blockAccess).getTileEntity(pos, Chunk.CreateEntityType.CHECK);
//        } else {
//            return blockAccess.getTileEntity(pos);
//        }
    }

    public static boolean isForeignTile(@Nullable TileEntity target) {
        return target != null && !(target instanceof TileEntityBase);
    }

    @Nullable
    public static TileEntity getTileFromBuf(PacketBuffer buf) {
        return DistExecutor.runForDist(() -> () -> Antimatter.PROXY.getClientWorld().getTileEntity(buf.readBlockPos()), () -> () -> {
            throw new RuntimeException("Shouldn't be called on server!");
        });
    }

    /**
     * Syncs NBT between Client & Server
     **/
    public static void markTileForNBTSync(TileEntity tile) {
        BlockState state = tile.getWorld().getBlockState(tile.getPos());
        tile.getWorld().notifyBlockUpdate(tile.getPos(), state, state, 3);
    }

    /**
     * Sends block update to clients
     **/
    public static void markTileForRenderUpdate(TileEntity tile) {
        BlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (tile.getWorld().isRemote) {
            tile.getWorld().notifyBlockUpdate(tile.getPos(), state, state, Constants.BlockFlags.RERENDER_MAIN_THREAD);
            ModelDataManager.requestModelDataRefresh(tile);
        }
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
        return TRANSFORM[facing.getIndex()][side.getIndex()];
    }

    public static Direction coverRotateFacing(Direction toRotate, Direction rotateBy) {
        RotationHelper.ModelRotation r = Utils.getModelRotationCover(rotateBy);
        return r.getRotation().rotateFace(toRotate);
    }

    public static Direction getOffsetFacing(BlockPos center, BlockPos offset) {
        if (center.getX() > offset.getX()) return Direction.WEST;
        else if (center.getX() < offset.getX()) return Direction.EAST;
        else if (center.getZ() > offset.getZ()) return Direction.NORTH;
        else if (center.getZ() < offset.getZ()) return Direction.SOUTH;
        else if (center.getY() > offset.getY()) return Direction.DOWN;
        else if (center.getY() < offset.getY()) return Direction.UP;
        else return Direction.NORTH;
    }

    final static double INTERACTION_OFFSET = 0.25;

    public static Direction getInteractSide(BlockRayTraceResult res) {
        Vector3d vec = res.getHitVec();
        return getInteractSide(res.getFace(), (float) vec.x - res.getPos().getX(), (float) vec.y - res.getPos().getY(), (float) vec.z - res.getPos().getZ());
    }

    public static Direction getInteractSide(Direction side, float x, float y, float z) {
        Direction backSide = side.getOpposite();
        switch (side.getIndex()) {
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

    public static Set<BlockPos> getCubicPosArea(int3 area, Direction side, BlockPos origin, PlayerEntity player, boolean excludeAir) {
        int xRadius, yRadius, zRadius;
        BlockPos center;

        if (side == null) {
            center = origin;
            xRadius = area.getX();
            yRadius = area.getY();
            zRadius = area.getZ();
        } else {
            center = origin.offset(side.getOpposite(), area.getZ());
            if (side.getAxis() == Direction.Axis.Y) {
                xRadius = player.getHorizontalFacing().getAxis() == Direction.Axis.X ? area.getY() : area.getX();
                yRadius = area.getZ();
                zRadius = player.getHorizontalFacing().getAxis() == Direction.Axis.Z ? area.getY() : area.getX();
            } else {
                xRadius = player.getHorizontalFacing().getAxis() == Direction.Axis.X ? area.getZ() : area.getX();
                yRadius = area.getY();
                zRadius = player.getHorizontalFacing().getAxis() == Direction.Axis.Z ? area.getZ() : area.getX();
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
                        state = player.world.getBlockState(harvestPos);
                        if (state.getBlock().isAir(state, player.world, harvestPos)) continue;
                    }
                    set.add(new BlockPos(x, y, z));
                }
            }
        }
        return set;
    }

    //So confusing, right? I barely even know anymore but so far it works. 90 and 270 are swapped.
    public static ModelRotation getModelRotation(Direction dir) {
        switch (dir) {
            case DOWN:
                return ModelRotation.getModelRotation(90, 0);
            case UP:
                return ModelRotation.getModelRotation(-90, 0);
            case NORTH:
                return ModelRotation.getModelRotation(0, 0);
            case SOUTH:
                return ModelRotation.getModelRotation(0, 180);
            case EAST:
                return ModelRotation.getModelRotation(0, 90);
            case WEST:
                return ModelRotation.getModelRotation(0, 270);
        }
        return null;
    }

    public static TransformationMatrix getRotation(Direction dir, Direction face) {
        int[] vec = rotationVector(dir, face);
        Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float) (-vec[1]), true);
        quaternion.multiply(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float) (-vec[0]), true));
        quaternion.multiply(new Quaternion(new Vector3f(0.0F, 0.0F, 1.0F), (float) (-vec[2]), true));
        return new TransformationMatrix(null, quaternion, null, null);
    }

    //All these getRotations, coverRotateFacings. Honestly look into them. I just made
    //something that works but it is really confusing... Some values here are inverted but it works? This is used
    //for multitexturer while getModelRotation is used for all else.
    public static RotationHelper.ModelRotation getModelRotationCover(Direction dir) {
        switch (dir) {
            case DOWN:
                return RotationHelper.getModelRotation(90, 0);
            case UP:
                return RotationHelper.getModelRotation(-90, 0);
            case NORTH:
                return RotationHelper.getModelRotation(0, 0);
            case SOUTH:
                return RotationHelper.getModelRotation(0, 180);
            case EAST:
                return RotationHelper.getModelRotation(0, 270);
            case WEST:
                return RotationHelper.getModelRotation(0, 90);
        }
        return null;
    }

    //Returns client safe version.
    @OnlyIn(Dist.CLIENT)
    public static ModelRotation getModelRotationCoverClient(Direction dir) {
        switch (dir) {
            case DOWN:
                return ModelRotation.getModelRotation(90, 0);
            case UP:
                return ModelRotation.getModelRotation(-90, 0);
            case NORTH:
                return ModelRotation.getModelRotation(0, 0);
            case SOUTH:
                return ModelRotation.getModelRotation(0, 180);
            case EAST:
                return ModelRotation.getModelRotation(0, 270);
            case WEST:
                return ModelRotation.getModelRotation(0, 90);
        }
        return null;
    }

    /**
     * Returns a list of x,y,z rotations given two facings.
     */
    public static int[] rotationVector(Direction dir, Direction h) {
        switch (dir) {
            case NORTH:
                return new int[]{0, 0, 0};
            case WEST:
                return new int[]{0, 90, 0};
            case SOUTH:
                return new int[]{0, 180, 0};
            case EAST:
                return new int[]{0, 270, 0};
            case UP:
                switch (h) {
                    case NORTH://
                        return new int[]{90, 0, 0};
                    case WEST:
                        return new int[]{90, 0, 270};
                    case SOUTH://
                        return new int[]{270, 180, 0};
                    case EAST:
                        return new int[]{90, 0, 90};
                }
                break;
            case DOWN:
                switch (h) {
                    case NORTH:// DONE
                        return new int[]{270, 0, 0};
                    case WEST:
                        return new int[]{270, 0, 90};
                    case SOUTH:// DONE!
                        return new int[]{90, 180, 0};
                    case EAST:
                        return new int[]{270, 0, 270};
                }
                break;
        }
        return null;
    }

    public static void createExplosion(@Nullable World world, BlockPos pos, float explosionRadius, Explosion.Mode modeIn) {
        if (world != null) {
            if (!world.isRemote) {
                world.createExplosion(null, pos.getX(), pos.getY() + 0.0625D, pos.getZ(), explosionRadius, modeIn);
            } else {
                world.addParticle(ParticleTypes.SMOKE, pos.getX(), pos.getY() + 0.5D, pos.getZ(), 0.0D, 0.0D, 0.0D);
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    public static void createFireAround(@Nullable World world, BlockPos pos) {
        if (world != null) {
            boolean fired = false;
            for (Direction side : Ref.DIRS) {
                BlockPos offset = pos.offset(side);
                if (world.getBlockState(offset) == Blocks.AIR.getDefaultState()) {
                    world.setBlockState(offset, Blocks.FIRE.getDefaultState());
                    fired = true;
                }
            }
            if (!fired) world.setBlockState(pos, Blocks.FIRE.getDefaultState());
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
    public static boolean breakBlock(World world, @Nullable PlayerEntity player, ItemStack stack, BlockPos pos, int damage) {
        if (world.isRemote) return false;
        BlockState state = world.getBlockState(pos);
        ServerPlayerEntity serverPlayer = ((ServerPlayerEntity) player);
        int exp = ForgeHooks.onBlockBreakEvent(world, serverPlayer.interactionManager.getGameType(), serverPlayer, pos);

        if (exp == -1) return false;
        stack.damageItem(state.getBlockHardness(world, pos) != 0.0F ? damage : 0, player, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        boolean destroyed = world.removeBlock(pos, false);// world.func_225521_a_(pos, !player.isCreative(), player);
        if (destroyed && state.canHarvestBlock(world, pos, player))
            state.getBlock().harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
        if (exp > 0) state.getBlock().dropXpOnBlockBreak((ServerWorld) world, pos, exp);
        return destroyed;
    }

    /**
     * IAntimatterTool-sensitive extension of IForgeBlockState::isToolEffective
     *
     * @param tool  IAntimatterTool derivatives
     * @param state BlockState that is being checked against
     * @return true if tool is effective by checking blocks or materials list of its AntimatterToolType
     */
    public static boolean isToolEffective(IAntimatterTool tool, BlockState state) {
        return tool.getAntimatterToolType().getEffectiveBlocks().contains(state.getBlock()) || tool.getAntimatterToolType().getEffectiveMaterials().contains(state.getMaterial()) || tool.getToolTypes().stream().anyMatch(state::isToolEffective);
    }

    /**
     * AntimatterToolType-sensitive extension of IForgeBlockState::isToolEffective
     *
     * @param type  AntimatterToolType object
     * @param state BlockState that is being checked against
     * @return true if tool is effective by checking blocks or materials list of its AntimatterToolType
     */
    public static boolean isToolEffective(AntimatterToolType type, Set<ToolType> toolTypes, BlockState state) {
        return type.getEffectiveBlocks().contains(state.getBlock()) || type.getEffectiveMaterials().contains(state.getMaterial()) || toolTypes.stream().anyMatch(state::isToolEffective);
    }

    /**
     * Performs tree logging. If Configs.GAMEPLAY.TREE_DETECTION is true, it will do a more complex search for branches, if set to false, it will do a normal vertical loop only
     *
     * @param stack  Player's heldItem
     * @param start  onBlockDestroy's BlockPos
     * @param player ServerPlayerEntity instance
     * @param world  World instance
     * @return if tree logging was successful
     */
    public static boolean treeLogging(@Nonnull IAntimatterTool tool, @Nonnull ItemStack stack, @Nonnull BlockPos start, @Nonnull PlayerEntity player, @Nonnull World world) {
        if (!AntimatterConfig.GAMEPLAY.SMARTER_TREE_DETECTION) {
            BlockPos.Mutable tempPos = new BlockPos.Mutable(start.getX(), start.getY(), start.getZ());
            for (int y = start.getY() + 1; y < start.getY() + AntimatterConfig.GAMEPLAY.AXE_TIMBER_MAX; y++) {
                if (stack.getDamage() < 2) return false;
                tempPos.move(Direction.UP);
                BlockState state = world.getBlockState(tempPos);
                if (state.isAir(world, tempPos) || !ForgeHooks.canHarvestBlock(state, player, world, tempPos))
                    return false;
                else if (state.getBlock().isIn(BlockTags.LOGS)) {
                    breakBlock(world, player, stack, tempPos, tool.getAntimatterToolType().getUseDurability());
                }
            }
        } else {
            LinkedList<BlockPos> blocks = new LinkedList<>();
            Set<BlockPos> visited = new ObjectOpenHashSet<>();
            int amount = AntimatterConfig.GAMEPLAY.AXE_TIMBER_MAX;
            blocks.add(start);
            BlockPos pos;
            Direction[] dirs = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            while (amount > 0) {
                if (blocks.isEmpty() || (stack.isDamaged() && stack.getDamage() < tool.getAntimatterToolType().getUseDurability()))
                    return false;
                pos = blocks.remove();
                if (!visited.add(pos)) continue;
                if (!world.getBlockState(pos).getBlock().isIn(BlockTags.LOGS)) continue;
                for (Direction side : dirs) {
                    BlockPos dirPos = pos.offset(side);
                    if (!visited.contains(dirPos)) blocks.add(dirPos);
                }
                for (int x = 0; x < 3; x++) {
                    for (int z = 0; z < 3; z++) {
                        BlockPos branchPos = pos.add(-1 + x, 1, -1 + z);
                        if (!visited.contains(branchPos)) blocks.add(branchPos);
                    }
                }
                amount--;
                if (pos.equals(start)) continue;
                boolean breakBlock = breakBlock(world, player, stack, pos, tool.getAntimatterToolType().getUseDurability());
                if (!breakBlock) break;
            }
        }
        return true;
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
    public static ImmutableSet<BlockPos> getHarvestableBlocksToBreak(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull IAntimatterTool tool, int column, int row, int depth) {
        ImmutableSet<BlockPos> totalBlocks = getBlocksToBreak(world, player, column, row, depth);
        return totalBlocks.stream().filter(b -> isToolEffective(tool, world.getBlockState(b))).collect(ImmutableSet.toImmutableSet());
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
    public static ImmutableSet<BlockPos> getBlocksToBreak(@Nonnull World world, @Nonnull PlayerEntity player, int column, int row, int depth) {
        Vector3d lookPos = player.getEyePosition(1), rotation = player.getLook(1), realLookPos = lookPos.add(rotation.x * 5, rotation.y * 5, rotation.z * 5);
        BlockRayTraceResult result = world.rayTraceBlocks(new RayTraceContext(lookPos, realLookPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
        Direction playerDirection = player.getHorizontalFacing();
        Direction.Axis playerAxis = playerDirection.getAxis(), faceAxis = result.getFace().getAxis();
        Direction.AxisDirection faceAxisDir = result.getFace().getAxisDirection();
        ImmutableSet.Builder<BlockPos> blockPositions = ImmutableSet.builder();
        if (faceAxis.isVertical()) {
            boolean isX = playerAxis == Direction.Axis.X;
            boolean isDown = faceAxisDir == Direction.AxisDirection.NEGATIVE;
            for (int y = 0; y < depth; y++) {
                for (int x = isX ? -column : -row; x <= (isX ? column : row); x++) {
                    for (int z = isX ? -row : -column; z <= (isX ? row : column); z++) {
                        if (!(x == 0 && y == 0 && z == 0))
                            blockPositions.add(result.getPos().add(x, isDown ? y : -y, z));
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
                            blockPositions.add(result.getPos().add(isX ? (isNegative ? x : -x) : (isNegative ? z : -z), y, isX ? (isNegative ? z : -z) : (isNegative ? x : -x)));
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
            Color enumColour = new Color(dyeColour.getColorValue());
            double distance = (colour.getRed() - enumColour.getRed()) * (colour.getRed() - enumColour.getRed())
                    + (colour.getGreen() - enumColour.getGreen()) * (colour.getGreen() - enumColour.getGreen())
                    + (colour.getBlue() - enumColour.getBlue()) * (colour.getBlue() - enumColour.getBlue());
            distances.put(distance, dyeColour);
        }
        return distances.get((double) Collections.min(distances.keySet()));
    }

    public static String lowerUnderscoreToUpperSpaced(String string) {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string)), ' ');
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
        String id = type.getId();
        int index = id.indexOf("_");
        if (index != -1 && type.isSplitName()) {
            id = String.join("", id.substring(index + 1), "_", id.substring(0, index), "s");
            if (id.contains("crushed")) id = StringUtils.replace(id, "crushed", "crushed_ore");
            return id;
        } else if (id.contains("crushed")) return StringUtils.replace(id, "crushed", "crushed_ores");
        return id.charAt(id.length() - 1) == 's' ? id.concat("es") : id.concat("s");
    }

    /**
     * Spawns a new item entity
     *
     * @param tile the active tile
     * @param item the item to spawn, 1.
     * @param dir  the direction to spawn it in.
     */
    public static void dropItemInWorldAtTile(TileEntity tile, Item item, Direction dir) {
        ItemEntity entity = new ItemEntity(tile.getWorld(), tile.getPos().getX() + dir.getXOffset(), tile.getPos().getY() + dir.getYOffset(), tile.getPos().getZ() + dir.getZOffset(), new ItemStack(item, 1));
        tile.getWorld().addEntity(entity);
    }

    public static String[] getLocalizedMaterialType(MaterialType<?> type) {
        String id = type.getId();
        int index = id.indexOf("_");
        if (index != -1) {
            String joined = String.join("", id.substring(index + 1), "_", id.substring(0, index));
            return lowerUnderscoreToUpperSpaced(joined).split(" ");
        }
        return new String[]{lowerUnderscoreToUpperSpaced(id).replace('_', ' ')};
    }

    public static String getLocalizedType(IAntimatterObject type) {
        String id = type.getId();
        int index = id.indexOf("_");
        if (index != -1) {
            if (type instanceof MaterialType) {
                String joined = String.join("", id.substring(index + 1), "_", id.substring(0, index));
                return StringUtils.replaceChars(lowerUnderscoreToUpperSpaced(joined), '_', ' ');
            }
            return StringUtils.replaceChars(lowerUnderscoreToUpperSpaced(id), '_', ' ');
        }
        return StringUtils.capitalize(id);
    }

    public static String getLocalizeStoneType(StoneType type) {
        return getLocalizedType(type);
    }

    public static boolean doesStackHaveToolTypes(ItemStack stack, ToolType... toolTypes) {
        if (!stack.isEmpty()) {
            for (ToolType toolType : toolTypes) {
                if (stack.getToolTypes().contains(toolType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean doesStackHaveToolTypes(ItemStack stack, AntimatterToolType... antimatterToolTypes) {
        List<ToolType> toolTypes = new ObjectArrayList<>();
        for (AntimatterToolType antimatterToolType : antimatterToolTypes) {
            toolTypes.addAll(antimatterToolType.getActualToolTypes());
        }
        return doesStackHaveToolTypes(stack, toolTypes.toArray(new ToolType[0]));
    }

    public static boolean isPlayerHolding(PlayerEntity player, Hand hand, ToolType... toolTypes) {
        return doesStackHaveToolTypes(player.getHeldItem(hand), toolTypes);
    }

    public static boolean isPlayerHolding(PlayerEntity player, Hand hand, AntimatterToolType... antimatterToolTypes) {
        List<ToolType> toolTypes = new ObjectArrayList<>();
        for (AntimatterToolType antimatterToolType : antimatterToolTypes) {
            toolTypes.addAll(antimatterToolType.getActualToolTypes());
        }
        return isPlayerHolding(player, hand, toolTypes.toArray(new ToolType[0]));
    }

    @Nullable
    //@Deprecated // Ready to use the methods above instead
    //Not deprecated so you don't have to call methods multiple times.
    public static AntimatterToolType getToolType(PlayerEntity player) {
        ItemStack stack = player.getHeldItemMainhand();
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof IAntimatterTool) {
                return ((IAntimatterTool) item).getAntimatterToolType();
            }
        }
        return null;
    }

    /**
     * @return an empty instance of Recipe
     */
    public static Recipe getEmptyRecipe() {
        return new Recipe(Collections.emptyList(), new ItemStack[0], new FluidStack[0], new FluidStack[0], 1, 1, 0, 1);
    }

    public static Recipe getEmptyPoweredRecipe(int duration, long euT, int amps) {
        return new Recipe(Collections.emptyList(), new ItemStack[0], new FluidStack[0], new FluidStack[0], duration, euT, 0, amps);
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
    public static Recipe getFluidPoweredRecipe(FluidStack[] input, FluidStack[] output, int duration, long euT, int amps) {
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
}
