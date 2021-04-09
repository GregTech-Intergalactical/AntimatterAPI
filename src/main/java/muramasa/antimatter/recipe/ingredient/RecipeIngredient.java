package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.function.Supplier;

/**
 * Small wrapper, to avoid typing lazyvalue.
 */
public class RecipeIngredient {
    private final LazyValue<Ingredient> value;
    public final int count;
    protected boolean nonConsume = false;
    protected boolean ignoreNbt = false;
    private boolean setStacks = false;

    public RecipeIngredient(LazyValue<Ingredient> source, int count) {
        this.value = source;
        this.count = count;
    }

    private void setStacksCounts(Ingredient i) {
        for (ItemStack matchingStack : i.getMatchingStacks()) {
            matchingStack.setCount(count);
        }
    }

    public RecipeIngredient(Supplier<Ingredient> source, int count) {
        this.value = new LazyValue<>(source);
        this.count = count;
    }

    public RecipeIngredient(Ingredient source, int count) {
        this.value = new LazyValue<>(() -> source);
        this.count = count;
    }

    public RecipeIngredient(JsonElement element) {
        this.value = new LazyValue<>(() -> {
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("ingredient")) {
                    return Ingredient.deserialize(obj.get("ingredient"));
                }
            }
            return Ingredient.deserialize(element);
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

    public RecipeIngredient(PacketBuffer buffer) {
        Ingredient i = Ingredient.read(buffer);
        this.value = new LazyValue<>(() -> i);
        this.count = buffer.readInt();
        this.nonConsume = buffer.readBoolean();
        this.ignoreNbt = buffer.readBoolean();
    }

    public void writeToBuffer(PacketBuffer buffer) {
        CraftingHelper.write(buffer, this.value.getValue());
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

    public Ingredient get() {
        Ingredient v = value.getValue();
        if (!setStacks) {
            setStacksCounts(v);
            setStacks = true;
        }
        return v;
    }

    public static RecipeIngredient of(int count, ItemStack... provider) {
        return new RecipeIngredient(Ingredient.fromStacks(provider), count);
    }

    public static RecipeIngredient of(ItemStack stack) {
        return new RecipeIngredient(Ingredient.fromStacks(stack), stack.getCount());
    }

    public static RecipeIngredient of(Ingredient custom, int count) {
        return new RecipeIngredient(custom, count);
    }

    public static RecipeIngredient of(IItemProvider provider, int count) {
        return of(count, new ItemStack(provider));
    }

    public static RecipeIngredient of(LazyValue<ItemStack> provider, int count) {
        return new RecipeIngredient(() -> Ingredient.fromStacks(provider.getValue()), count);
    }

    public static RecipeIngredient of(ResourceLocation tagIn, int count) {
        ensureRegisteredTag(tagIn);
        return new RecipeIngredient(() -> {
            ITag<Item> tag = collectTag(tagIn);
            return Ingredient.fromTag(tag);
        }, count);
    }
    public static RecipeIngredient of(ITag.INamedTag<Item> tagIn,int count) {
        ensureRegisteredTag(tagIn.getName());
        return new RecipeIngredient(() -> {
            ITag<Item> tag = collectTag(tagIn.getName());
            return Ingredient.fromTag(tag);
        }, count);
    }
    private static ITag<Item> collectTag(ResourceLocation loc) {
        return TagCollectionManager.getManager().getItemTags().get(loc);
    }

    private static void ensureRegisteredTag(ResourceLocation loc) {
        TagUtils.getItemTag(loc);
    }
}
