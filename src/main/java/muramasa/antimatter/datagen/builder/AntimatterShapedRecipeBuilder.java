package muramasa.antimatter.datagen.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import muramasa.antimatter.recipe.ToolRecipe;
import muramasa.antimatter.recipe.ingredient.MaterialIngredient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class AntimatterShapedRecipeBuilder {

    private final ItemStack result;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advBuilder = Advancement.Builder.builder();
    private final Int2ObjectOpenHashMap<IntList> materialSlots = new Int2ObjectOpenHashMap<>();
    private boolean toolRecipe = false;
    private String group;

    public AntimatterShapedRecipeBuilder(ItemStack result) {
        this.result = result;
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static AntimatterShapedRecipeBuilder shapedRecipe(IItemProvider result) {
        return new AntimatterShapedRecipeBuilder(new ItemStack(result, 1));
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
     * Adds a key to the recipe pattern.
     */
    public AntimatterShapedRecipeBuilder key(Character symbol, MaterialIngredient ingredient) {
        toolRecipe = true;
        return this.key(symbol, (Ingredient) ingredient);
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
        this.build(consumer, null, ForgeRegistries.ITEMS.getKey(this.result.getItem()));
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
     * the result.
     */
    public void build(Consumer<IFinishedRecipe> consumer, String toolType, String save) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.result.getItem());
        if (new ResourceLocation(save).equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        }
        else {
            this.build(consumer, toolType, new ResourceLocation(save));
        }
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<IFinishedRecipe> consumer, String toolType, ResourceLocation id) {
        this.validate(id);
        this.advBuilder.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(EntityPredicate.AndPredicate.ANY_AND, id)).withRewards(AdvancementRewards.Builder.recipe(id)).withRequirementsStrategy(IRequirementsStrategy.OR);
        consumer.accept(new Result(id, this.result, this.group == null ? "" : this.group, this.pattern, this.key, this.advBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItem().getGroup().getPath() + "/" + id.getPath()), toolRecipe, toolType));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void validate(ResourceLocation id) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        }
        else if (this.result.isItemEqual(ItemStack.EMPTY)) {
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

        //TOOL STUFF
        private final boolean tool;
        private final String toolType;

        public Result(ResourceLocation id, ItemStack result, String group, List<String> pattern, Map<Character, Ingredient> key, Advancement.Builder advBuilder, ResourceLocation advId, boolean tool, String toolType) {
            this.id = id;
            this.result = result;
            this.group = group;
            this.pattern = pattern;
            this.key = key;
            this.advBuilder = advBuilder;
            this.advId = advId;
            this.tool = tool;
            this.toolType = toolType;
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
            if (tool) {
                json.addProperty("tool", toolType);
            }
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return tool ? ToolRecipe.ToolRecipeSerializer.INSTANCE : IRecipeSerializer.CRAFTING_SHAPED;
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

}
