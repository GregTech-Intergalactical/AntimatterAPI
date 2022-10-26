package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Small wrapper, to avoid typing lazyvalue.
 */
public class RecipeIngredient extends Ingredient {
    protected boolean nonConsume = false;
    protected boolean ignoreNbt = false;

    public RecipeIngredient(Ingredient.Value... value) {
        super(Stream.of(value));
    }

    public int count() {
        if (getItems().length > 0) {
            return getItems()[0].getCount();
        }
        return 0;
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
        return new RecipeIngredient(new MultiValue(Arrays.stream(ingredient.getItems()).map(t -> new Value(t, count))));
    }


    public static RecipeIngredient of(int count, ItemStack... provider) {
        if (provider.length == 1) {
            return new RecipeIngredient(new Value(provider[0], count));
        } else {
            return new RecipeIngredient(new MultiValue(Arrays.stream(provider).map(t -> new Value(t, count))));
        }
    }

    public static RecipeIngredient of(ItemStack... provider) {
        if (provider.length == 1) {
            return new RecipeIngredient(new Value(provider[0]));
        } else {
            return new RecipeIngredient(new MultiValue(Arrays.stream(provider).map(Value::new)));
        }
    }


    public static RecipeIngredient of(ItemStack stack) {
        return new RecipeIngredient(new Value(stack));
    }

    public static RecipeIngredient of(ItemLike provider, int count) {
        return of(count, new ItemStack(provider));
    }

    public static RecipeIngredient of(ResourceLocation tagIn, int count) {
        ensureRegisteredTag(tagIn);
        return new RecipeIngredient(new Value(new TagKey<>(Registry.ITEM_REGISTRY, tagIn), count));
    }

    public static RecipeIngredient of(TagKey<Item> tagIn, int count) {
        ensureRegisteredTag(tagIn.location());
        return new RecipeIngredient(new Value(tagIn, count));
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

    private static class MultiValue implements Ingredient.Value {

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

        @Override
        public JsonObject serialize() {
            JsonArray arr = new JsonArray(values.length);
            for (Value value : values) {
                arr.add(value.serialize());
            }
            JsonObject obj = new JsonObject();
            obj.add("values", obj);
            return obj;
        }
    }

    private static class Value implements Ingredient.Value {
        private TagKey<Item> tag;
        private ItemStack stack;
        private final int count;

        Value(TagKey<Item> tag, int count) {
            this.tag = tag;
            this.count = count;
        }

        Value(ItemLike tag, int count) {
            this.stack = new ItemStack(tag);
            this.count = count;
        }

        Value(ItemStack stack) {
            this.stack = stack;
            this.count = stack.getCount();
        }

        Value(ItemStack stack, int count) {
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
