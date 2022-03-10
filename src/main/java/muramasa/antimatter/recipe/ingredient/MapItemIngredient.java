package muramasa.antimatter.recipe.ingredient;

import net.minecraft.world.item.Item;

public class MapItemIngredient extends AbstractMapIngredient {

    public Item stack;

    public MapItemIngredient(Item stack, boolean insideMap) {
        super(insideMap);
        this.stack = stack;
    }

    @Override
    protected int hash() {
        return stack.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof MapTagIngredient ing) {
           return stack.builtInRegistryHolder().is(ing.loc);
        }
        if (o instanceof MapItemIngredient) {
            return ((MapItemIngredient) o).stack.equals(stack);
        }
        return false;
    }

    @Override
    public String toString() {
        return stack.toString();
    }
}
