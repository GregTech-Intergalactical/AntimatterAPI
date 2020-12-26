package muramasa.antimatter.recipe.ingredient;

import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.stream.Stream;

public class StackIngredient extends AntimatterIngredient {
    protected ItemStack stack;

    protected StackIngredient(Stream<? extends IItemList> itemLists, int count) {
        super(itemLists, count);
        if (this.getMatchingStacks().length > 1) {
            Utils.onInvalidData("ERROR, INVALID INPUT TO STACK");
            this.stack = ItemStack.EMPTY;
        }
        this.stack = this.getMatchingStacks()[0];
    }

    @Override
    public int hashCode() {
        return itemHash(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (stack == null) return super.equals(o);
        if (o instanceof StackIngredient) {
            return ItemStack.areItemStacksEqual(stack,((StackIngredient)o).stack);
        }
        if (o instanceof TagIngredient) {
            return stack.getItem().getTags().contains(((TagIngredient)o).tag.getId());
        }
        if (o instanceof Ingredient) {
            return ((Ingredient)o).test(stack);
        }
        return false;
    }
}
