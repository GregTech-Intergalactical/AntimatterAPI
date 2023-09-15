package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = PropertyIngredient.class, remap = false)
public class PropertyIngredientMixin {
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return AntimatterAPI.get(IIngredientSerializer.class, "material", Ref.ID);
    }
}
