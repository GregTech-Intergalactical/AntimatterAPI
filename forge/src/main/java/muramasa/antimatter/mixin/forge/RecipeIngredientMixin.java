package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.registration.forge.AntimatterRegistration;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RecipeIngredient.class, remap = false)
public class RecipeIngredientMixin {
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return AntimatterRegistration.RECIPE_SERIALIZER;
    }
}
