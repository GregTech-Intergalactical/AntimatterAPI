package muramasa.antimatter.recipe.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TagIngredient extends AntimatterIngredient{
    protected ResourceLocation tag;

    protected TagIngredient(Stream<? extends IItemList> itemLists, int count, ResourceLocation loc) {
        super(itemLists,count);
        this.tag = loc;
    }

    public ResourceLocation getTag() {
        return tag;
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        return (p_test_1_ != null && p_test_1_.getItem().getTags().contains(tag) && p_test_1_.getCount() <= count) || super.test(p_test_1_);
    }

    @Override
    public boolean equals(Object o) {
        boolean ok = super.equals(o);
        if (ok) return true;
        if (tag == null) {
            return false;
        }
        if (o instanceof TagIngredient) {
            return ((TagIngredient)o).tag.equals(this.tag);
        }
        if (o instanceof Ingredient) {
            for (ItemStack stack : ((Ingredient)o).getMatchingStacks()) {
                if (stack.getItem().getTags().contains(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return tag == null ? 0 : tag.hashCode();
    }

    @Override
    public boolean testTag(ResourceLocation tag) {
        return tag.equals(this.tag);
    }
}
