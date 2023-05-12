package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Small wrapper, to avoid typing lazyvalue.
 */
public class RecipeIngredient extends Ingredient {
    protected boolean nonConsume = false;
    protected boolean ignoreNbt = false;

    public RecipeIngredient(Ingredient.Value... value) {
        this(Stream.of(value));
    }

    public RecipeIngredient(Stream<Ingredient.Value> value) {
        super(value);
    }

    public int count() {
        if (getItems().length > 0) {
            return getItems()[0].getCount();
        }
        return 0;
    }

    public static Stream<Ingredient.Value> valuesFromJson(@Nullable JsonElement json){
        if (json != null && !json.isJsonNull()) {
            if (json.isJsonObject()) {
                return Stream.of(valueFromJson(json.getAsJsonObject()));
            } else if (json.isJsonArray()) {
                JsonArray jsonArray = json.getAsJsonArray();
                if (jsonArray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    List<Value> elements = new ArrayList<>();
                    for (JsonElement e : jsonArray) {
                        elements.addAll(valuesFromJson(e).toList());
                    }
                    return elements.stream();
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static RecipeIngredient fromJson(@Nullable JsonElement json) {
        RecipeIngredient ingredient = new RecipeIngredient(valuesFromJson(json));
        if (json instanceof JsonObject object){
            if (object.has("nbt") && object.get("nbt").getAsBoolean()){
                ingredient.setIgnoreNbt();
            }
            if (object.has("noconsume") && object.get("noconsume").getAsBoolean()){
                ingredient.setNoConsume();
            }
        }
        return ingredient;
    }

    public static Ingredient.Value valueFromJson(JsonObject json) {
        if (json.has("values") && json.get("values") instanceof JsonArray array) {
            if (!array.isEmpty()) {
                List<Ingredient.Value> list = new ArrayList<>();
                for (JsonElement e : array) {
                    list.addAll(valuesFromJson(e).toList());
                }
                return new MultiValue(list.stream());
            } else {
                throw new JsonParseException("A Ingredient entry array must not be empty");
            }
        }
        int count = json.has("count") ? json.get("count").getAsInt() : 1;
        if (json.has("item") && json.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        } else if (json.has("item")) {
            Item item = ShapedRecipe.itemFromJson(json);
            return new RecipeValue(new ItemStack(item, count));
        } else if (json.has("tag")) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, resourceLocation);
            return new RecipeValue(tagKey, count);
        } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }

    public static Ingredient fromNetwork(FriendlyByteBuf buffer) {
        if (AntimatterPlatformUtils.isForge()){
            return Ingredient.fromNetwork(buffer);
        }
        return fromValues(buffer.readList(FriendlyByteBuf::readItem).stream().map(RecipeValue::new));
    }

    /*public RecipeIngredient(JsonElement element) {
        this.value = Suppliers.memoize(() -> {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("ingredient")) {
                    return Ingredient.fromJson(obj.get("ingredient"));
                }
            }
            return Ingredient.fromJson(element);
        });
        if (element.isJsonObject()) {
            JsonObject obj = (JsonObject) element;
            this.count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            this.nonConsume = obj.has("consume") && !obj.get("consume").getAsBoolean();
            this.ignoreNbt = obj.has("nbt") && !obj.get("nbt").getAsBoolean();
        } else {
            this.count = 1;
        }
    }*/

    /*public RecipeIngredient(FriendlyByteBuf buffer) {
        Ingredient i = Ingredient.fromNetwork(buffer);
        this.value = Suppliers.memoize(() -> i);
        this.count = buffer.readInt();
        this.nonConsume = buffer.readBoolean();
        this.ignoreNbt = buffer.readBoolean();
    }*/

    public RecipeIngredient setNoConsume() {
        nonConsume = true;
        return this;
    }

    public boolean ignoreConsume() {
        return nonConsume;
    }

    public RecipeIngredient setIgnoreNbt() {
        ignoreNbt = true;
        return this;
    }

    public boolean ignoreNbt() {
        return ignoreNbt;
    }

    public static int count(Ingredient ing) {
        if (ing instanceof RecipeIngredient i) {
            return i.count();
        }
        if (ing.getItems().length > 0) {
            return ing.getItems()[0].getCount();
        }
        return 1;
    }

    public static boolean ignoreNbt(Ingredient ing) {
        if (ing instanceof RecipeIngredient i) {
            return i.ignoreNbt;
        }
        return false;
    }

    public static boolean ignoreConsume(Ingredient ing) {
        if (ing instanceof RecipeIngredient i) {
            return i.nonConsume;
        }
        return false;
    }

    public static RecipeIngredient of(Ingredient ingredient, int count){
        return new RecipeIngredient(new MultiValue(Arrays.stream(ingredient.getItems()).map(t -> new RecipeValue(t, count))));
    }


    public static RecipeIngredient of(int count, ItemStack... provider) {
        if (provider.length == 1) {
            return new RecipeIngredient(new RecipeValue(provider[0], count));
        } else {
            return new RecipeIngredient(new MultiValue(Arrays.stream(provider).map(t -> new RecipeValue(t, count))));
        }
    }

    public static RecipeIngredient of(ItemStack... provider) {
        if (provider.length == 1) {
            return new RecipeIngredient(new RecipeValue(provider[0]));
        } else {
            return new RecipeIngredient(new MultiValue(Arrays.stream(provider).map(RecipeValue::new)));
        }
    }


    public static RecipeIngredient of(ItemStack stack) {
        return new RecipeIngredient(new RecipeValue(stack));
    }

    public static RecipeIngredient of(ItemLike provider, int count) {
        return of(count, new ItemStack(provider));
    }

    public static RecipeIngredient of(ResourceLocation tagIn, int count) {
        ensureRegisteredTag(tagIn);
        return new RecipeIngredient(new RecipeValue(new TagKey<>(Registry.ITEM_REGISTRY, tagIn), count));
    }

    public static RecipeIngredient of(TagKey<Item> tagIn, int count) {
        ensureRegisteredTag(tagIn.location());
        return new RecipeIngredient(new RecipeValue(tagIn, count));
    }

    public static RecipeIngredient ofObject(Object object, int amount){
        if (object instanceof TagKey tag){
            return of(tag, amount);
        }
        if (object instanceof ItemLike item){
            return of(item, amount);
        }
        if (object instanceof ResourceLocation location){
            return of(location, amount);
        }
        return RecipeIngredient.of(RecipeIngredient.EMPTY, 1);
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        JsonElement element = super.toJson();
        if (element instanceof JsonObject o){
            object = o;
        } else if (element instanceof JsonArray){
            object.add("values", element);
        }
        object.addProperty("nbt", ignoreNbt);
        object.addProperty("noconsume", nonConsume);
        return object;
    }

    private static void ensureRegisteredTag(ResourceLocation loc) {
        TagUtils.getItemTag(loc);
    }

    public static class MultiValue implements Ingredient.Value {

        private final Value[] values;

        MultiValue(Value... vals) {
            this.values = vals;
        }

        MultiValue(Stream<? extends Value> vals) {
            this.values = vals.toArray(Value[]::new);
        }

        @Override
        public Collection<ItemStack> getItems() {
            return Arrays.stream(values).flatMap(t -> t.getItems().stream()).collect(Collectors.toList());
        }

        public Value[] getValues() {
            return values;
        }

        @Override
        public JsonObject serialize() {
            JsonArray arr = new JsonArray(values.length);
            for (Value value : values) {
                arr.add(value.serialize());
            }
            JsonObject obj = new JsonObject();
            obj.add("values", arr);
            return obj;
        }
    }

    public static class RecipeValue implements Ingredient.Value {
        private TagKey<Item> tag;
        private ItemStack stack;
        private final int count;

        public RecipeValue(TagKey<Item> tag, int count) {
            this.tag = tag;
            this.count = count;
        }

        public RecipeValue(ItemLike tag, int count) {
            this.stack = new ItemStack(tag);
            this.count = count;
        }

        public RecipeValue(ItemStack stack) {
            this.stack = stack;
            this.count = stack.getCount();
        }

        public RecipeValue(ItemStack stack, int count) {
            this.stack = stack;
            stack.setCount(count);
            this.count = count;
        }

        @Override
        public Collection<ItemStack> getItems() {
            if (tag != null) {
                return TagUtils.nc(tag).getValues().stream().map(t -> new ItemStack(t, count)).toList();
            }
            return Collections.singletonList(stack);
        }

        public TagKey<Item> getTag() {
            return tag;
        }

        public int getCount() {
            return count;
        }

        @Override
        public JsonObject serialize() {
            if (this.tag != null) {
                JsonObject obj = new JsonObject();
                obj.addProperty("tag", this.tag.location().toString());
                obj.addProperty("count", this.count);
                return obj;
            }
            if (this.stack != null) {
               return toJson(this.stack);
            }
            return new JsonObject();
        }
    }

    private static JsonObject toJson(ItemStack stack)
    {
        JsonObject ret = new JsonObject();
        ret.addProperty("item", AntimatterPlatformUtils.getIdFromItem(stack.getItem()).toString());
        if (stack.getCount() != 1)
            ret.addProperty("count", stack.getCount());
        if (stack.getTag() != null)
            ret.addProperty("nbt", stack.getTag().toString());
        return ret;
    }
}
