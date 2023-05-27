package muramasa.antimatter.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    void addChances(int[] chances);

    void setHidden(boolean hidden);

    void addTags(Set<RecipeTag> tags);

    boolean hasInputItems();

    boolean hasOutputItems();

    boolean hasInputFluids();

    boolean hasOutputFluids();

    boolean hasChances();

    void setIds(ResourceLocation id, String map);

    void setId(ResourceLocation id);

    void setMapId(String map);

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
    int[] getChances();

    default long getTotalPower(){
        return getDuration() * getPower();
    }
    int getSpecialValue();

    boolean isHidden();

    Set<RecipeTag> getTags();

    Map<ItemStack, Integer> getChancesWithStacks();

    String getMapId();

    default JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        for (Ingredient ingredient : this.getInputItems()) {
            array.add(ingredient.toJson());
        }
        if (!array.isEmpty()){
            json.add("item_in", array);
        }
        array = new JsonArray();
        if (this.getOutputItems(false) != null){
            for (ItemStack stack : this.getOutputItems(false)){
                array.add(RecipeUtil.itemstackToJson(stack));
            }
        }
        if (!array.isEmpty()){
            json.add("item_out", array);
        }
        array = new JsonArray();
        for (FluidIngredient f : this.getInputFluids()) {
            array.add(f.toJson());
        }
        if (!array.isEmpty()){
            json.add("fluid_in", array);
        }
        array = new JsonArray();
        if (this.getOutputFluids() != null){
            for (FluidStack stack : this.getOutputFluids()){
                array.add(RecipeUtil.fluidstackToJson(stack));
            }
        }
        if (!array.isEmpty()){
            json.add("fluid_out", array);
        }
        array = new JsonArray();
        json.addProperty("eu", this.getPower());
        json.addProperty("duration", this.getDuration());
        json.addProperty("amps", this.getAmps());
        json.addProperty("special", this.getSpecialValue());
        if (this.hasChances()) {
            for (double d : this.getChances()){
                array.add(d);
            }
        }
        if (!array.isEmpty()){
            json.add("chances", array);
        }
        json.addProperty("recipeID", this.getId().toString());
        json.addProperty("map", this.getMapId());
        return json;
    }

    List<IRecipeValidator> getValidators();
}
