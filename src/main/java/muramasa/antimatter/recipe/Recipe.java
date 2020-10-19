package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Ref;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Recipe {
    private final ItemStack[] itemsOutput;
    @Nonnull
    private final List<AntimatterIngredient> itemsInput;

    private final FluidStack[] fluidsInput;
    private final FluidStack[] fluidsOutput;
    private final int duration;
    private final int special;
    private final long power;
    private int[] chances;
    private boolean hidden;
    private Set<RecipeTag> tags = new ObjectOpenHashSet<>();

    public Recipe(@Nonnull List<AntimatterIngredient> stacksInput, ItemStack[] stacksOutput, FluidStack[] fluidsInput, FluidStack[] fluidsOutput, int duration, long power, int special) {
        this.itemsInput = stacksInput;
        this.itemsOutput = stacksOutput;
        this.duration = duration;
        this.power = power;
        this.special = special;
        this.fluidsInput = fluidsInput;
        this.fluidsOutput = fluidsOutput;
    }

    public int getTagHash() {
        long hash = this.itemsInput.stream().filter(t -> t.tag != null).mapToLong(t -> t.tag.getId().hashCode()).sum();
        return (int) (hash ^(hash >>> 32));
    }

    public void addChances(int[] chances) {
        this.chances = chances;
    }

    public Stream<AntimatterIngredient> getTaggedInput() {
        return this.itemsInput.stream().filter(t -> t.tag != null);
    }

    public Stream<AntimatterIngredient> getStandardInput() {
        return this.itemsInput.stream().filter(t -> t.tag == null);
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void addTags(Set<RecipeTag> tags) {
        this.tags = tags;
    }

    public boolean hasInputItems() {
        return itemsInput.size() > 0;
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

    public boolean hasChances() {
        //TODO change this if we add input chances?
        return chances != null && chances.length == itemsOutput.length;
    }

    @Nullable
    public List<AntimatterIngredient> getInputItems() {
        return hasInputItems() ? itemsInput : null;
    }

    @Nullable
    public ItemStack[] getOutputItems() {
        if (hasOutputItems()) {
            ItemStack[] outputs = itemsOutput.clone();
            if (chances != null) {
                List<ItemStack> evaluated = new ObjectArrayList<>();
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

    @Nullable
    public int[] getChances() {
        return chances;
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

    public Set<RecipeTag> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (itemsInput.size() > 0) {
            builder.append("\nInput Items: { ");
            for (int i = 0; i < itemsInput.size(); i++) {
                if (itemsInput.get(i).tag != null) {
                    builder.append("Tag input: ").append(itemsInput.get(i).tag.getId());
                } else {
                    builder.append(itemsInput.get(i).getMatchingStacks()[0].getDisplayName().getFormattedText()).append(" x").append(itemsInput.get(i).getMatchingStacks()[0].getCount());
                }
                if (i != itemsInput.size() - 1) builder.append(", ");
            }
            builder.append(" }\n");
        }
        if (itemsOutput != null) {
            builder.append("Output Items: { ");
            for (int i = 0; i < itemsOutput.length; i++) {
                builder.append(itemsOutput[i].getDisplayName().getFormattedText()).append(" x").append(itemsOutput[i].getCount());
                if (i != itemsOutput.length - 1) builder.append(", ");
            }
            builder.append(" }\n");
        }
        if (fluidsInput != null) {
            builder.append("Input Fluids: { ");
            for (int i = 0; i < fluidsInput.length; i++) {
                builder.append(fluidsInput[i].getFluid().getRegistryName()).append(": ").append(fluidsInput[i].getAmount()).append("mb");
                if (i != fluidsInput.length - 1) builder.append(", ");
            }
            builder.append(" }\n");
        }
        if (fluidsOutput != null) {
            builder.append("Output Fluids: { ");
            for (int i = 0; i < fluidsOutput.length; i++) {
                builder.append(fluidsOutput[i].getFluid().getRegistryName()).append(": ").append(fluidsOutput[i].getAmount()).append("mb");
                if (i != fluidsOutput.length - 1) builder.append(", ");
            }
            builder.append(" }\n");
        }
        if (chances != null) {
            builder.append("Chances: { ");
            for (int i = 0; i < chances.length; i++) {
                builder.append(chances[i]).append("%");
                if (i != chances.length - 1) builder.append(", ");
            }
            builder.append(" }\n");
        }
        builder.append("Special: ").append(special).append("\n");
        return builder.toString();
    }
}
