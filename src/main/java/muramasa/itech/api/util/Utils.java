package muramasa.itech.api.util;

import muramasa.itech.api.recipe.Recipe;
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

    static {
        DECIMAL_SYMBOLS.setGroupingSeparator(' ');
    }

    public static String getString(ItemStack stack) {
        return stack.getUnlocalizedName(); //TODO 1.13: also append NBT
    }

    public static String getString(FluidStack fluidStack) {
        return fluidStack.getUnlocalizedName();
    }

    public static boolean equals(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && stack1.getMetadata() == stack2.getMetadata();
    }

    public static boolean equals(FluidStack fluidStack1, FluidStack fluidStack2) {
        return fluidStack1.isFluidEqual(fluidStack2);
    }

    public static ItemStack[] arr(ItemStack... stacks) {
        return stacks;
    }

    public static FluidStack[] arr(FluidStack... fluidStacks) {
        return fluidStacks;
    }

    public static ItemStack ca(int amount, ItemStack stack) {
        stack.setCount(amount);
        return stack;
    }

    public static boolean areStacksValid(ItemStack... stacks) {
        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i] == null) return false;
        }
        return true;
    }

    public static boolean areStacksValid(ItemStack[]... stackArrays) {
        for (int i = 0; i < stackArrays.length; i++) {
            if (!areStacksValid(stackArrays[i])) return false;
        }
        return true;
    }

    public static boolean areFluidsValid(FluidStack... fluidStacks) {
        for (int i = 0; i < fluidStacks.length; i++) {
            if (fluidStacks[i] == null) return false;
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

//    public static boolean isStacksValidForRecipe(Recipe recipe, ItemStack[] inputs) {
//        int matchCount = 0;
//        for (int i = 0; i < recipe.getInputs().length; i++) {
//            for (int j = 0; j < inputs.length; j++) {
//                if (equals(recipe.getInputs()[i], inputs[j]) && inputs[j].getCount() >= recipe.getInputs()[i].getCount()) {
//                    matchCount++;
//                    break;
//                }
//            }
//        }
//        return recipe.getInputs().length == matchCount;
//    }

//    public static boolean isStacksCountMoreOrEqual(Recipe recipe, ItemStack[] inputs) {
//        int matchCount = 0;
////        System.out.println(recipe.getInputs().length + " - " + inputs.length);
////        System.out.println(inputs);
//        if (recipe.getInputs().length != inputs.length) return false;
//        for (int i = 0; i < recipe.getInputs().length; i++) {
//            for (int j = 0; j < inputs.length; j++) {
//                if (inputs[j].getCount() >= recipe.getInputs()[i].getCount()) {
//                    matchCount++;
//                    break;
//                }
//            }
//        }
//        System.out.println("MC: " + matchCount);
//        return recipe.getInputs().length == matchCount;
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
            if (!equals(inputs[i], recipe.getFluidInputs()[i])) return false;
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

    public static boolean hasFlag(int value, int flag) {
        return (value & flag) != 0;
    }

    public static TileEntity getTile(IBlockAccess blockAccess, BlockPos pos) { //Safe version of world.getTileEntity
        if (blockAccess instanceof ChunkCache) {
            return ((ChunkCache) blockAccess).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
        } else {
            return blockAccess.getTileEntity(pos);
        }
    }

    public static int addFlag(int value, int flag) {
        value |= flag;
        return value;
    }

    public static int removeFlag(int value, int flag) {
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
