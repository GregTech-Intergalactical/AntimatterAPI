package muramasa.antimatter.datagen.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muramasa.antimatter.recipe.material.MaterialSerializer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import speiger.src.collections.ints.lists.IntList;
import speiger.src.collections.ints.maps.impl.hash.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class AntimatterShapedRecipeBuilder {

    private final List<ItemStack> result;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advBuilder = Advancement.Builder.builder();
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
    public static AntimatterShapedRecipeBuilder shapedRecipe(IItemProvider result) {
        return new AntimatterShapedRecipeBuilder(new ItemStack(result, 1));
    }

    public static AntimatterShapedRecipeBuilder shapedRecipe(List<ItemStack> result) {
        return new AntimatterShapedRecipeBuilder(result);
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static AntimatterShapedRecipeBuilder shapedRecipe(IItemProvider result, int count) {
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
    public AntimatterShapedRecipeBuilder key(Character symbol, ITag<Item> tag) {
        return this.key(symbol, Ingredient.fromTag(tag));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public AntimatterShapedRecipeBuilder key(Character symbol, IItemProvider item) {
        return this.key(symbol, Ingredient.fromItems(item));
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
    public AntimatterShapedRecipeBuilder addCriterion(String name, ICriterionInstance criterion) {
        this.advBuilder.withCriterion(name, criterion);
        return this;
    }

    public AntimatterShapedRecipeBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumer) {
        this.build(consumer, ForgeRegistries.ITEMS.getKey(this.result.get(0).getItem()));
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
     * the result.
     */
    public void build(Consumer<IFinishedRecipe> consumer, String save) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.result.get(0).getItem());
        if (new ResourceLocation(save).equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        }
        else {
            this.build(consumer, new ResourceLocation(save));
        }
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        this.validate(id);
        this.advBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumer.accept(new Result(id, this.result.get(0), this.group == null ? "" : this.group, this.pattern, this.key, this.advBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.get(0).getItem().getGroup().getPath() + "/" + id.getPath())));
    }

    public void buildTool(Consumer<IFinishedRecipe> consumer, ResourceLocation builder, String id) {
        buildTool(consumer, builder, new ResourceLocation(id));
    }
    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void buildTool(Consumer<IFinishedRecipe> consumer, ResourceLocation builder, ResourceLocation id) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.result.get(0).getItem());
        if (id.equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + id + " should remove its 'save' argument");
        }
        this.validate(id);
        this.advBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        ItemGroup group = this.result.get(0).getItem().getGroup();
        String groupId = group != null ? group.getPath() : "";
        consumer.accept(new ToolResult(builder, id, this.result, this.group == null ? "" : this.group, this.pattern, this.key, this.advBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + groupId + "/" + id.getPath())));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void validate(ResourceLocation id) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        }
        else if (this.result.get(0).isItemEqual(ItemStack.EMPTY)) {
            throw new IllegalStateException("Resulting ItemStack cannot be empty!");
        }
        else {
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

    public static class Result implements IFinishedRecipe {

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
        public void serialize(JsonObject json) {
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
                jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().serialize());
            }
            json.add("key", jsonobject);
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

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return IRecipeSerializer.CRAFTING_SHAPED;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return this.advBuilder.serialize();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return this.advId;
        }
    }


    public static class ToolResult extends Result {

        private final ResourceLocation builderId;
        private final List<ItemStack> result;

        public ToolResult(ResourceLocation builderId, ResourceLocation id, List<ItemStack> result, String group, List<String> pattern, Map<Character, Ingredient> key, Advancement.Builder advBuilder, ResourceLocation advId) {
            super(id, result.get(0), group, pattern, key, advBuilder, advId);
            this.builderId = builderId;
            this.result = result;
        }

        @Override
        public void serialize(JsonObject json) {
            super.serialize(json);
            json.addProperty("builder", builderId.toString());
            JsonArray arr = new JsonArray();
            result.forEach(el -> {
                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("item", ForgeRegistries.ITEMS.getKey(el.getItem()).toString());
                if (el.getCount() > 1) {
                    resultObj.addProperty("count", el.getCount());
                }
                arr.add(resultObj);
            });
            json.add("output", arr);
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return MaterialSerializer.INSTANCE;
        }
    }
}
