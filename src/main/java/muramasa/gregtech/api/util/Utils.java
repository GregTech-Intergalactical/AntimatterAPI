package muramasa.gregtech.api.util;

import muramasa.gregtech.api.recipe.Recipe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class Utils {

    private static Random RNG = new Random();
    private static DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getInstance(Locale.US);
    private static DecimalFormatSymbols DECIMAL_SYMBOLS = DECIMAL_FORMAT.getDecimalFormatSymbols();

    public static ItemStack[] EMPTY = new ItemStack[]{ItemStack.EMPTY};

    static {
        DECIMAL_SYMBOLS.setGroupingSeparator(' ');
    }

    public static String getString(ItemStack stack) {
        return !stack.isEmpty() ? stack.getUnlocalizedName() : ""; //TODO 1.13: also append NBT
    }

    public static String getString(FluidStack fluid) {
        return fluid != null ? fluid.getUnlocalizedName() : "";
    }

    public static String getString(ItemStack stack, FluidStack fluid) {
        return getString(stack) + getString(fluid);
    }

    public static boolean equals(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem() && b.getMetadata() == b.getMetadata();
    }

    public static boolean equals(FluidStack a, FluidStack b) {
        return a.isFluidEqual(b);
    }

    public static ItemStack[] arr(ItemStack... stacks) {
        return stacks;
    }

    public static FluidStack[] arr(FluidStack... fluids) {
        return fluids;
    }

    public static ItemStack ca(int amount, ItemStack stack) {
        stack.setCount(amount);
        return stack;
    }

    public static boolean areStacksValid(ItemStack... stacks) {
        if (stacks == null) return false;
        for (int i = 0; i < stacks.length; i++) {
            //TODO remove null check. Due to RecipeAdder passing stack arrays with null items
            if (stacks[i] == null || stacks[i].isEmpty()) return false;
        }
        return true;
    }

    public static boolean areStacksValid(ItemStack[]... stackArrays) {
        for (int i = 0; i < stackArrays.length; i++) {
            if (!areStacksValid(stackArrays[i])) return false;
        }
        return true;
    }

    public static boolean areFluidsValid(FluidStack... fluids) {
        if (fluids == null) return false;
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

    public static boolean doStacksMatch(ItemStack[] a, ItemStack[] b) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (equals(a[i], b[j])) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= a.length;
    }

    public static boolean doStacksMatchAndSizeValid(ItemStack[] a, ItemStack[] b) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (equals(a[i], b[j]) && b[j].getCount() >= a[i].getCount()) {
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
                if (equals(a[i], b[j]) && b[j].amount >= a[i].amount) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount >= a.length;
    }
//
    public static boolean canStacksFit(ItemStack[] a, ItemStack[] b) {
        return getSpaceForStacks(a, b) >= a.length;
    }

    public static int getSpaceForStacks(ItemStack[] a, ItemStack[] b) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (b[j].isEmpty() || (equals(a[i], b[j]) && b[j].getCount() + a[i].getCount() <= b[j].getMaxStackSize())) {
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount;
    }

//    public static boolean canFluidsFit(FluidStack[] a, FluidStack[] b) {
//        return getSpaceForFluids(a, b) >= a.length;
//    }
//
//    public static int getSpaceForFluids(FluidStack[] a, FluidStack[] b) {
//        int matchCount = 0;
//        for (int i = 0; i < a.length; i++) {
//            for (int j = 0; j < b.length; j++) {
//                if ((equals(a[i], b[j]) && b[j].amount + a[i].amount <= b[j].getMaxStackSize())) {
//                    matchCount++;
//                    break;
//                }
//            }
//        }
//        return matchCount;
//    }

//    public static boolean isStacksValidForRecipe(Recipe recipe, ItemStack[] inputs) {
//        int matchCount = 0;
//        for (int i = 0; i < recipe.getInputStacks().length; i++) {
//            for (int j = 0; j < inputs.length; j++) {
//                if (equals(recipe.getInputStacks()[i], inputs[j]) && inputs[j].getCount() >= recipe.getInputStacks()[i].getCount()) {
//                    matchCount++;
//                    break;
//                }
//            }
//        }
//        return recipe.getInputStacks().length == matchCount;
//    }

//    public static boolean isStacksCountMoreOrEqual(Recipe recipe, ItemStack[] inputs) {
//        int matchCount = 0;
////        System.out.println(recipe.getInputStacks().length + " - " + inputs.length);
////        System.out.println(inputs);
//        if (recipe.getInputStacks().length != inputs.length) return false;
//        for (int i = 0; i < recipe.getInputStacks().length; i++) {
//            for (int j = 0; j < inputs.length; j++) {
//                if (inputs[j].getCount() >= recipe.getInputStacks()[i].getCount()) {
//                    matchCount++;
//                    break;
//                }
//            }
//        }
//        System.out.println("MC: " + matchCount);
//        return recipe.getInputStacks().length == matchCount;
//    }

//    public static boolean doStacksMatch(ItemStack[] a, ItemStack[] b) {
//        for (int i = 0; i < a.length; i++) {
//            if (!equals(a[i], b[i])) return false;
//        }
//        return true;
//    }
//
//    public static boolean areStacksEmpty(ItemStack[] a) {
//        for (int i = 0; i < a.length; i++) {
//            if (!(a[i] == ItemStack.EMPTY)) return false;
//        }
//        return true;
//    }

    public static boolean doFluidsMatch(Recipe recipe, FluidStack... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            if (!equals(inputs[i], recipe.getInputFluids()[i])) return false;
        }
        return true;
    }

    public static String formatNumber(int aNumber) {
        return DECIMAL_FORMAT.format(aNumber);
    }

    public static int getRNG(int bound) {
        return RNG.nextInt(bound);
    }

    public static void seedRNG(int seed) {
        RNG.setSeed(seed);
    }

