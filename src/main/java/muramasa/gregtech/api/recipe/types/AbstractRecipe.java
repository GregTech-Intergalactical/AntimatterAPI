package muramasa.gregtech.api.recipe.types;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class AbstractRecipe {

    protected int duration, power;

    public AbstractRecipe(int duration, int power) {
        this.duration = duration;
        this.power = power;
    }

    abstract ItemStack[] getItemInputs();

    abstract ItemStack[] getItemOutputs();

    abstract FluidStack[] getFluidInputs();

    abstract FluidStack[] getFluidOutputs();

    public int getDuration() {
        return duration;
    }

    public int getPower() {
        return power;
    }

    public int getTotalPower() {
        return duration * power;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nInputs: {");
        for (int i = 0; i < getItemInputs().length; i++) {
            builder.append(getItemInputs()[i].getDisplayName() + " x" + getItemInputs()[i].getCount());
            if (i != getItemInputs().length - 1) builder.append(", ");
        }
        builder.append("}\n");
        builder.append("Outputs: {");
        for (int i = 0; i < getItemOutputs().length; i++) {
            builder.append(getItemOutputs()[i].getDisplayName() + " x" + getItemOutputs()[i].getCount());
            if (i != getItemOutputs().length - 1) builder.append(", ");
        }
        builder.append("}\n");
        return builder.toString();
    }
}
