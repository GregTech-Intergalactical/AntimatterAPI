package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.recipe.ingredient.IngredientSerializer;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;

import java.util.stream.Stream;

@Mixin(RecipeIngredient.class)
public class RecipeIngredientMixin extends Ingredient {
    public RecipeIngredientMixin(Stream<? extends Value> stream) {
        super(stream);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        IngredientSerializer.INSTANCE.write(buffer, (RecipeIngredient) (Object)this);
    }
}
