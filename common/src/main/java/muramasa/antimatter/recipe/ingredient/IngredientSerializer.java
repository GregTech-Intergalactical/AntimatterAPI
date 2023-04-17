package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;


public class IngredientSerializer implements IAntimatterIngredientSerializer<RecipeIngredient> {

    public static final IngredientSerializer INSTANCE = new IngredientSerializer();

    public static void init() {
        AntimatterAPI.register(IAntimatterIngredientSerializer.class, "ingredient", Ref.ID, INSTANCE);
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
        Ingredient.Value[] values;
        if (json.has("values")){
            JsonArray array = json.getAsJsonArray("values");
            values = new Ingredient.Value[array.size()];
            for (int i = 0; i < array.size(); i++){
                values[i] = Ingredient.valueFromJson(array.get(i).getAsJsonObject());
            }
        } else {
            values = new Ingredient.Value[]{Ingredient.valueFromJson(json)};
        }
        RecipeIngredient r = new RecipeIngredient(values);
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
