package muramasa.antimatter.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRecipe extends net.minecraft.world.item.crafting.Recipe<Container>{
    boolean isValid();
    void invalidate();

    int getAmps();

    void addOutputChances(int[] chances);

    void addInputChances(int[] chances);

    void setHidden(boolean hidden);

    void addTags(Set<RecipeTag> tags);

    boolean hasInputItems();

    boolean hasOutputItems();

    boolean hasInputFluids();

    boolean hasOutputFluids();

    boolean hasOutputChances();

    boolean hasInputChances();

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

    @NotNull
    List<FluidIngredient> getInputFluids();

    @Nullable
    FluidHolder[] getOutputFluids();

    int getDuration();

    long getPower();

    int @Nullable [] getOutputChances();

    int @Nullable [] getInputChances();

    default long getTotalPower(){
        return getDuration() * getPower();
    }
    int getSpecialValue();

    boolean isHidden();

    boolean isFake();

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
            json.add("inputItems", array);
        }
        array = new JsonArray();
        if (this.getOutputItems(false) != null){
            for (ItemStack stack : this.getOutputItems(false)){
                array.add(RecipeUtil.itemstackToJson(stack));
            }
        }
        if (!array.isEmpty()){
            json.add("outputItems", array);
        }
        array = new JsonArray();
        for (FluidIngredient f : this.getInputFluids()) {
            array.add(f.toJson());
        }
        if (!array.isEmpty()){
            json.add("inputFluids", array);
        }
        array = new JsonArray();
        if (this.getOutputFluids() != null){
            for (FluidHolder stack : this.getOutputFluids()){
                array.add(RecipeUtil.fluidstackToJson(stack));
            }
        }
        if (!array.isEmpty()){
            json.add("outputFluids", array);
        }
        array = new JsonArray();
        json.addProperty("eu", this.getPower());
        json.addProperty("duration", this.getDuration());
        json.addProperty("amps", this.getAmps());
        json.addProperty("special", this.getSpecialValue());
        if (this.hasOutputChances()) {
            for (int d : this.getOutputChances()){
                array.add(d);
            }
        }
        if (!array.isEmpty()){
            json.add("outputChances", array);
        }

        if (this.hasInputChances()) {
            for (int d : this.getInputChances()){
                array.add(d);
            }
        }
        if (!array.isEmpty()){
            json.add("inputChances", array);
        }
        json.addProperty("hidden", this.isHidden());
        json.addProperty("fake", this.isFake());
        json.addProperty("recipeID", this.getId().toString());
        json.addProperty("map", this.getMapId());
        return json;
    }

    List<IRecipeValidator> getValidators();
}
