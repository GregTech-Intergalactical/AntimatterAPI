package muramasa.antimatter.recipe;

import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRecipe extends net.minecraft.world.item.crafting.Recipe<Container>{
    boolean isValid();
    void invalidate();

    int getAmps();

    void addChances(double[] chances);

    void setHidden(boolean hidden);

    void addTags(Set<RecipeTag> tags);

    boolean hasInputItems();

    boolean hasOutputItems();

    boolean hasInputFluids();

    boolean hasOutputFluids();

    boolean hasChances();

    void setIds(ResourceLocation id, String map);

    void setId(ResourceLocation id);

    void sortInputItems();

    List<Ingredient> getInputItems();

    ItemStack[] getOutputItems();

    ItemStack[] getOutputItems(boolean chance);

    /**
     * Returns a list of items not bound by chances.
     *
     * @return list of items.
     */
    ItemStack[] getFlatOutputItems();

    //Note: does call get().
    boolean hasSpecialIngredients();

    @Nonnull
    List<FluidIngredient> getInputFluids();

    @Nullable
    FluidStack[] getOutputFluids();

    int getDuration();

    long getPower();

    @Nullable
    double[] getChances();

    default long getTotalPower(){
        return getDuration() * getPower();
    }
    int getSpecialValue();

    boolean isHidden();

    Set<RecipeTag> getTags();

    Map<ItemStack, Double> getChancesWithStacks();


}
