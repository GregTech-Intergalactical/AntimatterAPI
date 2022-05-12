package muramasa.antimatter.recipe.ingredient;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nonnull;

public class SpecialIngredientWrapper extends AbstractMapIngredient {

    private final Ingredient source;

    public SpecialIngredientWrapper(Ingredient source) {
        super(false);
        this.source = source;
    }

    public boolean isItemValid(@Nonnull ItemStack stack) {
        return source.test(stack);
    }

    //Illegal am I right? But it is in essence a map ingredient.
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapItemStackIngredient)) {
            return false;
        }
        return isItemValid(((MapItemStackIngredient) obj).stack);
    }

    @Override
    protected int hash() {
        return 0;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