//    public static boolean hasFlag(long value, long flag) {
//        return (value & flag) != 0;
//    }

    public static TileEntity getTile(IBlockAccess blockAccess, BlockPos pos) { //Safe version of world.getTileEntity
        if (blockAccess instanceof ChunkCache) {
            return ((ChunkCache) blockAccess).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
        } else {
            return blockAccess.getTileEntity(pos);
        }
    }

//    public static long addFlag(long value, long flag) {
//        value |= flag;
//        return value;
//    }

    public static long removeFlag(long value, long flag) {
        value &= ~flag;
        return value;
    }

    //TODO replace with doRaytrace in block?
    //TODO optimize...
    public static EnumFacing determineInteractionSide(EnumFacing side, float aX, float aY, float aZ) {
        EnumFacing backSide = side.getOpposite();
        switch (side.getIndex()) {
            case 0:
            case 1:
                if (aX < 0.25) {
                    if (aZ < 0.25) return backSide;
                    if (aZ > 0.75) return backSide;
                    return EnumFacing.WEST;
                }
                if (aX > 0.75) {
                    if (aZ < 0.25) return backSide;
                    if (aZ > 0.75) return backSide;
                    return EnumFacing.EAST;
                }
                if (aZ < 0.25) return EnumFacing.NORTH;
                if (aZ > 0.75) return EnumFacing.SOUTH;
                return side;
            case 2:
            case 3:
                if (aX < 0.25) {
                    if (aY < 0.25) return backSide;
                    if (aY > 0.75) return backSide;
                    return EnumFacing.WEST;
                }
                if (aX > 0.75) {
                    if (aY < 0.25) return backSide;
                    if (aY > 0.75) return backSide;
                    return EnumFacing.EAST;
                }
                if (aY < 0.25) return EnumFacing.DOWN;
                if (aY > 0.75) return EnumFacing.UP;
                return side;
            case 4:
            case 5:
                if (aZ < 0.25) {
                    if (aY < 0.25) return backSide;
                    if (aY > 0.75) return backSide;
                    return EnumFacing.NORTH;
                }
                if (aZ > 0.75) {
                    if (aY < 0.25) return backSide;
                    if (aY > 0.75) return backSide;
                    return EnumFacing.SOUTH;
                }
                if (aY < 0.25) return EnumFacing.DOWN;
                if (aY > 0.75) return EnumFacing.UP;
                return side;
        }
        return side;
    }
}
