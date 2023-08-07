package muramasa.antimatter.recipe.serializer;

import com.google.gson.JsonObject;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.map.RecipeBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface CustomRecipeSerializer {

    Recipe fromJson(ResourceLocation recipeId, JsonObject json);

    Recipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer);

    void toNetwork(FriendlyByteBuf buffer, Recipe recipe);

    void toJson(ResourceLocation recipeId, JsonObject json, RecipeBuilder recipeBuilder);
}
