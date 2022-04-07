package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonObject;
import muramasa.antimatter.Ref;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;


public class IngredientSerializer extends ForgeRegistryEntry<IngredientSerializer> implements IIngredientSerializer<RecipeIngredient> {

    public static final IngredientSerializer INSTANCE = new IngredientSerializer();

    static {
        INSTANCE.setRegistryName(new ResourceLocation(Ref.ID, "ingredient"));
    }
    @Override
    public RecipeIngredient parse(FriendlyByteBuf buffer) {
        int len = buffer.readVarInt();
        ItemStack[] items = new ItemStack[len];

        for (int i = 0; i < len; i++) {
            items[i] = buffer.readItem();
        }
        RecipeIngredient r = RecipeIngredient.of(items);
        r.nonConsume = buffer.readBoolean();
        r.ignoreNbt = buffer.readBoolean();
        return r;
     }

    @Override
    public RecipeIngredient parse(JsonObject json) {
        RecipeIngredient r = new RecipeIngredient(Ingredient.valueFromJson(json));
        if (json.get("nbt").getAsBoolean()) r.setIgnoreNbt();
        if (json.get("noconsume").getAsBoolean()) r.setNoConsume();
        return r;
    }

    @Override
    public void write(FriendlyByteBuf buffer, RecipeIngredient ingredient) {
        ItemStack[] items = ingredient.getItems();
        buffer.writeVarInt(items.length);

        for (ItemStack stack : items)
            buffer.writeItem(stack);
        buffer.writeBoolean(ingredient.ignoreConsume());
        buffer.writeBoolean(ingredient.ignoreNbt());
    }
}
