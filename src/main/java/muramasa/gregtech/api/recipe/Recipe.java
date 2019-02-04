package muramasa.gregtech.api.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class Recipe {

    private final ItemStack[] inputs, outputs;
    private FluidStack[] fluidInputs, fluidOutputs;
    private int duration, power;
    private int[] chances;

    public Recipe(ItemStack[] inputs, ItemStack[] outputs, int duration, int power) {
        this.inputs = inputs.clone();
        this.outputs = outputs.clone();
        this.duration = duration;
        this.power = power;
    }

    public Recipe(ItemStack[] inputs, ItemStack[] outputs, FluidStack[] fluidInputs, FluidStack[] fluidOutputs, int duration, int power) {
        this(inputs, outputs, duration, power);
        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
    }

    public Recipe addChances(int[] chances) {
        //TODO validation
        this.chances = chances;
        return this;
    }

    public ItemStack[] getInputs() {
        return inputs;
    }

    public ItemStack[] getOutputs() {
//        return chances == null ? outputs : evaluateChances(outputs);
        return outputs;
    }

    public FluidStack[] getFluidInputs() {
        return fluidInputs;
    }

    public FluidStack[] getFluidOutputs() {
        return fluidOutputs;
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

    private static ItemStack[] evaluateChances(ItemStack[] outputs) {
        return outputs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nInputs: {");
        for (int i = 0; i < inputs.length; i++) {
            builder.append(inputs[i].getDisplayName() + " x" + inputs[i].getCount());
            if (i != inputs.length - 1) builder.append(", ");
        }
        builder.append("}\n");
        builder.append("Outputs: {");
        for (int i = 0; i < outputs.length; i++) {
            builder.append(outputs[i].getDisplayName() + " x" + outputs[i].getCount());
            if (i != outputs.length - 1) builder.append(", ");
        }
        builder.append("}\n");
        return builder.toString();
    }
}
