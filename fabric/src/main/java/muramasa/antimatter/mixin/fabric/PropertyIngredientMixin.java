package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;

import java.util.stream.Stream;

@Mixin(value = PropertyIngredient.class, remap = false)
public class PropertyIngredientMixin extends Ingredient {
    public PropertyIngredientMixin(Stream<? extends Value> stream) {
        super(stream);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
        PropertyIngredient.Serializer.INSTANCE.write(buffer, (PropertyIngredient)(Object)this);
    }
}
