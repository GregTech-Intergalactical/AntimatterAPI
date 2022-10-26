package muramasa.antimatter.recipe.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public interface IAntimatterIngredientSerializer<T extends Ingredient> {
    T parse(FriendlyByteBuf buffer);

    T parse(JsonObject json);

    void write(FriendlyByteBuf buffer, T ingredient);
}
