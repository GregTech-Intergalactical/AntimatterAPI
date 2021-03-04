package muramasa.antimatter.recipe.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class StackListIngredient extends AntimatterIngredient{

    protected StackListIngredient(Stream<? extends IItemList> itemLists, int count) {
        super(itemLists, count);
    }

    @Override
    public boolean testTag(ResourceLocation tag) {
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        if (p_test_1_ == null) {
            return false;
        } else if (this.getMatchingStacks().length == 0) {
            return p_test_1_.isEmpty();
        } else {
            for(ItemStack itemstack : this.getMatchingStacks()) {
                if (compareItems(p_test_1_, itemstack)) {
                    return true;
                }
            }
            return false;
        }
    }
}
