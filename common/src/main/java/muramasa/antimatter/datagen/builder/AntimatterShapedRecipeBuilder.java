package muramasa.antimatter.datagen.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import muramasa.antimatter.recipe.container.ContainerItemShapedRecipe;
import muramasa.antimatter.recipe.material.MaterialSerializer;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class AntimatterShapedRecipeBuilder {

    private final List<ItemStack> result;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advBuilder = Advancement.Builder.advancement();
    private final Int2ObjectOpenHashMap<IntList> materialSlots = new Int2ObjectOpenHashMap<>();
    private String group;

    public AntimatterShapedRecipeBuilder(ItemStack result) {
        this.result = Collections.singletonList(result);
    }

    public AntimatterShapedRecipeBuilder(List<ItemStack> result) {
        this.result = result;
    }


    /**
     * Creates a new builder for a shaped recipe.
     */
    public static AntimatterShapedRecipeBuilder shapedRecipe(ItemLike result) {
        return new AntimatterShapedRecipeBuilder(new ItemStack(result, 1));
    }

    public static AntimatterShapedRecipeBuilder shapedRecipe(List<ItemStack> result) {
        return new AntimatterShapedRecipeBuilder(result);
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static AntimatterShapedRecipeBuilder shapedRecipe(ItemLike result, int count) {
        return new AntimatterShapedRecipeBuilder(new ItemStack(result, count));
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static AntimatterShapedRecipeBuilder shapedRecipe(ItemStack result) {
        return new AntimatterShapedRecipeBuilder(result);
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public AntimatterShapedRecipeBuilder key(Character symbol, TagKey<Item> tag) {
        return this.key(symbol, Ingredient.of(tag));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public AntimatterShapedRecipeBuilder key(Character symbol, ItemLike item) {
        return this.key(symbol, Ingredient.of(item));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public AntimatterShapedRecipeBuilder key(Character symbol, Ingredient ingredient) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredient);
            return this;
        }
    }

    /**
     * Adds a new entry to the patterns for this recipe.
     */
    public AntimatterShapedRecipeBuilder patternLine(String pattern) {
        if (!this.pattern.isEmpty() && pattern.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.pattern.add(pattern);
            return this;
        }
    }

    /**
     * Adds a criterion needed to unlock the recipe.
     */
    public AntimatterShapedRecipeBuilder addCriterion(String name, CriterionTriggerInstance criterion) {
        this.advBuilder.addCriterion(name, criterion);
        return this;
    }

    public AntimatterShapedRecipeBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    /**
     * Builds this recipe into an {@link FinishedRecipe}.
     */
    public void build(Consumer<FinishedRecipe> consumer) {
        this.build(consumer, AntimatterPlatformUtils.getIdFromItem(this.result.get(0).getItem()));
    }

    /**
     * Builds this recipe into an {@link FinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
     * the result.
     */
    public void build(Consumer<FinishedRecipe> consumer, String save) {
        ResourceLocation resourcelocation = AntimatterPlatformUtils.getIdFromItem(this.result.get(0).getItem());
        if (new ResourceLocation(save).equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(consumer, new ResourceLocation(save));
        }
    }

    /**
     * Builds this recipe into an {@link FinishedRecipe}.
     */
    public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        this.validate(id);
        this.advBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", new RecipeUnlockedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        consumer.accept(new Result(id, this.result.get(0), this.group == null ? "" : this.group, this.pattern, this.key, this.advBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.get(0).getItem().getItemCategory().getRecipeFolderName() + "/" + id.getPath())));
    }

    public void buildTool(Consumer<FinishedRecipe> consumer, String builder){
        buildTool(consumer, builder, AntimatterPlatformUtils.getIdFromItem(this.result.get(0).getItem()));
    }

    public void buildTool(Consumer<FinishedRecipe> consumer, String builder, String id) {
        buildTool(consumer, builder, new ResourceLocation(id));
    }

    /**
     * Builds this recipe into an {@link FinishedRecipe}.
     */
    public void buildTool(Consumer<FinishedRecipe> consumer, String builder, ResourceLocation id) {
        this.validate(id);
        this.advBuilder.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", new RecipeUnlockedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, id)).rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
        CreativeModeTab group = this.result.get(0).getItem().getItemCategory();
        String groupId = group != null ? group.getRecipeFolderName() : "";
        consumer.accept(new ToolResult(builder, id, this.result, this.group == null ? "" : this.group, this.pattern, this.key, this.advBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + groupId + "/" + id.getPath())));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void validate(ResourceLocation id) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else if (this.result.get(0).sameItem(ItemStack.EMPTY)) {
            throw new IllegalStateException("Resulting ItemStack cannot be empty!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');
            for (String s : this.pattern) {
                for (int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }
                    set.remove(c0);
                }
            }
            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
            } else if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
                throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
            } else if (this.advBuilder.getCriteria().isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + id);
            }
        }
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation id;
        private final ItemStack result;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advBuilder;
        private final ResourceLocation advId;

        public Result(ResourceLocation id, ItemStack result, String group, List<String> pattern, Map<Character, Ingredient> key, Advancement.Builder advBuilder, ResourceLocation advId) {
            this.id = id;
            this.result = result;
            this.group = group;
            this.pattern = pattern;
            this.key = key;
            this.advBuilder = advBuilder;
            this.advId = advId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            JsonArray jsonarray = new JsonArray();
            for (String s : this.pattern) {
                jsonarray.add(s);
            }
            json.add("pattern", jsonarray);
            JsonObject jsonobject = new JsonObject();
            for (Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
                jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }
            json.add("key", jsonobject);
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", AntimatterPlatformUtils.getIdFromItem(this.result.getItem()).toString());
            if (this.result.getCount() > 1) {
                resultObj.addProperty("count", this.result.getCount());
            }
            json.add("result", resultObj);
            if (this.result.hasTag()) {
                resultObj.addProperty("nbt", this.result.getTag().toString());
            }
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ContainerItemShapedRecipe.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advBuilder.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advId;
        }
    }


    public static class ToolResult extends Result {

        private final String builderId;
        private final List<ItemStack> result;

        public ToolResult(String builderId, ResourceLocation id, List<ItemStack> result, String group, List<String> pattern, Map<Character, Ingredient> key, Advancement.Builder advBuilder, ResourceLocation advId) {
            super(id, result.get(0), group, pattern, key, advBuilder, advId);
            this.builderId = builderId;
            this.result = result;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            super.serializeRecipeData(json);
            json.addProperty("builder", builderId);
            JsonArray arr = new JsonArray();
            result.forEach(el -> {
                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("item", AntimatterPlatformUtils.getIdFromItem(el.getItem()).toString());
                if (el.getCount() > 1) {
                    resultObj.addProperty("count", el.getCount());
                }
                arr.add(resultObj);
            });
            json.add("output", arr);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return MaterialSerializer.INSTANCE;
        }
    }
}
