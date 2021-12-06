package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Small wrapper, to avoid typing lazyvalue.
 */
public class RecipeIngredient {
    private final LazyLoadedValue<Ingredient> value;
    public final int count;
    protected boolean nonConsume = false;
    protected boolean ignoreNbt = false;
    private boolean setStacks = false;

    public RecipeIngredient(LazyLoadedValue<Ingredient> source, int count) {
        this.value = source;
        this.count = count;
    }

    private void setStacksCounts(Ingredient i) {
        for (ItemStack matchingStack : i.getItems()) {
            matchingStack.setCount(count);
        }
    }

    public RecipeIngredient(Supplier<Ingredient> source, int count) {
        this.value = new LazyLoadedValue<>(source);
        this.count = count;
    }

    public RecipeIngredient(Ingredient source, int count) {
        this.value = new LazyLoadedValue<>(() -> source);
        this.count = count;
    }

    public RecipeIngredient(JsonElement element) {
        this.value = new LazyLoadedValue<>(() -> {
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
    }

    public RecipeIngredient(FriendlyByteBuf buffer) {
        Ingredient i = Ingredient.fromNetwork(buffer);
        this.value = new LazyLoadedValue<>(() -> i);
        this.count = buffer.readInt();
        this.nonConsume = buffer.readBoolean();
        this.ignoreNbt = buffer.readBoolean();
    }

    public void writeToBuffer(FriendlyByteBuf buffer) {
        CraftingHelper.write(buffer, this.value.get());
        buffer.writeInt(count);
        buffer.writeBoolean(nonConsume);
        buffer.writeBoolean(ignoreNbt);
    }

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

    public Ingredient get() {
        Ingredient v = value.get();
        if (!setStacks) {
            setStacksCounts(v);
            setStacks = true;
            for (ItemStack stack : v.getItems()) {
                if (stack.isEmpty()) throw new RuntimeException("Empty item matched in RecipeIngredient");
            }
        }
        return v;
    }

    public static RecipeIngredient of(int count, ItemStack... provider) {
        return new RecipeIngredient(Ingredient.of(provider), count);
    }

    public static RecipeIngredient of(ItemStack stack) {
        return new RecipeIngredient(Ingredient.of(stack), stack.getCount());
    }

    public static RecipeIngredient of(Ingredient custom, int count) {
        return new RecipeIngredient(custom, count);
    }

    public static RecipeIngredient of(ItemLike provider, int count) {
        return of(count, new ItemStack(provider));
    }

    public static RecipeIngredient of(LazyLoadedValue<ItemStack> provider, int count) {
        return new RecipeIngredient(() -> Ingredient.of(provider.get()), count);
    }

    public static RecipeIngredient of(ResourceLocation tagIn, int count) {
        ensureRegisteredTag(tagIn);
        return new RecipeIngredient(() -> {
            Tag<Item> tag = collectTag(tagIn);
            return tag != null ? Ingredient.of(tag) : Ingredient.fromValues(Stream.empty());
        }, count);
    }

    public static RecipeIngredient of(Tag.Named<Item> tagIn, int count) {
        ensureRegisteredTag(tagIn.getName());
        return new RecipeIngredient(() -> {
            Tag<Item> tag = collectTag(tagIn.getName());
            return tag != null ? Ingredient.of(tag) : Ingredient.fromValues(Stream.empty());
        }, count);
    }

    private static Tag<Item> collectTag(ResourceLocation loc) {
        TagContainer getter = TagUtils.getSupplier();
        if (getter == null) {
            return TagUtils.nc(loc);
        }
        return getter.getOrEmpty(Registry.ITEM_REGISTRY).getTag(loc);
    }

    private static void ensureRegisteredTag(ResourceLocation loc) {
        TagUtils.getItemTag(loc);
    }
}
