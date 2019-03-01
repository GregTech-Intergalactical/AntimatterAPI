package muramasa.gregtech.api.recipe;

import muramasa.gregtech.common.utils.Ref;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Random;

public class Recipe {

    private static Random RNG = new Random();

    //TODO split up recipe types (item only, fluid only, mix etc) to optimize memory usage

    private ItemStack[] stacksInput, stacksOutput;
    private FluidStack[] fluidsInput, fluidsOutput;
    private int duration, power;
    private int[] chances;
    private int special;

    public Recipe(ItemStack[] stacksInput, ItemStack[] stacksOutput, int duration, int power, int special) {
        this.stacksInput = stacksInput;
        this.stacksOutput = stacksOutput;
        this.duration = duration;
        this.power = power;
        this.special = special;
    }

    public Recipe(ItemStack[] stacksInput, ItemStack[] stacksOutput, FluidStack[] fluidsInput, FluidStack[] fluidsOutput, int duration, int power, int special) {
        this(stacksInput, stacksOutput, duration, power, special);
        this.fluidsInput = fluidsInput;
        this.fluidsOutput = fluidsOutput;
    }

    public Recipe addChances(int[] chances) {
        //TODO validation
        this.chances = chances;
        return this;
    }

    public boolean hasInputStacks() {
        return stacksInput != null && stacksInput.length > 0;
    }

    public boolean hasOutputStacks() {
        return stacksOutput != null && stacksOutput.length > 0;
    }

    public boolean hasInputFluids() {
        return fluidsInput != null && fluidsInput.length > 0;
    }

    public boolean hasOutputFluids() {
        return fluidsOutput != null && fluidsOutput.length > 0;
    }

    public ItemStack[] getInputStacks() {
        return stacksInput.clone();
    }

    public ItemStack[] getOutputStacks() {
        ItemStack[] outputs = stacksOutput.clone();
        if (chances != null) {
            ArrayList<ItemStack> evaluated = new ArrayList<>();
            for (int i = 0; i < outputs.length; i++) {
                if (RNG.nextInt(100) < chances[i]) {
                    evaluated.add(outputs[i].copy());
                }
            }
            outputs = evaluated.toArray(new ItemStack[0]);
        }
        return outputs;
    }

    public ItemStack[] getOutputStacksJEI() {
        ItemStack[] outputs = stacksOutput.clone();
        if (chances != null) {
            for (int i = 0; i < outputs.length; i++) {
                if (chances[i] >= 100) continue;
                if (!outputs[i].hasTagCompound()) outputs[i].setTagCompound(new NBTTagCompound());
                outputs[i].getTagCompound().setInteger(Ref.KEY_STACK_CHANCE, chances[i]);
            }
        }
        return outputs;
    }

    public FluidStack[] getInputFluids() {
        return fluidsInput.clone();
    }

    public FluidStack[] getOutputFluids() {
        return fluidsOutput.clone();
    }

    public int getDuration() {
        return duration;
    }

    public int getPower() {
        return power;
    }

    public int getTotalPower() {
        return getDuration() * getPower();
    }

    public int getSpecialValue() {
        return special;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (stacksInput != null) {
            builder.append("\nInput Stacks: {");
            for (int i = 0; i < stacksInput.length; i++) {
                builder.append(stacksInput[i].getDisplayName() + " x" + stacksInput[i].getCount());
                if (i != stacksInput.length - 1) builder.append(", ");
            }
            builder.append("}\n");
        }
        if (stacksOutput != null) {
            builder.append("Output Stacks: {");
            for (int i = 0; i < stacksOutput.length; i++) {
                builder.append(stacksOutput[i].getDisplayName() + " x" + stacksOutput[i].getCount());
                if (i != stacksOutput.length - 1) builder.append(", ");
            }
            builder.append("}\n");
        }
        if (fluidsInput != null) {
            builder.append("Input Fluids: {");
            for (int i = 0; i < fluidsInput.length; i++) {
                builder.append(fluidsInput[i].getFluid().getName() + " x" + fluidsInput[i].amount);
                if (i != fluidsInput.length - 1) builder.append(", ");
            }
            builder.append("}\n");
        }
        if (fluidsOutput != null) {
            builder.append("Output Fluids: {");
            for (int i = 0; i < fluidsOutput.length; i++) {
                builder.append(fluidsOutput[i].getFluid().getName() + " x" + fluidsOutput[i].amount);
                if (i != fluidsOutput.length - 1) builder.append(", ");
            }
            builder.append("}\n");
        }
        return builder.toString();
    }
}
