package muramasa.antimatter.recipe;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Ref;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Recipe implements IRecipe<IInventory> {
    private final ItemStack[] itemsOutput;
    @Nonnull
    private final List<RecipeIngredient> itemsInput;

    private final FluidStack[] fluidsInput;
    private final FluidStack[] fluidsOutput;
    private final int duration;
    private final int special;
    private final long power;
    private final int amps;
    private int[] chances;
    private boolean hidden;
    private Set<RecipeTag> tags = new ObjectOpenHashSet<>();
    public ResourceLocation id;
    public ResourceLocation mapId;

    //For jei, have to put here instead of RecipeMapCategory.
    public final Int2ObjectMap<ResourceLocation> tagsToRender = new Int2ObjectOpenHashMap<>();
    //for stack lists
    public final Int2IntMap infoToRender = new Int2IntOpenHashMap();

    public static final IRecipeType<Recipe> RECIPE_TYPE = IRecipeType.register("antimatter:machine_recipe");

    public Recipe(@Nonnull List<RecipeIngredient> stacksInput, ItemStack[] stacksOutput, FluidStack[] fluidsInput, FluidStack[] fluidsOutput, int duration, long power, int special, int amps) {
        this.itemsInput = stacksInput;
        this.itemsOutput = stacksOutput;
        this.duration = duration;
        this.power = power;
        this.special = special;
        this.fluidsInput = fluidsInput;
        this.amps = amps;
        this.fluidsOutput = fluidsOutput;
    }

    public int getAmps() {
        return amps;
    }

    public void addChances(int[] chances) {
        this.chances = chances;
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

    public void setIds(ResourceLocation id, ResourceLocation map) {
        this.id = id;
        this.mapId = map;
    }

    @Nullable
    public List<RecipeIngredient> getInputItems() {
        return hasInputItems() ? itemsInput : null;
    }

    @Nullable
    public List<AntimatterIngredient> compileInput() {
        return hasInputItems() ? itemsInput.stream().map(RecipeIngredient::get).collect(Collectors.toList()) : Collections.emptyList();
    }

    @Nullable
    public ItemStack[] getOutputItems() {
        return getOutputItems(true);
    }

    public ItemStack[] getOutputItems(boolean chance) {
        if (hasOutputItems()) {
            ItemStack[] outputs = itemsOutput.clone();
            if (chances != null) {
                List<ItemStack> evaluated = new ObjectArrayList<>();
                for (int i = 0; i < outputs.length; i++) {
                    if (!chance || Ref.RNG.nextInt(100) < chances[i]) {
                        evaluated.add(outputs[i].copy());
                    }
                }
                outputs = evaluated.toArray(new ItemStack[0]);
            }
            return outputs;
        }
        return null;
    }

    /**
     * Returns a list of items not bound by chances.
     * @return list of items.
     */
    public ItemStack[] getFlatOutputItems() {
        if (hasOutputItems()) {
            ItemStack[] outputs = itemsOutput.clone();
            if (chances != null) {
                List<ItemStack> evaluated = new ObjectArrayList<>();
                for (int i = 0; i < outputs.length; i++) {
                    if (chances[i] < 100) continue;
                    evaluated.add(outputs[i]);
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
                builder.append(itemsInput.get(i).get().getMatchingStacks()[0].getDisplayName()).append(" x").append(itemsInput.get(i).get().getMatchingStacks()[0].getCount());
                if (i != itemsInput.size() - 1) builder.append(", ");
            }
            builder.append(" }\n");
        }
        if (itemsOutput != null) {
            builder.append("Output Items: { ");
            for (int i = 0; i < itemsOutput.length; i++) {
                builder.append(itemsOutput[i].getDisplayName()).append(" x").append(itemsOutput[i].getCount());
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

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id != null ? id : new ResourceLocation(Ref.ID, "default");
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return null;
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return Recipe.RECIPE_TYPE;
    }
}
