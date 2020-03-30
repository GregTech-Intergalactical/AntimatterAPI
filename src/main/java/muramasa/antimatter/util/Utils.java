package muramasa.antimatter.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;

public class Utils {

    private static DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getInstance(Locale.US);
    private static DecimalFormatSymbols DECIMAL_SYMBOLS = DECIMAL_FORMAT.getDecimalFormatSymbols();

    static {
        DECIMAL_SYMBOLS.setGroupingSeparator(' ');
    }

    /** Returns true of A is not empty, has the same Item and damage is equal to B **/
    public static boolean equals(ItemStack a, ItemStack b) {
        return a.isItemEqual(b);
    }

    /** Returns true of A has the same Fluid as B **/
    public static boolean equals(FluidStack a, FluidStack b) {
        return a.isFluidEqual(b);
    }

    /** Returns true if A equals() B and A amount >= B amount **/
    public static boolean contains(ItemStack a, ItemStack b) {
        return equals(a, b) && a.getCount() >= b.getCount();
    }

    /** Returns true if A equals() B and A amount >= B amount **/
    public static boolean contains(FluidStack a, FluidStack b) {
        return a.containsFluid(b);
    }

    /** Returns the index of an item in a list, or -1 if not found **/
    public static int contains(List<ItemStack> list, ItemStack item) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (equals(list.get(i), item)) return i;
        }
        return -1;
    }

    /** Returns the index of a fluid in a list, or -1 if not found **/
    public static int contains(List<FluidStack> list, FluidStack fluid) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (equals(list.get(i), fluid)) return i;
        }
        return -1;
    }

    /** Merges B into A, ignoring maxStackSize **/
    public static List<ItemStack> mergeItems(List<ItemStack> a, List<ItemStack> b) {
        int position, size = b.size();
        for (int i = 0; i < size; i++) {
            if (b.get(i).isEmpty()) continue;
            position = contains(a, b.get(i));
            if (position == -1) a.add(b.get(i));
            else a.get(position).grow(b.get(i).getCount());
        }
        return a;
    }

    /** Merges two Lists of FluidStacks, ignoring max amount **/
    public static List<FluidStack> mergeFluids(List<FluidStack> a, List<FluidStack> b) {
        int position, size = b.size();
        for (int i = 0; i < size; i++) {
            if (b.get(i) == null) continue;
            position = contains(a, b.get(i));
            if (position == -1) a.add(b.get(i));
            else a.get(position).grow(b.get(i).getAmount());
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
        for (int i = 0; i < items.length; i++) {
            if (items[i].isEmpty()) return false;
        }
        return true;
    }

    public static boolean areItemsValid(ItemStack[]... itemArrays) {
        for (int i = 0; i < itemArrays.length; i++) {
            if (!areItemsValid(itemArrays[i])) return false;
        }
        return true;
    }

    public static boolean areFluidsValid(FluidStack... fluids) {
        if (fluids == null || fluids.length == 0) return false;
        for (int i = 0; i < fluids.length; i++) {
            if (fluids[i].getRawFluid() == Fluids.EMPTY) return false;
        }
        return true;
    }

    public static boolean areFluidsValid(FluidStack[]... fluidArrays) {
        for (int i = 0; i < fluidArrays.length; i++) {
            if (!areFluidsValid(fluidArrays[i])) return false;
        }
        return true;
    }

    public static boolean doItemsMatchAndSizeValid(ItemStack[] a, ItemStack[] b) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (contains(b[j], a[i])) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= a.length;
    }

    public static boolean doFluidsMatchAndSizeValid(FluidStack[] a, FluidStack[] b) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (contains(b[j], a[i])) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= a.length;
    }

    public static void transferItems(IItemHandler from, IItemHandler to) {
        for (int i = 0; i < to.getSlots(); i++) {
            if (i >= from.getSlots()) break;
            ItemStack toInsert = from.extractItem(i, from.getStackInSlot(i).getCount(), true);
            if (ItemHandlerHelper.insertItem(to, toInsert, true).isEmpty()) {
                ItemHandlerHelper.insertItem(to, from.extractItem(i, from.getStackInSlot(i).getCount(), false), false);
            }
        }
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

    /** https://stackoverflow.com/a/1308407 **/
    public static long getNumberOfDigits(long n, boolean in10s) {
        if (n < 10000L) {
            if (n < 100L) {
                if (n < 10L) return 1;
                else return in10s ? 10 : 2;
            }
            else {
                if (n < 1000L) return in10s ? 100 : 3;
                else return in10s ? 1000 : 4;
            }
        }
        else {
            if (n < 1000000000000L) {
                if (n < 100000000L) {
                    if (n < 1000000L) {
                        if (n < 100000L) return in10s ? 10000 : 5;
                        else return in10s ? 100000 : 6;
                    }
                    else {
                        if (n < 10000000L) return in10s ? 1000000 : 7;
                        else return in10s ? 10000000 : 8;
                    }
                }
                else {
                    if (n < 10000000000L) {
                        if (n < 1000000000L) return in10s ? 100000000 : 9;
                        else return in10s ? 1000000000 : 10;
                    } else {
                        if (n < 100000000000L) return in10s ? 10000000000L : 11;
                        else return in10s ? 100000000000L : 12;
                    }
                }
            }
            else {
                if (n < 10000000000000000L) {
                    if (n < 100000000000000L) {
                        if (n < 10000000000000L) return in10s ? 1000000000000L : 13;
                        else return in10s ? 10000000000000L : 14;
                    }
                    else {
                        if (n < 1000000000000000L) return in10s ? 100000000000000L : 15;
                        else return in10s ? 1000000000000000L : 16;
                    }
                }
                else {
                    if (n < 1000000000000000000L) {
                        if (n < 100000000000000000L) return in10s ? 10000000000000000L : 17;
                        else return in10s ? 100000000000000000L : 18;
                    }
                    else return in10s ? 1000000000000000000L : 19;
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

    /** Safe version of world.getTileEntity **/
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

    @Nullable
    public static TileEntity getTileFromBuf(PacketBuffer buf) {
        return DistExecutor.runForDist(() -> () -> {
            return Antimatter.PROXY.getClientWorld().getTileEntity(buf.readBlockPos());
        }, () -> () -> {
            throw new RuntimeException("Shouldn't be called on server!");
        });
    }

    /** Syncs NBT between Client & Server **/
    public static void markTileForNBTSync(TileEntity tile) {
        BlockState state = tile.getWorld().getBlockState(tile.getPos());
        tile.getWorld().notifyBlockUpdate(tile.getPos(), state, state, 3);
    }

    /** Sends block update to clients **/
    public static void markTileForRenderUpdate(TileEntity tile) {
        BlockState state = tile.getWorld().getBlockState(tile.getPos());
        tile.getWorld().notifyBlockUpdate(tile.getPos(), state, state, 2);
    }

    //TODO this is pretty awful, but I can't seem to figure out why EAST and WEST sides are inverted
    //TODO Possibly something to do with ModelUtils.FACING_TO_MATRIX having incorrect matrices?
    public static Direction rotateFacingAlt(Direction toRotate, Direction rotateBy) {
        Direction result = toRotate;
        if (toRotate.getAxis() == Direction.Axis.Y || rotateBy.getAxis() == Direction.Axis.Y) return result;
        /** S-W-N-E **/
        if (rotateBy.getHorizontalIndex() < Direction.NORTH.getHorizontalIndex()) {
            //Rotate CCW
            int dif = rotateBy.getHorizontalIndex() - Direction.NORTH.getHorizontalIndex();
//            System.out.println("Difccw: " + dif);
            for (int i = 0; i < Math.abs(dif); i++) {
                result = result.rotateYCCW();
            }
        } else {
            //Rotate CW
            int dif = Direction.NORTH.getHorizontalIndex() - rotateBy.getHorizontalIndex();
//            System.out.println("Difcw: " + dif);
            for (int i = 0; i < Math.abs(dif); i++) {
                result = result.rotateY();
            }
        }
//        System.out.println("to: " + toRotate + " - by: " + rotateBy + " - res: " + toRotate);
        //return rotateBy == Direction.EAST || rotateBy == Direction.WEST ? toRotate.getOpposite() : toRotate;
        return result;
    }

    //TODO this is pretty awful, but I can't seem to figure out why EAST and WEST sides are inverted
    //TODO Possibly something to do with ModelUtils.FACING_TO_MATRIX having incorrect matrices?
    public static Direction rotateFacing(Direction toRotate, Direction rotateBy) {
        Direction result = toRotate;
        if (toRotate.getAxis() == Direction.Axis.Y || rotateBy.getAxis() == Direction.Axis.Y) return result;
        /** S-W-N-E **/
        if (rotateBy.getHorizontalIndex() < Direction.NORTH.getHorizontalIndex()) {
            //Rotate CCW
            int dif = rotateBy.getHorizontalIndex() - Direction.NORTH.getHorizontalIndex();
//            System.out.println("Difccw: " + dif);
            for (int i = 0; i < Math.abs(dif); i++) {
                result = result.rotateYCCW();
            }
        } else {
            //Rotate CW
            int dif = Direction.NORTH.getHorizontalIndex() - rotateBy.getHorizontalIndex();
//            System.out.println("Difcw: " + dif);
            for (int i = 0; i < Math.abs(dif); i++) {
                result = result.rotateY();
            }
        }
//        System.out.println("to: " + toRotate + " - by: " + rotateBy + " - res: " + toRotate);
        return rotateBy == Direction.EAST || rotateBy == Direction.WEST ? result.getOpposite() : result;
    }

    //TODO replace with doRaytrace in block?
    //TODO optimize...
    public static Direction getInteractSide(Direction side, float x, float y, float z) {
        Direction backSide = side.getOpposite();
        switch (side.getIndex()) {
            case 0:
            case 1:
                if (x < 0.25) {
                    if (z < 0.25) return backSide;
                    if (z > 0.75) return backSide;
                    return Direction.WEST;
                }
                if (x > 0.75) {
                    if (z < 0.25) return backSide;
                    if (z > 0.75) return backSide;
                    return Direction.EAST;
                }
                if (z < 0.25) return Direction.NORTH;
                if (z > 0.75) return Direction.SOUTH;
                return side;
            case 2:
            case 3:
                if (x < 0.25) {
                    if (y < 0.25) return backSide;
                    if (y > 0.75) return backSide;
                    return Direction.WEST;
                }
                if (x > 0.75) {
                    if (y < 0.25) return backSide;
                    if (y > 0.75) return backSide;
                    return Direction.EAST;
                }
                if (y < 0.25) return Direction.DOWN;
                if (y > 0.75) return Direction.UP;
                return side;
            case 4:
            case 5:
                if (z < 0.25) {
                    if (y < 0.25) return backSide;
                    if (y > 0.75) return backSide;
                    return Direction.NORTH;
                }
                if (z > 0.75) {
                    if (y < 0.25) return backSide;
                    if (y > 0.75) return backSide;
                    return Direction.SOUTH;
                }
                if (y < 0.25) return Direction.DOWN;
                if (y > 0.75) return Direction.UP;
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

        Set<BlockPos> set = new HashSet<>();
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

    /**
     * Custom Block Breaking implementation, normally used when breaking extra blocks during/after onBlockDestroyed
     * @param world World instance
     * @param player Player instance, preferably not a ClientPlayerEntity as BlockBreakEvent won't be fired
     * @param stack Player's heldItemStack
     * @param pos BlockPos of the block that is about to be destroyed
     * @param damage Damage that should be taken for the ItemStack
     * @return true if block is successfully broken, false if not
     */
    public static boolean breakBlock(World world, @Nullable PlayerEntity player, ItemStack stack, BlockPos pos, int damage) {
        BlockState state = world.getBlockState(pos);
        int exp = 0;
        if (!world.isRemote) {
            ServerPlayerEntity serverPlayer = ((ServerPlayerEntity) player);
            exp = ForgeHooks.onBlockBreakEvent(world, serverPlayer.interactionManager.getGameType(), serverPlayer, pos);
        }
        if (exp == -1) return false;
        stack.damageItem(state.getBlockHardness(world, pos) != 0.0F ? damage : 0, player, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        boolean destroyed = world.removeBlock(pos, false);// world.func_225521_a_(pos, !player.isCreative(), player);
        if (destroyed && state.canHarvestBlock(world, pos, player)) state.getBlock().harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
        if (exp > 0) state.getBlock().dropXpOnBlockBreak(world, pos, exp);
        return destroyed;
    }

    /**
     * AntimatterToolType sensitive variant of BlockState::isToolEffective
     * @param type AntimatterToolType object
     * @param state BLockState that is being checked against
     * @return true if tool is effective by checking blocks or materials list of its AntimatterToolType
     */
    @Deprecated
    public static boolean isToolEffective(AntimatterToolType type, BlockState state) {
        return type.getToolTypes().stream().anyMatch(state::isToolEffective) || type.getEffectiveBlocks().contains(state.getBlock()) || type.getEffectiveMaterials().contains(state.getMaterial());
    }

    /**
     * Performs tree logging. If Configs.GAMEPLAY.TREE_DETECTION is true, it will do a more complex search for branches, if set to false, it will do a normal vertical loop only
     * @param stack Player's heldItem
     * @param start onBlockDestroy's BlockPos
     * @param player ServerPlayerEntity instance
     * @param world World instance
     * @return if tree logging was successful
     */
    public static boolean treeLogging(@Nonnull IAntimatterTool tool, @Nonnull ItemStack stack, @Nonnull BlockPos start, @Nonnull PlayerEntity player, @Nonnull World world) {
        if (!Configs.GAMEPLAY.TREE_DETECTION) {
            BlockPos.Mutable tempPos = new BlockPos.Mutable(start);
            for (int y = start.getY() + 1; y < start.getY() + Configs.GAMEPLAY.AXE_TIMBER_MAX; y++) {
                if (stack.getDamage() < 2) return false;
                tempPos.move(Direction.UP);
                BlockState state = world.getBlockState(tempPos);
                if (state.isAir(world, tempPos) || !ForgeHooks.canHarvestBlock(state, player, world, tempPos)) return false;
                else if (state.getBlock().isIn(BlockTags.LOGS)) {
                    breakBlock(world, player, stack, tempPos, tool.getType().getUseDurability());
                }
            }
        }
        else {
            LinkedList<BlockPos> blocks = new LinkedList<>();
            Set<BlockPos> visited = new HashSet<>();
            int amount = Configs.GAMEPLAY.AXE_TIMBER_MAX;
            blocks.add(start);
            BlockPos pos;
            Direction[] dirs = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
            while (amount > 0) {
                if (blocks.isEmpty() || stack.getDamage() < 2) return false;
                pos = blocks.remove();
                if (!visited.add(pos)) continue;
                if (!world.getBlockState(pos).getBlock().isIn(BlockTags.LOGS)) continue;
                for (Direction dir : dirs) {
                    BlockPos dirPos = pos.offset(dir);
                    if (!visited.contains(dirPos)) blocks.add(dirPos);
                }
                for (int x = 0; x < 3; x++)  {
                    for (int z = 0; z < 3; z++) {
                        BlockPos acaciaPos = pos.add(-1 + x, 1, -1 + z);
                        if (!visited.contains(acaciaPos)) blocks.add(acaciaPos);
                    }
                }
                amount--;
                if (pos.equals(start)) continue;
                boolean breakBlock = breakBlock(world, player, stack, pos, tool.getType().getUseDurability());
                if (!breakBlock) break;
            }
        }
        return true;
    }

    /**
     * Gets harvestables out of a ImmutableSet of block positions, this is IAntimatterTool sensitive, and will not work for normal ItemStacks, for that, check out BlockState#isToolEffective
     * @param world World instance of the PlayerEntity
     * @param player PlayerEntity that is breaking the blocks
     * @param column vertical amount of blocks
     * @param row horizontal amount of blocks
     * @param depth depth amount of blocks
     * @return set of harvestable BlockPos in the specified range with specified player
     */
    public static ImmutableSet<BlockPos> getHarvestableBlocksToBreak(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull IAntimatterTool tool, int column, int row, int depth) {
        ImmutableSet<BlockPos> totalBlocks = getBlocksToBreak(world, player, column, row, depth);
        return totalBlocks.stream().filter(b -> isToolEffective(tool.getType(), world.getBlockState(b))).collect(ImmutableSet.toImmutableSet());
    }

    /**
     * Gets blocks to be broken in a column (radius), row (radius) and depth. This is axis-sensitive
     * @param world = World instance of the PlayerEntity
     * @param player = PlayerEntity that is breaking the blocks
     * @param column = vertical amount of blocks
     * @param row = horizontal amount of blocks
     * @param depth = depth amount of blocks
     * @return set of BlockPos in the specified range
     */
    public static ImmutableSet<BlockPos> getBlocksToBreak(@Nonnull World world, @Nonnull PlayerEntity player, int column, int row, int depth) {
        Vec3d lookPos = player.getEyePosition(1), rotation = player.getLook(1), realLookPos = lookPos.add(rotation.x * 5, rotation.y * 5, rotation.z * 5);
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
                        if (x == 0 && y == 0 && z == 0) continue;
                        else blockPositions.add(result.getPos().add(x, isDown ? y : -y, z));
                    }
                }
            }
        }
        else { // FaceAxis - Horizontal
            boolean isX = faceAxis == Direction.Axis.X;
            boolean isNegative = faceAxisDir == Direction.AxisDirection.NEGATIVE;
            for (int x = 0; x < depth; x++) {
                for (int y = -column; y <= column; y++) {
                    for (int z = -row; z <= row; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        else blockPositions.add(result.getPos().add(isX ? (isNegative ? x : -x) : (isNegative ? z : -z), y, isX ? (isNegative ? z : -z) : (isNegative ? x : -x)));
                    }
                }
            }
        }
        return blockPositions.build();
    }

    /**
     * Scrappy but efficient way of determining an DyeColor from mere RGB values
     * @param rgb int colour
     * @return DyeColor that is the closest to the RGB input
     */
    public static DyeColor determineColour(int rgb) {
        Color colour = new Color(rgb);
        Map<Double, DyeColor> distances = new HashMap<>();
        for (DyeColor dyeColour : DyeColor.values()) {
            Color enumColour = new Color(dyeColour.getColorValue());
            double distance = (colour.getRed() - enumColour.getRed()) * (colour.getRed() - enumColour.getRed())
                + (colour.getGreen() - enumColour.getGreen()) * (colour.getGreen() - enumColour.getGreen())
                + (colour.getBlue() - enumColour.getBlue()) * (colour.getBlue() - enumColour.getBlue());
            distances.put(distance, dyeColour);
        }
        return distances.get(Collections.min(distances.keySet()));
    }
    
    public static String lowerUnderscoreToUpperSpaced(String string) {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string)), ' ');
    }

    public static String underscoreToUpperCamel(String string) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string);
    }

    /**
     * Used primarily in chemical formula tooltips
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
        int index = string.indexOf("_");
        if (index != -1) return String.join("", string.substring(index + 1), "_", string.substring(0, index));
        return string;
    }

    public static String getConventionalMaterialType(MaterialType<?> type) {
        String id = type.getId();
        int index = id.indexOf("_");
        if (index != -1) {
            id = String.join("", id.substring(index + 1), "_", id.substring(0, index), "s");
            if (id.contains("crushed")) id = StringUtils.replace(id, "crushed", "crushed_ore");
            return id;
        }
        else if (id.contains("crushed")) return StringUtils.replace(id, "crushed", "crushed_ores");
        return id.charAt(id.length() - 1) == 's' ? id.concat("es") : id.concat("s");
    }

    /**
     * Redirects an ItemTag to a BlockTag
     * @param tag a ItemTag, preferably already created
     * @return BlockTag variant of the ItemTag
     */
    public static Tag<Block> itemToBlockTag(Tag<Item> tag) {
        return new BlockTags.Wrapper(tag.getId());
    }

    /**
     * Redirects an BlockTag to a ItemTag
     * @param tag a BlockTag, preferably already created
     * @return ItemTag variant of the BlockTag
     */
    public static Tag<Item> blockToItemTag(Tag<Block> tag) {
        return new ItemTags.Wrapper(tag.getId());
    }

    /**
     * @param loc ResourceLocation of a BlockTag, can be new or old
     * @return BlockTag
     */
    public static Tag<Block> getBlockTag(ResourceLocation loc) {
        return new BlockTags.Wrapper(loc);
    }

    /**
     * @param name name of a BlockTag, can be new or old, has the namespace "forge" attached
     * @return BlockTag
     */
    public static Tag<Block> getForgeBlockTag(String name) {
        return getBlockTag(new ResourceLocation("forge", name));
    }

    /**
     * @param loc ResourceLocation of a ItemTag, can be new or old
     * @return ItemTag
     */
    public static Tag<Item> getItemTag(ResourceLocation loc) {
        return new ItemTags.Wrapper(loc);
    }

    /**
     * @param name name of a ItemTag, can be new or old, has the namespace "forge" attached
     * @return ItemTag
     */
    public static Tag<Item> getForgeItemTag(String name) {
        return getItemTag(new ResourceLocation("forge", name));
    }

    public static String[] getLocalizedMaterialType(MaterialType<?> type) {
        String id = type.getId();
        int index = id.indexOf("_");
        if (index != -1) {
            String joined = String.join("", id.substring(index + 1), "_", id.substring(0, index));
            return lowerUnderscoreToUpperSpaced(joined).split(" ");
        }
        return new String[]{ lowerUnderscoreToUpperSpaced(id).replace('_', ' ') };
    }

    public static String getLocalizedType(IAntimatterObject type) {
        String id = type.getId();
        int index = id.indexOf("_");
        if (index != -1) {
            if (type instanceof MaterialType) {
                String joined = String.join("", id.substring(index + 1), "_", id.substring(0, index));
                return StringUtils.replaceChars(lowerUnderscoreToUpperSpaced(joined), '_', ' ');
            }
            return StringUtils.replaceChars(lowerUnderscoreToUpperSpaced(id),'_', ' ');
        }
        return StringUtils.capitalize(id);
    }

    /**
     * @return an empty instance of Recipe
     */
    public static Recipe getEmptyRecipe() {
        return new Recipe(new ItemStack[0], new ItemStack[0], new FluidStack[0], new FluidStack[0], 1, 1, 0);
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
