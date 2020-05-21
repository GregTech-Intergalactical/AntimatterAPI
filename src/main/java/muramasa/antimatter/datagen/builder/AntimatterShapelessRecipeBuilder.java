package muramasa.antimatter.datagen.builder;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class AntimatterShapelessRecipeBuilder {

    private final ItemStack result;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancementBuilder = Advancement.Builder.builder();
    private String group;

    public AntimatterShapelessRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static AntimatterShapelessRecipeBuilder shapelessRecipe(IItemProvider result) {
        return new AntimatterShapelessRecipeBuilder(new ItemStack(result, 1));
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static AntimatterShapelessRecipeBuilder shapelessRecipe(IItemProvider result, int count) {
        return new AntimatterShapelessRecipeBuilder(new ItemStack(result, count));
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static AntimatterShapelessRecipeBuilder shapelessRecipe(ItemStack stack) {
        return new AntimatterShapelessRecipeBuilder(stack);
    }

    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    public AntimatterShapelessRecipeBuilder addIngredient(Tag<Item> tagIn) {
        return this.addIngredient(Ingredient.fromTag(tagIn));
    }

    /**
     * Adds an ingredient of the given item.
     */
    public AntimatterShapelessRecipeBuilder addIngredient(IItemProvider itemIn) {
        return this.addIngredient(itemIn, 1);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public AntimatterShapelessRecipeBuilder addIngredient(IItemProvider itemIn, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.addIngredient(Ingredient.fromItems(itemIn));
        }
        return this;
    }

    /**
     * Adds an ingredient.
     */
    public AntimatterShapelessRecipeBuilder addIngredient(Ingredient ingredientIn) {
        return this.addIngredient(ingredientIn, 1);
    }

    /**
     * Adds an ingredient multiple times.
     */
    public AntimatterShapelessRecipeBuilder addIngredient(Ingredient ingredientIn, int quantity) {
        for (int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredientIn);
        }
        return this;
    }

    /**
     * Adds a criterion needed to unlock the recipe.
     */
    public AntimatterShapelessRecipeBuilder addCriterion(String name, ICriterionInstance criterionIn) {
        this.advancementBuilder.withCriterion(name, criterionIn);
        return this;
    }

    public AntimatterShapelessRecipeBuilder setGroup(String groupIn) {
        this.group = groupIn;
        return this;
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn) {
        this.build(consumerIn, ForgeRegistries.ITEMS.getKey(this.result.getItem()));
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
     * the result.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.result.getItem());
        if (new ResourceLocation(save).equals(resourcelocation)) {
            throw new IllegalStateException("Shapeless Recipe " + save + " should remove its 'save' argument");
        }
        else {
            this.build(consumerIn, new ResourceLocation(save));
        }
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        this.advancementBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumerIn.accept(new AntimatterShapelessRecipeBuilder.Result(id, this.result, this.group == null ? "" : this.group, this.ingredients, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItem().getGroup().getPath() + '/' + id.getPath())));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void validate(ResourceLocation id) {
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public static class Result implements IFinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final String group;
        private final List<Ingredient> ingredients;
        private final Advancement.Builder advBuilder;
        private final ResourceLocation advId;

        public Result(ResourceLocation id, ItemStack result, String group, List<Ingredient> ingredients, Advancement.Builder advBuilder, ResourceLocation advId) {
            this.id = id;
            this.result = result;
            this.group = group;
            this.ingredients = ingredients;
            this.advBuilder = advBuilder;
            this.advId = advId;
        }

        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            JsonArray jsonarray = new JsonArray();
            for (Ingredient ingredient : this.ingredients) {
                jsonarray.add(ingredient.serialize());
            }
            json.add("ingredients", jsonarray);
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result.getItem()).toString());
            if (this.result.getCount() > 1) {
                resultObj.addProperty("count", this.result.getCount());
            }
            json.add("result", resultObj);
            if (this.result.hasTag()) {
                resultObj.addProperty("nbt", this.result.getTag().toString());
            }
        }

        public IRecipeSerializer<?> getSerializer() {
            return IRecipeSerializer.CRAFTING_SHAPELESS;
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getID() {
            return this.id;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @Nullable
        public JsonObject getAdvancementJson() {
            return this.advBuilder.serialize();
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
         * is non-null.
         */
        @Nullable
        public ResourceLocation getAdvancementID() {
            return this.advId;
        }
    }
}