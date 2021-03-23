package muramasa.antimatter.recipe.ingredient;

import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
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
    public boolean testTag(ResourceLocation tag) {
        return stack.getItem().getTags().contains(tag);
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        if (p_test_1_ == null) return false;
        return AntimatterIngredient.compareItems(stack,p_test_1_);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public int hashCode() {
        return itemHash(stack);
    }
}
