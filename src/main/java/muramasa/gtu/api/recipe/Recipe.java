package muramasa.gtu.api.recipe;

import muramasa.gtu.api.util.Utils;
import muramasa.gtu.Ref;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class Recipe {

    private ItemStack[] itemsInput, itemsOutput;
    private FluidStack[] fluidsInput, fluidsOutput;
    private int duration, special;
    private long power;
    private int[] chances;
    private boolean hidden;

    public Recipe(ItemStack[] stacksInput, ItemStack[] stacksOutput, FluidStack[] fluidsInput, FluidStack[] fluidsOutput, int duration, long power, int special) {
        this.itemsInput = stacksInput;
        this.itemsOutput = stacksOutput;
        this.duration = duration;
        this.power = power;
        this.special = special;
        this.fluidsInput = fluidsInput;
        this.fluidsOutput = fluidsOutput;
    }

    public void addChances(int[] chances) {
        this.chances = chances;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean hasInputItems() {
        return itemsInput != null && itemsInput.length > 0;
    }

    public boolean hasOutputItems() {
        return itemsOutput != null && itemsOutput.length > 0;
    }

    public boolean hasInputFluids() {
        return fluidsInput != null && fluidsInput.length > 0;
    }

    public boolean hasOutputFluids() {
        return fluidsOutput != null && fluidsOutput.length > 0;
    }

    @Nullable
    public ItemStack[] getInputItems() {
        return hasInputItems() ? itemsInput.clone() : null;
    }

    @Nullable
    public ItemStack[] getOutputItems() {
        if (hasOutputItems()) {
            ItemStack[] outputs = itemsOutput.clone();
            if (chances != null) {
                ArrayList<ItemStack> evaluated = new ArrayList<>();
                for (int i = 0; i < outputs.length; i++) {
                    if (Ref.RNG.nextInt(100) < chances[i]) {
                        evaluated.add(outputs[i].copy());
                    }
                }
                outputs = evaluated.toArray(new ItemStack[0]);
            }
            return outputs;
        }
        return null;
    }

    @Nullable
    public ItemStack[] getOutputItemsJEI() {
        if (hasOutputItems()) {
            ItemStack[] outputs = itemsOutput.clone();
            if (chances != null) {
                for (int i = 0; i < outputs.length; i++) {
                    if (chances[i] >= 100) continue;
                    Utils.addChanceTag(outputs[i], chances[i]);
                }
            }
            return outputs;
        }
        return null;
    }

    @Nullable
    public FluidStack[] getInputFluids() {
        return hasInputFluids() ? fluidsInput.clone() : null;
    }

    @Nullable
    public FluidStack[] getOutputFluids() {
        return hasOutputFluids() ? fluidsOutput.clone() : null;
    }

    public int getDuration() {
        return duration;
    }

    public long getPower() {
        return power;
    }

    public long getTotalPower() {
        return getDuration() * getPower();
    }

    public int getSpecialValue() {
        return special;
    }

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (itemsInput != null) {
            builder.append("\nInput Items: {");
            for (int i = 0; i < itemsInput.length; i++) {
                builder.append(itemsInput[i].getDisplayName() + " x" + itemsInput[i].getCount());
                if (i != itemsInput.length - 1) builder.append(", ");
            }
            builder.append("}\n");
        }
        if (itemsOutput != null) {
            builder.append("Output Items: {");
            for (int i = 0; i < itemsOutput.length; i++) {
                builder.append(itemsOutput[i].getDisplayName() + " x" + itemsOutput[i].getCount());
                if (i != itemsOutput.length - 1) builder.append(", ");
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
