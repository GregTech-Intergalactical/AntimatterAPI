package muramasa.antimatter.datagen.builder;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AntimatterCookingRecipeBuilder {
    private final ItemStack result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();
    private String group;
    private final SimpleCookingSerializer<?> recipeSerializer;

    private AntimatterCookingRecipeBuilder(ItemStack resultIn, Ingredient ingredientIn, float experienceIn, int cookingTimeIn, SimpleCookingSerializer<?> serializer) {
        this.result = resultIn;
        this.ingredient = ingredientIn;
        this.experience = experienceIn;
        this.cookingTime = cookingTimeIn;
        this.recipeSerializer = serializer;
    }

    public static AntimatterCookingRecipeBuilder cookingRecipe(Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookingTimeIn, SimpleCookingSerializer<?> serializer) {
        return new AntimatterCookingRecipeBuilder(resultIn, ingredientIn, experienceIn, cookingTimeIn, serializer);
    }

    public static AntimatterCookingRecipeBuilder blastingRecipe(Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookingTimeIn) {
        return cookingRecipe(ingredientIn, resultIn, experienceIn, cookingTimeIn, RecipeSerializer.BLASTING_RECIPE);
    }

    public static AntimatterCookingRecipeBuilder smeltingRecipe(Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookingTimeIn) {
        return cookingRecipe(ingredientIn, resultIn, experienceIn, cookingTimeIn, RecipeSerializer.SMELTING_RECIPE);
    }

    public AntimatterCookingRecipeBuilder addCriterion(String name, CriterionTriggerInstance criterionIn) {
        this.advancementBuilder.addCriterion(name, criterionIn);
        return this;
    }

    public void build(Consumer<FinishedRecipe> consumerIn) {
        this.build(consumerIn, Registry.ITEM.getKey(this.result.getItem()));
    }

    public void build(Consumer<FinishedRecipe> consumerIn, String save) {
        ResourceLocation resourcelocation = Registry.ITEM.getKey(this.result.getItem());
        ResourceLocation resourcelocation1 = new ResourceLocation(save);
        if (resourcelocation1.equals(resourcelocation)) {
            throw new IllegalStateException("Recipe " + resourcelocation1 + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, resourcelocation1);
        }
    }

    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        consumerIn.accept(new Result(id, this.group == null ? "" : this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + id.getPath()), this.recipeSerializer));
    }

    /**
     * Makes sure that this obtainable.
     */
    private void validate(ResourceLocation id) {
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancementBuilder;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

        public Result(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookingTimeIn, Advancement.Builder advancementBuilderIn, ResourceLocation advancementIdIn, RecipeSerializer<? extends AbstractCookingRecipe> serializerIn) {
            this.id = idIn;
            this.group = groupIn;
            this.ingredient = ingredientIn;
            this.result = resultIn;
            this.experience = experienceIn;
            this.cookingTime = cookingTimeIn;
            this.advancementBuilder = advancementBuilderIn;
            this.advancementId = advancementIdIn;
            this.serializer = serializerIn;
        }

        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            json.add("ingredient", this.ingredient.toJson());
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", Registry.ITEM.getKey(this.result.getItem()).toString());
            if (this.result.getCount() > 1) {
                resultObj.addProperty("count", this.result.getCount());
            }
            if (this.result.hasTag()) {
                resultObj.addProperty("nbt", this.result.getTag().toString());
            }
            json.add("result", resultObj);
            json.addProperty("experience", this.experience);
            json.addProperty("cookingtime", this.cookingTime);
        }

        public RecipeSerializer<?> getType() {
            return this.serializer;
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancementBuilder.serializeToJson();
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
         * is non-null.
         */
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
