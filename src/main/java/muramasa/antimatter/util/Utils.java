package muramasa.antimatter.util;

import com.google.common.base.CaseFormat;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Utils {

    private static final ConcurrentMap<String, Boolean> MOD_LOADED_CACHE = new ConcurrentHashMap<>();
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
            if (fluids[i] == null) return false;
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
            xRadius = area.x;
            yRadius = area.y;
            zRadius = area.z;
        } else {
            center = origin.offset(side.getOpposite(), area.z);
            if (side.getAxis() == Direction.Axis.Y) {
                xRadius = player.getHorizontalFacing().getAxis() == Direction.Axis.X ? area.y : area.x;
                yRadius = area.z;
                zRadius = player.getHorizontalFacing().getAxis() == Direction.Axis.Z ? area.y : area.x;
            } else {
                xRadius = player.getHorizontalFacing().getAxis() == Direction.Axis.X ? area.z : area.x;
                yRadius = area.y;
                zRadius = player.getHorizontalFacing().getAxis() == Direction.Axis.Z ? area.z : area.x;
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

    //Credit: from Tinkers' Construct
//    public static void breakBlock(ItemStack stack, World world, BlockState state, BlockPos pos, PlayerEntity player) {
//        Block block = state.getBlock();
//        if (player.abilities.isCreativeMode) {
//            block.onBlockHarvested(world, pos, state, player);
//            if (block.removedByPlayer(state, world, pos, player, false)) {
//                block.onBlockDestroyedByPlayer(world, pos, state);
//            }
//            if (!world.isRemote) {
//                ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockChange(world, pos));
//            }
//            return;
//        }
//        stack.onBlockDestroyed(world, state, pos, player);
//
//        if (!world.isRemote) { // server sided handling
//            int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos);
//            if (xp == -1) return;//event cancelled
//
//
//            // serverside we reproduce ItemInWorldManager.tryHarvestBlock
//
//            // ItemInWorldManager.removeBlock
//            block.onBlockHarvested(world, pos, state, player);
//
//            if (block.removedByPlayer(state, world, pos, player, true)){
//                block.onBlockDestroyedByPlayer(world, pos, state);
//                block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
//                block.dropXpOnBlockBreak(world, pos, xp);
//            }
//
//            EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
//            mpPlayer.connection.sendPacket(new SPacketBlockChange(world, pos));
//        } else { // client sided handling
//            // PlayerControllerMP.onPlayerDestroyBlock
//            world.playBroadcastSound(2001, pos, Block.getStateId(state));
//            if (block.removedByPlayer(state, world, pos, player, true)) {
//                block.onBlockDestroyedByPlayer(world, pos, state);
//            }
//            stack.onBlockDestroyed(world, state, pos, player);
//
//            GregTech.PROXY.sendDiggingPacket(pos);
//        }
//    }
    
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
            if (id.contains("crushed")) id = id.replace("crushed", "crushed_ore");
            return id;
        }
        else if (id.contains("crushed")) return id.replace("crushed", "crushed_ores");
        return id.charAt(id.length() - 1) == 's' ? id.concat("es") : id.concat("s");
    }

    public static Tag<Block> itemToBlockTag(Tag<Item> tag) {
        return new BlockTags.Wrapper(tag.getId());
    }

    public static Tag<Item> blockToItemTag(Tag<Block> tag) {
        return new ItemTags.Wrapper(tag.getId());
    }

    public static Tag<Block> getBlockTag(ResourceLocation loc) {
        return new BlockTags.Wrapper(loc);
    }

    public static Tag<Block> getForgeBlockTag(String name) {
        return getBlockTag(new ResourceLocation("forge", name));
    }

    public static Tag<Item> getItemTag(ResourceLocation loc) {
        return new ItemTags.Wrapper(loc);
    }

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
                return lowerUnderscoreToUpperSpaced(joined).replace('_', ' ');
            }
            return lowerUnderscoreToUpperSpaced(id).replace('_', ' ');
        }
        return StringUtils.capitalize(id);
    }

    /**
     * Removes specific features, in specific generation stages, in specific biomes
     * @param biomes set containing biomes wish to remove features from
     * @param stage generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states BlockStates wish to be removed
     */
    public static void removeDecoratedFeaturesFromBiomes(Set<Biome> biomes, GenerationStage.Decoration stage, Feature<?> featureToRemove, BlockState... states) {
        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
            if (!biomes.contains(biome)) continue;
            for (BlockState state : states) {
                biome.getFeatures(stage).removeIf(f -> isDecoratedFeatureDisabled(f, featureToRemove, state));
            }
        }
    }

    /**
     * Removes specific features, in specific generation stages, in all biomes registered
     * @param stage generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states BlockStates wish to be removed
     */
    public static void removeDecoratedFeatureFromAllBiomes(GenerationStage.Decoration stage, Feature<?> featureToRemove, BlockState... states) {
        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
            for (BlockState state : states) {
                biome.getFeatures(stage).removeIf(f -> isDecoratedFeatureDisabled(f, featureToRemove, state));
            }
        }
    }

    /**
     * Check with BlockState in a feature if it is disabled
     */
    public static boolean isDecoratedFeatureDisabled(ConfiguredFeature<?, ?> configuredFeature, Feature<?> featureToRemove, BlockState state) {
        if (configuredFeature.config instanceof DecoratedFeatureConfig) {
            DecoratedFeatureConfig config = (DecoratedFeatureConfig) configuredFeature.config;
            Feature<?> feature = config.feature.feature;
            if (feature == featureToRemove) {
                IFeatureConfig featureConfig = config.feature.config;
                if (featureConfig instanceof OreFeatureConfig) {
                    BlockState configState = ((OreFeatureConfig) featureConfig).state;
                    if (configState != null && state == configState) return true;
                }
                if (featureConfig instanceof BlockStateFeatureConfig) {
                    BlockState configState = ((BlockStateFeatureConfig) featureConfig).field_227270_a_;
                    if (configState != null && state == configState) return true;
                }
            }
        }
        return false;
    }

    public static Recipe getEmptyRecipe() {
        return new Recipe(new ItemStack[0], new ItemStack[0], new FluidStack[0], new FluidStack[0], 1, 1, 0);
    }

    public static void onInvalidData(String msg) {
        if (Ref.DATA_EXCEPTIONS) throw new IllegalStateException(msg);
        Antimatter.LOGGER.error(msg);
    }

    public static void printError(String msg) {
        Antimatter.LOGGER.error("====================================================");
        Antimatter.LOGGER.error(msg);
        Antimatter.LOGGER.error("====================================================");
    }
}
